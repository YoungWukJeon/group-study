# 스트림에 여러 연산 병렬로 실행하기

- Stream에서는 한 번만 연산을 수행할 수 있으므로 결과도 한 번만 얻을 수 있다는 것이 Java 8 Stream의 가장 큰 단점이다.
    - 이미 소비한 Stream을 다시 사용하려고 할 경우 다음과 같은 에러가 발생한다.

    ```
    java.lang.IllegalStateException: stream has already been operated upon or closed
    ```

- 한 Stream에서 여러 결과를 얻어야 하는 상황이 있을 수 있다.
    - ex) 로그 파일을 Stream으로 파싱해서 한 번에 여러 통계를 얻는 상황 등
- 그러려면 한 번에 한 개 이상의 람다를 Stream으로 적용해야 한다.
- 안타깝게도 Java 8의 Stream은 이 기능을 제공하지 않는다.
- Appendix-C에서 이러한 기능을 만들어 볼 예정이다.
    - [http://mail.openjdk.java.net/pipermail/lambda-dev/2013-November/011516.html](http://mail.openjdk.java.net/pipermail/lambda-dev/2013-November/011516.html)

## Stream 포킹

- Stream에서 여러 연산을 병렬로 실행하기 위한 StreamForker를 정의한다.

    ```java
    public class StreamForker<T> {
    	private final Stream<T> stream;
      private final Map<Object, Function<Stream<T>, ?>> forks = new HashMap<> ();
        
      public StreamForker(Stream<T> stream) {
        this.stream = stream;
      }
        
      public StreamForker<T> fork(Object key, Function<Stream<T>, ?> f) {
        forks.put(key, f); // Stream에 적용할 함수 저장
        return this; // 유연하게 fork 메서드를 여러 번 호출할 수 있도록 this 반환
      }
        
      public Results getResults() {
        // 구현해야 함
      }
    }
    ```

    - fork 메서드는 StreamForker 자신을 반환하기 때문에 여러 연산을 포킹(forking: 분기)해서 파이프라인을 만들 수 있다.

    ![appendix-c-01](image/appendix-c-01.png 'StreamForker 동작 모습')

- getResults 메서드를 호출하면 fork 메서드로 추가한 모든 연산이 실행된다.

    ```java
    public static interface Results {
    	public <R> R get(Object key);
    }
    ```

    - Results 인터페이스는 fork 메서드에서 사용하는 key 객체를 받는 하나의 메서드 정의를 포함한다.
    - 이 메서드는 키에 대응하는 연산 결과를 반환한다.

### ForkingStreamConsumer로 Results 인터페이스 구현하기

- StreamForker의 getResults() 메서드의 내부를 다음과 같이 구현할 수 있다.

    ```java
    public Results getResults() {
    	ForkingStreamConsumer<T> consumer = build();
    	try {
    		stream.sequential().forEach(consumer);
    	} finally {
    		consumer.finish();
    	}
    	return consumer;
    }
    ```

    - ForkingStreamConsumer는 Results 인터페이스와 Consumer 인터페이스를 구현한다.
        - Stream의 모든 요소를 소비해서 for 메서드로 전달된 연산 수 만큼의 BlockingQueue로 분산시키는 것이 ForkingStreamConsumer의 주요 역할이다.
        - forEach 메서드를 병렬 Stream에 수행하면 큐에 삽입되는 요소의 순서가 흐트러질 수 있으므로 Stream을 순차로 처리하도록 지시한다.
- build() 메서드로 ForkingStreamConsumer를 만들 수 있다.

    ```java
    private ForkingStreamConsumer<T> build() {
    	List<BlockingQueue<T>> queues = new ArrayList<> (); // 각각의 연산을 저장할 큐 리스트를 생성
    	
    	// 연산 결과를 포함하는 Future를 연산을 식별할 수 있는 키에 대응시켜 맵에 저장
    	Map<Object, Future<?>> actions =
    		forks.entrySet().stream()
    			.reduce(new HashMap<Object, Future<?>> (),
    				(map, e) -> {
    					map.put(e.getKey(), getOperationResult(queues, e.getValue()));
    				}, (m1, m2) -> {
    					m1.putAll(m2);
    					return m1;
    				});
    	return new ForkingStreamConsumer<> (queues, actions);
    }
    ```

- getOperationResult 메서드로 각각의 Future를 만든다.

    ```java
    private Future<?> getOperationResult(List<BlockingQueue<T>> queues, Function<Stream<T>, ?> f) {
    	BlockingQueue<T> queue = new LinkedBlockingQueue<> ();
    	queues.add(queue); // 큐를 만들어 큐 리스트에 추가
    	Spliterator<T> spliterator = new BlockingQueueSpliterator<> (queue); // 큐의 요소를 탐색하는 Spliterator 생성
    	Stream<T> source = StreamSupport.stream(spliterator, false); // Spliterator를 소스로 갖는 Stream을 생성
    	return CompletableFuture.supplyAsync(() -> f.apply(source)); // Stream에서 주어진 함수를 비동기로 적용해서 결과를 얻을 Future 생성
    }
    ```

    - getOperationResult 메서드는 새로운 BlockingQueue를 생성하여 큐 리스트에 추가한다.
    - 그리고 큐를 새로운 BlockingQueueSpliterator로 전달한다.
    - 이것은 큐에서 탐색할 항목을 읽는 늦은 바인딩(late-binding) Spliterator다.
    - Spliterator를 탐색하는 순차 Stream을 만든 다음에 Stream에서 수행할 연산을 포함하는 함수를 적용한 결과를 계산한 Future를 만든다.

### ForkingStreamConsumer, BlockingQueueSpliterator 구현하기

```java
static class ForkingStreamConsumer<T> implements Consumer<T>, Results {
	static final Object END_OF_STREAM = new Object();

	private final List<BlockingQueue<T>> queues;
	private final Map<Object, Future<?>> actions;

	ForkingStreamConsumer(List<BlockingQueue<T>> queues, Map<Object, Future<?>> actions) {
		this.queues = queues;
		this.actions = actions;
	}

	@Override
	public void accept(T t) {
		queues.forEach(q -> q.add(t)); // Stream에서 탐색한 요소를 모든 큐로 전달
	}

	void finish() {
		accept((T) END_OF_STREAM); // Stream의 끝을 알리는 마지막 요소를 큐에 삽입
	}

	@Override
	public <R> R get(Object key) {
		try {
			return ((Future<R>) actions.get(key)).get(); // 키에 대응하는 동작의 결과를 반환. Future의 계산 완료 대기
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
```

- ForkingStreamConsumer 클래스는 Consumer 인터페이스와 Results 인터페이스를 구현하며, BlockingQueue의 List 참조와 Stream에 다양한 연산을 수행하는 Future의 Map 참조를 유지한다.

```java
class BlockingQueueSpliterator<T> implements Spliterator<T> {
	private final BlockingQueue<T> q;
	
	BlockingQueueSpliterator(BlockingQueue<T> q) {
		this.q = q;
	}

	@Override
	public boolean tryAdvance(Consumer<? super T> action) {
		T t;
		while (true) {
			try {
				t = q.take();
				break;
			} catch (InterruptedException e) {
			}
		}
		
		if (t != ForkingStreamConsumer.END_OF_STREAM) {
			action.accept(t);
			return true;
		}

		return false;
	}

	@Override
	public Spliterator<T> trySplit() {
		return null;
	}

	@Override
	public long estimateSize() {
		return 0;
	}

	@Override
	public int characteristics() {
		return 0;
	}
}
```

- 위 코드는 Stream을 어떻게 분할할지는 정의하지 않고 늦은 바인딩 기능만 활용하도록 Spliterator를 정의했다.
    - 따라서 trySplit은 구현하지 않았다.
- 큐에서 몇 개의 요소를 가져올 수 있는지 미리 알 수 없으므로 estimatedSize 값은 큰 의미가 없다.
    - 또한 분할을 하지 않는 상황이므로 estimatedSize 값을 활용하지 않아도 된다.
- Spliterator 특성을 사용하지 않으므로 characteristics 메서드는 0을 반환한다.
- tryAdvance 메서드는 ForkingStreamConsumer가 원래의 Stream에서 추가한 요소를 BlockingQueue에서 가져온다.
    - 이렇게 가져온 요소를 다음 Stream의 소스로 사용할 수 있도록 Consumer로 보낸다.
    - getOperationResult에서 생성한 Spliterator에서 요소를 보낼 Consumer를 결정하며, fork 메서드로 전달된 함수를 새로 만든 Stream에 적용한다.
    - ForkingStreamConsumer가 END_OF_STREAM 객체를 발견하기 전까지 true를 반환하며 소비할 다른 요소가 있음을 알린다.

![appendix-c-02](image/appendix-c-02.png 'StreamForker 빌딩 블록')

### StreamForker 활용

```java
// p139~140의 Dish 클래스와 초기 리스트 필요(4장. Stream 소개 예제)
Stream<Dish> menuStream = menu.stream();

StreamForker.Results results = new StreamForker<Dish> (menuStream)
		.fork("shortMenu", s -> s.map(Dish::getName).collect(joining(", ")))
		.fork("totalCalories", s -> s.mapToInt(Dish::getCalories).sum())
		.fork("mostCaloricDish", s -> s.collect(reducing((d1, d2) -> 
			d1.getCalories() > d2.getCalories()? d1: d2)).get())
		.fork("dishesByType", s -> s.collect(groupingBy(Dish::getType)))
		.getResults();

String shortMenu = results.get("shortMenu");
int totalCalories = results.get("totalCalories");
Dish mostCaloricDish = results.get("mostCaloricDish");
Map<Dish.Type, List<Dish>> dishesByType = results.get("dishesByType");

System.out.println("Short menu: " + shortMenu);
System.out.println("Total calories: " + totalCalories);
System.out.println("Most caloric dish: " + mostCaloricDish);
System.out.println("Dishes by type: " + dishesByType);
```

- StreamForker는 Stream을 포크하고 포크된 Stream에 다른 연산을 할당할 수 있도록 편리하고 유연한 API를 제공한다.
- 더 포크할 Stream이 없으면 StreamForker에 getResults를 호출해서 정의한 연산을 모두 수행하고 StreamForker.Results를 얻을 수 있다.
- 내부에서는 연산을 비동기적으로 수행하므로 getResults 메서드를 호출하면 결과를 기다리는 것이 아니라 즉시 반환된다.
- StreamForker.Results 인터페이스에 키를 전달해서 특정 연산의 결과를 얻을 수 있다.
    - 연산 결과가 끝난 상황이면 get 메서드가 결과를 반환한다.
    - 아직 연산이 끝나지 않았으면 결과가 나올 때까지 호출이 블록된다.
- 실행 결과

    ```
    Short menu: pork, beef, chicken, french fries, rice, season fruit, pizza, prawns, salmon
    Total calories: 4300
    Most calories dish: pork
    Dishes by type: {OTHER=[french fries, rice, season fruit, pizza], MEAT=[pork, beef, chicken], FISH=[prawns, salmon]}
    ```

## 성능 문제

- 메모리에 있는 데이터로 Stream을 만든 상황에서는 블록 큐를 사용하면서 발생하는 오버레드가 병렬 실행으로 인한 이득보다 클 수 있다.
- 아주 큰 파일을 Stream으로 사용하는 등 비싼 I/O 동작을 수행하는 상황에서는 한 번만 Stream을 활용하는 것이 더 좋은 선택일 수 있다.
- 가장 좋은 방법은 '직접 측정'해보는 것이다.