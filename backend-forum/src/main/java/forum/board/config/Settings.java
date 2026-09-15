package forum.board.config;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Settings {

    private final ConfigSettingRepository repository;
    private Map<String, String> settings;

    public Settings(ConfigSettingRepository repository, List<ConfigSetting> configSettings) {
        this.repository = repository;
        reload(configSettings);
    }

    public String get(String item) {
        return settings.get(item);
    }

    public void set(String item, String value) {
        ConfigSetting configSetting = repository.findByItem(item)
                .orElseGet(ConfigSetting::new);

        configSetting.setItem(item);
        configSetting.setValue(value);

        repository.save(configSetting);

        reload(repository.findAll());
    }

    private void reload(List<ConfigSetting> configSettings) {
        settings = configSettings.stream()
                .collect(Collectors.toMap(
                        ConfigSetting::getItem,
                        ConfigSetting::getValue
                ));
    }
}