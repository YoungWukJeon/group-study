import java.time.LocalTime;

public class LocalTimeTest {
    public static void main(String[] args) {
        // 12-3
        LocalTime time = LocalTime.of(13,45, 20);
        int hour = time.getHour();
        int minute = time.getMinute();
        int second = time.getSecond();

        System.out.println(hour);
        System.out.println(minute);
        System.out.println(second);
    }
}
