package forum.board.login;

import forum.board.users.Users;
import forum.board.users.UsersService;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import forum.board.enums.LogType;
import forum.board.exception.PasswordErrorException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
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
    public String login(@Valid @ModelAttribute LoginDTO loginDTO
    			, BindingResult result
    			, HttpSession session
    			, Model model
    			, HttpServletResponse response
    			, HttpServletRequest request
    			,@CookieValue(value = "remember_token", required = false) String rememberToken) {

    	
        if(result.hasErrors()) {
            model.addAttribute("error", result.getFieldError().getDefaultMessage());
            return "login";
        }
        
        try {
        	usersService.checkPassword(loginDTO);
        }
        catch(PasswordErrorException e) {
        	
            model.addAttribute(
                    "error",
                    e.getMessage()
                );

            return "login";
        }
        
        UUID user_id = usersService.getUsersByAccount(loginDTO.getAccount()).getId();
        
        loginService.insertLoginLog(request, user_id, LogType.LOGIN);
        
        if(loginDTO.isRemember()) {
            String loginToken = loginService.generateLoginToken(request, loginDTO.getAccount(), rememberToken);
            
            Cookie cookie = new Cookie("remember_token", loginToken);
            cookie.setHttpOnly(true);
            cookie.setMaxAge(60 * 60 * 24 * 7); // 7天
            cookie.setPath("/");
            response.addCookie(cookie);
        }
        
        session.setAttribute("loginUserId", user_id);	

        return "redirect:/home";
    }

    @GetMapping("/index")
    public String index() {
        return "home/homepage";
    }
}
    