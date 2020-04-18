package mapprocess;

import model.Account;

import java.util.HashMap;
import java.util.Map;

public class RemovePatternExample {
    public static void main(String[] args) {
        Map<Account, String> emails = new HashMap<> ();
        Account[] accounts = {
                new Account(null, "mike", "1"),
                new Account(null, "jane", "1"),
                new Account(null, "john", "1"),
                new Account(null, "mike", "2"),
                new Account(null, "john", "1")
        };

        // 초기화
        for (Account account : accounts) {
            emails.compute(account, (k, v) -> k.getName() + "@kakao.com");
        }

        emails.forEach((k, v) -> System.out.println(k + "->" + v));
        emails.remove(new Account(null, "john", "1"), "john@kakao.com");   // 키, 값을 통해 항목 제거
        System.out.println("\n**After removing**");
        emails.forEach((k, v) -> System.out.println(k + "->" + v));
    }
}
