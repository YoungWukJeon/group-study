
public class CategoryEntity {
	private Long no;
	private Long parentNo;
	private String name;
	
	public CategoryEntity(Long no, Long parentNo, String name) {
		this.no = no;
		this.parentNo = parentNo;
		this.name = name;
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
	
	@Override
	public String toString() {
		return "Category(no=" + no + ", parentNo=" + parentNo + ", name=" + name + ")";
	}
	
	@Override
	public boolean equals(Object obj) {
		CategoryEntity entity = (CategoryEntity) obj;
		return (this.no == entity.getNo() && this.parentNo == entity.getParentNo() && this.name == entity.getName());
	}
}
