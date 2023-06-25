package Logic.impl;

import Logic.port.Feld;

public class Figur {

    private String id;
    private Feld position;
    private boolean heimat;

    @Override
    public String toString() {
        return id;
    }

    public Figur(String id) {
        this.id = id;
        heimat = true;
    }

    public Feld getPosition() {
        return position;
    }

    public boolean isHeimat() {
        return heimat;
    }

    public void setPosition(Feld position) {
        this.position = position;
    }

    public void setHeimat(boolean heimat) {
        this.heimat = heimat;
    }

    public String getId() {
        return id;
    }
}
