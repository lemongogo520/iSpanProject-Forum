package forum.board.coin;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "coin_transaction")
@Getter
@Setter
@NoArgsConstructor
public class CoinTransaction {

    @Id
    @Column(name = "id",nullable = false,columnDefinition = "uniqueidentifier")
    private UUID id;

    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wallet_id",nullable = false,referencedColumnName = "id")
    private CoinWallet wallet;

    
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type",nullable = false,length = 30,columnDefinition = "varchar(30)")
    private TransactionType transactionType;

    
    /*
     * 金額固定儲存正整數。
     * 收入或支出由 transactionType 判斷。
     */
    @Column(name = "amount",nullable = false,columnDefinition = "bigint")
    private Long amount;

    
    /*
     * 此筆交易完成後的餘額。
     */
    @Column(name = "balance_after",nullable = false,columnDefinition = "bigint")
    private Long balanceAfter;

    /*
     * 同一筆轉帳的轉入及轉出紀錄，
     * 會使用相同的 referenceId。
     */
    @Column(name = "reference_id",columnDefinition = "uniqueidentifier")
    private UUID referenceId;

    
    /*
     * 轉帳對象或執行調整的會員 ID。
     */
    @Column(name = "related_user_id",columnDefinition = "uniqueidentifier")
    private UUID relatedUserId;

    
    @Column(name = "description",length = 200,columnDefinition = "nvarchar(200)")
    private String description;

    @Column(name = "create_time",nullable = false,columnDefinition = "datetimeoffset(7)")
    private Instant createTime;

    @PrePersist
    public void onCreate() {

        if (id == null) {
            id = UUID.randomUUID();
        }

        createTime = Instant.now();
    }
}