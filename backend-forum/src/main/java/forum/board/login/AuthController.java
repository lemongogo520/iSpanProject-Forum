package forum.board.login;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import forum.board.response.ErrorResponse;
import forum.board.response.SuccessResponse;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class AuthController {

    @GetMapping("/auth/me")
    public ResponseEntity<?> checkLogin(HttpSession session) {

        UUID userId = (UUID) session.getAttribute("loginUserId");

        if (userId != null) {

            return ResponseEntity.ok(
                    new SuccessResponse("已登入")
            );
        }

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("尚未登入"));
    }
}