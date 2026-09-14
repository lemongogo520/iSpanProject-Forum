package forum.board.users;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.Instant;

@Entity
@Table(
    uniqueConstraints = {
        @UniqueConstraint(
            name="UK_users_account",
            columnNames="account"
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
public class Users {

    @Id
    @Column(name = "id", columnDefinition = "uniqueidentifier") 
    private UUID id; 

    @Column(name = "account", nullable = false, columnDefinition = "varchar(20)") 
    private String account; 

    @JsonIgnore
    @Column(name = "password", nullable = false, columnDefinition = "varchar(255)") 
    private String password; 

    @Column(name = "active", nullable = false, columnDefinition = "bit") 
    private Boolean active; 

    @Column(name = "reset", nullable = false, columnDefinition = "bit") 
    private Boolean reset; 

    @Column(name = "create_time", nullable = false, columnDefinition = "datetimeoffset(7)") 
    private Instant createTime; 

    @Column(name = "creator", nullable = false, columnDefinition = "uniqueidentifier") 
    private UUID creator; 

    @Column(name = "create_ip", nullable = false, columnDefinition = "varchar(20)") 
    private String createIp; 

    @Column(name = "modify_time", nullable = true, columnDefinition = "datetimeoffset(7)") 
    private Instant modifyTime; 

    @Column(name = "modifier", nullable = true, columnDefinition = "uniqueidentifier") 
    private UUID modifier; 

    @Column(name = "modify_ip", nullable = true, columnDefinition = "varchar(20)") 
    private String modifyIp; 

    @JsonIgnore
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "users")
    @PrimaryKeyJoinColumn 
	private UserDetail usersDetail;

    @PrePersist
    public void onCreate() {

        createTime = Instant.now();

        if (active == null) {
            active = true;
        }

        if (reset == null) {
            reset = false;
        }
    }
}