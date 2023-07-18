package Logic.impl;

import java.util.ArrayList;
import java.util.List;

public class Player {

    public enum Color {
        RED("Rot"),
        BLUE("Blau"),
        YELLOW("Gelb");

        private final String name;

        Color(String name) {
            this.name = name;
        }
    }

    private String name;
    private Color color;
    private int diceRolls;
    private int diceValue;
    private int moveValue;
    private int movingFigure;
    private final List<Figure> figures = new ArrayList<>(5);
    private final List<Start> startFields = new ArrayList<>(2);

    @Override
    public String toString() {
        return color.name();
    }

    public Player(String name, Color color, List<Start> startFields) {
        this.name = name;
        this.color = color;

        for (int i = 1; i <= 5; i++) {
            String id = color.name().charAt(0) + String.valueOf(i);
            Figure figure = new Figure(id);
            figures.add(figure);
        }

        this.startFields.add(startFields.get(0));
        this.startFields.add(startFields.get(1));

        // Outer cube
        // Player Rot: Start Id = 1
        // Player Gelb: Start Id = 15
        // Player Blau: Start Id = 29
        // Player Schwartz: Start Id = 43

        // Inner cube
        // Player Rot: Start Id = 1
        // Player Gelb: Start Id = 11
        // Player Blau: Start Id = 21
        // Player Schwartz: Start Id = 31


    }

    public List<Figure> getFigures() {
        return figures;
    }

    public List<Figure> getHomeFigures() {
        List<Figure> homeFigures = new ArrayList<>(5);

        figures.forEach(figure -> {
            if (figure.isHome()) homeFigures.add(figure);
        });

        return homeFigures;
    }

    public List<Figure> getPlayingFieldFigures() {
        List<Figure> playingFieldFigures = new ArrayList<>(5);

        figures.forEach(figure -> {
            if (!figure.isHome()) playingFieldFigures.add(figure);
        });

        return playingFieldFigures;
    }

    public List<Start> getStartFields() {
        return startFields;
    }

    public int getDiceValue() {
        return diceValue;
    }

    public void setDiceValue(int diceValue) {
        this.diceValue = diceValue;
    }

    public void reduceDiceValue(int value) {
        this.diceValue -= value;
    }

    public int getMovingFigure() {
        return movingFigure;
    }

    public void setMovingFigure(int movingFigure) {
        this.movingFigure = movingFigure;
    }

    public int getMoveValue() {
        return moveValue;
    }

    public void setMoveValue(int moveValue) {
        this.moveValue = moveValue;
    }

    public void reduceMoveValue(int value) {
        this.moveValue -= value;
    }

    public int getDiceRolls() {
        return diceRolls;
    }

    public void setDiceRolls(int diceRolls) {
        this.diceRolls = diceRolls;
    }
}
