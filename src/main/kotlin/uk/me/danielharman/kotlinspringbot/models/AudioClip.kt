package uk.me.danielharman.kotlinspringbot.models

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "AudioClips")
class AudioClip(val clipName: String, val fileName: String, var guildId: String) {

    @Id
    lateinit var id: String

    val gridFsName: String
            get() = "clip:$guildId:$clipName:$fileName"

}