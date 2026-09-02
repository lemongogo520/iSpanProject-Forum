package forum.board.users;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import forum.board.exception.*;
import forum.board.home.RegisterDTO;
import forum.board.login.LoginDTO;
import forum.board.userdetail.UserDetail;
import forum.board.userdetail.UserDetailRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsersService {
	
	private final UsersRepository usersRepository;

	private final UserDetailRepository userDetailRepository;
	
    private final PasswordEncoder passwordEncoder;
	
	/**
	 * 註冊會員
	 * @param dto
	 * @param registerIp 建立時的IP
	 */
	public void register(RegisterDTO dto, String registerIp) {

        if(usersRepository.existsByAccount(dto.getAccount())){
            throw new AccountExistsException("帳號已存在");
        }

		
		// TODO Auto-generated method stub
        Users user = new Users();
        user.setAccount(dto.getAccount());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setCreateIp(registerIp);
        
		UUID newId = UUID.randomUUID();
		user.setId(newId);
		user.setCreator(newId);
		
		UserDetail userDetail = new UserDetail();
		userDetail.setUserId(newId);
		userDetail.setCreateIp(registerIp);
		userDetail.setCreator(newId);

        usersRepository.save(user);
        userDetailRepository.save(userDetail);
	}
	
	public Users getUsersByAccount(String account) {
		return usersRepository.findByAccount(account).get();
	}
	
	public void checkPassword(LoginDTO dto) {
		String passwordMapping = getUsersByAccount(dto.getAccount()).getPassword();
		if(!passwordEncoder.matches(dto.getPassword(), passwordMapping)){
            throw new PasswordErrorException("帳號或密碼錯誤");
		}
	}

	public boolean existsAccount(String account) {
		// TODO Auto-generated method stub
		return false;
	}
}
    