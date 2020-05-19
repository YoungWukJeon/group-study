package _5;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Stream;

public class Main {
    private static final List<Shop> shops = Arrays.asList(
            new Shop("BestPrice"),
            new Shop("LetsSaveBig"),
            new Shop("MyFavoriteShop"),
            new Shop("BuyItAll"),
            new Shop("ShopEasy")
    );

    // 상점 수만큼의 스레드를 갖는 스레드 풀을 생성한다. 스레드 수의 범위는 0~100
    private static final Executor executor = Executors.newFixedThreadPool(Math.min(shops.size(), 100), (r) -> {
        Thread th = new Thread(r);
        th.setDaemon(true);
        return th;
    });

    public static void main(String[] args) {
        long start = System.nanoTime();

        CompletableFuture[] futures = findPricesStream("myPhone27S")
                .map(f -> f.thenAccept(s -> System.out.println(s + " (done in " + ((System.nanoTime() - start) / 1_000_000) + "ms)")))
                .toArray(size -> new CompletableFuture[size]);
        CompletableFuture.allOf(futures).join();
//        CompletableFuture.anyOf(futures).join();

        long duration = (System.nanoTime() - start) / 1_000_000;
        System.out.println("All shops have now responded in " + duration + "ms");
    }

    public static Stream<CompletableFuture<String>> findPricesStream(String product) {
        return shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(
                        () -> shop.getPrice(product), executor))
                .map(future -> future.thenApply(Quote::parse))
                .map(future -> future.thenCompose(quote -> CompletableFuture.supplyAsync(
                        () -> Discount.applyDiscount(quote), executor)));
    }
}
