# 리액티브 프로그래밍

- 수년 전까지 대규모 애플리케이션은 수십 대의 서버, 기가바이트의 데이터, 수초의 응답 시간, 당연히 여겨졌던 몇 시간의 유지보수 시간 등의 특징을 가졌다.
- 오늘날에는 다음과 같은 적어도 세 가지 이유로 상황이 변하고 있다.
    - 빅데이터 : 보통 빅데이터는 페타바이트 단위로 구성되며 매일 증가한다.
    - 다양한 환경 : 모바일 디바이스에서 수천 개의 멀티 코어 프로세서로 실행되는 클라우드 기반 클러스터에 이르기까지 다양한 환경에 애플리케이션이 배포된다.
    - 사용 패턴 : 사용자는 1년 내내 항상 서비스를 이용할 수 있으며 밀리초 단위의 응답 시간을 기대한다.
- 리액티브 프로그래밍에서는 다양한 시스템과 소스에서 들어오는 데이터 항목 스트림을 비동기적으로 처리하고 합쳐서 이런 문제를 해결한다.
- 이런 방식으로 구성된 시스템에서는 고장, 정전 같은 상태에 대처할 뿐 아니라 다양한 네트워크 상태에서 메시지를 교환하고 전달할 수 있으며 무거운 작업을 하고 있는 상황에서도 가용성을 제공한다.

## 리액티브 매니페스토(Reactive Manifesto)

