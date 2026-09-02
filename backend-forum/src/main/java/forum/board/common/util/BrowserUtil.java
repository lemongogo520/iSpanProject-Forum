package forum.board.common.util;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class BrowserUtil {

    public static String getBrowser(HttpServletRequest request) {

        String ua = request.getHeader("User-Agent");

        if (ua == null)
            return "Unknown";

        if (ua.contains("Edg"))
            return "Microsoft Edge";

        if (ua.contains("Chrome"))
            return "Google Chrome";

        if (ua.contains("Firefox"))
            return "Firefox";

        if (ua.contains("Safari"))
            return "Safari";

        return "Unknown";
    }
}