package category;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryService {
    private static final List<Category> CATEGORY_LIST = Arrays.asList(
            new Category(1L, null, "서울"),
            new Category(2L, null, "경기도"),
            new Category(3L, 1L, "강남구"),
            new Category(4L, 1L, "송파구"),
            new Category(5L, 2L, "수원시"),
            new Category(6L, 2L, "안양시"),
            new Category(7L, 2L, "성남시")
    );


    public List<String> getCategoryNameListByParentNoFor(long parentNo){
        List<String> categoryNameList = new ArrayList<>();
        for(Category category : CATEGORY_LIST){
            if(category.getParentNo() != null && parentNo == category.getParentNo()){
                categoryNameList.add(category.getName());
            }
        }
        return categoryNameList;
    }

    public List<String> getCategoryNameListByParentNoStream(long parentNo){
        return CATEGORY_LIST.parallelStream()
                .filter(category -> category.getParentNo() != null)
                .filter(category -> parentNo == category.getParentNo())
                .map(Category::getName)
                .collect(Collectors.toList());
    }
}
