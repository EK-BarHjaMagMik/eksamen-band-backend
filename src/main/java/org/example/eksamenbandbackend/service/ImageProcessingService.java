package org.example.eksamenbandbackend.service;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;

@Service
public class ImageProcessingService {

    private static final int OPTIMIZED_MAX_DIMENSION = 1600;
    private static final float OPTIMIZED_QUALITY = 0.82f;
    // Sized to display crisply at the largest grid-column width (~400 CSS px)
    // on a 2x-DPR retina screen. 300px was visibly upscaled (~2.6x); 600px
    // keeps the bytes manageable (~50-70 KB) while looking sharp.
    private static final int THUMBNAIL_WIDTH = 600;
    private static final float THUMBNAIL_QUALITY = 0.75f;
    private static final String WEBP_EXT = ".webp";

    public Path generateOptimized(Path source) throws IOException {
        Path target = source.resolveSibling(baseName(source) + "-opt" + WEBP_EXT);
        Thumbnails.of(source.toFile())
                .size(OPTIMIZED_MAX_DIMENSION, OPTIMIZED_MAX_DIMENSION)
                .outputQuality(OPTIMIZED_QUALITY)
                .outputFormat("webp")
                .toFile(target.toFile());
        return target;
    }

    public Path generateThumbnail(Path source) throws IOException {
        Path target = source.resolveSibling(baseName(source) + "-thumb" + WEBP_EXT);
        Thumbnails.of(source.toFile())
                .width(THUMBNAIL_WIDTH)
                .outputQuality(THUMBNAIL_QUALITY)
                .outputFormat("webp")
                .toFile(target.toFile());
        return target;
    }

    private static String baseName(Path path) {
        String filename = path.getFileName().toString();
        int dot = filename.lastIndexOf('.');
        return dot > 0 ? filename.substring(0, dot) : filename;
    }
}
