package uk.me.danielharman.kotlinspringbot.audio.gridfs

import com.mongodb.client.gridfs.model.GridFSFile
import com.sedmelluq.discord.lavaplayer.tools.io.SeekableInputStream
import com.sedmelluq.discord.lavaplayer.track.info.AudioTrackInfoProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.gridfs.GridFsTemplate
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.util.*

class GridFsSeekableInputStream(gridFsTemplate: GridFsTemplate, private val file: GridFSFile, maxSkipDistance: Long) :
    SeekableInputStream(file.length, maxSkipDistance) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    private val inStream: InputStream = gridFsTemplate.getResource(file).inputStream
    private val stream: BufferedInputStream = BufferedInputStream(inStream)
    private var position: Long = 0

    override fun getPosition(): Long = position

    override fun read(): Int {
        val result = stream.read()

        if (result >= 0) {
            position++
        }
        return result
    }

    @Throws(IOException::class)
    override fun read(b: ByteArray, off: Int, len: Int): Int {
        val read: Int = stream.read(b, off, len)
        position += read.toLong()
        return read
    }

    @Throws(IOException::class)
    override fun skip(n: Long): Long {
        val skipped: Long = stream.skip(n)
        position += skipped
        return skipped
    }

    @Synchronized
    @Throws(IOException::class)
    override fun reset() {
        throw IOException("mark/reset not supported")
    }

    override fun markSupported(): Boolean {
        return false
    }

    @Throws(IOException::class)
    override fun close() {
        try {
            stream.close()
        } catch (e: IOException) {
            logger.debug("Failed to close channel", e)
        }
    }

    @Throws(IOException::class)
    override fun seekHard(position: Long) {
        //skip(position)
    }

    override fun canSeekHard(): Boolean = true

    override fun getTrackInfoProviders(): MutableList<AudioTrackInfoProvider> = Collections.emptyList()

    @Throws(IOException::class)
    override fun available(): Int = stream.available()

}