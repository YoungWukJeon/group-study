package _3;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Main {
    private static final List<Shop> shops = Arrays.asList(
            new Shop("BestPrice"),
            new Shop("LetsSaveBig"),
            new Shop("MyFavoriteShop"),
            new Shop("BuyItAll")
//           ,new Shop("BestPrice"),
//            new Shop("LetsSaveBig"),
//            new Shop("MyFavoriteShop"),
//            new Shop("BuyItAll"),
//            new Shop("BuyItAll")
    );

    // 상점 수만큼의 스레드를 갖는 스레드 풀을 생성한다. 스레드 수의 범위는 0~100
    private static final Executor executor = Executors.newFixedThreadPool(Math.min(shops.size(), 100), (r) -> {
        Thread th = new Thread(r);
        th.setDaemon(true);
        return th;
    });

    public static void main(String[] args) {
        long start = System.nanoTime();
        System.out.println(findPrices("myPhone27S"));
//        System.out.println(findPricesUsingParallelStream("myPhone27S"));
//        System.out.println(findPricesUsingCompletableFutureSequentially("myPhone27S"));
//        System.out.println(findPricesUsingCompletableFutureInParallel("myPhone27S"));
//        System.out.println(findPricesUsingCompletableFutureInParallelWithCustomExecutor("myPhone27S"));
        long duration = (System.nanoTime() - start) / 1_000_000;
        System.out.println("Done in " + duration + "ms");
    }

    public static List<String> findPrices(String product) {
        return shops.stream()
                .map(shop -> String.format("%s price is %.2f", shop.getName(), shop.getPrice(product)))
                .collect(Collectors.toList());
    }

    public static List<String> findPricesUsingParallelStream(String product) {
        return shops.parallelStream()
                .map(shop -> String.format("%s price is %.2f", shop.getName(), shop.getPrice(product)))
                .collect(Collectors.toList());
    }

    public static List<String> findPricesUsingCompletableFutureSequentially(String product) {
        return shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(
                        () -> shop.getName() + " price is " + shop.getPrice(product)))
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    public static List<String> findPricesUsingCompletableFutureInParallel(String product) {
        List<CompletableFuture<String>> priceFutures =
                shops.stream()
                        .map(shop -> CompletableFuture.supplyAsync(
                                () -> shop.getName() + " price is " + shop.getPrice(product)))
                        .collect(Collectors.toList());

        return priceFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    public static List<String> findPricesUsingCompletableFutureInParallelWithCustomExecutor(String product) {
        List<CompletableFuture<String>> priceFutures =
                shops.stream()
                        .map(shop -> CompletableFuture.supplyAsync(
                                () -> shop.getName() + " price is " + shop.getPrice(product), executor))
                        .collect(Collectors.toList());

        return priceFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }
}
