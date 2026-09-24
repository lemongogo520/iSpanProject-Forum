package forum.board.dollsystemuserequip;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import forum.board.dollsystemcategories.DollCategory;
import forum.board.dollsystemcategories.DollCategoryRepository;
import forum.board.dollsystemitem.DollItem;
import forum.board.dollsystemitem.DollItemRepository;
import forum.board.dollsystemitem.DollItemResponseDTO;
import forum.board.dollsystemuserowneditem.DollUserOwnedRepository;
import forum.board.users.Users;
import forum.board.users.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DollUserEquipService {

    private final UsersRepository usersRepo;
    private final DollItemRepository dollItemRepo;
    private final DollUserEquipRepository dollUserEquipRepo;
    private final DollCategoryRepository dollCategoryRepo;
    private final DollUserOwnedRepository dollUserOwnedRepo;

    @Transactional
    public void updateEquipItems(UUID userId, List<Integer> itemIds) {
        // 驗證user是否存在
        Users user = usersRepo.findById(userId).orElseThrow(() -> new RuntimeException("找不到使用者"));

        // 清空使用者裝備紀錄
        dollUserEquipRepo.deleteByUserId(userId);

        // 如果前端傳的資料是空的，結束
        if (itemIds == null || itemIds.isEmpty()) {
            return;
        }

        // 撈出前端傳來的裝備
        List<DollItem> items = dollItemRepo.findAllById(itemIds);

        // 組裝穿著紀錄
        List<DollUserEquip> newEquipItems = items.stream().map(item -> {
            DollUserEquip equitems = new DollUserEquip();
            equitems.setUser(user);
            equitems.setCategory(item.getCategory());
            equitems.setItem(item);
            return equitems;
        }).collect(Collectors.toList());

        dollUserEquipRepo.saveAll(newEquipItems);
    }

    // 檢視穿著紀錄
    public List<DollItemResponseDTO> getUserEquip(UUID userId) {
        List<DollUserEquip> equipList = dollUserEquipRepo.findByUserId(userId);

        // 如果使用者有儲存紀錄，直接回傳
        if (equipList != null && !equipList.isEmpty()) {
            return equipList.stream()
                    .map(newEquip -> DollItemResponseDTO.fromEntity(newEquip.getItem()))
                    .collect(Collectors.toList());
        }

        // 如果沒有儲存紀錄，找出所有圖層裝備，回傳該圖層使用者擁有的第一個裝備作為預設
        List<DollCategory> requiredCategories = dollCategoryRepo.findAll();
        List<DollItemResponseDTO> defaultEquip = new ArrayList<>();

        for (DollCategory cat : requiredCategories) {
            List<DollItem> ownItems = dollUserOwnedRepo.findItemsByUserIdAndCategoryId(userId, cat.getId());

            if (ownItems != null && !ownItems.isEmpty()) {
                DollItem defaultItem = ownItems.get(0);
                defaultEquip.add(DollItemResponseDTO.fromEntity(defaultItem));
            }

        }
        return defaultEquip;
    }

}
