## 동시성을 구현하는 자바 지원의 진화

- Java는 Runnable과 Thread를 동기화된 클래스와 메서드를 이용해 잠갔다.
- Java5는 좀 더 표현력있는 동시성을 지원하는 특히 스레드 실행과 태스크 제출을 분리하는 ExecutorService 인터페이스, 높은 수준의 결과 즉, Runnable, Thread의 변형을 반환하는 Callback<T> and Future<T>, 제네릭 등을 지원했다.
- Java7에서는 분할 그리고 정복 알고리즘의 포크/조인 구현을 지원하는 java.util.concurrent.RecursiveTask가 추가되었다.
- Java8에서는 스트림과 새로 추가된 람다 지원에 기반한 병렬 프로세싱이 추가되었다.
- Java9에서는 분산 비동기 프로그래밍을 명시적으로 지원한다.
- 다양한 웹 서비스를 이용하고 이들 정보를 실시간으로 조합해 사용자에게 제공하거나 추가 웹 서비스를 통해 제공하는 종류의 애플리케이션을 개발하는데 필수적인 기초모델과 툴킷을 제공
    - 이 과정을 리액티브 프로그래밍이라고 부름
    - Java9에서는 발행-구독 모델(java.util.concurrent.Flow 인터페이스 추가)로 이를 지원
- CompletableFuture와 java.util.concurrent.Flow의 궁극적인 목표는 가능한한 동시에 실행할  수 있는 독립적인 태스크를 가능하게 만들면서 멀티코어 또는 여러 기기를 통해 제공되는 병렬성을 쉽게 이용하는 것이다.
- 스레드와 높은 수준의 추상화

    ```java
    // 단일 코어
    long sum = 0;
    for (int i = 0; i < 1; 1_000_000; i++) {
    	sum += i;
    }

    // 멀티 스레드(4개의 스레드 이용)
    // 1번째 스레드
    long sum0 = 0;
    for (int i = 0; i < 250_000; i++) {
    	sum0 += i;
    }
    ...
    // 4번째 스레드
    long sum3 = 0;
    for (int i = 750_000; i < 1_000_000; i++) {
    	sum3 += 0;
    }
    // 각 스레드를 start()로 실행한 후 join()으로 완료될 때까지 기다림
    sum = sum0 + sum1 + sum2 + sum3;

    // Java8 스트림 이용
    sum = LongStream.range(0, 1_000_000).parallel().sum();
    ```

    - 단일 코어로 실행하게 되면 작업의 복잡도에 따라 오랜 시간이 걸린다.
    - 멀티 스레드를 사용하게 되면 공유 변수나 외부 반복(명시적 루프)에 대한 처리 등 고려해야할 것이 많아진다.
    - 스트림을 이용하면 스레드 사용 패턴을 추상화할 수 있다.
        - 쓸모 없는 코드가 라이브러리 내부로 구현되면서 복잡성도 줄어든다는 장점이 더해진다.
