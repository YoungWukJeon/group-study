package _2;

import java.util.concurrent.Flow.*;

// TempInfo를 다른 TempInfo로 변환하는 프로세서
public class TempProcessor implements Processor<TempInfo, TempInfo> {
    private Subscriber<? super TempInfo> subscriber;

    @Override
    public void subscribe(Subscriber<? super TempInfo> subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public void onNext(TempInfo temp) {
        subscriber.onNext(
                new TempInfo(temp.getTown(), toCelsiusTemperature(temp.getTemp()))
        ); // 섭씨로 변환한 다음 TempInfo를 다시 전송
    }

    private int toCelsiusTemperature(int temp) {
        return (temp - 32) * 5 / 9;
    }

    @Override
    public void onSubscribe(Subscription subscription) {    // 다른 모든 신호는 업스트림 구독자에게 전달
        subscriber.onSubscribe(subscription);
    }

    @Override
    public void onError(Throwable throwable) {  // 다른 모든 신호는 업스트림 구독자에게 전달
        subscriber.onError(throwable);
    }

    @Override
    public void onComplete() {  // 다른 모든 신호는 업스트림 구독자에게 전달
        subscriber.onComplete();
    }
}
