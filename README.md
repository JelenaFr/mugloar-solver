# Mugloar Solver - Automatic bot for the game "Dragons of Mugloar"

## Description
This project implements a bot that automatically completes quests, analyzes risks, buys items in the store and manages a strategy to achieve the maximum score.
The game continues until you reach 1000 points or until you have life.

## Technologies
- Java 17+
- Spring Boot
- RestTemplate
- SLF4J (logging)
- Gradle

## Install and run

## Clone the Repository

1. Open a terminal and navigate to your desired directory.
2. Clone the repository:
   ```sh
   git clone https://github.com/JelenaFr/mugloar-solver.git
   ```
3. Navigate into the project folder:
   ```sh
   cd mugloar-solver
   ```

---

## Build and Run the Project

### Running with Gradle

For Windows:

```sh
gradlew.bat bootRun
```

For Linux/macOS:

```sh
./gradlew bootRun
```

### Build the Project

To build the project manually, run:

```sh
./gradlew build
```

### Running the JAR File

After building, you can run the application with:

```sh
java -jar build/libs/MugloarSolver-0.0.1-SNAPSHOT.jar
```

The server will start on port **8080**.

---

## Start a New Game

To start a game, send an HTTP POST request to the server.

### Using PowerShell

```sh
curl -Method Post http://localhost:8080/game/start
```

### Using Terminal (Linux/macOS)

```sh
curl -X POST http://localhost:8080/game/start
```

### Using Postman

1. Open **Postman**.
2. Select **POST**.
3. Enter the URL:
   ```
   http://localhost:8080/game/start
   ```
4. Click **Send**.

Once the request is completed, the server will return a JSON response with the game details.

---

## Additional Information

- Ensure that **Java 17+** is installed.
- If the default **8080** port is in use, you can change it in `application.properties`.
- Logs will be available in the terminal for debugging.

Happy gaming! 


## APIs that the bot uses

- **Start the game:**
`POST https://dragonsofmugloar.com/api/v2/game/start`

- **Get quests:**
`GET https://dragonsofmugloar.com/api/v2/{gameId}/messages`

- **Solve quests:**
`POST https://dragonsofmugloar.com/api/v2/{gameId}/solve/{adId}`

- **Get reputation:**
`POST https://dragonsofmugloar.com/api/v2/{gameId}/investigate/reputation`

- **Get items from the store:**
`GET https://dragonsofmugloar.com/api/v2/{gameId}/shop`

- **Purchase items:**
`POST https://dragonsofmugloar.com/api/v2/{gameId}/shop/buy/{itemId}`





