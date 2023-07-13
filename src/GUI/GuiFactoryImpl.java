package GUI;

import Logic.impl.Player;
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
            ROLL_DICE_AGAIN, "%s hat eine %d gewürfelt",
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
            case ROLL_DICE ->
                    renderString(String.format(notifications.get(renderTyp), gameInfos.getPlayers().get(gameInfos.getCurrentPlayer())));
            case ROLL_DICE_AGAIN, SELECT_MOVE_AMOUNT, SELECT_FIGURE ->
                    renderString(String.format(notifications.get(renderTyp), gameInfos.getPlayers().get(gameInfos.getCurrentPlayer()), gameInfos.getPlayers().get(gameInfos.getCurrentPlayer()).getDiceValue()));
            case MOVE_FORWARD_BACKWARD ->
                    renderString(String.format(notifications.get(renderTyp), gameInfos.getPlayers().get(gameInfos.getCurrentPlayer()), gameInfos.getPlayers().get(gameInfos.getCurrentPlayer()).getFigures().get(gameInfos.getPlayers().get(gameInfos.getCurrentPlayer()).getMovingFigure()), gameInfos.getPlayers().get(gameInfos.getCurrentPlayer()).getMoveValue()));
            case FORK_REACHED_LEFT_RIGHT_MIDDLE, FORK_REACHED_LEFT_RIGHT ->
                    renderString(String.format(notifications.get(renderTyp), gameInfos.getPlayers().get(gameInfos.getCurrentPlayer())));
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
        gameInfos.getPlayers().forEach(sp -> {
            System.out.print(String.format("\t%s:\t", sp));

            if (sp.getHomeFigures().size() == 5) {
                System.out.println("0 von 5 Figuren im Spiel");

            } else {
                System.out.print(String.format("%d von 5 Figuren im Spiel: ", sp.getPlayingFieldFigures().size()));

                sp.getPlayingFieldFigures().forEach(figur -> {
                    System.out.print(String.format("%s Feld %s;", figur, figur.getPosition().getID()));
                });

                System.out.println();
            }
        });
    }

    public void renderDiceRoll(State.Value type) {
        if("x".equals(gameInfos.getInput()) || "y".equals(gameInfos.getInput())) {
            Player player = gameInfos.getPlayers().get(gameInfos.getCurrentPlayer());
            renderString(String.valueOf(player.getDiceValue()));
        }
    }

    public void renderView(State.Value type) {
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
            case ROLL_DICE, SELECT_FIGURE, SELECT_MOVE_AMOUNT,
                    MOVE_FORWARD_BACKWARD, FORK_REACHED_LEFT_RIGHT_MIDDLE,
                    FORK_REACHED_LEFT_RIGHT -> renderView(stateMachine.getState());
            case ROLL_DICE_AGAIN -> {
                renderDiceRoll(stateMachine.getState());
                renderView(stateMachine.getState());
            }
            case NEXT_PLAYER -> renderDiceRoll(stateMachine.getState());
        }
    }
}
