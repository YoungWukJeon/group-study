## Future의 단순 활용

- Java5부터는 미래의 어느 시점에 결과를 얻는 모델에 활용할 수 있도록 Future 인터페이스를 제공하고 있다.
- 시간이 걸릴 수 있는 작업을 Future 내부로 설정하면 호출자 스레드가 결과를 기다리는 동안 다른 유용한 작업을 수행할 수 있다.
- Future는 저수준의 스레드에 비해 직관적으로 이해하기 쉽다는 장점이 있다.
- Future를 이용하려면 시간이 오래 걸리는 작업을 Callable 객체 내부로 감싼 다음에 ExecutorService에 제출해야 한다.

```java
// Java8 이전 코드 예제
ExecutorService executor = Executors.newCachedThreadPool();
Future<Double> future = executor.submit(new Callable<Double> () {
	public Double call() {
		return doSomeLongComputation(); // 시간이 오래 걸리는 작업
	}
});
doSomethingElse();
try {
	Double result = future.get(1, TimeUnit.SECONDS);
} catch (ExecutionException ee) {
	// 계산 중 예외 발생
} catch (InterruptedException ie) {
	// 현재 스레드에서 대기 중 인터럽트 발생
} catch (TimeoutException te) {
		// Future가 완료되기 전에 타임아웃 발생
}
```

- 다른 작업을 처리하다가 시간이 오래 걸리는 작업의 결과가 필요한 시점이 되었을 때 Future의 get 메서드로 결과를 가져올 수 있다.
- get 메서드를 호출했을 때 이미 계산이 완료되어 결과가 준비되어있다면 즉시 결과를 반환하지만 결과가 준비되지 않았다면 작업이 완료될 때까지 우리 스레드를 블록시킨다.

![chapter16-01](image/chapter16-01.png 'Future로 시간이 오래 걸리는 작업을 비동기적으로 실행하기')

- get 메서드를 오버로드해서 우리 스레드가 대기할 최대 타임아웃 시간을 설정하는 것이 좋다.
- Future의 제한
    - 여러 Future의 결과가 있을 때 이들의 의존성을 표현하기가 어렵다.
        - ex) 오래 걸리는 A라는 계산이 끝나면 그 결과를 다른 오래 걸리는 계산 B로 전달하고 B의 결과가 나오면 다른 질으의 결과와 B의 결과를 조합하시오.
    - Future에서 필요한 선언형 기능
        - 두 개의 비동기 계산 결과를 하나로 합친다. 두 가지 계산 결과는 서로 독립적일 수 있으며 또는 두 번째 결과가 첫 번째 결과에 의존하는 상황일 수 있다.
        - Future 집합이 실행하는 모든 태스크의 완료를 기다린다.
        - Future 집합에서 가장 빨리 완료되는 태스크를 기다렸다가 결과를 얻는다.
        - 프로그램적으로 Future를 완료시킨다. (즉, 비동기 동작에 수동으로 결과 제공)
        - Future 완료 동작에 반응한다. (즉, 결과를 기다리면서 블록되지 않고 결과가 준비되었다는 알림을 받은 다음에 Future의 결과로 원하는 추가 동작을 수행할 수 있음)
    - 지금까지 설명한 기능을 선언형으로 이용할 수 있도록 Java8에서 새로 제공하는 CompletableFuture 클래스(Future 인터페이스를 구현한 클래스)를 살펴본다.
    - Stream과 CompletableFuture는 비슷한 패턴, 즉 람다 표현식과 파이프라이닝을 활용한다.
- CompletableFuture로 비동기 애플리케이션 만들기
    - 예산을 줄일 수 있도록 여러 온라인 상점 중 가장 저렴한 가격을 제시하는 상점을 찾는 애플리케이션을 완성해 가는 예제
    - 배울수 있는 기술
        1. 고객에게 비동기 API를 제공하는 방법을 배운다.  (온라인 상점을 운영하고 있는 독자에게 특히 유용한 기술)
        2. 동기 API를 사용해야 할 때 코드를 비블록으로 만드는 방법을 배운다. 두 개의 비동기 동작을 파이프라인으로 만드는 방법과 두 개의 동작 결과를 하나의 비동기 계산으로 합치는 방법을 살펴본다.
        3. 비동기 동작의 완료에 대응하는 방법을 배운다. 즉, 모든 상점에서 가격 정보를 얻을 때까지 기다리는 것이 아니라 각 상점에서 가격 정보를 얻을 때마다 즉시 최저가격을 찾는 애플리케이션을 갱신하는 방법을 설명한다.

## 비동기 API 구현

- 제품명에 해당하는 가격을 반환하는 메서드 정의 코드

    ```java
    public class Shop {
    	public double getPrice(String product) {
    		// 구현해야 함
    	}
    }
    ```

