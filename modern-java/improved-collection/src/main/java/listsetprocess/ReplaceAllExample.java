package listsetprocess;

import model.KakaoAccount;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class ReplaceAllExample {
    public static void main(String[] args) {
        List<KakaoAccount> accounts = new ArrayList<>();
        accounts.add(new KakaoAccount("abc1@kakao.com", "홍길동"));
        accounts.add(new KakaoAccount("abc2@kakao.com", "김유신"));
        accounts.add(new KakaoAccount("abc3@kakao.com", "홍길동"));
        accounts.add(new KakaoAccount("abc4@kakao.com", "이성계"));

        useListIterator(accounts);  // listIterator 이용
        useReplaceAll(accounts);    // replaceAll 이용
    }

    public static void useListIterator(List<KakaoAccount> accounts) {
        // 변경 전
        System.out.println(accounts);

        // 변경 후
        for (ListIterator<KakaoAccount> iterator = accounts.listIterator(); iterator.hasNext(); ) {
            KakaoAccount account = iterator.next();
            if ("홍길동".equals(account.getName())) {
                iterator.set(new KakaoAccount(account.getEmail(), "임꺽정"));
            }
        }
        System.out.println(accounts);
    }

    public static void useReplaceAll(List<KakaoAccount> accounts) {
        // 변경 전
        System.out.println(accounts);

        // 변경 후
        accounts.replaceAll(e -> {
            if ("홍길동".equals(e.getName())) {
                return new KakaoAccount(e.getEmail(), "임꺽정");
            }
            return e;
        });
        System.out.println(accounts);
    }
}
