package ru.tuneit.videostreamingdemo.model

data class StreamRange(val rangeStart: Long, val rangeEnd: Long) {

    fun getRangeEnd(fileSize: Long): Long {
        return rangeEnd.coerceAtMost(fileSize - 1)
    }

    companion object {
        fun parseHttpRangeString(httpRangeString: String?, defaultChunkSize: Long): StreamRange {
            if (httpRangeString == null) {
                return StreamRange(0, defaultChunkSize)
            }
            val dashIndex = httpRangeString.indexOf("-")
            val startRange = httpRangeString.substring(6, dashIndex).toLong()
            val endRangeString = httpRangeString.substring(dashIndex + 1)
            if (endRangeString.isEmpty()) {
                return StreamRange(startRange, startRange + defaultChunkSize)
            }
            val endRange = endRangeString.toLong()
            return StreamRange(startRange, endRange)
        }
    }
}
