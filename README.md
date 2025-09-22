# Rock Paper Scissors

## Backend

The backend is a typical Spring Boot application, providing a simple rest API.

## Web UI

The frontend is a very basic Next.js project. To run it, go to the web-ui folder and run `npm run dev`. In development the frontend expects the backend api to run at http://localhost:8080.

## Ideas for Improvement / Known Limitations

- add persistence in the backend repo
- refactor api so that players cannot see each other's moves
- use SWR for the two POST endpoints
- show errors/loading spinner/feedback for all SWR hooks
- Use web sockets to push state to the UI
- replace redundant exception based error handling in the backend with return values (preferably: use Rich Errors, which will be available in Kotlin 2.4+)
- fully support more than two players
- add frontend tests

### CQRS

The code is prepared for CQRS a little bit: There are basically two commands (POST endpoints) and one query (GET endpoint). In this implementation there is no separation, but we could introduce a more basic repository which only stores the matches (id, player ids) and moves, and upon each match creation and move update the more elaborate view currently represented by the Match class.
