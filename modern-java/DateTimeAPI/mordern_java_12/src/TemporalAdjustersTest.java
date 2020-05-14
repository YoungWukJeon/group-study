import java.time.DayOfWeek;
import java.time.LocalDate;

import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static java.time.temporal.TemporalAdjusters.nextOrSame;

public class TemporalAdjustersTest {
    public static void main(String[] args) {
        // 12-8
        LocalDate date1 = LocalDate.of(2014, 3, 18);
        LocalDate date2 = date1.with(nextOrSame(DayOfWeek.SUNDAY));
        LocalDate date3 = date2.with(lastDayOfMonth());

        System.out.println(date1);
        System.out.println(date2);
        System.out.println(date3);
    }
}
