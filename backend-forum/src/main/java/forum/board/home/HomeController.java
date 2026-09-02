package forum.board.home;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import forum.board.common.util.CookieUtil;
import forum.board.enums.LogType;
import forum.board.login.LoginService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {
	
	private final LoginService loginService;

    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }
    
    @GetMapping("/getfile")
    public String getFile() {
    	return "getfile";
    }

    @GetMapping("/home")
    public String home() {
        return "home/homepage";
    }
    
    @GetMapping("/home/homepage")
    public String homepage() {
        return "home/homepage";
    }
    
    @PostMapping("/logout")
    public String logout(HttpSession session
			, HttpServletResponse response
			, HttpServletRequest request) {

    	
    	if(session.getAttribute("loginUserId") != null) {
            loginService.insertLoginLog(request, (UUID) session.getAttribute("loginUserId"), LogType.LOGOUT);

            //刪除全部
        	//session.invalidate();
        	//刪除登入session
        	session.removeAttribute("loginUserId");
        	
        	//刪除token
        	String token = CookieUtil.getCookieValue(request, "remember_token");
        	if(token != null) {
            	loginService.updateTokenActive(token, false);
        	}
        	
        	//清除cookie
        	Cookie cookie = new Cookie("remember_token", null);
    	    cookie.setHttpOnly(true);
    	    cookie.setMaxAge(0);
    	    cookie.setPath("/");

    	    response.addCookie(cookie);
    	    
    	}
    	
    	return "redirect:/login";
    }
}