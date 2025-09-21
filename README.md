# Rock Paper Scissors

## Backend

The backend is a typical Spring Boot application, providing a simple rest API.

## Web UI

The frontend is a very basic Next.js project. To run it, go to the web-ui folder and run "npm run dev". In development the frontend expects the backend api to run at localhost:8080.

## TODO / Areas For Improvement

### Frontend Tests

Skipped for this assignment.

### Persistence

In this prototype there is no persistence, but of course it can easly be added by expanding the Repository class.

### More than 2 players

There is rudimentary support in the game logic, but the frontend as well as the Match data would need to be extended to properly show the scoring to the user.

### Polling the Match Data

For production, such a use case would require push communication from the server (for instance using websockets).

### Error Handling

In the backend the error handling is implemented with exceptions redundantly in both the controller and the business logic. This should be refactored to use return values in the business logic. Can be done via sealed interfaces or more elegantly with Rich Errors (Kotlin 2.4+)

### TODO: put id into Match

It is a little redundant, but putting the match id inside the Match class simplifies the code.

### CQRS

The code is prepared for CQRS a little bit: There are basically two commands (POST endpoints) and one query (GET endpoint). In this implementation there is no separation, but we could introduce a more basic repository which only stores the matches (id, player ids) and moves, and upon each match creation and move update the more elaborate view currently represented by the Match class.

### TODO

- refactor api so that players cannot see each other's moves
- use SWR for the two POST endpoints
- show errors for all SWR hooks

