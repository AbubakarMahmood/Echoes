package com.echoes.app.util

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import java.io.File
import java.util.UUID

object CapsuleImageStorage {

    private const val IMAGE_DIRECTORY = "capsule-images"
    private const val FALLBACK_EXTENSION = "jpg"

    fun createCameraImageFile(context: Context): File {
        return createImageFile(context, "camera", FALLBACK_EXTENSION)
    }

    fun createCameraImageUri(context: Context, imageFile: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )
    }

    fun importImageFromUri(context: Context, sourceUri: Uri): String {
        val resolver = context.contentResolver
        val mimeType = resolver.getType(sourceUri)
        val extension = MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(mimeType)
            ?.takeIf { it.isNotBlank() }
            ?: FALLBACK_EXTENSION

        val destinationFile = createImageFile(context, "picked", extension)
        resolver.openInputStream(sourceUri)?.use { input ->
            destinationFile.outputStream().use { output ->
                input.copyTo(output)
            }
        } ?: error("Could not open image input stream.")

        return destinationFile.absolutePath
    }

    fun uriForStoredPath(path: String?): Uri? {
        if (path.isNullOrBlank()) return null

        return when {
            path.startsWith("content://") || path.startsWith("file://") -> Uri.parse(path)
            else -> {
                val imageFile = File(path)
                if (imageFile.exists()) imageFile.toUri() else null
            }
        }
    }

    fun deleteStoredImage(path: String?) {
        if (path.isNullOrBlank()) return

        runCatching {
            val imageFile = when {
                path.startsWith("file://") -> File(Uri.parse(path).path.orEmpty())
                path.startsWith("content://") -> null
                else -> File(path)
            }

            if (imageFile != null && imageFile.exists()) {
                imageFile.delete()
            }
        }
    }

    private fun createImageFile(context: Context, prefix: String, extension: String): File {
        val imageDirectory = File(context.filesDir, IMAGE_DIRECTORY).apply {
            mkdirs()
        }

        return File(
            imageDirectory,
            "${prefix}_${UUID.randomUUID()}.$extension"
        )
    }
}
