package pattern_matching;

public class Number extends Expr{
    int val;

    public Number(int val){
        this.val = val;
    }

    public String toString(){
        return "Number(val=" + val + ")";
    }
}
