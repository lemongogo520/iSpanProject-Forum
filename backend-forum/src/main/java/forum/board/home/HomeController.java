package forum.board.home;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import forum.board.common.util.CookieUtil;
import forum.board.enums.LogType;
import forum.board.login.LoginService;
import forum.board.response.SuccessResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class HomeController {
	
	private final LoginService loginService;
	
	@PostMapping("/logout")
	public ResponseEntity<?> logout(
	        HttpSession session,
	        HttpServletResponse response,
	        HttpServletRequest request) {

	    if (session.getAttribute("loginUserId") != null) {

	        // 登出紀錄
	        loginService.insertLoginLog(
	                request,
	                (UUID) session.getAttribute("loginUserId"),
	                LogType.LOGOUT
	        );

	        // 移除登入 Session
	        session.removeAttribute("loginUserId");

	        // 取得 remember_token
	        String token = CookieUtil.getCookieValue(
	                request,
	                "remember_token"
	        );

	        if (token != null) {

	            // 將 token 設為無效
	            loginService.updateTokenActive(token, false);
	        }

	        // 清除 remember_token Cookie
	        Cookie cookie = new Cookie("remember_token", null);
	        cookie.setHttpOnly(true);
	        cookie.setMaxAge(0);
	        cookie.setPath("/");

	        response.addCookie(cookie);
	    }

	    return ResponseEntity.ok().body(new SuccessResponse("登出成功"));
	}
}