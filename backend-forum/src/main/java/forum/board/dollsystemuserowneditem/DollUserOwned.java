package forum.board.dollsystemuserowneditem;

import java.time.Instant;

import forum.board.dollsystemitem.DollItem;
import forum.board.users.Users;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "doll_user_owned", uniqueConstraints = {
        // 確保同一位使用者，不會重複擁有同一件裝備
        @UniqueConstraint(columnNames = { "user_id", "item_id" })
})
@Getter
@Setter
@NoArgsConstructor
public class DollUserOwned {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private DollItem item;

    @Column(name = "acquired_at", nullable = false, updatable = false)
    private Instant acquiredAt;

    @PrePersist
    public void onCreate() {
        if (acquiredAt == null) {
            acquiredAt = Instant.now();
        }
    }
}