- Executor와 스레드 풀
    - 스레드의 문제
        - Java 스레드는 직접 운영체제 스레드에 접근한다.
        - 운영체제 스레드의 숫자는 제한되어 있는 것이 문제다.
        - 운영체제가 지원하는 스레드 수를 초과해 사용하면 Java 애플리케이션이 예상치 못한 방식으로 크래시될 수 있으므로 기존 스레드가 실행되는 상태에서 계속 새로운 스레드를 만드는 상황이 일어나지 않도록 주의해야 한다.
        - 다양한 기기에서 실행할 수 있는 프로그램에서는 미리 하드웨어 스레드 개수를 추측하지 않는 것이 좋다.
        - 한편 주어진 프로그램에서 사용할 최적의 Java 스레드 개수는 사용할 수 있는 하드웨어 코어의 개수에 따라 달라진다.
    - 스레드 풀 그리고 스레드 풀이 더 좋은 이유
        - Java ExecutorService는 태스크를 제출하고 나중에 결과를 수집할 수 있는 인터페이스를 제공한다

        ```java
        ExecutorService newFixedThreadPool(int nThreads)
        ```

        - 위 팩토리 메서드는 워커 스레드라 불리는 nThreads를 포함하는 ExecutorService를 만들고 이들을 스레드 풀에 저장한다.
        - 스레드 풀에서 사용하지 않은 스레드로 제출된 태스크를 먼저 온 순서대로 실행한다.
        - 이들 태스크 실행이 종료되면 이들 스레드를 풀로 반환한다.
        - 이 방식의 장점은 하드웨어에 맞는 수의 태스크를 유지함과 동시에 수 천개의 태스크를 스레드 풀에 아무 오버헤드 없이 제출할 수 있다는 점이다.
        - 프로그래머는 태스크(Runnable이나 Callable)를 제공하면 스레드가 이를 실행한다.
    - 스레드 풀 그리고 스레드 풀이 나쁜 이유
        - 주의 사항
            - k 스레드를 가진 스레드 풀은 오직 k만큼의 스레드를 동시에 실행할 수 있다.
            - 잠을 자거나 I/O를 기다리거나 네트워크 연결을 기다리는 태스크가 있다면 주의해야 한다.
                - 블록 상황에서 이들 태스크가 워커 스레드에 할당된 상태를 유지하지만 아무 작업도 하지 않게 된다. (병렬 실행 성능이 저하됨)

                ![chapter15-01](image/chapter15-01.png '자는 태스크는 스레드 풀의 성능을 저하시킨다.')

            - 처음 제출한 테스크가 기존 실행 중인 태스크가 나중의 태스크 제출을 기다리는 상황(Future의 일반적인 패턴)이라면 데드락에 걸릴 수도 있다.
            - 블록(자거나 이벤트를 기다리는)할 수 있는 태스크는 스레드 풀에 제출하지 말아야 한다는 것이지만 항상 이를 지킬 수 있는 것은 아니다.
            - 프로그램을 종료하기 전에 모든 스레드 풀을 종료하는 습관을 갖는 것이 중요하다.
        - 스레드의 다른 추상화 : 중첩되지 않은 메서드 호출
            - 태스크나 스레드가 메서드 호출 안에서 시작되면 그 메서드 호출은 반환하지 않고 작업이 끝나기를 기다렸다.
            - 다시 말해 스레드 생성과 join()이 한 쌍처럼 중첩된 메서드 호출 내에 추가되었다.
            - 이를 엄격한(strict) 포크/조인이라 한다.

                ![chapter15-02](image/chapter15-02.png '엄격한 포크/조인. 화살표는 스레드, 원은 포크와 조인을, 사각형은 메서드 호출과 반환을 의미한다.')

            - 시작된 태스크를 내부 호출이 아니라 외부 호출에서 종료하도록 기다리는 좀 더 여유로운 방식의 포크/조인을 사용해도 비교적 안전하다.
                - 제공된 인터페이스를 사용자는 일반 호출로 간주할 수 있다.

                ![chapter15-03](image/chapter15-03.png '여유로운 포크/조인')

                - 특히 메서드 호출자에 기능을 제공하도록 메서드가 반환된 후에도 만들어진 태스크 실행이 계속되는 메서드를 비동기 메서드라고 한다.
                - 비동기 메서드의 위험성
                    - 데이터 경쟁 문제를 일으키지 않도록 주의해야 한다.
                    - 기존에 실행 중이던 스레드가 종료되지 않은 상황에서 java의 main() 메서드가 반환하면 어떻게 될까?
                        - 애플리케이션이 종료되지 못하고 모든 스레드가 실행을 끝날 때까지 기다린다. (비데몬 스레드)
                            - 잊고서 종료를 못한 스레드에 의해 애플리케이션이 크래시될 수 있다.
                        - 애플리케이션 종료를 방해하는 스레드를 강제종료(kill) 시키고 애플리케이션을 종료한다. (데몬 스레드)
                            - 디스크에 쓰기 I/O 작업을 시도하는 일련의 작업을 중단했을 때 이로 인해 외부 데이터의 일관성이 파괴될 수 있다.
                        - 이들 문제를 피하려면 애플리케이션에서 만든 모든 스레드를 추적하고 애플리케이션을 종료하기 전에 스레드 풀을 포함한 모든 스레드를 종료하는 것이 좋다.
                        - Java 스레드는 setDaemon() 메서드를 이용해 데몬 또는 비데몬으로 구분시킬 수 있다.
                            - 데몬 스레드는 애플리케이션이 종료될 때 강제 종료되므로 디스크의 데이터 일관성을 파괴하지 않는 동작을 수행할 때 유용할 수 있다.
                            - main() 메서드는 모든 비데몬 스레드가 종료될 때까지 프로그램을 종료하지 않고 기다린다.
                - 스레드에 바라는 점은 일반적으로 모든 하드웨어 스레드를 활용해 병렬성의 장점을 극대화하도록 프로그램 구조를 만드는 것 즉, 프로그램을 작은 태스크 단위로 구조화하는 것이 목표다.

