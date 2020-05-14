import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DateTimeFormatterCustomerTest {
    public static void main(String[] args) {
        ZoneId romeZone = ZoneId.of("Europe/Rome");
        LocalDate date = LocalDate.of(2014, Month.MARCH,18);
        ZonedDateTime zdt1 = date.atStartOfDay(romeZone);

        System.out.println(date);
        System.out.println(zdt1);
    }
}
