package Logic.impl;

import Logic.port.GameManager;

import java.util.ArrayList;
import java.util.List;

public class Spieler {

    public enum Color {
        ROT("Rot"),
        BLAU("Blau"),
        GELB("Gelb");

        private final String colorName;

        Color(String colorName) {
            this.colorName = colorName;
        }
    }

    private String name;
    private Color farbe;
    private int diceValue;
    private List<Figur> figuren = new ArrayList<>(5);
    private List<Start> startFelder = new ArrayList<>(2);

    @Override
    public String toString() {
        return farbe.name();
    }

    public Spieler(String name, Color farbe, List<Gabelung> startFeld) {
        this.name = name;
        this.farbe = farbe;

        for (int i = 1; i <= 5; i++) {
            String id = farbe.name().charAt(0) + String.valueOf(i);
            Figur figur = new Figur(id);
            figuren.add(figur);
        }

        startFelder.add((Start) startFeld.get(0));
        startFelder.add((Start) startFeld.get(1));

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
            if(figur.isHeimat()) figurenAufHeimat.add(figur);
        });

        return figurenAufHeimat;
    }

    public List<Figur> getFigurenAufSpielfeld() {
        List<Figur> figurenAufSpielfeld = new ArrayList<>(5);

        figuren.forEach(figur -> {
            if(!figur.isHeimat()) figurenAufSpielfeld.add(figur);
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
}
