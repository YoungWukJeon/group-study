import java.util.ArrayList;
import java.util.List;

interface Publisher<T> {
    void subscribe(Subscriber<? super T> subscriber);
}

interface Subscriber<T> {
    void onNext(T t);
}

public class SimpleCell implements Publisher<Integer>, Subscriber<Integer> {
    private int value = 0;
    private String name;
    private List<Subscriber> subscribers = new ArrayList<>();

    public SimpleCell(String name) {
        this.name = name;
    }

    @Override
    public void subscribe(Subscriber<? super Integer> subscriber) {
        subscribers.add(subscriber);
    }

    // 새로운 값이 있음을 모든 구독자에게 알리는 메서드
    private void notifyAllSubscribers() {
        subscribers.forEach(subscriber -> subscriber.onNext(this.value));
    }

    @Override
    public void onNext(Integer newValue) {
        this.value = newValue;  // 구독한 셀에 새 값이 생겼을 때 값을 갱신해서 반응함
        System.out.println(this.name + ":" + this.value);   // 값을 콘솔로 출력하지만 실제로는 UI의 셀을 갱신할 수 있음
        notifyAllSubscribers(); // 값이  갱신되었음을 모든 구독자에게 알림
    }
}
