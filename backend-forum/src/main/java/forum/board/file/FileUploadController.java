package forum.board.file;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import forum.board.config.Settings;
import forum.board.enums.FileModule;
import forum.board.file.FileService.FileInfo;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/file")
public class FileUploadController {

	private final FileService fileService;
	
	private final Settings settings;

    @PostMapping("upload")
    public ResponseEntity<?> upload(@RequestParam MultipartFile file, @RequestParam FileModule fileModule) {

        try {
        	
            FileService.FileInfo result = fileService.saveFile(file, fileModule);
            settings.set("login_logo", result.fileId().toString());
            
            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());

        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body("檔案儲存失敗");
        }
    }
    
    @GetMapping("/files/{fileId}")
    public ResponseEntity<?> getFile(@PathVariable UUID fileId) {

        try {
            Resource resource = fileService.getFileResource(fileId);

            String contentType = Files.probeContentType(
                    resource.getFile().toPath()
            );

            return ResponseEntity.ok()
                    .contentType(
                        contentType != null
                            ? MediaType.parseMediaType(contentType)
                            : MediaType.APPLICATION_OCTET_STREAM
                    )
                    .body(resource);

        } catch (FileNotFoundException e) {
            return ResponseEntity.notFound().build();

        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/info/{fileId}")
    public FileInfo getFileInfo(@PathVariable UUID fileId) {
        return fileService.getFileInfo(fileId);
    }
}
