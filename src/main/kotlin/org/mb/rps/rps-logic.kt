package org.mb.rps

import org.mb.rps.GameResult.*
import org.mb.rps.GameSymbol.*

enum class GameSymbol { ROCK, PAPER, SCISSORS }
enum class GameResult { WIN, DRAW, LOSS }

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

    val moves = openRound.moves + move
    if (moves.size < players.size) return copy(openRound = openRound.copy(moves = moves))

    val wins = buildList {
        for (player in players) for (otherPlayer in players) if (player > otherPlayer) {
            val playerSymbol = moves.single { it.by == player }.symbol
            val otherPlayerSymbol = moves.single { it.by == otherPlayer }.symbol

            when (val result = computeResult(playerSymbol, otherPlayerSymbol)) {
                DRAW -> continue
                WIN, LOSS -> add(
                    Win(
                        if (result == WIN) player else otherPlayer,
                        if (result == WIN) otherPlayer else player
                    )
                )
            }
        }
    }

    return copy(doneRounds = doneRounds + DoneRound(moves = moves, wins = wins), openRound = OpenRound())
}

fun computeResult(playerSymbol: GameSymbol, otherPlayerSymbol: GameSymbol) = when {
    playerSymbol == otherPlayerSymbol -> DRAW
    else -> when (playerSymbol) {
        ROCK -> if (otherPlayerSymbol == SCISSORS) WIN else LOSS
        PAPER -> if (otherPlayerSymbol == ROCK) WIN else LOSS
        SCISSORS -> if (otherPlayerSymbol == PAPER) WIN else LOSS
    }
}

