package StateMachine.port;

import StateMachine.StateMachineImpl;

public interface Observer {
    void update(StateMachineImpl stateMachine);
}
