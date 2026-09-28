package forum.board.dollsystemuserowneditem;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import forum.board.dollsystemitem.DollItem;
import forum.board.dollsystemitem.DollItemRepository;
import forum.board.dollsystemitem.DollItemResponseDTO;
import forum.board.users.Users;
import forum.board.users.UsersRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DollUserOwnedService {

    private final DollUserOwnedRepository ownedRepo;
    private final DollItemRepository itemRepo;
    private final UsersRepository usersRepo;

    // 取得「特定分類」下玩家已擁有的裝備 (回傳 DTO 給前端)
    public List<DollItemResponseDTO> getOwnedItemsByCategory(UUID userId, Integer categoryId) {
        List<DollItem> items = ownedRepo.findItemsByUserIdAndCategoryId(userId, categoryId);

        // 將實體類別轉換為 DTO 陣列回傳給前端
        return items.stream()
                .map(DollItemResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // 取得玩家「所有圖層」的擁有裝備 (供前端隨機換裝使用)
    public List<DollItemResponseDTO> getAllOwnedItems(UUID userId) {
        List<DollItem> items = ownedRepo.findItemsByUserId(userId);
        return items.stream()
                .map(DollItemResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // 取得特定圖層的所有裝備(並標記使用者是否擁有)
    public List<DollItemResponseDTO> getAllItemsWithOwnedStatus(UUID userId, Integer categoryId) {

        // 找出所有已上架的裝備
        List<DollItem> allActiveItems = itemRepo.findByCategoryIdAndIsActiveTrue(categoryId);

        // 找出使用者已經有的裝備
        List<DollItem> ownedItems = ownedRepo.findItemsByUserIdAndCategoryId(userId, categoryId);

        // 把已有裝備ID抽出來變成一個Set
        Set<Integer> ownedItemsIds = ownedItems.stream()
                .map(DollItem::getId)
                .collect(Collectors.toSet());

        // 把裝備一筆一筆拿出來檢查
        return allActiveItems.stream()
                .map(item -> {

                    // 檢查這件裝備的ID有沒有在使用者已有裝備ID裡面
                    boolean isOwned = ownedItemsIds.contains(item.getId());

                    // 把裝備轉成DTO
                    return DollItemResponseDTO.fromEntity(item, isOwned);
                })
                .collect(Collectors.toList());
    }

    // 商城檢視 (分頁版，並標記使用者是否擁有)
    public Page<DollItemResponseDTO> getShopItemsByPage(UUID userId, Integer categoryId, Integer pageNumber) {

        // 1. 分頁設定
        Pageable pgb = PageRequest.of(pageNumber - 1, 8, Sort.Direction.DESC, "createdAt");

        // 2. 查詢該分頁的所有上架裝備
        org.springframework.data.domain.Page<DollItem> page = itemRepo.findByCategoryIdAndIsActiveTrue(categoryId, pgb);

        // 3. 找出使用者已經有的裝備 (用來比對)
        List<DollItem> ownedItems = ownedRepo.findItemsByUserIdAndCategoryId(userId, categoryId);
        Set<Integer> ownedItemsIds = ownedItems.stream()
                .map(DollItem::getId)
                .collect(Collectors.toSet());

        // 4. 將 Page<DollItem> 轉換為 Page<DollItemResponseDTO>，並塞入 isOwned
        return page.map(item -> {
            boolean isOwned = ownedItemsIds.contains(item.getId());
            return DollItemResponseDTO.fromEntity(item, isOwned);
        });
    }

    // 使用者獲得裝備
    public void acquireItem(UUID userId, Integer itemId) {

        if (ownedRepo.existsByUserIdAndItemId(userId, itemId)) {
            throw new RuntimeException("已擁有此裝備");
        }

        Users user = usersRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("找不到使用者"));

        DollItem item = itemRepo.findById(itemId)
                .orElseThrow(() -> new RuntimeException("找不到裝備"));

        DollUserOwned dollUserOwned = new DollUserOwned();
        dollUserOwned.setUser(user);
        dollUserOwned.setItem(item);

        ownedRepo.save(dollUserOwned);
    }

    // 使用者一次獲得多件裝備 (批次新增)
    public void acquireItems(UUID userId, List<Integer> itemIds) {
        Users user = usersRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("找不到使用者"));

        // 找出所有傳入 ID 的裝備實體
        List<DollItem> itemsToAcquire = itemRepo.findAllById(itemIds);
        List<DollUserOwned> newOwnedItems = new ArrayList<>();

        for (DollItem item : itemsToAcquire) {
            // 檢查是否已經擁有了，沒有才加入新增清單
            if (!ownedRepo.existsByUserIdAndItemId(userId, item.getId())) {
                DollUserOwned dollUserOwned = new DollUserOwned();
                dollUserOwned.setUser(user);
                dollUserOwned.setItem(item);
                newOwnedItems.add(dollUserOwned);
            }
        }

        // 批次儲存，大幅提升效能並減少資料庫連線次數
        if (!newOwnedItems.isEmpty()) {
            ownedRepo.saveAll(newOwnedItems);
        }
    }

}
