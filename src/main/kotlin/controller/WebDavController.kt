package controller

import com.github.sardine.SardineFactory
import dto.DAVElement
import dto.ElementType
import tornadofx.*
import view.MainView
import java.time.LocalDateTime
import java.time.ZoneId


class WebDavController : Controller() {
    private val view: MainView by inject()

    private fun refreshContent(elements: ArrayList<DAVElement>) {
        runLater {
            view.updateTable(elements)
        }
    }

    fun tryGet(url: String, username: String, password: String) {
        runAsync {
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
            elements
        } ui { result: ArrayList<DAVElement> ->
            refreshContent(result)
        }
    }
}