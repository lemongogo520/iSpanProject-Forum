package forum.board.login;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import forum.board.users.UserDetailDTO;
import forum.board.users.Users;
import forum.board.users.UsersService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final UsersService usersService;

    private final LoginService loginService;

    @GetMapping("/auth/me")
    public ResponseEntity<?> checkLogin(
            HttpSession session,
            HttpServletRequest request,
            @CookieValue(value = "remember_token", required = false) String rememberToken) {

        UUID userId = (UUID) session.getAttribute("loginUserId");

        Users users = null;

        // Session 有登入
        if (userId != null) {
            users = usersService.getUsersById(userId);
        }

        // Session 沒有，嘗試使用 remember_token 自動登入
        if (users == null && rememberToken != null) {
            users = loginService.checkRememberToken(request, rememberToken);

            if (users != null) {
                userId = users.getId();
                session.setAttribute("loginUserId", userId);
            }
        }

        // Session 和 Token 都無法取得使用者
        if (users == null) {
            session.invalidate();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDetailDTO dto = usersService.getUserInfoByAccount(users.getAccount());

        Map<String, Object> returnValue = new HashMap<>();
        returnValue.put("uid", dto.getId());
        returnValue.put("account", dto.getAccount());
        returnValue.put("name", dto.getName());
        returnValue.put("permission", dto.getPermission());

        return ResponseEntity.ok(returnValue);
    }
}