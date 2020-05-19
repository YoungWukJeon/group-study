package _4;

import lombok.Getter;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

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

    public String getPriceUsingRandomDelay(String product) {
        double price = calculatePriceUsingRandomDelay(product);
        Discount.Code code = Discount.Code.values() [random.nextInt(Discount.Code.values().length)];
        return String.format("%s:%.2f:%s", name, price, code);
    }

//    public Future<Double> getPriceAsync(String product) {
//        CompletableFuture<Double> futurePrice = new CompletableFuture<> ();
//        new Thread(() -> {
//            double price = calculatePrice(product);
//            futurePrice.complete(price);
//        }).start();
//        return futurePrice;
//    }
//
//    public Future<Double> getPriceAsyncUsingSupplyAsync(String product) {
//        return CompletableFuture.supplyAsync(() -> calculatePrice(product));
//    }
//
//    public Future<Double> getPriceAsyncWithException(String product) {
//        CompletableFuture<Double> futurePrice = new CompletableFuture<> ();
//        new Thread(() -> {
//            try {
//                double price = calculatePrice(product);
//                if (product.contains("product")) {
//                    throw new RuntimeException("product not available");
//                }
//                futurePrice.complete(price);
//            } catch (Exception ex) {
//                futurePrice.completeExceptionally(ex);
//            }
//        }).start();
//        return futurePrice;
//    }

    private double calculatePrice(String product) {
        delay();    // 인위적인 delay(1s). 외부 서버에서 가격정보를 가져오는 부분이라고 이해하면 됨
        return random.nextDouble() * product.charAt(0) + product.charAt(1);
    }

    private double calculatePriceUsingRandomDelay(String product) {
        randomDelay();
        return random.nextDouble() * product.charAt(0) + product.charAt(1);
    }

    public static void delay() {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void randomDelay() {
        int delay = 500 + random.nextInt(2000);
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
