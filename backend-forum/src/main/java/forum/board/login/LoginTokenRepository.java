package forum.board.login;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginTokenRepository extends JpaRepository<LoginToken, Integer> {
	Optional<LoginToken> findLoginTokenByToken(String hashToken);
}
    