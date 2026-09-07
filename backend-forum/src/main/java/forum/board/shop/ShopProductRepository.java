package forum.board.shop;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopProductRepository
        extends JpaRepository<ShopProduct, UUID> {

    List<ShopProduct> findByActiveTrueOrderByCreateTimeDesc();

    List<ShopProduct>
        findByProductTypeAndActiveTrueOrderByCreateTimeDesc(
            ProductType productType);

    Optional<ShopProduct>findByIdAndActiveTrue(UUID id);
    
}