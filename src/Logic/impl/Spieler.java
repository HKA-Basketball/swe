package Logic.impl;

import java.util.ArrayList;
import java.util.List;

public class Spieler {

    public enum Color {
        RED("Rot"),
        BLUE("Blau"),
        YELLOW("Gelb");

        private final String colorName;

        Color(String colorName) {
            this.colorName = colorName;
        }
    }

    private String name;
    private Color farbe;
    private int diceRolls;
    private int diceValue;
    private int moveValue;
    private int movingFigure;
    private boolean reachedFork;
    private List<Figur> figuren = new ArrayList<>(5);
    private List<Start> startFelder = new ArrayList<>(2);

    @Override
    public String toString() {
        return farbe.name();
    }

    public Spieler(String name, Color farbe, List<Start> startFeld) {
        this.name = name;
        this.farbe = farbe;

        for (int i = 1; i <= 5; i++) {
            String id = farbe.name().charAt(0) + String.valueOf(i);
            Figur figur = new Figur(id);
            figuren.add(figur);
        }

        startFelder.add(startFeld.get(0));
        startFelder.add(startFeld.get(1));

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

    public List<Figur> getFiguren() {
        return figuren;
    }

    public List<Figur> getFigurenAufHeimat() {
        List<Figur> figurenAufHeimat = new ArrayList<>(5);

        figuren.forEach(figur -> {
            if (figur.isHeimat()) figurenAufHeimat.add(figur);
        });

        return figurenAufHeimat;
    }

    public List<Figur> getFigurenAufSpielfeld() {
        List<Figur> figurenAufSpielfeld = new ArrayList<>(5);

        figuren.forEach(figur -> {
            if (!figur.isHeimat()) figurenAufSpielfeld.add(figur);
        });

        return figurenAufSpielfeld;
    }

    public List<Start> getStartFelder() {
        return startFelder;
    }

    public int getDiceValue() {
        return diceValue;
    }

    public void setDiceValue(int diceValue) {
        this.diceValue = diceValue;
    }

    public void setFarbe(Color farbe) {
        this.farbe = farbe;
    }

    public Color getFarbe() {
        return farbe;
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

    public int getDiceRolls() {
        return diceRolls;
    }

    public void setDiceRolls(int diceRolls) {
        this.diceRolls = diceRolls;
    }
}
