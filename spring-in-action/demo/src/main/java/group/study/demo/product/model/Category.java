package group.study.demo.product.model;

import java.util.Arrays;
import java.util.Optional;

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

    public static Category findByCategory(String key) {
        return Arrays.stream(values()).filter(s -> s.name().equals(key.toUpperCase())).findFirst().orElse(Category.ETC);
    }
}