- getPrice 메서드는 상점의 데이터베이스를 이용해서 가격 정보를 얻는 동시에 다른 외부 서비스에도 접근할 것이다. (예를 들어 물건 발행자나 제조사 관련 프로모션 할인 등)
- 이 예제에서는  실제 호출할 서비스까지 구현할 수 없으므로 이처럼 오래 걸리는 작업을 다음과 같이 delay라는 메서드로 대체할 것이다.

    ```java
    // 인위적으로 1초를 지연시키는 코드
    public static void delay() {
    	try {
    		Thread.sleep(1000L);
    	} catch (InterruptedException e) {
    		throw new RuntimeException(e);
    	}
    }
    ```

- 아래 코드에서 볼 수 있는 것처럼 제품명에 chatAt을 적용해서 임의의 계산값을 반환한다.

    ```java
    public double getPrice(String product) {
    	return calculatePrice(product);
    }

    public double calculatePrice(String product) {
    	delay(); // 오래걸리는 작업임을 임의로 나타냄
    	return random.nextDouble() * product.chatAt(0) + product.charAt(1);
    }
    ```

- 사용자가 이 API(최저가격 검색 애플리케이션)을 호출하면 비동기 동작이 완료될 때까지 1초 동안 블록된다.
- 동기 메서드를 비동기 메서드로 변환
    - 동기 메서드 getPrice를 비동기 메서드로 변환하려면 다음 코드처럼 먼저 이름(getPriceAsync)과 변환값을 바꿔야 한다.

        ```java
        public Future<Double> getPriceAsync(String product) {
        	CompletableFuture<Double> futurePrice = new CompletableFuture<> ();
        	new Thread(() -> {
        		double price = calculatePrice(product); // 다른 스레드에서 비동기적으로 계산을 수행
        		futurePrice.complete(price); // 오랜 시간이 걸리는 계산이 완룓뢰면 Future에 값을 설정
        	}).start();
        	return futurePrice; // 계산 결과가 완료되길 기다리지 않고 Future를 반환
        }
        ```

    - 다음 코드에서 보여주는 것처럼 클라이언트는 getPriceAsync를 활용할 수 있다.

        ```java
        Shop shop = new Shop("BestShop");
        long start = System.nanoTime();
        Future<Double> futurePrice = shop.getPriceAsync("my favorite product"); // 상점에 제품가격 정보 요청
        long invocationTime = ((System.nanoTime() - start) / 1_000_000);
        System.out.println("Invocation returned after " + invocationTime + " msecs");

        // 제품의 가격을 계산하는 동안
        doSomethingElse(); // 다른 상점 검색 등 다른 작업 수행
        try {
        	double price = futurePrice.get(); // 가격 정보가 있으면 Future에서 읽고, 없으면 받을 때까지 블록한다.
        	System.out.printf("Price is %.2f%n", price);
        } catch (Exception e) {
        }
        long retrievalTime = ((System.nanoTime() - start) / 1_000_000);
        System.out.println("Price returned after " + retrievalTime + " msecs");
        ```

    - 결과는?

        ```
        Invocation returned after 43 msecs
        Price is 123.26
        Price returned after 1045 msecs
        ```

- 에러 처리 방법
    - 예외가 발생하면 해당 스레드에만 영향을 미친다.
    - 즉, 에러가 발생해도 가격 계산은 계속 진행되며 일의 순서가 꼬인다.
    - 결과적으로 클라이언트는 get 메서드가 반환될 때까지 영원히 기다리게 될 수도 있다.
    - 클라이언트는 타임아웃값을 받는 get 메서드의 오버로드 버전을 만들어 이 문제를 해결할 수 있다.
    - 하지만 이때 제품가격 계산에 왜 에러가 발생했는지 알 수 있는 방법이 없다.
    - 따라서 completeExceptionally 메서드를 이용해서 CompletableFuture 내부에서 발생한 예외를 클라이언트로 전달해야 한다.

        ```java
        // CompletableFuture 내부에서 발생한 에러 전파
        public Future<Double> getPriceAsync(String product) {
        	CompletableFuture<Double> futurePrice = new CompletableFuture<> ();
        	new Thread(() -> {
        		try {
        			double price = calculatePrice(product);
        			futurePrice.complete(price);
        		} catch (Exception ex) {
        			futurePrice.completeExceptionally(ex); // 도중에 문제가 발생하면 발생한 에러를 포함시켜 Future를 종료한다.
        		}
        	}).start();
        	return futurePrice;
        }
        ```

    - 팩토리 메서드 supplyAsync로 CompletableFuture 만들기

        ```java
        public Future<Double> getPriceAsync(String product) {
        	return CompletableFuture.supplyAsync(() -> calculatePrice(product));
        }
        ```

        - supplyAsync 메서드는 Supplier를 인수로 받아서 CompletableFuture를 반환한다.
        - CompletableFuture는 Supplier를 실행해서 비동기적으로 결과를 생성한다.
        - ForkJoinPool의 Executor 중 하나가 Supplier를 실행할 것이다.
        - 두 번째 인수를 받는 오버로드 버전의 supplyAsync 메서드를 이용해서 다른 Executor를 지정할 수 있다.
        - 결국 모든 다른 CompletableFuture의 팩토리 메서드에 Executor를 선택적으로 전달할 수 있다.

