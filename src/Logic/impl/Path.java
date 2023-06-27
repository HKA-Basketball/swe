package Logic.impl;

import Logic.port.Field;

public class Path implements Field {

    private int id;
    private Field next;
    private Field previous;

    public Path(int id) {
        this.id = id;
    }

    @Override
    public int getID() {
        return id;
    }

    public Field getNext() {
        return next;
    }

    public Field getPrevious() {
        return previous;
    }

    public void setNext(Field next) {
        this.next = next;
    }

    public void setPrevious(Field previous) {
        this.previous = previous;
    }
}
