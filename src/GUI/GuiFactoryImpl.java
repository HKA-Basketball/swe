package GUI;

import Logic.impl.Figur;
import Logic.impl.Spieler;
import Logic.port.GameManager;
import StateMachine.impl.StateMachineImpl;
import StateMachine.port.Observer;
import StateMachine.port.State;

import java.util.*;
import java.util.stream.Collectors;

import static StateMachine.port.State.Value.*;

class GuiFactoryImpl implements GuiFactory, Observer {

    private GameManager gameInfos = GameManager.FACTORY;

    private static final Map<State.Value, String> notifications = Map.of(
            PLAYER_TURN, "%s is am Zug",
            HAS_ROLLED, "%s hat eine %s gewürfelt",
            MOVES, "%s bewegt %s",
            REMAINING_MOVES, "%s kann noch %d Felder gehen",
            FORK_REACHED, "%s erreicht eine Gabelung",
            MOVES_BY, "%s bewegt %s um %d Felder"
    );

    private static final Map<State.Value, String> actions = Map.of(
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

    private static final Map<State.Value, String> notifications2 = Map.of(
            ROLL_DICE, "%s is am Zug",
            ROLL_DICE_AGAIN, "%s hat eine %s gewürfelt",
            START_FIELD, "%s hat eine %s gewürfelt",
            SELECT_FIGURE, "%s kann noch %d Felder gehen",
            SELECT_MOVE_AMOUNT, "%s kann noch %d Felder gehen",
            CHOOSE_DIRECTION, "%s bewegt %s um %d Felder"

    );

    private static final Map<State.Value, String> actions2 = Map.of(
            ROLL_DICE, "Zum Würfeln drücken Sie \"x\"!",
            ROLL_DICE_AGAIN, "Zum Würfeln drücken Sie \"x\"!",
            START_FIELD, "Wähle ein Startfeld (1 oder 2)!",
            SELECT_FIGURE, "Wähle eine Figur zum Bewegen aus! %s",
            SELECT_MOVE_AMOUNT, "Wähle die Anzahl der Felder zum Bewegen aus!",
            CHOOSE_DIRECTION, "Bewege die Figur vorwärts \"v\" oder rückwärts \"r\"!",
            FORK_REACHED_LEFT_RIGHT_MIDDLE, "Bewege die Figur links \"l\", rechts \"r\" oder mittig \"m\"!",
            FORK_REACHED_LEFT_RIGHT, "Bewege die Figur links \"l\" oder rechts \"r\"!"


    );


    private void renderNotification(State.Value renderTyp) {
        switch (renderTyp){
            case ROLL_DICE, ROLL_DICE_AGAIN, SELECT_FIGURE -> renderString(notifications2.get(renderTyp));
            case SELECT_MOVE_AMOUNT -> renderString(String.format(notifications2.get(renderTyp), gameInfos.getSpieler().get(gameInfos.getCurrentPlayer()), gameInfos.getSpieler().get(gameInfos.getCurrentPlayer()).getMoveValue()));
            case MOVE_FORWARD_BACKWARD -> renderString(notifications2.get(renderTyp));
            case FORK_REACHED_LEFT_RIGHT_MIDDLE -> renderString(notifications2.get(renderTyp));
            case FORK_REACHED_LEFT_RIGHT -> renderString(notifications2.get(renderTyp));
        }
    }

    private void renderAction(State.Value renderTyp) {
        switch (renderTyp){
            case ROLL_DICE, ROLL_DICE_AGAIN, START_FIELD, SELECT_MOVE_AMOUNT, CHOOSE_DIRECTION, MOVE_DIRECTION,
                    MOVE_FORWARD_BACKWARD, FORK_REACHED_LEFT_RIGHT, FORK_REACHED_LEFT_RIGHT_MIDDLE
                    -> renderString(actions2.get(renderTyp));
            case SELECT_FIGURE -> renderString(String.format(actions2.get(renderTyp), gameInfos.getStringListOfMovableFigures()));
        }
    }

    private void renderGameInfo() {
        gameInfos.getSpieler().forEach(sp -> {
            System.out.print(String.format("\t%s:\t", sp));

            if (sp.getFigurenAufHeimat().size() == 5) {
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

    public void renderUebersicht(State.Value type) {
        renderNotification(type);

        renderGameInfo();

        renderAction(type);
    }

    public void renderString(String str) {
        System.out.println(str);
    }

    @Override
    public void update(StateMachineImpl stateMachine) {

        switch (stateMachine.getState()){
            case ROLL_DICE, ROLL_DICE_AGAIN -> renderUebersicht(ROLL_DICE);
            case SELECT_FIGURE -> renderUebersicht(SELECT_FIGURE);
            case SELECT_MOVE_AMOUNT -> renderUebersicht(SELECT_MOVE_AMOUNT);
            case MOVE_DIRECTION -> renderUebersicht(MOVE_DIRECTION);
            case MOVE_FORWARD_BACKWARD -> renderUebersicht(MOVE_FORWARD_BACKWARD);
            case FORK_REACHED_LEFT_RIGHT_MIDDLE -> renderUebersicht(FORK_REACHED_LEFT_RIGHT_MIDDLE);
            case FORK_REACHED_LEFT_RIGHT -> renderUebersicht(FORK_REACHED_LEFT_RIGHT);
        }

    }
}
