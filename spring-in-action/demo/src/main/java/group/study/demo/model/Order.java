package group.study.demo.model;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
public class Order {
    private Long no;
    private Long userNo;
    @NotEmpty(message = "not exist product name")
    @Length(min = 5, message = "123123123")
    private String productName;
    private Integer productCount;
    private LocalDateTime orderDate;
}
