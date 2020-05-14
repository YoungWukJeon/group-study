import java.time.LocalDate;
import java.time.LocalTime;

public class ParseTest {
    public static void main(String[] args) {
        LocalDate date = LocalDate.parse("2017-09-21");
        LocalTime time = LocalTime.parse("13:45:20");

        System.out.println(date);
        System.out.println(time);
    }
}
