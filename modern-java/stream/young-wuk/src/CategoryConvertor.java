import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CategoryConvertor {
	public List<Category> convertCategoryList(List<CategoryEntity> categoryEntityList) {
		
		Map<Long, List<CategoryEntity>> groupingCategory = categoryEntityList.stream()
				.filter(e -> e.getParentNo() != null)
				.collect(Collectors.groupingBy(CategoryEntity::getParentNo));
		
		return categoryEntityList.stream()
				.filter(e -> e.getParentNo() == null)
				.map(e1 -> 
					new Category(
							e1.getNo(), e1.getParentNo(), e1.getName(), 
							groupingCategory.get(e1.getNo()).stream()
								.map(e2 -> new Category(
										e2.getNo(), e2.getParentNo(), e2.getName(), Collections.emptyList())
								).collect(Collectors.toList()))
				).collect(Collectors.toList());
	}
	
	public List<CategoryEntity> convertToCategoryEntityList(List<Category> categoryList) {
	    List<CategoryEntity> depth1EntityList = categoryList.stream()
	    		.map(c -> {
	    			return new CategoryEntity(c.getNo(), c.getParentNo(), c.getName());
	    		}).collect(Collectors.toList());
	    
	    List<CategoryEntity> depth2EntityList = categoryList.stream()
	    		.flatMap(c1 -> c1.getSubCategoryList().stream()
	    				.map(c2 -> new CategoryEntity(c2.getNo(), c2.getParentNo(), c2.getName()))
	    		).collect(Collectors.toList());
	    
	    depth1EntityList.addAll(depth2EntityList);
	    
	    return depth1EntityList.stream().sorted(
	    			(e1, e2) -> Long.compare(e2.getNo(), e2.getNo())
	    		).collect(Collectors.toList());
	}
	
	public void createCategoryList(List<CategoryEntity> categoryEntityList, List<CategoryEntity> dbCategoryEntityList){
	    categoryEntityList.stream()
	    		.filter(e1 -> !dbCategoryEntityList.stream().anyMatch(e2 -> e1.equals(e2)))
	    		.forEach(System.out::println);
	    // 구현
	    
	    // save(targetEntityList)
	}
	
	public void updateCategoryList(List<CategoryEntity> categoryEntityList, List<CategoryEntity> dbCategoryEntityList){
	    categoryEntityList.stream()
	    		.filter(e1 -> dbCategoryEntityList.stream()
		    				.anyMatch(
			    				e2 -> 
			    					e1.getNo() == e2.getNo() && 
			    						(e1.getParentNo() != e2.getParentNo() || e1.getName() != e2.getName())
	    				))
	    		.forEach(System.out::println);
	    // 구현 

	    // save(targetEntityList)
	}
	
	public void deleteCategoryList(List<CategoryEntity> categoryEntityList, List<CategoryEntity> dbCategoryEntityList){
		dbCategoryEntityList.stream()
				.filter(e1 -> !categoryEntityList.stream().anyMatch(e2 -> e1.equals(e2)))
				.forEach(System.out::println);
	    // 구현

	    // delete(targetEntityList)
	}
	
	public static void main(String[] args) {
		CategoryConvertor categoryConvertor = new CategoryConvertor();
		
		List<CategoryEntity> dbCategoryEntityList = Arrays.asList(
				new CategoryEntity(1L, null, "서울"),
				new CategoryEntity(2L, null, "경기도"),
				new CategoryEntity(3L, 1L, "강남구")
		);
		
		List<CategoryEntity> categoryEntityList1 = Arrays.asList(
				new CategoryEntity(1L, null, "서울"),
				new CategoryEntity(2L, null, "경기도"),
				new CategoryEntity(3L, 1L, "강남구"),
				new CategoryEntity(4L, 2L, "수원시"),
				new CategoryEntity(5L, 1L, "송파구"),
				new CategoryEntity(6L, 2L, "안양시"),
				new CategoryEntity(7L, 2L, "성남시")
		);
		
		List<CategoryEntity> categoryEntityList2 = Arrays.asList(
				new CategoryEntity(1L, null, "서울"),
				new CategoryEntity(2L, 0L, "경기도"),
				new CategoryEntity(3L, 1L, "강남구 대치동"),
				new CategoryEntity(4L, 2L, "수원시"),
				new CategoryEntity(5L, 1L, "송파구"),
				new CategoryEntity(6L, 2L, "안양시"),
				new CategoryEntity(7L, 2L, "성남시")
		);
		
		List<CategoryEntity> categoryEntityList3 = Arrays.asList(
				new CategoryEntity(1L, null, "서울"),
				new CategoryEntity(3L, 1L, "강남구")
		);
		
//		categoryConvertor.convertCategoryList(categoryEntityList1).stream().forEach(System.out::println);
//		categoryConvertor.convertToCategoryEntityList(
//				categoryConvertor.convertCategoryList(categoryEntityList1)).forEach(System.out::println);
//		categoryConvertor.createCategoryList(categoryEntityList1, dbCategoryEntityList);
//		categoryConvertor.updateCategoryList(categoryEntityList2, dbCategoryEntityList);
//		categoryConvertor.deleteCategoryList(categoryEntityList3, dbCategoryEntityList);
	}
}
