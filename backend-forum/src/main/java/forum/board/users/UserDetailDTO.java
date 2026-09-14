package forum.board.users;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDetailDTO {
    private UUID id;
    
	private String account;
	
	private Integer permission;

    private String name;

    private String nickname;
    
    private Boolean showNickname;

    private LocalDate birthday;

    private Boolean showBirthday;

    private String email;

    private Boolean showEmail;

    private String photoUrl;
    
    private Boolean showPhoto;
    
    private Long coin;
}
