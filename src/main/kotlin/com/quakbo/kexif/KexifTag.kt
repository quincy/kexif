package com.quakbo.kexif

import org.apache.commons.imaging.formats.tiff.constants.DcfTagConstants
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants
import org.apache.commons.imaging.formats.tiff.constants.MicrosoftTagConstants
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo

sealed class KexifTag {
    internal abstract val commonsTag: TagInfo

    companion object {
        internal fun create(tagInfo: TagInfo): KexifTag {
            return allTags().firstOrNull { it.commonsTag == tagInfo }
                ?: throw IllegalArgumentException("Unsupported TagInfo=$tagInfo")
        }

        fun allTags(): Collection<KexifTag> {
            return KexifTag::class.sealedSubclasses
                .flatMap { it.sealedSubclasses }
                .mapNotNull { it.objectInstance }
        }
    }

    override fun toString(): String {
        return commonsTag.name
    }
}

sealed class ByteArrayTag : KexifTag()
sealed class ByteTag : KexifTag()
sealed class DoubleArrayTag : KexifTag()
sealed class DoubleTag : KexifTag()
sealed class FloatArrayTag : KexifTag()
sealed class FloatTag : KexifTag()
sealed class GPSTextTag : KexifTag()
sealed class LongArrayTag : KexifTag()
sealed class LongTag : KexifTag()
sealed class RationalArrayTag : KexifTag()
sealed class RationalTag : KexifTag()
sealed class ShortArrayTag : KexifTag()
sealed class ShortTag : KexifTag()
sealed class StringTag : KexifTag()
sealed class StringArrayTag : KexifTag()
sealed class TimestampTag : KexifTag()
sealed class UnknownTag : KexifTag()


object ApertureValue : RationalTag() {
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_APERTURE_VALUE
}

object CameraOwnerName : StringTag() {
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_CAMERA_OWNER_NAME
}

object ColorSpace : ShortTag() {
    override val commonsTag: TagInfo = DcfTagConstants.EXIF_TAG_COLOR_SPACE
}

object ComponentsConfiguration : ByteArrayTag() {
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_COMPONENTS_CONFIGURATION
}

object CompressedBitsPerPixel : RationalTag() {
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_COMPRESSED_BITS_PER_PIXEL
}

object Compression : ShortTag() {
    override val commonsTag: TagInfo = TiffTagConstants.TIFF_TAG_COMPRESSION
}

object CustomRendered : ShortTag() {
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_CUSTOM_RENDERED
}

object DateTime : TimestampTag() {
    override val commonsTag: TagInfo = TiffTagConstants.TIFF_TAG_DATE_TIME
}

object DateTimeDigitized : TimestampTag() {
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_DATE_TIME_DIGITIZED
}

object DateTimeOriginal : TimestampTag() {
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL
}

object DigitalZoomRatio : RationalTag() {
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_DIGITAL_ZOOM_RATIO
}

object ExifImageLength : ShortTag() {
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_EXIF_IMAGE_LENGTH
}

object ExifImageWidth : ShortTag() {
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_EXIF_IMAGE_WIDTH
}

object ExifOffset : LongTag() {
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_EXIF_OFFSET
}

object ExifVersion : ByteArrayTag() {
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_EXIF_VERSION
}

object ExposureCompensation : RationalArrayTag() {
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_EXPOSURE_COMPENSATION
}

object ExposureMode : ShortTag() {
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_EXPOSURE_MODE
}

object ExposureTime : RationalArrayTag() {
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_EXPOSURE_TIME
}

object FileSource : ByteTag() { // FIXME UndefinedTag, not ByteTag
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_FILE_SOURCE
}

object FocalPlaneResolutionUnit : ShortTag() {
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_FOCAL_PLANE_RESOLUTION_UNIT_EXIF_IFD
}

object FocalPlaneXResolution : RationalTag() {
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_FOCAL_PLANE_XRESOLUTION_EXIF_IFD
}

object FocalPlaneYResolution : RationalTag() {
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_FOCAL_PLANE_YRESOLUTION_EXIF_IFD
}

object FNumber : RationalArrayTag() {
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_FNUMBER
}

object Flash : ShortTag() {
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_FLASH
}

object FlashpixVersion : ByteArrayTag() {
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_FLASHPIX_VERSION
}

