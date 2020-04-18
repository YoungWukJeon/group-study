import category.Category;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LambdaTesting {

    @Test
    public void stackTrace(){
        List<Category> categoryList = Arrays.asList(new Category(1L, null, "서울"), null);
        categoryList.stream()
                .map(Category::getName)
                .forEach(System.out::println);
    }

    @Test
    public void logging(){
        List<Category> categoryList = Arrays.asList(
                new Category(1L, null, "서울"),
                new Category(2L, null, "경기도"),
                new Category(3L, 1L, "강남구"),
                new Category(4L, 1L, "송파구"),
                new Category(5L, 2L, "수원시"),
                new Category(6L, 2L, "안양시"),
                new Category(7L, 2L, "성남시")
        );

        long parentNo = 2L;
        List<String> result = categoryList.stream()
                .peek(category -> System.out.println(category))
                .filter(category -> category.getParentNo() != null)
                .peek(category -> System.out.println(category))
                .filter(category -> parentNo == category.getParentNo())
                .peek(category -> System.out.println(category))
                .map(Category::getName)
                .peek(categoryName -> System.out.println(categoryName))
                .collect(Collectors.toList());

    }
}
