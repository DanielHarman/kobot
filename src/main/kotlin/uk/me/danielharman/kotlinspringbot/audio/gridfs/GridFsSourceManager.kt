package uk.me.danielharman.kotlinspringbot.audio.gridfs

import com.mongodb.client.gridfs.model.GridFSFile
import com.sedmelluq.discord.lavaplayer.container.*
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.ProbingAudioSourceManager
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioItem
import com.sedmelluq.discord.lavaplayer.track.AudioReference
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.gridfs.GridFsTemplate
import java.io.DataInput
import java.io.DataOutput
import java.io.IOException


class GridFsSourceManager(containerRegistry: MediaContainerRegistry, private val gridFsTemplate: GridFsTemplate) :
    ProbingAudioSourceManager(containerRegistry) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    constructor(gridFsTemplate: GridFsTemplate) : this(MediaContainerRegistry.DEFAULT_REGISTRY, gridFsTemplate) {}

    override fun getSourceName(): String = "gridfs"

    override fun loadItem(manager: DefaultAudioPlayerManager, reference: AudioReference): AudioItem {
        val fileName = reference.identifier
        logger.info("Have $fileName")
        val findOne: GridFSFile = gridFsTemplate.findOne(Query(Criteria.where("filename").`is`(reference.identifier)))

        return handleLoadResult(detectContainer(reference, findOne))
    }

    private fun detectContainer(reference: AudioReference, gridFSFile: GridFSFile): MediaContainerDetectionResult {
        try {
            GridFsSeekableInputStream(gridFsTemplate, gridFSFile, 0).use { inputStream ->
                val lastDotIndex: Int = gridFSFile.filename.lastIndexOf('.')
                val fileExtension: String? =
                    if (lastDotIndex >= 0) gridFSFile.filename.substring(lastDotIndex + 1) else null

                logger.info("Trying to determine filetype from ${fileExtension}")

                return MediaContainerDetection(
                    containerRegistry, reference, inputStream,
                    MediaContainerHints.from(null, fileExtension)
                ).detectContainer()
            }
        } catch (e: IOException) {
            throw FriendlyException("Failed to open file for reading.", FriendlyException.Severity.SUSPICIOUS, e)
        }
    }

    override fun createTrack(trackInfo: AudioTrackInfo, containerTrackFactory: MediaContainerDescriptor): AudioTrack {
        return GridFsAudioTrack(trackInfo, gridFsTemplate, containerTrackFactory, this)
    }

    override fun isTrackEncodable(track: AudioTrack): Boolean {
        return true
    }

    override fun encodeTrack(track: AudioTrack, output: DataOutput?) {
        encodeTrackFactory((track as GridFsAudioTrack).getContainerTrackFactory(), output)
    }

    override fun decodeTrack(trackInfo: AudioTrackInfo, input: DataInput): AudioTrack? {
        val containerTrackFactory = decodeTrackFactory(input)
        return if (containerTrackFactory != null) {
            GridFsAudioTrack(trackInfo, gridFsTemplate, containerTrackFactory, this)
        } else null
    }

    override fun shutdown() {

    }


}