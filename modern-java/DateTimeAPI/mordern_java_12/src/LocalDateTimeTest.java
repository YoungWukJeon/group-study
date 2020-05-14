import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;

public class LocalDateTimeTest {
    public static void main(String[] args) {
        // 12-4
        LocalDate date = LocalDate.parse("2017-09-21");
        LocalTime time = LocalTime.parse("13:45:20");

        LocalDateTime dt1 = LocalDateTime.of(2017, Month.SEPTEMBER, 21, 13, 45, 20);
        LocalDateTime dt2 = LocalDateTime.of(date, time);
        LocalDateTime dt3 = date.atTime(13,45,20);
        LocalDateTime dt4 = date.atTime(time);
        LocalDateTime dt5 = time.atDate(date);

        System.out.println(dt1);
        System.out.println(dt2);
        System.out.println(dt3);
        System.out.println(dt4);
        System.out.println(dt5);

        LocalDate date1 = dt1.toLocalDate();
        LocalTime time1 = dt1.toLocalTime();

        System.out.println(date1);
        System.out.println(time1);
    }
}
