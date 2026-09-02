package forum.board.exception;

public class AccountExistsException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    public AccountExistsException(String message){
        super(message);
    }

}