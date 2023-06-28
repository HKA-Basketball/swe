package GUI;

import Logic.port.GameManager;
import StateMachine.impl.StateMachineImpl;
import StateMachine.port.Observer;
import StateMachine.port.State;

import java.util.*;

import static StateMachine.port.State.Value.*;

class GuiFactoryImpl implements GuiFactory, Observer {

    private GameManager gameInfos = GameManager.FACTORY;

    private static final Map<State.Value, String> notifications = Map.of(
            ROLL_DICE, "%s is am Zug",
            ROLL_DICE_AGAIN, "%s hat eine %s gewürfelt",
            START_FIELD, "%s hat eine %s gewürfelt",
            SELECT_FIGURE, "%s kann noch %d Felder gehen",
            SELECT_MOVE_AMOUNT, "%s kann noch %d Felder gehen",
            FORK_REACHED_LEFT_RIGHT_MIDDLE, "%s erreicht eine Gabelung",
            FORK_REACHED_LEFT_RIGHT, "%s erreicht eine Gabelung",
            MOVE_FORWARD_BACKWARD, "%s bewegt %s um %d Felder"

    );

    private static final Map<State.Value, String> actions = Map.of(
            ROLL_DICE, "Zum Würfeln drücken Sie \"x\"!",
            ROLL_DICE_AGAIN, "Zum Würfeln drücken Sie \"x\"!",
            START_FIELD, "Wähle ein Startfeld (1 oder 2)!",
            SELECT_FIGURE, "Wähle eine Figur zum Bewegen aus! %s",
            SELECT_MOVE_AMOUNT, "Wähle die Anzahl der Felder zum Bewegen aus!",
            MOVE_FORWARD_BACKWARD, "Bewege die Figur vorwärts \"v\" oder rückwärts \"r\"!",
            FORK_REACHED_LEFT_RIGHT_MIDDLE, "Bewege die Figur links \"l\", rechts \"r\" oder mittig \"m\"!",
            FORK_REACHED_LEFT_RIGHT, "Bewege die Figur links \"l\" oder rechts \"r\"!"


    );

    private void renderNotification(State.Value renderTyp) {
        switch (renderTyp){
            case ROLL_DICE, ROLL_DICE_AGAIN -> renderString(String.format(notifications.get(renderTyp), gameInfos.getSpieler().get(gameInfos.getCurrentPlayer())));
            case SELECT_MOVE_AMOUNT, SELECT_FIGURE -> renderString(String.format(notifications.get(renderTyp), gameInfos.getSpieler().get(gameInfos.getCurrentPlayer()), gameInfos.getSpieler().get(gameInfos.getCurrentPlayer()).getDiceValue()));
            case MOVE_FORWARD_BACKWARD -> renderString(String.format(notifications.get(renderTyp), gameInfos.getSpieler().get(gameInfos.getCurrentPlayer()), gameInfos.getSpieler().get(gameInfos.getCurrentPlayer()).getFiguren().get(gameInfos.getSpieler().get(gameInfos.getCurrentPlayer()).getMovingFigure()), gameInfos.getSpieler().get(gameInfos.getCurrentPlayer()).getMoveValue()));
            case FORK_REACHED_LEFT_RIGHT_MIDDLE, FORK_REACHED_LEFT_RIGHT -> renderString(String.format(notifications.get(renderTyp), gameInfos.getSpieler().get(gameInfos.getCurrentPlayer())));
        }
    }

    private void renderAction(State.Value renderTyp) {
        switch (renderTyp){
            case ROLL_DICE, ROLL_DICE_AGAIN, START_FIELD, SELECT_MOVE_AMOUNT,
                    MOVE_FORWARD_BACKWARD, FORK_REACHED_LEFT_RIGHT, FORK_REACHED_LEFT_RIGHT_MIDDLE
                    -> renderString(actions.get(renderTyp));
            case SELECT_FIGURE -> renderString(String.format(actions.get(renderTyp), gameInfos.getStringListOfMovableFigures()));
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
            case MOVE_FORWARD_BACKWARD -> renderUebersicht(MOVE_FORWARD_BACKWARD);
            case FORK_REACHED_LEFT_RIGHT_MIDDLE -> renderUebersicht(FORK_REACHED_LEFT_RIGHT_MIDDLE);
            case FORK_REACHED_LEFT_RIGHT -> renderUebersicht(FORK_REACHED_LEFT_RIGHT);
        }
    }
}
