import type {Metadata} from 'next'
import {ReactNode} from 'react'
import "bootstrap/dist/css/bootstrap.min.css";

export const metadata: Metadata = {
    title: 'Rock Paper Scissors',
    description: 'Play Rock Paper Scissors!',
}

export default function RootLayout({children}: Readonly<{ children: ReactNode }>) {
    return <html lang="en">
    <body className="p-2">
    {children}
    </body>
    </html>
}
