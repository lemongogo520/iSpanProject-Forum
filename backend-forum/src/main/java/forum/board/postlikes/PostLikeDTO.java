package forum.board.postlikes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class PostLikeDTO {

	// 文章目前的按讚總數
	private final long likeCount;
	
	// 目前使用者是否已按讚
	private final boolean likedByCurrentUser;
	
}
