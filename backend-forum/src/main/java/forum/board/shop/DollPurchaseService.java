package forum.board.shop;

import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import forum.board.coin.CoinService;
import forum.board.dollsystemitem.DollItem;
import forum.board.dollsystemuserowneditem.DollUserOwnedRepository;
import forum.board.dollsystemuserowneditem.DollUserOwnedService;
import forum.board.users.Users;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DollPurchaseService {
    private final EntityManager entityManager;
    private final CoinService coinService;
    private final DollUserOwnedRepository ownedRepository;
    private final DollUserOwnedService ownedService;

    public record PurchaseResponse(UUID orderId, Integer itemId, String itemName,
            long price, long balanceAfter, String status) {
    }

    @Transactional
    public PurchaseResponse purchase(UUID userId, Integer itemId) {
        if (userId == null || itemId == null || itemId <= 0) {
            throw new IllegalArgumentException("會員或商品編號不正確");
        }
        // 與每日任務相同，先鎖會員，再處理錢包。同會員購買依序執行。
        Users user = entityManager.find(Users.class, userId, LockModeType.PESSIMISTIC_WRITE);
        if (user == null)
            throw new IllegalArgumentException("找不到會員");
        DollItem item = entityManager.find(DollItem.class, itemId, LockModeType.PESSIMISTIC_WRITE);
        if (item == null || !Boolean.TRUE.equals(item.getIsActive())) {
            throw new IllegalArgumentException("商品不存在或已下架");
        }
        if (ownedRepository.existsByUserIdAndItemId(userId, itemId)) {
            throw new IllegalArgumentException("你已擁有此道具，請重新整理背包");
        }
        Integer configuredPrice = item.getPrice();
        if (configuredPrice == null || configuredPrice < 0) {
            throw new IllegalArgumentException("商品價格設定錯誤");
        }
        long price = configuredPrice.longValue();
        var wallet = coinService.getOrCreateWallet(userId);
        if (wallet.getBalance() == null || wallet.getBalance() < price) {
            throw new IllegalArgumentException("點數不足");
        }
        DollPurchaseOrder order = new DollPurchaseOrder();
        order.setId(UUID.randomUUID());
        order.setUserId(userId);
        order.setItemId(itemId);
        order.setItemName(item.getName());
        order.setPrice(price);
        order.setCreatedAt(Instant.now());
        entityManager.persist(order);
        // 零點商品保留訂單與背包紀錄，不呼叫只允許正數的扣點方法。
        if (price > 0)
            coinService.purchase(userId, order.getId(), price);
        ownedService.acquireItem(userId, itemId);
        // 在回傳前檢查唯一限制、錢包版本；任一失敗整筆交易回滾。
        entityManager.flush();
        return new PurchaseResponse(order.getId(), itemId, item.getName(), price,
                wallet.getBalance(), "COMPLETED");
    }
}
