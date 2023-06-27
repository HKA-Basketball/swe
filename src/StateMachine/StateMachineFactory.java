package StateMachine;

import StateMachine.port.Observer;
import StateMachine.port.State;

public interface StateMachineFactory {
    StateMachineFactory FACTORY = new StateMachineFactoryImpl();

    void getInstance();
    public void setState(State.Value state);
    public State.Value getState();
    public void attach(Observer observer);
    public void detach(Observer observer);
    public void notifyObservers();
}
