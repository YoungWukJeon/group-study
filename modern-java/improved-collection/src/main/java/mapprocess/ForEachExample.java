package mapprocess;

import java.util.Map;

public class ForEachExample {
    public static void main(String[] args) {
        Map<String, String> accounts = Map.ofEntries(
                Map.entry("abc1@kakao.com", "홍길동"),
                Map.entry("abc2@kakao.com", "김유신"),
                Map.entry("abc3@kakao.com", "이성계")
        );

        useIterator(accounts);  // Iterator 이용
        useForEach(accounts);   // Java8 forEach 이용
    }

    public static void useIterator(Map<String, String> accounts) {
        for (Map.Entry<String, String> entry: accounts.entrySet()) {
            String email = entry.getKey();
            String name = entry.getValue();
            System.out.println(email + "'s name is " + name);
        }
    }

    public static void useForEach(Map<String, String> accounts) {
        accounts.forEach((email, name) ->
                System.out.println(email + "'s name is " + name)
        );
    }
}