## 비블록 코드 만들기

- 다음과 같은 상점 리스트가 있다고 가정하자.

    ```java
    List<Shop> shops = Arrays.asList(
    	new Shop("BestPrice"),
    	new Shop("LetsSaveBig"),
    	new Shop("MyFavoriteShop"),
    	new Shop("BuyItAll")
    );
    ```

- 다음처럼 제품명을 입력하면 상점 이름과 제품가격 문자열 정보를 포함하는 List를 반환하는 메서드를 구현해야 한다.

    ```java
    public List<String> findPrices(String product) {
    	return shops.stream()
    		.map(shop -> String.format("%s price is %.2f", shop.getName(), shop.getPrice(product)))
    		.collect(toList());
    }

    // findPrices의 결과와 성능 확인
    long start = System.nanoTime();
    System.out.println(findPrices("myPhone27S"));
    long duration = (System.nanoTime() - start) / 1_000_000;
    System.out.println("Done in " + duration + " msecs");
    ```

- 실행 결과는?

    ```
    [BestPrice price is 123.26, LetsSaveBig price is 169.47, MyFavoriteShop price is 214.13, BuyItAll price is 184.74]
    Done in 4032 msecs
    ```

- 병렬 스트림으로 요청 병렬화하기
    - 병렬 스트림을 이용해서 순차 계산을 병렬로 처리해서 성능을 개선할 수 있다.

        ```java
        public List<String> findPrices(String product) {
        	return shops.parallelStream()
        		.map(shop -> String.format("%s price is %.2f", shop.getName(), shop.getPrice(product)))
        		.collect(toList());
        }
        ```

    - 성능은?

        ```
        [BestPrice price is 123.26, LetsSaveBig price is 169.47, MyFavoriteShop price is 214.13, BuyItAll price is 184.74]
        Done in 1180 msecs
        ```

- CompletableFuture로 비동기 호출 구현하기
    - 팩터리 메서드 이용하기

        ```java
        List<CompletableFuture<String>> priceFutures = shops.stream()
        		.map(shop -> CompletableFuture.supplyAsync(() -> String.format("%s price is %.2f", shop.getName(), shop.getPrice(product))))
        		.collect(toList());
        ```

    - CompletableFuture의 join 메서드는 Future 인터페이스의 get 메서드와 같은 의미를 갖는다.
    - 다만 join은 아무 예외도 발생시키지 않는다는 점이 다르다.
    - join을 추가해 재구현한 코드

        ```java
        // 순차 파이프라인(스트림의 게으름 특성으로 인해 병렬성이 저하됨)
        public List<String> findPrices(String product) {
        	List<CompletableFuture<String>> priceFutures = shops.stream()
        		.map(shop -> CompletableFuture.supplyAsync(() -> shop.getName() + " price is " + shop.getPrice(product)))
        		.map(CompletableFuture::join)
        		.collect(Collectors.toList());
        }

        // 병렬 파이프라인
        public List<String> findPrices(String product) {
        	List<CompletableFuture<String>> priceFutures = shops.stream()
        		.map(shop -> CompletableFuture.supplyAsync(() -> shop.getName() + " price is " + shop.getPrice(product)))
        		.collect(Collectors.toList());
        	
        	return priceFuture.stream()
        		.map(CompletableFuture::join) // 모든 비동기 동작이 끝나길 기다린다.
        		.collect(totList());
        }
        ```

        - 두 map 연산을 하나의 스트림 처리 파이프라인으로 처리하지 않고 두 개의 스트림 파이프라인으로 처리했다는 사실에 주목하자.
        - 스트림 연산은 게으른 특성이 있으므로 하나의 파이프라인으로 연산을 처리했다면 모든 가격 정보 요청 동작이 동기적, 순차적으로 이루어지는 결과가 된다.

        ![chapter16-02](image/chapter16-02.png '스트림의 게으름 때문에 순차 계산이 일어나는 이유와 순차 계산을 회피하는 방법')

        - CompletableFuture로 각 상점의 정보를 요청할 때 기존 요청 작업이 완료되어야 join이 결과를 반환하면서 다른 상점으로 정보를 요청할 수 있기 때문이다.
        - 윗부분은 순차적으로 평가를 진행하는 단일 파이프라인 스트림 처리 과정을 보여준다. 즉, 이전 요청의 처리가 완전히 끝난 다음에 새로 만든 CompletableFuture가 처리된다.
        - 반면 아래쪽은 우선 CompleteFuture를 리스트로 모은 다음에 다른 작업과는 독립적으로 각자의 작업을 수행하는 모습을 보여준다.
        - 성능은?

            ```
            [BestPrice price is 123.26, LetsSaveBig price is 169.47, MyFavoriteShop price is 214.13, BuyItAll price is 184.74]
            Done in 2005 msecs
            ```

            - 약간의 코드만 수정하면 되는 병렬 스트림에 비해 만족스러운 결과가 아니다.
                - 병렬 스트림에 비해 2배의 시간이 걸림
            - 코드를 실행하는 기기가 네 개의 스레드를 병렬로 실행할 수 있는 기기라는 점에 착안해서 이 문제를 좀 더 고민해보자.
