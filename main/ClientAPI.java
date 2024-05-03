package main;

import java.util.List;

public interface ClientAPI {
    /*
    Command	Arguments	Actions
    ident		send ticket or pseudonym
    ticket	ticket	store ticket for reuse
    menu	players
    games	list of connected players and list of available games
    list	game
    players	list of players in game
    notify	game
    player	player joined game
    start	game	game started
    round	game
    number players guesses points results [eliminated]	round ended with corresponding information
    end	game player	game ended with player winning
    info	message	information message
    error	message	error message
     */
    void ident();
    void ticket(String ticket);
    void menu(List<Player> players, List<Game> games);
    void list(Game game, List<Player> players);
    void notify(Game game, Player player);
    void start(Game game);
    void round(Game game, int number, List<Player> players, List<Integer> guesses, List<Integer> points, List<Boolean> results, List<Player> eliminated);
    void end(Game game, Player player);
    void info(String message);
    void error(String message);


}
