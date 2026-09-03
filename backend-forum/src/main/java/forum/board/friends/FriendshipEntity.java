package forum.board.friends;

import java.time.Instant;

import forum.board.users.Users;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table( name="friendship",
		uniqueConstraints = @UniqueConstraint(columnNames = {"user1_id","user2_id"}))
public class FriendshipEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id", nullable = false)
    private Users user1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id", nullable = false)
    private Users user2;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private FriendshipStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private Users requester;
    
    @Column(name = "create_time", nullable = false)
    private Instant createTime;
    
    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
    	if(this.createTime==null) {
    		this.createTime = now;
    	}
        this.updateTime = now;
    }
    
    @Column(name = "update_time", nullable = false)
    private Instant updateTime;
    
    @PreUpdate
    public void preUpdate() {
    	this.updateTime = Instant.now();	
    }
    
    public enum FriendshipStatus {
        PENDING
        , ACCEPTED
        // , REJECTED
        // , BLOCKED
    }
}
	
	

