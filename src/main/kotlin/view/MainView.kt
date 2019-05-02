package view

import controller.Utils
import controller.WebDavController
import dto.DAVElement
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Priority
import tornadofx.*
import view.Styles.Companion.fullWindow

class MainView : View("WebDAV Player") {
    override val root = BorderPane()

    private val webdavController: WebDavController by inject()

    private val elements = FXCollections.observableArrayList<DAVElement>()
    private val pathLabel = SimpleStringProperty("Please enter credentials")

    private val model = object : ViewModel() {
        val url = bind { SimpleStringProperty(Utils.properties.getProperty("default.url")) }
        val username = bind { SimpleStringProperty(Utils.properties.getProperty("default.username")) }
        val password = bind { SimpleStringProperty() }
    }

    fun updateTable(daoElements: ArrayList<DAVElement>) {
        // By default elements are sorted on type and filename
        val sortedList = daoElements.sortedWith(compareBy({ it.type }, { it.filename }))
        elements.clear()
        elements.addAll(sortedList)
    }

    fun updateCurrentPath(path: String) {
        pathLabel.set(path)
    }

    fun loadingMessage() {
        pathLabel.set("Loading...")
    }

    init {
        with(root) {
            prefWidthProperty().bind(root.widthProperty())
            vgrow = Priority.ALWAYS
            useMaxWidth = true
            useMaxHeight = true
            addClass(fullWindow)
            top {
                form {
                    vgrow = Priority.ALWAYS
                    useMaxWidth = true
                    useMaxHeight = true
                    fieldset {
                        field("Server URL") {
                            textfield(model.url) {
                                required()
                                whenDocked { requestFocus() }
                            }
                        }
                        field("Username") {
                            textfield(model.username) {
                                required()
                            }
                        }
                        field("Password") {
                            passwordfield(model.password).required()
                        }
                    }

                    button("Connect") {
                        isDefaultButton = true

                        action {
                            model.commit {
                                webdavController.tryGet(
                                        model.url.value,
                                        model.username.value,
                                        model.password.value
                                )
                            }
                        }
                    }
                }
            }
            right {
                vgrow = Priority.NEVER
            }
            bottom {
                hgrow = Priority.NEVER
            }
            left {
                vgrow = Priority.NEVER
            }
            center {
                vbox {
                    label(pathLabel)
                    tableview(elements) {
                        readonlyColumn("Type", DAVElement::type).pctWidth(10)
                        readonlyColumn("File Name", DAVElement::filename).remainingWidth()
                        readonlyColumn("URL", DAVElement::filepath).pctWidth(25)
                        readonlyColumn("Size", DAVElement::filesize).pctWidth(10)
                        readonlyColumn("Last modified", DAVElement::lastUpdate).pctWidth(15)
                        columnResizePolicy = SmartResize.POLICY
                        onUserSelect { element ->
                            webdavController.loadElement(element)
                        }

                    }
                }
            }
        }
    }
}