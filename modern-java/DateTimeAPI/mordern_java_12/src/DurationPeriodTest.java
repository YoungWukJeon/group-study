import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;

public class DurationPeriodTest {
    public static void main(String[] args) {
        // 12-5
        LocalDate time1 = LocalDate.of(2012, 10, 24);
        LocalDate time2 = LocalDate.of(2013,12,20);

        Instant t1 = Instant.ofEpochSecond(3);
        Instant t2 = Instant.ofEpochSecond(5, 3);

        Duration d1 = Duration.between(t1, t2);
        Period d2 = Period.between(time1, time2);

        System.out.println(d1.getSeconds());
        System.out.println(d1.getNano());
        System.out.println(d2.getYears());
        System.out.println(d2.getMonths());
        System.out.println(d2.getDays());

    }
}
