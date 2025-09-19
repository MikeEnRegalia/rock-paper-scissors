'use client'

import {Alert, Button, Spinner} from 'react-bootstrap'
import {useRouter} from 'next/navigation'
import useSWR from 'swr'

const backendUrl = process.env.NEXT_PUBLIC_BACKEND_URL

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

export function CreateMatchButton() {
    const router = useRouter()
    const createGameCallback: () => void = () => createGame()
        .then(data => router.push(`/matches/${data.id}/players/${data.match.players[0]}`))
        .catch(err => console.log(err))
    return <Button onClick={createGameCallback}>New Match</Button>
}

export function Match({matchId}: { matchId: string }) {
    const {
        data,
        isLoading,
        error,
        mutate
    } = useSWR<Match>(`${backendUrl}/matches/${matchId}`, swrFetcher, {
        refreshInterval: 1000
    })

    const errorUI = error && <>
        <Alert variant="danger">The match could not be loaded.</Alert>
        <CreateMatchButton/>
    </>

    const loadingUI = isLoading && <Spinner className="float-end"/>

    if (data == null) {
        return <>
            {loadingUI}
            <h1>Rock Paper Scissors</h1>
            {errorUI}
        </>
    }

    if (error) {
        return errorUI
    }

    const {players, playedGames, currentGame} = data

    const lastPlayers = currentGame.moves.map(move => move.player)

    const score = (player: string) => playedGames.flatMap(game => game.wins.filter(win => win.winner === player)).length

    const rankedPlayers = [...players].sort((a, b) => score(b) - score(a))
    const winner = score(rankedPlayers[1]) < score(rankedPlayers[0]) ? rankedPlayers[0] : null

    return <>
        {loadingUI}
        <h1>Rock Paper Scissors</h1>
        <div className="d-flex flex-column gap-1">
            <table className="table">
                <thead>
                <tr>
                    {players.map((player, playerIndex) => <th key={player} style={{width: '30%'}}>
                        Player {playerIndex + 1}: {score(player)} {player == winner ?
                        <span className="small text-success">[WINNER]</span> : null}
                    </th>)}
                    <th></th>
                </tr>
                </thead>
                <tbody>
                {playedGames.map((game, i) => <tr key={i}>
                    {players.map(player => <td key={player} className={getMoveCSS(game, player)}>
                        {game.moves.find(m => m.player === player)?.symbol}
                    </td>)}
                    <td></td>
                </tr>)}
                <tr>

                </tr>
                <tr>
                    {players.map((player, playerIndex) => <td key={player} className="align-middle">
                        {lastPlayers?.includes(player) ? currentGame.moves.find(m => m.player === player)?.symbol :
                            <div className="d-inline-flex gap-1">{gameSymbols.map(symbol =>
                                <Button
                                    key={symbol} onClick={() => {
                                    makeMove(matchId, player, symbol)
                                        .then(match => mutate(match))
                                        .catch(err => console.log(err))

                                }}>{symbol}</Button>)}
                            </div>}
                    </td>)}
                    <td></td>
                </tr>

                </tbody>
            </table>
        </div>


        <pre className="small mt-4 text-body-tertiary">
            {JSON.stringify(data, null, 4)}
        </pre>
    </>
}

function getMoveCSS(game: PlayedGame, player: string) {
    if (game.wins.some(win => win.winner === player)) {
        return 'text-success'
    }
    if (game.wins.some(win => win.loser === player)) {
        return 'text-danger text-decoration-line-through'
    }
    return 'text-body-tertiary'
}

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

async function swrFetcher(url: string) {
    const res = await fetch(url, {
        headers: {'Content-Type': 'application/json',}
    })

    if (!res.ok) {
        throw new Error(`Error: ${res.status} ${res.statusText}`)
    }

    return await res.json()
}
