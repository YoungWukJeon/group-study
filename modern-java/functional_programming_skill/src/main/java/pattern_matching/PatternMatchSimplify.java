package pattern_matching;

import java.util.function.Function;
import java.util.function.Supplier;

public class PatternMatchSimplify {
    static <T> T patternMatchExpr(Expr e, TriFunction<String, Expr, Expr, T> binopcase, Function<Integer, T> numcase, Supplier<T> defaultcase){
        return (e instanceof BinOp) ?
                binopcase.apply(((BinOp) e).opname, ((BinOp) e).left, ((BinOp) e).right) :
                (e instanceof Number) ?
                        numcase.apply(((Number) e).val) : defaultcase.get();
    }

    public static Expr simplify(Expr expr){
        TriFunction<String, Expr, Expr, Expr> binopcase = (opname, left, right) -> {
            if ("+".equals(opname)){
                if(left instanceof Number && ((Number) left).val == 0){
                    return right;
                }
                if(right instanceof Number && ((Number) right).val == 0){
                    return left;
                }
            }
            if("*".equals(opname)){
                if(left instanceof Number && ((Number) left).val == 1){
                    return right;
                }
                if(right instanceof Number && ((Number) right).val == 1){
                    return left;
                }
            }

            return new BinOp(opname, left, right);
        };

        // 숫자 처리
        Function<Integer, Expr> numcase = val -> new Number(val);
        Supplier<Expr> defaultcase = () -> new Number(0);

        return patternMatchExpr(expr, binopcase, numcase, defaultcase);
    }
}
