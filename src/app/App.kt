package app

import Game
import board.board
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState

interface AppState : RState

interface AppProps : RProps {
    var game: Game
    var restart: () -> Unit
}

class App : RComponent<AppProps, AppState>() {

    override fun AppState.init(props: AppProps) {

    }

    override fun RBuilder.render() {
        board(props.game, props.restart)
    }
}

fun RBuilder.app(game: Game, restart: () -> Unit) = child(App::class) {
    this.attrs.game = game
    this.attrs.restart = restart
}
