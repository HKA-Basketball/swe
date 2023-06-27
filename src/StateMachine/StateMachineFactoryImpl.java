package StateMachine;

import StateMachine.impl.StateMachineImpl;
import StateMachine.port.Observer;
import StateMachine.port.State;
import StateMachine.port.StateMachine;
import StateMachine.port.Subject;

import java.util.ArrayList;
import java.util.List;

public class StateMachineFactoryImpl implements StateMachineFactory, Subject, StateMachine {

    private StateMachineImpl stateMachine;

    public void getInstance() {
        if (this.stateMachine == null)
            this.stateMachine = new StateMachineImpl();
    }

    @Override
    public void setState(State.Value state) {
        this.stateMachine.setState(state);
        notifyObservers();
    }

    @Override
    public State.Value getState() {
        return this.stateMachine.getState();
    }

    @Override
    public void attach(Observer observer) {
        this.stateMachine.attach(observer);
    }

    @Override
    public void detach(Observer observer) {
        this.stateMachine.detach(observer);
    }

    @Override
    public void notifyObservers() {
        this.stateMachine.notifyObservers();
    }
}
