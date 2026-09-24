package forum.board.users;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import forum.board.config.Settings;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController 
@RequestMapping("/api/users")
public class UsersController {

    private final UsersService userService;
    
    private final Settings settings;

    @GetMapping("/detail")
    public ResponseEntity<?> getUserDetail(@RequestParam String account) {

        if (account == null || account.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        if (userService.getUsersByAccount(account) == null) {
            return ResponseEntity.notFound().build();
        }
    	
        UserDetailDTO dto = userService.getUserDetail(account);
        return ResponseEntity.ok(dto);
    }
    

	@PostMapping("/detail")
	public ResponseEntity<UserDetail> saveUserDetail(
	        @RequestBody UserDetailDTO dto,
	        HttpSession session,
	        HttpServletRequest request) {

        UUID userId = (UUID) session.getAttribute("loginUserId");

	    if(session.getAttribute("loginUserId") != null){
		    UserDetail result = userService.saveUserDetail(dto, userId, request);
			
		    return ResponseEntity
		            .status(HttpStatus.CREATED)
		            .body(result);
	    }

		return ResponseEntity.badRequest().body(null);
	}
	
	@GetMapping("/allUsers")
	public List<UsersDTO> getAllUsers(
	        @RequestParam List<Integer> types) {

	    return userService.getAllUsersByTypes(types);
	}
	
	@GetMapping("/users/config")
	public UsersConfigDTO getUsersConfig() {
		
		UsersConfigDTO usersConfig = new UsersConfigDTO();
		usersConfig.setAccountMinLength(settings.get("account_min_length")); 
		usersConfig.setAccountMaxLength(settings.get("account_max_length")); 
		usersConfig.setPasswordMinLength(settings.get("password_min_length")); 
		usersConfig.setAllowChangePassword(Boolean.parseBoolean(settings.get("allow_change_password"))); 
	
		return usersConfig;
	}
	

    @GetMapping("/admin/allUsers")
    public List<AdminUserResponseDTO> getUsers(
            @RequestParam(required = false) String keyword) {

        return userService.searchUsers(keyword);
    }

    @GetMapping("/admin/{uid}")
    public AdminUserResponseDTO getUser(
            @PathVariable UUID uid) {

        return userService.getUser(uid);
    }
}
    