## 동기 API와 비동기 API

- 두 가지 단계로 병렬성을 이용할 수 있다.
    - 외부 반복(명시적 for 루프)을 내부 반복(스트림 메서드 사용)으로 바꿔야 한다.
    - 그리고 스트림에 parallel() 메서드를 이용하므로 Java 런타임 라이브러리가 복잡한 스레드 작업을 하지 않고 병렬로 요소가 처리되도록 할 수 있다.
    - 루프가 실행될 때 추측에 의존해야 하는 프로그래머와 달리 런타임 시스템은 사용할 수 있는 스레드를 더 정확하게 알고 있다는 것도 내부 반복의 장점이다.

```java
int f(int x); // 메서드 시그니처
int g(int x); // 메서드 시그니처

...
int y = f(x);
int z = g(x);
System.out.println(y + z);
```

- 이들 메서드(f, g)는 물리적 결과를 반환하므로 **동기 API**라고 부른다.
- f와 g가 오랜 시간이 걸리는 작업이라고 가정하자.
    - f, g의 작업을 컴파일러가 완전하게 이해하기 어려우므로 보통 Java 컴파일러는 코드 최적화와 관련한 아무 작업도 수행하지 않을 수 있다.
    - f와 g가 서로 상호작용하지 않는다는 사실을 알고 있거나 상호작용을 전혀 신경쓰지 않는다면 f와 g를 별도의 CPU 코어로 실행함으로 f와 g 중 오래 걸리는 작업의 시간으로 합계를 구하는 시간을 단축할 수 있다.
    - 별도의 스레드로 f와 g를 실행해 이를 구현할 수 있다.
    - 의도는 좋지만 이전의 단순했던 코드가 다음처러머 복잡하게 변한다.
        - 복잡한 코드는 스레드에서 결과를 가져오는 부분과 관련이 있다.
        - 오직 바깥 최종 결과 변수만 람다나 내부 클래스에서 사용할 수 있다는 제한은 있지만 실제 문제는 다름아닌 명시적 스레드 조작 부분에 존재한다.

        ```java
        class ThreadExample {
        	public static void main(String[] args) throws InterruptedException {
        		int x = 1337;
        		Result result = new Result();
        		
        		Thread t1 = new Thread(() -> { result.left = f(x); });
        		Thread t2 = new Thread(() -> { result.right = g(x); });
        		t1.start();
        		t2.start();
        		t1.join();
        		t2.join();
        		System.out.println(result.left + result.right);
        	}

        	private static class Result {
        		private int left;
        		private int right;
        	}
        }
        ```

    - Runnable 대신 Future API 인터페이스를 이용해 코드를 더 단순화할 수 있다.

        ```java
        public class ExecutorServiceExample {
        	public static void main(String[] args) throws ExecutionException, InterruptedException {
        		int x = 1337;
        		
        		ExecutorService executorService = Executors.newFixedThreadPool(2);
        		Future<Integer> y = executorService.submit(() -> f(x));
        		Future<Integer> z = executorService.submit(() -> g(x));
        		System.out.println(y.get() + z.get());
        		
        		executorService.shutdown();
        	}
        }
        ```

        - 이 코드도 명시적인 submit 메서드 호출 같은 불필요한 코드로 오염되었다.
        - 명시적 반복으로 병렬화를 수행하던 코드를 스트림을 이용해 내부 반복으로 바꾼것처럼 비슷한 방법으로 이 문제를 해결해야 한다.
        - 문제의 해결은 **비동기 API**라는 기능으로 API를 바꿔서 해결할 수 있다.
            - 첫 번째 방법인 Java의  Future를 이용하면 이 문제를 조금 개선할 수 있다.
                - Java5에서 소개된 Future는 Java8의 CompletableFuture로 이들을 조합할 수 있게 되면서 더욱 기능이 풍부해졌다.
            - 두 번째 방법은 발행-구독 프로토콜에 기반한 Java9의 java.util.concurrent.Flow 인터페이스를 이용하는 방법으로 추후 설명한다.
