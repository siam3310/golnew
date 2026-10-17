package com.angeldevtech.gol.data.repositories

import com.angeldevtech.gol.domain.repositories.PlayerRepository
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class PlayerRepositoryImpl @Inject constructor() : PlayerRepository {

    private val m3u8Regex = Regex("""https?://[^\s"']+\.m3u8(\?[^\s"']*)?""")

    override suspend fun extractM3U8Url(webPlayerUrl: String): Result<String> = withContext(Dispatchers.IO) {
        try {

            if (webPlayerUrl.isBlank()) {
                return@withContext Result.failure(IllegalArgumentException("Source does not have a valid URL"))
            }

            if (webPlayerUrl.contains("rename.playztv3310.workers.dev") || webPlayerUrl.contains("workers.dev")) {
                val normalizedUrl = webPlayerUrl
                    .replace("mrtgh.b-cdn.net", "dertg.b-cdn.net")
                    .replace("crtgh.b-cdn.net", "dertg.b-cdn.net")
                    .replace("mrtgh", "dertg")
                return@withContext Result.success(normalizedUrl)
            }

            if (webPlayerUrl.contains("b-cdn.net")) {
                val normalizedUrl = webPlayerUrl
                    .replace("mrtgh.b-cdn.net", "dertg.b-cdn.net")
                    .replace("crtgh.b-cdn.net", "dertg.b-cdn.net")
                    .replace("mrtgh", "dertg")
                val encodedUrl = java.net.URLEncoder.encode(normalizedUrl, "UTF-8").replace("+", "%20")
                return@withContext Result.success("https://rename.playztv3310.workers.dev/?url=$encodedUrl")
            }

            if (webPlayerUrl.endsWith(".m3u8") || webPlayerUrl.contains(".m3u8?") ||
                webPlayerUrl.endsWith(".mp4") || webPlayerUrl.contains(".mp4?") ||
                webPlayerUrl.endsWith(".mkv") || webPlayerUrl.contains(".mkv?") ||
                webPlayerUrl.endsWith(".webm") || webPlayerUrl.contains(".webm?") ||
                webPlayerUrl.contains(".mkv")
            ) {
                return@withContext Result.success(webPlayerUrl)
            }

            val doc = Jsoup.connect(webPlayerUrl)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36")
                .timeout(15000)
                .get()

            val scripts = doc.select("script")
            for (script in scripts) {
                val scriptData = script.data()
                if (scriptData.isNotEmpty()) {
                    m3u8Regex.find(scriptData)?.let {
                        return@withContext Result.success(it.value)
                    }
                }
                val scriptHtml = script.html()
                if(scriptHtml.isNotEmpty()){
                    m3u8Regex.find(scriptHtml)?.let {
                        return@withContext Result.success(it.value)
                    }
                }
            }

            val bodyHtml = doc.body().html()
            m3u8Regex.find(bodyHtml)?.let {
                return@withContext Result.success(it.value)
            }


            Result.failure(RuntimeException("Could not obtain stream URL from source"))

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}