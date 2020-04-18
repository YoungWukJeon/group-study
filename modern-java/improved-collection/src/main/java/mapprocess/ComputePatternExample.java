package mapprocess;

import model.Account;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ComputePatternExample {
    public static void main(String[] args)  {
        Map<Account, String> emails = new HashMap<> ();
        Account[] accounts = {
                new Account(null, "mike", "1"),
                new Account(null, "jane", "1"),
                new Account(null, "john", "1"),
                new Account(null, "mike", "2"),
                new Account(null, "john", "1")
        };

        Map<String, Integer> wordCounters = new HashMap<> ();
        String sentence = "a group of words, usually containing a verb, " +
                "that expresses a thought in the form of a statement, question, instruction, " +
                "or exclamation and starts with a capital letter when written.";

        System.out.println("**computeIfAbsent**");
        useComputeIfAbsent(emails, accounts);    // name과 id를 통해 이메일 양식 만들기
        System.out.println("\n**computeIfPresent**");
        useComputeIfPresent(wordCounters, sentence);    // 문장에서 단어별 빈도 수 구하기
        System.out.println("\n**compute**");
        useCompute(emails, accounts);   // name으로 이메일 양식 만들기
    }

    public static void useComputeIfAbsent(Map<Account, String> emails, Account[] accounts) {
        for (Account account : accounts) {
            emails.computeIfAbsent(account, k -> {
                System.out.println("working: " + k);
                return k.getName() + "." + k.getId() + "@kakao.com";
            });
        }
        emails.forEach((account, email) -> System.out.println(account + "->" + email));
    }

    public static void useComputeIfPresent(Map<String, Integer> wordCounters, String sentence) {
        List<String> words =
                List.of(sentence.split(" "))   // 공백 단위로 구분
                        .stream()
                        .map(s -> s.replaceAll("\\.", "").replaceAll(",", ""))    // 간단한 전처리
                        .collect(Collectors.toList());

        for (String word : words) {
            wordCounters.computeIfAbsent(word, k -> 0); // 단어별 초깃값을 주기 위해 computeIfAbsent 이용
            wordCounters.computeIfPresent(word, (k, v) -> v + 1);
        }

        wordCounters.forEach((k, v) -> System.out.println(k + "->" + v));
    }

    public static void useCompute(Map<Account, String> emails, Account[] accounts) {
        for (Account account : accounts) {
            emails.compute(account, (k, v) -> {
                System.out.println("working: " + k);
                return k.getName() + "@kakao.com";
            });
        }
        emails.forEach((k, v) -> System.out.println(k + "->" + v));
    }
}
