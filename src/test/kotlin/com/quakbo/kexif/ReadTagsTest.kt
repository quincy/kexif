package com.quakbo.kexif

import com.natpryce.hamkrest.allOf
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.quakbo.kexif.matchers.hasElement
import com.quakbo.kexif.matchers.isEmptyMap
import com.quakbo.kexif.number.toRational
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

/** See `resources/images/contents.md` for a description of the various images available for testing. */
class ReadTagsTest {
    @Test
    internal fun `can read DateTimeOriginal as String`() {
        val filepath = this::class.java.getResource("/images/0001.jpg").file
        val value = Kexif.openJPEG(filepath).getString(DateTimeOriginal)
        assertThat(value, equalTo("2020:11:26 12:13:14"))
    }

    @Test
    internal fun `can read DateTimeOriginal as LocalDateTime`() {
        val filepath = this::class.java.getResource("/images/0001.jpg").file
        val value = Kexif.openJPEG(filepath).getLocalDateTime(DateTimeOriginal)
        assertThat(value, equalTo(LocalDateTime.of(2020, 11, 26, 12, 13, 14)))
    }

    @Test
    internal fun `rational value can be read as a string`() {
        val filepath = this::class.java.getResource("/images/0001.jpg").file
        val value = Kexif.openJPEG(filepath).getString(XResolution)
        assertThat(value, equalTo("96/1"))
    }

    @Test
    internal fun `rational value can be read as a Rational`() {
        val filepath = this::class.java.getResource("/images/0001.jpg").file
        val value = Kexif.openJPEG(filepath).getRational(XResolution)
        assertThat(value, equalTo("96/1".toRational()))
    }

    @Test
    internal fun `asMap for image with no tags returns an empty map`() {
        val filepath = this::class.java.getResource("/images/0000.jpg").file
        assertThat(Kexif.openJPEG(filepath).asMap(), isEmptyMap())
    }

    @Test
    internal fun `asMap for image with tags returns expected map`() {
        val filepath = this::class.java.getResource("/images/0001.jpg").file
        assertThat(
            Kexif.openJPEG(filepath).asMap(),
            allOf(
                hasElement(ColorSpace, (-1).toShort()),
                hasElement(ComponentsConfiguration, byteArrayOf(1, 2, 3, 0)),
                hasElement(DateTimeOriginal, "2020:11:26 12:13:14"),
                hasElement(ExifOffset, 90),
                hasElement(ExifVersion, byteArrayOf(48, 50, 51, 50)),
                hasElement(FlashpixVersion, byteArrayOf(48, 49, 48, 48)),
                hasElement(ResolutionUnit, 2.toShort()),
                hasElement(XResolution, "96/1".toRational()),
                hasElement(YCbCrPositioning, 1.toShort()),
                hasElement(YResolution, "96/1".toRational()),
            )
        )
    }
}