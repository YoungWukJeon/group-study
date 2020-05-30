package pattern_matching;

public class SimplifyVisitor {

    public Expr visit(BinOp binOp){
        if("+".equals(binOp.opname) && binOp.right instanceof Number && ((Number) binOp.right).val == 0){
            return binOp.left;
        }
        //..
        return null;
    }
}
