package forum.board.message;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="messages")
public class MessagesEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; 
	
	@Column(name = "type",updatable = false, nullable = false)
	private ChatType type;
	
	@Column(name = "sender",updatable = false, nullable = false)
	private UUID senderId;
	
	private String content;
	
	@Column(name = "create_time", nullable = false)
	private Instant createTime;
	@PrePersist
    public void onCreate() {
    	if(this.createTime == null) {
    		this.createTime = Instant.now();
    	}
	}

	@Column(name = "update_time")
	private Instant updateTime;
	public void onUpdate(){
		if(this.updateTime == null) {
    		this.updateTime = Instant.now();
    	}
	}

	private Instant deleteTime;
	
	public enum ChatType{
		TEXT,
		IMAGE,
		FILE,
		SYSTEM
	}	
}
