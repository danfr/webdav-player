package dto

import java.time.LocalDateTime

class DAVElement(val filename: String, val filepath: String, val filesize: Long, val lastUpdate: LocalDateTime, val type: String)

object ElementType {
    val DIRECTORY = "DIR"
    val FILE = "FILE"
}