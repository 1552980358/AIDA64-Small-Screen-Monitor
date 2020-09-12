package app.github1552980358.aida64

import java.io.Serializable

class Settings(
    ip: String,
    port: String,
    val brightness: Boolean,
    val amoled: Boolean,
    val heartbeat: Long,
    val connect: Int,
    val read: Int
): Serializable {
    val link = "http://$ip:$port/"
}