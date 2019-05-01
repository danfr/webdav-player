package view

import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        val loginForm by cssclass()
    }

    init {
        loginForm {
            padding = box(15.px)
            vgap = 7.px
            hgap = 10.px
        }
    }
}