package forum.board.login;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import forum.board.response.ErrorResponse;
import forum.board.response.SuccessResponse;
import forum.board.users.UserDetailDTO;
import forum.board.users.Users;
import forum.board.users.UsersService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

	private final UsersService usersService;
	
    @GetMapping("/auth/me")
    public ResponseEntity<?> checkLogin(HttpSession session) {

	    UUID userId = (UUID) session.getAttribute("loginUserId");

	    if (userId == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	    }

	    Users users = usersService.getUsersById(userId);

	    if (users == null) {
	        session.invalidate();
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	    }

        UserDetailDTO dto = usersService.getUserInfoByAccount(users.getAccount());
        Map<String, Object> returnValue = new HashMap<>();
        returnValue.put("message", "登入成功");
        returnValue.put("account", dto.getAccount());
        returnValue.put("name", dto.getName());
        returnValue.put("permission", dto.getPermission());

	    return ResponseEntity.ok(returnValue);
    }
}