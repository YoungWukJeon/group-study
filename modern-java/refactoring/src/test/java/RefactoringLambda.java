import category.CategoryService;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.IntBinaryOperator;

import static com.sun.javaws.JnlpxArgs.verify;

public class RefactoringLambda {

    @Test
    public void 익명_클래스(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("익명 클래스");
            }
        };
        runnable.run();

        Runnable lambda = () -> System.out.println("람다");
        lambda.run();
    }

    @Test
    public void 익명_클래스_this_super(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println(this.getClass());
            }
        };
        runnable.run();

        Runnable lambda = () -> System.out.println(this.getClass());
        lambda.run();
    }

    @Test
    public void 익명_클래스_shadow_변수_불가능(){
        int a = 10;

        // 가능
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int a = 2;
                System.out.println(a);
            }
        };
        runnable.run();

        AtomicInteger integer  = new AtomicInteger(a);
        Integer b = 3;
        // 컴파일 에러
        Runnable lambda = () -> {
//            int a = 3;
            integer.getAndSet(5);
            System.out.println(integer);
        };
        lambda.run();
    }

    @Test
    public void 익명_클래스_context_모호함(){
        OverloadingRunner overloadingRunner = new OverloadingRunner();

        overloadingRunner.run(new Consumer() {
            @Override
            public void accept(Object o) {
                System.out.println(o.toString());
            }
        });
        overloadingRunner.run(new Runnable() {
            @Override
            public void run() {

            }
        });

        // 명시적 타입 캐스팅이 필요
//        overloadingRunner.run((s) -> System.out.println(s.toString()));
        overloadingRunner.run((Runnable)() -> System.out.println("call"));
    }

    @Test
    public void 람다_to_메서드_참조(){
        // 람다 예제
        IntBinaryOperator lambdaOperator = (x, y) -> Calculator.staticAdd(x, y);
        System.out.println(lambdaOperator.applyAsInt(1, 2));

        // 메서드 참조
        IntBinaryOperator methodReferencer = Calculator::staticAdd;
        System.out.println(methodReferencer.applyAsInt(2, 3));

        Calculator calculator = new Calculator();
        methodReferencer = calculator::add;
        System.out.println(methodReferencer.applyAsInt(20, 30));
    }

    @Test
    public void 명령형_to_스트림(){
        CategoryService categoryService = new CategoryService();
        final long parentNo = 2L;

        List<String> forResult = categoryService.getCategoryNameListByParentNoFor(parentNo);
        List<String> streamResult = categoryService.getCategoryNameListByParentNoStream(parentNo);

        Assert.assertArrayEquals(forResult.toArray(), streamResult.toArray());
    }

}
