package uk.me.danielharman.kotlinspringbot.helpers

import org.springframework.stereotype.Component

@Component
class RequestBuilder {
    fun buildRequest(params: Map<String, String>): String{
        var request = ""
        for(param in params){
            request += "&" + param.key + "=" + param.value
        }
        return request
    }
}