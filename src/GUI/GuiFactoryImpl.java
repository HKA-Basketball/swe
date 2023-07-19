package GUI;

import Logic.impl.Figure;
import Logic.impl.Player;
import Logic.port.GameManager;
import StateMachine.impl.StateMachineImpl;
import StateMachine.port.Observer;
import StateMachine.port.State;

import java.util.*;

import static GUI.ConsoleColors.*;
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
        Player player = gameInfos.getCurrentPlayer();
        Figure figure = player.getFigures().get(player.getMovingFigure());

        System.out.print(WHITE_UNDERLINED_BRIGHT);

        switch (renderTyp){
            case ROLL_DICE, FORK_REACHED_LEFT_RIGHT_MIDDLE, FORK_REACHED_LEFT_RIGHT ->
                    renderString(String.format(notifications.get(renderTyp), player));
            case ROLL_DICE_AGAIN, SELECT_MOVE_AMOUNT, SELECT_FIGURE ->
                    renderString(String.format(notifications.get(renderTyp), player, player.getDiceValue()));
            case MOVE_FORWARD_BACKWARD ->
                    renderString(String.format(notifications.get(renderTyp), player, figure, player.getMoveValue()));
        }

        System.out.print(RESET);
    }

    private void renderAction(State.Value renderTyp) {
        System.out.print(WHITE_BRIGHT);

        switch (renderTyp){
            case ROLL_DICE, ROLL_DICE_AGAIN, START_FIELD, SELECT_MOVE_AMOUNT,
                    MOVE_FORWARD_BACKWARD, FORK_REACHED_LEFT_RIGHT, FORK_REACHED_LEFT_RIGHT_MIDDLE
                    -> renderString(actions.get(renderTyp));
            case SELECT_FIGURE -> renderString(String.format(actions.get(renderTyp), gameInfos.getStringListOfMovableFigures()));
        }

        System.out.print(RESET);
    }

    private void renderGameInfo() {
        gameInfos.getPlayers().forEach(player -> {
            switch(player.toString()) {
                case "RED" -> System.out.print(RED);
                case "BLUE" -> System.out.print(BLUE);
                case "YELLOW" -> System.out.print(YELLOW);
            }

            System.out.print(String.format("\t%s:\t", player));

            if (player.getHomeFigures().size() == 5) {
                System.out.print("0 von 5 Figuren im Spiel");

            } else {
                System.out.print(String.format("%d von 5 Figuren im Spiel: ", player.getPlayingFieldFigures().size()));

                player.getPlayingFieldFigures().forEach(figure -> {
                    System.out.print(String.format("%s Feld %s;", figure, figure.getPosition().getID()));
                });
            }

            System.out.println(RESET);
        });
    }

    public void renderDiceRoll() {
        if("x".equals(gameInfos.getInput()) || "y".equals(gameInfos.getInput())) {
            renderString(GREEN_BRIGHT + gameInfos.getCurrentPlayer().getDiceValue() + RESET);
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
            case ROLL_DICE, SELECT_MOVE_AMOUNT, MOVE_FORWARD_BACKWARD,
                FORK_REACHED_LEFT_RIGHT_MIDDLE, FORK_REACHED_LEFT_RIGHT ->
                    renderView(stateMachine.getState());
            case ROLL_DICE_AGAIN, SELECT_FIGURE -> {
                    renderDiceRoll();
                    renderView(stateMachine.getState());
            }
            case NEXT_PLAYER -> renderDiceRoll();
        }
    }
}
