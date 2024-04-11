package ru.tuneit.videostreamingdemo.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import ru.tuneit.videostreamingdemo.model.FileMetadata
import ru.tuneit.videostreamingdemo.model.StreamRange
import ru.tuneit.videostreamingdemo.service.VideoStreamingService
import java.util.*


@RestController
@RequestMapping("/streaming/video")
class VideoStreamingController (
    val videoStreamingService: VideoStreamingService,
    @Value("\${streaming.chunk.default-size}") val chunkDefaultSize: Long
    ) {

    @GetMapping("/{uuid}")
    fun fetchChunk(
        @RequestHeader(value = RANGE, required = false) range: String?,
        @PathVariable uuid: UUID
    ): ResponseEntity<ByteArray?> {
        val parsedRange = StreamRange.parseHttpRangeString(range, chunkDefaultSize)
        val streamChunk = videoStreamingService.fetchChunk(uuid, parsedRange)
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
            .header(CONTENT_TYPE, streamChunk.fileMetadata.contentType)
            .header(ACCEPT_RANGES, "bytes")
            .header(CONTENT_LENGTH, getContentLengthHeader(parsedRange, streamChunk.fileMetadata.contentSize))
            .header(CONTENT_RANGE, getContentRangeHeader(parsedRange, streamChunk.fileMetadata.contentSize))
            .body(streamChunk.chunk)
    }

    @GetMapping("/list")
    fun getVideoList(): List<FileMetadata> = videoStreamingService.getVideoList()

    @PostMapping("/upload")
    fun saveVideo(
        @RequestParam("video") video: MultipartFile,
        @RequestParam("videoName") videoName : String)
                                                  = ResponseEntity.ok(videoStreamingService.saveVideo(video, videoName))

    @PostMapping("/delete/{uuid}")
    fun deleteVideo(@PathVariable uuid: UUID)  = ResponseEntity.ok(videoStreamingService.deleteVideo(uuid))


    private fun getContentLengthHeader(range: StreamRange, fileSize: Long)
                                                       = (range.getRangeEnd(fileSize) - range.rangeStart + 1).toString()


    private fun getContentRangeHeader(range: StreamRange, fileSize: Long)
                                      = "bytes " + range.rangeStart + "-" + range.getRangeEnd(fileSize) + "/" + fileSize

}


