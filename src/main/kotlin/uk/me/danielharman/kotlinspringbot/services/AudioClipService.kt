package uk.me.danielharman.kotlinspringbot.services

import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.gridfs.GridFsTemplate
import org.springframework.stereotype.Service
import uk.me.danielharman.kotlinspringbot.models.AudioClip
import uk.me.danielharman.kotlinspringbot.repositories.AudioClipRepository
import java.io.InputStream

@Service
class AudioClipService(
    private val audioClipRepository: AudioClipRepository,
    private val gridFsTemplate: GridFsTemplate
) {

    fun getAudioClip(guildId: String, clipName: String): AudioClip? =
        audioClipRepository.findOneByGuildIdAndClipName(guildId, clipName)

    fun getAudioClips(guildId: String, page: Int = 0, pageSize: Int = 25): List<AudioClip> =
        audioClipRepository.findAllByGuildId(guildId, PageRequest.of(page, pageSize)).toList()

    fun saveAudioClip(guildId: String, clipName: String, fileName: String, stream: InputStream): Boolean {
        val audioClip = getAudioClip(guildId, clipName)
        if (audioClip != null) {
            return false
        }
        val clip = audioClipRepository.save(AudioClip(clipName, fileName, guildId))

        gridFsTemplate.store(stream, clip.gridFsName)
        return true
    }

    fun getAudioClipStream(guildId: String, clipName: String): InputStream? {
        val audioClip = getAudioClip(guildId, clipName) ?: return null

        val findOne = gridFsTemplate.findOne(
            Query(
                Criteria.where("filename").`is`(audioClip.gridFsName)
            )
        )
        return gridFsTemplate.getResource(findOne).inputStream
    }

}