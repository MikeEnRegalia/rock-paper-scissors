package org.mb.rps

import org.mb.rps.GameSymbol.*

enum class GameSymbol { ROCK, PAPER, SCISSORS }

data class Game(
    val players: List<String> = listOf(),
    val rounds: List<Round> = listOf(Round())
)

data class Round(val moves: List<Move> = listOf(), val wins: List<Win> = listOf())
data class Move(val by: String, val symbol: GameSymbol)
data class Win(val winner: String, val loser: String)

fun Game.canMove(player: String) =  rounds.last().moves.none { it.by == player }
fun Game.makeMove(move: Move): Game {
    val lastRound = rounds.last()
    if (lastRound.moves.any { it.by == move.by }) throw IllegalStateException()

    val moves = lastRound.moves + move
    if (moves.size < players.size) return copy(rounds = rounds.dropLast(1) + lastRound.copy(moves = moves))

    val wins = buildList {
        for (player in players) for (otherPlayer in players) {
            if (player > otherPlayer) {
                val playerSymbol = moves.single { it.by == player }.symbol
                val otherPlayerSymbol = moves.single { it.by == otherPlayer }.symbol
                if (playerSymbol == otherPlayerSymbol) continue

                val playerWon = when (playerSymbol) {
                    ROCK -> otherPlayerSymbol == SCISSORS
                    PAPER -> otherPlayerSymbol == ROCK
                    SCISSORS -> otherPlayerSymbol == PAPER
                }

                add(
                    Win(
                        if (playerWon) player else otherPlayer,
                        if (playerWon) otherPlayer else player
                    )
                )
            }
        }
    }

    val newLastRound = lastRound.copy(moves = moves, wins = wins)
    return copy(rounds = rounds.dropLast(1) + newLastRound + Round())
}

