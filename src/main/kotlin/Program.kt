import javafx.stage.Stage
import tornadofx.*
import view.MainView
import view.Styles

class Program : App(MainView::class, Styles::class) {
    override fun start(stage: Stage) {
        super.start(stage)
        stage.width = 800.0
        stage.height = 600.0
    }
}

fun main(args: Array<String>) {
    launch<Program>(args)
}