- 더 확장성이 좋은 해결 방법
    - 병렬 스트림 버전의 코드는 정확히 네 개의 상점에 하나의 스레드를 할당해서 네 개의 작업을 병렬로 수행하면서 검색 시간을 최소화할 수 있었다.
    - 만약 다섯번째 상점이 추가되면 어떻게 될까?
        - 순차 스트림의 경우 5초 이상이 소요된다.

            ```
            [BestPrice price is 123.26, LetsSaveBig price is 169.47, MyFavoriteShop price is 214.13, BuyItAll price is 184.74, ShopEasy price is 176.08]
            Done in 5025 msecs
            ```

        - 병렬 스트림 버전에서는 네 개의 상점을 검색하느라 네 개의 모든 스레드(일반적으로 스레드 풀에서 제공하는 스레드 수는 4개)가 사용된 상황이므로 다섯번째 상점을 처리하는데 추가로 1초 이상 소요된다. 즉, 네 개의 스레드 중 누군가가 작업을 완료해야 다섯번째 질의를 수행할 수 있다.

            ```
            [BestPrice price is 123.26, LetsSaveBig price is 169.47, MyFavoriteShop price is 214.13, BuyItAll price is 184.74, ShopEasy price is 176.08]
            Done in 2177 msecs
            ```

        - CompletableFuture 버전이 병렬 스트림 버전보다 아주 조금 빠르다.

            ```
            [BestPrice price is 123.26, LetsSaveBig price is 169.47, MyFavoriteShop price is 214.13, BuyItAll price is 184.74, ShopEasy price is 176.08]
            Done in 2006 msecs
            ```

    - 아홉 개의 상점이 있다고 가정해보자.
        - 병렬 스트림의 경우 3143ms, CompletableFuture의 경우 3009ms가 소요된다.
    - 두 가지 버전 모두 내부적으로 Runtime.getRuntime().availableProcessors()가 반환하는 스레드 수를 사용하면서 비슷한 결과가 된다.
    - 결과적으로 비슷하지만 CompletableFuture는 병렬 스트림 버전에 비해 작업에 이용할 수 있는 다양한 Executor를 지정할 수 있다는 장점이 있다.
    - 따라서 Executor로 스레드 풀의 크기를 조절하는 등 애플리케이션에 맞는 최적화된 설정을 만들 수 있다.
- 커스텀 Executor 사용하기
    - 스레드 풀 크기 조절
        - 자바 병렬 프로그래밍(Java Concurrency in Practice, 브라이언 게츠 공저)에서는 스레드 풀의 최적값을 찾는 방법을 제안한다.
        - 스레드 풀이 너무 크면 CPU와 메모리 자원을 서로 경쟁하느라 시간을 낭비할 수 있다.
        - 반면 스레드 풀이 너무 작으면 CPU의 일부 코어는 활용되지 않을 수 있다.
        - 게츠가 제안한 공식

            ![chapter16-03](image/chapter16-03.png)

    - 우리 애플리케이션은 상점의 응답을 대략 99%의 시간만큼 기다리므로 W/C 비율은 100으로 간주할 수 있다.
    - 즉, 대상 CPU 활용률이 100%라면 400 스레드를 갖는 풀을 만들어야 함을 의미한다.
    - 하지만 상점 수보다 많은 스레드를 가지고 있어봐야 사용할 가능성이 전혀 없으므로 상점 수보다 많은 스레드를 갖는 것은 시간 낭비일 뿐이다.
    - 즉, 가격 정보를 검색하려는 상점 수만큼 스레드를 갖도록 Executor를 설정한다.
    - 스레드 수가 너무 많으면 오히려 서버가 크래시될 수 있으므로 하나의 Executor에서 사용할 스레드의 최대 개수는 100 이하로 설정하는 것이 바람직하다.

    ```java
    // 상점 수만큼의 스레드를 갖는 풀을 생성한다. (스레드 수의 범위는 0~100)
    private final Executor executor = Executors.newFixedThreadPool(Math.min(shops.size(), 100), new ThreadFactory() {
    	public Thread newThread(Runnable r) {
    		Thread t = new Thread(r);
    		t.setDaemon(true); // 프로그램 종료를 방해하지 않는 데몬 스레드를 사용한다.
    		return t;
    	}
    });
    ```

    - 우리가 만드는 풀은 데몬 스레드(daemon thread)를 포함한다.
    - Java에서 일반 스레드가 실행 중이면 Java 프로그램은 종료되지 않는다.
        - 어떤 이벤트를 한없이 기다리면서 종료되지 않는 일반 스레드가 있으면 문제가 될 수 있다.
    - 반면 데몬 스레드는 Java 프로그램이 종료될 때 강제로 실행이 종료될 수 있다.
    - 두 스레드의 성능은 같다.
    - 팩토리 메서드 supplyAsync의 두 번째 인수로 새로운 Executor를 전달할 수 있다.

    ```java
    CompletableFuture.supplyAsync(() -> shop.getName() + " price is " + shop.getPrice(product), executor); 
    ```

    - CompletableFuture 버전의 코드 성능을 확인하니 다섯 개의 상점을 검색할 때는 1021ms, 아홉 개의 상점을 검색할 때는 1022ms가 소요된다.
    - 결국 애플리케이션의 특성에 맞는 Executor를 만들어 CompletableFuture를 활용하는 것이 바람직하다는 사실을 확인할 수 있다.
    - 적절한 병렬화 기법 선택
        - I/O가 포함되지 않은 계산 중심의 동작을 실행할 때는 스트림 인터페이스가 가장 구현하기 간단하며 효율적일 수 있다.
            - 모든 스레드가 계산 작업을 수행하는 상황에서는 프로세서 코어 수 이상의 스레드를 가질 필요가 없다.
        - 작업이 I/O를 기다리는 작업을 병렬로 실행할 때는 CompletableFuture가 더 많은 유연성을 제공하며 대기/계산(W/C)의 비율에 적합한 스레드 수를 설정할 수 있다.
            - 스트림의 게으른 특성 때문에 스트림에서 I/O를 실제로 언제 처리할지 예측하기 어려운 문제도 있다.

