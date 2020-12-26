package com.quakbo.kexif

import com.quakbo.kexif.number.Rational
import com.quakbo.kexif.number.toRational
import org.apache.commons.imaging.ImageReadException
import org.apache.commons.imaging.Imaging
import org.apache.commons.imaging.common.RationalNumber
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter
import org.apache.commons.imaging.formats.tiff.TiffField
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoAscii
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet
import java.io.BufferedOutputStream
import java.io.Closeable
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

internal class JpegMetadataReadWriter(private val filepath: String) : Metadata, Closeable {
    private val updates = mutableMapOf<KexifTag, Any>()

    private val rawMetadata = try {
        Imaging.getMetadata(File(filepath))
    } catch (e: IOException) {
        throw KexifReadException(e.message, e)
    } catch (e: ImageReadException) {
        throw KexifReadException(e.message, e)
    }

    private val metadata = rawMetadata as? JpegImageMetadata

    fun get(key: KexifTag): Any? {
        return metadata?.exif?.getFieldValue(key.commonsTag) ?: updates[key]
    }

    override fun getByte(tag: ByteTag): Byte? {
        return when (val v = get(tag)) {
            null -> null
            is Byte -> v
            else -> throw UnsupportedOperationException("Cannot convert value [$v] to Byte.")
        }
    }

    override fun getByteArray(tag: ByteArrayTag): ByteArray? {
        return when (val v = get(tag)) {
            null -> null
            is ByteArray -> v
            else -> throw UnsupportedOperationException("Cannot convert value [$v] to ByteArray.")
        }
    }

    override fun getDouble(tag: DoubleTag): Double? {
        return when (val v = get(tag)) {
            null -> null
            is Double -> v
            else -> throw UnsupportedOperationException("Cannot convert value [$v] to Double.")
        }
    }

    override fun getDoubleArray(tag: DoubleArrayTag): DoubleArray? {
        return when (val v = get(tag)) {
            null -> null
            is DoubleArray -> v
            else -> throw UnsupportedOperationException("Cannot convert value [$v] to DoubleArray.")
        }
    }

    override fun getFloat(tag: FloatTag): Float? {
        return when (val v = get(tag)) {
            null -> null
            is Float -> v
            else -> throw UnsupportedOperationException("Cannot convert value [$v] to Float.")
        }
    }

    override fun getFloatArray(tag: FloatArrayTag): FloatArray? {
        return when (val v = get(tag)) {
            null -> null
            is FloatArray -> v
            else -> throw UnsupportedOperationException("Cannot convert value [$v] to FloatArray.")
        }
    }

    override fun getLocalDateTime(tag: TimestampTag): LocalDateTime? {
        return when (val v = get(tag)) {
            null -> null
            is String -> LocalDateTime.parse(v, EXIF_DATE_TIME_FORMAT)
            else -> throw UnsupportedOperationException("Cannot convert value [$v] to LocalDateTime.")
        }
    }

    override fun getLong(tag: LongTag): Long? {
        return when (val v = get(tag)) {
            null -> null
            is Int -> v.toLong()
            is Long -> v
            else -> throw UnsupportedOperationException("Cannot convert value [$v] to Long.")
        }
    }

    override fun getLongArray(tag: LongArrayTag): LongArray? {
        return when (val v = get(tag)) {
            null -> null
            is LongArray -> v
            else -> throw UnsupportedOperationException("Cannot convert value [$v] to LongArray.")
        }
    }

    override fun getShort(tag: ShortTag): Short? {
        return when (val v = get(tag)) {
            null -> null
            is Short -> v
            else -> throw UnsupportedOperationException("Cannot convert value [$v] to Short.")
        }
    }

    override fun getShortArray(tag: ShortArrayTag): ShortArray? {
        return when (val v = get(tag)) {
            null -> null
            is ShortArray -> v
            else -> throw UnsupportedOperationException("Cannot convert value [$v] to ShortArray.")
        }
    }

    override fun getString(tag: StringTag): String? {
        return when (val v = get(tag)) {
            null -> null
            is String -> v
            is RationalNumber -> v.toRational().toString()
            else -> v.toString()
        }
    }

    override fun getStringArray(tag: StringArrayTag): Array<String>? {
        return when (val v = get(tag)) {
            null -> null
            is Array<*> -> v.map { it.toString() }.toTypedArray()
            else -> throw UnsupportedOperationException("Cannot convert value [$v] to Array<String>.")
        }
    }

    override fun getRational(tag: RationalTag): Rational? {
        return when (val v = get(tag)) {
            null -> null
            is RationalNumber -> v.toRational()
            is String -> v.toRational()
            else -> throw UnsupportedOperationException("Cannot convert value [$v] to Rational.")
        }
    }

    override fun getRationalArray(tag: RationalArrayTag): Array<Rational>? {
        return when (val v = get(tag)) {
            null -> null
            is Array<*> -> v.map { it as RationalNumber }.map { it.toRational() }.toTypedArray()
            else -> throw UnsupportedOperationException("Cannot convert value [$v] to Array<String>.")
        }
    }

    override fun set(key: KexifTag, value: Any) {
        updates[key] = value
    }

    override fun asMap(): Map<KexifTag, Any?> {
        return metadata?.exif?.allFields
            ?.map(TiffField::getTagInfo)
            ?.map(KexifTag::create)
            ?.map { tag ->
                when (val v = get(tag)) {
                    is RationalNumber -> tag to Rational(v)
                    else -> tag to v
                }
            }?.toMap()
            ?: emptyMap()
    }

    override fun close() {
        val outputSet = metadata?.exif?.outputSet ?: TiffOutputSet()

        outputSet.orCreateExifDirectory.apply {
            updates.entries.forEach { (key, value) ->
                removeField(key.commonsTag)
                when (val tag = key.commonsTag) {
                    is TagInfoAscii -> add(tag, value as String)
                    else -> throw UnsupportedOperationException("Unsupported tag type: $tag")
                }
            }
        }

        val sourceFile = File(filepath)
        val destinationFile = File("$filepath.working")
        FileOutputStream(destinationFile).use { fos ->
            BufferedOutputStream(fos).use { os ->
                ExifRewriter().updateExifMetadataLossless(sourceFile, os, outputSet)
            }
        }.also { destinationFile.renameTo(sourceFile) }
    }
}

val EXIF_DATE_TIME_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")

class KexifReadException(message: String?, cause: Throwable?) : RuntimeException(message, cause)

