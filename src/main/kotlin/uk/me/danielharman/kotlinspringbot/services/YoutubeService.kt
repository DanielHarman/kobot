package uk.me.danielharman.kotlinspringbot.services

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import uk.me.danielharman.kotlinspringbot.KotlinBotProperties
import uk.me.danielharman.kotlinspringbot.helpers.Failure
import uk.me.danielharman.kotlinspringbot.helpers.OperationResult
import uk.me.danielharman.kotlinspringbot.helpers.Success
import java.io.InputStream

@Service
class YoutubeService(private val kotlinBotProperties: KotlinBotProperties) {

    data class YoutubeRequest(val mediaType: String, val url: String)

    fun getYoutubeAudio(url: String): OperationResult<InputStream, String> {

        if (kotlinBotProperties.youtubeDlMicroserviceHost == null) {
            return Failure("Microservice host not specified")
        }

        val client = HttpClient(CIO) {
            install(JsonFeature) {
                serializer = JacksonSerializer()
            }
        }

        return try {
            val response: HttpResponse = runBlocking {
                client.request(kotlinBotProperties.youtubeDlMicroserviceHost!!) {
                    contentType(ContentType.Application.Json)
                    method = HttpMethod.Post
                    body = YoutubeRequest("audio", url)
                }
            }
            val res: ByteArray = runBlocking { response.receive() }

            Success(res.inputStream())

        } catch (e: ClientRequestException) {
            Failure(e.message)
        }
    }

}