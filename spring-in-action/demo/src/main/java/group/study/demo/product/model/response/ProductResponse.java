package group.study.demo.product.model.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductResponse {
    private final Long productNo;
    private final String name;
    private final String category;
    private final String description;
    private final Long price;
    private final LocalDateTime createDate;
    private final LocalDateTime updateDate;
}
