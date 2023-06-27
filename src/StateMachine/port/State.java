package StateMachine.port;


public interface State {

    enum Value {
        NONE,
        ROLL_DICE,
        ROLL_DICE_AGAIN,
        HAS_ROLLED,
        MOVES,
        REMAINING_MOVES,
        FORK_REACHED,
        MOVE,
        CHECK_COLLISION,
        FORK_REACHED_LEFT_RIGHT_MIDDLE,
        FORK_REACHED_LEFT_RIGHT,
        MOVE_DIRECTION,
        MOVES_BY,
        START_FIELD,
        MOVE_FORWARD_BACKWARD,
        SELECT_FIGURE,
        MOVE_LEFT_RIGHT,
        MOVE_LEFT_RIGHT_MIDDLE,
        PLAYER_TURN,
        SELECT_MOVE_AMOUNT,
        CHOOSE_DIRECTION,
        DUEL_START,
        NEXT_PLAYER,
        END
    }

    public void setState(State.Value state);

    public Value getState();
}
