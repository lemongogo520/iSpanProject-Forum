package forum.board.chat.message;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.Nationalized;

import forum.board.chat.conversation.ConversationEntity;
import forum.board.users.Users;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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
@Table(name = "messages", indexes = {
		@Index(name = "idx_msg_conv_id", columnList = "conversation_id, id DESC")
}, uniqueConstraints = {
		@UniqueConstraint(name = "ux_msg_client", columnNames = { "conversation_id", "client_msg_id" })
})
public class MessagesEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "conversation_id", updatable = false, nullable = false)
	private ConversationEntity conversation;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", updatable = false, nullable = false)
	private ChatType type;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sender_id", updatable = false, nullable = false)
	private Users sender;

	@Nationalized
	@Column(name = "content", length = 2000)
	private String content;

	@Column(name = "create_time", nullable = false)
	private Instant createTime;

	@PrePersist
	public void onCreate() {
		if (this.createTime == null)
			this.createTime = Instant.now();
		if (this.clientMsgId == null)
			this.clientMsgId = UUID.randomUUID().toString();

	}

	@Column(name = "update_time")
	private Instant updateTime;

	@PreUpdate
	public void onUpdate() {
		this.updateTime = Instant.now();
	}

	@Column(name = "delete_time")
	private Instant deleteTime;

	@Column(name = "client_msg_id", length = 36, updatable = false, nullable = false)
	private String clientMsgId;

	public enum ChatType {
		TEXT,
		IMAGE,
		FILE,
		SYSTEM
	}
}
