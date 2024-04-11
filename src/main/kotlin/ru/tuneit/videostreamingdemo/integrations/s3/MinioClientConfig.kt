package ru.tuneit.videostreamingdemo.integrations.s3

import io.minio.MinioClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MinioClientConfig (
    @Value("\${minio.url}") val minioUrl: String,
    @Value("\${minio.username}") val minioUsername: String,
    @Value("\${minio.password}") val minioPassword: String) {

    @Bean
    fun generateMinioClient(): MinioClient {
        return try {
            MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(minioUsername, minioPassword)
                .build()
        } catch (e: Exception) {
            throw RuntimeException(e.message)
        }
    }
}
