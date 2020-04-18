package model;

import lombok.Data;

@Data
public class Account {
    private String email;
    private String name;
    private String id;

    public Account(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public Account(String email, String name, String id) {
        this.email = email;
        this.name = name;
        this.id = id;
    }

    @Override
    public int hashCode() {
        return email != null? email.hashCode(): (name + "." + id + "@kakao.com").hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        Account account = (Account) obj;

        if (id.equals(account.getId()) && name.equals(account.getName())) {
            return true;
        }

        return false;
    }
}
