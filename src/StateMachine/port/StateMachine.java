package StateMachine.port;

public interface StateMachine {
    public void setState(State.Value state);

    public State.Value getState();
}
