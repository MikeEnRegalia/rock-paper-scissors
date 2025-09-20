import type {Metadata} from 'next'
import {ReactNode} from 'react'
import 'bootstrap/dist/css/bootstrap.min.css'

export const metadata: Metadata = {
    title: 'Rock Paper Scissors',
    description: 'Play Rock Paper Scissors!',
}

export default ({children}: Readonly<{ children: ReactNode }>) => <html lang="en">
<body className="p-2">{children}</body>
</html>
