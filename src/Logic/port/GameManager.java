package Logic.port;

import Logic.impl.GameManagerImpl;
import Logic.impl.Player;

import java.util.List;

public interface GameManager {
    GameManager FACTORY = new GameManagerImpl();

    List<Player> getPlayers();
    int getCurrentPlayer();
    List<String> getStringListOfMovableFigures();

    void setInput(String input);
}
