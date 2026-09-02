package forum.board.common.util;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class CookieUtil {

    public static String getCookieValue(
            HttpServletRequest request,
            String name) {

        Cookie[] cookies = request.getCookies();

        if(cookies == null){
            return null;
        }

        for(Cookie cookie : cookies){
            if(name.equals(cookie.getName())){
                return cookie.getValue();
            }
        }

        return null;
    }

}