package Logic;

import Logic.impl.GameManagerImpl;
import Logic.impl.Player;

import java.util.List;

public interface GameManagerFactory {
    GameManagerFactory FACTORY = new GameManagerImpl();

    List<Player> getPlayers();
    Player getCurrentPlayer();
    List<String> getStringListOfMovableFigures();

    void setInput(String input);
    String getInput();
}
