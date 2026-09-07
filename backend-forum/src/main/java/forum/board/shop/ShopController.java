package forum.board.shop;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/shop")
public class ShopController {

    private final ShopService shopService;

    public ShopController(
            ShopService shopService) {

        this.shopService = shopService;
    }

    // =========================================
    // 商品查詢
    // =========================================

    /**
     * 查詢所有已上架商品
     *
     * GET /api/shop/products
     *
     * 分類查詢：
     * GET /api/shop/products?type=CLOTHES
     */
    @GetMapping("/products")
    public List<ShopProductResponse> getProducts(
            @RequestParam(required = false)
            ProductType type) {

        if (type != null) {

            return shopService
                    .getProductsByType(type);
        }

        return shopService
                .getActiveProducts();
    }

    /**
     * 查詢單一商品
     *
     * GET /api/shop/products/{productId}
     */
    @GetMapping("/products/{productId}")
    public ShopProductResponse getProduct(
            @PathVariable UUID productId) {

        return shopService
                .getProduct(productId);
    }

    // =========================================
    // 商品購買
    // =========================================

    /**
     * 購買商品
     *
     * POST
     * /api/shop/products/{productId}/purchase
     * ?userId={userId}
     */
    @PostMapping(
            "/products/{productId}/purchase")
    public PurchaseResponse purchase(
            @PathVariable UUID productId,
            @RequestParam UUID userId) {

        return shopService.purchase(
                userId,
                productId);
    }

    // =========================================
    // 訂單查詢
    // =========================================

    /**
     * 查詢會員的商城訂單
     *
     * GET
     * /api/shop/orders?userId={userId}
     */
    @GetMapping("/orders")
    public List<ShopOrderResponse> getMyOrders(
            @RequestParam UUID userId) {

        return shopService
                .getMyOrders(userId);
    }
    
    /**
     * 商城訂單退款
     *
     * POST
     * /api/shop/orders/{orderId}/refund
     * ?userId={userId}
     */
    @PostMapping("/orders/{orderId}/refund")
    public RefundResponse refund(
            @PathVariable UUID orderId,
            @RequestParam UUID userId) {

        return shopService.refund(
                userId,
                orderId
        );
    }
    
    
}