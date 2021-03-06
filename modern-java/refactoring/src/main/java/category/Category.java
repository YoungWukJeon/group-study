package category;


public class Category {
    private Long no;
    private Long parentNo;
    private String name;

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

    @Override
    public String toString() {
        return "Category{" +
                "no=" + no +
                ", parentNo=" + parentNo +
                ", name='" + name + "}";
    }
}
