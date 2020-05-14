import java.time.*;
import java.time.temporal.ChronoField;
import java.util.Date;
import java.time.temporal.TemporalAdjusters.*;

public class Test {
    public static void main(String[] args) {
        Date date = new Date(119, 14, 3);
        System.out.println(date);

        LocalDate today = LocalDate.now();
        System.out.println(today);

        int year = today.get(ChronoField.YEAR);

        LocalDateTime dt1 = LocalDateTime.of(2017, 3, 21, 13, 45, 20, 110);
        System.out.println(dt1);

        System.out.println(System.currentTimeMillis());


        LocalDate time1 = LocalDate.of(2012, 10, 24);
        LocalDate time2 = LocalDate.of(2013,12,20);



        Instant t1 = Instant.ofEpochSecond(3);
        Instant t2 = Instant.ofEpochSecond(5, 3);

        Duration d1 = Duration.between(t1, t2);
        Period d2 = Period.between(time1, time2);

        d1.getSeconds();
        d1.getNano();

        System.out.println(d2.getYears());
        System.out.println(d2.getMonths());
        System.out.println(d2.getDays());

        //time1.with(nextOrSame


    }
}
