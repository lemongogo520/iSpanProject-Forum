package forum.board.post;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PostImageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp"
    );

    @Value("${app.upload.image-dir:../uploads/img}")
    private String imageDirectory;

    public String saveImage(MultipartFile file) {

        // 檢查有沒有選擇檔案
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("請選擇圖片");
        }

        // 檢查檔案格式是否允許
        String contentType = file.getContentType();

        if (contentType == null ||
                !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new RuntimeException(
                    "只允許 JPG、PNG、GIF 或 WEBP 圖片"
            );
        }

        // 取得副檔名
        String extension = getExtension(contentType);

        // 使用 UUID 產生新檔名
        String fileName = UUID.randomUUID() + extension;

        // 取得圖片儲存路徑
        Path uploadPath = Paths.get(imageDirectory)
                .toAbsolutePath()
                .normalize();

        try {
            // 建立資料夾 uploads/img
            Files.createDirectories(uploadPath);

            Path targetPath = uploadPath
                    .resolve(fileName)
                    .normalize();

            // 檢查路徑是否在 uploads/img 資料夾內，避免路徑穿越攻擊
            if (!targetPath.startsWith(uploadPath)) {
                throw new RuntimeException("圖片儲存路徑錯誤");
            }

            // 將圖片存進資料夾
            file.transferTo(targetPath);

            return fileName;

        } catch (IOException e) {
            throw new RuntimeException("圖片儲存失敗", e);
        }
    }

    private String getExtension(String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            default -> throw new RuntimeException("不支援的圖片格式");
        };
    }
}