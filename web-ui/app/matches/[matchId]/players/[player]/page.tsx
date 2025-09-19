import {Match} from '@/components/match'

export default async function PlayerPage({params}: { params: Promise<{ matchId: string, player: string }> }) {
    const {matchId, player} = await params
    return <>
        <Match matchId={matchId} player={player}/>
    </>
}
