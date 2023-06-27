package Logic.impl;

import Logic.port.Field;

public class Figur {

    private String id;
    private Field position;

    private Field previousPos;
    private boolean heimat;

    @Override
    public String toString() {
        return id;
    }

    public Figur(String id) {
        this.id = id;
        heimat = true;
    }

    public Field getPosition() {
        return position;
    }

    public boolean isHeimat() {
        return heimat;
    }

    public void setPosition(Field position) {
        this.position = position;
    }

    public void setHeimat(boolean heimat) {
        this.heimat = heimat;
    }

    public String getId() {
        return id;
    }

    public Field getPreviousPos() {
        return previousPos;
    }

    public void setPreviousPos(Field previousPos) {
        this.previousPos = previousPos;
    }
}
