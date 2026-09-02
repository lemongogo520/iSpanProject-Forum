package forum.board.config;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import forum.board.enums.ConfigMenuType;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ConfigController {
	
    @GetMapping("/config")
    public String config(Model model) {
    	
    	model.addAttribute("activeMenu", ConfigMenuType.CONFIG);
        return "config/systemadmin";
    }
    
    @GetMapping("/config/users")
    public String configUsers(Model model) {
    	
    	model.addAttribute("activeMenu", ConfigMenuType.USERS);
        return "config/users";
    }
    
    @GetMapping("/config/permissions")
    public String configPermissions(Model model) {
    	
    	model.addAttribute("activeMenu", ConfigMenuType.PERMISSIONS);
        return "config/permissions";
    }
    
    @GetMapping("/config/settings")
    public String configSettings(Model model) {
    	
    	model.addAttribute("activeMenu", ConfigMenuType.SETTINGS);
        return "config/settings";
    }
}
