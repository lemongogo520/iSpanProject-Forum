package forum.board.forumtemplate;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.genai.Client;

@Configuration
public class GeminiConfig {

    @Bean(destroyMethod = "close")
    Client geminiClient() {
        return new Client();
    }
}