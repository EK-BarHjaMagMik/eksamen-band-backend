package org.example.eksamenbandbackend.service;

import org.example.eksamenbandbackend.dto.BandMemberResponse;
import org.example.eksamenbandbackend.dto.UpdateBandMemberRequest;
import org.example.eksamenbandbackend.entity.BandMember;
import org.example.eksamenbandbackend.repository.BandMemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class BandMemberService {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp");
    private static final long MAX_UPLOAD_SIZE_BYTES = 30L * 1024 * 1024;
    // Photos for band-member profiles live under their own subdirectory so
    // they're clearly separate from the general photos library on disk.
    private static final String MEMBER_PHOTO_SUBDIR = "members";

    @Value("${app.upload-dir}")
    private String uploadDir;

    private final BandMemberRepository bandMemberRepository;

    public BandMemberService(BandMemberRepository bandMemberRepository) {
        this.bandMemberRepository = bandMemberRepository;
    }

    public List<BandMemberResponse> getBandMembers() {
        return bandMemberRepository.findAllByOrderByDisplayOrderAsc()
                .stream()
                .map(BandMemberResponse::fromEntity)
                .toList();
    }

    public BandMemberResponse getBandMemberById(Long bandMemberId) {
        return bandMemberRepository.findById(bandMemberId)
                .map(BandMemberResponse::fromEntity)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Band member not found with id: " + bandMemberId));
    }

    public BandMemberResponse editBandMemberById(Long id, UpdateBandMemberRequest request) {
        return bandMemberRepository.findById(id)
                .map(bandMember -> {
                    bandMember.setName(request.name());
                    bandMember.setRole(request.role());
                    bandMember.setBio(request.bio());
                    // photoUrl is server-controlled — set only via uploadBandMemberPhoto().
                    return BandMemberResponse.fromEntity(bandMemberRepository.save(bandMember));
                })
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Band member not found with id: " + id));
    }

    public BandMemberResponse uploadBandMemberPhoto(Long id, MultipartFile file) {
        BandMember bandMember = bandMemberRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Band member not found with id: " + id));

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty");
        }
        if (file.getSize() > MAX_UPLOAD_SIZE_BYTES) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "File is too large: " + file.getSize() + " bytes");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Unsupported file type: " + contentType);
        }

        Path savedPath;
        try {
            savedPath = saveFile(file);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to save file", e);
        }

        String oldUrl = bandMember.getPhotoUrl();
        String newUrl = "/uploads/" + MEMBER_PHOTO_SUBDIR + "/" + savedPath.getFileName();

        bandMember.setPhotoUrl(newUrl);
        BandMember saved;
        try {
            saved = bandMemberRepository.save(bandMember);
        } catch (RuntimeException e) {
            deleteFileFromDisk(newUrl);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to update band member", e);
        }

        deleteFileFromDisk(oldUrl);

        return BandMemberResponse.fromEntity(saved);
    }

    private Path saveFile(MultipartFile file) throws IOException {
        // Defense in depth: uploadBandMemberPhoto already rejects anything
        // outside ALLOWED_TYPES, but re-check here so this method is safe
        // to call from any future entry point.
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new IOException("Unsupported content type: " + contentType);
        }

        Path dir = Paths.get(uploadDir.trim()).resolve(MEMBER_PHOTO_SUBDIR);
        Files.createDirectories(dir);

        // Derive the extension from the validated content type — never from
        // the user's originalFilename. The default branch is unreachable
        // given the check above; it's only here to satisfy the compiler.
        String fileExt = switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> ".bin";
        };

        String filename = UUID.randomUUID() + fileExt;
        Path targetPath = dir.resolve(filename);

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, targetPath);
        }

        return targetPath;
    }

    private void deleteFileFromDisk(String url) {
        if (url == null || url.isEmpty()) {
            return;
        }
        // url is /uploads/members/<filename>. Resolve under the configured
        // upload dir + members subdir so old (pre-subdir) URLs at the root
        // of /uploads/ would NOT be touched here — those belong to the
        // sample-member seed init and shouldn't be deleted on a new upload.
        String filename = Paths.get(url).getFileName().toString();
        Path filePath = Paths.get(uploadDir.trim()).resolve(MEMBER_PHOTO_SUBDIR).resolve(filename);
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.err.println("Failed to delete file: " + filePath + " - " + e.getMessage());
        }
    }
}
