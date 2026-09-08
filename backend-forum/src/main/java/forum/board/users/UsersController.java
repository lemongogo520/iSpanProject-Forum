package forum.board.users;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UsersController {

    private final UsersService userService;

    @GetMapping("/detail")
    public ResponseEntity<?> getUserDetail(@RequestParam String account) {
    	
    	if(userService.getUsersByAccount(account) == null) { 
    		return ResponseEntity.badRequest().body(null);
    	}
    	
        UserDetailDTO dto = userService.getUserDetail(account);
        return ResponseEntity.ok(dto);
    }
}
    