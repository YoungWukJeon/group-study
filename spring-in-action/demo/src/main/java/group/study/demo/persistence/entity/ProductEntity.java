package group.study.demo.persistence.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name = "product")
@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "no")
    private Long no;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "category", nullable = false, length = 50)
    private String category;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "price", nullable = false)
    private Long price;
    @Column(name = "create_date", nullable = false)
    private LocalDateTime createDate;
    @Column(name = "update_date", nullable = false)
    private LocalDateTime updateDate;
}
