package forum.board.login;

import forum.board.users.UserDetailDTO;
import forum.board.users.Users;
import forum.board.users.UsersService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import forum.board.enums.LogType;
import forum.board.exception.PasswordErrorException;
import forum.board.response.ErrorResponse;
import forum.board.response.SuccessResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LoginController {
	private final UsersService usersService;
	
	private final LoginService loginService;
	
	@GetMapping("/login")
    public String login(HttpServletRequest request
            , HttpSession session
			, HttpServletResponse response) {

	    if(session.getAttribute("loginUserId") != null){

		    Users rememberLogin = loginService.checkRememberToken(request, "remember_token");

		    if(rememberLogin != null){
		    	
		    	String newToken = loginService.generateLoginToken(request, rememberLogin.getAccount(), "remember_token");

	            Cookie cookie = new Cookie("remember_token", newToken);
	            cookie.setHttpOnly(true);
	            cookie.setMaxAge(60 * 60 * 24 * 7); // 7天
	            cookie.setPath("/");
	            response.addCookie(cookie);
	            
	            session.setAttribute("loginUserId", rememberLogin.getId());
	            
		        return "redirect:/home";
		    }
	    }
		
        return "login";
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginDTO loginDTO,
            BindingResult result,
            HttpSession session,
            HttpServletResponse response,
            HttpServletRequest request,
            @CookieValue(
                    value = "remember_token",
                    required = false
            ) String rememberToken) {

        // DTO 驗證失敗
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(
            		new ErrorResponse(result.getFieldError().getDefaultMessage()));
        }

        try {
            // 驗證帳號密碼
            usersService.checkPassword(loginDTO);

        } catch (PasswordErrorException e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            		new ErrorResponse(e.getMessage()));
        }

        // 取得使用者
        UserDetailDTO dto = usersService.getUserInfoByAccount(loginDTO.getAccount());

        // 登入紀錄
        loginService.insertLoginLog(request, dto.getId(), LogType.LOGIN);

        // 記住我
        if (loginDTO.isRemember()) {

            String loginToken = loginService.generateLoginToken(
                    request,
                    loginDTO.getAccount(),
                    rememberToken
            );

            Cookie cookie = new Cookie(
                    "remember_token",
                    loginToken
            );

            cookie.setHttpOnly(true);
            cookie.setMaxAge(60 * 60 * 24 * 7);
            cookie.setPath("/");

            response.addCookie(cookie);
        }

        // Session 登入
        session.setAttribute("loginUserId", dto.getId());

        Map<String, Object> returnValue = new HashMap<>();
        returnValue.put("message", "登入成功");
        returnValue.put("uid", dto.getId());
        returnValue.put("account", dto.getAccount());
        returnValue.put("name", dto.getName());
        returnValue.put("permission", dto.getPermission());

    	return ResponseEntity.ok(returnValue);	
    }
    
}
    