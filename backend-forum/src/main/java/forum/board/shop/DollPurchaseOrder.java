package forum.board.shop;

import java.time.Instant;
import java.util.UUID;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "doll_purchase_order", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "item_id" }))
@Getter
@Setter
@NoArgsConstructor
public class DollPurchaseOrder {
    @Id
    @Column(columnDefinition = "uniqueidentifier")
    private UUID id;
    @Column(name = "user_id", nullable = false, columnDefinition = "uniqueidentifier")
    private UUID userId;
    @Column(name = "item_id", nullable = false)
    private Integer itemId;
    @Column(name = "item_name", nullable = false, length = 100)
    private String itemName;
    @Column(nullable = false)
    private Long price;
    @Column(name = "created_at", nullable = false, columnDefinition = "datetimeoffset(7)")
    private Instant createdAt;
}
