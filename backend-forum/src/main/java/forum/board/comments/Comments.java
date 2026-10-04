package forum.board.comments;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import forum.board.post.Posts;
import forum.board.users.Users;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "post_comments")
@Getter
@Setter
@NoArgsConstructor
public class Comments {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id", nullable = false)
	private Posts post;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private Users users;

	@Column(columnDefinition = "nvarchar(max)")
	private String content;

	// 子樓留言
	// 如果這是一樓的留言，parentComment 就 null
	// 如果這是子樓留言，parentComment 就會指向被回覆的那條 Comments
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	@JsonIgnore
	private Comments parentComment;

	// 軟刪除狀態 (預設 false)
	@Column(name = "is_deleted", nullable = false)
	private Boolean isDeleted = false;

	// 是否由版主或管理員刪除
	@Column(name = "deleted_by_moderator")
	private Boolean deletedByModerator = false;

	// 管理員刪除留言的原因
	@Column(name = "delete_reason", length = 200)
	private String deleteReason;

	// 軟刪除時間
	@Column(name = "deleted_time")
	private Instant deletedTime;

	@Column(name = "create_time")
	private Instant createTime;

	@Column(name = "modify_time")
	private Instant modifyTime;

	@JsonProperty("userName")
	public String getUserName() {
		return users != null && users.getUsersDetail() != null
				? users.getUsersDetail().getName()
				: null;
	}

	@PrePersist
	public void createAt() {

		if (createTime == null) {
			createTime = Instant.now();
		}
	}

	public String getContent() {
		if (Boolean.TRUE.equals(this.isDeleted)) {
			if (Boolean.TRUE.equals(this.deletedByModerator)) {
				if (deleteReason != null &&
						!deleteReason.isBlank()) {
					return "此留言因「" + deleteReason + "」已被管理員刪除";
				}
				return "此留言因違反規章已被管理員刪除";
			}
			return "此留言已被使用者刪除";
		}
		return content;
	}

}
