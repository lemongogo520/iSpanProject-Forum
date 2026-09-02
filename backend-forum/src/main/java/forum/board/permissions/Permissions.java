package forum.board.permissions;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class Permissions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; 

    @Column(name = "name", nullable = true, columnDefinition = "nvarchar(20)") 
    private String name; 

    @Column(name = "rank", nullable = false, columnDefinition = "int") 
    private Integer rank; 

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