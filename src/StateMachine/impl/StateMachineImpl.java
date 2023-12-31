package StateMachine.impl;

import StateMachine.port.StateMachine;
import StateMachine.port.Observer;
import StateMachine.port.State;
import StateMachine.port.Subject;

import java.util.ArrayList;
import java.util.List;

public class StateMachineImpl implements StateMachine, Subject {
    private State.Value currentState;
    private List<Observer> observers;

    public StateMachineImpl() {
        currentState = State.Value.NONE;
        observers = new ArrayList<>();
    }

    @Override
    public void setState(State.Value state) {
        currentState = state;
    }

    @Override
    public State.Value getState() {
        return currentState;
    }

    @Override
    public void attach(Observer observer) {

        //System.err.println("add: " + observer);
        observers.add(observer);
    }

    @Override
    public void detach(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {

            //System.err.println("Update: " + observer);
            observer.update(this);
        }
    }
}