## 비동기 작업 파이프라인 만들기

- 우리와 계약을 맺은 모든 상점이 하나의 할인 서비스를 사용하기로 했다고 가정하자.

```java
// enum으로 할인 코드 정의
public class Discount {
	public enum Code {
		NONE(0), SILVER(5), GOLD(10), PLATINUM(15), DIAMOND(20);
	
		private final int percentage;

		Code(int percentage) {
			this.percentage = percentage;
		}
	}
	// 생략된 Discount 클래스 구현
}

// 또한 상점에서 getPrice 메서드의 결과 형식도 바꾸기로 했다.
public String getPrice(String product) {
	double price = calculatePrice(product);
	Discount.Code code = Discount.Code.values() [random.nextInt(Discount.Code.values().length];
	return String.format("%s:%.2f:%s", name, price, code);
}

private double calculatePrice(String product) {
	delay();
	return random.nextDouble() * product.charAt(0) + product.charAt(1);
}
```

- 할인 서비스 구현
    - 할인 서버에서 할인율을 확인해서 최종 가격을 계산할 수 있다.
        - 할인 코드와 연계된 할인율은 언제든 바뀔수 있으므로 매번 서버에서 정보를 얻어 와야 한다.
    - 상점에서 제공한 문자열 파싱은 다음처럼 Quote 클래스로 캡슐화할 수 있다.

        ```java
        public class Quote {
        	private final String shopName;
        	private final double price;
        	private final Discount.Code discountCode;

        	public Quote(String shopName, double price, Discount.Code code) {
        		this.shopName = shopName;
        		this.price = price;
        		this.discountCode = code;
        	}

        	public static Quote parse(String s) {
        		String[] split = s.split(":");
        		String shopName = split[0];
        		double price = Double.parseDouble(split[1]);
        		Discount.Code discountCode = Discount.Code.valueOf(split[2]);
        		return new Quote(shopName, price, discountCode);
        	}

        	public String getShopName() {
        		return shopName;
        	}

        	public double getPrice() {
        		return price;
        	}

        	public Discount.Code getDiscountCode() {
        		return discountCode;
        	}
        }
        ```

    - 다음 코드에서 보여주는 것처럼 Discount 서비스에서는 Quote 객체를 인수로 받아 할인된 가격 문자열을 반환하는 applyDiscount 메서드도 제공한다.

        ```java
        public class Discount {
        	public enum Code {
        		// 소스코드 생략
        	}
        	
        	// 기존 가격에 할인 코드를 적용한다.
        	public static String applyDiscount(Quote quote) {
        		return quote.getShopName() + " price is " + 
        			Discount.apply(quote.getPrice(), quote.getDiscountCode());
        	}

        	private static double apply(double price, Code code) {
        		delay(); // Discount 서비스의 응답 지연을 흉내낸다.
        		return format(price * (100 - code.percentage) / 100);
        	}
        }
        ```

