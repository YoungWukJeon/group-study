package model.optional;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Posts {
    private Optional<Account> account;
    private String title;
    private LocalDateTime createAt;
}
