import lombok.Getter;

@Getter
public class Test {
    private String message = "Hello World!";

    public static void main(String[] args) {
        Test test = new Test();
        System.out.println(test.getMessage());
    }
}
