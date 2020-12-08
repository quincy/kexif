package com.quakbo.kexif

import com.quakbo.kexif.number.Rational
import java.io.Closeable
import java.time.LocalDateTime

object Kexif {
    fun openJPEG(path: String): Metadata {
        return JpegMetadata(JpegMetadataReadWriter(path))
    }
}

interface Metadata : MetadataReader, MetadataWriter

interface MetadataReader : Closeable {
    fun asMap(): Map<KexifTag, Any?>
    fun getByte(tag: ByteTag): Byte?
    fun getByteArray(tag: ByteArrayTag): ByteArray?
    fun getDouble(tag: DoubleTag): Double?
    fun getDoubleArray(tag: DoubleArrayTag): DoubleArray?
    fun getFloat(tag: FloatTag): Float?
    fun getFloatArray(tag: FloatArrayTag): FloatArray?
    fun getLocalDateTime(tag: TimestampTag): LocalDateTime?
    fun getLong(tag: LongTag): Long?
    fun getLongArray(tag: LongArrayTag): LongArray?
    fun getRational(tag: RationalTag): Rational?
    fun getRationalArray(tag: RationalArrayTag): Array<Rational>?
    fun getShort(tag: ShortTag): Short?
    fun getShortArray(tag: ShortArrayTag): ShortArray?
    fun getString(tag: StringTag): String?
    fun getStringArray(tag: StringArrayTag): Array<String>?
    // fun getUndefined
    // fun getUndefineds
    // fun getGPS Text
    // fun getUnknown
    // fun getUnknowns
    // fun getInfo Directory
    // fun getInfo XPS String
}

interface MetadataWriter : Closeable {
    fun set(key: KexifTag, value: Any)
}

class JpegMetadata internal constructor(private val readWriter: JpegMetadataReadWriter) : Metadata by readWriter
