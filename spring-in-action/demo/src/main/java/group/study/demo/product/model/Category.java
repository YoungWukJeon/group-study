package group.study.demo.product.model;

public enum Category {
    CAR("차량"),
    FOOD("식품"),
    BOOK("도서"),
    DRESS("옷"),
    ETC("기타");

    private final String name;

    Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
