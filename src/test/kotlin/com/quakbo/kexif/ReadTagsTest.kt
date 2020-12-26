package com.quakbo.kexif

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.allOf
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.present
import com.quakbo.kexif.matchers.hasElement
import com.quakbo.kexif.matchers.isEmptyMap
import com.quakbo.kexif.number.toRational
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDateTime
import java.util.stream.Stream

/** See `resources/images/contents.md` for a description of the various images available for testing. */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReadTagsTest {
    @Test
    internal fun `image with no tags can be read`() {
        val filepath = this::class.java.getResource("/images/0000.jpg").file
        val tags = Kexif.openJPEG(filepath).asMap()
        assertThat(tags, isEmptyMap())
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("allTagsSource")
    internal fun `image with no tags returns null value for any tag`(tag: KexifTag) {
        val filepath = this::class.java.getResource("/images/0000.jpg").file
        val value = Kexif.openJPEG(filepath).use { metadata ->
            when (tag) {
                is ByteArrayTag -> metadata.getByteArray(tag)
                is ByteTag -> metadata.getByte(tag)
                is DoubleArrayTag -> metadata.getDoubleArray(tag)
                is DoubleTag -> metadata.getDouble(tag)
                is FloatArrayTag -> metadata.getFloatArray(tag)
                is FloatTag -> metadata.getFloat(tag)
                is LongArrayTag -> metadata.getLongArray(tag)
                is LongTag -> metadata.getLong(tag)
                is RationalArrayTag -> metadata.getRationalArray(tag)
                is RationalTag -> metadata.getRational(tag)
                is ShortArrayTag -> metadata.getShortArray(tag)
                is ShortTag -> metadata.getShort(tag)
                is StringArrayTag -> metadata.getStringArray(tag)
                is StringTag -> metadata.getString(tag)
            }
        }

        when (tag) {
            // ExifOffset is always present as it's calculated by apache commons imaging
            ExifOffset -> assertThat(value, present())
            else -> assertThat(value, absent())
        }
    }

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

    fun allTagsSource(): Stream<Arguments> {
        return KexifTag.allTags().stream().map { Arguments.of(it) }
    }
}