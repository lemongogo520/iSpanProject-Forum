package forum.board.login;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import forum.board.common.util.BrowserUtil;
import forum.board.common.util.CookieUtil;
import forum.board.common.util.DeviceUtil;
import forum.board.common.util.HashUtil;
import forum.board.common.util.IpUtil;
import forum.board.enums.LogType;
import forum.board.users.Users;
import forum.board.users.UsersRepository;
import forum.board.users.UsersService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginService {
	
	private final LoginLogRepository loginLogRepository;
	
	private final LoginTokenRepository loginTokenRepository;

	private final UsersRepository usersRepository;
	
	private final UsersService usersService;
	
	public String generateLoginToken(HttpServletRequest request, String account, String token) {
		
		if(token != null) {
			Optional<LoginToken> opToken = loginTokenRepository.findLoginTokenByToken(HashUtil.sha256(token));
			if(opToken != null) {
				LoginToken loginToken = opToken.get();

				if(loginToken.getExpireTime().isAfter(Instant.now())
						&& loginToken.getActive()) {

					//建立新的token取代原來的
					String newToken = UUID.randomUUID().toString();
					loginToken.setToken(HashUtil.sha256(newToken));
					loginToken.setExpireTime(Instant.now().plus(30, ChronoUnit.DAYS));
					loginToken.setLastAccessTime(Instant.now());
					loginToken.setLastAccessIp(IpUtil.getClientIp(request));

					loginTokenRepository.save(loginToken);
					return newToken;
				}
				
				if(loginToken.getActive()) {
					//過期處理
					if(loginToken.getExpireTime().isBefore(Instant.now())) {
						loginToken.setActive(false);
						loginToken.setRevokeTime(Instant.now());
						loginToken.setRevokeReason("TOKEN_EXPIRED");
					}
				}
			}
		}

		String rememberToken = UUID.randomUUID().toString();
		token = rememberToken;
		String ua = request.getHeader("User-Agent");
		String device = DeviceUtil.getDevice(request);
		
		LoginToken loginToken = new LoginToken();
		loginToken.setUserId(usersService.getUsersByAccount(account).getId());

		loginToken.setToken(HashUtil.sha256(rememberToken));
		loginToken.setExpireTime(Instant.now().plus(30, ChronoUnit.DAYS));
		loginToken.setLastAccessTime(Instant.now());
		loginToken.setLastAccessIp(IpUtil.getClientIp(request));
		loginToken.setCreateIp(IpUtil.getClientIp(request));
		loginToken.setActive(true);
		loginToken.setDeviceName(device);
		loginToken.setUserAgent(ua);
		loginToken.setBrowserName(BrowserUtil.getBrowser(request));
		
		loginTokenRepository.save(loginToken);
		
		return rememberToken;
	}

	public Users checkRememberToken(HttpServletRequest request, String tokenName) {
		
		String token = CookieUtil.getCookieValue(request, tokenName);

	    if(token == null){
	        return null;
	    }

	    String hash = HashUtil.sha256(token);

	    LoginToken loginToken = loginTokenRepository.findLoginTokenByToken(hash).orElse(null);
	    if(loginToken == null){
	        return null;
	    }

	    if(!loginToken.getActive()){
	        return null;
	    }

	    if(loginToken.getExpireTime().isBefore(Instant.now())){
	        return null;
	    }
		
	    return usersRepository.findById(loginToken.getUserId()).orElse(null);
	}
	
	public LoginToken getLoginDTO(String token) {

	    String hash = HashUtil.sha256(token);
	    LoginToken loginToken = loginTokenRepository.findLoginTokenByToken(hash).orElse(null);
		return loginToken;
	}
	
	public void updateTokenActive(String token, boolean active) {
	    String hash = HashUtil.sha256(token);
	    LoginToken loginToken = loginTokenRepository.findLoginTokenByToken(hash).orElse(null);
	    System.out.println(loginToken);
	    if(loginToken != null) {
		    loginToken.setActive(active);
		    loginTokenRepository.save(loginToken);
	    }
	}
	
	public void insertLoginLog(HttpServletRequest request, UUID user_id, LogType logType) {
		
		LoginLog loginLog = new LoginLog();
		loginLog.setUserId(user_id);
		loginLog.setStatus(logType.toString());
		loginLog.setLoginIp(IpUtil.getClientIp(request));
		
		loginLogRepository.save(loginLog);
		
	}
}
    