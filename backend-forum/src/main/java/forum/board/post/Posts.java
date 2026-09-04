package forum.board.post;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import forum.board.board.Board;
import forum.board.comments.Comments;
import forum.board.users.Users;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "forum_posts")
@Getter
@Setter
@NoArgsConstructor
public class Posts {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "board_id", nullable = false)
	private Board board;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private Users users;

	@Column(name = "post_type", nullable = false, length = 20)
	private String postType;

	private String title;

	@Column(name = "content", columnDefinition = "nvarchar(max)")
	private String content;

	@Column(nullable = false)
	private Integer views = 0;

	@Column(name = "create_time")
	private Instant createTime;

	@Column(name = "modify_time")
	private Instant modifyTime;

	@Column(name = "is_deleted", nullable = false)
	private Boolean isDeleted = false;

	@Column(name = "deleted_time")
	private Instant deletedTime;

	@OneToMany(mappedBy = "post")
	private List<Comments> comments = new ArrayList<>();

	@PrePersist
	public void createAt() {

		if (createTime == null) {
			createTime = Instant.now();
		}
	}

	public Boolean getIsDeleted() {
		return isDeleted;
	}

}
