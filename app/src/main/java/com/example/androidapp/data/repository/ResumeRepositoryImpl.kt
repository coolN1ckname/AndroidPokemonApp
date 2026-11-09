package com.example.androidapp.data.repository

import android.content.Context
import com.example.androidapp.repository.ResumeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

class ResumeRepositoryImpl(
    private val context: Context
) : ResumeRepository {

    override suspend fun downloadResume(url: String): File =
        withContext(Dispatchers.IO) {

            require(url.startsWith("https://")) {
                "Укажи корректную HTTPS-ссылку"
            }

            require(url.substringBefore('?').endsWith(".pdf", true)) {
                "Поддерживаются только PDF-файлы"
            }

            val connection = URL(url)
                .openConnection() as HttpURLConnection

            try {
                connection.connectTimeout = 15000
                connection.readTimeout = 15000
                connection.requestMethod = "GET"

                if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                    throw Exception("Не удалось скачать PDF")
                }

                val file = File(
                    context.cacheDir,
                    "resume_${System.currentTimeMillis()}.pdf"
                )

                connection.inputStream.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                file
            } finally {
                connection.disconnect()
            }
        }
}