- 리액티브 매니페스토([https://www.reactivemanifesto.org](https://www.reactivemanifesto.org))는 2013년과 2014년에 걸쳐 조나스 보너(Jonas Bonér), 데이브 팔리(Dave Farley), 롤랜드 쿤(Roland Kuhn), 마틴 톰슨(Martin Thompson)에 의해 개발되었으며 리액티브 애플리케이션과 시스템 개발의 핵심 원칙을 공식적으로 정의한다.
    - 반응성(responsive) : 리액티브 시스템은 빠를 뿐 아니라 더 중요한 특징으로 일정하고 예상할 수 있는 반응 시간을 제공한다. 결과적으로 사용자가 기대치를 가질 수 있다. 기대치를 통해 사용자의 확신이 증가하면서 사용할 수 있는 애플리케이션이라는 확인을 제공할 수 있다.
    - 회복성(resilient) : 장애가 발생해도 시스템은 반응해야 한다. 컴포넌트 실행 복제, 여러 컴포넌트의 시간(발송자와 수신자가 독립적인 생명주기를 가짐)과 공간(발송자와 수신자가 다른 프로세스에서 실행됨), 각 컴포넌트가 비동기적으로 작업을 다른 컴포넌트에 위임하는 등 리액티브 매니페스토는 회복성을 달성할 수 있는 다양한 기법을 제시한다.
    - 탄력성(elastic) : 애플리케이션의 생명주기 동안 다양한 작업 부하를 받게 되는데 이 다양한 작업 부하로 애플리케이션의 반응성이 위협받을 수 있다. 리액티브 시스템에서는 무거운 작업 부하가 발생하면 자동으로 관련 컴포넌트에 할당된 자원 수를 늘린다.
    - 메시지 주도(Message-driven) : 회복성과 탄력성을 지원하려면 약한 결합, 고립, 위치 투명성 등을 지원할 수 있도록 시싀템을 구성하는 컴포넌트의 경계를 명확하게 정의해야 한다. 비동기 메시지를 전달해 컴포넌트 끼리의 통신이 이루어진다. 이 덕분에 회복성(장애를 메시지로 처리)과 탄력성(주고 받은 메시지의 수를 감시하고 메시지의 양에 따라 적절하게 리소스를 할당)을 얻을 수 있다.

    ![chapter17-01](image/chapter17-01.png '리액티브 시스템의 핵심 기능')

- 애플리케이션 수준의 리액티브
    - 애플리케이션 수준 컴포넌트의 리액티브 프로그래밍의 주요 기능은 비동기로 작업을 수행할 수 있다는 점이다.
    - 이벤트 스트림을 블록하지 않고 비동기로 처리하는 것이 최신 멀티코어 CPU의 사용률을 극대화할 수 있는 방법이다.
    - 이 목표를 달성할 수 있도록 리액티브 프레임워크와 라이브러리는 스레드를 Future, Actor, 일련의 Callback을 발생시키는 이벤트 루프 등과 공유하고 처리할 이벤트를 변환하고 관리한다.
    - 개발자 입장에서 동기 블록, 경쟁 조건, 데드락 같은 저 수준의 멀티스레드 문제를 직접 처리할 필요가 없어지면서 비즈니스 요구사항을 구현하는데 더 집중할 수 있다.
    - 스레드를 다시 쪼개는 종류의 기술을 이용할 때는 메인 이벤트 루프 안에서는 절대 동작을 블록하지 않아야 한다는 중요한 전제 조건이 항상 따른다.
        - 모든 I/O 관련 동작이 블록 동작에 속한다.

    ![chapter17-02](image/chapter17-02.png '블록 동작 때문에 스레드가 다른 동작을 수행하지 못하고 낭비된다.')

    - RxJava, Akka 같은 리액티브 프레임워크는 별도로 지정된 스레드 풀에서 블록 동작을 실행시켜 이 문제를 해결한다.
        - 메인 풀의 모든 스레드는 방해받지 않고 실행되므로 모든 CPU 코어가 가장 최적의 상황에서 동작할 수 있다.
        - CPU 관련 작업과 I/O 관련 작업을 분리하면 조금 더 정밀하게 풀의 크기 등을 설정할 수 있고 두 종류의 작업의 성능을 관찰할 수 있다.
            - CPU 관련 작업은 실제로 할당된 CPU 코어 또는 스레드를 100% 활용해 뭔가를 연산하느라 다른 일을 처리할 수 없어 블록되는 반면, I/O 관련 작업에서는 사용자 입력 같은 외부 응답을 기다리면서 CPU 코어나 스레드가 처리할 일이 없어 블록되는 상황이다.
            - 이런 이유에서 개발자는 CPU가 최대로 활용될 수 있도록 특정 작업이 CPU 관련 작업인지 I/O 관련 작업인지를 적절하게 선택해야 한다.
    - 리액티브 시스템을 만들려면 훌륭하게 설계된 리액티브  애플리케이션 집합이 서로 잘 조화를 이루게 만들어야 한다.
- 시스템 수준의 리액티브
    - **리액티브 시스템**은 여러 애플리케이션이 한 개의 일관적인, 회복할 수 있는 플랫폼을 구성할 수 있게 해줄 뿐 아니라 이들 애플리케이션 중 하나가 실패해도 전체 시스템은 계속 운영될 수 있도록 도와주는 소프트웨어 아키텍처다.
    - 메시지는 정의된 목적지 하나를 향하는 반면, 이벤트는 관련 이벤트를 관찰하도록 등록한 컴포넌트가 수신한다는 점이 다르다.
    - 리액티브 시스템에서는 수신자와 발신자가 각각 수신 메시지, 발신 메시지와 결합하지 않도록 이들 메시지를 비동기로 처리해야 한다.
    - 각 컴포넌트를 완전히 고립하려면 이들이 결합되지 않도록 해야 하며 그래야만 시스템이 장애(**회복성**)와 높은 부하(**탄력성**)에서도 **반응성**을 유지할 수 있다.
    - 리액티브 아키텍처에서는 컴포넌트에서 발생한 장애를 고립시킴으로 문제가 주변의 다른 컴포넌트로 전파되면서 전체 시스템 장애로 이어지는 것을 막음으로 회복성을 제공한다.
        - 이런 맥락에서 회복성은 결함 허용 능력(fault-tolerance)과 같은 의미를 지닌다.
        - 에러를 전파를 방지하고 이들을 메시지로 바꾸어 다른 컴포넌트로 보내는 등 감독자 역할을 수행함으로 이루어진다.
    - 고립과 비결합이 회복성의 핵심이라면 탄력성의 핵심은 위치 투명성이다.
        - 위치 투명성은 리액티브 시스템의 모든 컴포넌트가 수신자의 위치에 상관없이 다른 모든 서비스와 통신할 수 있음을 의미한다.
        - 위치 투명성 덕분에 시스템을 복제할 수 있으며 현재 작업 부하에 따라 (자동으로) 애플리케이션을 확장할 수 있다.
        - 위치를 따지지 않는 확장성은 리액티브 애플리케이션(시간에 기반한 비동기, 동시적, 비결합)과 리액티브 시스템(위치 투명성을 통한 공간적 비결합할 수 있음)의 또 다른 차이를 보여준다.

## 리액티브 스트림과 Flow API

- **리액티브 프로그래밍**은 리액티브 스트림을 사용하는 프로그래밍이다.
- 리액티브 스트림은 잠재적으로 무한의 비동기 데이터를 순서대로 그리고 블록하지 않는 역압력을 전제해 처리하는 표준 기술이다.
- **역압력(BackPressure)**은 발행-구독 프로토콜에서 이벤트 스트림의 구독자가 발행자가 이벤트를 제공하는 속도보다 느린 속도로 이벤트를 소비하면서 문제가 발생하지 않도록 보장하는 장치다.
- 스트림 처리의 비동기적인 특성상 역압력 기능의 내장은 필수라는 사실을 알 수 있다.
- 넷플릭스, 레드햇, 트위터, 라이트벤드(lightbend) 및 기타 회사들이 참여한 리액티브 스트림 프로젝트(Reactive Streams Project, [http://www.reactive-streams.org](http://www.reactive-streams.org))에서는 모든 리액티브 스트림 구현이 제공해야 하는 최소 기능 집합을 네 개의 관련 인터페이스로 정의했다.
- Java9의 새로운 java.util.concurrent.Flow 클래스뿐 아니라 Akka 스트림(라이트벤드), 리액터(Reactor, Pivotal), RxJava(넷플릭스), Vert.x(레드햇) 등 많은 서드 파티 라이브러리에서 이들 인터페이스를 구현했다.
- Flow 클래스 소개
    - Java9에서는 리액티브 프로그래밍을 제공하는 클래스 java.util.concurrent.Flow를 추가했다.
    - 이 클래스는 정적 컴포넌트 하나를 포함하고 있으며 인스턴스화할 수 없다.
    - 리액티브 스트림 프로젝트의 표준에 따라 프로그래밍 발행-구독 모델을 지원할 수 있도록 Flow 클래스는 중첩된 인터페이스 네 개를 포함한다.
        - Publisher
        - Subscriber
        - Subscription
        - Processor
    - Publisher가 항목을 발행하면 Subscriber가 한 개씩 또는 한 번에 여러 항목을 소비하는데 Subscription이 이 과정을 관리할 수 있도록 Flow 클래스는 관련된 인터페이스와 정적 메서드를 제공한다.
        - Publisher는 수많은 일련의 이벤트를 제공할 수 있지만 Subscriber의 요구사항에 따라 역압력 기법에 의해 이벤트 제공 속도가 제한된다.
    - Publisher는 Java의 함수형 인터페이스로, Subscriber는 Publisher가 발행한 이벤트의 리스너로 자신을 등록할 수 있다.
    - Subscription은 Publisher와 Subscriber 사이의 제어 흐름, 역압력을 관리한다.

    ```java
    @FunctionalInterface
    public interface Publisher<T> {
    	void subscribe(Subscriber<? super T> s);
    }

    public interface Subscribe<T> {
    	void onSubscribe(Subscription s);
    	void onNext(T t);
    	void onError(Throwable t);
    	void onComplete();
    }

    public interface Subscription {
    	void request(long n);
    	void cancel();
    }

    public interface Processor<T, R> extends Subscriber<T>, Publisher<R> {}
    ```

    - Subscriber의 경우
        - 이들 이벤트는 다음 프로토콜에서 정의한 순서로 지정된 메서드 호출을 통해 발행되어야 한다.
            - onSubscribe onNext* (onError | onComplete)?
                - onSubscribe : 항상 처음 호출됨을 의미
                - onNext* : 여러 번 호출될 수 있음을 의미
                - (onError | onComplete)? : 이벤트 스트림은 영원히 지속되거나  아니면 onComplete 콜백을 통해 더 이상의 데이터가 없고 종료됨에 알릴 수 있으며 또는 Publisher에 장애가 발생했을 때는 onError를 호출할 수 있다.
    - Java9 Flow 명세서에서는 이들 인터페이스 구현이 어떻게 서로 협력해야 하는지를 설명하는 규칙 집합을 정의한다.
        - Publisher는 반드시 Subscription의 request 메서드에 정의된 개수 이하의 요소만 Subscriber에 전달해야 한다.
        - Subscriber는 요소를 받아 처리할 수 있음을 Publisher에 알려야 한다.
            - 이런 방식으로 Subscriber는 Publisher에 역압력을 행사할 수 있고 Subscriber가 관리할 수 없이 너무 많은 요소를 받는 일을 피할 수 있다.
        - Publisher와 Subscriber는 정확하게 Subscription을 공유해야 하며 각각이 고유한 역할을 수행해야 한다.
            - 그러려면 onSubscribe와 onNext 메서드에서 Subscriber는 request 메서드를 동기적으로 호출할 수 있어야 한다.

    ![chapter17-03](image/chapter17-03.png 'Flow API를 사용하는 리액티브 애플리케이션의 생명주기')

    - Processor 인터페이스는 단지 Publisher와 Subscriber를 상속받을 뿐 아무 메서드도 추가하지 않는다.
        - 실제 이 인터페이스는 리액티브 스트림에서 처리하는 이벤트의 변환 단계를 나타낸다.
        - Processor가 에러를 수신하면 이로부터 회복하거나 즉시 onError 신호로 모든 Subscriber에 에러를 전파할 수 있다.
    - Java9 Flow API/리액티브 스트림 API에서는 Subscriber 인터페이스의 모든 메서드 구현이 Publisher를 블록하지 않도록 강제하지만 이들 메서드가 이벤트를 동기적으로 처리해야 하는지 아니면 비동기적으로 처리해야 하는지는 지정하지 않는다.
    - 하지만 이들 인터페이스에 정의된 모든 메서드는 void를 반환하므로 온전히 비동기 방식으로 이들 메서드를 구현할 수 있다.
- 첫 번째 리액티브 애플리케이션 만들기
    - Flow 클래스에 정의된 인터페이스 대부분은 직접 구현하도록 의도된 것이 아니다.
    - 그럼에도 Java9 라이브러리는 이들 인터페이스를 구현하는 클래스를 제공하지 않는다.
        - Akka, RxJava 등의 리액티브 라이브러리에서는 이들 인터페이스를 구현했다.
    - Java9 java.util.concurrent.Flow 명세는 이들 라이브러리가 준수해야 할 규칙과 다양한 리액티브 라이브러리를 이용해 개발된 리액티브 애플리케이션이 서로 협동하고 소통할 수 있는 공용어를 제시한다.
    - 예시를 통해 지금까지 배운 네 개의 인터페이스가 어떻게 동작하는지 쉽게 확인할 수 있다.
        - TempInfo : 원격 온도계를 흉내낸다. (0~99 사이의 화씨 온도를 임의로 만들어 연속적으로 보고)
        - TempSubscriber : 레포트를 관찰하면서 각 도시에 설치된 센서에서 보고한 온도 스트림을 출력한다.

    ```java
    import java.util.Random;

    public class TempInfo {
    	public static final Random random = new Random();

    	private final String town;
    	private final int temp;

    	public TempInfo(String town, int temp) {
    		this.town = town;
    		this.temp = temp;
    	}

    	// 정적 팩토리 메서드를 이용해 해당 도시의 TempInfo 인스턴스를 만든다.
    	public static TempInfo fetch(String town) { // 1/10 확률로 온도 가져오기 작업이 실패
    		if (random.nextInt(10) == 0) {
    			throw new RuntimeException("Error!");
    		}
    		return new TempInfo(town, random.nextInt(100)); // 0~99 사이에서 임의의 화씨 온도를 반환
    	}

    	@Override
    	public String toString() {
    		return town + " : " + temp;
    	}

    	public int getTemp() {
    		return temp;
    	}

    	public String getTown() {
    		return town;
    	}
    }
    ```

    ```java
    import java.util.concurrent.Flow.*;

    public class TempSubscription implements Subscription {
    	private final Subscriber<? super TempInfo> subscriber;
    	private final String town;

    	public TempSubscription(Subscriber<? super TempInfo> subscriber, String town) {
    		this.subscriber = subscriber;
    		this.town = town;
    	}

    	@Override
    	public void request(long n) {
    		for (long i = 0L; i < n; i++) {
    			try { // Subscriber가 만든 요청을 한 개씩 반복
    				subscriber.onNext(TempInfo.fetch(town)); // 현재 온도를 Subscriber로 전달
    			} catch (Exception e) {
    				subscriber.onError(e); // 온도 가져오기를 실패하면 Subscriber로 에러를 전달
    				break;
    			}
    		}
    	}

    	@Override
    	public void cancel() {
    		subscriber.onComplete(); // 구독이 취소되면 완료(onComplete) 신호를 Subscriber에 전달
    	}
    }
    ```

    ```java
    import java.util.concurrent.Flow.*;

    public class TempSubscriber implements Subscriber<TempInfo> {
    	private Subscription subscription;

    	@Override
    	public void onSubscribe(Subscription subscription) { // 구독을 저장하고 첫 번째 요청을 전달
    		this.subscription = subscription;
    		subscription.request(1);
    	}

    	@Override
    	public void onNext(TempInfo tempInfo) { // 수신한 온도를 출력하고 다음 정보를 요청
    		System.out.println(tempInfo);
    		subscription.request(1);
    	}

    	@Override
    	public void onError(Throwable t) { // 에러가 발생하면 에러 메시지 출력
    		System.err.println(t.getMessage());
    	}

    	@Override
    	public void onComplete() {
    		System.out.println("Done!");
    	}
    }
    ```

    ```java
    import java.util.concurrent.Flow.*;

    public class Main {
    	public static void main(String[] args) {
    		getTemperatures("New York").subscribe(new TempSubscriber()); // 뉴욕에 새 Publisher를 만들고 TempSubscriber를 구독시킴
    	}

    	private static Publisher<TempInfo> getTemperatures(String town) { // 구독한 Subscriber에게 TempSubscription을 전송하는 Publisher를 반환
    		return subscriber -> subscriber.onSubscribe(new TempSubscription(subscriber town));
    	}
    }
    ```

    ```
    New York : 44
    New York : 68
    New York : 95
    New York : 30
    Error!
    ```

    - 여기서 TempInfo 팩토리 메서드(fetch) 내에서 에러를 임의로 발생시키는 코드를 없앤 다음 main을 오래 실행하면 어떤 일이 일어날까?

        ```
        Exception in thread "main" java.lang.StackOverflowError
        	at java.base/java.io.PrintStream.print(PrintStream.java:666)
        	at java.base/java.io.PrintStream.println(PrintStream.java:820)
        	at flow.TempSubscriber.onNext(TempSubscriber.java:36)
        	at flow.TempSubscriber.onNext(TempSubscriber.java:24)
        	at flow.TempSubscription.request(TempSubscription.java:60)
        	at flow.TempSubscriber.onNext(TempSubscriber.java:37)
        	at flow.TempSubscriber.onNext(TempSubscriber.java:24)
        	at flow.TempSubscription.request(TempSubscription.java:60)
        ```

        - Executor를 TempSubscription으로 추가한 다음 다른 스레드에서 TempSubscriber로 새 요소를 전달하는 방법이 있다.

        ```java
        import java.util.concurrent.ExecutorService;
        import java.util.concurrent.Executors;

        public class TempSubscription implements Subscription {
        	... // 기존 TempSubscription 코드 생략
        	
        	private static final ExecutorService executor = Executors.newSingleThreadExecutor();
        	
        	@Override
        	public void request(long n) { // 다른 스레드에서 다음 요소를 구독자에게 보낸다.
        		executor.submit(() -> {
        			for (long i = 0L; i < n; i++) {
        				try {
        					subscriber.onNext(TempInfo.fetch(town));
        				} catch (Exception e) {
        					subscriber.onError(e);
        					break;
        				}
        			}
        		});	
        	}
        }
        ```

- Processor로 데이터 변환하기
    - 사실 Processor의 목적은 Publisher를 구독한 다음 수신한 데이터를 가공해 다시 제공하는 것이다.
    - 화씨가 아닌 섭씨로 온도를 보고하는 Publisher를 만들어보자.

    ```java
    import java.util.concurrent.Flow.*;

    public class TempProcessor implements Processor<TempInfo, TempInfo> { // TempInfo를 다른 TempInfo로 변환하는 프로세서
    	private Subscriber<? super TempInfo> subscriber;

    	@Override
    	public void subscribe(Subscriber<? super TempInfo> subscriber) {
    		this.subscriber = subscriber;
    	}	

    	@Override
    	public void onNext(TempInfo temp) {
    		subscriber.onNext(new TempInfo(temp.getTown(), (temp.getTemp() - 32) * 5 / 9)); // 섭씨로 변환한 다음 TempInfo를 다시 전송
    	}

    	@Override
    	public void onSubscribe(Subscription subscription) { // 다른 모든 신호는 업스트림 구독자에게 전달
    		subscriber.onSubscribe(subscription);
    	}
    	
    	@Override
    	public void onError(Throwable throwable) { // 다른 모든 신호는 업스트림 구독자에게 전달
    		subscriber.onError(throwable);
    	}

    	@Override
    	public void onComplete() { // 다른 모든 신호는 업스트림 구독자에게 전달
    		subscriber.onComplete();
    	}
    }
    ```

    ```java
    import java.util.concurrent.Flow.*;

    public class Main {
    	public static void main(String[] args) {
    		getCelsiusTemperatures("New York") // 뉴욕의 섭씨 온도를 전송한 Publisher를 만듦
    			.subscribe(new TempSubscriber()); // TempSubscriber를 Publisher로 구독
    	}

    	public static Publisher<TempInfo> getCelsiusTemperatures(String town) {
    		return subscriber -> {
    			TempProcessor processor = new TempProcessor(); // TempProcessor를 만들고 Subscriber와 반환된 Publisher 사이로 연결
    			processor.subscribe(subscriber);
    			processor.onSubscribe(new TempSubscription(processor, town));
    		};
    	}
    }
    ```

    ```
    New York : 10
    New York : -12
    New York : 23
    Error!
    ```

- Java는 왜 Flow API 구현을 제공하지 않는가?
    - Java9에서는 `Publisher<T>` 인터페이스만 선언하고 구현을 제공하지 않으므로 직접 인터페이스를 구현해야 한다.
    - 인터페이스가 프로그래밍 구조를 만드는데 도움이 될 순 있지만 프로그램을 더 빨리 구현하는 데는 도움이 되지 않는다.
    - API를 만들 당시 Akka, RxJava 등 다양한 리액티브 스트림의 Java 코드 라이브러리가 이미 존재했기 때문이다.
        - 이들 라이브러리는 같은 발행-구독 사상에 기반해 리액티브 프로그래밍을 구현했지만, 독립적으로 개발되었고 서로 다른 이름규칙과 API를 사용했다.
    - Java9의 표준화 과정에서 기존처럼 자신만의 방법이 아닌 이들 라이브러리는 공식적으로 java.util.concurrent.Flow의 인터페이스를 기반으로 리액티브 개념을 구현하도록 진화했다.

## 리액티브 라이브러리 RxJava 사용하기

- RxJava는 Java로 리액티브 애플리케이션션을 구현하는 데 사용하는 라이브러리다.
- RxJava는 기반한 넷플릭스의 Reactive Extensions(Rx) 프로젝트(원래 마이크로소프트 닷넷 환경에서 개발했던 프로젝트)의 일부로 시작되었다.
- Reactive Streams API와 Java9에 적용된 java.util.concurrent.Flow를 지원하도록 RxJava 2.0 버전이 개발되었다.
- 좋은 시스템 아키텍처 스타일을 유지하려면 시스템에서 오직 일부에 사용된 개념의 세부 사항을 전체 시스템에서 볼 수 있게 만들지 않아야 한다.
- 따라서 Observable의 추가 구조가 필요한 상황에서만 Observable을 사용하고 그렇지 않으면 Publisher의 인터페이스를 사용하는 것이 좋다.
- RxJava는 Flow.Publisher를 구현하는 두 클래스를 제공한다.
    - RxJava 문서를 읽다보면 Java9에서 리액티브 당김 기반 역압력 기능(request  메서드)이 있는 Flow를 포함하는 io.reactivex.Flowable 클래스를 확인할 수 있다.
    - 역압력은 Publisher가 너무 빠른 속도로 데이터를 발행하면서 Subscriber가 이를 감당할 수 없는 상황에 이르는 것을 방지하는 기능이다.
    - 나머지 클래스는 역압력을 지원하지 않는 기존 버전의 RxJava에서 제공하던 Publisher io.reactivex.Observable 클래스다.
        - 이 클래스는 단순한 프로그램, 마우스 움직임 같은 사용자 인터페이스 이벤트에 더 적합하다.
        - 마우스 움직임을 느리게 하거나 멈출 수 없기 때문에 이들 이벤트 스트림에는 역압력을 적용하기 어렵기 때문이다.
        - RxJava는 천 개 이하의 요소를 가진 스트림이나 마우스 움직임, 터치 이벤트 등 역압력을 적용하기 힘든 GUI 이벤트 그리고 자주 발생하지 않는 종류의 이벤트에 역압력을 적용하지 말 것을 권장한다.
- 모든 구독자는 구독 객체의 request(Long.MAX_VALUE) 메서드를 이용해 역압력 기능을 끌 수 있다.
    - 물론 Subscriber가 정해진 시간 안에 수신한 모든 이벤트를 처리할 수 있다고 확신할 수 있는 상황이 아니라면 역압력 기능을 끄지 않는 것이 좋다.
- Observable 만들고 사용하기
    - just() 팩토리 메서드는 한 개 이상의 요소를 이용해 이를 방출하는 Observable로 변환한다.

        ```java
        Observable<String> strings = Observable.just("first", "second");
        ```

        - Observable의 구독자는 onNext("first"), onNext("second"), onComplete()의 순서로 메시지를 받는다.
    - 사용자와 실시간으로 상호작용하면서 지정된 속도로 이벤트를 방출하는 상황에서 유용하게 사용할 수 있는 다른 Observable 팩토리 메서드도 있다.

        ```java
        Observable<Long> onePerSec = Observable.interval(1, TimeUnit.SECONDS);
        ```

        - 팩토리 메서드 interval은 onePerSec라는 변수로 Observable을 반환해 할당한다.
        - 이 Observable은 0에서 시작해 1초 간격으로 long 형식의 값을 무한으로 증가시키며 값을 방출한다.
    - RxJava에서 Observable이 Flow API의 Publisher 역할을 하며 Observer는 Flow의 Subscriber 인터페이스 역할을 한다.
        - RxJava Observer 인터페이스는 Java9 Subscriber와 같은 메서드를 정의하며 onSubscribe 메서드가 Subscription 대신 Disposable 인수를 갖는다는 점만 다르다.
        - Observable은 역압력을 지원하지 않으므로 Subscription의 request 메서드를 포함하지 않는다.

        ```java
        public interface Observer<T> {
        	void onSubscribe(Disposable d);
        	void onNext(T t);
        	void onError(Throwable t);
        	void onComplete();
        }
        ```

        - RxJava의 API는 Java9 Native Flow API보다 유연하다.
            - 많은 오버로드된 기능을 제공
            - 예를 들어 다른 세 메서드는 생략하고 onNext 메서드의 시그니처에 해당하는 람다 표현식을 전달해 Observable을 구독할 수 있다.
            - 즉 이벤트를 수신하는 Consumer의  onNext 메서드만 구현하고 나머지 완료, 에러 처리 메서드는 아무것도 하지 않는 기본 동작을 가진 Observer를 만들어  Observable에 가입할 수 있다.
            - 이 기능을 활용하면 Observable onePerSec에 가입하고 뉴욕에서 매 초마다 발생하는 온도를 출력하는 기능을 코드 한 줄로 구현할 수 있다.

            ```java
            onePerSec.subscribe(i -> System.out.println(TempInfo.fetch("New York")));
            ```

            - 위 코드를 main 메서드에 추가해서 실제 실행해보면 아무것도 출력되지 않는데 이는 매 초마다 정보를 발행하는 Observable이 RxJava의 연산 스레드 풀 즉 데몬 스레드에서 실행되기 때문이다.
            - 위 코드 뒤에 스레드의 sleep 메서드를 추가해 프로그램이 종료되는걸 막는 방법도 있다.
            - 현재 스레드(예제에서는 메인 스레드)에서 콜백을 호출하는 blockingSubscribe 메서드를 사용하면 더 깔끔하게 문제를 해결할 수 있다.

            ```java
            onePerSec.blockingSubscribe(
            	i -> System.out.println(TempInfo.fetch("New York"))
            );
            ```

            ```
            New York : 87
            New York : 18
            New York : 75
            java.lang.RuntimeException: Error!
            	at flow.common.TempInfo.fetch(TempInfo.java:18)
            	at flow.Main.lambda$0(Main.java:12)
            	at io.reactivex.internal.observers.LambdaObserver
            		.onNext(LambdaObserver.java:59)
            	at io.reactivex.internal.operators.observable
            		.ObservableInterval$IntervalObserver.run(ObservableInterval.java:74)
            ```

            - 예제에서 구현한 Observer는 onError 같은 에러 관리 기능을 포함하지 않으므로 위와 같은 처리되지 않은 예외가 사용자에게 직접 보여진다.
        - 온도를 직접 출력하지 않고 사용자에게 팩토리 메서드를 제공해 매 초마다 온도를 방출(편의상 최대 다섯 번 온도를 방출하고 종료시킴)하는 Observable을 반환할 것이다.

            ```java
            public static Observable<TempInfo> getTemperature(String town) {
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
            ```

            - 필요한 이벤트를 전송하는 ObservableEmitter를 소비하는 함수로 Observable을 만들어 반환했다.
            - RxJava의 ObservableEmitter 인터페이스는 RxJava의 기본 Emitter(onSubscribe 메서드가 빠진 Observer와 같음)를 상속한다.

            ```java
            public interface Emitter<T> {
            	void onNext(T t);
            	void onError(Throwable t);
            	void onComplete();
            }
            ```

            - Emitter는 새 Disposable을 설정하는 메서드와 시퀀스가 이미 다운스트림을 폐기했는지 확인하는 메서드 등을 제공한다.

            ```java
            import io.reactivex.Observer;
            import io.reactivex.disposables.Disposable;

            public class TempObserver implements Observer<TempInfo> {
            	@Override
            	public void onComplete() {
            		System.out.println("Done!");
            	}

            	@Override
            	public void onError(Throwable throwable) {
            		System.out.println("Got problem: " + throwable.getMessage());
            	}

            	@Override
            	public void onSubscribe(Disposable disposable) {}

            	@Override
            	public void onNext(TempInfo tempInfo) {
            		System.out.println(tempInfo);
            	}
            }
            ```

            - RxJava의 Observable은 역압력을 지원하지 않으므로 전달된 요소를 처리한 다음 추가 요소를 요청하는 request() 메서드가 필요 없기 때문이다.

            ```java
            public class Main {
            	public static void main(String[] args) {
            		Observable<TempInfo> observable = getTemperature("New York"); // 매 초마다 뉴욕의 온도 보고를 방출하는 Observable 만들기
            		observable.blockingSubscribe(new TempObserver()); // 단순 Observer로 이 Observable에 가입해서 온도 출력하기
            	}
            }
            ```

            ```
            New York : 69
            New York : 26
            New York : 85
            New York : 94
            New York : 29
            Done!
            ```

- Observable을 변환하고 합치기
    - RxJava나 기타 리액티브 라이브러리는 Java9 Flow API에 비해 스트림을 합치고, 만들고, 거르는 등의 풍부한 도구상자를 제공하는 것이 장점이다.
    - 스트림에서 관심있는 요소만 거른 다른 스트림을 만들거나 매핑 함수로 요소를 변환하거나 두 스트림을 다양한 방법으로 합치는 등의 작업을 할 수 있다.
        - Flow.Processor만으로는 달성하기 어려운 일
    - RxJava의 mergeDelayError 함수 관련 설명

        > Observable을 한 Observable로 방출하는 Observable을 평면화해서 모든 Observable 소스에서 성공적으로 방출된 모든 항목을 Observer가 수신할 수 있도록 한다. 이때 이들 중 에러 알림이 발생해도 방해받지 않으며 이들 Observable에 동시 구독할 수 있는 숫자에는 한계가 있다.

        - 위 설명은 아무리 읽어봐도 이해하기 어렵다.
        - 리액티브 스트림 커뮤니티는 마블 다이어그램(marble diagram)이라는 시각적 방법을 이용해 이런 어려움을 해결하고자 노력한다.
    - 마블 다이어그램은 수평선으로 표시된 리액티브 스트림에 임의의 순서로 구성된 요소가 기하학적 모형이 나타난다.
        - 특수 기호는 에러나 완료 신호를 나타낸다.
        - 박스는 해당 연산이 요소를 어떻게 변화하거나 여러 스트림을 어떻게 합치는지 보여준다.

    ![chapter17-04](image/chapter17-04.png '리액티브 라이브러리에서 제공하는 연산자 문서화에 일반적으로 사용되는 마블 다이어그램 범례')

    - 이 표기법을 이용하면 모든 RxJava 라이브러리의 함수를 시각적으로 표현할 수 있다.

    ![chapter17-05](image/chapter17-05.png 'map, merge 함수의 마블 다이어그램')

    ```java
    public static Observable<TempInfo> getCelsiusTemperature(String town) {
    	return getTemperature(town)
    		.map(temp -> new TempInfo(temp.getTown(), (temp.getTemp() - 32) * 5 / 9));
    }
    ```

    - 영하 온도만 거르기
        - Observable 클래스의 filter 메서드는 Predicate를 인수로 받아 Predicate 조건을 만족하는 요소만 방출하는 두 번째 Observable을 만든다.
        - 사용자가 동상에 걸릴 위험이 있을 때 알려주는 경고 시스템을 개발해달라고 누군가 부탁했다고 가정하자.
            - 섭씨 0도 이하일 때만 온도가 방출되도록 Observable을 구현

        ```java
        public static Observable<TempInfo> getNegativeTemperature(String town) {
        	return getCelsiusTemperature(town)
        		.filter(temp -> temp.getTemp() < 0);
        }
        ```

    - 마지막으로 구현한 메서드를 일반화해서 사용자가 한 도시뿐 아니라 여러 도시에서 온도를 방출하는 Observable을 가질 수 있도록 해야 한다고 가정하자.

        ```java
        public static Observable<TempInfo> getCelsiusTemperatures(String.. towns) {
        	return Observable.merge(Arrays.stream(towns)
        		.map(TempObservable::getCelsiusTemperature)
        		.collect(toList()));
        }
        ```

        - 이런식으로 각 도시는 매 초마다 도시의 온도를 방출하는 Observable로 변신한다.
        - Observable의 스트림은 리스트로 모아지며 다시 리스트는 Observable 클래스가 제공하는 정적 팩토리 메서드 merge로 전달된다.
        - 이 메서드는 Observable의 Iterable을 인수로 받아 마치 한 개의 Observable처럼 동작하도록 결과를 합친다.
        - 즉, 결과 Observable은 전달된 Iterable에 포함된 모든 Observable의 이벤트 발행물을 시간 순서대로 방출한다.

        ```java
        public class Main {
        	public static void main(String[] args) {
        		Observable<TempInfo> observable = getCelsiusTemperatures(
        			"New York", "Chicago", "San Francisco");
        		observable.blockingSubscribe(new TempObserver());
        	}
        }
        ```

        ```
        New York : 21
        Chicago : 6
        San Francisco : -15
        New York : -3
        Chicago : 12
        San Francisco : 5
        Got problem : Error!
        ```