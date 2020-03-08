package board

import Colors
import Game
import GameOverException
import InvalidGuessException
import kotlinx.html.DIV
import kotlinx.html.classes
import kotlinx.html.js.onClickFunction
import react.*
import react.dom.*

interface BoardProps : RProps {
    var game: Game
    var restart: () -> Unit
}

interface BoardState : RState {
    var rows: List<List<Colors?>>
    var hints: List<List<Colors>>
    var currentGuess: Int
    var message: String?
    var gameOver: Boolean
    var solved: Boolean
}

class Board(props: BoardProps) : RComponent<BoardProps, BoardState>(props) {

    override fun BoardState.init(props: BoardProps) {
        message = null
        updateFromGame()
    }

    fun BoardState.updateFromGame() {
        currentGuess = props.game.guesses.size

        val tempRows: MutableList<List<Colors?>> = props.game.guesses.map {
            val copy = ArrayList<Colors?>(it)
            while (copy.size < props.game.guessLength) {
                copy.add(null)
            }
            copy
        }.toMutableList()

        while (tempRows.size < props.game.maxGuesses) {
            tempRows.add(listOf(null, null, null, null))
        }

        rows = tempRows
        gameOver = props.game.done()
        solved = props.game.solved()
        hints = props.game.hints()
    }

    override fun componentDidMount() {

    }

    override fun componentWillUnmount() {

    }

    override fun RBuilder.render() {
        div("board") {
            state.rows.withIndex().forEach { (rowIndex, row) ->
                renderRow(rowIndex, row)
            }

            if (state.gameOver) {
                div("row") {
                    props.game.solution.forEach { color ->
                        span("space") {
                            attrs {
                                jsStyle {
                                    backgroundColor = color.name.toLowerCase()
                                }
                            }
                        }
                    }
                }
                if (state.solved) {
                    div("message") {
                        +"You win!"
                    }
                } else {
                    div("message") {
                        +"Game over"
                    }
                }
                button {
                    +"Play again"
                    attrs {
                        onClickFunction = {
                            props.restart()
                        }
                    }
                }
            } else {
                button {
                    +"Guess"
                    attrs {
                        onClickFunction = {
                            val game = props.game
                            try {
                                if (state.currentGuess < state.rows.size) {
                                    val guess = state.rows[state.currentGuess].filterNotNull()
                                    if (game.guess(guess)) {
                                        setState {
                                            message = "That's right!"
                                            updateFromGame()
                                        }
                                    } else {
                                        setState {
                                            message = null
                                            updateFromGame()
                                        }
                                    }
                                }
                            } catch (ex: GameOverException) {
                                setState {
                                    message = "Game over!"
                                    updateFromGame()
                                }
                            } catch (ex: InvalidGuessException) {
                                setState {
                                    message = "Invalid guess"
                                }
                            }
                        }
                    }
                }

                state.message?.let {
                    div("message") {
                        +it
                    }
                }
            }
        }
    }

    private fun RDOMBuilder<DIV>.renderRow(rowIndex: Int, row: List<Colors?>) {
        div("row") {
            attrs {
                if (state.currentGuess == rowIndex) {
                    classes += "current-row"
                }
            }

            row.withIndex().forEach { (spaceIndex, space) ->
                renderSpace(space, rowIndex, spaceIndex)
            }

            if (rowIndex < state.hints.size) {
                state.hints[rowIndex].forEach { color ->
                    span("hint") {
                        attrs {
                            jsStyle {
                                backgroundColor = color.name.toLowerCase()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun RDOMBuilder<DIV>.renderSpace(space: Colors?, rowIndex: Int, spaceIndex: Int) {
        span("space") {
            attrs {
                jsStyle {
                    space?.name?.toLowerCase()?.let {
                        backgroundColor = it
                    }
                }
                if (!state.gameOver && rowIndex == state.currentGuess) {
                    onClickFunction = {
                        // cycle through the colors on each click of a space
                        val spaceColor = state.rows[rowIndex][spaceIndex]
                        val colorIndex = (spaceColor?.ordinal?.plus(1) ?: 0) % Colors.values().size
                        val newSpaceColor = Colors.values()[colorIndex]
                        setState {
                            message = null
                            // copy over the guesses, except change the one at the current space
                            this.rows = this.rows.mapIndexed { i, list ->
                                if (i == rowIndex) {
                                    list.mapIndexed { j, color ->
                                        if (j == spaceIndex) {
                                            newSpaceColor
                                        } else {
                                            color
                                        }
                                    }
                                } else {
                                    list
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun RBuilder.board(game: Game, restart: () -> Unit) = child(Board::class) {
    attrs.game = game
    attrs.restart = restart
}
