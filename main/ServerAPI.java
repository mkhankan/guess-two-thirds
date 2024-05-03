package main;

public interface ServerAPI {
    /*
    Command	Arguments	Actions
    pseudo	pseudonym	generate new ticket and associate it with pseudonym
    ticket	ticket	validate received ticket and, if valid, welcome player with pseudonym
    join	game	add player to game if it exists, otherwise create a new game
    ready	game	confirm player readiness for game
    guess	game
    number	receive player guess for current round of game
     */
    void pseudo(String pseudonym);
    boolean ticket(String ticket);
    boolean join(String gameName);
    boolean ready(Game game);
    boolean guess(Game game, int number);
}
