import java.util.List;

public class Category {
	private Long no;
	private Long parentNo;
	private String name;
	private List<Category> subCategoryList;
	
	public Category(Long no, Long parentNo, String name, List<Category> subCategoryList) {
		this.no = no;
		this.parentNo = parentNo;
		this.name = name;
		this.subCategoryList = subCategoryList;
	}
	
	public void setNo(Long no) {
		this.no = no;
	}
	
	public void setParentNo(Long parentNo) {
		this.parentNo = parentNo;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Long getNo() {
		return this.no;
	}
	
	public Long getParentNo() {
		return this.parentNo;
	}
	
	public String getName() {
		return this.name;
	}
	
	public List<Category> getSubCategoryList() {
		return this.subCategoryList;
	}
	
	@Override
	public String toString() {
		return "Category(no=" + no + ", name=" + name + ", subCategoryList=" + subCategoryList.size() + ")";
	}
}
