package uk.me.danielharman.kotlinspringbot.models

import org.joda.time.DateTime
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "ChatActions")
class ChatAction(
    val actionValue: String,
    val matchValue: String,
    val action: Action,
    val matchPolicy: MatchPolicy,
    val originId: String,
    val userId: String,
    val created: DateTime = DateTime.now()
) {

    @Id
    lateinit var id: String


    enum class Action {
        Reply,
        React,
        Delete
    }

    enum class MatchPolicy {
        Regex,
        Contains,
        Exact
    }

    fun match(value: String): Boolean{
        return when(matchPolicy){
            MatchPolicy.Regex -> value.matches(Regex(matchValue))
            MatchPolicy.Contains -> value.contains(matchValue, true)
            MatchPolicy.Exact -> value.contentEquals(matchValue,true)
        }
    }

}