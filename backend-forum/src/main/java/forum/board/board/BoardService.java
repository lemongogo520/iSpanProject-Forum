package forum.board.board;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import forum.board.category.Category;
import forum.board.category.CategoryRepository;
import forum.board.category.CategoryService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {

	private final BoardRepository boardRepo;
	
	private final CategoryService categoryService; 

	// 所有項目
	public List<Board> findAll() {

		return boardRepo.findAll();
	}

	// 用id查項目
	public Board findById(Integer id) {

		Optional<Board> opBoard = boardRepo.findById(id);

		if (opBoard.isEmpty()) {
			return null;
		}

		return opBoard.get();
	}

	// 新增項目
	public Board createBoard(Board board) {

		// 檢查有沒有同樣名稱的項目
		Optional<Board> opBoard = boardRepo.findByName(board.getName());
		if (opBoard.isPresent()) {
			return null;
		}
		// 檢查有沒有填值
		 if (board.getCategory() == null || board.getCategory().getId() == null) {
	            return null;
	        }

		//檢查有沒有分類
		Category category = categoryService.findById(board.getCategory().getId());
		
        if (category == null) {
            return null;
        }

		Board newBoard = new Board();
		newBoard.setName(board.getName());
		newBoard.setDescription(board.getDescription());
		newBoard.setRank(board.getRank());
		newBoard.setCategory(category); 

		return boardRepo.save(newBoard);
	}

	// 根據類別查詢項目
	public List<Board> findByCategoryId(Integer categoryId) {

		return boardRepo.findByCategoryId(categoryId);
	}

	// 刪除項目
	public void delete(Integer id) {

		boardRepo.deleteById(id);
	}

	// 更新項目
	public Board updateBoard(Board board) {
		Optional<Board> opBoard = boardRepo.findById(board.getId());
		if (opBoard.isEmpty()) {
			return null;
		}
		Board newBoard = opBoard.get();
		newBoard.setName(board.getName());
		newBoard.setDescription(board.getDescription());
		newBoard.setRank(board.getRank());
		return boardRepo.save(newBoard);
	}

}
