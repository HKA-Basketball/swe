package GUI;

import Logic.impl.Figur;
import Logic.impl.Spieler;
import StateMachine.port.State;

import java.util.List;

public interface GuiFactory {
    GuiFactory FACTORY = new GuiFactoryImpl();
}
