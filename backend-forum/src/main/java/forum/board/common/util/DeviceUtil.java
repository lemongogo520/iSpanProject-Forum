package forum.board.common.util;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class DeviceUtil {

    public static String getDevice(HttpServletRequest request) {

        String ua = request.getHeader("User-Agent");

        if (ua == null)
            return "Unknown";

        if (ua.contains("Windows NT 10.0"))
            return "Windows 10/11";

        if (ua.contains("Windows NT 6.3"))
            return "Windows 8.1";

        if (ua.contains("Windows NT 6.1"))
            return "Windows 7";

        if (ua.contains("Android"))
            return "Android";

        if (ua.contains("iPhone"))
            return "iPhone";

        if (ua.contains("iPad"))
            return "iPad";

        if (ua.contains("Mac OS X"))
            return "Mac";

        if (ua.contains("Linux"))
            return "Linux";

        return "Unknown";
    }

}