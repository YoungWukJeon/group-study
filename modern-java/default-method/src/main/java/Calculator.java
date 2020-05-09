interface CalculatorInterface {
    int plus(int a, int b);
    int minus(int a, int b);

    // 디폴트 메서드는 재정의 가능
    default int multi(int a, int b) {
        return a * b;
    }

    // 정적 메소드는 재정의 불가능
    static int div(int a, int b) {
        if(b == 0) {
            return 0;
        }
        return a / b;
    }
}

class CalculatorClass implements CalculatorInterface {
    @Override
    public int plus(int a, int b) {
        return a + b;
    }

    @Override
    public int minus(int a, int b) {
        return a - b;
    }
}

public class Calculator {
    public static void main(String[] args) {
        CalculatorClass calc = new CalculatorClass();
        System.out.println( "plus : " + calc.plus(2, 1) );
        System.out.println( "minus : " + calc.minus(2, 1) );
        // 참조 변수로 호출 가능
        System.out.println( "multi : " + calc.multi(2, 1) );
        // 인터페이스 명으로 호출 가능
        System.out.println( "div : " + CalculatorInterface.div(2, 1) );
    }
}