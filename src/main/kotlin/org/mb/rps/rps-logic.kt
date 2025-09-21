package org.mb.rps

import org.mb.rps.GameResult.*
import org.mb.rps.GameSymbol.*

enum class GameSymbol { ROCK, PAPER, SCISSORS }
enum class GameResult { WIN, DRAW, LOSS }

fun computeResult(player: GameSymbol, opponent: GameSymbol) = when {
    player == ROCK && opponent == SCISSORS -> WIN
    player == SCISSORS && opponent == PAPER -> WIN
    player == PAPER && opponent == ROCK -> WIN
    player == opponent -> DRAW
    else -> LOSS
}

data class Match(
    val players: List<String> = listOf(),
    val playedGames: List<PlayedGame> = listOf(),
    val currentGame: CurrentGame = CurrentGame()
)

data class PlayedGame(val moves: List<Move> = listOf(), val wins: List<Win> = listOf())
data class CurrentGame(val moves: List<Move> = listOf())

data class Move(val player: String, val symbol: GameSymbol)
data class Win(val winner: String, val loser: String)

fun Match.canMove(player: String) = player in players && currentGame.moves.none { it.player == player }
fun Match.makeMove(move: Move): Match {
    if (currentGame.moves.any { it.player == move.player }) throw IllegalStateException()

    val newMoves = currentGame.moves + move
    if (newMoves.size < players.size) return copy(currentGame = currentGame.copy(moves = newMoves))

    val wins = buildList {
        for (player in players) for (otherPlayer in players) if (player > otherPlayer) {
            val symbol = newMoves.single { it.player == player }.symbol
            val otherSymbol = newMoves.single { it.player == otherPlayer }.symbol

            when (computeResult(symbol, otherSymbol)) {
                DRAW -> continue
                WIN -> add(Win(player, otherPlayer))
                LOSS -> add(Win(otherPlayer, player))
            }
        }
    }

    return copy(playedGames = playedGames + PlayedGame(moves = newMoves, wins = wins), currentGame = CurrentGame())
}
