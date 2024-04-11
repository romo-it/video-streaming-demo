package ru.tuneit.videostreamingdemo.model

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import java.util.UUID

@Table(name = "file_meta")
@Entity
class FileMetadata (
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    var id: Long? = null,
    var name: String,
    var uuid: UUID,
    var contentType: String?,
    var contentSize: Long,
    @CreationTimestamp
    var uploadedAt: LocalDateTime
)