- Future 형식 API

    ```java
    Future<Integer> f(int x); // 변경된 시그니처
    Future<Integer> g(int x); // 변경된 시그니처

    ...
    Future<Integer> y = f(x);
    Future<Integer> z = g(x);
    System.out.println(y.get() + z.get());
    ```

    - 예제에서는 API는 그대로 유지하고 g를 그대로 호출하면서 f에만 Future를 적용할 수 있었다.
    - 하지만 조금 더 큰 프로그램에서는 두 가지 이유로 이런 방식을 사용하지 않는다.
        - 다른 상황에서는 g에도 Future 형식이 필요할 수 있으므로 API 형식을 통일하는 것이 바람직하다.
        - 병렬 하드웨어로 프로그램 실행 속도를 극대화하려면 여러 작은 하지만 합리적인 크기의 태스크로 나누는 것이 좋다.
- 리액티브 형식 API
    - 두 번째 대안은 f, g의 시그니처를 바꿔서 콜백 형식의 프로그래밍을 이용하는 것이다.

    ```java
    void f(int x, IntConsumer dealWithResult);
    ```

    - 처음에는 두 번째 대안이 이상해 보일 수 있다.
        - f가 값을 반환하지 않는데 어떻게 프로그램이 동작할까?
        - f에 추가 인수로 콜백(람다)을 전달해서 f의 바디에서는 return 문으로 결과를 반환하는 것이 아니라 결과가 준비되면 이를 람다로 호출하는 태스크를 만드는 것이 비결이다.

        ```java
        public class CallbackStyleExample {
        	public static void main(String[] args) {
        		int x = 1337;
        		Result result = new Result();
        		
        		f(x, (int y) -> {
        			result.left = y;
        			System.out.println((result.left + result.right));
        		});

        		g(x, (int z) -> {
        			result.right = z;
        			System.out.println((result.left + result.right));
        		});
        	}
        }
        ```

        - 결과가 달라졌다.
            - f와 g의 호출 합계를 정확하게 출력하지 않고 상황에 따라 먼저 계산된 결과를 출력한다.
            - 락을 사용하지 않으므로 값을 두 번 출력할 수 있을 뿐더러 때로는 +에 제공된 두 피연산자가 println이 호출되기 전에 업데이트될 수도 있다.
            - 다음처럼 두 가지 방법으로 이 문제를 보완할 수 있다.
                - if-then-else를 이용해 적절한 락을 이용해 두 콜백이 모두 호출되었는지 확인한 다음 println을 호출해 원하는 기능을 수행할 수 있다.
                - 리액티브 형식의 API는 보통 한 결과가 아니라 일련의 이벤트에 반응하도록 설계되었으므로 Future를 이용하는 것이 더 적절하다.
            - 메서드 f, g는 dealWithResult 콜백을 여러 번 호출할 수 있다.
            - 원래의 f, g 함수는 오직 한 번만 return을 사용하도록 되어있다.
            - Future도 한 번만 완료되며 그 결과를 get()으로 얻을 수 있다.
            - 리액티브 형식의 비동기 API는 자연스럽게 일련의 값(나중에 스트림으로 연결)을,  Future 형식의 API는 일회성의 값을 처리하는데 적합하다.
