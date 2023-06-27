package Logic.port;

import Logic.impl.Figur;
import Logic.impl.GameManagerImpl;
import Logic.impl.Spieler;

import java.util.List;

public interface GameManager {
    GameManager FACTORY = new GameManagerImpl();

    List<Spieler> getSpieler();
    int getCurrentPlayer();
    List<String> getStringListOfMovableFigures();

    void setInput(String input);
}
