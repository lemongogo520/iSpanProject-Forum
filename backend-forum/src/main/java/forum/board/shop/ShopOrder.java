package forum.board.shop;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "shop_order")
public class ShopOrder {

    @Id
    @Column(name = "id", columnDefinition = "uniqueidentifier")
    private UUID id;

    @Column(
        name = "user_id",
        nullable = false,
        columnDefinition = "uniqueidentifier"
    )
    private UUID userId;

    @Column(
        name = "product_id",
        nullable = false,
        columnDefinition = "uniqueidentifier"
    )
    private UUID productId;

    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    @Column(name = "price", nullable = false, columnDefinition = "bigint")
    private Long price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatus status;

    @Column(
        name = "create_time",
        nullable = false,
        columnDefinition = "datetimeoffset(7)"
    )
    private Instant createTime;

    @PrePersist
    public void onCreate() {

        if (id == null) {
            id = UUID.randomUUID();
        }

        if (status == null) {
            status = OrderStatus.PENDING;
        }

        createTime = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Instant getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }
}