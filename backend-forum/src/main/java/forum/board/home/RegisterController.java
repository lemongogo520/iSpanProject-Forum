package forum.board.home;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;

import forum.board.common.util.IpUtil;
import forum.board.exception.AccountExistsException;
import forum.board.users.UsersService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
@RequiredArgsConstructor
public class RegisterController {
	
	private final UsersService usersService;

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerDTO") RegisterDTO dto,
                           BindingResult result,
                           HttpServletRequest request,
                           Model model) {

        if (result.hasErrors()) {
            model.addAttribute("error",
                    result.getFieldError().getDefaultMessage());
            model.addAttribute("registerDTO", dto);
            return "register";
        }

        // 密碼一致驗證
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            model.addAttribute("error", "兩次輸入的密碼不一致");
            model.addAttribute("registerDTO", dto);
            return "register";
        }
        
        try {
        	//建立會員
            usersService.register(dto, IpUtil.getClientIp(request));
        } catch(AccountExistsException e){

            model.addAttribute(
                "error",
                e.getMessage()
            );

            return "register";
        }

        
        
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {

        model.addAttribute("registerDTO", new RegisterDTO());

        return "register";
    }
}