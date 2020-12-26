package com.quakbo.kexif

import org.apache.commons.imaging.formats.tiff.constants.DcfTagConstants
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants
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
                .flatMap { it.sealedSubclasses }
                .mapNotNull { it.objectInstance }
        }
    }

    override fun toString(): String {
        return commonsTag.name
    }
}

sealed class StringTag : KexifTag()

sealed class ByteArrayTag : StringTag()
sealed class ByteTag : StringTag()
sealed class DoubleArrayTag : StringTag()
sealed class DoubleTag : StringTag()
sealed class FloatArrayTag : StringTag()
sealed class FloatTag : StringTag()
sealed class LongArrayTag : StringTag()
sealed class LongTag : StringTag()
sealed class RationalArrayTag : StringTag()
sealed class RationalTag : StringTag()
sealed class ShortArrayTag : StringTag()
sealed class ShortTag : StringTag()
sealed class StringArrayTag : StringTag()
sealed class TimestampTag : StringTag()


object ColorSpace : ShortTag() {
    override val commonsTag: TagInfo = DcfTagConstants.EXIF_TAG_COLOR_SPACE
}

object ComponentsConfiguration : ByteArrayTag() {
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_COMPONENTS_CONFIGURATION
}

object DateTimeOriginal : TimestampTag() {
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL
}

object ExifOffset : LongTag() {
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_EXIF_OFFSET
}

object ExifVersion : ByteArrayTag() {
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_EXIF_VERSION
}

object FlashpixVersion : ByteArrayTag() {
    override val commonsTag: TagInfo = ExifTagConstants.EXIF_TAG_FLASHPIX_VERSION
}

object ResolutionUnit : ShortTag() {
    override val commonsTag: TagInfo = TiffTagConstants.TIFF_TAG_RESOLUTION_UNIT
}

object XResolution : RationalTag() {
    override val commonsTag: TagInfo = TiffTagConstants.TIFF_TAG_XRESOLUTION
}

object YCbCrPositioning : ShortTag() {
    override val commonsTag: TagInfo = TiffTagConstants.TIFF_TAG_YCBCR_POSITIONING
}

object YResolution : RationalTag() {
    override val commonsTag: TagInfo = TiffTagConstants.TIFF_TAG_YRESOLUTION
}