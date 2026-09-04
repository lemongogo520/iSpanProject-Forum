package forum.board.coin;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CoinTransactionRepository extends JpaRepository<CoinTransaction, UUID>{
	
	List<CoinTransaction>findByWalletIdOrderByCreateTimeDesc(UUID walletId);
	
	
	 boolean existsByWalletIdAndTransactionTypeAndCreateTimeGreaterThanEqualAndCreateTimeLessThan(
	            UUID walletId,
	            TransactionType transactionType,
	            Instant startTime,
	            Instant endTime);

}
