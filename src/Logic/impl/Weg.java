package Logic.impl;

import Logic.port.Feld;

public class Weg implements Feld {

    private int id;
    private Feld next;
    private Feld previous;

    public Weg(int id) {
        this.id = id;
    }

    @Override
    public int getID() {
        return id;
    }

    public Feld getNext() {
        return next;
    }

    public Feld getPrevious() {
        return previous;
    }

    public void setNext(Feld next) {
        this.next = next;
    }

    public void setPrevious(Feld previous) {
        this.previous = previous;
    }
}
