import {useState} from 'react'
import {Button} from 'react-bootstrap'

interface GameAndId {
    id: string
    game: Game
}

interface Game {
    players: string[]
    scores: Record<string, number>
}

export function Game() {
    const [data, setData] = useState<GameAndId | null>(null)
    const createGameCallback: () => void = () => createGame()
        .then(game => setData(game))

    const createGameButton = <Button onClick={createGameCallback}>New Game</Button>

    if (data == null) {
        return createGameButton
    }

    const {game} = data
    const {players, scores} = game

    return <>
        <h2 className="h4">Current Game Stats</h2>
        {players.length} players, scores: {players.map((player, playerIndex) => <span key={player}>player #{playerIndex+1}: {scores[player] ?? 0} </span>)}
    </>
}

async function createGame() {
    const url = 'http://localhost:8080/rps/games'
    const response = await fetch(url, {method: 'POST'})
    return await response.json() as GameAndId
}

