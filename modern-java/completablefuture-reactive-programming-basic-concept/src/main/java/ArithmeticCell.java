public class ArithmeticCell extends SimpleCell {
    private int left;
    private int right;

    public ArithmeticCell(String name) {
        super(name);
    }

    public void setLeft(int left) {
        this.left = left;
        onNext(left + this.right);  // 셀 값을 갱신하고 모든 구독자에 알림
    }

    public void setRight(int right) {
        this.right = right;
        onNext(right + this.left);  // 셀 값을 갱신하고 모든 구독자에 알림
    }
}
