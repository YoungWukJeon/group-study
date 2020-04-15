package listsetprocess;

import model.KakaoAccount;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RemoveIfExample {
    public static void main(String[] args) {
        List<KakaoAccount> accounts = new ArrayList<> ();
        accounts.add(new KakaoAccount("abc1@kakao.com", "홍길동"));
        accounts.add(new KakaoAccount("abc2@kakao.com", "김유신"));
        accounts.add(new KakaoAccount("abc3@kakao.com", "홍길동"));
        accounts.add(new KakaoAccount("abc4@kakao.com", "이성계"));

//        useForEach(accounts);   // for-each 이용
//        useIterator(accounts);  // iterator 이용
        useRemoveIf(accounts);  // removeIf 이용
    }

    public static void useForEach(List<KakaoAccount> accounts) {
        for (KakaoAccount account : accounts) {
            if ("홍길동".equals(account.getName())) {
                accounts.remove(account);
            }
        }
        System.out.println(accounts);
    }

    public static void useIterator(List<KakaoAccount> accounts) {
        for (Iterator<KakaoAccount> iterator = accounts.iterator(); iterator.hasNext(); ) {
            KakaoAccount account = iterator.next();
            if ("홍길동".equals(account.getName())) {
                iterator.remove();
            }
        }
        System.out.println(accounts);
    }

    public static void useRemoveIf(List<KakaoAccount> accounts) {
        accounts.removeIf(e -> "홍길동".equals(e.getName()));
        System.out.println(accounts);
    }
}
