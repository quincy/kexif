package com.quakbo.kexif

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.attribute.PosixFilePermission.OWNER_EXECUTE
import java.nio.file.attribute.PosixFilePermission.OWNER_READ
import java.nio.file.attribute.PosixFilePermission.OWNER_WRITE
import java.nio.file.attribute.PosixFilePermissions
import kotlin.io.path.Path

/** See `resources/images/contents.md` for a description of the various images available for testing. */
class WriteTagsTest {
    companion object {
        lateinit var tempDir: File

        @BeforeAll
        @JvmStatic
        fun setup() {
            tempDir = initImagesDir()
        }
    }

    @Test
    internal fun `can add new DateTimeOriginal tag to image`() {
        val imageFile = Path(tempDir.path, "0000.jpg").toString()
        Kexif.openJPEG(imageFile).use { metadata ->
            metadata.set(DateTimeOriginal, "2020:11:26 16:17:18")
            assertThat(metadata.getString(DateTimeOriginal), equalTo("2020:11:26 16:17:18"))
        }

        // read the file in again and verify the changes were written to disk
        Kexif.openJPEG(imageFile).use { metadata ->
            assertThat(metadata.getString(DateTimeOriginal), equalTo("2020:11:26 16:17:18"))
        }
    }
}

private fun initImagesDir(): File {
    val dir = createTempDir()
    File(WriteTagsTest::class.java.getResource("/images").file).walk().asIterable()
        .filterNot { it.name in setOf("images", "contents.md") }
        .forEach { f ->
            f.copyTo(target = File(dir.path, f.name), overwrite = true)
        }
    return dir
}

private fun createTempDir(): File = Files.createDirectories(
    Paths.get("", "build", "kexif-test-dir").toAbsolutePath(),
    PosixFilePermissions.asFileAttribute(setOf(OWNER_READ, OWNER_WRITE, OWNER_EXECUTE))
).toFile()