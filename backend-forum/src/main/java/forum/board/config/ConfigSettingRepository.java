package forum.board.config;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigSettingRepository extends JpaRepository<ConfigSetting, Integer> {

	Optional<ConfigSetting> findByItem(String item);
}