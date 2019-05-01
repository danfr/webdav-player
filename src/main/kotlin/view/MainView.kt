package view

import controller.WebDavController
import dto.DAVElement
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.scene.layout.GridPane
import tornadofx.*
import view.Styles.Companion.loginForm

class MainView : View() {
    override val root = GridPane()

    private val webdavController: WebDavController by inject()

    private val elements = FXCollections.observableArrayList<DAVElement>()

    private val model = object : ViewModel() {
        val url = bind { SimpleStringProperty() }
        val username = bind { SimpleStringProperty() }
        val password = bind { SimpleStringProperty() }
    }

    fun updateTable(daoElements: ArrayList<DAVElement>) {
        elements.clear()
        elements.addAll(daoElements)
    }

    init {
        with(root) {
            row {
                vbox {
                    label("WebDAV Player")
                }
            }
            row {
                form {
                    addClass(loginForm)
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
            row {
                vbox {
                    label("Tableview from a map")
                    tableview(elements) {
                        readonlyColumn("Type", DAVElement::type)
                        readonlyColumn("File Name", DAVElement::filename)
                        readonlyColumn("URL", DAVElement::filepath)
                        readonlyColumn("Size", DAVElement::filesize)
                        readonlyColumn("Last modified", DAVElement::lastUpdate)
                        columnResizePolicy = SmartResize.POLICY
                    }
                }
            }
        }
    }
}