package Logic.impl;

import GUI.GuiFactory;
import Logic.port.Feld;
import Logic.port.GameManager;
import StateMachine.port.State;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class GameManagerImpl implements GameManager {

    GuiFactory gui = GuiFactory.FACTORY;
    private static final Scanner scanner = new Scanner(System.in);
    private Feld spielFeld = null;

    private List<Gabelung> rotStartFeld = new ArrayList<>(2);
    private List<Gabelung> blauStartFeld = new ArrayList<>(2);
    private List<Gabelung> gelbStartFeld = new ArrayList<>(2);
    private List<Spieler> spieler = new ArrayList<>(3);

    /*public void setPositionByIDs(int playerID, int figurID, Feld newPosition) {
        this.spieler.get(playerID).getFiguren().get(figurID).setPosition(newPosition);
    }*/

    public GameManagerImpl() {
        // Init
        createSpielfeld();
        creatPlayer();
    }

    public void startLogic() {
        // Game Logic
        while (true) {
            playStepByStep(spieler.get(0));
            playStepByStep(spieler.get(1));
            playStepByStep(spieler.get(2));
        }
    }

    private void playStepByStep(Spieler spieler) {
        int spielerId = this.spieler.indexOf(spieler);

        gui.renderUebersicht(State.Value.PLAYER_TURN, State.Value.ROLL_DICE, spieler, this.spieler);

        int num = 0;
        if(spieler.getFigurenAufSpielfeld().isEmpty()) {
            for (int i = 0; i < 3; i++) {
                String returnVal = getStringInput("xy");
                num = returnVal.equals("y") ? 7 : wuerfeln();
                this.spieler.get(spielerId).setDiceValue(num);

                if(num == 7) {
                    break;
                }

                gui.renderUebersicht(State.Value.HAS_ROLLED, State.Value.ROLL_DICE, spieler, this.spieler);
            }
        } else {
            String returnVal = getStringInput("xy");
            num = returnVal.equals("y") ? 7 : wuerfeln();
            this.spieler.get(spielerId).setDiceValue(num);
        }


        if (spieler.getDiceValue() == 7) {
            gui.renderUebersicht(State.Value.HAS_ROLLED, State.Value.START_FIELD, spieler, this.spieler);
            figurAufStart(spielerId);

        } else if(!spieler.getFigurenAufSpielfeld().isEmpty()) {

            figurenBewegen(spielerId, spieler.getDiceValue());
        }
    }

    private void createSpielfeld() {
        rotStartFeld.add(new Start(1));
        spielFeld = rotStartFeld.get(0);

        Feld current = spielFeld;
        // Outer cube
        for (int i = 2; i <= 56; i++) {
            Feld nextField;

            if(i == 15 || i == 29 || i == 43) {
                Start tmp = new Start(i);
                nextField = tmp;

                if(i == 15) {
                    gelbStartFeld.add(tmp);
                } else if (i == 29 ) {
                    blauStartFeld.add(tmp);
                } else if (i == 43) {
                    //schwartzeFeld.add(tmp);
                }

                if(current instanceof Gabelung currentGab) {
                    currentGab.setLeft(nextField);
                } else {
                    ((Weg) current).setNext(nextField);
                }
                nextField.setPrevious(current);
            } else {
                nextField = new Weg(i);
                if(current instanceof Gabelung currentGab) {
                    currentGab.setLeft(nextField);
                } else {
                    ((Weg)current).setNext(nextField);
                }
                nextField.setPrevious(current);
            }
            current = nextField;
        }
        ((Weg) current).setNext(spielFeld);
        spielFeld.setPrevious(current);

        rotStartFeld.add(new Start(57));
        current = rotStartFeld.get(1);
        // Inner cube
        for (int i = 59; i <= 97; i++) {
            Feld nextField;

            if(i == 67 || i == 77 || i == 87) {
                Start tmp = new Start(i);
                nextField = tmp;

                if(i == 67) {
                    gelbStartFeld.add(tmp);
                } else if (i == 77) {
                    blauStartFeld.add(tmp);
                } else if (i == 87) {
                    //schwartzeFeld = tmp;
                }

                if(current instanceof Gabelung currentGab) {
                    currentGab.setRight(nextField);
                } else {
                    ((Weg) current).setNext(nextField);
                }
                nextField.setPrevious(current);
            } else {
                nextField = new Weg(i);
                if(current instanceof Gabelung currentGab) {
                    currentGab.setRight(nextField);
                } else {
                    ((Weg)current).setNext(nextField);
                }
                nextField.setPrevious(current);
            }
            current = nextField;
        }
        ((Weg) current).setNext(rotStartFeld.get(1));
        rotStartFeld.get(1).setPrevious(current);

        rotStartFeld.get(0).setRight(rotStartFeld.get(1));
        rotStartFeld.get(1).setLeft(rotStartFeld.get(0));

        gelbStartFeld.get(0).setRight(gelbStartFeld.get(1));
        gelbStartFeld.get(1).setLeft(gelbStartFeld.get(0));

        blauStartFeld.get(0).setRight(blauStartFeld.get(1));
        blauStartFeld.get(1).setLeft(blauStartFeld.get(0));
    }

    private void creatPlayer() {
        spieler.add(new Spieler("Player1", Spieler.Color.ROT, rotStartFeld));
        spieler.add(new Spieler("Player2", Spieler.Color.BLAU, blauStartFeld));
        spieler.add(new Spieler("Player3", Spieler.Color.GELB, gelbStartFeld));
    }

    public Feld getSpielFeld() {
        return spielFeld;
    }

    public int wuerfeln() {
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int sum = ThreadLocalRandom.current().nextInt(1, 7);
        sum += ThreadLocalRandom.current().nextInt(1, 7);
        return sum;
    }

    public void figurAufStart(int spielerId) {
        boolean startFeld1 = false, startFeld2 = false;

        for (Figur figurPrüfen: this.spieler.get(spielerId).getFigurenAufSpielfeld()) {

            if(!startFeld1) {
                startFeld1 = figurPrüfen.getPosition().equals(this.spieler.get(spielerId).getStartFelder().get(0));
            }

            if(!startFeld2) {
                startFeld2 = figurPrüfen.getPosition().equals(this.spieler.get(spielerId).getStartFelder().get(1));
            }

            if (startFeld1 && startFeld2) break;
        }

        if(!startFeld1 || !startFeld2) {
            for (int i = 0; i < this.spieler.get(spielerId).getFiguren().size(); i++) {
                if(this.spieler.get(spielerId).getFiguren().get(i).isHeimat()) {
                    if(!startFeld1) {
                        this.spieler.get(spielerId).getFiguren().get(i).setPosition(this.spieler.get(spielerId).getStartFelder().get(0));
                        this.spieler.get(spielerId).getFiguren().get(i).setHeimat(false);
                    } else if(!startFeld2) {
                        this.spieler.get(spielerId).getFiguren().get(i).setPosition(this.spieler.get(spielerId).getStartFelder().get(1));
                        this.spieler.get(spielerId).getFiguren().get(i).setHeimat(false);
                    }
                    break;
                }
            }
        }
    }

    public void figurenBewegen(int spielerId, int distance) {

        int canMove = distance;
        while(canMove > 0) {
            gui.renderUebersicht(State.Value.REMAINING_MOVES, State.Value.SELECT_FIGURE, spieler.get(spielerId), null, canMove, this.spieler);
            String[] stringList = spieler.get(spielerId).getFigurenAufSpielfeld().stream().map(Figur::toString).collect(Collectors.joining(";")).split(";");

            String figureString = getStringInput(Arrays.asList(stringList));
            int movingFigureId = 0;
            for(int i = 0; i < 5; i++) {
                if(!spieler.get(spielerId).getFiguren().get(i).isHeimat()) {
                    if(spieler.get(spielerId).getFiguren().get(i).getId().equals(figureString)) {
                        movingFigureId = i;
                        break;
                    }
                }
            }

            gui.renderUebersicht(State.Value.REMAINING_MOVES, State.Value.SELECT_MOVE_AMOUNT, spieler.get(spielerId), spieler.get(spielerId).getFiguren().get(movingFigureId), canMove, this.spieler);
            int movingDistance = getIntInput(canMove);
            figurBewegen(spielerId, movingFigureId, movingDistance);
            canMove = canMove - movingDistance;
            checkForCollision(spielerId, movingFigureId);
        }
    }

    public void figurBewegen(int spielerId, int figurId, int distance) {
        Feld previousPosition = spieler.get(spielerId).getFiguren().get(figurId).getPosition();

        for(int i = 0; i < distance; i++) {

            if(spieler.get(spielerId).getFiguren().get(figurId).getPosition() instanceof Gabelung gabelung) {
                if(i == 0) {
                    gui.renderUebersicht(State.Value.REACHED_FORK, State.Value.MOVE_LEFT_RIGHT_MIDDLE, spieler.get(spielerId), spieler.get(spielerId).getFiguren().get(figurId), this.spieler);
                    previousPosition = forkReached(spielerId, figurId, getStringInput("lrm"));
                } else {
                    gui.renderUebersicht(State.Value.REACHED_FORK, State.Value.MOVE_LEFT_RIGHT, spieler.get(spielerId), spieler.get(spielerId).getFiguren().get(figurId), this.spieler);
                    previousPosition = forkReached(spielerId, figurId, getStringInput("lr"), previousPosition);
                }

            } else {
                if(i == 0) {
                    gui.renderUebersicht(State.Value.MOVES_BY, State.Value.MOVE_FORWARD_BACKWARD, spieler.get(spielerId), spieler.get(spielerId).getFiguren().get(figurId), distance, this.spieler);
                    previousPosition = richtungBestimmen(spielerId, figurId, getStringInput("vr"));
                } else {
                    previousPosition = moveForward(spielerId, figurId, previousPosition);
                }
            }
        }
    }

    public Feld richtungBestimmen(int spielerId, int figurId, String richtung) {
        Feld previousPosition = spieler.get(spielerId).getFiguren().get(figurId).getPosition();

        if(spieler.get(spielerId).getFiguren().get(figurId).getPosition() instanceof Weg weg) {
            if("v".equals(richtung)) {
                spieler.get(spielerId).getFiguren().get(figurId).setPosition(weg.getNext());
            } else {
                spieler.get(spielerId).getFiguren().get(figurId).setPosition(weg.getPrevious());
            }

        } else if(spieler.get(spielerId).getFiguren().get(figurId).getPosition() instanceof Gabelung gabelung) {
            if("v".equals(richtung)) {
                spieler.get(spielerId).getFiguren().get(figurId).setPosition(gabelung.getLeft());
            } else {
                spieler.get(spielerId).getFiguren().get(figurId).setPosition(gabelung.getPrevious());
            }
        }

        return previousPosition;
    }

    public Feld forkReached(int spielerId, int figurId, String richtung, Feld previous) {
        Feld previousPosition = spieler.get(spielerId).getFiguren().get(figurId).getPosition();

        if("r".equals(richtung)) {
            if(((Gabelung)previousPosition).getRight().equals(previous)) {
                spieler.get(spielerId).getFiguren().get(figurId).setPosition(((Gabelung)previousPosition).getPrevious());
            } else {
                spieler.get(spielerId).getFiguren().get(figurId).setPosition(((Gabelung)previousPosition).getRight());
            }
        } else {
            if(((Gabelung)previousPosition).getLeft().equals(previous)) {
                spieler.get(spielerId).getFiguren().get(figurId).setPosition(((Gabelung)previousPosition).getPrevious());
            } else {
                spieler.get(spielerId).getFiguren().get(figurId).setPosition(((Gabelung)previousPosition).getLeft());
            }
        }

        return previousPosition;
    }

    public Feld forkReached(int spielerId, int figurId, String richtung) {
        Feld previousPosition = spieler.get(spielerId).getFiguren().get(figurId).getPosition();

        if("r".equals(richtung)) {
            spieler.get(spielerId).getFiguren().get(figurId).setPosition(((Gabelung)previousPosition).getRight());

        } else if("l".equals(richtung)) {
            spieler.get(spielerId).getFiguren().get(figurId).setPosition(((Gabelung)previousPosition).getLeft());

        } else {
            spieler.get(spielerId).getFiguren().get(figurId).setPosition(((Gabelung)previousPosition).getPrevious());
        }

        return previousPosition;
    }

    public Feld moveForward(int spielerId, int figurId, Feld previous) {
        Feld previousPosition = spieler.get(spielerId).getFiguren().get(figurId).getPosition();

        if(spieler.get(spielerId).getFiguren().get(figurId).getPosition() instanceof Weg weg) {
            if(weg.getPrevious().equals(previous)) {
                spieler.get(spielerId).getFiguren().get(figurId).setPosition(weg.getNext());
            } else {
                spieler.get(spielerId).getFiguren().get(figurId).setPosition(weg.getPrevious());
            }

        } else if(spieler.get(spielerId).getFiguren().get(figurId).getPosition() instanceof Gabelung gabelung) {
            if(gabelung.getPrevious().equals(previous)) {
                spieler.get(spielerId).getFiguren().get(figurId).setPosition(gabelung.getLeft());
            } else {
                spieler.get(spielerId).getFiguren().get(figurId).setPosition(gabelung.getPrevious());
            }
        }

        return previousPosition;
    }

    public void checkForCollision(int playerID, int figureID) {

        for (int i = 0; i < spieler.size(); i++) {
            if(playerID == i) continue;

            for (int j = 0; j < spieler.get(i).getFiguren().size(); j++) {

                if(spieler.get(playerID).getFiguren().get(figureID).getPosition().equals(spieler.get(i).getFiguren().get(j).getPosition())) {
                    spieler.get(i).getFiguren().get(j).setHeimat(true);
                }
            }
        }
    }

    private String getStringInput(List<String> valid) {

        var input = "d1650de6-55eb-45d0-a015-c6d387865ca8";
        while(!valid.contains(input)) {
            input = scanner.nextLine();
            System.out.println(input);
        }

        return input;
    }

    private String getStringInput(String valid) {
        List<String> list = new ArrayList<>(valid.length());
        for (int i = 0; i < valid.length(); i++) {
            list.add(String.valueOf(valid.charAt(i)));
        }
        return getStringInput(list);
    }

    private int getIntInput(int max) {

        var input = 0;
        do {
            try {
                input = scanner.nextInt();
            } catch (InputMismatchException e) {}
            System.out.println(input);
        }while(input < 0 || input > max);

        return input;
    }
}