- 잠자기(그리고 기타 블로킹 동작)는 해로운 것으로 간주
    - 스레드는 잠들어도 여전히 시스템 자원을 점유한다.
    - 스레드 풀에서 잠을 자는 태스크는 다른 태스크가 시작되지 못하게 막으므로 자원을 소비한다는 사실을 기억하자.
    - 모든 블록 동작도 마찬가지다.

    ```java
    // 코드 A (스레드 풀에서 동작한다고 가정)
    work1();
    Thread.sleep(1000);
    work2();

    // 코드 B (스레드 풀에서 동작한다고 가정)
    public class ScheduledExecutorServiceExample {
    	public static void main(String[] args) {
    		ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    		
    		work1();
    		scheduledExecutorService.schedule(ScheduledExecutorServiceExample::work2, 10, TimeUnit.SECONDS);
    		scheduledExecutorService.shutdown();
    	}

    	public static void work1() {
    		System.out.println("Hello from Work1!");
    	}
    	
    	public static void work2() {
    		System.out.println("Hello from Work2!");
    	}
    }
    ```

    - 코드 A의 경우 스레드 풀에 work1()이 실행되고 10초를 잔다.
        - 깨어나서 work2()를 실행한 다음 작업을 종료하고 워커 스레드를 해제한다.
    - 코드 B는 work1()을 실행하고 종료한다.
        - work2()가 10초 뒤에 실행될 수 있도록 큐에 추가한다.
    - 두 코드의 다른 점은 A가 자는 동안 귀중한 스레드 자원을 점유하는 반면 B는 다른 작업이 실행될 수 있도록 허용한다는 점이다.
        - 스레드를 사용할 필요없이 메모리만 조금 더 사용했다.
    - 태스크를 만들 때는 이런 특징을 잘 활용해야 한다.
        - 태스크가 실행되면 귀중한 자원을 점유하므로 태스크가 끝나서 자원을 해제하기 전까지 태스크를 계속 실행해야 한다.
        - 태스크를 블록하는 것보다는 다음 작업을 태스크로 제출하고 현재 태스크는 종료하는 것이 바람직하다.
    - 이런 디자인 패턴을 따르려면 읽기 어려운 코드가 많아지는 것처럼 보일 수 있다.
        - Java CompletableFuture 인터페이스는 이전에 살펴본 Future에 get()을 이용해 명시적으로 블록하지 않고 콤비네이터를 사용함으로 이런 형식의 코드를 런타임 라이브러리 내에 추상화한다.
    - 스레드는 제한된 자원이므로 가능하면 코드 B 형식을 따르는 것이 좋다.
- 현실성 확인
    - 모든 동작을 비동기 호출로 구현한다면 병렬 하드웨어를 최대한 활용할 수 있다.
    - 하지만 현실적으로 '모든 것은 비동기'라는 설계 원칙을 어겨야 한다.
        - 최상은 좋은 것의 적이다.
- 비동기 API에서 예외는 어떻게 처리하는가?
    - Future를 구현한 CompletableFuture에서는 런타임 get() 메서드에 예외를 처리할 수 있는 기능을 제공하며 예외에서 회복할 수 있도록 exceptionally() 같은 메서드도 제공한다.
    - 리액티브 형식의 비동기 API에서는 return 대신 기존 콜백이 호출되므로 예외가 발생했을 때 실행될 추가 콜백을 만들어 인터페이스를 바꿔야 한다.

    ```java
    void f(int x, Consumer<Integer> dealWithResult, Consumer<Throwable> dealWithException);
    ```

    - 콜백이 여러 개면 이를 따로 제공하는 것보다는 한 객체로 이 메서드를 감싸는 것이 좋다.
    - 예를 들어 Java9 Flow API에서는 여러 콜백을 한 객체(네 개의 콜백을 각각 대표하는 네 메서드를 포함하는 Subscriber<T> 클래스)로 감싼다.

    ```java
    void onSubscribe(Subscription subscription)
    void onComplete() // 값을 다 소진했거나 에러가 발생해서 더 이상 처리할 데이터가 없을 때
    void onError(Throwable throwable) // 도중에 에러가 발생했을 때
    void onNext(T item) // 값이 있을 때

    ...
    void f(int x, Subscriber<Integer> s);
    ```

    - 보통 이런 종류의 호출을 메시지 또는 **이벤트**라 부른다.
    - 이런 이벤트 API의 일부를 보자면 API는 이벤트의 순서(채널 프로토콜이라 불리는)에는 전혀 개의치 않는다.
    - 실제 부속 문서에서는 "onComplete 이벤트 다음에는 아무 이벤트도 일어나지 않음" 같은 구문을 사용해 프로토콜을 정의한다.

## 박스와 채널 모델

