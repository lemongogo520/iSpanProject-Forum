package forum.board.file;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FileStoreRepository extends JpaRepository<FileStore, Long> {

	Optional<FileStore> findByFileId(UUID fileId);
}