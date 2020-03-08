import kotlin.random.Random

class Game {
    val solution: List<Colors> = randomSolution()
    val guesses = mutableListOf<List<Colors>>()
    val maxGuesses = 12
    val guessLength = 4
    fun done() = guesses.size >= maxGuesses || solved()
    fun solved() = guesses.lastOrNull() == solution

    /**
     * returns true if your guess was right, false if not, exception if game is over
     */
    fun guess(guess: List<Colors>): Boolean {
        if (guess.size != guessLength) {
            throw InvalidGuessException()
        }

        if (done()) {
            // stop guessing!
            throw GameOverException()
        }

        guesses.add(guess)

        return guess == solution
    }

    fun hints() = guesses.map { guess ->
        val sol: Array<Colors?> = solution.toTypedArray()
        val hintColors = ArrayList<Colors>()

        sol.forEachIndexed { index, color ->
            if (guess[index] == color) {
                sol[index] = null
                hintColors.add(Colors.BLACK)
            }
        }

        guess.forEach {
            val index = sol.indexOf(it)
            if (index >= 0) {
                sol[index] = null
                hintColors.add(Colors.WHITE)
            }
        }
        hintColors
    }
}

class GameOverException : Exception()

class InvalidGuessException : Exception()

val rand = Random.Default

fun randomSolution() = listOf(randomColor(), randomColor(), randomColor(), randomColor())

fun randomColor() = Colors.values()[rand.nextInt(0, Colors.values().size)]