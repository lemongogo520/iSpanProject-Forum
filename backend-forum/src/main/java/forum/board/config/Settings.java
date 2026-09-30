package forum.board.config;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class Settings {

    private final ConfigSettingRepository repository;
    private Map<String, String> settings;

    public Settings(ConfigSettingRepository repository) {
        this.repository = repository;
        reload();
    }

    public String get(String item) {
        return settings.get(item);
    }

    public void set(String item, String value) {
        set(item, value, null);
    }

    public void set(String item, String value, String comment) {
        ConfigSetting configSetting = repository.findByItem(item)
                .orElseGet(ConfigSetting::new);

        configSetting.setItem(item);
        configSetting.setValue(value);

        if (comment != null) {
            configSetting.setComment(comment);
        }

        repository.save(configSetting);
        reload();
    }

    public Map<String, String> getAll() {
        reload();
        return Map.copyOf(settings);
    }

    private void reload() {
        settings = repository.findAll().stream()
                .collect(Collectors.toMap(
                        ConfigSetting::getItem,
                        ConfigSetting::getValue
                ));
    }
}