package GUI;

import Logic.port.GameManager;
import StateMachine.StateMachineFactory;
import StateMachine.impl.StateMachineImpl;
import StateMachine.port.Observer;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import static StateMachine.port.State.Value.*;

public class GuiController implements Observer {
    private static final Scanner scanner = new Scanner(System.in);
    private GameManager gameInfos = GameManager.FACTORY;

    private String input;

    private StateMachineFactory stateMachine = StateMachineFactory.FACTORY;
    private GameManager game = GameManager.FACTORY;
    private GuiFactory gui = GuiFactory.FACTORY;

    public GuiController() {
        stateMachine.getInstance();
        stateMachine.attach((Observer) gui);
        stateMachine.attach(this);
        stateMachine.attach((Observer) game);

        stateMachine.setState(ROLL_DICE);

        while (true) {
            stateMachine.notifyObservers();
        }
    }

    private String getStringInput(List<String> valid) {

        var input = "d1650de6-55eb-45d0-a015-c6d387865ca8";
        while (!valid.contains(input)) {
            input = scanner.nextLine();
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
            } catch (InputMismatchException e) {
            }
        } while (input < 0 || input > max);

        return input;
    }

    public String getInput() {
        return input;
    }

    @Override
    public void update(StateMachineImpl stateMachine) {
        //System.err.println(stateMachine.getState());
        switch (stateMachine.getState()){
            case ROLL_DICE, ROLL_DICE_AGAIN -> gameInfos.setInput(getStringInput("xy"));
            case SELECT_FIGURE -> gameInfos.setInput(getStringInput(gameInfos.getStringListOfMovableFigures()));
            case SELECT_MOVE_AMOUNT -> gameInfos.setInput(String.valueOf(getIntInput(gameInfos.getPlayers().get(gameInfos.getCurrentPlayer()).getDiceValue())));
            case MOVE_FORWARD_BACKWARD -> gameInfos.setInput(getStringInput("vr"));
            case FORK_REACHED_LEFT_RIGHT_MIDDLE -> gameInfos.setInput(getStringInput("lmr"));
            case FORK_REACHED_LEFT_RIGHT -> gameInfos.setInput(getStringInput("lr"));
        }
    }
}
