'use client'

import {Alert, Button, Spinner} from 'react-bootstrap'
import {useRouter} from 'next/navigation'
import useSWR from 'swr'
import Link from 'next/link'

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
    const createGameCallback: () => void = async () => {
        try {
            const data = await createGame()
            router.push(`/matches/${data.id}/players/${data.match.players[0]}`)
        } catch (error) {
            console.log(error)
        }
    }
    return <Button onClick={createGameCallback}>New Match</Button>
}

export function Match({matchId, player: you}: { matchId: string, player: string }) {
    const {
        data,
        isLoading,
        error,
        mutate
    } = useSWR<Match>(`${backendUrl}/matches/${matchId}`, swrFetcher, {
        refreshInterval: (data) =>
            data?.players.every(player => player === you ||
                data?.currentGame?.moves?.some(move => move.player === player)) ? 0 : 1000
    })

    if (error) return <>
        <Alert variant="danger">The match could not be loaded.</Alert>
        <CreateMatchButton/>
    </>

    const loadingUI = isLoading && <Spinner className="float-end"/>

    if (data == null) return <>
        {loadingUI}
        <h1>Rock Paper Scissors</h1>
    </>

    const {players, playedGames, currentGame} = data
    const lastPlayers = currentGame.moves.map(move => move.player)

    const score = (player: string) => playedGames.flatMap(game => game.wins.filter(win => win.winner === player)).length

    const rankedPlayers = [...players].sort((a, b) => score(b) - score(a))
    const winner = score(rankedPlayers[1]) < score(rankedPlayers[0]) ? rankedPlayers[0] : null

    function PlayerUI({player}: { player: string }) {
        const madeMove = lastPlayers?.includes(player)
        if (you !== player) return madeMove
            ? <>Moved</>
            : <Link href={`/matches/${matchId}/players/${player}`} target="_blank">Awaiting Move</Link>
        return madeMove
            ? currentGame.moves.find(m => m.player === player)?.symbol
            : <div className="d-inline-flex gap-1">{gameSymbols.map(symbol =>
                <Button
                    key={symbol} onClick={() => {
                    makeMove(matchId, player, symbol)
                        .then(match => mutate(match))
                        .catch(err => console.log(err))

                }}>{symbol}</Button>)}
            </div>
    }

    return <>
        {loadingUI}
        <h1>Rock Paper Scissors</h1>
        <CreateMatchButton/>
        <table className="table">
            <thead>
            <tr>
                {players.map((player, playerIndex) => <th key={player} style={{width: '30%'}}>
                    {you === player ? 'You' : <>Player {playerIndex + 1}</>}: {score(player)} {player == winner ?
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
                {players.map(player => <td key={player} className="align-middle"><PlayerUI player={player}/></td>)}
                <td></td>
            </tr>
            </tbody>
        </table>

        <pre className="small mt-4 text-body-tertiary">{JSON.stringify(data, null, 4)}</pre>
    </>
}

function getMoveCSS(game: PlayedGame, player: string) {
    if (game.wins.some(win => win.winner === player)) return 'text-success'
    if (game.wins.some(win => win.loser === player)) return 'text-danger text-decoration-line-through'
    return 'text-body-tertiary'
}

async function createGame() {
    const response = await fetch(`${backendUrl}/matches`, {method: 'POST'})
    if (!response.ok) throw new Error(response.statusText)
    return await response.json() as MatchAndId
}

async function makeMove(matchId: string, player: string, symbol: GameSymbol) {
    const response = await fetch(`${backendUrl}/matches/${matchId}/moves`, {
        method: 'POST',
        body: JSON.stringify({player, symbol}),
        headers: {'Content-Type': 'application/json'}
    })
    if (!response.ok) throw new Error(response.statusText)
    return await response.json() as Match
}

async function swrFetcher(url: string) {
    const res = await fetch(url, {headers: {'Content-Type': 'application/json'}})
    if (!res.ok) throw new Error(`Error: ${res.status} ${res.statusText}`)
    return await res.json()
}
