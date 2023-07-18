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


    private final StateMachineFactory stateMachine = StateMachineFactory.FACTORY;
    private String input;
    private final List<Player> players = new ArrayList<>(3);
    private Player currentPlayer;

    public GameManagerImpl() {
        createPlayers();
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getInput() {
        return this.input;
    }

    public List<String> getStringListOfMovableFigures() {
        return Arrays.asList(currentPlayer.getPlayingFieldFigures().stream().map(Figure::toString)
                .collect(Collectors.joining(";")).split(";"));
    }

    public int getFigureIDByString(Player player, String figureName) {
        for (int i = 0; i < player.getFigures().size(); i++) {
            Figure figure = player.getFigures().get(i);
            if (figure.getId().equals(figureName)) {
                return i;
            }
        }

        return -1;
    }

    private void createPlayers() {
        players.add(new Player("Player1", Player.Color.RED, PlayingField.getStartingFields().get(Player.Color.RED)));
        players.add(new Player("Player2", Player.Color.BLUE, PlayingField.getStartingFields().get(Player.Color.BLUE)));
        players.add(new Player("Player3", Player.Color.YELLOW, PlayingField.getStartingFields().get(Player.Color.YELLOW)));
        currentPlayer = players.get(0);
    }

    /**
     * Advances the game to the next player.
     * Resets the dice rolls for the current player.
     * Clears the previous positions of all figures belonging to the current player.
     * Updates the currentPlayer index to point to the next player in the list, wrapping around if necessary.
     * Updates the state to ROLL_DICE.
     */
    public void nextPlayer() {
        currentPlayer.setDiceRolls(0);

        // Clear the previous positions of all figures belonging to the current player
        for (Figure figure : currentPlayer.getFigures()) {
            figure.setPreviousPos(null);
        }

        // Move to the next player, wrapping around if necessary
        int currentPlayerID = players.indexOf(currentPlayer);
        currentPlayerID = (currentPlayerID + 1) % players.size();
        currentPlayer = players.get(currentPlayerID);

        // Update the game state
        stateMachine.setState(ROLL_DICE);
    }

    /**
     * Retrieves the start status of the player's figures.
     * The start status indicates whether each start field is occupied by one of the player's figures.
     * @return a list of boolean values representing the start status.
     */
    public List<Boolean> getStartStatus() {
        List<Boolean> startStatus = new ArrayList<>(2);
        startStatus.add(false);
        startStatus.add(false);

        for (Figure figure : currentPlayer.getPlayingFieldFigures()) {

            // Check if the figure's position matches the start fields
            if (figure.getPosition().equals(currentPlayer.getStartFields().get(0))) {
                // The figure's position matches the first start field
                startStatus.set(0, true);

            } else if (figure.getPosition().equals(currentPlayer.getStartFields().get(1))) {
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
        currentPlayer.setDiceRolls(currentPlayer.getDiceRolls()+1);

        // Roll the dice by generating two random numbers between 1 and 6 (inclusive)
        int sum = ThreadLocalRandom.current().nextInt(1, 7);
        sum += ThreadLocalRandom.current().nextInt(1, 7);
        currentPlayer.setDiceValue(sum);

        // Allow forcing a dice roll of 7 by pressing 'y'
        if ("y".equals(input)) {
            currentPlayer.setDiceValue(7);
        }

        // Update the game state
        boolean playingFieldIsEmpty = currentPlayer.getPlayingFieldFigures().isEmpty();

        if (currentPlayer.getDiceValue() == 7 && (playingFieldIsEmpty || !isStartBlocked())) {
            // The player has rolled a 7 and is able to place a figure on start
            stateMachine.setState(State.Value.START_FIELD);

        } else if (playingFieldIsEmpty && currentPlayer.getDiceRolls() < 3) {
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
        List<Boolean> startStatus = getStartStatus();

        // Check if at least one start field is available for the player's figure
        if (!startStatus.get(0) || !startStatus.get(1)) {
            Figure figure = currentPlayer.getHomeFigures().get(0);

            // Determine the index of the start field to set the figure's position
            int index = startStatus.get(0) ? 1 : 0;
            figure.setPosition(currentPlayer.getStartFields().get(index));
            figure.setHome(false);
        }

        // Update the game state
        stateMachine.setState(State.Value.NEXT_PLAYER);
    }

    /**
     * Chooses a figure for the current player based on the user's input
     * and sets the chosen figure as the moving figure for the player.
     * Determines the next state based on the player's dice value.
     */
    public void chooseFigure() {
        // Set the moving figure for the player based on the user's input
        currentPlayer.setMovingFigure(getFigureIDByString(currentPlayer, input));

        // Check the player's dice value to determine the next state
        if (currentPlayer.getDiceValue() > 0) {
            stateMachine.setState(State.Value.SELECT_MOVE_AMOUNT);
        } else {
            stateMachine.setState(State.Value.NEXT_PLAYER);
        }
    }

    /**
     * Selects the move amount for the current player's figure.
     * Sets the move value for the current player's figure based on the user's input.
     * Reduces the player's dice value by the selected move amount.
     * Sets the state to MOVE.
     */
    public void selectMoveAmount() {
        Figure figure = currentPlayer.getFigures().get(currentPlayer.getMovingFigure());

        // Set the previous position of the figure to null
        figure.setPreviousPos(null);

        // Parse the user's input as the moving distance
        int movingDistance = Integer.parseInt(input);
        currentPlayer.setMoveValue(movingDistance);
        currentPlayer.reduceDiceValue(movingDistance);

        // Update the game state
        stateMachine.setState(MOVE);
    }

    /**
     * Moves the current player's figure based on the current state and position.
     * Determines the appropriate next state for the figure's movement.
     * Updates the state machine accordingly.
     */
    public void move() {
        Figure figure = currentPlayer.getFigures().get(currentPlayer.getMovingFigure());

        // Check if the previous position of the figure is null
        boolean isPreviousPositionNull = figure.getPreviousPos() == null;
        // Check if the current position of the figure is a Path (no fork)
        boolean isPositionPath = figure.getPosition() instanceof Path;

        // Check if the player's move value is greater than 0
        if (currentPlayer.getMoveValue() > 0) {
            if (isPreviousPositionNull) {
                // If the previous position is null, set the state based on the type of current position
                stateMachine.setState(isPositionPath ? MOVE_FORWARD_BACKWARD : FORK_REACHED_LEFT_RIGHT_MIDDLE);
            } else {
                // If the previous position is not null, set the state based on the type of current position
                stateMachine.setState(isPositionPath ? MOVE_DIRECTION : FORK_REACHED_LEFT_RIGHT);
            }
        } else {
            stateMachine.setState(CHECK_COLLISION);
        }
    }

    /**
     * Starts the move direction for the current player's figure on a Path.
     * Reduces the player's move value by 1.
     * Updates the previous position of the figure.
     * Sets the state to MOVE.
     */
    public void startMoveDirection() {
        Figure figure = currentPlayer.getFigures().get(currentPlayer.getMovingFigure());
        Field currentPosition = figure.getPosition();

        // Check if the current position is a path without a fork
        if (currentPosition instanceof Path path) {
            // If the position is a Path, update the position based on the user's input ('v' or 'r')
            figure.setPosition("v".equals(input) ? path.getNext() : path.getPrevious());
        }

        currentPlayer.reduceMoveValue(1);
        figure.setPreviousPos(currentPosition);
        stateMachine.setState(MOVE);
    }

    public void startMoveFork() {
        Figure figure = currentPlayer.getFigures().get(currentPlayer.getMovingFigure());
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

        currentPlayer.reduceMoveValue(1);
        figure.setPreviousPos(currentPosition);
        stateMachine.setState(MOVE);
    }

    public void moveDirection() {
        Figure figure = currentPlayer.getFigures().get(currentPlayer.getMovingFigure());
        Field currentPosition = figure.getPosition();

        if (currentPosition instanceof Path path) {
            figure.setPosition(figure.isPreviousPos(path.getPrevious()) ? path.getNext() : path.getPrevious());
        }

        currentPlayer.reduceMoveValue(1);
        figure.setPreviousPos(currentPosition);
        stateMachine.setState(MOVE);
    }

    public void moveFork() {
        Figure figure = currentPlayer.getFigures().get(currentPlayer.getMovingFigure());
        Field currentPosition = figure.getPosition();

        if (currentPosition instanceof Fork fork) {
            Field left = fork.getLeft();
            Field right = fork.getRight();

            if (figure.isPreviousPos(fork.getLeft())) {
                left = fork.getRight();
                right = fork.getPrevious();

            } else if(figure.isPreviousPos(fork.getRight())) {
                left = fork.getPrevious();
                right = fork.getLeft();
            }

            figure.setPosition("l".equals(input) ? left : right);
        }

        currentPlayer.reduceMoveValue(1);
        figure.setPreviousPos(currentPosition);
        stateMachine.setState(MOVE);
    }

    public void checkForCollision() {
        Figure figure = currentPlayer.getFigures().get(currentPlayer.getMovingFigure());
        Field currentPosition = figure.getPosition();

        for (Player enemyPlayer : this.players) {
            if (currentPlayer == enemyPlayer) continue;

            for (Figure enemyFigure : enemyPlayer.getPlayingFieldFigures()) {
                if (currentPosition.equals(enemyFigure.getPosition())) {
                    enemyFigure.setHome(true);
                }
            }
        }

        if (currentPlayer.getDiceValue() > 0) {
            stateMachine.setState(SELECT_FIGURE);
        } else {
            stateMachine.setState(NEXT_PLAYER);
        }
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
