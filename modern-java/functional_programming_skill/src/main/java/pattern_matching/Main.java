package pattern_matching;

public class Main {

    private static void simplifyTest(){
        BinOp binOp = new BinOp("+", new Number(5), new Number(0));
        Expr result = Simplify.simplifyExpr(binOp);
        System.out.println(result);
    }

    private static void visitorSimplifyTest(){
        BinOp binOp = new BinOp("+", new Number(5), new Number(0));
        Expr result = binOp.accept(new SimplifyVisitor());
        System.out.println(result);
    }

    private static void patternMatchSimplifyTest(){
        BinOp binOp = new BinOp("+", new Number(5), new Number(0));
        Expr result = PatternMatchSimplify.simplify(binOp);
        System.out.println(result);
    }

    public static void main(String[] args){
//        simplifyTest();
//        visitorSimplifyTest();

        patternMatchSimplifyTest();

    }
}
