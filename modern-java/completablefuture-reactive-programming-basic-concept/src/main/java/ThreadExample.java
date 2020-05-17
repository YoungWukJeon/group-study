public class ThreadExample {
    public static void main(String[] args) throws InterruptedException {
        int x = 1337;
        Result result = new Result();

        long start = System.currentTimeMillis();

        Thread t1 = new Thread(() -> { result.left = f(x); });
        Thread t2 = new Thread(() -> { result.right = g(x); });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println(result.left + result.right);

        long end = System.currentTimeMillis();
        System.out.println("Thread: " + (end - start) + "ms takes");
    }

    static int f(int x) {
        return x + 10;
    }

    static int g(int x) {
        return x + 20;
    }

    private static class Result {
        private int left;
        private int right;
    }
}
