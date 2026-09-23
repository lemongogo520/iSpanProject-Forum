package forum.board.file;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import forum.board.config.Settings;
import forum.board.enums.FileModule;
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
}
