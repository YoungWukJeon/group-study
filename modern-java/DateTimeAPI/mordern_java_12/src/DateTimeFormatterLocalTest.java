import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateTimeFormatterLocalTest {
    public static void main(String[] args) {
        DateTimeFormatter italianFormatter = DateTimeFormatter.ofPattern("d. MMMM yyyy", Locale.ITALIAN);
        LocalDate date1 = LocalDate.of(2014, 3, 18);
        String formattedDate = date1.format(italianFormatter);
        LocalDate date2 = LocalDate.parse(formattedDate, italianFormatter);

        System.out.println(date1);
        System.out.println(formattedDate);
        System.out.println(date2);
    }
}
