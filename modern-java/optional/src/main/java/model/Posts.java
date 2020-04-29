package model;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Posts {
    private Account account;
    private String title;
    private LocalDateTime createAt;
}
