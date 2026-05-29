package org.example.eksamenbandbackend.service;

import java.util.HashMap;
import java.util.Map;
import org.example.eksamenbandbackend.dto.PhotoResponse;
import org.example.eksamenbandbackend.entity.Photo;
import org.example.eksamenbandbackend.entity.Show;
import org.example.eksamenbandbackend.repository.PhotoRepository;
import org.example.eksamenbandbackend.repository.ShowRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PhotoServiceTest {

    @Mock
    private PhotoRepository photoRepository;

    @Mock
    private ShowRepository showRepository;

    private PhotoService photoService;

    @BeforeEach
    void setUp() {
        photoService = new PhotoService(photoRepository, showRepository);
    }

    @Test
    void batchUpdatePhotos_updatesPhotosAndReturnsResponses() {
        Photo p1 = new Photo();
        p1.setId(1L);
        p1.setUrl("/uploads/1.jpg");
        p1.setCaption("old");

        Photo p2 = new Photo();
        p2.setId(2L);
        p2.setUrl("/uploads/2.jpg");
        p2.setCaption("old2");

        when(photoRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(p1, p2));

        Map<String, Object> req = new HashMap<>();
        req.put("photoIds", List.of(1, 2));
        req.put("caption", "New caption");

        List<PhotoResponse> resp = photoService.batchUpdatePhotos(req);

        assertEquals(2, resp.size());
        assertEquals("New caption", resp.get(0).caption());
        verify(photoRepository).saveAll(any());
    }

    @Test
    void batchUpdatePhotos_throwsWhenPhotoIdMissing() {
        when(photoRepository.findAllById(List.of(99L))).thenReturn(List.of());
        Map<String, Object> req = new HashMap<>();
        req.put("photoIds", List.of(99));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> photoService.batchUpdatePhotos(req));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void batchUpdatePhotos_throwsWhenShowNotFound() {
        Photo p1 = new Photo();
        p1.setId(1L);

        when(photoRepository.findAllById(List.of(1L))).thenReturn(List.of(p1));
        when(showRepository.findById(5L)).thenReturn(Optional.empty());

        Map<String, Object> req = new HashMap<>();
        req.put("photoIds", List.of(1));
        req.put("showId", 5L);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> photoService.batchUpdatePhotos(req));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void getPhotos_returnsOrderedAndMapped() {
        Photo p1 = new Photo();
        p1.setId(1L);
        p1.setUrl("/p1.jpg");
        p1.setCaption("cap1");
        p1.setDateTaken(LocalDate.of(2026, 1, 2));
        p1.setPhotographer("ph1");

        Photo p2 = new Photo();
        p2.setId(2L);
        p2.setUrl("/p2.jpg");
        p2.setCaption("cap2");
        p2.setDateTaken(LocalDate.of(2026, 1, 1));
        p2.setPhotographer("ph2");

        when(photoRepository.findAllByOrderByDateTakenDescCreatedAtDesc()).thenReturn(List.of(p1, p2));

        List<PhotoResponse> res = photoService.getPhotos();

        assertEquals(2, res.size());
        assertEquals(p1.getId(), res.get(0).id());
        assertEquals(p1.getUrl(), res.get(0).url());
        assertEquals(p2.getId(), res.get(1).id());
    }

    @Test
    void getRecentPhotos_appliesLimitAndMaps() {
        int limit = 1;

        Photo p = new Photo();
        p.setId(10L);
        p.setUrl("/recent.jpg");
        p.setCaption("recent");
        p.setDateTaken(LocalDate.of(2026, 2, 1));
        p.setPhotographer("phx");

        when(photoRepository.findAllByOrderByDateTakenDescCreatedAtDesc(any(Pageable.class))).thenReturn(List.of(p));

        List<PhotoResponse> res = photoService.getRecentPhotos(limit);

        assertEquals(1, res.size());
        assertEquals(p.getId(), res.get(0).id());

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(photoRepository).findAllByOrderByDateTakenDescCreatedAtDesc(captor.capture());
        Pageable used = captor.getValue();
        assertEquals(0, used.getPageNumber());
        assertEquals(limit, used.getPageSize());
    }

    @Test
    void uploadPhotos_savesFileAndCreatesDbEntry(@TempDir Path tempDir) throws Exception {
        // arrange
        // set upload dir
        var f = PhotoService.class.getDeclaredField("uploadDir");
        f.setAccessible(true);
        f.set(photoService, tempDir.toString());

        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(5L);
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getOriginalFilename()).thenReturn("photo.jpg");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[] { 1, 2, 3 }));

        // act
        var resp = photoService.uploadPhotos(List.of(file), "caption", "photog", LocalDate.of(2026, 5, 1), null);

        // assert
        assertEquals(1, resp.uploaded().size());
        assertTrue(resp.errors().isEmpty());

        ArgumentCaptor<org.example.eksamenbandbackend.entity.Photo> captor = ArgumentCaptor
                .forClass(org.example.eksamenbandbackend.entity.Photo.class);
        verify(photoRepository).save(captor.capture());
        var saved = captor.getValue();
        assertEquals("caption", saved.getCaption());
        assertEquals("photog", saved.getPhotographer());
        assertEquals(LocalDate.of(2026, 5, 1), saved.getDateTaken());

        // file exists in upload dir
        var files = java.util.Arrays.stream(tempDir.toFile().listFiles()).map(java.io.File::getName).toList();
        assertFalse(files.isEmpty());
        assertTrue(files.get(0).endsWith(".jpg"));
    }

    @Test
    void uploadPhotos_withUnknownShowIdThrowsBadRequest() {
        when(showRepository.findById(99L)).thenReturn(java.util.Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> photoService.uploadPhotos(List.of(), "caption", "photog", LocalDate.of(2026, 5, 1), 99L));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Show with ID 99 not found"));
        verify(showRepository).findById(99L);
        verifyNoInteractions(photoRepository);
    }

    @Test
    void uploadPhotos_associatesPersistedPhotoWithResolvedShow(@TempDir Path tempDir) throws Exception {
        var f = PhotoService.class.getDeclaredField("uploadDir");
        f.setAccessible(true);
        f.set(photoService, tempDir.toString());

        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(5L);
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getOriginalFilename()).thenReturn("photo.jpg");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[] { 1, 2, 3 }));

        Show show = new Show();
        show.setId(42L);
        when(showRepository.findById(42L)).thenReturn(Optional.of(show));

        var resp = photoService.uploadPhotos(List.of(file), "caption", "photog", LocalDate.of(2026, 5, 1), 42L);

        assertEquals(1, resp.uploaded().size());
        assertTrue(resp.errors().isEmpty());

        ArgumentCaptor<Photo> captor = ArgumentCaptor.forClass(Photo.class);
        verify(photoRepository).save(captor.capture());
        assertSame(show, captor.getValue().getShow());
        verify(showRepository).findById(42L);
    }

    @Test
    void uploadPhotos_reportsEmptyFile() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);
        when(file.getOriginalFilename()).thenReturn("empty.jpg");

        var resp = photoService.uploadPhotos(List.of(file), null, null, null, null);

        assertTrue(resp.uploaded().isEmpty());
        assertEquals(1, resp.errors().size());
        assertEquals("empty.jpg", resp.errors().get(0).filename());
    }

    @Test
    void uploadPhotos_reportsUnsupportedType() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(10L);
        when(file.getContentType()).thenReturn("application/pdf");
        when(file.getOriginalFilename()).thenReturn("doc.pdf");

        var resp = photoService.uploadPhotos(List.of(file), null, null, null, null);

        assertTrue(resp.uploaded().isEmpty());
        assertEquals(1, resp.errors().size());
        assertTrue(resp.errors().get(0).reason().contains("Unsupported file type"));
    }

    @Test
    void uploadPhotos_reportsTooLargeFile() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(30L * 1024 * 1024 + 1);
        when(file.getOriginalFilename()).thenReturn("big.jpg");

        var resp = photoService.uploadPhotos(List.of(file), null, null, null, null);

        assertTrue(resp.uploaded().isEmpty());
        assertEquals(1, resp.errors().size());
        assertTrue(resp.errors().get(0).reason().contains("too large")
                || resp.errors().get(0).reason().contains("too large"));
    }

    @Test
    void uploadPhotos_handlesIOExceptionWhenSaving(@TempDir Path tempDir) throws Exception {
        var f = PhotoService.class.getDeclaredField("uploadDir");
        f.setAccessible(true);
        f.set(photoService, tempDir.toString());

        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(5L);
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getOriginalFilename()).thenReturn("io.jpg");
        when(file.getInputStream()).thenThrow(new IOException("disk error"));

        var resp = photoService.uploadPhotos(List.of(file), null, null, null, null);

        assertTrue(resp.uploaded().isEmpty());
        assertEquals(1, resp.errors().size());
        assertTrue(resp.errors().get(0).reason().contains("Failed to save file"));
    }
}