- 동시성 모델을 가장 잘 설계하고 개념화하려면 그림이 필요하다.
- 우리는 이 기법을 박스와 채널 모델(box-and-channel model)이라고 부른다.

![chapter15-04](image/chapter15-04.png '간단한 박스와 채널 다이어그램')

- 이전 예제인 f(x) + g(x)의 계산을 일반화해서 정수와 관련된 간단한 상황이 있다고 가정하자.
    - f나 g를 호출하거나 p 함수에 인수 x를 이용해 호출하고 그 결과를 q1과 q2에 전달하며 다시 이 두 호출의 결과를 함수 r을 호출한 다음 결과를 출력한다.

    ```java
    // 첫 번째 구현 방법
    int t = p(x);
    System.out.println(r(q1(t), q2(t)));
    // 겉보기엔 깔끔해 보이는 코드지만 Java가 q1, q2를 차례로 호출하는데
    // 이는 하드웨어 병렬성의 활용과 거리가 멀다.

    // 두 번째 구현 방법
    Future<Integer> a1 = executorService.submit(() -> q1(t));
    Future<Integer> a2 = executorService.submit(() -> q2(t));
    System.out.println(r(a1.get(), a2.get()));
    // 
    ```

    - Java8에서는 CompletableFuture와 콤비네이터(combinators)를 이용해 문제를 해결한다.

## CompletableFuture와 콤비네이터를 이용한 동시성

- Java8에서는 Future 인터페이스의 구현인 CompletableFuture를 이용해 Future를 조합할 수 있는 기능을 추가했다.
- ComposableFuture가 아니고 CompletableFuture라고 부르는 이유는 뭘까?
    - 일반적으로 Future는 실행해서 get()으로 결과를 얻을 수 있는 Callable로 만들어진다.
    - 하지만 CompletableFuture는 실행할 코드 없이 Future를 만들 수 있도록 허용하며 complete() 메서드를 이용해 나중에 어떤 값을 이용해 다른 스레드가 이를 완료할 수 있고 get()으로 값을 얻을 수 있도록 허용한다.
    - 그래서 CompletableFuture라고 부른다.
- f(x)와 g(x)를 동시에 실행해 합계를 구하는 코드를 다음처럼 구현할 수 있다.

    ```java
    public class CFComplete {
    	public static void main(String[] args) throws ExecutionException, InterruptedException {
    		ExecutorService executorService = Executors.newFixedThreadPool(10);
    		int x = 1337;

    		CompletableFuture<Integer> a = new CompletableFuture<> ();
    		executorService.submit(() -> a.complete(f(x)));
    		int b = g(x);
    		System.out.println(a.get() + b);

    		executorService.shutdown();
    	}
    }

    // 또는
    public class CFComplete {
    	public static void main(String[] args) throws ExecutionException, InterruptedException {
    		ExecutorService executorService = Executors.newFixedThreadPool(10);
    		int x = 1337;

    		CompletableFuture<Integer> b = new CompletableFuture<> ();
    		executorService.submit(() -> b.complete(g(x)));
    		int a = f(x);
    		System.out.println(a + b.get());

    		executorService.shutdown();
    	}
    }
    ```

    - 위 두 코드는 f(x)의 실행이 끝나지 않거나 아니면 g(x)의 실행이 끝나지 않는 상황에서 get()을 기다려야 하므로 프로세싱 자원을 낭비할 수 있다.
