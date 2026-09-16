package forum.board.dollsystemitem;

import lombok.RequiredArgsConstructor;
import forum.board.dollsystemcategories.DollCategory;
import forum.board.dollsystemcategories.DollCategoryRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class DollItemService {

    private final DollItemRepository iRepo;
    private final DollCategoryRepository cRepo;

    // 新增部件
    public DollItem addDollItem(DollItemDTO dollItemDTO, MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("上傳的圖片不能為空");
        }

        DollCategory categoryId = cRepo.findById(dollItemDTO.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("找不到對應的分類 ID: " + dollItemDTO.getCategoryId()));

        String originalFilename = file.getOriginalFilename();
        String contentType = file.getContentType();

        // 檢查 Content-Type 必須是 image/png，且副檔名必須是 .png
        boolean isPngContent = contentType != null && contentType.equals("image/png");
        boolean isPngExtension = originalFilename != null && originalFilename.toLowerCase().endsWith(".png");

        if (!isPngContent || !isPngExtension) {
            throw new IllegalArgumentException("檔案格式不符，請上傳真實的 PNG 圖片檔案！");
        }

        // 產生隨機檔名
        String newFilename = UUID.randomUUID().toString().substring(0, 8) + ".png";

        // 組合相對路徑跟存檔路徑
        String relativeFilePath = "doll_items_" + categoryId.getCode() + "/" + newFilename;

        // 抓取專案啟動時的真實根目錄 (絕對路徑)
        String projectDir = System.getProperty("user.dir");

        // 為了避免 user.dir 是在根目錄還是 backend-forum 目錄的差異
        Path basePath = projectDir.endsWith("backend-forum")
                ? Paths.get(projectDir, "../uploads/sprites")
                : Paths.get(projectDir, "uploads/sprites");

        Path targetFilePath = basePath.resolve(relativeFilePath).normalize();

        // 把檔案寫到存檔資料夾
        Files.createDirectories(targetFilePath.getParent());
        file.transferTo(targetFilePath.toAbsolutePath().toFile());

        DollItem dollItem = new DollItem();
        dollItem.setCategory(categoryId);
        dollItem.setName(dollItemDTO.getName());
        dollItem.setFilePath(relativeFilePath);
        dollItem.setIsActive(dollItem.getIsActive() != null ? dollItemDTO.getIsActive() : true);
        return iRepo.save(dollItem);
    }

    // 更改部件名稱
    public DollItem changeItemName(Integer itemId, String newName) {
        DollItem dollItem = iRepo.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("找不到對應的道具 ID: " + itemId));
        dollItem.setName(newName);
        return iRepo.save(dollItem);
    }

    // 更改上下架狀態
    public DollItem updateItemStatus(Integer itemId, boolean isActive) {
        DollItem dollItem = iRepo.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("找不到對應的道具 ID: " + itemId));
        dollItem.setIsActive(isActive);
        return iRepo.save(dollItem);
    }

    // 使用者檢視部件
    public List<DollItemResponseDTO> checkItemByUser(Integer categoryId) {
        List<DollItem> items = iRepo.findByCategoryIdAndIsActiveTrue(categoryId);
        return items.stream()
                .map(DollItemResponseDTO::fromEntity)
                .toList();
    }

    // 管理者檢視部件
    public List<DollItem> checkItemByAdmin(Integer catrdoryId) {
        return iRepo.findByCategoryId(catrdoryId);
    }

}
