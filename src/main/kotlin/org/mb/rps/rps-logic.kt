package org.mb.rps

import org.mb.rps.GameResult.*
import org.mb.rps.GameSymbol.*

enum class GameSymbol { ROCK, PAPER, SCISSORS }
enum class GameResult { WIN, DRAW, LOSS }

fun GameSymbol.playAgainst(opponent: GameSymbol) = when {
    this == ROCK && opponent == SCISSORS -> WIN
    this == SCISSORS && opponent == PAPER -> WIN
    this == PAPER && opponent == ROCK -> WIN
    this == opponent -> DRAW
    else -> LOSS
}

data class Match(
    val id: String,
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
    val moves = currentGame.moves + move
    return when {
        moves.size < players.size -> copy(currentGame = currentGame.copy(moves = currentGame.moves + move))
        else -> copy(playedGames = playedGames + PlayedGame(moves, computeWins(moves)), currentGame = CurrentGame())
    }
}

private fun Match.computeWins(newMoves: List<Move>) = playerPairings().mapNotNull { (player, opponent) ->
    when (newMoves.by(player).playAgainst(newMoves.by(opponent))) {
        DRAW -> null
        WIN -> Win(player, opponent)
        LOSS -> Win(opponent, player)
    }
}

private fun List<Move>.by(player: String) = single { it.player == player }.symbol

private fun Match.playerPairings() = players.flatMap { p -> players.filter { it > p }.map { p to it } }
