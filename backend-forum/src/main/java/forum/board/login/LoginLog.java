package forum.board.login;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.time.Instant;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class LoginLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; 

    @Column(name = "user_id", nullable = true, columnDefinition = "uniqueidentifier") 
    private UUID userId; 

    @Column(name = "status", nullable = true, columnDefinition = "varchar(20)") 
    private String status; 

    @Column(name = "login_time", nullable = true, columnDefinition = "datetimeoffset(7)") 
    private Instant loginTime; 

    @Column(name = "login_ip", nullable = true, columnDefinition = "varchar(20)") 
    private String loginIp; 


    @PrePersist
    public void onCreate() {

    	loginTime = Instant.now();
    }
    
}