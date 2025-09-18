'use client'

import {useState} from 'react'
import {Button} from 'react-bootstrap'

interface GameAndId {
    id: string
    game: Game
}

interface Game {
    players: string[]
}

async function createGame() {
    const url = 'http://localhost:8080/rps/games'
    const response = await fetch(url, {method: 'POST'})
    return await response.json() as GameAndId
}

export default function Home() {
    const [game, setGame] = useState<GameAndId | null>(null)

    return <>
        <h1>Rock Paper Scissors</h1>
        <h2>Game: {game?.id ?? 'none'} <Button onClick={() => {
            createGame().then(game => {
                setGame(game)
            })
        }}>New Game</Button></h2>

    </>
}
