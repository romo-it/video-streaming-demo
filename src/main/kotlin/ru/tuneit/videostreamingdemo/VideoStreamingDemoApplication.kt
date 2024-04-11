package ru.tuneit.videostreamingdemo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class VideoStreamingDemoApplication

fun main(args: Array<String>) {
    runApplication<VideoStreamingDemoApplication>(*args)
}
