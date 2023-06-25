package GUI;

import Logic.impl.Figur;
import Logic.impl.Spieler;
import StateMachine.port.State;

import java.util.*;
import java.util.stream.Collectors;

import static StateMachine.port.State.Value.*;

class GuiFactoryImpl implements GuiFactory {

    private static final Map<State.Value, String> prev = Map.of(
            PLAYER_TURN, "%s is am Zug",
            HAS_ROLLED, "%s hat eine %s gewürfelt",
            MOVES, "%s bewegt %s",
            REMAINING_MOVES, "%s kann noch %d Felder gehen",
            REACHED_FORK, "%s erreicht eine Gabelung",
            MOVES_BY, "%s bewegt %s um %d Felder"
    );

    private static final Map<State.Value, String> text = Map.of(
        ROLL_DICE, "Zum Würfeln drücken Sie \"x\"!",
        START_FIELD, "Wähle ein Startfeld (1 oder 2)!",
        MOVE_FORWARD_BACKWARD, "Bewege die Figur vorwärts \"v\" oder rückwärts \"r\"!",
        SELECT_FIGURE, "Wähle eine Figur zum Bewegen aus! %s",
        MOVE_LEFT_RIGHT, "Bewege die Figur links \"l\" oder rechts \"r\"!",
        MOVE_LEFT_RIGHT_MIDDLE, "Bewege die Figur links \"l\", rechts \"r\" oder mittig \"m\"!",
        PLAYER_TURN, "%s is am Zug",
        SELECT_MOVE_AMOUNT, "Wähle die Anzahl der Felder zum Bewegen aus!",
        DUEL_START, "%s beginnt ein Duell mit %s",
        END, "Letzter Zug!"
    );


    private void renderPreview(State.Value renderTyp, Spieler spieler, Figur figur, int moveValue) {
        switch(renderTyp) {
            case HAS_ROLLED -> System.out.println(String.format(
                    prev.get(renderTyp), spieler, spieler.getDiceValue()));
            case REMAINING_MOVES -> System.out.println(String.format(
                    prev.get(renderTyp), spieler, moveValue));
            case MOVES_BY -> System.out.println(String.format(
                    prev.get(renderTyp), spieler, figur, moveValue));
            case PLAYER_TURN -> System.out.println(String.format(
                    prev.get(renderTyp), spieler));
            case REACHED_FORK -> System.out.println(String.format(
                    prev.get(renderTyp), figur));
            default -> System.out.println(prev.get(renderTyp));
        }
    }

    private void renderPreview(State.Value renderTyp, Spieler spieler) {
        renderPreview(renderTyp, spieler, spieler.getFiguren().get(0), 0);
    }

    private void renderPreview(State.Value renderTyp, Spieler spieler, Figur figur) {
        renderPreview(renderTyp, spieler, figur, 0);
    }

    private void renderAction(State.Value renderTyp, Spieler spieler, Spieler gegner) {
        switch(renderTyp) {
            case PLAYER_TURN -> System.out.println(String.format(text.get(renderTyp), spieler));
            case DUEL_START -> System.out.println(String.format(text.get(renderTyp), spieler, gegner));
            case SELECT_FIGURE -> System.out.println(String.format(text.get(renderTyp),
                    spieler.getFigurenAufSpielfeld().stream().map(Object::toString).collect(Collectors.joining(", "))));
            default -> System.out.println(text.get(renderTyp));
        }
    }

    private void renderAction(State.Value renderTyp, Spieler spieler) {
        renderAction(renderTyp, spieler, spieler);
    }

    private void renderGameInfo(List<Spieler> spieler) {
        spieler.forEach(sp -> {
            System.out.print(String.format("\t%s:\t", sp));

            if(sp.getFigurenAufHeimat().size() == 5) {
                System.out.println("0 von 5 Figuren im Spiel");

            } else {
                System.out.print(String.format("%d von 5 Figuren im Spiel: ", sp.getFigurenAufSpielfeld().size()));

                sp.getFigurenAufSpielfeld().forEach(figur -> {
                    System.out.print(String.format("%s Feld %s;", figur, figur.getPosition().getID()));
                });

                System.out.println();
            }
        });
    }

    public void renderUebersicht(State.Value prevTyp, State.Value nextTyp, Spieler spieler, List<Spieler> allSpieler) {
        renderPreview(prevTyp, spieler);

        renderGameInfo(allSpieler);

        renderAction(nextTyp, spieler);
    }

    public void renderUebersicht(State.Value prevTyp, State.Value renderTyp, Spieler spieler, Figur figur, int moveValue, List<Spieler> allSpieler) {
        renderPreview(prevTyp, spieler, figur, moveValue);

        renderGameInfo(allSpieler);

        renderAction(renderTyp, spieler);
    }

    public void renderUebersicht(State.Value prevTyp, State.Value nextTyp, Spieler spieler, Figur figur, List<Spieler> allSpieler) {
        renderPreview(prevTyp, spieler, figur);

        renderGameInfo(allSpieler);

        renderAction(nextTyp, spieler);
    }

    public void renderUebersicht(State.Value prevTyp, State.Value nextTyp, Spieler spieler, Spieler gegner, List<Spieler> allSpieler) {
        renderPreview(prevTyp, spieler);

        renderGameInfo(allSpieler);

        renderAction(nextTyp, spieler, gegner);
    }

    public void renderString(String str) {
        System.out.println(str);
    }
}
