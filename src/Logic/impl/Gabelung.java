package Logic.impl;

import Logic.port.Field;

public class Gabelung implements Field {
    private int id;
    private Field left;
    private Field right;
    private Field previous;

    public Gabelung(int id) {
        this.id = id;
    }

    @Override
    public int getID() {
        return id;
    }

    public Field getLeft() {
        return left;
    }

    public Field getRight() {
        return right;
    }

    public Field getPrevious() {
        return previous;
    }

    public void setLeft(Field left) {
        this.left = left;
    }

    public void setRight(Field right) {
        this.right = right;
    }

    public void setPrevious(Field previous) {
        this.previous = previous;
    }
}
