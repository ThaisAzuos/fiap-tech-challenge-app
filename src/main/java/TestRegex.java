import java.util.regex.Pattern;

public class TestRegex {
    public static void main(String[] args) {
        Pattern p = Pattern.compile("^[A-Z]{3}\\d[A-Z]\\d{2}$|^[A-Z]{3}-?\\d{4}$");
        System.out.println("ABC1D23 matches: " + p.matcher("ABC1D23").matches());
        System.out.println("ABC-1234 matches: " + p.matcher("ABC-1234").matches());
        System.out.println("ABC1234 matches: " + p.matcher("ABC1234").matches());
    }
}
