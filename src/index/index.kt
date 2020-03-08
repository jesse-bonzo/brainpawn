package index

import Game
import app.app
import kotlinext.js.require
import kotlinext.js.requireAll
import react.dom.render
import react.dom.unmountComponentAtNode
import kotlin.browser.document

fun main() {
    requireAll(require.context("src", true, js("/\\.css$/")))

    start()
}


fun start() {
    unmountComponentAtNode(document.getElementById("root"))
    render(document.getElementById("root")) {
        app(Game()) {
            start()
        }
    }
}

