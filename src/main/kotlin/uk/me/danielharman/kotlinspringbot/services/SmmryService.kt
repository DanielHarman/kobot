package uk.me.danielharman.kotlinspringbot.services

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.ContentType.Application.Json
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import uk.me.danielharman.kotlinspringbot.models.SmmryResponse
import uk.me.danielharman.kotlinspringbot.properties.SmmryPropeties

//
@Service
class SmmryService(private val smmryPropeties: SmmryPropeties) {
    var httpClient: HttpClient = HttpClient(CIO)

    fun getSummaryText(url: String, length: Int) : SmmryResponse?{
        val response = runBlocking {httpClient.request<SmmryResponse>(smmryPropeties.endpoint){
            method = HttpMethod.Get
            parameter("SM_API_KEY", smmryPropeties.apiKey)
            parameter("SM_LENGTH", length)
            parameter("SM_URL", url)
            accept(ContentType.Application.Json)
        }}
        return response
    }
}