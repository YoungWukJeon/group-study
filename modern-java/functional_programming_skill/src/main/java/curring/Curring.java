package curring;

import java.util.function.DoubleUnaryOperator;

public class Curring {

    static double converter(double x, double f, double b){
        return x * f + b;
    }

    static DoubleUnaryOperator curriedConverter(double f, double b){
        return (double x) -> x * f + b;
    }

    public static void main(String[] args){
        double x = 32;
        // 그냥
        double pureResult = converter(x, 9.0/5, 32);

        // curring
        DoubleUnaryOperator converterCtoF = curriedConverter(9.0/5, 32);
        DoubleUnaryOperator converterUSDtoGBP = curriedConverter(0.6, 0);
        DoubleUnaryOperator converterKmtoMi = curriedConverter(0.6214, 0);

        double result = converterCtoF.applyAsDouble(x);

        System.out.println(pureResult + ", " + result);

        assert pureResult == result;

    }
}
