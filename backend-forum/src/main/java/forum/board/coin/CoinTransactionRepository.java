package forum.board.coin;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoinTransactionRepository extends JpaRepository<CoinTransaction, UUID> {

    List<CoinTransaction>
        findByWalletIdOrderByCreateTimeDesc(
            UUID walletId
        );

    Page<CoinTransaction>
        findByWallet_IdOrderByCreateTimeDesc(
            UUID walletId,
            Pageable pageable
        );

    boolean existsByWalletIdAndTransactionTypeAndCreateTimeGreaterThanEqualAndCreateTimeLessThan(
            UUID walletId,
            TransactionType transactionType,
            Instant startTime,
            Instant endTime
        );

    Optional<CoinTransaction>
        findByWalletIdAndTransactionTypeAndReferenceId(
            UUID walletId,
            TransactionType transactionType,
            UUID referenceId
        );

    boolean existsByWalletIdAndTransactionTypeAndReferenceId(
            UUID walletId,
            TransactionType transactionType,
            UUID referenceId
        );
}