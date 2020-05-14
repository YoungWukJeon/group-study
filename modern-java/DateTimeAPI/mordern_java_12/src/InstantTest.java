import java.time.Instant;

public class InstantTest {
    public static void main(String[] args) {
        // Instant
        Instant t1 = Instant.ofEpochSecond(3);
        Instant t2 = Instant.ofEpochSecond(3, 0);
        Instant t3 = Instant.ofEpochSecond(2, 1_000_000_000);
        Instant t4 = Instant.ofEpochSecond(4, -1_000_000_000);

        System.out.println(t1);
        System.out.println(t2);
        System.out.println(t3);
        System.out.println(t4);
    }
}