package ru.tuneit.videostreamingdemo.service

import jakarta.transaction.Transactional
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import ru.tuneit.videostreamingdemo.integrations.s3.MinioService
import ru.tuneit.videostreamingdemo.model.FileMetadata
import ru.tuneit.videostreamingdemo.model.StreamChunk
import ru.tuneit.videostreamingdemo.model.StreamRange
import ru.tuneit.videostreamingdemo.repo.FileMetadataRepo
import java.time.LocalDateTime
import java.util.*
import kotlin.NoSuchElementException

@Service
class VideoStreamingService (
    val fileMetadataRepo: FileMetadataRepo,
    val minioService: MinioService) {

    @Transactional
    fun saveVideo(video: MultipartFile, videoName: String): UUID {
         try {
            val videoUuid: UUID = UUID.randomUUID()
            val metadata = FileMetadata(
                name = videoName,
                uuid = videoUuid,
                contentType = video.contentType,
                contentSize = video.size,
                uploadedAt = LocalDateTime.now())
            fileMetadataRepo.save(metadata)
            minioService.uploadFile(video, metadata)
            return videoUuid
        } catch (e: Exception) {
            throw RuntimeException(e.message)
        }
    }

    @Transactional
    fun deleteVideo(videoUuid: UUID) {
        try {
          fileMetadataRepo.deleteByUuid(videoUuid)
          minioService.deleteFile(videoUuid)
        }
        catch (e: Exception) {
            throw RuntimeException(e.message)
        }
    }

    fun fetchChunk(uuid: UUID, range: StreamRange): StreamChunk {
        val fileMetadata = fileMetadataRepo.findByUuid(uuid)?: throw NoSuchElementException()
        return StreamChunk(fileMetadata, readChunk(fileMetadata, range))
    }

    private fun readChunk(fileMetadata: FileMetadata, range: StreamRange): ByteArray {
        val start = range.rangeStart
        val end = range.getRangeEnd(fileMetadata.contentSize)
        val chunkSize = end - start + 1
        try {
            minioService.getFileAsInputStream(fileMetadata, start, chunkSize)
                         .use { inputStream -> return inputStream.readBytes() }
        } catch (e: Exception) {
            throw RuntimeException(e.message)
        }
    }

    fun getVideoList(): MutableList<FileMetadata> = fileMetadataRepo.findAll(Sort.by(Sort.Direction.ASC, "name"))
}
