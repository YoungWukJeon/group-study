package group.study.demo.product.model;

public enum Category {
    FOOD("식품"),
    BOOK("도서"),
    DRESS("옷");

    private final String name;

    Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
