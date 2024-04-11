package ru.tuneit.videostreamingdemo.integrations.s3

import io.minio.*
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import ru.tuneit.videostreamingdemo.model.FileMetadata
import java.io.InputStream
import java.util.UUID
import kotlin.system.exitProcess


@Service
class MinioService (
    @Value("\${minio.bucket.name}")
    val bucketName: String,
    @Value("\${minio.stream.part-size}")
    val streamPartSize: Long,
    val minioClient: MinioClient
        ) {

    @PostConstruct
    fun initialize() {
        if (!bucketExists()) createBucket()
    }

    private fun bucketExists(): Boolean {
       return runCatching { minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build()) }
            .getOrDefault(false)
    }

    private fun createBucket() {
        val args = MakeBucketArgs.builder()
            .bucket(bucketName)
            .build()
        runCatching { minioClient.makeBucket(args)}
                    .onFailure { exitProcess(1) }
    }


    fun uploadFile(file: MultipartFile, fileMetadata: FileMetadata) {
        minioClient.putObject(
            PutObjectArgs
                .builder()
                .bucket(bucketName)
                .`object`(fileMetadata.uuid.toString())
                .stream(file.inputStream, file.size, streamPartSize)
                .build()
        )
    }

    fun deleteFile(fileUUID: UUID) {
        minioClient.removeObject(
            RemoveObjectArgs.builder()
                .bucket(bucketName)
                .`object`(fileUUID.toString())
                .build())
    }

    fun getFileAsInputStream(fileMetadata: FileMetadata, offset: Long, length: Long): InputStream {
        return minioClient.getObject(
            GetObjectArgs
                .builder()
                .bucket(bucketName)
                .offset(offset)
                .length(length)
                .`object`(fileMetadata.uuid.toString())
                .build()
        )
    }
}
