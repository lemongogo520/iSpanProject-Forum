package forum.board.config;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/config")
public class ConfigController {
	
	private final ConfigSettingService configSettingService;
	
	@GetMapping("/settings")
	public List<SettingsDTO> getAllConfigSettings() {
	    return configSettingService.getAllSettings();
	}
	
	@PutMapping("/settings")
	public List<SettingsDTO> setAllConfigSettings(@RequestBody List<SettingsDTO> settings) {

	    System.out.println("收到設定數量：" + settings.size());
	    System.out.println("收到設定：" + settings);

	    return configSettingService.setAllSettings(settings);
	}
}
