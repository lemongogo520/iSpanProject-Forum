package forum.board.coin;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import forum.board.users.Users;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "coin_wallet",uniqueConstraints = 
          {@UniqueConstraint(name = "UK_coin_wallet_user_id",columnNames = "user_id")})
@Getter
@Setter
@NoArgsConstructor
public class CoinWallet {

    @Id
    @Column(name = "id",nullable = false,columnDefinition = "uniqueidentifier")
    private UUID id;

    
    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id",nullable = false,referencedColumnName = "id")
    private Users user;
    

    @Column(name = "balance",nullable = false,columnDefinition = "bigint")
    private Long balance;

    /*
     * 防止兩個請求同時修改餘額時互相覆蓋。
     */
    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    
    @Column(name = "create_time",nullable = false,columnDefinition = "datetimeoffset(7)")
    private Instant createTime;

    @Column(name = "modify_time",columnDefinition = "datetimeoffset(7)")
    private Instant modifyTime;

    @PrePersist
    public void onCreate() {

        if (id == null) {
            id = UUID.randomUUID();
        }

        if (balance == null) {
            balance = 0L;
        }

        createTime = Instant.now();
    }

    @PreUpdate
    public void onUpdate() {
        modifyTime = Instant.now();
    }
}