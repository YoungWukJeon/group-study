import java.util.stream.LongStream;

class SumThread extends Thread {
    final int start;
    final int end;
    long sum = 0;

    SumThread(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public long getSum() {
        return sum;
    }

    @Override
    public void run() {
        for (int i = start; i < end; i++) {
            sum += i;
        }
    }
}

public class ThreadAndHighLevelAbstraction {
    public static void main(String[] args) throws InterruptedException {
        singleCore();
        multiThread();
        streamApi();
    }

    public static void singleCore() throws InterruptedException {
        long start = System.currentTimeMillis();

        long sum = 0;
        for (int i = 0; i < 1_000_000; i++) {
            sum += i;
        }
        System.out.println("result: " + sum);

        long end = System.currentTimeMillis();
        System.out.println("SingleCore: " + (end - start) + "ms takes");
    }

    public static void multiThread() throws InterruptedException {
        long start = System.currentTimeMillis();

        SumThread th0 = new SumThread(0, 250_000);
        SumThread th1 = new SumThread(250_000, 500_000);
        SumThread th2 = new SumThread(500_000, 750_000);
        SumThread th3 = new SumThread(750_000, 1_000_000);

        th0.start();
        th1.start();
        th2.start();
        th3.start();

        th0.join();
        th1.join();
        th2.join();
        th3.join();

        long sum = th0.getSum() + th1.getSum() + th2.getSum() + th3.getSum();
        System.out.println("result: " + sum);

        long end = System.currentTimeMillis();
        System.out.println("MultiThread: " + (end - start) + "ms takes");
    }

    public static void streamApi() {
        long start = System.currentTimeMillis();

        long sum = LongStream.range(0, 1_000_000).parallel().sum();
        System.out.println("result: " + sum);

        long end = System.currentTimeMillis();
        System.out.println("StreamApi: " + (end - start) + "ms takes");
    }
}