- CompletableFuture<T>에 thenCombine 메서드를 사용함으로 두 연산 결과를 더 효과적으로 더할 수 있다.

    ```java
    CompletableFuture<V> thenCombine(CompletableFuture<U> other, BiFunction<T, U, V> fn)
    ```

    - 이 메서드는 두 개의 CompletableFuture 값(T, U 결과 형식)을 받아 한 개의 새 값을 만든다.
    - 처음 두 작업이 끝나면 두 결과 모두에 fn을 적용하고 블록하지 않은 상태로 결과 Future를 반환한다.

    ```java
    public class CFCombine {
    	public static void main(String[] args) throws ExecutionException, InterruptedExecption {
    		ExecutorService executorService = Executors.newFixedThreadPool(10);
    		int x = 1337;

    		CompletableFuture<Integer> a = new CompletableFuture<> ();
    		CompletableFuture<Integer> b = new CompletableFuture<> ();
    		CompletableFuture<Integer> c = a.thenCombine(b, (y, z) -> y + z);
    		executorService.submit(() -> a.complete(f(x)));
    		executorService.submit(() -> b.complete(g(x)));

    		System.out.println(c.get());
    		executorService.shutdown();
    	}
    }
    ```

    - Future a와 Future b의 결과를 알지 못한 상태에서 thenCombine은 두 연산이 끝났을 때 스레드 풀에서 실행된 연산을 만든다.
    - 결과를 추가하는 세 번째 연산 c는 다른 두 작업이 끝날 때까지는 스레드에서 실행되지 않는다.
    - 따라서 기존의 두 가지 버전의 코드에서 발생했던 블록 문제가 어디서도 일어나지 않는다.
    - Future의 연산이 두 번째로 종료되는 상황에서 실제 필요한 스레드는 한 개지만 스레드 풀의 두 스레드가 여전히 활성 상태다.
    - 이전의 두 버전에서 y+z 연산은 f(x) 또는 g(x)를 실행(블록될 가능성이 있는)한 같은 스레드에서 수행했다.
    - 반면 thenCombine을 이용하면 f(x)와 g(x)가 끝난 다음에야 덧셈 계산이 실행된다.

    ![chapter15-05](image/chapter15-05.png 'f(x), g(x), 결과 합산 세 가지 연산의 타이밍 다이어그램')

- 어떤 상황에서는 많은 수의 Future를 사용해야 한다. (서비스에서 여러 질의를 처리하는 상황 등)
- 이런 상황에서는 CompletableFuture와 콤비네이터를 이용해 get()에서 블록하지 않을 수 있고 그렇게 함으로 병렬 실행의 효율성은 높이고 데드락은 피하는 최상의 해결책을 구현할 수 있다.

## 발행-구독 그리고 리액티브 프로그래밍

- Future와 CompletableFuture는 독립적 실행과 병렬성이라는 정식적 모델에 기반한다.
- 연산이 끝나면 get()으로 Future의 결과를 얻을 수 있다.
- Future는 한 번만 실행해 결과를 제공한다.
- 반면 리액티브 프로그래밍은 시간이 흐르면서 여러 Future 같은 객체를 통해 여러 결과를 제공한다.
- 스트림은 선형적인 파이프라인 처리 기법에 알맞다.
- Java9에서는 java.util.concurrent.Flow의 인터페이스에 발행-구독 모델(또는 줄여서 pub-sub이라 불리는 프로토콜)을 적용해 리액티브 프로그래밍을 제공한다.
    - 구독자가 구독할 수 있는 **발행자**
    - 이 연결을 **구독**(subscription)이라 한다.
    - 이 연결을 이용해 **메시지**(또는 **이벤트**로 알려짐)를 전송한다.

![chapter15-06](image/chapter15-06.png '발행자-구독자 모델')

- 두 플로를 합치는 예제
    - 값을 포함하는 셀을 구현한다.

        ```java
        private class SimpleCell {
        	private int value = 0;
        	private String name;
        	public SimpleCell(String name) {
        		this.name = name;
        	}
        }

        // 초기화
        SimpleCell c2 = new SimpleCell("C2");
        SimpleCell c1 = new SimpleCell("C1");
        ```

    - c1이나 c2의 값이 바뀌었을 때 c3가 두 값을 더하도록 어떻게 지정할 수 있을까?
    - c1과 c2에 이벤트가 발생했을 때 c3를 구독하도록 만들어야 한다.

        ```java
        // 이 인터페이스는 통신할 구독자를 인수로 받는다.
        interface Publisher<T> {
        	void subscribe(Subscriber<? super T> subscriber);
        }

        // onNext라는 정보를 전달할 단순 메서드를 포함하며 
        // 구현자가 필요한대로 이 메서드를 구현할 수 있다.
        interface Subscriber<T> {
        	void onNext(T t);
        }
        ```

    - 사실 Cell은 Publisher (셀의 이벤트에 구독할 수 있음)이며 동시에 Subscriber (다른 셀의 이벤트에 반응함)임을 알 수 있다.
