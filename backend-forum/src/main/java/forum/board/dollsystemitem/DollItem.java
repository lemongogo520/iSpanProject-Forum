package forum.board.dollsystemitem;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

import forum.board.dollsystemcategories.DollCategory;

@Entity
@Table(name = "doll_items")
@Getter
@Setter
@NoArgsConstructor
public class DollItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JoinColumn(name = "category_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private DollCategory category;

    @Column(name = "item_name", length = 50)
    private String name;

    // 是否上架
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    //圖片路徑
    @Column(name = "file_path", length = 255)
    private String filePath;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }

        if (updatedAt == null) {
            updatedAt = Instant.now();
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = Instant.now();
    }
}
