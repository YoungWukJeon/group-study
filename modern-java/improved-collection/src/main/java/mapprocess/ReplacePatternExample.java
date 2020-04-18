package mapprocess;

import model.Account;

import java.util.HashMap;
import java.util.Map;

public class ReplacePatternExample {
    public static void main(String[] args) {
        Map<Account, String> emails = new HashMap<>();
        Account[] accounts = {
                new Account(null, "mike", "1"),
                new Account(null, "jane", "1"),
                new Account(null, "john", "1"),
                new Account(null, "mike", "2")
        };

        // 초기화
        for (Account account : accounts) {
            emails.compute(account, (k, v) -> k.getName() + "@kakao.com");
        }

        useReplaceAll(emails);  // replaceAll
//        useReplace(emails);   // replace
    }

    public static void useReplaceAll(Map<Account, String> emails) {
        emails.replaceAll((account, email) -> email.toUpperCase());
        emails.forEach((k, v) -> System.out.println(k + "->" + v));
    }

    public static void useReplace(Map<Account, String> emails) {
        emails.replace(new Account(null, "john", "1"), "john@kakao.com(changed)");
        emails.forEach((k, v) -> System.out.println(k + "->" + v));
    }
}
