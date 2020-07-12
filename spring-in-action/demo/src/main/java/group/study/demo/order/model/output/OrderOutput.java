package group.study.demo.order.model.output;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class OrderOutput {
    private Long no;

    private String userName;

    private String productName;

    private Integer productCount;

    @DateTimeFormat(pattern = "yyyy-MM-ddThh:mm:ss")
    private LocalDateTime orderDate;
}
