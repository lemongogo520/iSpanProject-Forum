package forum.board.coin;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CoinWalletRepository
        extends JpaRepository<CoinWallet, UUID> {

    Optional<CoinWallet> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);
}