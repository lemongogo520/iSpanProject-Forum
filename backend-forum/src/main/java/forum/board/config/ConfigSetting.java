package forum.board.config;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "config_setting")
@Getter
@Setter
@NoArgsConstructor
public class ConfigSetting {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "item", nullable = false, unique = true, columnDefinition = "varchar(max)") 
    private String item; 

    @Column(name = "value", nullable = true, columnDefinition = "nvarchar(max)") 
    private String value; 

    @Column(name = "comment", nullable = true, columnDefinition = "nvarchar(max)") 
    private String comment; 
}