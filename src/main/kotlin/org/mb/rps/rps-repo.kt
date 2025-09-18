package org.mb.rps

import org.springframework.stereotype.Service

@Service
class RpsRepository {
    private val matches = mutableMapOf<String, Match>()

    fun createMatch(id: String, match: Match) = id to match
        .also { matches[id] = it }

    fun getMatch(id: String): Match? = matches[id]

    fun updateMatch(id: String, match: Match) {
        matches[id] = match
    }
}
