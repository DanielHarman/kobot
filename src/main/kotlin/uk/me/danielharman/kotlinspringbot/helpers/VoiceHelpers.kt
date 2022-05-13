package uk.me.danielharman.kotlinspringbot.helpers

import java.net.URI

object VoiceHelpers {

    fun getAudioUrl(url: String): String {

        val uri = URI(url)
        var domain = uri.host
        if (domain != null) {
            domain = domain.removePrefix("www.")

            //Lavaplayer only needs the video id it turns out
            return when (domain) {
                "youtube.com" -> {
                    val path = uri.path
                    if (path.contains("/shorts/")) {
                        return path.split("/").last()
                    }
                    url
                }
                "youtu.be" -> uri.path.trimStart('/')
                else -> url
            }
        }
        return url
    }

}