package forum.board.conversation;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import forum.board.users.Users;

public interface ConversationUserRepository extends JpaRepository<Users, UUID>{
    Optional<Users> findByAccount(String account);
}
