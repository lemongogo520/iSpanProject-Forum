package forum.board.users;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUserResponseDTO {

    private UUID uid;
    private String account;
    private String name;
    private String email;
    private Integer permission;

    public AdminUserResponseDTO(
            UUID uid,
            String account,
            String name,
            String email,
            Integer permission) {

        this.uid = uid;
        this.account = account;
        this.name = name;
        this.email = email;
        this.permission = permission;
    }
}
