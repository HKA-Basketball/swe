package StateMachine.port;


public interface State {

    enum Value {
        ROLL_DICE,
        HAS_ROLLED,
        MOVES,
        REMAINING_MOVES,
        REACHED_FORK,
        MOVES_BY,
        START_FIELD,
        MOVE_FORWARD_BACKWARD,
        SELECT_FIGURE,
        MOVE_LEFT_RIGHT,
        MOVE_LEFT_RIGHT_MIDDLE,
        PLAYER_TURN,
        SELECT_MOVE_AMOUNT,
        DUEL_START,
        END
    }

    public void setState(State.Value state);

    public Value getState();
}
