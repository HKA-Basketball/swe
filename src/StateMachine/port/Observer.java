package StateMachine.port;

import StateMachine.impl.StateMachineImpl;

public interface Observer {
    void update(StateMachineImpl stateMachine);
}
