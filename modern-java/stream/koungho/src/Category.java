import java.util.List;


public class Category {
    private Long no;
    private Long parentNo;
    private String name;
    // 하위 카테고리 리스트
    private List<Category> subCategoryList;

    public Category() {
    }

    public Category(Long no, Long parentNo, String name) {
        this.no = no;
        this.parentNo = parentNo;
        this.name = name;
    }

    public Long getNo() {
        return no;
    }

    public void setNo(Long no) {
        this.no = no;
    }

    public Long getParentNo() {
        return parentNo;
    }

    public void setParentNo(Long parentNo) {
        this.parentNo = parentNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Category> getSubCategoryList() {
        return subCategoryList;
    }

    public void setSubCategoryList(List<Category> subCategoryList) {
        this.subCategoryList = subCategoryList;
    }

    @Override
    public String toString() {
        return "Category{" +
                "no=" + no +
                ", parentNo=" + parentNo +
                ", name='" + name + '\'' +
                ", subCategoryList=" + subCategoryList +
                '}';
    }

}