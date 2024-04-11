package ru.tuneit.videostreamingdemo.model

data class StreamChunk (val fileMetadata: FileMetadata, val chunk: ByteArray ) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StreamChunk

        if (fileMetadata != other.fileMetadata) return false
        if (!chunk.contentEquals(other.chunk)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fileMetadata.hashCode()
        result = 31 * result + chunk.contentHashCode()
        return result
    }
}
