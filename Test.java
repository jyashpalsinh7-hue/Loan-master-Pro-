import java.text.NumberFormat;
import java.util.Locale;

public class Test {
    public static void main(String[] args) {
        System.out.println(NumberFormat.getCurrencyInstance(Locale.UK).format(1000));
        System.out.println(NumberFormat.getCurrencyInstance(Locale.CANADA).format(1000));
    }
}
