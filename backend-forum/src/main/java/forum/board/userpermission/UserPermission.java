package forum.board.userpermission;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.UUID;

import forum.board.permissions.Permissions;
import forum.board.users.Users;

import java.time.Instant;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class UserPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; 

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private Users users;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", insertable = false, updatable = false)
    private Permissions permissions;
    
    @Column(name = "user_id", nullable = false, columnDefinition = "uniqueidentifier") 
    private UUID userId; 

    @Column(name = "permission_id", nullable = false, columnDefinition = "int") 
    private Integer permissionId; 

    @Column(name = "create_time", nullable = false, columnDefinition = "datetimeoffset(7)") 
    private Instant createTime; 

    @Column(name = "creator", nullable = false, columnDefinition = "uniqueidentifier") 
    private UUID creator; 

    @Column(name = "create_ip", nullable = true, columnDefinition = "varchar(20)") 
    private String createIp; 

    @Column(name = "modify_time", nullable = true, columnDefinition = "datetimeoffset(7)") 
    private Instant modifyTime; 

    @Column(name = "modifier", nullable = true, columnDefinition = "uniqueidentifier") 
    private UUID modifier; 

    @Column(name = "modify_ip", nullable = true, columnDefinition = "varchar(20)") 
    private String modifyIp; 
}