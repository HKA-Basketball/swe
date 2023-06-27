package Logic.impl;

import Logic.port.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static Logic.impl.Player.Color;
import static Logic.impl.Player.Color.*;

public class PlayingField {

    private static final Map<Color, List<Start>> startingFields = Map.of(
            RED, new ArrayList<>(2),
            BLUE, new ArrayList<>(2),
            YELLOW, new ArrayList<>(2)
    );

    public static Map<Color, List<Start>> getStartingFields() {
        return startingFields;
    }

    static {
        startingFields.get(RED).add(new Start(1));
        Field current = startingFields.get(RED).get(0);

        // Outer cube
        for (int i = 2; i <= 56; i++) {
            Field nextField;

            if (i == 15 || i == 29 || i == 43) {
                Start tmp = new Start(i);
                nextField = tmp;

                if (i == 15) {
                    startingFields.get(YELLOW).add(tmp);
                } else if (i == 29) {
                    startingFields.get(BLUE).add(tmp);
                } else if (i == 43) {
                    //startingFields.get(BLACK).add(tmp);
                }

                if (current instanceof Gabelung currentGab) {
                    currentGab.setLeft(nextField);
                } else {
                    ((Path) current).setNext(nextField);
                }
                nextField.setPrevious(current);
            } else {
                nextField = new Path(i);
                if (current instanceof Gabelung currentGab) {
                    currentGab.setLeft(nextField);
                } else {
                    ((Path) current).setNext(nextField);
                }
                nextField.setPrevious(current);
            }
            current = nextField;
        }

        ((Path) current).setNext(startingFields.get(RED).get(0));
        startingFields.get(RED).get(0).setPrevious(current);

        startingFields.get(RED).add(new Start(57));
        current = startingFields.get(RED).get(1);
        // Inner cube
        for (int i = 59; i <= 97; i++) {
            Field nextField;

            if (i == 67 || i == 77 || i == 87) {
                Start tmp = new Start(i);
                nextField = tmp;

                if (i == 67) {
                    startingFields.get(YELLOW).add(tmp);
                } else if (i == 77) {
                    startingFields.get(BLUE).add(tmp);
                } else if (i == 87) {
                    //startingFields.get(Black).add(tmp);
                }

                if (current instanceof Gabelung currentGab) {
                    currentGab.setRight(nextField);
                } else {
                    ((Path) current).setNext(nextField);
                }
                nextField.setPrevious(current);
            } else {
                nextField = new Path(i);
                if (current instanceof Gabelung currentGab) {
                    currentGab.setRight(nextField);
                } else {
                    ((Path) current).setNext(nextField);
                }
                nextField.setPrevious(current);
            }
            current = nextField;
        }

        ((Path) current).setNext(startingFields.get(RED).get(1));
        startingFields.get(RED).get(1).setPrevious(current);

        startingFields.get(RED).get(0).setRight(startingFields.get(RED).get(1));
        startingFields.get(RED).get(1).setLeft(startingFields.get(RED).get(0));

        startingFields.get(YELLOW).get(0).setRight(startingFields.get(YELLOW).get(1));
        startingFields.get(YELLOW).get(1).setLeft(startingFields.get(YELLOW).get(0));

        startingFields.get(BLUE).get(0).setRight(startingFields.get(BLUE).get(1));
        startingFields.get(BLUE).get(1).setLeft(startingFields.get(BLUE).get(0));
    }

}
