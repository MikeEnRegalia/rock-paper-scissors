package org.mb.rps

import org.springframework.stereotype.Service

@Service
class RpsRepository {
    private val matches = mutableMapOf<String, Match>()

    fun storeNewMatch(match: Match) {
        if (match.id in matches) throw IllegalArgumentException("match id ${match.id} already exists")
        matches[match.id] = match
    }

    fun getMatch(id: String) = matches[id]

    fun updateMatch(match: Match) {
        if (match.id !in matches) throw IllegalArgumentException("match id ${match.id} does not exist")
        matches[match.id] = match
    }
}
