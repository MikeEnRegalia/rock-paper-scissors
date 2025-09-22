'use client'

import {Alert, Button, DropdownButton, DropdownItem, Spinner} from 'react-bootstrap'
import {useRouter} from 'next/navigation'
import useSWR from 'swr'
import Link from 'next/link'

const backendUrl = process.env.NEXT_PUBLIC_BACKEND_URL

const winningGames = 3

interface Match {
    id: string
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
    const onClick = async () => {
        try {
            const match = await createGame()
            router.push(`/matches/${match.id}/players/${match.players[0]}`)
        } catch (error) {
            console.log(error)
        }
    }
    return <Button onClick={onClick} variant="link">New Match</Button>
}

function hasMoved(currentGame: OpenGame, player: string) {
    return currentGame?.moves?.some(move => move.player === player)
}

function othersHaveMoved(match: Match, you: string) {
    return match.players.every(p => p === you || hasMoved(match.currentGame, p))
}

export function Match({matchId, player: you}: { matchId: string, player: string }) {
    const {
        data,
        isLoading,
        error,
        mutate
    } = useSWR<Match>(`${backendUrl}/matches/${matchId}`, swrFetcher, {
        refreshInterval: (data => data && othersHaveMoved(data, you) ? 0 : 1000)
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

    const {players: originalPlayers, playedGames, currentGame} = data
    const lastPlayers = currentGame.moves.map(move => move.player)

    const players = [you, ...originalPlayers.filter(p => p != you)]

    const score = (player: string) => playedGames.flatMap(game => game.wins.filter(win => win.winner === player)).length

    const rankedPlayers = [...players].sort((a, b) => score(b) - score(a))

    const leader = score(rankedPlayers[1]) < score(rankedPlayers[0]) ? rankedPlayers[0] : null
    const winner = leader != null && score(leader) >= winningGames ? leader : null

    function PlayerUI({player}: { player: string }) {
        const madeMove = lastPlayers?.includes(player)
        if (you !== player) return madeMove
            ? <>Moved</>
            : <><Link href={`/matches/${matchId}/players/${player}`} target="_blank">Waiting</Link> <Spinner
                size="sm"/></>
        return madeMove
            ? currentGame.moves.find(m => m.player === player)?.symbol
            : <DropdownButton title="Move">{gameSymbols.map(symbol =>
                <DropdownItem key={symbol} onClick={async () => {
                    try {
                        await mutate(await makeMove(matchId, player, symbol))
                    } catch (error) {
                        console.log(error)
                    }
                }}>{symbol}</DropdownItem>)}
            </DropdownButton>
    }

    return <>
        {loadingUI}
        <h1>Rock Paper Scissors</h1>
        <CreateMatchButton/>
        <table className="table">
            <thead>
            <tr>
                {players.map(player =>
                    <th key={player} className="text-nowrap" style={{width: '30%'}}>
                        {you === player ? 'You' : <>Opponent</>}: {score(player)} {player == leader ?
                        <span className="small text-success">[{player === winner ? 'WINNER' : 'LEADER'}]</span> : null}
                    </th>)}
                <th></th>
            </tr>
            </thead>
            <tbody>
            {playedGames.map((game, i) => <tr key={i}>
                {players.map(player => <td key={player} className={getMoveCSS(game.wins, player)}>
                    {game.moves.find(m => m.player === player)?.symbol}
                </td>)}
                <td></td>
            </tr>)}
            <tr>

            </tr>
            {winner == null
                ? <tr>
                    {players.map(player => <td key={player} className="align-middle"><PlayerUI player={player}/></td>)}
                    <td></td>
                </tr>
                : <tr>
                    <th colSpan={players.length + 1}>
                        Game over. {you === winner ? 'You won!' : 'You lost!'}
                    </th>
                </tr>
            }
            </tbody>
        </table>
    </>
}

function getMoveCSS(wins: Win[], player: string) {
    if (wins.some(win => win.winner === player)) return 'text-success'
    if (wins.some(win => win.loser === player)) return 'text-danger text-decoration-line-through'
    return 'text-body-tertiary'
}

async function createGame() {
    const response = await fetch(`${backendUrl}/matches`, {method: 'POST'})
    if (!response.ok) throw new Error(response.statusText)
    return await response.json() as Match
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
