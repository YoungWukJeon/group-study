package _3;

import io.reactivex.Observable;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        Observable<String> strings = Observable.just("first", "second");
        strings.subscribe(System.out::println);
//        Observable<Long> onePerSec = Observable.interval(1, TimeUnit.SECONDS);
//        onePerSec.subscribe(i -> System.out.println(TempInfo.fetch("New York")));
//        onePerSec.blockingSubscribe(i -> System.out.println(TempInfo.fetch("New York")));
//        Observable<TempInfo> observable = getTemperature("New York"); // 매 초마다 뉴욕의 온도 보고를 방출하는 Observable 만들기
//        Observable<TempInfo> observable = getCelsiusTemperature("New York");  // 섭씨 온도를 보고
//        Observable<TempInfo> observable = getNegativeTemperature("New York");   // 영하 온도에서만 보고
//        Observable<TempInfo> observable = getCelsiusTemperatures("New York", "Chicago", "San Francisco");
//        observable.blockingSubscribe(new TempObserver());   // 단순 Observer로 이 Observable에 가입해서 온도 출력하기
    }

    private static Observable<TempInfo> getTemperature(String town) {
        return Observable.create(emitter -> // Observer를 소비하는 함수로부터 Observable 만들기
                Observable.interval(1, TimeUnit.SECONDS) // 매 초마다 무한으로 증가하는 일련의 long 값을 방출하는 Observable
                        .subscribe(i -> {
                            if (!emitter.isDisposed()) { // 소비된 옵저버가 아직 폐기되지 않았으면 어떤 작업을 수행(이전 에러)
                                if (i >= 5) { // 온도를 다섯 번 보고했으면 옵저버를 완료하고 스트림을 종료
                                    emitter.onComplete();
                                } else {
                                    try {
                                        emitter.onNext(TempInfo.fetch(town)); // 아니면 온도를 Observer로 보고
                                    } catch (Exception e) {
                                        emitter.onError(e); // 에러가 발생하면 Observer에 알림
                                    }
                                }
                            }
                        }));
    }

    private static Observable<TempInfo> getCelsiusTemperature(String town) {
        return getTemperature(town)
                .map(temp -> new TempInfo(temp.getTown(), toCelsiusTemperature(temp.getTemp())));
    }

    private static int toCelsiusTemperature(int temp) {
        return (temp - 32) * 5 / 9;
    }

    private static Observable<TempInfo> getNegativeTemperature(String town) {
        return getCelsiusTemperature(town)
                .filter(temp -> temp.getTemp() < 0);
    }

    private static Observable<TempInfo> getCelsiusTemperatures(String... towns) {
        return Observable.merge(
                Arrays.stream(towns)
                        .map(Main::getCelsiusTemperature)
                        .collect(Collectors.toList())
        );
    }
}
