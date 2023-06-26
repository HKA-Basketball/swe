package Logic.impl;

import GUI.GuiFactory;
import Logic.port.Feld;
import Logic.port.GameManager;
import StateMachine.port.State;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class GameManagerImpl implements GameManager {

    GuiFactory gui = GuiFactory.FACTORY;
    private static final Scanner scanner = new Scanner(System.in);
    private PlayingField playingField;
    private List<Spieler> spieler = new ArrayList<>(3);

    public int getPlayerID(Spieler player) {
        return this.spieler.indexOf(player);
    }

    public int getFigureID(Spieler player, Figur figure) {
        return this.spieler.get(getPlayerID(player)).getFiguren().indexOf(figure);
    }

    public List<String> getStringListOfMovableFigures(Spieler player) {
        return Arrays.asList(spieler.get(getPlayerID(player)).getFigurenAufSpielfeld()
                .stream().map(Figur::toString).collect(Collectors.joining(";")).split(";"));

    }

    public int getFigureIDByString(Spieler player, String figureName) {
        for (Figur figure: player.getFiguren()) {
            if(figure.getId().equals(figureName)) {
                return getFigureID(player, figure);
            }
        }

        return -1;
    }

    public GameManagerImpl() {
        // Init
        creatPlayer();
    }

    public void startLogic() {
        // Game Logic
        while (true) {
            playStepByStep(spieler.get(0));
            playStepByStep(spieler.get(1));
            playStepByStep(spieler.get(2));
        }
    }

    private void playStepByStep(Spieler player) {
        rollDice(player);
        moveFigures(player);
    }

    public int wuerfeln() {
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int sum = ThreadLocalRandom.current().nextInt(1, 7);
        sum += ThreadLocalRandom.current().nextInt(1, 7);
        return sum;
    }

    private void rollDice(Spieler player) {
        int playerID = getPlayerID(player);
        this.spieler.get(playerID).setDiceValue(0);

        gui.renderUebersicht(State.Value.PLAYER_TURN, State.Value.ROLL_DICE, player, this.spieler);

        if(player.getFigurenAufSpielfeld().isEmpty()) {
            for (int i = 0; i < 3; i++) {
                int diceValue = getStringInput("xy").equals("y") ? 7 : wuerfeln();
                this.spieler.get(playerID).setDiceValue(diceValue);

                if(diceValue == 7) break;

                gui.renderUebersicht(State.Value.HAS_ROLLED, State.Value.ROLL_DICE, player, this.spieler);
            }
        } else {
            int diceValue = getStringInput("xy").equals("y") ? 7 : wuerfeln();
            this.spieler.get(playerID).setDiceValue(diceValue);
        }
    }

    private void moveFigures(Spieler player) {
        if (!isStartBlocked(player) && player.getDiceValue() == 7) {
            gui.renderUebersicht(State.Value.HAS_ROLLED, State.Value.START_FIELD, player, this.spieler);
            setFigureOnStart(player);

        } else if(!player.getFigurenAufSpielfeld().isEmpty() && player.getDiceValue() > 0) {
            chooseFigure(player);
        }
    }

    public List<Boolean> getStartStatus(Spieler player) {
        List<Boolean> startStatus = new ArrayList<>(2);
        startStatus.add(false);
        startStatus.add(false);

        for (Figur figure: player.getFigurenAufSpielfeld()) {

            if(!startStatus.get(0)) {
                startStatus.set(0, figure.getPosition().equals(player.getStartFelder().get(0)));
            }

            if(!startStatus.get(1)) {
                startStatus.set(1, figure.getPosition().equals(player.getStartFelder().get(1)));
            }

            if (startStatus.get(0) && startStatus.get(1)) break;
        }

        return startStatus;
    }

    public boolean isStartBlocked(Spieler player) {
        List<Boolean> startStatus = getStartStatus(player);
        return (startStatus.get(0) && startStatus.get(1));
    }

    private void setFigureOnStart(Spieler player) {
        int playerID = getPlayerID(player);
        List<Boolean> startStatus = getStartStatus(player);

        if(!startStatus.get(0) || !startStatus.get(1)) {
            int figureID = getFigureID(player, player.getFigurenAufHeimat().get(0));
            if(!startStatus.get(0)) {
                this.spieler.get(playerID).getFiguren().get(figureID)
                        .setPosition(this.spieler.get(playerID).getStartFelder().get(0));
                this.spieler.get(playerID).getFiguren().get(figureID).setHeimat(false);
            } else if(!startStatus.get(1)) {
                this.spieler.get(playerID).getFiguren().get(figureID)
                        .setPosition(this.spieler.get(playerID).getStartFelder().get(1));
                this.spieler.get(playerID).getFiguren().get(figureID).setHeimat(false);
            }
        }
    }

    public void chooseFigure(Spieler player) {
        int playerID = getPlayerID(player);

        while(spieler.get(playerID).getDiceValue() > 0) {
            gui.renderUebersicht(State.Value.REMAINING_MOVES, State.Value.SELECT_FIGURE,
                    player, null, player.getDiceValue(), this.spieler);

            String figureName = getStringInput(getStringListOfMovableFigures(player));
            int figureID = getFigureIDByString(player, figureName);

            gui.renderUebersicht(State.Value.REMAINING_MOVES, State.Value.SELECT_MOVE_AMOUNT,
                    player, player.getFiguren().get(figureID), player.getDiceValue(), this.spieler);

            int movingDistance = getIntInput(player.getDiceValue());
            moveFigure(player, player.getFiguren().get(figureID), movingDistance);
            spieler.get(playerID).setDiceValue(spieler.get(playerID).getDiceValue() - movingDistance);
            checkForCollision(player, player.getFiguren().get(figureID));
        }
    }

    public void moveFigure(Spieler player, Figur figure, int movingDistance) {
        int playerID = getPlayerID(player);
        int figureID = getFigureID(player, figure);
        Feld previousPosition = spieler.get(playerID).getFiguren().get(figureID).getPosition();

        for(int i = 0; i < movingDistance; i++) {
            if(i == 0) {
                previousPosition = startMoveDirection(player, figure, movingDistance);
            } else {
                previousPosition = moveDirection(player, figure, previousPosition);
            }
        }
    }

    public Feld startMoveDirection(Spieler player, Figur figure, int movingDistance) {
        int playerID = getPlayerID(player);
        int figureID = getFigureID(player, figure);
        Feld currentPosition = spieler.get(playerID).getFiguren().get(figureID).getPosition();

        if(currentPosition instanceof Gabelung gabelung) {
            gui.renderUebersicht(State.Value.REACHED_FORK, State.Value.MOVE_LEFT_RIGHT_MIDDLE, player, figure, this.spieler);
            String direction = getStringInput("lrm");

            if("r".equals(direction)) {
                spieler.get(playerID).getFiguren().get(figureID).setPosition(gabelung.getRight());
            } else if("l".equals(direction)) {
                spieler.get(playerID).getFiguren().get(figureID).setPosition(gabelung.getLeft());
            } else {
                spieler.get(playerID).getFiguren().get(figureID).setPosition(gabelung.getPrevious());
            }

        } else if(currentPosition instanceof Weg weg) {
            gui.renderUebersicht(State.Value.MOVES_BY, State.Value.MOVE_FORWARD_BACKWARD, player, figure, movingDistance, this.spieler);
            String direction = getStringInput("vr");

            if("v".equals(direction)) {
                spieler.get(playerID).getFiguren().get(figureID).setPosition(weg.getNext());
            } else {
                spieler.get(playerID).getFiguren().get(figureID).setPosition(weg.getPrevious());
            }
        }

        return currentPosition;
    }

    public Feld moveDirection(Spieler player, Figur figure, Feld previousPostion) {
        int playerID = getPlayerID(player);
        int figureID = getFigureID(player, figure);
        Feld currentPosition = spieler.get(playerID).getFiguren().get(figureID).getPosition();

        if(currentPosition instanceof Weg weg) {
            if (weg.getPrevious().equals(previousPostion)) {
                spieler.get(playerID).getFiguren().get(figureID).setPosition(weg.getNext());
            } else {
                spieler.get(playerID).getFiguren().get(figureID).setPosition(weg.getPrevious());
            }

        } else if(currentPosition instanceof Gabelung gabelung) {
            gui.renderUebersicht(State.Value.REACHED_FORK, State.Value.MOVE_LEFT_RIGHT, player, figure, this.spieler);
            String direction = getStringInput("lr");

            if ("r".equals(direction)) {
                if (gabelung.getRight().equals(previousPostion)) {
                    spieler.get(playerID).getFiguren().get(figureID).setPosition(gabelung.getPrevious());
                } else {
                    spieler.get(playerID).getFiguren().get(figureID).setPosition(gabelung.getRight());
                }
            } else {
                if (gabelung.getRight().equals(previousPostion)) {
                    spieler.get(playerID).getFiguren().get(figureID).setPosition(gabelung.getPrevious());
                } else {
                    spieler.get(playerID).getFiguren().get(figureID).setPosition(gabelung.getLeft());
                }
            }
        }

        return currentPosition;
    }

    public void checkForCollision(Spieler player, Figur figure) {
        int playerID = getPlayerID(player);
        int figureID = getFigureID(player, figure);

        for(Spieler enemyPlayer: this.spieler) {
            int enemyPlayerID = getPlayerID(enemyPlayer);
            if(playerID == enemyPlayerID) continue;

            for(Figur enemyFigure: enemyPlayer.getFigurenAufSpielfeld()) {
                int enemyFigureID = getFigureID(enemyPlayer, enemyFigure);

                if(figure.getPosition().equals(enemyFigure.getPosition())) {
                    spieler.get(enemyPlayerID).getFiguren().get(enemyFigureID).setHeimat(true);
                }
            }
        }
    }

    private void creatPlayer() {

        spieler.add(new Spieler("Player1", Spieler.Color.RED, playingField.getStartingFields().get(Spieler.Color.RED)));
        spieler.add(new Spieler("Player2", Spieler.Color.BLUE, playingField.getStartingFields().get(Spieler.Color.BLUE)));
        spieler.add(new Spieler("Player3", Spieler.Color.YELLOW, playingField.getStartingFields().get(Spieler.Color.YELLOW)));
    }

    private String getStringInput(List<String> valid) {

        var input = "d1650de6-55eb-45d0-a015-c6d387865ca8";
        while(!valid.contains(input)) {
            input = scanner.nextLine();
            System.out.println(input);
        }

        return input;
    }

    private String getStringInput(String valid) {
        List<String> list = new ArrayList<>(valid.length());
        for (int i = 0; i < valid.length(); i++) {
            list.add(String.valueOf(valid.charAt(i)));
        }
        return getStringInput(list);
    }

    private int getIntInput(int max) {

        var input = 0;
        do {
            try {
                input = scanner.nextInt();
            } catch (InputMismatchException e) {}
            System.out.println(input);
        }while(input < 0 || input > max);

        return input;
    }
}
