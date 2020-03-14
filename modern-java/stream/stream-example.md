# Java 8 스트림과 람다 실습해보기 

## 스트림 문제 풀기 

## [문제] 카테고리 

현재 화면에서는 카테고리를 tree view 형식으로 노출하고 있음   
카테고리를 DB 에서 가져와서 tree view 형식으로 구성하는 로직을 만들어보자   
- 카테고리는 2 depth 라고 가정 
ex) 카테고리 tree view 
```
서울
 ㄴ 강남구 
 ㄴ 송파구 
경기도
 ㄴ 수원시
 ㄴ 안양시 
 ㄴ 성남시 
```

### 카테고리 tree view 로 변환하기 
- DB 에 저장되는 카테고리 
```java
public class CategoryEntity{
    private Long no;
    private Long parentNo;
    private String name;
}
```
- 화면에 노출할 카테고리 
```java
public class Category{
    private Long no;
    private Long parentNo;
    private String name;
    // 하위 카테고리 리스트 
    private List<Category> subCategoryList;
}
```

- 아래 메소드를 구현해보자 
```java
/** 
 * @param categoryEntityList: DB 에서 가져온 모든 카테고리 리스트 
 *   - 1 depth, 2 depth 가 1차원 배열로 구성됨 
 */
public List<Category> convertCategoryList(List<CategoryEntity> categoryEntityList){
    // stream 으로 변환하면 됨 ㅎ 
    return null;
}
```

### 카테고리 tree 수정 
관리자는 tree view 에서 여러 카테고리를 한번에 수정하여 저장이 가능함 
ex) 수정하는 것의 예 
```
서울
 ㄴ 강남구 
 (삭제) ㄴ 송파구 
경기도
 ㄴ 수원시 -> 용인시 (수정)
 ㄴ 안양시 
 ㄴ 성남시 
인천 (새로 추가)
```
- 아래 메소드를 구현해보자 
입력은 기존에 화면에 출력된 카테고리 tree view 임
ㄴ List<Category>
```java
public void updateCategoryListTree(List<Category> categoryList){
    // DB 에서 조회한 카테고리라고 가정 
    List<CategoryEntity> dbCategoryList = new ArrayList<CategoryEntity>(){
        {
            add(new CategoryEntity(){
                {
                    setNo(1L);
                    setParentNo(null);
                    setName("서울");
                }
            });
            add(new CategoryEntity(){
                {
                    setNo(2L);
                    setParentNo(null);
                    setName("경기도");
                }
            });
            add(new CategoryEntity(){
                {
                    setNo(3L);
                    setParentNo(1L);
                    setName("강남구");
                }
            });
            // ... 중략 
        }
    };
    
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
    List<CategoryEntity> targetEntityList = null;
    return targetEntityList;
}
private void createCategoryList(List<CategoryEntity> categoryList, List<CategoryEntity> dbCategoryList){
    List<CategoryEntity> targetEntityList = null;
    // 구현

    // save(targetEntityList)
}
private void updateCategoryList(List<CategoryEntity> categoryList, List<CategoryEntity> dbCategoryList){
    List<CategoryEntity> targetEntityList = null;
    // 구현 

    // save(targetEntityList)
}
private void deleteCategoryList(List<CategoryEntity> categoryList, List<CategoryEntity> dbCategoryList){
    List<CategoryEntity> targetEntityList = null;
    // 구현

    // delete(targetEntityList)
}
```

### 카테고리 별 post 수 세기 
post 는 카테고리를 의존하는 하위 객체라고 생각하자   
post 는 아래처럼 생겼다 
```java
public class Post{
    private Long no;
    private String title;
    private Long categoryNo;
}
```
- 카테고리 별로 post 수를 세는 로직을 작성해보자 
카테고리는 1 ~ 2 depth 까지 존재한다고 가정  
1 depth 카테고리 count 는 하위 2 depth 카테고리 카운트의 합이다   
ex)
```
서울
 ㄴ 강남구 : post 10
 ㄴ 송파구 : post 3

예상 출력
서울 : 13
강남구 : 10
송파구 : 3
```
- 이제 구현해보자 
```java
public Map<Category, Long> countPostGroupingByCategory(List<Category> categoryList, List<Post> postList){
    // 
    return null;
}
```


