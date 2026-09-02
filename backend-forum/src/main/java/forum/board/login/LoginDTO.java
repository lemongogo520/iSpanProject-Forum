package forum.board.login;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDTO {

    @NotBlank(message="請輸入帳號")
    private String account;

    @NotBlank(message="請輸入密碼")
    private String password;

    private boolean remember;
}
