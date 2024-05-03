
### Rules of the game
Range 0 - 100
Starting 5 points
Losing -1

After every round:
- Round number
- Players in the game
- The numbers chosen
- The remaining points
- The outcome of each player
- The eliminated players

Last round: If a player selects 0, they lose

**main.Game**
- 2 - 6 players
- When one joins, notify others and show them the players
- No one can join after the game starts
- Eliminated players watch the others
- The game ends when there is one player left
- Timeout for reply from client
- If all players choose the same number, all lose

**main.Server**
- Write nickname. The ticket will be a unique ID + nickname. The counter increments. Add it to a list and check before joining a game
- Show all players on the server
- Join a game - create a game
- Top 5 ranking - total games won => sent to newly connected players

Order:
1. Leaderboard
2. All the players
3. All the games / create a new game / join

**main.Server implementation**
- main.Client.java
    - Provide server IP and port through the keyboard
- main.Game.java
    - Unique ID
    - To handle game rules
- main.Player.java (implements Runnable)
- Main menu - use boolean to check if the game started
- main.Server.java
    - Multi-threaded
    - Port 13337