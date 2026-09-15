package forum.board.chat.message;

import java.time.Instant;
import java.util.UUID;

import forum.board.chat.conversation.ConversationEntity;
import forum.board.users.Users;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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

	@ManyToOne (fetch = FetchType.LAZY)
	@JoinColumn (name = "conversation_id", updatable = false, nullable = false)
	private ConversationEntity conversation;
	
	@Column(name = "type",updatable = false, nullable = false)
	private ChatType type;
	
	@ManyToOne (fetch = FetchType.LAZY)
	@JoinColumn (name = "sender_id",updatable = false, nullable = false)
	private Users sender;
	
	@Column(name = "content")
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
