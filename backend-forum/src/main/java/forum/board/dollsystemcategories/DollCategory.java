package forum.board.dollsystemcategories;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name="doll_categories")
@Getter
@Setter
@NoArgsConstructor
public class DollCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false,unique = true, length = 50)
    private String code;

    @Column(name = "type_name",nullable = false,length = 50)
    private String typeName;

    @Column(name = "z_index",nullable = false)
    private Integer zIndex;

    @Column(name = "is_required",nullable = false)
    private Boolean isRequired;

    @Column(name="created_at",nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    public void onCreate() {
        if(createdAt == null) {
            createdAt = Instant.now();
        }

        if (updatedAt == null) {
            updatedAt = Instant.now();
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = Instant.now();
    }

}
