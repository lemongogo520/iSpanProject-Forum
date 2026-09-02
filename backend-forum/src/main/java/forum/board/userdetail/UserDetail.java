package forum.board.userdetail;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.UUID;

import forum.board.users.Users;

import java.time.LocalDate;
import java.time.Instant;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class UserDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; 

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private Users users;
	
    @Column(name = "user_id", nullable = false, columnDefinition = "uniqueidentifier") 
    private UUID userId; 

    @Column(name = "name", nullable = true, columnDefinition = "nvarchar(20)") 
    private String name; 

    @Column(name = "nick_name", nullable = true, columnDefinition = "nvarchar(20)") 
    private String nickName; 

    @Column(name = "show_nick_name", nullable = false, columnDefinition = "bit") 
    private Boolean showNickName; 

    @Column(name = "birthday", nullable = true, columnDefinition = "date") 
    private LocalDate birthday; 

    @Column(name = "show_birthday", nullable = false, columnDefinition = "bit") 
    private Boolean showBirthday; 

    @Column(name = "email", nullable = true, columnDefinition = "varchar(200)") 
    private String email; 

    @Column(name = "show_email", nullable = false, columnDefinition = "bit") 
    private Boolean showEmail; 

    @Column(name = "photo", nullable = true, columnDefinition = "varchar(max)") 
    private String photo; 
    
    @Column(name = "show_photo", nullable = false, columnDefinition = "bit") 
    private Boolean showPhoto; 

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

    @PrePersist
    public void onCreate() {

        createTime = Instant.now();

        if (showNickName == null) {
        	showNickName = false;
        }

        if (showBirthday == null) {
        	showBirthday = false;
        }
        
        if (showEmail == null) {
        	showEmail = false;
        }
        
        if (showPhoto == null) {
        	showPhoto = false;
        }
    
    }
    
}