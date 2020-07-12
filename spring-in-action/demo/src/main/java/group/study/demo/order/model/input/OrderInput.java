package group.study.demo.order.model.input;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class OrderInput {
    @NotNull
    private Long no;
    @NotNull
    private Long userNo;
    @NotBlank
    private String productName;
    @NotNull
    private Integer productCount;
}
