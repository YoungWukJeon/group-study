import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class CategoryService {

    /**
     * @param categoryEntityList: DB 에서 가져온 모든 카테고리 리스트
     *   - 1 depth, 2 depth 가 1차원 배열로 구성됨
     */
    public List<Category> convertCategoryList(List<CategoryEntity> categoryEntityList){
        Map<Long, List<Category>> subCategoryMap = categoryEntityList.stream()
                .map(categoryEntity -> {
                    Category category = new Category();
                    category.setNo(categoryEntity.getNo());
                    category.setParentNo(categoryEntity.getParentNo() == null ? 0L : categoryEntity.getParentNo());
                    category.setName(categoryEntity.getName());
                    return category;
                }).collect(Collectors.groupingBy(Category::getParentNo));

        // 1 depth
        return subCategoryMap.get(0L)
                .stream()
                .map(category -> {
                    category.setSubCategoryList(subCategoryMap.get(category.getNo()));
                    return category;
                }).collect(Collectors.toList());
    }


    public void updateCategoryListTree(List<Category> categoryList, List<CategoryEntity> dbCategoryList){

        // 내부 entity 들로 변환한다
        List<CategoryEntity> categoryEntityList = convertToCategoryEntityList(categoryList);
        // 새로 추가된 카테고리 추가
        createCategoryList(categoryEntityList, dbCategoryList);
        // DB 존재하고 화면에서도 있는것 업데이트
        updateCategoryList(categoryEntityList, dbCategoryList);
        // DB 에만 존재하는 것 삭제
        deleteCategoryList(categoryEntityList, dbCategoryList);
    }

    /** 아래 4개의 메소드를 구현해보자 (stream)
     * - targetEntityList 를 생성하면 됨
     */
    private List<CategoryEntity> convertToCategoryEntityList(List<Category> categoryList){
        // 1 depth
        List<CategoryEntity> targetEntityList = categoryList.stream()
                .map(category -> {
                    CategoryEntity categoryEntity = new CategoryEntity();
                    categoryEntity.setNo(category.getNo());
                    categoryEntity.setParentNo(category.getParentNo());
                    categoryEntity.setName(category.getName());
                    return categoryEntity;
                }).collect(Collectors.toList());

        targetEntityList.addAll(categoryList.stream()
                .flatMap(category -> {
                    if(category.getSubCategoryList() == null){
                        return Stream.empty();
                    }
                    return category.getSubCategoryList().stream();
                })
                .map(category -> {
                    CategoryEntity categoryEntity = new CategoryEntity();
                    categoryEntity.setNo(category.getNo());
                    categoryEntity.setParentNo(category.getParentNo());
                    categoryEntity.setName(category.getName());
                    return categoryEntity;
                }).collect(Collectors.toList()));
        return targetEntityList;
    }
    private void createCategoryList(List<CategoryEntity> categoryList, List<CategoryEntity> dbCategoryList){
        Set<Long> dbCategoryNoSet = dbCategoryList.stream()
                .map(CategoryEntity::getNo)
                .collect(Collectors.toSet());
        List<CategoryEntity> targetEntityList = categoryList.stream()
                .filter(categoryEntity -> !dbCategoryNoSet.contains(categoryEntity.getNo()))
                .collect(Collectors.toList());

        assert targetEntityList.size() == 1;
        assert targetEntityList.get(0).getName().equals("인천");

        // save(targetEntityList)
    }
    private void updateCategoryList(List<CategoryEntity> categoryList, List<CategoryEntity> dbCategoryList){
        Map<Long, CategoryEntity> dbCategoryMap = dbCategoryList.stream()
                .collect(Collectors.toMap(CategoryEntity::getNo, Function.identity()));

        List<CategoryEntity> targetEntityList = categoryList.stream()
                .filter(categoryEntity -> dbCategoryMap.keySet().contains(categoryEntity.getNo()))
                .filter(categoryEntity -> {
                    CategoryEntity dbCategoryEntity = dbCategoryMap.get(categoryEntity.getNo());

                    return !categoryEntity.getName().equals(dbCategoryEntity.getName());
                }).collect(Collectors.toList());

        assert targetEntityList.size() == 1;
        assert targetEntityList.get(0).getName().equals("용인시");

        // save(targetEntityList)
    }
    private void deleteCategoryList(List<CategoryEntity> categoryList, List<CategoryEntity> dbCategoryList){
        Set<Long> categorySet = categoryList.stream()
                .map(CategoryEntity::getNo)
                .collect(Collectors.toSet());

        List<CategoryEntity> targetEntityList = dbCategoryList.stream()
                .filter(dbCategoryEntity -> !categorySet.contains(dbCategoryEntity.getNo()))
                .collect(Collectors.toList());

        assert targetEntityList.size() == 1;
        assert targetEntityList.get(0).getName().equals("송파구");

        // delete(targetEntityList)
    }
}
