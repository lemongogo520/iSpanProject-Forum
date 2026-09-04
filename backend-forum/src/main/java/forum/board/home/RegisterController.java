package forum.board.home;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import forum.board.common.util.IpUtil;
import forum.board.exception.AccountExistsException;
import forum.board.users.UsersService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RegisterController {

    private final UsersService usersService;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterDTO dto,
            HttpServletRequest request) {

        // 密碼一致驗證
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {

            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse("兩次輸入的密碼不一致"));
        }

        try {

            // 建立會員
            usersService.register(
                    dto,
                    IpUtil.getClientIp(request)
            );

        } catch (AccountExistsException e) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(e.getMessage()));
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new SuccessResponse("註冊成功"));
    }


    // =========================
    // API 回傳格式
    // =========================

    public record ErrorResponse(String message) {
    }

    public record SuccessResponse(String message) {
    }
}