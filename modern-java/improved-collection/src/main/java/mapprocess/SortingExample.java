package mapprocess;

import java.util.Map;

public class SortingExample {
    public static void main(String[] args) {
        Map<String, String> accounts = Map.ofEntries(
                Map.entry("abc1@kakao.com", "홍길동"),
                Map.entry("abc2@kakao.com", "김유신"),
                Map.entry("abc3@kakao.com", "이성계")
        );

        comparingByKey(accounts);  //키로 정렬
        comparingByValue(accounts);   // 값으로 정렬
    }

    public static void comparingByKey(Map<String, String> accounts) {
        System.out.println("**ComparingByKey**");
        accounts.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(System.out::println);
    }

    public static void comparingByValue(Map<String, String> accounts) {
        System.out.println("**ComparingByValue**");
        accounts.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEach(System.out::println);
    }
}
