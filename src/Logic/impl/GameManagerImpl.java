package Logic.impl;

import Logic.port.Field;
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
    private State.Value nextState = NONE;
    private String input;
    private int currentPlayer = 0;
    private List<Player> players = new ArrayList<>(3);

    public GameManagerImpl() {
        createPlayer();
    }

    public List<Player> getPlayers() {
        return players;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getInput() {
        return this.input;
    }

    public int getPlayerID(Player player) {
        return this.players.indexOf(player);
    }

    public int getFigureID(Player player, Figure figure) {
        return this.players.get(getPlayerID(player)).getFigures().indexOf(figure);
    }

    public List<String> getStringListOfMovableFigures() {
        return Arrays.asList(players.get(currentPlayer).getPlayingFieldFigures()
                .stream().map(Figure::toString).collect(Collectors.joining(";")).split(";"));

    }

    public int getFigureIDByString(Player player, String figureName) {
        for (Figure figure : player.getFigures()) {
            if (figure.getId().equals(figureName)) {
                return getFigureID(player, figure);
            }
        }

        return -1;
    }

    private void createPlayer() {
        players.add(new Player("Player1", Player.Color.RED, PlayingField.getStartingFields().get(Player.Color.RED)));
        players.add(new Player("Player2", Player.Color.BLUE, PlayingField.getStartingFields().get(Player.Color.BLUE)));
        players.add(new Player("Player3", Player.Color.YELLOW, PlayingField.getStartingFields().get(Player.Color.YELLOW)));
    }

    public void nextPlayer() {
        Player player = players.get(currentPlayer);
        player.setDiceRolls(0);

        for (Figure f : player.getFigures()) {
            f.setPreviousPos(null);
        }

        currentPlayer = (currentPlayer + 1) % players.size();
        stateMachine.setState(ROLL_DICE);
    }

    /**
     * Retrieves the start status of the player's figures.
     * The start status indicates whether each start field is occupied by one of the player's figures.
     * @return a list of boolean values representing the start status.
     */
    public List<Boolean> getStartStatus() {
        Player player = players.get(currentPlayer);
        List<Boolean> startStatus = new ArrayList<>(2);
        startStatus.add(false);
        startStatus.add(false);

        for (Figure figure : player.getPlayingFieldFigures()) {

            // Check if the figure's position matches the start fields
            if (figure.getPosition().equals(player.getStartFields().get(0))) {
                // The figure's position matches the first start field
                startStatus.set(0, true);

            } else if (figure.getPosition().equals(player.getStartFields().get(1))) {
                // The figure's position matches the second start field
                startStatus.set(1, true);
            }

            // Break out of the loop if both start fields are occupied
            if (startStatus.get(0) && startStatus.get(1)) {
                break;
            }
        }

        return startStatus;
    }

    /**
     * Checks if both start fields are blocked by the player's figures.
     * @return true if both start fields are occupied, false otherwise.
     */
    public boolean isStartBlocked() {
        // Get the start status of the player's figures
        List<Boolean> startStatus = getStartStatus();

        // Check if both start fields are occupied
        return startStatus.get(0) && startStatus.get(1);
    }

    /**
     * Rolls the dice for the current player
     * and updates the game state accordingly.
     */
    private void rollDice() {
        Player player = players.get(currentPlayer);
        player.setDiceRolls(player.getDiceRolls()+1);

        // Roll the dice by generating two random numbers between 1 and 6 (inclusive)
        int sum = ThreadLocalRandom.current().nextInt(1, 7);
        sum += ThreadLocalRandom.current().nextInt(1, 7);
        player.setDiceValue(sum);

        // Allow forcing a dice roll of 7 by pressing 'y'
        if ("y".equals(input)) {
            player.setDiceValue(7);
        }

        // Update the game state
        boolean playingFieldIsEmpty = player.getPlayingFieldFigures().isEmpty();

        if (player.getDiceValue() == 7 && (playingFieldIsEmpty || !isStartBlocked())) {
            // The player has rolled a 7 and is able to place a figure on start
            stateMachine.setState(State.Value.START_FIELD);

        } else if (playingFieldIsEmpty && player.getDiceRolls() < 3) {
            // The player has no movable figure and is able to roll again
            stateMachine.setState(State.Value.ROLL_DICE_AGAIN);

        } else if (!playingFieldIsEmpty) {
            // The player has a figure to move it by the number that has been rolled
            stateMachine.setState(State.Value.SELECT_FIGURE);

        } else {
            // The player cant roll anymore or has a movable figure
            stateMachine.setState(State.Value.NEXT_PLAYER);
        }
    }

    /**
     * Places a figure on the start field for the current player, if available.
     * Updates the figure's position and home status accordingly.
     */
    private void setFigureOnStart() {
        Player player = players.get(currentPlayer);
        List<Boolean> startStatus = getStartStatus();

        // Check if at least one start field is available for the player's figure
        if (!startStatus.get(0) || !startStatus.get(1)) {
            int figureID = getFigureID(player, player.getHomeFigures().get(0));
            Figure figure = player.getFigures().get(figureID);

            // Determine the index of the start field to set the figure's position
            int index = startStatus.get(0) ? 1 : 0;
            figure.setPosition(player.getStartFields().get(index));
            figure.setHome(false);
        }

        // Update the game state
        stateMachine.setState(State.Value.NEXT_PLAYER);
    }

    public void chooseFigure() {
        Player player = players.get(currentPlayer);

        String figureName = input;
        int figureID = getFigureIDByString(player, figureName);
        player.setMovingFigure(figureID);

        if (players.get(currentPlayer).getDiceValue() > 0) {
            stateMachine.setState(State.Value.SELECT_MOVE_AMOUNT);
        } else {
            stateMachine.setState(NEXT_PLAYER);
        }
    }

    public void selectMoveAmount() {
        Player player = players.get(currentPlayer);
        Figure figure = player.getFigures().get(player.getMovingFigure());
        figure.setPreviousPos(null);

        int movingDistance = Integer.parseInt(input);
        player.setMoveValue(movingDistance);

        player.setDiceValue(player.getDiceValue() - movingDistance);

        stateMachine.setState(MOVE);
    }

    public void move() {
        Player player = players.get(currentPlayer);
        Figure figure = player.getFigures().get(player.getMovingFigure());

        if (player.getMoveValue() > 0) {
            if(figure.getPreviousPos() != null) {
                if(figure.getPosition() instanceof Path) {
                    stateMachine.setState(MOVE_DIRECTION);
                } else {
                    stateMachine.setState(FORK_REACHED_LEFT_RIGHT);
                }
            } else {
                if(figure.getPosition() instanceof Path) {
                    stateMachine.setState(MOVE_FORWARD_BACKWARD);
                } else {
                    stateMachine.setState(FORK_REACHED_LEFT_RIGHT_MIDDLE);
                }
            }
        }
        else if(player.getDiceValue() > 0) {
            nextState = SELECT_FIGURE;
            stateMachine.setState(CHECK_COLLISION);

        } else {
            nextState = NEXT_PLAYER;
            stateMachine.setState(CHECK_COLLISION);
        }
    }

    public void startMoveDirection() {
        Player player = players.get(currentPlayer);
        Figure figure = player.getFigures().get(player.getMovingFigure());
        Field currentPosition = figure.getPosition();

        if (currentPosition instanceof Path path) {
            if ("v".equals(input)) {
                figure.setPosition(path.getNext());
            } else {
                figure.setPosition(path.getPrevious());
            }
        }

        player.setMoveValue(player.getMoveValue()-1);
        figure.setPreviousPos(currentPosition);
        stateMachine.setState(MOVE);
    }

    public void startMoveFork() {
        Player player = players.get(currentPlayer);
        Figure figure = player.getFigures().get(player.getMovingFigure());
        Field currentPosition = figure.getPosition();

        if (currentPosition instanceof Fork fork) {
            if ("r".equals(input)) {
                figure.setPosition(fork.getRight());
            } else if ("l".equals(input)) {
                figure.setPosition(fork.getLeft());
            } else {
                figure.setPosition(fork.getPrevious());
            }
        }

        player.setMoveValue(player.getMoveValue()-1);
        figure.setPreviousPos(currentPosition);
        stateMachine.setState(MOVE);
    }

    public void moveDirection() {
        Player player = players.get(currentPlayer);
        Figure figure = player.getFigures().get(player.getMovingFigure());
        Field currentPosition = figure.getPosition();

        if (currentPosition instanceof Path path) {
            if (path.getPrevious().equals(figure.getPreviousPos())) {
                figure.setPosition(path.getNext());
            } else {
                figure.setPosition(path.getPrevious());
            }

        }

        player.setMoveValue(player.getMoveValue()-1);
        figure.setPreviousPos(currentPosition);
        stateMachine.setState(MOVE);
    }

    public void moveFork() {
        Player player = players.get(currentPlayer);
        Figure figure = player.getFigures().get(player.getMovingFigure());
        Field currentPosition = figure.getPosition();

        if (currentPosition instanceof Fork fork) {
            if ("r".equals(input)) {
                if (fork.getRight().equals(figure.getPreviousPos())) {
                    figure.setPosition(fork.getPrevious());
                } else {
                    figure.setPosition(fork.getRight());
                }
            } else {
                if (fork.getRight().equals(figure.getPreviousPos())) {
                    figure.setPosition(fork.getPrevious());
                } else {
                    figure.setPosition(fork.getLeft());
                }
            }
        }

        player.setMoveValue(player.getMoveValue()-1);
        figure.setPreviousPos(currentPosition);
        stateMachine.setState(MOVE);
    }

    public void checkForCollision() {
        Player player = players.get(currentPlayer);
        Figure figure = player.getFigures().get(player.getMovingFigure());

        for (Player enemyPlayer : this.players) {
            int enemyPlayerID = getPlayerID(enemyPlayer);
            if (currentPlayer == enemyPlayerID) continue;

            for (Figure enemyFigure : enemyPlayer.getPlayingFieldFigures()) {
                int enemyFigureID = getFigureID(enemyPlayer, enemyFigure);

                if (figure.getPosition().equals(enemyFigure.getPosition())) {
                    players.get(enemyPlayerID).getFigures().get(enemyFigureID).setHome(true);
                }
            }
        }

        stateMachine.setState(nextState);
    }

    @Override
    public void update(StateMachineImpl stateMachine) {
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
