package Logic.impl;

import Logic.port.Field;

public class Figure {

    private String id;
    private Field position;

    private Field previousPos;
    private boolean home;

    @Override
    public String toString() {
        return id;
    }

    public Figure(String id) {
        this.id = id;
        home = true;
    }

    public Field getPosition() {
        return position;
    }

    public boolean isHome() {
        return home;
    }

    public void setPosition(Field position) {
        this.position = position;
    }

    public void setHome(boolean home) {
        this.home = home;
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

    public boolean isPreviousPos(Field previous) {
        return previousPos != null && previousPos.equals(previous);
    }
}
