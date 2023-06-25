package Logic.impl;

import Logic.port.Feld;

public class Gabelung implements Feld {
    private int id;
    private Feld left;
    private Feld right;
    private Feld previous;

    public Gabelung(int id) {
        this.id = id;
    }

    @Override
    public int getID() {
        return id;
    }

    public Feld getLeft() {
        return left;
    }

    public Feld getRight() {
        return right;
    }

    public Feld getPrevious() {
        return previous;
    }

    public void setLeft(Feld left) {
        this.left = left;
    }

    public void setRight(Feld right) {
        this.right = right;
    }

    public void setPrevious(Feld previous) {
        this.previous = previous;
    }
}
