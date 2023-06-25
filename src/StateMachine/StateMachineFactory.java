package StateMachine;

import StateMachine.port.State;

public interface StateMachineFactory {
    StateMachineFactory FACTORY = new StateMachineFactoryImpl();

}
