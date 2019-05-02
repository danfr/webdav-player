package controller

import controller.Utils.vlcSupportedExtensions
import dto.DAVElement
import dto.ElementType
import tornadofx.*
import view.MainView
import java.net.URL
import java.time.LocalDateTime


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
            val res = Utils.callWebDav(username, password, url)

            if (res.isNotEmpty()) {
                // remove the current directory from list
                val current = res.findLast { it.filepath.trimEnd('/', ' ').toLowerCase() == currentUrl.path.toLowerCase() }
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

    fun loadElement(element: DAVElement?) {
        var portSuffix = ""
        if (currentUrl.port != -1)
            portSuffix = ":" + currentUrl.port

        if (element?.type == ElementType.DIRECTORY) {
            val fullhost = "${currentUrl.protocol}://${currentUrl.host}$portSuffix"
            tryGet(fullhost + element.filepath, currentUsername, currentPassword)
        } else if (element?.type == ElementType.FILE) {
            val extension = element.filename.substringAfterLast('.', "")
            if (vlcSupportedExtensions.contains(extension.toLowerCase()))
                Utils.openWithVLC(currentUrl.protocol, currentUrl.host, portSuffix, element.filepath, currentUsername, currentPassword)
        }
    }
}