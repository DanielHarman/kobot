package uk.me.danielharman.kotlinspringbot.services

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.springframework.stereotype.Service
import uk.me.danielharman.kotlinspringbot.models.SmmryResponse
import uk.me.danielharman.kotlinspringbot.properties.SmmryPropeties
import kotlinx.serialization.json.Json


@Service
class SmmryService(private val smmryPropeties: SmmryPropeties) {
    var httpClient: HttpClient = HttpClient(CIO)

    suspend fun getSummaryText(url: String, length: Int) : SmmryResponse?{
        val response : HttpResponse = httpClient.post(smmryPropeties.endpoint){
            parameter("SM_API_KEY", smmryPropeties.apiKey)
            parameter("SM_LENGTH", length)
            parameter("SM_URL", url)
            accept(ContentType.Application.Json)
        }
        return Json.decodeFromString(SmmryResponse.serializer(), response.receive())
    }
}