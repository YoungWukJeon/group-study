package collectionfactory;

import java.util.Map;

public class MapFactoryExample {
    public static void main(String[] args) {
        Map<String, Integer> wordCounts = Map.of("apple", 1, "banana", 2, "grape", 5);
        Map<String, Integer> wordCounts2 = Map.ofEntries(
                Map.entry("apple", 1),
                Map.entry("banana", 2),
                Map.entry("grape", 5)
        );
        System.out.println(wordCounts);
        System.out.println(wordCounts2);
    }
}
