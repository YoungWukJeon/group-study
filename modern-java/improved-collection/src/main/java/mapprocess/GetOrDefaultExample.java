package mapprocess;

import java.util.Map;

public class GetOrDefaultExample {
    public static void main(String[] args) {
        Map<String, String> accounts = Map.ofEntries(
                Map.entry("abc1@kakao.com", "홍길동"),
                Map.entry("abc2@kakao.com", "김유신"),
                Map.entry("abc3@kakao.com", "이성계")
        );

        useGetOrDefault(accounts);
    }

    public static void useGetOrDefault(Map<String, String> accounts) {
        System.out.println(accounts.get("abc@kakao.com"));  // 없는 키 조회
        System.out.println(accounts.getOrDefault("abc@kakao.com", "-"));    // 없는 키 조회
    }
}