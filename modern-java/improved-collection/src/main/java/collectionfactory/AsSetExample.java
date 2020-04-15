package collectionfactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AsSetExample {
    public static void main(String[] args) {
        String[] arrays = {"apple", "banana", "grape"};

        useHashSet(arrays); // HashSet 이용
        useStreamAPI(arrays);   // Stream API 이용
    }

    public static void useHashSet(String[] arrays) {
        Set<String> fruits = new HashSet<> (Arrays.asList(arrays));
        System.out.println(fruits);
    }

    public static void useStreamAPI(String[] arrays) {
        Set<String> fruits = Stream.of(arrays).collect(Collectors.toSet());
        System.out.println(fruits);
    }
}
