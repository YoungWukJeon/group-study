package collectionfactory;

import java.util.Set;

public class SetFactoryExample {
    public static void main(String[] args) {
        String[] arrays = {"apple", "banana", "grape"};
        String[] arrays2 = {"apple", "banana", "banana"};
        Set<String> sets = Set.of(arrays);
        Set<String> sets2 = Set.of(arrays2);

        System.out.println(sets);
        System.out.println(sets2);
    }
}
