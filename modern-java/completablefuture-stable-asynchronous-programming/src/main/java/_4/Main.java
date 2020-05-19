package _4;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

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

    private static final ExchangeService exchangeService = new ExchangeService();

    public static void main(String[] args) throws Exception {
        long start = System.nanoTime();
        System.out.println(shops.get(0).getPrice("myPhone27S"));
//        System.out.println(findPrices("myPhone27S"));
//        System.out.println(findPricesUsingCompletableFutureInParallelWithCustomExecutor("myPhone27S"));
//        System.out.println(findPricesUsingOneCompletableFutureInParallelWithCustomExecutor("myPhone27S"));
//        System.out.println(findPricesUsingCompletableFutureInParallelWithCustomExecutorAndThenComposeAsync("myPhone27S"));
//        System.out.println(findRatePricesUsingCompletableFutureInParallelWithCustomExecutor("myPhone27S"));
//        System.out.println(findRatePricesUsingJava7("myPhone27S"));
//        System.out.println(findRatePricesUsingCompletableFutureInParallelWithCustomExecutorAndTimeout("myPhone27S"));
        long duration = (System.nanoTime() - start) / 1_000_000;
        System.out.println("Done in " + duration + "ms");
    }

    public static List<String> findPrices(String product) {
        return shops.stream()
                .map(shop -> shop.getPrice(product))    // 각 상점에서 할인 전 가격 얻기
                .map(Quote::parse)  // 상점에서 반환한 문자열을 Quote 객체로 변
                .map(Discount::applyDiscount)   // Discount 서비스를 이용해서 각 Quote에 할인을 적
                .collect(Collectors.toList());
    }

    public static List<String> findPricesUsingCompletableFutureInParallelWithCustomExecutor(String product) {
        List<CompletableFuture<String>> priceFutures =
                shops.stream()
                        .map(shop -> CompletableFuture.supplyAsync(
                                () -> shop.getPrice(product), executor))
                        .map(future -> future.thenApply(Quote::parse))
                        .map(future -> future.thenCompose(
                                quote -> CompletableFuture.supplyAsync(
                                        () -> Discount.applyDiscount(quote), executor)))
                        .collect(Collectors.toList());

        return priceFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    public static List<String> findPricesUsingOneCompletableFutureInParallelWithCustomExecutor(String product) {
        List<CompletableFuture<String>> priceFutures =
                shops.stream()
                        .map(shop -> CompletableFuture.supplyAsync(
                                () -> Discount.applyDiscount(Quote.parse(shop.getPrice(product))), executor))
                        .collect(Collectors.toList());

        return priceFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    public static List<String> findPricesUsingCompletableFutureInParallelWithCustomExecutorAndThenComposeAsync(String product) {
        List<CompletableFuture<String>> priceFutures =
                shops.stream()
                        .map(shop -> CompletableFuture.supplyAsync(
                                () -> shop.getPrice(product), executor))
                        .map(future -> future.thenApply(Quote::parse))
                        .map(future -> future.thenComposeAsync(
                                quote -> CompletableFuture.supplyAsync(
                                        () -> Discount.applyDiscount(quote), executor)))
                        .collect(Collectors.toList());

        return priceFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    public static List<String> findRatePricesUsingCompletableFutureInParallelWithCustomExecutor(String product) {
        List<CompletableFuture<Double>> priceFuturesInUSD =
                shops.stream()
                        .map(shop -> CompletableFuture.supplyAsync(
                                () -> shop.getPriceBasic(product), executor)
                                .thenCombine(CompletableFuture.supplyAsync(
                                        () -> exchangeService.getRate(Money.EUR, Money.USD)),
                                        (price, rate) -> price * rate))
                        .collect(Collectors.toList());

        return priceFuturesInUSD.stream()
                .map(CompletableFuture::join)
                .map(price -> String.format("%.2f", price))
                .collect(Collectors.toList());
    }

    // Stream은 그대로 사용하려고 한다.
    public static List<String> findRatePricesUsingJava7(String product) throws Exception {
        ExecutorService executorService = Executors.newCachedThreadPool();
        final Future<Double> rateFuture = executorService.submit(new Callable<Double>() {
            @Override
            public Double call() {
                return exchangeService.getRate(Money.EUR, Money.USD);
            }
        });

        List<Future<Double>> priceFuturesInUSD =
                shops.stream()
                        .map(shop -> executorService.submit(new Callable<Double>() {
                                @Override
                                public Double call() throws Exception {
                                    double priceInEUR = shop.getPriceBasic(product);
                                    return priceInEUR * rateFuture.get();   // 가격을 검색한 Future를 이용해서 가격과 환율을 곱한다.
                                }
                            })
                        )
                        .collect(Collectors.toList());

        return priceFuturesInUSD.stream()
                .map(future -> {
                    try {
                        return future.get();
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    } finally {
                        executorService.shutdown();
                    }
                })
                .map(price -> String.format("%.2f", price))
                .collect(Collectors.toList());
    }

    // Java9에 추가된 타임아웃 관리 기능
    public static List<String> findRatePricesUsingCompletableFutureInParallelWithCustomExecutorAndTimeout(String product) {
        List<CompletableFuture<Double>> priceFuturesInUSD =
                shops.stream()
                        .map(shop -> CompletableFuture.supplyAsync(
                                () -> shop.getPriceBasic(product), executor)
                                .thenCombine(CompletableFuture.supplyAsync(
                                        () -> exchangeService.getRate(Money.EUR, Money.USD))
                                                .completeOnTimeout(ExchangeService.DEFAULT_RATE, 1, TimeUnit.SECONDS),  // 환전 서비스가 일 초 안에 결과를 제공하지 않으면 기본 환율값을 적용
                                        (price, rate) -> price * rate)
                                .orTimeout(3, TimeUnit.SECONDS))    // 3초 뒤에 작업이 완료되지 않으면 Future가 TimeoutException을 발생시키도록 설정
                        .collect(Collectors.toList());

        return priceFuturesInUSD.stream()
                .map(CompletableFuture::join)
                .map(price -> String.format("%.2f", price))
                .collect(Collectors.toList());
    }
}
