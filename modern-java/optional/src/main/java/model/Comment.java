package model;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    private Posts posts;
    private Account account;
    private String content;
    private LocalDateTime createAt;
}