object FocalLength : RationalArrayTag() {
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_FOCAL_LENGTH
}

object GPSInfo : LongTag() { // FIXME TagInfoDirectory, not Long
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_GPSINFO
}

object ImageDescription : StringTag() {
    override val commonsTag: TagInfo = TiffTagConstants.TIFF_TAG_IMAGE_DESCRIPTION
}

object InteroperabilityIndex : StringTag() {
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_INTEROPERABILITY_INDEX
}

object InteroperabilityVersion : ByteArrayTag() { // FIXME InfoUndefinedTag, not ByteArray
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_INTEROPERABILITY_VERSION
}

object InteropOffset : LongTag() { // FIXME InfoDirectory, not Long
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_INTEROP_OFFSET
}

object ISO : ShortTag() {
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_ISO
}

object JpgFromRawLengthIFD2 : LongTag() {
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_JPG_FROM_RAW_LENGTH_IFD2
}

object JpgFromRawLengthSubIFD : LongTag() {
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_JPG_FROM_RAW_LENGTH_SUB_IFD
}

object JpgFromRawStartIFD2 : LongTag() {
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_JPG_FROM_RAW_START_IFD2
}

object JpgFromRawStartSubIFD : LongTag() {
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_JPG_FROM_RAW_START_SUB_IFD
}

object MaxApertureValue : RationalTag() {
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_MAX_APERTURE_VALUE
}

object Make : StringTag() {
    override val commonsTag: TagInfo = TiffTagConstants.TIFF_TAG_MAKE
}

object MakerNote : ByteArrayTag() { // FIXME UndefinedArrayTag, not ByteArray
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_MAKER_NOTE
}

object MeteringMode : ShortTag() {
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_METERING_MODE
}

object Model : StringTag() {
    override val commonsTag: TagInfo = TiffTagConstants.TIFF_TAG_MODEL
}

object Orientation : ShortTag() {
    override val commonsTag: TagInfo = TiffTagConstants.TIFF_TAG_ORIENTATION
}

object PhotographicSensitivity : ShortTag() {
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_ISO
}

object Rating : ShortTag() {
    override val commonsTag: TagInfo = MicrosoftTagConstants.EXIF_TAG_RATING
}

object RatingPercent : ShortTag() {
    override val commonsTag: TagInfo = MicrosoftTagConstants.EXIF_TAG_RATING_PERCENT
}

object RelatedImageLength : LongTag() {
    override val commonsTag: TagInfo = DcfTagConstants.EXIF_TAG_RELATED_IMAGE_LENGTH
}

object RelatedImageWidth : LongTag() {
    override val commonsTag: TagInfo = DcfTagConstants.EXIF_TAG_RELATED_IMAGE_WIDTH
}

object ResolutionUnit : ShortTag() {
    override val commonsTag: TagInfo = TiffTagConstants.TIFF_TAG_RESOLUTION_UNIT
}

object SceneCaptureType : ShortTag() {
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_SCENE_CAPTURE_TYPE
}

object SensingMethod : ShortTag() {
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_SENSING_METHOD_EXIF_IFD
}

object ShutterSpeedValue : RationalTag() {
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_SHUTTER_SPEED_VALUE
}

object SubSecTime : StringTag() {
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_SUB_SEC_TIME
}

object SubSecTimeDigitized : StringTag() {
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_SUB_SEC_TIME_DIGITIZED
}

object SubSecTimeOriginal : StringTag() {
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_SUB_SEC_TIME_ORIGINAL
}

object XResolution : RationalTag() {
    override val commonsTag: TagInfo = TiffTagConstants.TIFF_TAG_XRESOLUTION
}

object Unknown : UnknownTag() {
    override val commonsTag: TagInfo = TiffTagConstants.TIFF_TAG_UNKNOWN
}

object UserComment : GPSTextTag() {
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_USER_COMMENT
}

// TODO There is also WHITE_BALANCE_2 which has the same name "WhiteBalance", two ways to get to the same value?
object WhiteBalance : ShortTag() {
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_WHITE_BALANCE_1
}

object YCbCrPositioning : ShortTag() {
    override val commonsTag: TagInfo = TiffTagConstants.TIFF_TAG_YCBCR_POSITIONING
}

object YResolution : RationalTag() {
    override val commonsTag: TagInfo = TiffTagConstants.TIFF_TAG_YRESOLUTION
}