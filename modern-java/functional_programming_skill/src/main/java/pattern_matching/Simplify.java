package pattern_matching;

public class Simplify {
    /** 5+0 을 단순화 한다면 ..*/
    public static Expr simplifyExpr(Expr expr){
        if(expr instanceof BinOp && ((BinOp) expr).opname.equals("+")
            && ((BinOp) expr).right instanceof Number
            && ((BinOp) expr).left instanceof Number){
            if ( ((Number) ((BinOp) expr).right).val == 0){
                return ((BinOp) expr).left;
            }
            //..
        }
        //...

        return null;
    }


}
