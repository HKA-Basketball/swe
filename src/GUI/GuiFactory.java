package GUI;

import Logic.impl.Figur;
import Logic.impl.Spieler;
import StateMachine.port.State;

import java.util.List;

public interface GuiFactory {
    GuiFactory FACTORY = new GuiFactoryImpl();

    void renderUebersicht(State.Value prevTyp, State.Value nextTyp, Spieler spieler, List<Spieler> allSpieler);
    void renderUebersicht(State.Value prevTyp, State.Value nextTyp, Spieler spieler, Figur figur, int moveValue, List<Spieler> allSpieler);
    void renderUebersicht(State.Value prevTyp, State.Value nextTyp, Spieler spieler, Figur figur, List<Spieler> allSpieler);
    void renderString(String str);
}
