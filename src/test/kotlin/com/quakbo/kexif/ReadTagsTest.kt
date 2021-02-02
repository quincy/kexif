package com.quakbo.kexif

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.present
import com.quakbo.kexif.matchers.contentsEqual
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
                is GPSTextTag -> metadata.getGPSText(tag)
                is LongArrayTag -> metadata.getLongArray(tag)
                is LongTag -> metadata.getLong(tag)
                is RationalArrayTag -> metadata.getRationalArray(tag)
                is RationalTag -> metadata.getRational(tag)
                is ShortArrayTag -> metadata.getShortArray(tag)
                is ShortTag -> metadata.getShort(tag)
                is StringArrayTag -> metadata.getStringArray(tag)
                is StringTag -> metadata.getString(tag)
                is TimestampTag -> metadata.getLocalDateTime(tag)
                is UnknownTag -> metadata.getUnknown(tag)
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
        assertThat(Kexif.openJPEG(filepath).getString(DateTimeOriginal), equalTo("2020:11:26 12:13:14"))
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
        val metadata = Kexif.openJPEG(filepath)
        assertThat(metadata.getShort(ColorSpace), equalTo((-1).toShort()))
        assertThat(metadata.getByteArray(ComponentsConfiguration), contentsEqual(byteArrayOf(1, 2, 3, 0)))
        assertThat(metadata.getLocalDateTime(DateTimeOriginal), equalTo(LocalDateTime.parse("2020:11:26 12:13:14", EXIF_DATE_TIME_FORMAT)))
        assertThat(metadata.getLong(ExifOffset), equalTo(90L))
        assertThat(metadata.getByteArray(ExifVersion), contentsEqual(byteArrayOf(48, 50, 51, 50)))
        assertThat(metadata.getByteArray(FlashpixVersion), contentsEqual(byteArrayOf(48, 49, 48, 48)))
        assertThat(metadata.getShort(ResolutionUnit), equalTo(2.toShort()))
        assertThat(metadata.getRational(XResolution), equalTo("96/1".toRational()))
        assertThat(metadata.getShort(YCbCrPositioning), equalTo(1.toShort()))
        assertThat(metadata.getRational(YResolution), equalTo("96/1".toRational()))
    }

    @Test
    internal fun `read tags`() {
        val filepath = this::class.java.getResource("/images/0002.jpg").file
        val metadata = Kexif.openJPEG(filepath)
        assertThat(metadata.getByteArray(ComponentsConfiguration), contentsEqual(byteArrayOf(1, 2, 3, 0)))
        assertThat(metadata.getRational(ApertureValue), equalTo("107/32".toRational()))
        assertThat(metadata.getString(CameraOwnerName), equalTo(""))
        assertThat(metadata.getShort(ColorSpace), equalTo(1.toShort()))
        assertThat(metadata.getByteArray(ComponentsConfiguration), contentsEqual(byteArrayOf(1, 2, 3, 0)))
        assertThat(metadata.getRational(CompressedBitsPerPixel), equalTo("3/1".toRational()))
        assertThat(metadata.getShort(Compression), equalTo(6.toShort()))
        assertThat(metadata.getShort(CustomRendered), equalTo(0.toShort()))
        assertThat(metadata.getLocalDateTime(DateTime), equalTo(LocalDateTime.parse("2021:01:01 00:29:26", EXIF_DATE_TIME_FORMAT)))
        assertThat(metadata.getLocalDateTime(DateTimeDigitized), equalTo(LocalDateTime.parse("2021:01:01 00:29:26", EXIF_DATE_TIME_FORMAT)))
        assertThat(metadata.getLocalDateTime(DateTimeOriginal), equalTo(LocalDateTime.parse("2021:01:01 00:29:26", EXIF_DATE_TIME_FORMAT)))
        assertThat(metadata.getRational(DigitalZoomRatio), equalTo("1/1".toRational()))
        assertThat(metadata.getShort(ExifImageLength), equalTo(3864.toShort()))
        assertThat(metadata.getShort(ExifImageWidth), equalTo(5152.toShort()))
        assertThat(metadata.getLong(ExifOffset), equalTo(270L))
        assertThat(metadata.getByteArray(ExifVersion), contentsEqual(byteArrayOf(48, 50, 51, 48)))
        assertThat(metadata.getRationalArray(ExposureCompensation), contentsEqual(arrayOf("0/1".toRational())))
        assertThat(metadata.getShort(ExposureMode), equalTo(0.toShort()))
        assertThat(metadata.getRationalArray(ExposureTime), contentsEqual(arrayOf("1/50".toRational())))
        assertThat(metadata.getRationalArray(FNumber), contentsEqual(arrayOf("16/5".toRational())))
        assertThat(metadata.getByte(FileSource), equalTo(3.toByte()))
        assertThat(metadata.getShort(Flash), equalTo(25.toShort()))
        assertThat(metadata.getByteArray(FlashpixVersion), contentsEqual(byteArrayOf(48, 49, 48, 48)))
        assertThat(metadata.getRationalArray(FocalLength), contentsEqual(arrayOf("5/1".toRational())))
        assertThat(metadata.getShort(FocalPlaneResolutionUnit), equalTo(2.toShort()))
        assertThat(metadata.getRational(FocalPlaneXResolution), equalTo("5152000/243".toRational()))
        assertThat(metadata.getRational(FocalPlaneYResolution), equalTo("1288000/61".toRational()))
        assertThat(metadata.getLong(GPSInfo), equalTo(6930L))
        assertThat(metadata.getString(ImageDescription), equalTo("                               "))
        assertThat(metadata.getLong(InteropOffset), equalTo(6876L))
        assertThat(metadata.getString(InteroperabilityIndex), equalTo("R98"))
        assertThat(metadata.getByteArray(InteroperabilityVersion), contentsEqual(byteArrayOf(48, 49, 48, 48)))
        assertThat(metadata.getLong(JpgFromRawLengthSubIFD), equalTo(6541L))
        assertThat(metadata.getLong(JpgFromRawStartSubIFD), equalTo(7042L))
        assertThat(metadata.getString(Make), equalTo("Canon"))
        assertThat(metadata.getByteArray(MakerNote), present()) // hasSize(equalTo(5764))
        assertThat(metadata.getRational(MaxApertureValue), equalTo("107/32".toRational()))
        assertThat(metadata.getShort(MeteringMode), equalTo(5.toShort()))
        assertThat(metadata.getString(Model), equalTo("Canon PowerShot ELPH 180"))
        assertThat(metadata.getShort(Orientation), equalTo(1.toShort()))
        assertThat(metadata.getShort(PhotographicSensitivity), equalTo(640.toShort()))
        assertThat(metadata.getShort(Rating), equalTo(1.toShort()))
        assertThat(metadata.getShort(RatingPercent), equalTo(1.toShort()))
        assertThat(metadata.getLong(RelatedImageLength), equalTo(3864L))
        assertThat(metadata.getLong(RelatedImageWidth), equalTo(5152L))
        assertThat(metadata.getShort(ResolutionUnit), equalTo(2.toShort()))
        assertThat(metadata.getShort(SceneCaptureType), equalTo(0.toShort()))
        assertThat(metadata.getShort(SensingMethod), equalTo(2.toShort()))
        assertThat(metadata.getRational(ShutterSpeedValue), equalTo("181/32".toRational()))
        assertThat(metadata.getString(SubSecTime), equalTo("83"))
        assertThat(metadata.getString(SubSecTimeDigitized), equalTo("83"))
        assertThat(metadata.getString(SubSecTimeOriginal), equalTo("83"))
        assertThat(metadata.getUnknown(Unknown), absent())
        assertThat(metadata.getGPSText(UserComment), equalTo(""))
        assertThat(metadata.getShort(WhiteBalance), equalTo(0.toShort()))
        assertThat(metadata.getRational(XResolution), equalTo("180/1".toRational()))
        assertThat(metadata.getShort(YCbCrPositioning), equalTo(2.toShort()))
        assertThat(metadata.getRational(YResolution), equalTo("180/1".toRational()))
    }

    fun allTagsSource(): Stream<Arguments> {
        return KexifTag.allTags().stream().map { Arguments.of(it) }
    }
}