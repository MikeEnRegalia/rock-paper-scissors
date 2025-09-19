# Rock Paper Scissors

## Backend API

The backend is a run-of-the-mills Spring Boot application. You can run it in all the standard ways.

NOTE: The frontend is hard-coded to look for the backend on port 8080.

## Web UI

The frontend is a very basic Next.js project. To run it, go to the web-ui folder and run "npm run dev".

## TODO / Areas For Improvement

### Frontend Tests

Skipped for the time being

### Persistence

In this prototype there is no persistence, but of course it can be added by expanding the Repository class.

### More than 2 players

There is rudimentary support in that the logic works, but the frontend as well as the Match data would need to be extended to properly show the scoring to the user

### Play against a real human by sharing a match link

This can be done by implementing routes for match players and either polling for moves or implementing something like a websocket or other forms of server pushing content to the client.


