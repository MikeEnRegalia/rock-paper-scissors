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

