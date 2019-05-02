package view

import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        val fullWindow by cssclass()
    }

    init {
        fullWindow {
            padding = box(15.px)
            vgap = 7.px
            hgap = 10.px
        }
    }
}