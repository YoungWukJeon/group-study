import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

public class DateControlTest {
    public static void main(String[] args) {
        // 12-6
        LocalDate date1 = LocalDate.of(2014,3,18);
        LocalDate date2 = date1.withYear(2011);
        LocalDate date3 = date2.withDayOfMonth(25);
        LocalDate date4 = date3.with(ChronoField.MONTH_OF_YEAR, 9);

        System.out.println(date1);
        System.out.println(date2);
        System.out.println(date3);
        System.out.println(date4);

        // 12-7
        LocalDate date5 = LocalDate.of(2014,3,18);
        LocalDate date6 = date5.plusWeeks(1);
        LocalDate date7 = date6.minusYears(3);
        LocalDate date8 = date7.plus(6, ChronoUnit.MONTHS);

        System.out.println(date5);
        System.out.println(date6);
        System.out.println(date7);
        System.out.println(date8);
    }
}
