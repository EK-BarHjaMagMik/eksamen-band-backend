package org.example.eksamenbandbackend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class ImageProcessingServiceTest {

    private ImageProcessingService service;

    @BeforeEach
    void setUp() {
        service = new ImageProcessingService();
    }

    @Test
    void shouldGenerateOptimizedWebpBoundedTo1600(@TempDir Path tempDir) throws IOException {
        Path source = createTestImage(tempDir);

        Path optimized = service.generateOptimized(source);

        assertThat(optimized).exists();
        assertThat(optimized.getFileName().toString()).isEqualTo("abc123-opt.webp");
        BufferedImage img = ImageIO.read(optimized.toFile());
        assertThat(img).isNotNull();
        assertThat(img.getWidth()).isLessThanOrEqualTo(1600);
        assertThat(img.getHeight()).isLessThanOrEqualTo(1600);
        // 3000x2000 source → optimized should be 1600x1067 (width-bound, aspect kept)
        assertThat(img.getWidth()).isEqualTo(1600);
    }

    @Test
    void shouldGenerateThumbnailWebp300pxWide(@TempDir Path tempDir) throws IOException {
        Path source = createTestImage(tempDir);

        Path thumbnail = service.generateThumbnail(source);

        assertThat(thumbnail).exists();
        assertThat(thumbnail.getFileName().toString()).isEqualTo("abc123-thumb.webp");
        BufferedImage img = ImageIO.read(thumbnail.toFile());
        assertThat(img).isNotNull();
        assertThat(img.getWidth()).isEqualTo(300);
        // 3000x2000 → thumb 300x200, aspect preserved
        assertThat(img.getHeight()).isEqualTo(200);
    }

    @Test
    void thumbnailShouldBeSmallerBytesThanOriginal(@TempDir Path tempDir) throws IOException {
        Path source = createTestImage(tempDir);
        long originalSize = source.toFile().length();

        Path thumbnail = service.generateThumbnail(source);

        assertThat(thumbnail.toFile().length()).isLessThan(originalSize);
    }

    /**
     * Writes a 3000×2000 JPEG named "abc123.jpg" into the given directory.
     * Uses a gradient (not a solid color) so WebP compression has something
     * realistic to chew on. Returns the path so tests can pass it to the
     * service under test.
     */
    private static Path createTestImage(Path dir) throws IOException {
        int width = 3000;
        int height = 2000;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        for (int y = 0; y < height; y++) {
            g.setColor(new Color(y % 256, (y * 2) % 256, (y * 3) % 256));
            g.fillRect(0, y, width, 1);
        }
        g.dispose();
        Path path = dir.resolve("abc123.jpg");
        ImageIO.write(img, "jpg", path.toFile());
        return path;
    }
}
