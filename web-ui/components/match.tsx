import {useState} from 'react'
import {Button} from 'react-bootstrap'

interface MatchAndId {
    id: string
    match: Match
}

interface Match {
    players: string[]
    playedGames: PlayedGame[]
    currentGame: OpenGame
}

interface PlayedGame {
    moves: Move[]
    wins: Win[]
}

interface OpenGame {
    moves: Move[]
}

interface Move {
    player: string
    symbol: GameSymbol
}

interface Win {
    winner: string
    loser: string
}

type GameSymbol = 'ROCK' | 'PAPER' | 'SCISSORS'
const gameSymbols: GameSymbol[] = ['ROCK', 'PAPER', 'SCISSORS']

export function Match() {
    const [data, setData] = useState<MatchAndId | null>(null)
    const createGameCallback: () => void = () => createGame()
        .then(game => setData(game))
        .catch(err => console.log(err))

    const createGameButton = <Button onClick={createGameCallback}>New Game</Button>

    if (data == null) {
        return createGameButton
    }

    const {id: matchId, match} = data
    const {players, playedGames, currentGame} = match

    const lastPlayers = currentGame.moves.map(move => move.player)

    return <>
        Score: {players.map((player, playerIndex) => <span
        key={player}>player #{playerIndex + 1}: {playedGames.flatMap(game => game.wins.filter(win => win.winner === player)).length} </span>)}

        <div className="d-flex flex-column gap-1">
            {players.map((player, playerIndex) => <div key={player}>
                <div>Player {playerIndex + 1}: <div className="d-inline-flex gap-1">{gameSymbols.map(symbol => <Button
                    key={symbol} onClick={() => {
                    makeMove(matchId, player, symbol)
                        .then(match => setData({id: matchId, match}))
                        .catch(err => console.log(err))

                }} disabled={lastPlayers?.includes(player)}>
                    {symbol}
                </Button>)}</div></div>
            </div>)}
            <div>
                {createGameButton}
            </div>
        </div>


        <pre className="mt-4 text-body-tertiary">
            {JSON.stringify(match, null, 4)}
        </pre>
    </>
}

const backendUrl = process.env.NEXT_PUBLIC_BACKEND_URL

async function createGame() {
    const url = `${backendUrl}/matches`
    const response = await fetch(url, {method: 'POST'})
    if (response.status != 200) throw new Error(response.statusText)

    return await response.json() as MatchAndId
}

async function makeMove(matchId: string, player: string, symbol: GameSymbol) {
    const url = `${backendUrl}/matches/${matchId}/moves`
    const response = await fetch(url, {
        method: 'POST',
        body: JSON.stringify({player, symbol}),
        headers: {'Content-Type': 'application/json'}
    })
    if (response.status != 200) throw new Error(response.statusText)

    return await response.json() as Match

}
