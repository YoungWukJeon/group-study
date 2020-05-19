package _3;

import lombok.Getter;

import java.util.Random;

@Getter
public class Shop {
    private String name;

    public Shop(String name) {
        this.name = name;
    }

    public double getPrice(String product) {
        return calculatePrice(product);
    }

    private double calculatePrice(String product) {
        delay();    // 인위적인 delay(1s). 외부 서버에서 가격정보를 가져오는 부분이라고 이해하면 됨
        Random random = new Random();
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
