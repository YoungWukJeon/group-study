import java.util.function.Function;

public class InnerClass {
    Function<Object, String> f = new Function<> () {
        @Override
        public String apply(Object obj) {
            return obj.toString();
        }
    };

    public static void main(String[] args) {}
}