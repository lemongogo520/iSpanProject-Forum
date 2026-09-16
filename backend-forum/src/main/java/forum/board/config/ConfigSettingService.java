package forum.board.config;

import java.util.List;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConfigSettingService {

    private final Settings settings;

    public List<SettingsDTO> getAllSettings() {
        return settings.getAll().entrySet().stream()
                .map(entry -> {
                    SettingsDTO dto = new SettingsDTO();
                    dto.setItem(entry.getKey());
                    dto.setValue(entry.getValue());
                    return dto;
                })
                .toList();
    }

	public List<SettingsDTO> setAllSettings(List<SettingsDTO> settingsList) {
	    settingsList.forEach(setting ->
	        settings.set(setting.getItem(), setting.getValue())
	    );
	
	    return getAllSettings();
	}
}