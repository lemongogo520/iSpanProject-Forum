package forum.board.common.util;

import jakarta.servlet.http.HttpServletRequest;

public class IpUtil {

	public static String getClientIp(HttpServletRequest request) {

	    String ip = request.getHeader("X-Forwarded-For");

	    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
	        ip = request.getHeader("Proxy-Client-IP");
	    }

	    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
	        ip = request.getHeader("WL-Proxy-Client-IP");
	    }

	    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
	        ip = request.getRemoteAddr();
	    }

	    // X-Forwarded-For 可能有多個 IP
	    if (ip.contains(",")) {
	        ip = ip.split(",")[0].trim();
	    }

	    return ip;
	}
	
}
