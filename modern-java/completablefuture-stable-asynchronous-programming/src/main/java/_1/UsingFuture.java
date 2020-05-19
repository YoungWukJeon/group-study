package _1;

import java.util.concurrent.*;

public class UsingFuture {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();
        Future<Double> future = executor.submit(new Callable<Double>() {
            @Override
            public Double call() throws InterruptedException {
                return doSomeLongComputation(); // 시간이 오래걸리는 작업을 비동기적으로 실행
            }
        });
        doSomethingElse();
        try {
            Double result = future.get(1, TimeUnit.SECONDS);    // 비동기 작업 결과를 가져온다.
            System.out.println("Result: " + result);
        } catch (ExecutionException ee) {
            // 계산 중 예외 발생
        } catch (InterruptedException ie) {
            // 현재 스레드에서 대기 중 인터럽트 발생
        } catch (TimeoutException te) {
            // Future가 완료되기 전에 타임아웃 발생
        } finally {
            executor.shutdown();
        }
    }

    static Double doSomeLongComputation() throws InterruptedException {
        System.out.println("오래 걸리는 일");
        Thread.sleep(1000);
        return 0d;
    }

    static void doSomethingElse() {
        System.out.println("다른 일");
    }
}
