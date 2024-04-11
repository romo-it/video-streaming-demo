package ru.tuneit.videostreamingdemo.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import ru.tuneit.videostreamingdemo.model.FileMetadata
import java.util.UUID

interface FileMetadataRepo : JpaRepository<FileMetadata, Long> {

    @Query("select f from FileMetadata f where f.uuid = :fileUUID")
    fun findByUuid(fileUUID: UUID) : FileMetadata?

    fun deleteByUuid(fileUUID: UUID)
}
