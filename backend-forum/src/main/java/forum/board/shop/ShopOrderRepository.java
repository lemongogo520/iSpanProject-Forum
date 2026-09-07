package forum.board.shop;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopOrderRepository
        extends JpaRepository<ShopOrder, UUID> {

    List<ShopOrder>
        findByUserIdOrderByCreateTimeDesc(UUID userId);

    boolean existsByUserIdAndProductIdAndStatus(
        UUID userId,
        UUID productId,
        OrderStatus status      
    );
    
    Optional<ShopOrder> findByIdAndUserId(
            UUID id,
            UUID userId
    ); 
    
    
    
    
    
}