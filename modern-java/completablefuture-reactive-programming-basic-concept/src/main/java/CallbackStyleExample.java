import java.util.function.IntConsumer;

public class CallbackStyleExample {
    public static void main(String[] args) {
        int x = 1337;
        Result result = new Result();

        f(x, (int y) -> {
            result.left = y;
            System.out.println((result.left + result.right));
        });

        g(x, (int z) -> {
            result.right = z;
            System.out.println((result.left + result.right));
        });
    }

    static void f(int x, IntConsumer dealWithResult) {
        dealWithResult.accept(x + 10);
    }

    static void g(int x, IntConsumer dealWithResult) {
        dealWithResult.accept(x + 20);
    }

    private static class Result {
        private int left;
        private int right;
    }
}