- 할인 서비스 적용
    - 일단은 가장 쉬운 방법(즉, 순차적과 동기 방식)으로 findPrices 메서드를 구현한다.

        ```java
        public List<String> findPrices(String product) {
        	return shops.stream()
        		.map(shop -> shop.getPrice(product)) // 각 상점에서 할인 전 가격 얻기
        		.map(Quote::parse) // 상점에서 반환한 문자열을 Quote 객체로 변환
        		.map(Discount::applyDiscount) // Discount 서비스를 이용해서 각 Quote에 할인을 적용
        		.collect(toList());
        }
        ```

        - 세 개의 map 연산을 상점 스트림에 파이프라인으로 연결해서 원하는 결과를 얻었다.
            - 첫 번째 연산에서는 각 상점을 요청한 제품의 가격과 할인 코드로 변환한다.
            - 두 번째 연산에서는 이들 문자열을 파싱해서 Quote 객체를 만든다.
            - 세 번째 연산에서는 원격 Discount 서비스에 접근해서 최종 할인가격을 계산하고 가격에 대응하는 상점 이름을 포함하는 문자열을 반환한다.
        - 성능은?

            ```
            [BestPrice price is 110.93, LetsSaveBig price is 135.58, MyFavoriteShop price is 192.72, BuyItAll price is 184.74, ShopEasy price is 167.28]
            Done in 10028 msecs
            ```

        - 순차적으로 다섯 상점에 가격 정보를 요청하느라 5초가 소요되었고, 다섯 상점에서 반환한 가격 정보에 할인 코드를 적용할 수 있도록 할인 서비스에 5초가 소요되었다.
        - 병렬 스트림을 이용하면 성능을 쉽게 개선할 수 있지만 병렬 스트림에서는 스트림이 사용하는 스레드 풀의 크기가 고정되어 있어서 상점 수가 늘어났을 때처럼 검색 대상이 확장되었을 때 유연하게 대응할 수 없다는 사실도 확인했다.
- 동기 작업과 비동기 작업 조합하기
    - CompletableFuture에서 제공하는 기능을 사용해 비동기적으로 재현한 코드다.

        ```java
        public List<String> findPrices(String product) {
        	List<CompletableFuture<String>> priceFutures = shops.stream()
        		.map(shop -> CompletableFuture.supplyAsync(
        			() -> shop.getPrice(product), executor))
        		.map(future -> future.thenApply(Quote::parse))
        		.map(future -> future.thenCompose(
        			quote -> CompletableFuture.supplyAsync(
        				() -> Discount.applyDiscount(quote), executor)))
        		.collect(toList());

        		return priceFutures.stream()
        			.map(CompletableFuture::join)
        			.collect(toList());
        }
        ```

    - CompletableFuture의 기능을 이용해서 이들 동작을 비동기로 만들어야 한다.

        ![chapter16-04](image/chapter16-04.png '동기 작업과 비동기 작업 조합하기')

    - 흐름
        - 가격 정보 얻기(비동기 동작)
            - 상점에서 정보를 조회
        - Quote 파싱하기(동기 동작)
            - 원격 서비스나 I/O가 없으므로 원하는 즉시 지연 없이 동작을 수행한다.
            - thenApply 메서드는 CompletableFuture가 끝날 때까지 블록하지 않는다는 점을 주의해야 한다.
            - 즉, CompletableFuture가 동작을 완전히 완료한 다음에 thenApply 메서드로 전달된 람다 표현식을 적용할 수 있다.
        - CompletableFuture를 조합해서 할인된 가격 계산하기(비동기 동작)
            - 원격 Discount 서비스에서 제공하는 할인율을 적용
            - Java8의 CompletableFuture API는 이와 같이 두 비동기 연산을 파이프라인으로 만들 수 있도록 thenCompose 메서드를 제공한다.
            - 따라서 Future가 여러 상점에서 Quote를 얻는 동안 메인 스레드는 UI 이벤트에 반응하는 등 유용한 작업을 수행할 수 있다.
    - 결과는?

        ```
        [BestPrice price is 110.93, LetsSaveBig price is 135.58, MyFavoriteShop price is 192.72, BuyItAll price is 184.74, ShopEasy price is 167.28]
        Done in 2035 msecs
        ```

    - thenCompose 메서드도 Async로 끝나는 버전이 존재한다.
        - Async로 끝나지 않는 메서드는 이전 작업을 수행한 스레드와 같은 스레드에서 작업을 실행함을 의미하며 Async로 끝나는 메서드는 다음 작업이 다른 스레드에서 실행되도록 스레드 풀로 작업을 제출한다.
        - 여기서 두번째 CompleteFuture의 결과는 첫번째 CompletableFuture에 의존하므로 두 CompletableFuture를 하나로 조합하든 Async 버전의 메서드를 사용하든 최종 결과나 개괄적인 실행시간에는 영향을 미치지 않는다.
        - 따라서 스레드 전환 오버헤드가 적게 발생하면서 효율성이 좀 더 좋은 thenCompose를 사용했다.
