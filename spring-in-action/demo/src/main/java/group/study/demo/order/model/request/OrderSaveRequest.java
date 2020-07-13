package group.study.demo.order.model.request;

import lombok.Data;

import java.util.List;

@Data
public class OrderSaveRequest {
    private final Long userNo;
    private final List<String> products;
}
