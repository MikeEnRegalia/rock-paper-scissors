package org.mb.rps

import org.mb.rps.GameResult.*
import org.mb.rps.GameSymbol.*

enum class GameSymbol { ROCK, PAPER, SCISSORS }
enum class GameResult { WIN, DRAW, LOSS }

fun computeResult(symbol: GameSymbol, otherSymbol: GameSymbol) = when (symbol) {
    ROCK -> when (otherSymbol) {
        SCISSORS -> WIN
        ROCK -> DRAW
        PAPER -> LOSS
    }

    PAPER -> when (otherSymbol) {
        ROCK -> WIN
        PAPER -> DRAW
        SCISSORS -> LOSS
    }

    SCISSORS -> when (otherSymbol) {
        PAPER -> WIN
        SCISSORS -> DRAW
        ROCK -> LOSS
    }
}


data class Match(
    val players: List<String> = listOf(),
    val playedGames: List<PlayedGame> = listOf(),
    val currentGame: CurrentGame = CurrentGame()
)

data class PlayedGame(val moves: List<Move> = listOf(), val wins: List<Win> = listOf())
data class CurrentGame(val moves: List<Move> = listOf())

data class Move(val by: String, val symbol: GameSymbol)
data class Win(val winner: String, val loser: String)

fun Match.canMove(player: String) = currentGame.moves.none { it.by == player }
fun Match.makeMove(move: Move): Match {
    if (currentGame.moves.any { it.by == move.by }) throw IllegalStateException()

    val newMoves = currentGame.moves + move
    if (newMoves.size < players.size) return copy(currentGame = currentGame.copy(moves = newMoves))

    val wins = buildList {
        for (player in players) for (otherPlayer in players) if (player > otherPlayer) {
            val symbol = newMoves.single { it.by == player }.symbol
            val otherSymbol = newMoves.single { it.by == otherPlayer }.symbol

            when (computeResult(symbol, otherSymbol)) {
                DRAW -> continue
                WIN -> add(Win(player, otherPlayer))
                LOSS -> add(Win(otherPlayer, player))
            }
        }
    }

    return copy(playedGames = playedGames + PlayedGame(moves = newMoves, wins = wins), currentGame = CurrentGame())
}
