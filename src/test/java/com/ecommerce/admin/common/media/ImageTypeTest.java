package com.ecommerce.admin.common.media;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ImageTypeTest {

    static Stream<Arguments> supportedImages() {
        return Stream.of(
                Arguments.of(Named.of("JPEG", bytes(0xFF, 0xD8, 0xFF, 0xE0, 0x00, 0x10)), ImageType.JPEG),
                Arguments.of(Named.of("PNG", bytes(0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 0x00)), ImageType.PNG),
                Arguments.of(Named.of("WebP", riff("WEBP")), ImageType.WEBP));
    }

    static Stream<Named<byte[]>> unsupportedFiles() {
        return Stream.of(
                Named.of("empty file", new byte[0]),
                Named.of("GIF", ascii("GIF89a")),
                Named.of("PDF", ascii("%PDF-1.7")),
                Named.of("SVG", ascii("<svg xmlns=\"http://www.w3.org/2000/svg\">")),
                Named.of("HTML", ascii("<html><script>alert(1)</script>")),
                Named.of("WAV (RIFF but not WebP)", riff("WAVE")),
                Named.of("truncated PNG header", bytes(0x89, 0x50, 0x4E)));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("supportedImages")
    void detectsTheTypeFromTheFirstBytes(byte[] content, ImageType expected) {
        assertThat(ImageType.detect(content)).contains(expected);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("unsupportedFiles")
    void rejectsEverythingElse(byte[] content) {
        assertThat(ImageType.detect(content)).isEmpty();
    }

    private static byte[] bytes(int... values) {
        var result = new byte[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = (byte) values[i];
        }
        return result;
    }

    private static byte[] ascii(String text) {
        return text.getBytes(StandardCharsets.US_ASCII);
    }

    private static byte[] riff(String format) {
        var header = new byte[12];
        System.arraycopy(ascii("RIFF"), 0, header, 0, 4);
        System.arraycopy(ascii(format), 0, header, 8, 4);
        return header;
    }
}
