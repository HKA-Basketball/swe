package GUI;

import Logic.GameManagerFactory;
import StateMachine.StateMachineFactory;
import StateMachine.impl.StateMachineImpl;
import StateMachine.port.Observer;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import static StateMachine.port.State.Value.*;

class GuiController implements GuiControllerFactory, Observer {
    private static final Scanner scanner = new Scanner(System.in);

    private StateMachineFactory stateMachine = StateMachineFactory.FACTORY;
    private GameManagerFactory game = GameManagerFactory.FACTORY;
    private GuiViewFactory gui = GuiViewFactory.FACTORY;

    public GuiController() {
        stateMachine.getInstance();
        if (stateMachine == null) {
            //ERROR
        }
        stateMachine.attach((Observer) gui);
        stateMachine.attach(this);
        stateMachine.attach((Observer) game);

        stateMachine.setState(ROLL_DICE);
    }

    public void startLoop() {
        while (true) {
            stateMachine.notifyObservers();
        }
    }

    private String getStringInput(List<String> valid) {
        while (true) {
            String input = scanner.nextLine();
            if (valid.contains(input)) {
                return input;
            }
        }
    }

    private String getStringInput(String valid) {
        return getStringInput(valid.chars()
                .mapToObj(ch -> String.valueOf((char) ch))
                .collect(Collectors.toList()));
    }

    private int getIntInput(int max) {
        int input;
        do {
            try {
                input = scanner.nextInt();
            } catch (InputMismatchException e) {
                input = -1; // Invalid input, set to a value outside the valid range
                scanner.next(); // Clear the invalid input from the scanner
            }
        } while (input < 0 || input > max);

        return input;
    }

    @Override
    public void update(StateMachineImpl stateMachine) {
        //System.err.println(stateMachine.getState());
        switch (stateMachine.getState()){
            case ROLL_DICE, ROLL_DICE_AGAIN -> game.setInput(getStringInput("xy"));
            case SELECT_FIGURE -> game.setInput(getStringInput(game.getStringListOfMovableFigures()));
            case SELECT_MOVE_AMOUNT -> game.setInput(String.valueOf(
                    getIntInput(game.getCurrentPlayer().getDiceValue())));
            case MOVE_FORWARD_BACKWARD -> game.setInput(getStringInput("vr"));
            case FORK_REACHED_LEFT_RIGHT_MIDDLE -> game.setInput(getStringInput("lmr"));
            case FORK_REACHED_LEFT_RIGHT -> game.setInput(getStringInput("lr"));
        }
    }
}
