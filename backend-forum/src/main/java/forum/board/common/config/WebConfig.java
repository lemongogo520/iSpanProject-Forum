package forum.board.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

        private final LoginInterceptor loginInterceptor;

        @Override
        public void addInterceptors(InterceptorRegistry registry) {

                registry.addInterceptor(loginInterceptor)
                                .addPathPatterns("/**")
                                .excludePathPatterns(
                                                "/login",
                                                "/register",
                                                "/css/**",
                                                "/js/**",
                                                "/images/**",
                                                "/uploads/**",
                                                "/getfile",
                                                "/api/**",
                                                "/sprites/**");
        }

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {

                String projectDir = System.getProperty("user.dir");
                
                // 為了避免 user.dir 是在根目錄還是 backend-forum 目錄的差異
                java.nio.file.Path basePath = projectDir.endsWith("backend-forum") 
                        ? java.nio.file.Paths.get(projectDir, "../uploads/sprites") 
                        : java.nio.file.Paths.get(projectDir, "uploads/sprites");
                        
                String spritesPath = basePath.normalize().toUri().toString();
                // Spring Boot 的 addResourceLocations 資料夾路徑「必須」以斜線結尾
                if (!spritesPath.endsWith("/")) {
                        spritesPath += "/";
                }

                registry.addResourceHandler("/uploads/**")
                                .addResourceLocations("file:../uploads/");

                registry.addResourceHandler("/sprites/**")
                                .addResourceLocations(spritesPath);
        }
}