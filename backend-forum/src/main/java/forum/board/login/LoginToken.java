package forum.board.login;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.UUID;

import forum.board.users.Users;

import java.time.Instant;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class LoginToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private Users users;
	
    @Column(name = "user_id", nullable = false, columnDefinition = "uniqueidentifier") 
    private UUID userId; 

    @Column(name = "token", nullable = false, columnDefinition = "varchar(64)") 
    private String token; 

    @Column(name = "expire_time", nullable = false, columnDefinition = "datetimeoffset(7)") 
    private Instant expireTime; 

    @Column(name = "last_access_time", nullable = true, columnDefinition = "datetimeoffset(7)") 
    private Instant lastAccessTime; 

    @Column(name = "last_access_ip", nullable = true, columnDefinition = "varchar(20)") 
    private String lastAccessIp; 

    @Column(name = "create_time", nullable = false, columnDefinition = "datetimeoffset(7)") 
    private Instant createTime; 

    @Column(name = "create_ip", nullable = true, columnDefinition = "varchar(20)") 
    private String createIp; 

    @Column(name = "active", nullable = true, columnDefinition = "bit") 
    private Boolean active; 

    @Column(name = "user_agent", nullable = true, columnDefinition = "nvarchar(500)") 
    private String userAgent; 

    @Column(name = "device_name", nullable = true, columnDefinition = "nvarchar(100)") 
    private String deviceName; 

    @Column(name = "browser_name", nullable = true, columnDefinition = "nvarchar(100)") 
    private String browserName;

    @Column(name = "revoke_time", nullable = true, columnDefinition = "datetimeoffset(7)") 
    private Instant revokeTime; 

    @Column(name = "revoke_reason", nullable = true, columnDefinition = "nvarchar(100)") 
    private String revokeReason; 
    
    @PrePersist
    public void onCreate() {

        createTime = Instant.now();
    }
}