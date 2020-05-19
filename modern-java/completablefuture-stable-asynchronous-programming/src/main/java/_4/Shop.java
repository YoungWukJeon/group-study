package _4;

import lombok.Getter;

import java.util.Random;

public class Shop {
    @Getter
    private final String name;
    private final static Random random = new Random();

    public Shop(String name) {
        this.name = name;
    }

    public String getPrice(String product) {
        double price = calculatePrice(product);
        Discount.Code code = Discount.Code.values() [random.nextInt(Discount.Code.values().length)];
        return String.format("%s:%.2f:%s", name, price, code);
    }

    public double getPriceBasic(String product) {
        return calculatePrice(product);
    }

    private double calculatePrice(String product) {
        delay();    // 인위적인 delay(1s). 외부 서버에서 가격정보를 가져오는 부분이라고 이해하면 됨
        return random.nextDouble() * product.charAt(0) + product.charAt(1);
    }

    public static void delay() {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
