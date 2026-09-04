package forum.board.coin;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import forum.board.userpermission.UserPermission;

public interface CoinUserPermissionRepository extends JpaRepository<UserPermission, Integer> {
	
	 boolean existsByUserIdAndPermissionsNameIgnoreCase(UUID userId,String permissionName);
	 
	 
	 

}
    
