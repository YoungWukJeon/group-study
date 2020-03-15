import java.util.ArrayList;
import java.util.List;

public class Main {

    private static List<CategoryEntity> getDbCategoryEntityList(){
        List<CategoryEntity> categoryEntityList = new ArrayList<CategoryEntity>(){
            {
                add(new CategoryEntity(1L, null, "서울"));
                add(new CategoryEntity(2L, null, "경기도"));
                add(new CategoryEntity(3L, 1L, "강남구"));
                add(new CategoryEntity(4L, 1L, "송파구"));
                add(new CategoryEntity(5L, 2L, "수원시"));
                add(new CategoryEntity(6L, 2L, "안양시"));
                add(new CategoryEntity(7L, 2L, "성남시"));
            }
        };

        return categoryEntityList;
    }

    private static List<Category>  getInputCategory() {
        List<Category> inputCategoryList = new ArrayList<Category>(){
            {
                add(new Category(1L, null, "서울"){
                    {
                        setSubCategoryList(new ArrayList<Category>(){
                            {
                                add(new Category(3L, 1L, "강남구"));
                            }
                        });
                    }
                });
                add(new Category(2L, null, "경기도"){
                    {
                        setSubCategoryList(new ArrayList<Category>(){
                            {
                                add(new Category(5L, 2L, "용인시"));
                                add(new Category(6L, 2L, "안양시"));
                                add(new Category(7L, 2L, "성남시"));
                            }
                        });
                    }
                });
                add(new Category(null, null, "인천"));
            }
        };
        return inputCategoryList;
    }

    public static void main(String[] args){
        CategoryService categoryService = new CategoryService();


//        List<Category> categoryList = categoryService.convertCategoryList(getDbCategoryEntityList());
//        System.out.println(categoryList);


        categoryService.updateCategoryListTree(getInputCategory(), getDbCategoryEntityList());
    }
}
