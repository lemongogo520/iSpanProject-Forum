package forum.board.users;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsersConfigDTO {
	private String accountMinLength;
	private String accountMaxLength;
	private String passwordMinLength;
	private boolean allowChangePassword;
}
