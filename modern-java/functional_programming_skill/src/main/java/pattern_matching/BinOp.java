package pattern_matching;

public class BinOp extends Expr {
    String opname;
    Expr left;
    Expr right;

    public BinOp(String opname, Expr left, Expr right){
        this.opname = opname;
        this.left = left;
        this.right = right;
    }

    public Expr accept(SimplifyVisitor visitor){
        return visitor.visit(this);
    }

    public String toString(){
        return "BinOp(left=" + left.toString() + ", opname=" + opname + ", right=" + right.toString() +")";
    }
}
