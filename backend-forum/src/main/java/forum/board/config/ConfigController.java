package forum.board.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import forum.board.enums.FileModule;
import forum.board.file.FileService;
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

	    return configSettingService.setAllSettings(settings);
	}
}
