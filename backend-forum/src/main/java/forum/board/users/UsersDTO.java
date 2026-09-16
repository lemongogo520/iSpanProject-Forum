package forum.board.users;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsersDTO {

    private UUID uid;
    
	private String account;

    private String name;

    private String photoUrl;
    
    public UsersDTO(UserDetail user) {
        this.uid = user.getUsers().getId();
        this.account = user.getUsers().getAccount();
        this.name = user.getName();
        this.photoUrl = user.getPhoto();
    }
}