- 독립 CompletableFuture와 비독립 CompletableFuture 합치기
    - 실전에서는 독립적으로 실행된 두 개의 CompletableFuture 결과를 합쳐야 하는 상황이 종종 발생한다.
    - 첫번째 CompletableFuture의 동작 완료와 관계없이 두 번째 CompletableFuture를 실행할 수 있어야 한다.
    - 이런 상황에서는 thenCombine 메서드를 사용한다.
        - BiFunction을 두번째 인수로 받는다.
        - BiFunction은 두개의 CompletableFuture 결과를 어떻게 합칠지 정의한다.
        - thenCombineAsync 메서드에서는 BiFunction이 정의하는 조합이 스레드 풀로 제출되면서 별도의 태스크에서 비동기적으로 수행된다.
    - 예제에서는 한 온라인 상점이 유로(EUR) 가격 정보를 제공하는데, 고객에게는 항상 달러(USD) 가격을 보여줘야 한다.
    - 주어진 상품의 가격을 요청하는 한편 원격 환율 교환 서비스를 이용해서 유로와 현재 환율을 비동기적으로 요청해야 한다.
    - 두 가지 데이터를 얻었으면 가격에 환율을 곱해서 결과를 합칠 수 있다.

    ```java
    Future<Double> futurePriceUSD = CompletableFuture.supplyAsync(
    	() -> shop.getPrice(product))
    	.thenCombine(CompletableFuture.supplyAsync(
    		() -> exchangeService.getRate(Money.EUR, Money.USD)), 
    			(price, rate) -> price * rate));
    ```

    - 여기서 합치는 연산은 단순한 곱셈이므로 별도의 태스크에서 수행하여 자원을 낭비할 필요가 없다.
        - thenCombineAsync 메서드를 사용할 필요가 없다.

    ![chapter16-05](image/chapter16-05.png '독립적인 두 개의 비동기 태스크 합치기')

- Future의 리플렉션과 CompletableFuture의 리플렉션
    - CompletableFuture는 람다 표현식을 사용한다.
    - 람다 덕분에 다양한 동기 태스크, 비동기 태스크를 활용해서 복잡한 연산 수행 방법을 효과적으로 쉽게 정의할 수 있는 선언형 API를 만들 수 있다.

    ```java
    // Java7로 두 Future 합치기
    ExecutorService executor = Executors.newCachedThreadPool(); // 태스크를 스레드 풀에 제출할 수 있도록 ExecutorService를 생성한다.
    final Future<Double> futureRate = executor.submit(new Callable<Double> () {
    	public Double call() {
    		return exchangeService.getRate(Money.EUR, Money.USD); // EUR, USD 환율 정보를 가져올 Future를 생성한다.
    	}
    });
    Future<Double> futurePriceInUSD = executor.submit(new Callable<Double> () {
    	public Double call() {
    		double priceInEUR = shop.getPrice(product); // 두번째 Future로 상점에서 요청 제품의 가격을 검색한다.
    		return priceInEUR * futureRate.get(); // 가격을 검색한 Future를 이용해서 가격과 환율을 곱한다.
    	}
    });
    ```

    - 얼핏 보기에는 두 구현이 크게 다른 것 같지 않을 수 있다.
- 타임아웃 효과적으로 사용하기
    - 앞에서 설명한 것처럼 Future의 계산 결과를 읽을 때는 무한정 기다리는 상황이 발생할 수 있으므로 블록하지 않는 것이 좋다.
    - Java9에서는 CompletableFuture에서 제공하는 몇 가지 기능을 이용해 이런 문제를 해결할 수 있었다.
    - orTimeout 메서드는 지정된 시간이 지난 후에 CompletableFuture를 TimeoutException으로 완료하면서 또 다른 ComletableFuture를 반환할 수 있도록 내부적으로 ScheduledThreadExecutor를 활용한다.
    - 이 메서드를 이용하면 계산 파이프라인을 연결하고 여기서 TimeoutException이 발생했을 때 사용자가 쉽게 이해할 수 있는 메시지를 제공할 수 있다.

    ```java
    // Java9에서 비동기 타임아웃 관리 기능이 추가됨
    Future<Double> futurePriceInUSD = CompletableFuture.supplyAsync(
    	() -> shop.getPrice(product))
    	.thenCombine(CompletableFuture.supplyAsync(
    		() -> exchangeService.getRate(Money.EUR, Money.USD)),
    			(price, rate) -> price * rate))
    	.orTimeout(3, TimeUnit.SECONDS); // 3초 뒤에 작업이 완료되지 않으면 Future가 TimeoutException을 발생시키도록 설정
    ```

    - 일시적으로 서비스를 이용할 수 없는 상황에서는 꼭 서버에서 얻은 값이 아닌 미리 지정된 값을 사용할 수 있는 상황도 있다.
    - orTimeout rTimeout 메서드처럼 completeOnTimeout 메서드는 CompletableFuture를 반환하므로 이 결과를 다른 CompletableFuture 메서드와 연결할 수 있다.

        ```java
        Future<Double> futurePriceInUSD = CompletableFuture.supplyAsync(
        	() -> shop.getPrice(product))
        	.thenCombine(CompletableFuture.supplyAsync(
        		() -> exchangeService.getRate(Money.EUR, Money.USD))
        			.completeOnTimeout(DEFAULT_RATE, 1, TimeUnit.SECONDS), // 환전 서비스가 일 초 안에 결과를 제공하지 않으면 기본 환율값을 사용
        			(price, rate) -> price * rate))
        	.orTimeout(3, TimeUnit.SECONDS);
        ```

## CompletableFuture의 종료에 대응하는 방법

