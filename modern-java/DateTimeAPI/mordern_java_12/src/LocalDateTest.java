import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoField;

public class LocalDateTest {

    public static void main(String[] args) {
        // 12-1
        LocalDate date = LocalDate.of(2017, 9, 21);
        int year = date.getYear();
        Month month = date.getMonth();
        int day = date.getDayOfMonth();
        DayOfWeek dow = date.getDayOfWeek();
        int len = date.lengthOfMonth();
        boolean leap = date.isLeapYear();

        LocalDate today = LocalDate.now();

        System.out.println(date);
        System.out.println(year);
        System.out.println(month);
        System.out.println(day);
        System.out.println(dow);
        System.out.println(len);
        System.out.println(leap);
        System.out.println(today);

        // 12-2
        year = date.get(ChronoField.YEAR);
        int int_month = date.get(ChronoField.MONTH_OF_YEAR);
        day = date.get(ChronoField.DAY_OF_MONTH);

        System.out.println(year);
        System.out.println(int_month);
        System.out.println(day);


    }
}

