package forum.board.users;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUserCreateDTO {

    private UUID uid;
    private String account;
    private String password;
    private String name;
    private String email;
    private Integer permission;
}
