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
        val value = metadata?.exif?.getFieldValue(key.commonsTag) ?: updates[key] ?: return null

        return try {
            when (key) {
                is ByteArrayTag -> value as ByteArray
                is ByteTag -> value as Byte
                is DoubleArrayTag -> value as DoubleArray
                is DoubleTag -> value as Double
                is FloatArrayTag -> value as FloatArray
                is FloatTag -> value as Float
                is GPSTextTag -> value as String
                is LongArrayTag -> value as LongArray
                is LongTag -> (value as Number).toLong()
                is RationalArrayTag -> when (value) {
                    is RationalNumber -> value.toRational()
                    is Array<*> -> value.map { (it as RationalNumber).toRational() }
                    else -> throw IllegalStateException("Cannot convert unknown type to RationalArray value=$value")
                }
                is RationalTag -> (value as RationalNumber).toRational()
                is ShortArrayTag -> value as ShortArray
                is ShortTag -> (value as Number).toShort()
                is StringTag -> value as String
                is StringArrayTag -> value as Array<String>
                is TimestampTag -> LocalDateTime.parse(value as String, EXIF_DATE_TIME_FORMAT)
                is UnknownTag -> value
            }
        } catch (e: ClassCastException) {
            throw KexifReadException(
                "Could not cast value=$value for key=$key (${key::class.supertypes}) to any known type.  ${value::class.javaObjectType}",
                e
            )
        }
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

    override fun getGPSText(tag: GPSTextTag): String? {
        return when (val v = get(tag)) {
            null -> null
            is CharSequence -> v.filterNot { it == '\u0000' }.toString()
            is String -> v
            else -> v.toString()
        }
    }

    override fun getLocalDateTime(tag: TimestampTag): LocalDateTime? {
        return when (val v = get(tag)) {
            null -> null
            is LocalDateTime -> v
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

    override fun getString(tag: KexifTag): String? {
        return when (val v = get(tag)) {
            null -> null
            is LocalDateTime -> v.format(EXIF_DATE_TIME_FORMAT)
            is RationalNumber -> v.toRational().toString()
            is String -> v
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
            is Rational -> v
            is RationalNumber -> v.toRational()
            is String -> v.toRational()
            else -> throw UnsupportedOperationException("Cannot convert value [$v] to Rational.")
        }
    }

    override fun getRationalArray(tag: RationalArrayTag): Array<Rational>? {
        return when (val v = get(tag)) {
            null -> null
            is Rational -> arrayOf(v)
            is Array<*> -> v.map { it as RationalNumber }.map { it.toRational() }.toTypedArray()
            else -> throw UnsupportedOperationException("Cannot convert value [$v] to Array<String>.")
        }
    }

    override fun getUnknown(tag: UnknownTag): Any? {
        return get(tag)
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