- 여러 상점에 정보를 제공했을 때 몇몇 상점은 다른 상점보다 훨씬 먼저 결과를 제공할 가능성이 크다.
- 0.5초에서 2.5초 사이의 임의의 지연으로 이를 시뮬레이션하자.

    ```java
    private static final Random random = new Random();
    public static void randomDelay() {
    	int delay = 500 + random.nextInt(2000);
    	try {
    		Thread.sleep(delay);
    	} catch (InterruptedException e) {
    		throw new RuntimeException(e);
    	}
    }
    ```

- 최저가격 검색 애플리케이션 리팩터링
    - 먼저 모든 가격 정보를 포함할 때까지 리스트 생성을 기다리지 않도록 프로그램을 고쳐야 한다.

        ```java
        public Stream<CompletableFuture<String>> findPricesStream(String product) {
        	return shops.stream()
        		.map(shop -> CompletableFuture.supplyAsync(
        			() -> shop.getPrice(product), executor))
        		.map(future -> future.thenApply(Quote::parse))
        		.map(future -> future.thenCompose(quote -> CompletableFuture.supplyAsync(
        			() -> Discount.applyDiscount(quote), executors)));
        }
        ```

    - 새로 추가한 연산은 단순하게 각 CompletableFuture에 동작을 등록한다.
    - CompletableFuture API는 thenAccept라는 메서드로 이 기능을 제공한다.
    - thenAccept 메서드는 연산 결과를 소비하는 Consumer를 인수로 받는다.
    - thenAccept에도 thenAcceptAsync라는 Async 버전이 존재한다.
        - thenAcceptAsync 메서드는 CompletableFuture가 완료된 스레드가 아니라 새로운 스레드를 이용해서 Consumer를 실행한다.
        - 불필요한 콘텍스트 변경을 피하는 동시에 CompletableFuture가 완료되는 즉시 응답하는 것이 좋으므로 thenAcceptAsync를 사용하지 않는다.
        - 오히려 thenAcceptAsync를 사용하면 새로운 스레드를 이용할 수 있을 때까지 기다려야 하는 상황이 일어날 수 있다.
    - thenAccept 메서드는 CompletableFuture가 생성한 결과를 어떻게 소비할지 미리 지정했으므로 `CompletableFuture<Void>`를 반환한다.
    - 이렇게 해서 우리가 원하는 동작을 구현했다.
    - 또한 가장 느린 상점에서 응답을 받아서 반환된 가격을 출력할 기회를 제공하고 싶다고 가정하자.
        - 그러면 다음 코드에서 보여주는 것처럼 스트림의 모든 `CompletableFuture<Void>`를 배열로 추가하고 실행 결과를 기다려야 한다.

        ```java
        CompletableFuture[] futures = findPricesStream("myPhone")
        	.map(f -> f.thenAccept(System.out::println))
        	.toArray(size -> new CompletableFuture[size]);
        CompletableFuture.allOf(futures).join();
        ```

        - 팩토리 메서드 allOf는 CompletableFuture 배열을 입력으로 받아 `CompletableFuture<Void>`를 반환한다.
        - 전달된 모든 CompletableFuture가 완료되어야 `CompletableFuture<Void>`가 완료된다.
        - 따라서 allOf 메서드가 반환하는 CompletableFuture에 join을 호출하면 원래 스트림의 모든 CompletableFuture의 실행 완료를 기다릴 수 있다.
    - 배열의 CompletableFuture 중 하나의 작업이 끝나길 기다리는 상황에서 팩토리 메서드 anyOf를 사용할 수 있다.
        - ex) 두 개의 환율 정보 서버에 동시에 접근했을 때 한 서버의 응답만 있으면 충분하다.
        - anyOf 메서드는 CompletableFuture 배열을 입력으로 받아서 `CompletableFuture<Object>`를 반환한다.
        - `CompletableFuture<Object>`는 처음으로 완료한 CompletableFuture의 값으로 동작을 완료한다.
- 응용
    - 어떤 부분이 달라졌는지 명확하게 확인할 수 있도록 각각의 계산에 소요된 시간을 출력하는 부분을 코드에 추가했다.

        ```java
        long start = System.nanoTime();
        CompletableFuture[] futures = findPricesStream("myPhone27S")
        	.map(f -> f.thenAccept(s -> 
        		System.out.println(s + " (done is " + 
        			((System.nanoTime() - start) / 1_000_000) + " msecs")))
        	.toArray(size -> new CompletableFuture[size]);
        CompletableFuture.allOf(futures).join();
        System.out.println("All shops have now responsed in " + 
        	((System.nanoTime() - start) / 1_000_000) + " msecs");
        ```

    - 코드 수행 결과?

        ```
        BuyItAll price is 184.74 (done in 2005 msecs)
        MyFavoriteShop price is 192.72 (done in 2157 msecs)
        LetsSaveBig price is 135.58 (done in 3301 msecs)
        ShopEasy price is 167.28 (done in 3869 msecs)
        BestPrice price is 110.93 (done in 4188 msecs)
        All shops have now responsed in 4188 msecs
        ```