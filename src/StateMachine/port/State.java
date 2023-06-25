package StateMachine.port;


public interface State {

    enum Value {
        WÜRFELN,
        FIGUR_AUF_STARTFELD_SETZEN,
        FIGUREN_BEWEGEN,
        END
    }

    public void setState(State.Value state);

    public Value getState();
}
