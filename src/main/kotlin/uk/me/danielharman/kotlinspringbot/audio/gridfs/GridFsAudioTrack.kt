package uk.me.danielharman.kotlinspringbot.audio.gridfs

import com.mongodb.client.gridfs.model.GridFSFile
import com.sedmelluq.discord.lavaplayer.container.MediaContainerDescriptor
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo
import com.sedmelluq.discord.lavaplayer.track.DelegatedAudioTrack
import com.sedmelluq.discord.lavaplayer.track.InternalAudioTrack
import com.sedmelluq.discord.lavaplayer.track.playback.LocalAudioTrackExecutor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.gridfs.GridFsTemplate

class GridFsAudioTrack(
    trackInfo: AudioTrackInfo,
    private val gridFsTemplate: GridFsTemplate,
    private val containerTrackFactory: MediaContainerDescriptor,
    private val sourceManager: GridFsSourceManager
) : DelegatedAudioTrack(trackInfo) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun process(executor: LocalAudioTrackExecutor) {
        logger.info("Attempting to find and process ${trackInfo.identifier}")
        val findOne: GridFSFile = gridFsTemplate.findOne(Query(Criteria.where("filename").`is`(trackInfo.identifier)))
        val inputStream = GridFsSeekableInputStream(gridFsTemplate, findOne, 0)
        processDelegate(containerTrackFactory.createTrack(trackInfo, inputStream) as InternalAudioTrack, executor)
    }

    override fun makeShallowClone(): AudioTrack = GridFsAudioTrack(trackInfo, gridFsTemplate, containerTrackFactory, sourceManager)

    override fun getSourceManager(): AudioSourceManager = sourceManager

    fun getContainerTrackFactory(): MediaContainerDescriptor = containerTrackFactory

}