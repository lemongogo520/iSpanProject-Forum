package forum.board.chat.ws;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

public class UserIdHandshakeHandler extends DefaultHandshakeHandler {
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
            Map<String, Object> attributes) {
        UUID userId = (UUID) attributes.get("loginUserId");
        String name = userId.toString();
        System.out.println("determineUser 被呼叫 userId=" + userId);
        return () -> name;
    }
}
