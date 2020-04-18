package listsetprocess;

import model.Account;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RemoveIfExample {
    public static void main(String[] args) {
        List<Account> accounts = new ArrayList<> ();
        accounts.add(new Account("abc1@kakao.com", "홍길동"));
        accounts.add(new Account("abc2@kakao.com", "김유신"));
        accounts.add(new Account("abc3@kakao.com", "홍길동"));
        accounts.add(new Account("abc4@kakao.com", "이성계"));

//        useForEach(accounts);   // for-each 이용
//        useIterator(accounts);  // iterator 이용
        useRemoveIf(accounts);  // removeIf 이용
    }

    public static void useForEach(List<Account> accounts) {
        for (Account account : accounts) {
            if ("홍길동".equals(account.getName())) {
                accounts.remove(account);
            }
        }
        System.out.println(accounts);
    }

    public static void useIterator(List<Account> accounts) {
        for (Iterator<Account> iterator = accounts.iterator(); iterator.hasNext(); ) {
            Account account = iterator.next();
            if ("홍길동".equals(account.getName())) {
                iterator.remove();
            }
        }
        System.out.println(accounts);
    }

    public static void useRemoveIf(List<Account> accounts) {
        accounts.removeIf(e -> "홍길동".equals(e.getName()));
        System.out.println(accounts);
    }
}
