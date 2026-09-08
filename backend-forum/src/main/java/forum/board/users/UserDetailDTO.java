package forum.board.users;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDetailDTO {
	private String account;

    private String name;

    private String nickname;

    private Boolean showNickname;

    private LocalDate birthday;

    private Boolean showBirthday;

    private String email;

    private Boolean showEmail;

    private String photoUrl;
    
    private Boolean showPhoto;
}
