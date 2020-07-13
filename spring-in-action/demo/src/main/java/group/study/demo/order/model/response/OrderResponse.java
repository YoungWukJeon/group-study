package group.study.demo.order.model.response;

import group.study.demo.product.model.response.ProductResponse;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private final Long orderNo;
    private final Long userNo;
    private final List<ProductResponse> products;
    private final Long totalPrice;

    @DateTimeFormat(pattern = "yyyy-MM-ddThh:mm:ss")
    private final LocalDateTime createDate;

    @DateTimeFormat(pattern = "yyyy-MM-ddThh:mm:ss")
    private final LocalDateTime updateDate;
}
