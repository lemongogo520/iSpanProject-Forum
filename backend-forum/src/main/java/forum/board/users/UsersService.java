package forum.board.users;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import forum.board.coin.CoinService;
import forum.board.common.util.IpUtil;
import forum.board.exception.*;
import forum.board.home.RegisterDTO;
import forum.board.login.LoginDTO;
import forum.board.permissions.PermissionsService;
import forum.board.userpermission.UserPermission;
import forum.board.userpermission.UserPermissionRepository;
import forum.board.userpermission.UserPermissionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsersService {
	
	private final UsersRepository usersRepository;
	private final UserDetailRepository userDetailRepository;
	private final UserPermissionRepository userPermissionRepository;

    private final UserPermissionService userPermissionService;
    private final PermissionsService permissionsService;
    private final CoinService coinService;
    
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
		//加入預設使用者圖片
		userDetail.setUserId(newId);
		userDetail.setCreateIp(registerIp);
		userDetail.setCreator(newId);
		
		UserPermission userPer = new UserPermission();
		userPer.setUserId(newId);
		userPer.setCreateIp(registerIp);
		userPer.setCreator(newId);
		userPer.setPermissionId(permissionsService.getPermission("user").getId()); //初始劃為一般使用者

        usersRepository.save(user);
        userDetailRepository.save(userDetail);
        userPermissionRepository.save(userPer);
	}

	/**
	 * 透過帳號取得使用者
	 * @param account
	 * @return
	 */
	public Users getUsersById(UUID userId) {
		return usersRepository.findById(userId).get();
	}
	
	/**
	 * 透過帳號取得使用者
	 * @param account
	 * @return
	 */
	public Users getUsersByAccount(String account) {
		return usersRepository.findByAccount(account).get();
	}

	/**
	 * 透過帳號取得使用者完整資料
	 * @param account
	 * @return
	 */
	public UserDetailDTO getUserInfoByAccount(String account) {
		
		UserDetailDTO dto = new UserDetailDTO();
		Users users = usersRepository.findByAccount(account).get();
	    UserDetail ud = userDetailRepository.findByUserId(users.getId()).get();
		dto.setId(users.getId());
		dto.setAccount(users.getAccount());
		dto.setPermission(userPermissionService.getPermissionRankByUserId(users.getId()));
	    dto.setName(ud.getName());
	    dto.setNickname(ud.getNickName());
	    dto.setShowNickname(ud.getShowNickName());
	    dto.setBirthday(ud.getBirthday());
	    dto.setShowBirthday(ud.getShowBirthday());
	    dto.setEmail(ud.getEmail());
	    dto.setShowEmail(ud.getShowEmail());
	    dto.setPhotoUrl(ud.getPhoto());
	    dto.setShowPhoto(ud.getShowPhoto());
	    dto.setCoin(coinService.getBalance(users.getId()));
		
		return dto;
	}
	
	/**
	 * 驗證密碼是否正確
	 * @param dto
	 */
	public void checkPassword(LoginDTO dto) {
		
		String passwordMapping = getUsersByAccount(dto.getAccount()).getPassword();
		if(!passwordEncoder.matches(dto.getPassword(), passwordMapping)){
            throw new PasswordErrorException("帳號或密碼錯誤");
		}
	}
	
	/**
	 * 驗證帳號是否已經存在
	 * @param account
	 * @return
	 */
	public boolean existsAccount(String account) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * 取得使用者明細資料
	 * @param account
	 * @return
	 */
	public UserDetailDTO getUserDetail(String account) {
		Users users = getUsersByAccount(account);
	
	    UserDetailDTO dto = new UserDetailDTO();
	    Optional<UserDetail> userDetail = userDetailRepository.findByUserId(users.getId());
	    if(userDetail != null) {
	    	UserDetail ud = userDetail.get();
		    dto.setAccount(account);
		    dto.setName(ud.getName());
		    dto.setNickname(ud.getNickName());
		    dto.setShowNickname(ud.getShowNickName());
		    dto.setBirthday(ud.getBirthday());
		    dto.setShowBirthday(ud.getShowBirthday());
		    dto.setEmail(ud.getEmail());
		    dto.setShowEmail(ud.getShowEmail());
		    dto.setPhotoUrl(ud.getPhoto());
		    dto.setShowPhoto(ud.getShowPhoto());
	    }
	
	    return dto;
	}

	public UserDetail saveUserDetail(UserDetailDTO dto, UUID userId, HttpServletRequest request) {
		
		Users users = getUsersByAccount(dto.getAccount());
	    Optional<UserDetail> op = userDetailRepository.findByUserId(users.getId());
	    UserDetail ud = op.get();
	    ud.setName(dto.getName());
	    ud.setNickName(dto.getNickname());
	    ud.setShowNickName(dto.getShowNickname());
	    ud.setBirthday(dto.getBirthday());
	    ud.setShowBirthday(dto.getShowBirthday());
	    ud.setEmail(dto.getEmail());
	    ud.setShowEmail(dto.getShowEmail());
	    ud.setPhoto(dto.getPhotoUrl());
	    ud.setShowPhoto(dto.getShowPhoto());
		ud.setModifyTime(Instant.now());
		ud.setModifier(userId);
		ud.setModifyIp(IpUtil.getClientIp(request));
		
		userDetailRepository.save(ud);
		
		return null;
	}

	public List<UsersDTO> getAllUsersByTypes(List<Integer> types) {

		if(types.size() != 0) {
			
			List<UserDetail> listUsers = new ArrayList<>();
			
			if(types.contains(0)) {
				listUsers = userDetailRepository.findAll();
			}
			else {
				listUsers = userDetailRepository.findByRankIn(types);
			}
			
			List<UsersDTO> dto = new ArrayList<>();

			for (UserDetail user : listUsers) {
			    dto.add(new UsersDTO(user));
			}
			
	        return dto;
		}
		
		return null;
	}
}
    