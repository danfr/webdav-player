package controller

import com.github.sardine.SardineFactory
import dto.DAVElement
import dto.ElementType
import java.io.File
import java.io.IOException
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import java.util.concurrent.TimeUnit
import javax.naming.ConfigurationException
import kotlin.collections.ArrayList


object Utils {
    private const val propertyFilename = "webdav-player.properties"
    val vlcSupportedExtensions = listOf("asx", "dts", "gxf", "m2v", "m3u", "m4v", "mpeg1", "mpeg2", "mts", "mxf", "ogm", "pls", "bup", "a52", "aac", "b4s", "cue", "divx", "dv", "flv", "m1v", "m2ts", "mkv", "mov", "mpeg4", "oma", "spx", "ts", "vlc", "vob", "xspf", "dat", "bin", "ifo", "part", "3g2", "avi", "mpeg", "mpg", "flac", "m4a", "mp1", "ogg", "wav", "xm", "3gp", "srt", "wmv", "ac3", "asf", "mod", "mp2", "mp3", "mp4", "wma", "mka", "m4p")

    val properties by lazy {
        val res = Properties()
        val classloader = Thread.currentThread().contextClassLoader
        val propertiesIs = classloader.getResourceAsStream(propertyFilename)
        propertiesIs.ifNull { throw ConfigurationException("Property file '$propertyFilename' not found !") }
        res.load(propertiesIs)
        res
    }

    fun callWebDav(username: String, password: String, url: String): ArrayList<DAVElement> {
        val elements = ArrayList<DAVElement>()
        val sardine = SardineFactory.begin()
        sardine.setCredentials(username, password)
        val resources = sardine.list(url)
        resources.forEach { davResource ->
            elements.add(DAVElement(
                    davResource.name,
                    davResource.href.toASCIIString(),
                    davResource.contentLength,
                    LocalDateTime.ofInstant(davResource.modified.toInstant(), ZoneId.systemDefault()),
                    if (davResource.isDirectory) ElementType.DIRECTORY else ElementType.FILE)
            )
        }
        return elements
    }

    fun openWithVLC(protocol: String, host: String, portSuffix: String, filepath: String, username: String, password: String) {
        val fullUrl = "$protocol://$username:$password@$host$portSuffix$filepath"
        val vlcExe = properties.getProperty("vlc.binaries.executable")
        "$vlcExe,$fullUrl".runAsyncCommand(File("."))
    }

    private fun String.runAsyncCommand(workingDir: File) {
        val commandLineThread = Thread {
            try {
                ProcessBuilder(*split(",").toTypedArray())
                        .directory(workingDir)
                        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                        .redirectError(ProcessBuilder.Redirect.INHERIT)
                        .start()
                        .waitFor(1, TimeUnit.MINUTES)
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }
        commandLineThread.isDaemon = true
        commandLineThread.start()
    }

    private infix fun Any?.ifNull(block: () -> Unit) {
        if (this == null) block()
    }
}