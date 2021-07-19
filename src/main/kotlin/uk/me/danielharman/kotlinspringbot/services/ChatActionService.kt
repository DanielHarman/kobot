package uk.me.danielharman.kotlinspringbot.services

import org.springframework.stereotype.Service
import uk.me.danielharman.kotlinspringbot.helpers.Failure
import uk.me.danielharman.kotlinspringbot.helpers.OperationResult
import uk.me.danielharman.kotlinspringbot.helpers.Success
import uk.me.danielharman.kotlinspringbot.models.ChatAction
import uk.me.danielharman.kotlinspringbot.repositories.ChatActionRepository

@Service
class ChatActionService(private val chatActionRepository: ChatActionRepository) {

    fun deleteActionByMatchValue(originId: String, matchValue: String){
        chatActionRepository.deleteByOriginIdAndMatchValue(originId, matchValue)
    }

    fun getActionByMatchValue(originId: String, matchValue: String): OperationResult<ChatAction, String>{
        val value = chatActionRepository.findByOriginIdAndMatchValue(originId, matchValue)

        return if(value == null){
            Failure("No such chat action")
        } else{
            Success(value)
        }
    }

    fun getActions(originId: String): OperationResult<List<ChatAction>, String> {
        return Success(chatActionRepository.findAllByOriginId(originId))
    }

    fun createAction(
        actionValue: String,
        matchValue: String,
        action: ChatAction.Action,
        matchPolicy: ChatAction.MatchPolicy,
        originId: String,
        userId: String
    ): OperationResult<ChatAction, String> {

        val actionByMatchValue = getActionByMatchValue(originId, matchValue)

        if(actionByMatchValue is Success){
            deleteActionByMatchValue(originId, matchValue)
        }

        val newAction = ChatAction(actionValue, matchValue, action, matchPolicy, originId, userId)
        return Success(chatActionRepository.save(newAction))
    }

}