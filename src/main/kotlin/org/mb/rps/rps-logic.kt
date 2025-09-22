package org.mb.rps

import org.mb.rps.GameResult.*
import org.mb.rps.GameSymbol.*

enum class GameSymbol { ROCK, PAPER, SCISSORS }
enum class GameResult { WIN, DRAW, LOSS }

fun GameSymbol.playedAgainst(opponent: GameSymbol) = when {
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
    val openMoves: List<Move> = listOf()
)

data class PlayedGame(val moves: List<Move> = listOf(), val wins: List<Win> = listOf())

data class Move(val player: String, val symbol: GameSymbol)
data class Win(val winner: String, val loser: String)

fun Match.canMove(player: String) = player in players && openMoves.none { it.player == player }

fun Match.makeMove(move: Move): Match {
    if (!canMove(move.player)) throw IllegalStateException()

    val moves = openMoves + move
    return when {
        moves.size < players.size -> copy(openMoves = moves)
        else -> copy(
            playedGames = playedGames + PlayedGame(moves, playerPairings().computeWins(moves)),
            openMoves = listOf()
        )
    }
}

private fun List<Pair<String, String>>.computeWins(moves: List<Move>) = mapNotNull { (player, opponent) ->
    when (moves.by(player).playedAgainst(moves.by(opponent))) {
        DRAW -> null
        WIN -> Win(player, opponent)
        LOSS -> Win(opponent, player)
    }
}

private fun List<Move>.by(player: String) = single { it.player == player }.symbol

private fun Match.playerPairings() = players.flatMap { p -> players.filter { it > p }.map { p to it } }
