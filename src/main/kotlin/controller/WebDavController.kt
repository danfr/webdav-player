package controller

import com.github.sardine.SardineFactory
import dto.DAVElement
import dto.ElementType
import tornadofx.*
import view.MainView
import java.net.URL
import java.time.LocalDateTime
import java.time.ZoneId


class WebDavController : Controller() {
    private val view: MainView by inject()
    private lateinit var currentUrl: URL
    private lateinit var currentUsername: String
    private lateinit var currentPassword: String

    private fun refreshContent(elements: ArrayList<DAVElement>) {
        runLater {
            view.updateTable(elements)
            view.updateCurrentPath(currentUrl.path)
        }
    }

    fun tryGet(url: String, username: String, password: String) {
        view.loadingMessage()

        runAsync {
            //Save context
            currentUrl = URL(url.trim('/', ' '))
            currentUsername = username
            currentPassword = password

            // Get elements from server
            val res = callWebDav(username, password, url)

            if (res.isNotEmpty()) {
                // remove the current directory from list
                val current = res.findLast { it.filepath.trimEnd('/', ' ') == currentUrl.path }
                var lastUpdate = LocalDateTime.now()

                current?.let {
                    lastUpdate = current.lastUpdate
                    res.removeAt(res.indexOf(current))
                }

                //Add manually current element's parent
                val splittedPath = currentUrl.path.split("/")
                val filteredPath = splittedPath.dropLast(1) // we drop current path
                val parentPath = filteredPath.joinToString("/")
                res.add(DAVElement("..", parentPath, -1, lastUpdate, ElementType.DIRECTORY))
            }
            res
        } ui { result: ArrayList<DAVElement> ->
            refreshContent(result)
        }
    }

    private fun callWebDav(username: String, password: String, url: String): ArrayList<DAVElement> {
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

    fun loadElement(element: DAVElement?) {
        var portSuffix = ""
        if (currentUrl.port != -1)
            portSuffix = ":" + currentUrl.port

        val fullhost = "${currentUrl.protocol}://${currentUrl.host}$portSuffix"

        if (element?.type == ElementType.DIRECTORY) {
            tryGet(fullhost + element.filepath, currentUsername, currentPassword)
        } else if (element?.type == ElementType.FILE) {
            openWithVLC(fullhost + element.filepath, currentUsername, currentPassword)
        }
    }

    private fun openWithVLC(url: String, currentUsername: String, currentPassword: String) {

    }
}