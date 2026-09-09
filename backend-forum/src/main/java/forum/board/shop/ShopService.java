package forum.board.shop;

import java.util.List;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import forum.board.coin.CoinService;



@Service
public class ShopService {

    private final ShopProductRepository shopProductRepository;
    private final ShopOrderRepository shopOrderRepository;
    private final CoinService coinService;

    public ShopService(
            ShopProductRepository shopProductRepository,
            ShopOrderRepository shopOrderRepository,
            CoinService coinService) {

        this.shopProductRepository = shopProductRepository;
        this.shopOrderRepository = shopOrderRepository;
        this.coinService = coinService;
    }

    // =========================================
    // 商品查詢
    // =========================================

    /**
     * 查詢所有已上架商品
     */
    public List<ShopProductResponse> getActiveProducts() {

        return shopProductRepository
                .findByActiveTrueOrderByCreateTimeDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * 依照商品類型查詢已上架商品
     */
    public List<ShopProductResponse> getProductsByType(
            ProductType productType) {

        return shopProductRepository
                .findByProductTypeAndActiveTrueOrderByCreateTimeDesc(
                        productType)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * 查詢單一已上架商品
     */
    public ShopProductResponse getProduct(UUID productId) {

        ShopProduct product = shopProductRepository
                .findByIdAndActiveTrue(productId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "商品不存在或已下架"));

        return toResponse(product);
    }
    
    /**
     * 查詢會員商城訂單
     */
    public List<ShopOrderResponse> getMyOrders(
            UUID userId) {

        return shopOrderRepository
                .findByUserIdOrderByCreateTimeDesc(userId)
                .stream()
                .map(this::toOrderResponse)
                .toList();
    }
    

    // =========================================
    // 商品購買
    // =========================================

    
    @Transactional
    public PurchaseResponse purchase(
            UUID userId,
            UUID productId) {

        // 1. 查詢商品
        ShopProduct product = shopProductRepository
                .findByIdAndActiveTrue(productId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "商品不存在或已下架"));

        // 2. 檢查商品價格
        if (product.getPrice() == null
                || product.getPrice() <= 0) {

            throw new RuntimeException(
                    "商品價格設定錯誤");
        }

        // 3. 防止同一會員重複購買同一商品
        boolean alreadyPurchased =
                shopOrderRepository
                        .existsByUserIdAndProductIdAndStatus(
                                userId,
                                productId,
                                OrderStatus.COMPLETED);

        if (alreadyPurchased) {
            throw new RuntimeException(
                    "你已經購買過此商品");
        }

        // 4. 建立訂單
        ShopOrder order = new ShopOrder();

        order.setUserId(userId);
        order.setProductId(product.getId());
        order.setProductName(product.getName());
        order.setPrice(product.getPrice());
        order.setStatus(OrderStatus.PENDING);

        order = shopOrderRepository.save(order);

        // 5. 呼叫 CoinService 扣點數
        // orderId 會存入 CoinTransaction.referenceId
        coinService.purchase(
                userId,
                order.getId(),
                product.getPrice());

        // 6. 扣款成功，訂單改為完成
        order.setStatus(OrderStatus.COMPLETED);

        order = shopOrderRepository.save(order);

        // 7. Entity 轉 PurchaseResponse DTO
        return toPurchaseResponse(order);
    }
    
    
    @Transactional
    public RefundResponse refund(
            UUID userId,
            UUID orderId) {

        // 1. 找到這個會員的訂單
        ShopOrder order =
                shopOrderRepository
                        .findByIdAndUserId(
                                orderId,
                                userId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "訂單不存在"
                                )
                        );

        // 2. 確認訂單是不是已完成
        if (order.getStatus()
                != OrderStatus.COMPLETED) {

            if (order.getStatus()
                    == OrderStatus.REFUNDED) {

                throw new RuntimeException(
                        "此訂單已經退款"
                );
            }

            throw new RuntimeException(
                    "此訂單目前無法退款"
            );
        }

        // 3. 確認價格
        if (order.getPrice() == null
                || order.getPrice() <= 0) {

            throw new RuntimeException(
                    "訂單金額錯誤"
            );
        }

        // 4. 呼叫 CoinService 加回點數
        coinService.refund(
                userId,
                order.getId(),
                order.getPrice()
        );

        // 5. 訂單改為已退款
        order.setStatus(
                OrderStatus.REFUNDED
        );

        order =
                shopOrderRepository.save(order);

        // 6. 回傳 DTO
        RefundResponse response =
                new RefundResponse();

        response.setOrderId(
                order.getId()
        );

        response.setProductId(
                order.getProductId()
        );

        response.setProductName(
                order.getProductName()
        );

        response.setRefundAmount(
                order.getPrice()
        );

        response.setStatus(
                order.getStatus().name()
        );

        return response;
    }
        
    
    

    // =========================================
    // DTO 轉換
    // =========================================

   
    private ShopProductResponse toResponse(
            ShopProduct product) {

        ShopProductResponse response =
                new ShopProductResponse();

        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setProductType(product.getProductType());
        response.setImageUrl(product.getImageUrl());
        response.setStock(product.getStock());

        return response;
    }

    
    private PurchaseResponse toPurchaseResponse(
            ShopOrder order) {

        PurchaseResponse response =
                new PurchaseResponse();

        response.setOrderId(order.getId());
        response.setProductId(order.getProductId());
        response.setProductName(order.getProductName());
        response.setPrice(order.getPrice());
        response.setStatus(order.getStatus().name());

        return response;
    }
    
    /**
     * ShopOrder Entity
     * ↓
     * ShopOrderResponse DTO
     */
    private ShopOrderResponse toOrderResponse(
            ShopOrder order) {

        ShopOrderResponse response =
                new ShopOrderResponse();

        response.setOrderId(order.getId());

        response.setProductId(
                order.getProductId());

        response.setProductName(
                order.getProductName());

        response.setPrice(
                order.getPrice());

        response.setStatus(
                order.getStatus().name());

        response.setCreateTime(
                order.getCreateTime());

        return response;
    }
    
    
    
    
    
}