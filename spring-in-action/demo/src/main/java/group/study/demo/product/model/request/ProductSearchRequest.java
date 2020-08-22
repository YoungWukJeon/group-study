package group.study.demo.product.model.request;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
public class ProductSearchRequest {
    private Integer pageNum = 1;
    @Max(value = 10)
    @Min(value = 2)
    private Integer pageSize = 2;
}