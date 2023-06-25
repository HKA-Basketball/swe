package StateMachine;

import StateMachine.port.State;

public interface StateMachine {
    void setState(State.Value state);
    State.Value getState();
}
