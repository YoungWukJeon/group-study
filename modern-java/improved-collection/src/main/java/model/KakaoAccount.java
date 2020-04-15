package model;

import lombok.Data;

@Data
public class KakaoAccount {
    private String email;
    private String name;

    public KakaoAccount(String email, String name) {
        this.email = email;
        this.name = name;
    }
}
