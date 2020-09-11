package group.study.demo.persistence.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table("product")
public class ProductEntity {
    @Id
    private Long no;
    private String name;
    private String category;
    private String description;
    private Long price;
    private String image;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    @Builder
//    @PersistenceConstructor
    public ProductEntity(String name, String category,
                         String description, Long price, String image,
                         LocalDateTime createDate, LocalDateTime updateDate) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.price = price;
        this.image = image;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }
}
