package Logic.impl;

import GUI.GuiFactory;
import Logic.port.Feld;
import Logic.port.GameManager;
import StateMachine.impl.StateMachineImpl;
import StateMachine.port.Observer;
import StateMachine.port.State;
import StateMachine.StateMachineFactory;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static StateMachine.port.State.Value.*;

public class GameManagerImpl implements GameManager, Observer {


    private StateMachineFactory stateMachine = StateMachineFactory.FACTORY;
    private String input;
    private int currentPlayer = 0;
    private PlayingField playingField;
    private List<Spieler> spieler = new ArrayList<>(3);

    public List<Spieler> getSpieler() {
        return spieler;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public int getPlayerID(Spieler player) {
        return this.spieler.indexOf(player);
    }

    public int getFigureID(Spieler player, Figur figure) {
        return this.spieler.get(getPlayerID(player)).getFiguren().indexOf(figure);
    }

    public List<String> getStringListOfMovableFigures() {
        return Arrays.asList(spieler.get(currentPlayer).getFigurenAufSpielfeld()
                .stream().map(Figur::toString).collect(Collectors.joining(";")).split(";"));

    }

    public int getFigureIDByString(Spieler player, String figureName) {
        for (Figur figure : player.getFiguren()) {
            if (figure.getId().equals(figureName)) {
                return getFigureID(player, figure);
            }
        }

        return -1;
    }

    public GameManagerImpl() {
        // Init
        creatPlayer();
    }

    public void nextPlayer() {

        Spieler player = spieler.get(currentPlayer);
        player.setDiceRolls(0);

        for (Figur f : player.getFiguren()) {
            f.setPreviousPos(null);
        }

        currentPlayer++;
        if (currentPlayer >= spieler.size()) {
            currentPlayer = 0;
        }
        stateMachine.setState(ROLL_DICE);
    }

    public void startLogic() {
        // Game Logic
        playStepByStep();
    }

    private void playStepByStep() {
        rollDice();
        moveFigures();
    }

    public int wuerfeln() {
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int sum = ThreadLocalRandom.current().nextInt(1, 7);
        sum += ThreadLocalRandom.current().nextInt(1, 7);
        return sum;
    }

    private void rollDice() {
        Spieler player = spieler.get(currentPlayer);
        player.setDiceRolls(player.getDiceRolls()+1);

        player.setDiceValue(wuerfeln());
        if ("y".equals(input)) {
            player.setDiceValue(7);
        }

        System.out.println(player.getDiceValue());

        if(player.getFigurenAufSpielfeld().isEmpty() && player.getDiceRolls() <= 3) {
            if(player.getDiceValue() == 7) {
                stateMachine.setState(State.Value.START_FIELD);
            } else {
                stateMachine.setState(State.Value.ROLL_DICE_AGAIN);
            }
        } else if(!player.getFigurenAufSpielfeld().isEmpty() && player.getDiceRolls() <= 1) {
            if(player.getDiceValue() == 7 && !isStartBlocked()) {
                stateMachine.setState(State.Value.START_FIELD);
            } else {
                stateMachine.setState(State.Value.SELECT_FIGURE);
            }
        } else {
            stateMachine.setState(State.Value.NEXT_PLAYER);
        }
    }

    private void moveFigures() {
        Spieler player = spieler.get(currentPlayer);
        if (!isStartBlocked() && player.getDiceValue() == 7) {
            setFigureOnStart();

        } else if (!player.getFigurenAufSpielfeld().isEmpty() && player.getDiceValue() > 0) {
            chooseFigure();
        }


    }

    public List<Boolean> getStartStatus() {
        Spieler player = spieler.get(currentPlayer);
        List<Boolean> startStatus = new ArrayList<>(2);
        startStatus.add(false);
        startStatus.add(false);

        for (Figur figure : player.getFigurenAufSpielfeld()) {

            if (!startStatus.get(0)) {
                startStatus.set(0, figure.getPosition().equals(player.getStartFelder().get(0)));
            }

            if (!startStatus.get(1)) {
                startStatus.set(1, figure.getPosition().equals(player.getStartFelder().get(1)));
            }

            if (startStatus.get(0) && startStatus.get(1)) break;
        }

        return startStatus;
    }

    public boolean isStartBlocked() {
        List<Boolean> startStatus = getStartStatus();
        return (startStatus.get(0) && startStatus.get(1));
    }

    private void setFigureOnStart() {
        Spieler player = spieler.get(currentPlayer);
        List<Boolean> startStatus = getStartStatus();

        if (!startStatus.get(0) || !startStatus.get(1)) {
            int figureID = getFigureID(player, player.getFigurenAufHeimat().get(0));
            if (!startStatus.get(0)) {
                this.spieler.get(currentPlayer).getFiguren().get(figureID)
                        .setPosition(this.spieler.get(currentPlayer).getStartFelder().get(0));
                this.spieler.get(currentPlayer).getFiguren().get(figureID).setHeimat(false);
            } else if (!startStatus.get(1)) {
                this.spieler.get(currentPlayer).getFiguren().get(figureID)
                        .setPosition(this.spieler.get(currentPlayer).getStartFelder().get(1));
                this.spieler.get(currentPlayer).getFiguren().get(figureID).setHeimat(false);
            }
        }

        stateMachine.setState(State.Value.NEXT_PLAYER);
    }

    public void chooseFigure() {
        Spieler player = spieler.get(currentPlayer);

        /*while (spieler.get(currentPlayer).getDiceValue() > 0) {
            gui.renderUebersicht(State.Value.REMAINING_MOVES, State.Value.SELECT_FIGURE,
                    player, null, player.getDiceValue(), this.spieler);

            String figureName = getStringInput(getStringListOfMovableFigures(player));
            int figureID = getFigureIDByString(player, figureName);
            player.setMovingFigure(figureID);

            gui.renderUebersicht(State.Value.REMAINING_MOVES, State.Value.SELECT_MOVE_AMOUNT,
                    player, player.getFiguren().get(figureID), player.getDiceValue(), this.spieler);


            //moveFigure();
            //checkForCollision();
        }*/

        if (spieler.get(currentPlayer).getDiceValue() > 0) {
            stateMachine.setState(State.Value.SELECT_MOVE_AMOUNT);
        } else {
            stateMachine.setState(NEXT_PLAYER);
        }
    }

    public void selectMoveAmount() {
        Spieler player = spieler.get(currentPlayer);

        int movingDistance = Integer.parseInt(input);
        player.setMoveValue(movingDistance);

        spieler.get(currentPlayer).setDiceValue(spieler.get(currentPlayer).getDiceValue() - movingDistance);

        stateMachine.setState(MOVE);
    }

    public void move() {
        Spieler player = spieler.get(currentPlayer);
        Figur figure = player.getFiguren().get(player.getMovingFigure());

        /*for (int i = 0; i < player.getMoveValue(); i++) {
            if (i == 0) {
                startMoveDirection();
            } else {
                moveDirection();
            }
        }*/
        if (player.getMoveValue() > 0) {
            if(figure.getPreviousPos() != null) {
                if(figure.getPosition() instanceof Weg) {
                    stateMachine.setState(MOVE_DIRECTION);
                } else {
                    stateMachine.setState(FORK_REACHED_LEFT_RIGHT);
                }
            } else {
                if(figure.getPosition() instanceof Weg) {
                    stateMachine.setState(MOVE_FORWARD_BACKWARD);
                } else {
                    stateMachine.setState(FORK_REACHED_LEFT_RIGHT_MIDDLE);
                }
            }
        }
        else if(player.getDiceValue() > 0) {
            stateMachine.setState(SELECT_MOVE_AMOUNT);

        } else {
            stateMachine.setState(CHECK_COLLISION);
        }

    }

    public void startMoveDirection() {
        Spieler player = spieler.get(currentPlayer);
        Figur figure = player.getFiguren().get(player.getMovingFigure());
        Feld currentPosition = figure.getPosition();

        if (currentPosition instanceof Weg weg) {
            if ("v".equals(input)) {
                figure.setPosition(weg.getNext());
            } else {
                figure.setPosition(weg.getPrevious());
            }
        }

        player.setMoveValue(player.getMoveValue()-1);
        figure.setPreviousPos(currentPosition);
        stateMachine.setState(MOVE);
    }

    public void startMoveFork() {
        Spieler player = spieler.get(currentPlayer);
        Figur figure = player.getFiguren().get(player.getMovingFigure());
        Feld currentPosition = figure.getPosition();

        if (currentPosition instanceof Gabelung gabelung) {
            if ("r".equals(input)) {
                figure.setPosition(gabelung.getRight());
            } else if ("l".equals(input)) {
                figure.setPosition(gabelung.getLeft());
            } else {
                figure.setPosition(gabelung.getPrevious());
            }
        }

        player.setMoveValue(player.getMoveValue()-1);
        figure.setPreviousPos(currentPosition);
        stateMachine.setState(MOVE);
    }

    public void moveDirection() {
        Spieler player = spieler.get(currentPlayer);
        Figur figure = player.getFiguren().get(player.getMovingFigure());
        Feld currentPosition = figure.getPosition();

        if (currentPosition instanceof Weg weg) {
            if (weg.getPrevious().equals(figure.getPreviousPos())) {
                figure.setPosition(weg.getNext());
            } else {
                figure.setPosition(weg.getPrevious());
            }

        }

        player.setMoveValue(player.getMoveValue()-1);
        figure.setPreviousPos(currentPosition);
        stateMachine.setState(MOVE);
    }

    public void moveFork() {
        Spieler player = spieler.get(currentPlayer);
        Figur figure = player.getFiguren().get(player.getMovingFigure());
        Feld currentPosition = figure.getPosition();

        if (currentPosition instanceof Gabelung gabelung) {
            if ("r".equals(input)) {
                if (gabelung.getRight().equals(figure.getPreviousPos())) {
                    figure.setPosition(gabelung.getPrevious());
                } else {
                    figure.setPosition(gabelung.getRight());
                }
            } else {
                if (gabelung.getRight().equals(figure.getPreviousPos())) {
                    figure.setPosition(gabelung.getPrevious());
                } else {
                    figure.setPosition(gabelung.getLeft());
                }
            }
        }

        player.setMoveValue(player.getMoveValue()-1);
        figure.setPreviousPos(currentPosition);
        stateMachine.setState(MOVE);
    }

    public void checkForCollision() {
        Spieler player = spieler.get(currentPlayer);
        Figur figure = player.getFiguren().get(player.getMovingFigure());

        for (Spieler enemyPlayer : this.spieler) {
            int enemyPlayerID = getPlayerID(enemyPlayer);
            if (currentPlayer == enemyPlayerID) continue;

            for (Figur enemyFigure : enemyPlayer.getFigurenAufSpielfeld()) {
                int enemyFigureID = getFigureID(enemyPlayer, enemyFigure);

                if (figure.getPosition().equals(enemyFigure.getPosition())) {
                    spieler.get(enemyPlayerID).getFiguren().get(enemyFigureID).setHeimat(true);
                }
            }
        }

        stateMachine.setState(NEXT_PLAYER);
    }

    private void creatPlayer() {
        spieler.add(new Spieler("Player1", Spieler.Color.RED, playingField.getStartingFields().get(Spieler.Color.RED)));
        spieler.add(new Spieler("Player2", Spieler.Color.BLUE, playingField.getStartingFields().get(Spieler.Color.BLUE)));
        spieler.add(new Spieler("Player3", Spieler.Color.YELLOW, playingField.getStartingFields().get(Spieler.Color.YELLOW)));
    }

    @Override
    public void update(StateMachineImpl stateMachine) {
        System.out.println(stateMachine.getState());

        switch (stateMachine.getState()){
            case ROLL_DICE, ROLL_DICE_AGAIN -> rollDice();
            case START_FIELD -> setFigureOnStart();
            case SELECT_FIGURE -> chooseFigure();
            case SELECT_MOVE_AMOUNT -> selectMoveAmount();
            case MOVE -> move();
            case MOVE_DIRECTION -> moveDirection();
            case MOVE_FORWARD_BACKWARD -> startMoveDirection();
            case FORK_REACHED_LEFT_RIGHT_MIDDLE -> startMoveFork();
            case FORK_REACHED_LEFT_RIGHT -> moveFork();
            case CHECK_COLLISION -> checkForCollision();
            case NEXT_PLAYER -> nextPlayer();
        }
    }
}
