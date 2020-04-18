package mapprocess;

import model.Account;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MergeExample {
    public static void main(String[] args) {
        Map<String, Account> accounts1 = Map.ofEntries(
            Map.entry("mike@kakao.com", new Account(null, "mike", "1")),
            Map.entry("jane@kakao.com", new Account(null, "jane", "1"))
        );
        Map<String, Account> accounts2 = Map.ofEntries(
                Map.entry("john@kakao.com", new Account(null, "john", "1")),
                Map.entry("mike@kakao.com", new Account(null, "mike", "2"))
        );

        Map<String, Integer> wordCounters = new HashMap<> ();
        String sentence = "a group of words, usually containing a verb, " +
                "that expresses a thought in the form of a statement, question, instruction, " +
                "or exclamation and starts with a capital letter when written.";

//        usePutAll(accounts1, accounts2);    // 고전적인 방법. 키가 중복될 때, 값이 이후에 추가된 값으로 치환됨
        useMerge(accounts1, accounts2); // Map의 merge 메서드를 이용해서 중복된 키가 있을 때, 값 처리 가능
//        useMergeAnother(wordCounters, sentence);    // ComputePattern에서 ComputeIfPresent 예제와 같은 단어의 개수 세기
    }

    public static void usePutAll(Map<String, Account> accounts1, Map<String, Account> accounts2) {
        Map<String, Account> totalAccounts = new HashMap<> (accounts1);
        totalAccounts.putAll(accounts2);
        totalAccounts.forEach((email, account) -> System.out.println(email + "->" + account));
    }

    public static void useMerge(Map<String, Account> accounts1, Map<String, Account> accounts2) {
        Map<String, Account> totalAccounts = new HashMap<> (accounts1);
        accounts2.forEach((email, account) ->
                totalAccounts.merge(email, account, (account1, account2) ->
                    new Account(account1.getEmail(), account1.getName(), account1.getId() + "&" + account2.getId())
                )
        );
        totalAccounts.forEach((email, account) -> System.out.println(email + "->" + account));
    }

    public static void useMergeAnother(Map<String, Integer> wordCounters, String sentence) {
        List<String> words =
                List.of(sentence.split(" "))   // 공백 단위로 구분
                        .stream()
                        .map(s -> s.replaceAll("\\.", "").replaceAll(",", ""))    // 간단한 전처리
                        .collect(Collectors.toList());

        for (String word : words) {
            wordCounters.merge(word, wordCounters.getOrDefault(word, 1), (k, v) -> v + 1);
        }

        wordCounters.forEach((word, count) -> System.out.println(word + "->" + count));
    }
}