package group.study.demo.persistence.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
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
}
