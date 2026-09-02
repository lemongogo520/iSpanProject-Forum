package forum.board.exception;

public class PasswordErrorException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    public PasswordErrorException(String message){
        super(message);
    }

}