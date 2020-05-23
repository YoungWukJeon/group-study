package _2;

import java.util.concurrent.Flow.*;

public class Main {
    public static void main(String[] args) {
        getTemperatures("New York").subscribe(new TempSubscriber());    // 뉴욕에 새 Publisher를 만들고 TempSubscriber를 구독시킴
//        getCelsiusTemperatures("New York")  // 뉴욕의 섭씨 온도를 전송한 Publisher를 만듦
//                .subscribe(new TempSubscriber());   // TempSubscriber를 Publisher로 구독
    }

    private static Publisher<TempInfo> getTemperatures(String town) {   // 구독한 Subscriber에게 TempSubscription을 전송하는 Publisher를 반환
        return subscriber -> subscriber.onSubscribe(new TempSubscription(subscriber, town));
    }

    private static Publisher<TempInfo> getCelsiusTemperatures(String town) {
        return subscriber -> {
            TempProcessor processor = new TempProcessor(); // TempProcessor를 만들고 Subscriber와 반환된 Publisher 사이로 연결
            processor.subscribe(subscriber);
            processor.onSubscribe(new TempSubscription(processor, town));
        };
    }
}
