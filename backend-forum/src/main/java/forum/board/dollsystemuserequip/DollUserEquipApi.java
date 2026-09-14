package forum.board.dollsystemuserequip;

import org.springframework.web.bind.annotation.RestController;

import forum.board.dollsystemitem.DollItemResponseDTO;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequiredArgsConstructor
public class DollUserEquipApi {

    private final DollUserEquipService dService;

    //檢視穿著api
    @GetMapping("/api/dollitem/equipped/{userId}")
    public ResponseEntity<List<DollItemResponseDTO>> getEquipItems(@PathVariable UUID userId) {

        List<DollItemResponseDTO> userEquip = dService.getUserEquip(userId);

        return ResponseEntity.ok(userEquip);
    }

    //新增或更新穿著api
    @PutMapping("/api/dollitem/newequip/{userId}")
    public ResponseEntity<List<DollItemResponseDTO>> newEquipItems(
            @PathVariable UUID userId,
            @RequestBody List<Integer> itemIds) {

        //把穿著紀錄放到後端
        dService.updateEquipItems(userId, itemIds);

        //重新檢視穿著紀錄
        List<DollItemResponseDTO> userEquip = dService.getUserEquip(userId);

        return ResponseEntity.ok(userEquip);
    }
}
