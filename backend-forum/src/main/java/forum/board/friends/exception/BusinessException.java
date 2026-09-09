package forum.board.friends.exception;

public class BusinessException extends RuntimeException{
    public BusinessException(
            String message
    ) {
        super(message);
    }
}
