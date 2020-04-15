package collectionfactory;

import java.util.Arrays;
import java.util.List;

public class AsListExample {
    public static void main(String[] args) {
        String[] arrays = {"apple", "banana", "grape"};
        List<String> lists = Arrays.asList(arrays);

        changeOriginArrayElement(arrays, lists);    // 원본 배열 요소 변경
//        addElement(lists);    // 요소 삽입
//        removeElement(lists); // 요소 삭제
    }

    public static void changeOriginArrayElement(String[] arrays, List<String> lists) {
        // 원본 요소 변경 전
        System.out.println(lists);

        // 원본 요소 변경 후
        arrays[1] = "orange";
        System.out.println(lists);
    }

    public static void addElement(List<String> lists) {
        lists.add("peach");
        System.out.println(lists);
    }

    public static void removeElement(List<String> lists) {
        lists.remove("apple");
        System.out.println(lists);
    }
}
