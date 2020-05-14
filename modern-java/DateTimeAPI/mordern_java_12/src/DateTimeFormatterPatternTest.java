import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateTimeFormatterPatternTest {
    public static void main(String[] args) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate date1 = LocalDate.of(2014, 3, 18);
        String formattedDate = date1.format(formatter);
        LocalDate date2 = LocalDate.parse(formattedDate, formatter);

        System.out.println(date1);
        System.out.println(date2);
    }
}
