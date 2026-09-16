package forum.board.users;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UsersController {

    private final UsersService userService;

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
}
    