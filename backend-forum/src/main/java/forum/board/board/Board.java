package forum.board.board;

import java.time.Instant;

import forum.board.category.Category;
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
@Table(name = "forum_board")
@Getter
@Setter
@NoArgsConstructor
public class Board {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="category_id",nullable = false)
	private Category category;
	
	private String name;
	
	private String description;
	
	private Integer rank=0;
	
	@Column(name="create_time")
	private Instant createTime;
	
	@PrePersist
	public void createAt() {
		
		if(createTime==null) {
			createTime=Instant.now();
		}
	}
}
