package _2;

import java.util.concurrent.Future;

public class Main {
    public static void main(String[] args) {
        Shop shop = new Shop("BestShop");
        long start = System.nanoTime();
        Future<Double> futurePrice = shop.getPriceAsync("my favorite product");
//        Future<Double> futurePrice = shop.getPriceAsyncWithException("my favorite product");
//        Future<Double> futurePrice = shop.getPriceAsyncUsingSupplyAsync("my favorite product");
        long invocationTime = ((System.nanoTime() - start) / 1_000_000);
        System.out.println("Invocation returned after " + invocationTime + "ms");

        // 제품 가격을 계산하는 동안
        doSomethingElse();  // 다른 상점 검색 등 다른 작업 수행
        try {
            double price = futurePrice.get();
            System.out.printf("Price is %.2f%n", price);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        long retrievalTime = ((System.nanoTime() - start) / 1_000_000);
        System.out.println("Price returned after " + retrievalTime + "ms");
    }

    static void doSomethingElse() {
        System.out.println("다른 일");
    }
}
