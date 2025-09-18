import {useState} from 'react'
import {Button} from 'react-bootstrap'

interface GameAndId {
    id: string
    game: Game
}

interface Game {
    players: string[]
    moves: {by: string, symbol: GameSymbol}[]
    scores: Record<string, number>
}

type GameSymbol = 'ROCK' | 'PAPER' | 'SCISSORS'
const gameSymbols: GameSymbol[] = ['ROCK', 'PAPER', 'SCISSORS']

export function Game() {
    const [data, setData] = useState<GameAndId | null>(null)
    const createGameCallback: () => void = () => createGame()
        .then(game => setData(game))

    const createGameButton = <Button onClick={createGameCallback}>New Game</Button>

    if (data == null) {
        return createGameButton
    }

    const {id: gameId, game} = data
    const {players, moves, scores} = game

    const lastPlayer = moves.length == 0 ? null : moves[moves.length - 1].by

    return <>
        Score: {players.map((player, playerIndex) => <span
        key={player}>player #{playerIndex + 1}: {scores[player] ?? 0} </span>)}

        <div className="d-flex flex-column gap-1">
            {players.map((player, playerIndex) => <div key={player}>
                <div>Player {playerIndex + 1}: <div className="d-inline-flex gap-1">{gameSymbols.map(symbol => <Button onClick={() => {
                    makeMove(gameId, player, symbol).then(game => setData({id: gameId, game}))
                }} disabled={lastPlayer === player}>
                    {symbol}
                </Button>)}</div></div>
            </div>)}
            <div>
                {createGameButton}
            </div>
        </div>


        <pre className="mt-4 text-body-tertiary">
            {JSON.stringify(game, null, 4)}
        </pre>
    </>
}

async function createGame() {
    const url = 'http://localhost:8080/rps/games'
    const response = await fetch(url, {method: 'POST'})
    return await response.json() as GameAndId
}

async function makeMove(gameId: string, player: string, symbol: GameSymbol) {
    const url = `http://localhost:8080/rps/games/${gameId}/moves`
    const response = await fetch(url, {
        method: 'POST',
        body: JSON.stringify({player, symbol}),
        headers: {'Content-Type': 'application/json'}
    })
    return await response.json() as Game

}
