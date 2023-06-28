package StateMachine.port;


public interface State {

    enum Value {
        NONE,
        ROLL_DICE,
        ROLL_DICE_AGAIN,
        START_FIELD,
        SELECT_FIGURE,
        SELECT_MOVE_AMOUNT,
        MOVE,
        MOVE_DIRECTION,
        MOVE_FORWARD_BACKWARD,
        FORK_REACHED_LEFT_RIGHT_MIDDLE,
        FORK_REACHED_LEFT_RIGHT,
        CHECK_COLLISION,
        NEXT_PLAYER,
        UPDATE_GUI
    }

    public void setState(State.Value state);

    public Value getState();
}