- 데이터가 발행자(생산자)에서 구독자(소비자)로 흐름에 착안해 개발자는 이를 업스트림(upstream) 또는 다운스트림(downstream)이라 부른다.
    - 위 예제에서 데이터 newValue는 업스트림 onNext() 메서드로 전달되고 notifyAllSubscribers() 호출을 통해 다운스트림 onNext() 호출로 전달된다.
- 간단하지만 플로 인터페이스의 개념을 복잡하게 만든 두 가지 기능은 압력과 역압력이다.
- 빠른 속도로 발생하는 이벤트를 처리하는 상황을 압력(pressure)이라고 부른다.
- Java9 Flow API에서는 발행자가 무한의 속도로 아이템을 방출하는 대신 요청했을 때만 다음 아이템을 보내도록 하는 request() 메서드(Subscription이라는 새 인터페이스에 포함)를 제공한다. (밀어내기(push) 모델이 아니라 당김(pull) 모델)
- 역압력
    - 정보의 흐름 속도를 역압력(흐름 제어)으로 제어 즉 Subscriber에서 Publisher로 정보를 요청해야 할 필요가 있을 수 있다.

    ```java
    // Java9 Flow API의 Subscriber 인터페이스는 네 번째 메서드를 포함한다.
    // Publisher와 Subscriber 사이에 채널이 연결되면 첫 이벤트로 이 메서드가 호출된다.
    void onSubscribe(Subscription subscription);

    // Subscription 객체는 다음처럼 Subscriber와 Publisher와 통신할 수 있는 메서드를 포함
    interface Subscription {
    	void cancel();
    	void request(long n);
    }
    ```

    - Publisher는 Subscription 객체를 만들어 Subscriber로 전달하면 Subscriber는 이를 이용해 Publisher로 정보를 보낼 수 있다.
    - 실제 역압력의 고려사항
        - 여러 Subscriber가 있을 때 이벤트를 가장 느린 속도로 보낼 것인가? 아니면 각 Subscriber에게 보내지 않은 데이터를 저장할 별도의 큐를 가질 것인가?
        - 큐가 너무 커지면 어떻게 해야 할까?
        - Subscriber가 준비가 안 되었다면 큐의 데이터를 폐기할 것인가?
    - 이 고려사항은 데이터의 성격에 따라 달라진다.
        - 중요하지 않은 데이터를 유실하는 것은 그리 대수로운 일은 아니지만 은행 계좌에서 크레딧이 사라지는 것은 큰일이다.
    - 당김 기반 리액티브 역압력 기법에서는 Subscriber가 Publisher로부터 요청을 당긴다(pull)는 의미에서 리액티브 당김 기반(reactive pull-based)이라 불린다.
    - 결과적으로 이런 방식으로 역압력을 구현할 수도 있다.

## 리액티브 시스템 vs 리액티브 프로그래밍

- 리액티브 시스템(reactive system)은 런타임 환경이 변화에 대응하도록 전체 아키텍처가 설계된 프로그램을 가리킨다.
- 반응성(responsive), 회복성(resilient), 탄력성(elastic)으로 세 가지 속성을 요약할 수 있다.
- 반응성은 리액티브 시스템이 큰 작업을 처리하느라 간단한 질의의 응답을 지연하지 않고 실시간으로 입력에 반응하는 것을 의미한다.
- 회복성은 한 컴포넌트의 실패로 전체 시스템이 실패하지 않음을 의미한다.
- 탄력성은 시스템이 자신의 작업 부하에 맞게 적응하며 작업을 효율적으로 처리함을 의미한다.
- java.util.concurrent.Flow 관련된 Java 인터페이스에서 제공하는 리액티브 프로그래밍 형식을 이용하는 것도 주요 방법 중 하나다.
- 이들 인터페이스 설계는 Reactive Manifesto의 네 번째이자 마지막 속성 즉 메시지 주도(message-driven) 속성을 반영한다.
- 메시지 주도 시스템은 박스와 채널 모델에 기반한 내부 API를 갖고 있는데 여기서 컴포넌트는 처리할 입력을 기다리고 결과를 다른 컴포넌트로 보내면서 시스템이 반응한다.