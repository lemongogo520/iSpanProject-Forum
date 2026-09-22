package forum.board.home;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import forum.board.common.util.IpUtil;
import forum.board.config.Settings;
import forum.board.exception.AccountExistsException;
import forum.board.response.ErrorResponse;
import forum.board.response.SuccessResponse;
import forum.board.users.UsersService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RegisterController {

    private final UsersService usersService;
    
    private final Settings settings;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterDTO dto,
            HttpServletRequest request) {


        int accountMinLength = Integer.parseInt(settings.get("account_min_length"));
        int accountMaxLength = Integer.parseInt(settings.get("account_max_length"));
        int passwordMinLength = Integer.parseInt(settings.get("password_min_length"));
    	
        //驗證帳號長度
        if (dto.getAccount().length() < accountMinLength ||
                dto.getAccount().length() > accountMaxLength) {

        	String message = String.format("帳號長度必須介於%s~%s個字元之間"
        			, accountMinLength, accountMaxLength);
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse(message));
        }

        //驗證密碼長度
        if (dto.getPassword().length() < passwordMinLength ||
                dto.getConfirmPassword().length() < passwordMinLength) {

        	String message = String.format("密碼長度必須大於%s個字元", passwordMinLength);
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse(message));
        }
        
    	System.out.printf("0");
        // 密碼一致驗證
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {

            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse("兩次輸入的密碼不一致"));
        }

        try {
            // 建立會員
            usersService.register(dto, IpUtil.getClientIp(request));
            
        } catch (AccountExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getMessage()));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new SuccessResponse("註冊成功"));
    }


    // =========================
    // API 回傳格式
    // =========================
}