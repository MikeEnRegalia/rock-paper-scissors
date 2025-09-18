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


data class Game(
    val players: List<String> = listOf(),
    val doneRounds: List<DoneRound> = listOf(),
    val openRound: OpenRound = OpenRound()
)

data class DoneRound(val moves: List<Move> = listOf(), val wins: List<Win> = listOf())
data class OpenRound(val moves: List<Move> = listOf())

data class Move(val by: String, val symbol: GameSymbol)
data class Win(val winner: String, val loser: String)

fun Game.canMove(player: String) = openRound.moves.none { it.by == player }
fun Game.makeMove(move: Move): Game {
    if (openRound.moves.any { it.by == move.by }) throw IllegalStateException()

    val newMoves = openRound.moves + move
    if (newMoves.size < players.size) return copy(openRound = openRound.copy(moves = newMoves))

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

    return copy(doneRounds = doneRounds + DoneRound(moves = newMoves, wins = wins), openRound = OpenRound())
}
