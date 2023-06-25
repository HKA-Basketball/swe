package Logic.port;

import Logic.impl.Figur;
import Logic.impl.GameManagerImpl;
import Logic.impl.Spieler;

public interface GameManager {
    GameManager FACTORY = new GameManagerImpl();

    void startLogic();
}
