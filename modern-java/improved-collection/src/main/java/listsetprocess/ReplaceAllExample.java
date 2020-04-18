package listsetprocess;

import model.Account;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class ReplaceAllExample {
    public static void main(String[] args) {
        List<Account> accounts = new ArrayList<>();
        accounts.add(new Account("abc1@kakao.com", "홍길동"));
        accounts.add(new Account("abc2@kakao.com", "김유신"));
        accounts.add(new Account("abc3@kakao.com", "홍길동"));
        accounts.add(new Account("abc4@kakao.com", "이성계"));

        useListIterator(accounts);  // listIterator 이용
        useReplaceAll(accounts);    // replaceAll 이용
    }

    public static void useListIterator(List<Account> accounts) {
        // 변경 전
        System.out.println(accounts);

        // 변경 후
        for (ListIterator<Account> iterator = accounts.listIterator(); iterator.hasNext(); ) {
            Account account = iterator.next();
            if ("홍길동".equals(account.getName())) {
                iterator.set(new Account(account.getEmail(), "임꺽정"));
            }
        }
        System.out.println(accounts);
    }

    public static void useReplaceAll(List<Account> accounts) {
        // 변경 전
        System.out.println(accounts);

        // 변경 후
        accounts.replaceAll(e -> {
            if ("홍길동".equals(e.getName())) {
                return new Account(e.getEmail(), "임꺽정");
            }
            return e;
        });
        System.out.println(accounts);
    }
}
