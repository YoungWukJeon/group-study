package group.study.demo.persistence.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table("product")
public class ProductEntity implements Persistable<Long> {
    @Id
    private Long no;
    private String name;
    private String category;
    private String description;
    private Long price;
    private String image;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    @Transient
    private boolean newUser;

    @Override
    public Long getId() {
        return no;
    }

    @Override
    @Transient
    public boolean isNew() {
        return this.newUser || no == null;
    }

    @Builder
    @PersistenceConstructor
    public ProductEntity(Long no, String name, String category,
                         String description, Long price, String image,
                         LocalDateTime createDate, LocalDateTime updateDate) {
        this.no = no;
        this.name = name;
        this.category = category;
        this.description = description;
        this.price = price;
        this.image = image;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }
}