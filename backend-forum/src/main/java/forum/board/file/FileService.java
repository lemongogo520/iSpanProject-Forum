package forum.board.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import forum.board.enums.FileModule;

@Service
public class FileService {

	private final FileStoreRepository fileStoreRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public FileService(FileStoreRepository fileStoreRepository) {
        this.fileStoreRepository = fileStoreRepository;
    }

    public FileInfo saveFile(MultipartFile file, FileModule module) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("檔案不能為空");
        }

        String fileName = file.getOriginalFilename();

        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("檔名不能為空");
        }

        String extension = getExtension(fileName);

        UUID fileId = UUID.randomUUID();

        Path uploadPath = Paths.get(uploadDir).resolve(module.name().toLowerCase());

        Files.createDirectories(uploadPath);

        String storedFileName = fileId + "." + extension;

        Path targetPath = uploadPath.resolve(storedFileName);

        Files.copy(
            file.getInputStream(),
            targetPath,
            StandardCopyOption.REPLACE_EXISTING
        );

        FileStore fileStore = new FileStore();

        fileStore.setFileId(fileId);
        fileStore.setFileName(fileName);
        fileStore.setUploadTime(OffsetDateTime.now());
        fileStore.setFileModule(module.name());
        fileStore.setFilePath(uploadPath.toString());

        fileStoreRepository.save(fileStore);

        return new FileInfo(
            fileId,
            fileName,
            extension
        );
    }

    private String getExtension(String fileName) {

        int index = fileName.lastIndexOf('.');

        if (index == -1) {
            return "";
        }

        return fileName.substring(index + 1).toLowerCase();
    }

    public record FileInfo(
        UUID fileId,
        String fileName,
        String extension
    ) {}
}