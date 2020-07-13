package group.study.demo.product.model.request;

import lombok.Data;

@Data
public class ProductSaveRequest {
    private final String name;
    private final String category;
    private final String description;
    private final Long price;
}
