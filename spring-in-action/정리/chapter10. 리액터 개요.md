- 애플리케이션 코드를 개발할 때는 명령형(imperative)과 리액티브(reactive, 반응형)의 두 가지 형태로 코드를 작성할 수 있다.
    - **명령형** 코드는 순차적으로 연속되는 작업이며, 각 작업은 한 번에 하나씩 그리고 이전 작업 다음에 실행된다. 데이터는 모아서 처리되고 이전 작업이 데이터 처리를 끝낸 후에 다음 작업으로 넘어갈 수 있다.
    - **리액티브** 코드는 데이터 처리를 위해 일련의 작업들이 정의되지만, 이 작업들은 병렬로 실행될 수 있다. 그리고 각 작업은 부분 집합의 데이터를 처리할 수 있으며, 처리가 끝난 데이터를 다음 작업에 넘겨주고 다른 부분 집합의 데이터로 계속 작업할 수 있다.

# 리액티브 프로그래밍 이해하기

- 리액티브 프로그래밍은 명령형 프로그래밍의 대안이 되는 패러다임이다.
- 명령형 프로그래밍의 한계를 해결할 수 있기 때문이다.
- 실제로 현재 우리가 작성하는 대부분(또는 모든)의 코드는 여전히 명령형일 가능성이 높다.
- 명령형 프로그래밍의 발상은 간단하다.
- 즉, 한 번에 하나씩 만나는 순서대로 실행되는 명령어들로 코드를 작성하면 된다.
- 그리고 프로그램에서는 하나의 작업이 완전히 끝나기를 기다렸다가 그 다음 작업을 수행한다.
- 각 단계마다 처리되는 데이터는 전체를 처리할 수 있도록 사용할 수 있어야 한다.
- 그러나 작업이 수행되는 동안 이 작업이 완료될 때까지 아무 것도 할 수 없다.
    - 따라서 이 작업을 수행하는 스레드는 차단된다.
    - 이렇게 차단되는 스레드는 낭비다.
- 자바를 비롯해서 대부분의 프로그래밍 언어는 동시 프로그래밍(concurrent programming)을 지원한다.
    - 자바에서는 스레드가 어떤 작업을 계속 수행하는 동안 이 스레드에서 다른 스레드를 시작시키고 작업을 수행하게 하는 것은 매우 쉽다.
    - 그러나 스레드를 생성하는 것은 쉬울지라도 생성된 스레드는 어떤 이유로든 결국 차단된다.
    - 게다가 다중 스레드로 동시성을 관리하는 것은 쉽지 않다.
    - 스레드가 많을수록 더 복잡해지기 때문이다.
- 이에 반해 리액티브 프로그래밍은 본질적으로 함수적이면서 선언적이다.
    - 즉, 순차적으로 수행되는 작업 단계를 나타낸 것이 아니라 데이터가 흘러가는 파이프라인(pipeline)이나 스트림(stream)을 포함한다.
    - 그리고 이런 리액티브 스트림은 데이터 전체를 사용할 수 있을 때까지 기다리지 않고 사용 가능한 데이터가 있을 때마다 처리되므로 사실상 입력되는 데이터는 무한할 수 있다.

## 리액티브 스트림 정의하기

- 리액티브 스트림은 넷플릭스(Netflix), 라이트벤드(Lightbend), 피보탈(Pivotal)의 엔지니어들에 의해 2013년 말에 시작되었다.
- 리액티브 스트림은 차단되지 않는 백 프레셔(backpressure)를 갖는 비동기 스트림 처리의 표준을 제공하는 것이 목적이다.
- 리액티브 프로그래밍의 비동기 특성은 이미 얘기하였다.
- 즉, 동시에 여러 작업을 수행하여 더 큰 확장성을 얻게 해준다.
- 백 프레셔는 데이터를 소비하는(읽는) 컨슈머가 처리할 수 있는 만큼으로 전달 데이터를 제한함으로써 지나치게 빠른 데이터 소스로부터의 데이터 전달 폭주를 피할 수 있는 수단이다.
- 리액티브 스트림은 4개의 인터페이스 Publisher(발생자), Subscriber(구독자), Subscription(구독), Processor(프로세서)로 요약할 수 있다.
- Publisher는 하나의 Subscription당 하나의 Subscriber에 발행(전송)하는 데이터를 생성한다.

```java
public interface Publisher<T> {
	void subscribe(Subscriber<? super T> subscriber);
}
```

```java
public interface Subscriber<T> {
	void onSubscribe(Subscription sub);
	void onNext(T item);
	void onError(Throwable ex);
	void onComplete();
}
```

```java
public interface Subscription {
	void request(long n);
	void cancel();
}
```

```java
public interface Processor<T, R> extends Subscriber<T>, Publisher<R> {}
```

- 그러나 리액티브 스트림 인터페이스는 스트림을 구성하는 기능이 없다.
- 이에 따라 프로젝트 리액터에서는 리액티브 스트림을 구성하는 API를 제공하여 리액티브 스트림 인터페이스를 구현하였다.
- 리액터는 스프링 5의 리액티브 프로그래밍 모델의 기반이다.

# 리액터 시작하기

- 리액티브 프로그래밍은 명령형 프로그래밍과 매우 다른 방식으로 접근해야 한다.
- 즉, 일련의 작업 단계를 기술하는 것이 아니라 데이터 전달될 파이프라인을 구성하는 것이다.
- 그리고 이 파이프라인을 통해 데이터가 전달되는 동안 어떤 형태로든 변경 또는 사용될 수 있다.
- 예를 들어, 사람의 이름을 가져와서 모두 대문자로 변경한 후 이것으로 인사말 메시지를 만들어 출력한다고 해보자.
- 명령형 프로그래밍 모델에서는 다음과 같은 코드를 작성할 수 있다.

```java
String name = "Craig";
String capitalName = name.toUpperCase();
String greeting = "Hello, " + capitalName + "!";
System.out.println(greeting);
```

- 이 경우는 각 줄의 코드가 같은 스레드에서 한 단계씩 차례대로 실행된다.
- 이와는 다르게 리액티브 코드에서는 다음과 같이 할 수 있다.

```java
Mono.just("Craig")
	.map(n -> n.toUpperCase())
	.map(cn -> "Hello, " + cn + "!")
	.subscribe(System.out::println);
```

- 이 예의 리액티브 코드가 단계별로 실행되는 것처럼 보이겠지만, 실제로는 데이터가 전달되는 파이프라인을 구성하는 것이다.
- 그리고 파이프라인의 각 단계에서는 어떻게 하든 데이터가 변경된다.
- 또한, 각 오퍼레이션은 같은 스레드로 실행되거나 다른 스레드로 실행될 수 있다.
- 이 예의 Mono는 리액터의 두 가지 핵심 타입 중 하나이며, 다른 하나로는 Flux가 있다.
- 두 개 모두 리액티브 스트림의 Publisher 인터페이스를 구현한 것이다.
- Flux는 0, 1 또는 다수의(무한일 수 있는) 데이터를 갖는 파이프라인을 나타낸다.
- 반면에 Mono는 하나의 데이터 항목만 갖는 데이터셋에 최적화된 리액티브 타입이다.

## 리액티브 플로우의 다이어그램

- 리액티브 플로우는 마블 다이어그램(marble diagram)으로 나타내곤 한다.
- 마블 다이어그램의 제일 위에는 Flux나 Mono를 통해 전달되는 데이터의 타임라인을 나타내고, 중앙에는 오퍼레이션을, 제일 밑에는 결과로 생성되는 Flux나 Mono의 타임라인을 나타낸다.
- 원래의 Flux를 통해 데이터가 지나가는 동안 오퍼레이션을 통해 처리되어 새로운 Flux가 생성된다.
- Mono는 0 또는 하나의 데이터 항목과 에러를 갖는다는 것이 Flux와 다르다.

![chapter10-01](image/chapter10-01.png 'Flux의 기본적인 플로우를 보여주는 마블 다이어그램')

![chapter10-02](image/chapter10-02.png 'Mono의 기본적인 플로우를 보여주는 마블 다이어그램')

## 리액터 의존성 추가하기

- 리액터를 시작시키려면 다음 의존성을 포로젝트 빌드에 추가해야 한다.

```xml
<dependency>
	<groupId>io.projectreactor</groupId>
	<artifactId>reactor-core</artifactId>
</dependency>
```

- 만일 리액터 코드의 여러 테스트를 작성하고자 한다면 다음 의존성도 빌드에 추가하자

```xml
<dependency>
	<groupId>io.projectreactor</groupId>
	<artifactId>reactor-test</artifactId>
	<scope>test</scope>
</dependency>
```

- 스프링 부트는 의존성 관리를 자동으로 해주므로 해당 의존성에 `<version>` 요소를 지정할 필요가 없다.
- 그러나 스프링 부트가 아닌 프로젝트에 리액터를 사용하는 경우에는 리액터의 명세(Bill Of Materials, BOM)를 빌드에 설정해야 한다.

```xml
<dependencyManagement>
	<dependencies>
		<dependency>
			<groupId>io.projectreactor</groupId>
			<artifactId>reactor-bom</artifactId>
			<version>Bismuth-RELEASE</version>
			<type>pom</type>
			<scope>import</scope>
		</dependency>
	</dependencies>
</dependencyManagement>
```

# 리액티브 오퍼레이션 적용하기

- Flux와 Mono에는 500개 이상의 오퍼레이션이 있으며, 각 오퍼레이션은 다음과 같이 분류될 수 있다.
    - 생성(creation) 오퍼레이션
    - 조합(combination) 오퍼레이션
    - 변환(transformation) 오퍼레이션
    - 로직(logic) 오퍼레이션

## 리액티브 타입 생성하기

- 스프링에서 리액티브 타입을 사용할 때는 리퍼지터리나 서비스로부터 Flux나 Mono가 제공되므로 우리의 리액티브 타입을 생성할 필요가 있다.
- 그러나 데이터를 발행(방출)하는 새로운 리액티브 발행자(publisher)를 생성해야 할 때가 있다.

### 객체로부터 생성하기

- Flux나 Mono로 생성하려는 하나 이상의 객체(우리가 필요한 데이터를 갖는)가 있다면 Flux나 Mono의 just() 메서드(static 메서드임)를 사용하여 리액티브 타입을 생성할 수 있다.

```java
@Test
public void createAFlux_just() {
	Flux<String> fruitFlux = Flux.just("Apple", "Orange", "Grape", "Banana", "Strawberry");
}
```

- 이 경우 Flux는 생성되지만, 구독자(subscriber)가 없다.
- 구독자가 없이는 데이터가 전달되지 않을 것이다.
- 구독자를 추가할 때는 Flux의 subscribe() 메서드를 호출하면 된다.

```java
fruitFlux.subscribe(
	f -> System.out.println("Here's some fruit: " + f);
);
```

- 여기서 subscribe()에 지정된 람다는 실제로는 java.util.Consumer이며, 이것은 리액티브 스트림의 Subscriber 객체를 생성하기 위해 사용된다.
- subscribe()를 호출하는 즉시 데이터가 전달되기 시작한다.
- 리액터의 StepVerifier를 사용하는 것이 Flux나 Mono를 테스트하는 더 좋은 방법이다.
- Flux나 Mono가 지정되면 StepVerifier는 해당 리액티브 타입을 구독한 다음에 스트림을 통해 전달되는 데이터에 대해 어서션(assertion)을 적용한다.
- 그리고 해당 스트림이 기대한 대로 완전하게 작동하는지 검사한다.

```java
StepVerifier.create(fruitFlux)
	.expectNext("Apple")
	.expectNext("Orange")
	.expectNext("Grape")
	.expectNext("Banana")
	.expectNext("Strawberry")
	.verifyComplete();
```

- 이 경우 StepVerifier가 fruitFlux를 구독한 후 각 데이터 항목이 기대한 과일(fruit) 이름과 일치하는지 어서션을 적용한다.
- 그리고 마지막으로 fruitFlux가 완전한지 검사한다.

### 컬렉션으로부터 생성하기

- Flux는 또한 배열, Iterable 객체, 자바 Stream 객체로부터 생성될 수도 있다.

![chapter10-03](image/chapter10-03.png 'Flux는 배열, Iterable 객체, Stream 객체로부터 생성될 수 있다.')

- 배열로부터 Flux를 생성하려면 static 메서드인 fromArray()를 호출하며, 이때 소스 배열을 인자로 전달한다.

```java
@Test
public void createAFlux_fromArray() {
	String[] fruits = new String[] {
		"Apple", "Orange", "Grape", "Banana", "Strawberry"};

	Flux<String> fruitFlux = Flux.fromArray(fruits);

	StepVerifier.create(fruitFlux)
		.expectNext("Apple")
		.expectNext("Orange")
		.expectNext("Grape")
		.expectNext("Banana")
		.expectNext("Strawberry")
		.verifyComplete();
}
```

- java.util.List, java.util.Set 또는 java.lang.Iterable의 다른 구현 컬렉션으로부터 Flux를 생성해야 한다면 해당 컬렉션을 인자로 전달하여 static 메서드인 fromIterable()을 호출하면 된다.

```java
@Test
public void createAFlux_fromIterable() {
	List<String> fruitList = new ArrayList<>();
	fruitList.add("Apple");
	fruitList.add("Orange");
	fruitList.add("Grape");
	fruitList.add("Banana");
	fruitList.add("Strawberry");
	
	Flux<String> fruitFlux = Flux.fromIterable(fruitList);
	
	// ... 검사하는 코드
}
```

- 또는 Flux를 생성하는 소스로 자바 Stream 객체를 사용해야 한다면 static 메서드인 fromStream()을 호출하면 된다.

```java
@Test
public void createAFlux_fromStream() {
	Stream<String> fruitStream = Stream.of("Apple", "Orange", "Grape", "Banana", "Strawberry");
	
	Flux<String> fruitFlux = Flux.fromStream(fruitStream);
	
	// ... 검사하는 코드
}
```

### Flux 데이터 생성하기

- 때로는 데이터 없이 매버너 새 값으로 증가하는 숫자를 방출하는 카운터 역할의 Flux만 필요한 경우가 있다.
- 이와 같은 카운터 Flux를 생성할 때는 static 메서드인 range()를 사용할 수 있다.

![chapter10-04](image/chapter10-04.png '카운터 Flux 생성하기')

```java
@Test
public void createAFlux_range() {
	Flux<Integer> intervalFlux = Flux.range(1, 5);

	StepVerifier.create(intervalFlux)
		.expectNext(1)
		.expectNext(2)
		.expectNext(3)
		.expectNext(4)
		.expectNext(5)
		.verifyComplete();
}
```

- 이 예에서는 1부터 5까지의 값을 포함하는 카운터 Flux가 생성된다.
- range()와 유사한 또 다른 Flux 생성 메서드로 interval()이 있다.
- range() 메서드처럼 interval()도 증가값을 방출하는 Flux를 생성한다.
- 그러나 시작 값과 종료 값 대신 값이 방출되는 시간 간격이나 주기를 지정한다.

![chapter10-05](image/chapter10-05.png '시간 간격으로부터 생성되는 Flux는 주기적인 항목을 갖는다.')

- 예를 들어, 매초마다 값을 방출하는 Flux를 생성하려면 다음과 같이 static 메서드인 interval()을 사용하면 된다.

```java
@Test
public void createAFlux_interval() {
	Flux<Long> intervalFlux =
		Flux.interval(Duration.ofSeconds(1))
			.take(5);

	StepVerifier.create(intervalFlux)
		.expectNext(1)
		.expectNext(2)
		.expectNext(3)
		.expectNext(4)
		.expectNext(5)
		.verifyComplete();
}
```

- 이런 Flux가 방출하는 값은 0부터 시작하여 값이 증가한다는 것에 유의하자.
- 또한, interval()에는 최대값이 지정되지 않으므로 무한정 실행된다.
- 따라서 이 경우 take() 오퍼레이션을 사용해서 첫 번째  5개의 항목으로 결과를 제한할 수 있다.

## 리액티브 타입 조합하기

- 두 개의 리액티브 타입을 결합해야 하거나 하나의 Flux를 두 개 이상의 리액티브 타입으로 분할해야 하는 경우가 있을 수 있다.

### 리액티브 타입 결합하기

- 하나의 Flux를 다른 것과 결합하려면 mergeWith() 오퍼레이션을 사용하면 된다.

![chapter10-06](image/chapter10-06.png '두 Flux 스트림을 결합하면 각 스트림의 메시지가 새로운 Flux로 끼워진다.')

- 예를 들어, TV나 영화의 캐릭터 이름을 값으로 갖는 Flux가 하나 있고, 이 캐릭터들이 즐겨 먹는 식품 이름의 값으로 갖는 또 다른 Flux가 있다고 해보자.

```java
@Test
public void mergeFluxes() {
	Flux<String> characterFlux = Flux
		.just("Garfield", "Kojak", "Barbossa")
		.delayElements(Duration.ofMillis(500));
	Flux<String> foodFlux = Flux
		.just("Lasagna", "Lollipops", "Apples")
		.delaySubscription(Duration.ofMillis(250))
		.delayElements(Duration.ofMillis(500));

	Flux<String> mergedFlux = characterFlux.mergeWith(foodFlux);

	StepVerifier.create(mergedFlux)
		.expectNext("Garfield")
		.expectNext("Lasagna")
		.expectNext("Kojak")
		.expectNext("Lollipops")
		.expectNext("Barbossa")
		.expectNext("Apples")
		.verifyComplete();
}
```

- 일반적으로 Flux는 가능한 빨리 데이터를 방출한다.
- 따라서 생성되는 Flux 스트림 두 개 모두에 delayElements() 오퍼레이션을 사용해서 조금 느리게 방출되도록(500밀리초마다) 하였다.
- 또한, foodFlux가 characterFlux 다음에 스트리밍을 시작하도록 foodFlux에 delaySubscription() 오퍼레이션을 적용하여 250밀리초가 지난 후에 구독 및 데이터를 방출하도록 하였다.
- 두 Flux 객체가 결합되면 하나의 Flux(mergedFlux)가 새로 생성된다.
- mergedFlux로부터 방출되는 항목의 순서는 두 개의 소스 Flux로부터 방출되는 시간에 맞춰 결정된다.
- 여기서는 두 Flux 객체 모두 일정한 속도로 방출되게 설정되었으므로 두 Flux의 값은 번갈아 mergedFlux에 끼워진다.
- mergeWith()는 소스 Flux들의 값이 완벽하게 번갈아 방출되게 보장할 수 없으므로 필요하다면 zip() 오퍼레이션을 대신 사용할 수 있다.
- 이 오퍼레이션은 각 Flux 소스로부터 한 항목씩 번갈아 가져와 새로운 Flux를 생성한다.

![chapter10-07](image/chapter10-07.png 'zip() 오퍼레이션은 각 Flux 소스로부터 한 항목씩 번갈아 가져와 새로운 Flux를 생성한다.')

```java
@Test
public void zipFluxes() {
	Flux<String> characterFlux = Flux
		.just("Garfield", "Kojak", "Barbossa");
	Flux<String> foodFlux = Flux
		.just("Lasagna", "Lollipops", "Apples");

	Flux<Tuple2<String, String>> zippedFlux = Flux.zip(characterFlux, foodFlux);

	StepVerifier.create(zippedFlux)
		.expectNextMatches(p -> 
			p.getT1().equals("Garfield") &&
			p.getT2().equals("Lasagna"))
		.expectNextMatches(p -> 
			p.getT1().equals("Kojak") &&
			p.getT2().equals("Lollipops"))
		.expectNextMatches(p -> 
			p.getT1().equals("Barbossa") &&
			p.getT2().equals("Apples"))
		.verifyComplete();
}
```

- mergeWith()와 다르게 zip() 오퍼레이션은 정적인 생성 오퍼레이션이다.
- zippedFlux로부터 방출되는 각 항목은 Tuple2(두 개의 다른 객체를 전달하는 컨테이너 객체)이며, 각 소스 Flux가 순서대로 방출하는 항목을 포함한다.
- 만일 Tuple2가 아닌 다른 타입을 사용하고 싶다면 우리가 원하는 객체를 생성하는 함수를 zip()에 제공하면 된다.

![chapter10-08](image/chapter10-08.png '두 개의 입력 Flux 요소로부터 생성된 메시지를 포함하는 Flux를 생성하는 zip() 오퍼레이션')

- 예를 들어, 다음 테스트 메서드는 캐릭터 이름 Flux와 식품 이름 Flux를 zip()하여 String 객체의 Flux를 생성하는 방법을 보여준다.

```java
@Test
public void zipFluxesToObject() {
	Flux<String> characterFlux = Flux
		.just("Garfield", "Kojak", "Barbossa");
	Flux<String> foodFlux = Flux
		.just("Lasagna", "Lollipops", "Apples");

	Flux<String> zippedFlux = 
		Flux.zip(characterFlux, foodFlux, (c, f) -> c + " eats " + f);

	StepVerifier.create(zippedFlux)
		.expectNext("Garfield eats Lasagna")
		.expectNext("Kojak eats Lollipops")
		.expectNext("Barbossa eats Apples")
		.verifyComplete();
}
```

### 먼저 값을 방출하는 리액티브 타입 선택하기

- 두 개의 Flux 객체가 있는데, 이것을 결합하는 대신 먼저 값을 방출하는 소스 Flux의 값을 발행하는 새로운 Flux를 생성하고 싶다고 해보자.
- first() 오퍼레이션은 두 Flux 객체 중 먼저 값을 방출하는 Flux의 값을 선택해서 이값을 발행한다.

![chapter10-09](image/chapter10-09.png 'first() 오퍼레이션은 먼저 값을 방출하는 소스 Flux를 선택해서 메시지로 발행한다.')

```java
@Test
public void firstFlux() {
	Flux<String> slowFlux = Flux.just("tortoise", "snail", "sloth")
		.delaySubscription(Duration.ofMillis(100));
	Flux<String> fastFlux = Flux.just("hare", "cheetah", "squirrel");

	Flux<String> firstFlux = Flux.first(slowFlux, fastFlux);

	StepVerifier.create(firstFlux)
		.expectNext("hare")
		.expectNext("cheetah")
		.expectNext("squirrel")
		.verifyComplete();
}
```

- 새로 생성되는 Flux(firstFlux)는 느린 Flux를 무시하고 빠른 Flux(fastFlux)의 값만 발행하게 된다.

## 리액티브 스트림의 변환과 필터링

- 데이터가 스트림을 통해 흐르는 동안 일부 값을 필터링하거나(걸러내거나) 다른 값으로 변경해야 할 경우가 있다.

### 리액티브 타입으로부터 데이터 필터링하기

- Flux로부터 데이터가 전달될 때 이것을 필터링하는 가장 기본적인 방법은 맨 앞부터 원하는 개수의 항목을 무시하는 것이다.
- 이때 skip() 오퍼레이션을 사용한다.

![chapter10-10](image/chapter10-10.png 'skip() 오퍼레이션은 지정된 수의 메시지를 건너뛴 후에 나머지 메시지를 결과 Flux로 전달한다.')

- 다수의 항목을 갖는 소스 Flux가 지정되었을 때 skip() 오퍼레이션을 소스 Flux의 항목에서 지정된 수만큼 건너뛴 후 나머지 항목을 방출하는 새로운 Flux를 생성한다.

```java
@Test
public void skipAFew() {
	Flux<String> skipFlux = Flux.just(
		"one", "two", "skip a few", "ninety nine", "one hundred")
		.skip(3);

	StepVerifier.create(skipFlux)
		.expectNext("ninety nine", "one hundred")
		.verifyComplete();
}
```

- 그러나 특정 수의 항목을 건너뛰는 대신, 일정 시간이 경과할 때까지 처음의 여러 항목을 건너뛰어야 하는 경우가 있다.
- 이런 형태의 skip() 오퍼레이션은 지정된 시간이 경과할 때까지 기다렸다가 소스 Flux의 항목을 방출하는 Flux를 생성한다.

![chapter10-11](image/chapter10-11.png '이런 형태의 skip() 오퍼레이션은 지정된 시간이 경과할 때까지 기다렸다가 결과 Flux로 메시지를 전달한다.')

```java
@Test
public void skipAFewSeconds() {
	Flux<String> skipFlux = Flux.just(
		"one", "two", "skip a few", "ninety nine", "one hundred")
		.delayElements(Duration.ofSeconds(1))
		.skip(Duration.ofSeconds(4));

	StepVerifier.create(skipFlux)
		.expectNext("ninety nine", "one hundred")
		.verifyComplete();
}
```

- skip() 오퍼레이션의 반대 기능이 필요할 때는 take()를 고려할 수 있다.
- take()는 처음부터 지정된 수의 항목만을 방출한다.

```java
@Test
public void take() {
	Flux<String> nationalParkFlux = Flux.just(
		"Yellowstone", "Yosemite", "Grand Canyon", 
		"Zion", "Grand Teton")
		.take(3);
		
	StepVerifier.create(nationalParkFlux)
		.expectNext("Yellowstone", "Yosemite", "Grand Canyon")
		.verifyComplete();
}
```

![chapter10-12](image/chapter10-12.png 'take() 오퍼레이션은 입력 Flux로부터 처음부터 지정된 수의 메시지만 전달하고 구독을 취소시킨다.')

- skip()처럼 take()도 항목 수가 아닌 경과 시간을 기준으로 하는 다른 형태를 갖는다.
- 이 경우 소스 Flux로부터 전달되는 항목이 일정 시간이 경과될 동안만 방출된다.

![chapter10-13](image/chapter10-13.png '이런 형태의 take() 오퍼레이션은 일정 시간이 경과될 동안만 결과 Flux로 메시지를 전달한다.')

```java
@Test
public void takeForAwhile() {
	Flux<String> nationalParkFlux = Flux.just(
		"Yellowstone", "Yosemite", "Grand Canyon", 
		"Zion", "Grand Teton")
		.delayElements(Duration.ofSeconds(1))
		.take(Duration.ofMillis(3500));

	StepVerifier.create(nationalParkFlux)
		.expectNext("Yellowstone", "Yosemite", "Grand Canyon")
		.verifyComplete();
}
```

- skip()과 take() 오퍼레이션은 카운트나 경과 시간을 필터 조건으로 하는 일종의 필터 오퍼레이션이라고 생각할 수 있다.
- 그러나 Flux 값의 더 범용적인 필터링을 할 때는 filter() 오퍼레이션이 매우 유용하다.
- Flux를 통해 항목을 전달할 것인가의 여부를 결정하는 조건식(Predicate)이 지정되면 filter() 오퍼레이션에서 우리가 원하는 조건을 기반으로 선택적인 발행을 할 수 있다.

![chapter10-14](image/chapter10-14.png '지정된 조건식에 일치되는 메시지만 결과 Flux가 수신하도록 입력 Flux를 필터링할 수 있다.')

```java
@Test
public void filter() {
	Flux<String> nationalParkFlux = Flux.just(
		"Yellowstone", "Yosemite", "Grand Canyon", 
		"Zion", "Grand Teton")
		.filter(np -> !np.contains(" "));
	StepVerifier.create(nationalParkFlux)
		.expectNext("Yellowstone", "Yosemite", "Zion")
		.verifyComplete();
}
```

- distinct() 오퍼레이션을 사용하면 발행된 적이 없는(중복되지 않는) 소스 Flux의 항목만 발행하는 결과 Flux를 생성한다.

![chapter10-15](image/chapter10-15.png 'distinct() 오퍼레이션은 중복 메시지를 걸러낸다.')

```java
@Test
public void distinct() {
	Flux<String> animalFlux = Flux.just(
		"dog", "cat", "bird", "dog", "bird", "anteater")
		.distinct();

	StepVerifier.create(animalFlux)
		.expectNext("dog", "cat", "bird", "anteater")
		.verifyComplete();
}
```

### 리액티브 데이터 매핑하기

- Flux나 Mono에 가장 많이 사용하는 오퍼레이션 중 하나는 발행된 항목을 다른 형태나 타입으로 매핑(변환)하는 것이다.
- 리액터의 타입은 이런 목적의 map()과 flatMap() 오퍼레이션을 제공한다.
- map() 오퍼레이션은 변환(각 객체에 지정된 함수에 의해 처리되는)을 수행하는 Flux를 생성한다.

![chapter10-16](image/chapter10-16.png 'map() 오퍼레이션은 입력 메시지의 변환을 수행하여 결과 스트림의 새로운 메시지로 발행한다.')

```java
@Test
public void map() {
	Flux<Player> playerFlux = Flux
		.just("Michael Jordan", "Scottie Pippen", "Steve Kerr")
		.map(n -> {
			String[] split = n.split("\\s");
			return new Player(split[0], split[1]);
		});

	StepVerifier.create(playerFlux)
		.expectNext(new Player("Michael", "Jordan"))
		.expectNext(new Player("Scottie", "Pippen"))
		.expectNext(new Player("Steve", "Kerr"))
		.verifyComplete();
}
```

- map()에서 알아 둘 중요한 것은, 각 항목이 소스 Flux로부터 발행될 때 동기적으로(각 항목을 순차적 처리) 매핑이 수행된다는 것이다.
- 따라서 비동기적으로 (각 항목을 병행 처리) 매핑을 수행하고 싶다면 flatMap() 오퍼레이션을 사용해야 한다.
- map()에서는 한 객체를 다른 객체로 매핑하는 정도였지만, flatMap()에서는 각 객체를 새로운 Mono나 Flux로 매핑하며, 해당 Mono나 Flux들의 결과는 하나의 새로운 Flux가 된다.
- flatMap()을 subscribeOn()과 함께 사용하면 리액터 타입의 변환을 비동기적으로 수행할 수 있다.

![chapter10-17](image/chapter10-17.png 'flatMap() 오퍼레이션은 수행 도중 실행되는 임시 Flux를 사용해서 변환을 수행하므로 비동기 변환이 가능하다.')

```java
@Test
public void flatMap() {
	Flux<Player> playerFlux = Flux
		.just("Michael Jordan", "Scottie Pippen", "Steve Kerr")
		.flatMap(n -> Mono.just(n)
			.map(p -> {
				String[] split = p.split("\\s");
				return new Player(split[0], split[1]);
			})
		.subscribeOn(Schedulers.parallel())
	);

	List<Player> playerList = Arrays.asList(
		new Player("Michael", "Jordan"),
		new Player("Scottie", "Pippen"),
		new Player("Steve", "Kerr"));

	StepVerifier.create(playerFlux)
		.expectNextMatches(p -> playerList.contains(p))
		.expectNextMatches(p -> playerList.contains(p))
		.expectNextMatches(p -> playerList.contains(p))
		.verifyComplete();
}
```

- 마지막에 subscribeOn()을 호출하였다.
- 것은 각 구독이 병렬 스레드로 수행되어야 한다는 것을 나타낸다.
- 따라서 다수의 입력 객체(String 타입)들의 map() 오퍼레이션이 비동기적으로 병행 수행될 수 있다.
- 리액터는 어떤 특정 동시성 모델도 강요하지 않으며, 우리가 사용하기 원하는 동시성 모델을 subscribeOn()의 인자로 지정할 수 있다.
- 이때 Schedulers의 static 메서드 중 하나를 사용한다.
- 이 예에서는 고정된 크기의 스레드 풀(CPU 코어의 개수가 크기가 됨)의 작업 스레드로 실행되는 parallel()을 사용하였다.
- Schedulers의 동시성 모델

| Schedulers 메서드 | 개요 |
| --- | --- |
| .immediate() | 현재 스레드에서 구독을 실행한다. |
| .single() | 단일의 재사용 가능한 스레드에서 구독을 실행한다. 모든 호출자에 대해 동일한 스레드를 재사용한다. |
| .newSingle() | 매 호출마다 전용 스레드에서 구독을 실행한다. |
| .elastic() | 무한하고 신축성 있는 풀에서 가져온 작업 스레드에서 구독을 실행한다. 필요 시 새로운 작업 스레드가 생성되며, 유휴 스레드는 제거된다.(기본적으로 60초 후에) |
| .parallel() | 고정된 크기의 풀에서 가져온 작업 스레드에서 구독을 실행하며, CPU 코어의 개수가 크기가 된다. |

- flatMap()이나 subscribeOn()을 사용할 때의 장점은 다수의 병행 스레드에 작업을 분할하여 스트림의 처리량을 증가시킬 수 있다는 것이다.
- 그러나 작업이 병행으로 수행되므로 어떤 작업이 먼저 끝날지 보장이 안되어 결과 Flux에서 방출되는 항목의 순서를 알 방법이 없다.

### 리액티브 스트림의 데이터 버퍼링하기

- Flux를 통해 전달되는 데이터를 처리하는 동안 데이터 스트림을 작은 덩어리로 분할하면 도움이 될 수 있다.
- 이때 buffer() 오퍼레이션을 사용할 수 있다.

![chapter10-18](image/chapter10-18.png 'buffer() 오퍼레이션은 지정된 초대 크기의 리스트(입력 Flux로부터 수집된)로 된 Flux를 생성한다.')

```java
@Test
public void buffer() {
	Flux<String> fruitFlux = Flux.just(
		"apple", "orange", "banana", "kiwi", "strawberry");

	Flux<List<String>> bufferedFlux = fruitFlux.buffer(3);

	StepVerifier
		.create(bufferedFlux)
		.expectNext(Arrays.asList("apple", "orange", "banana"))
		.expectNext(Arrays.asList("kiwi", "strawberry"))
		.verifyComplete();
}
```

- buffer()를 flatMap()과 같이 사용하면 각 List 컬렉션을 병행으로 처리할 수 있다.

```java
Flux.just("apple", "orange", "banana", "kiwi", "strawberry")
	.buffer(3)
	.flatMap(x ->
		Flux.fromIterable(x)
			.map(y -> y.toUpperCase())
			.subscribeOn(Schedulers.parallel())
			.log()
	).subscribe();
```

- 정말 이렇게 실행되는지 확인하기 위해 각 하위 Flux에 log() 오퍼레이션을 포함시켰다.
- log() 오퍼레이션은 모든 리액티브 스트림 이벤트를 로깅하므로 실제 어떻게 되는지 파악할 수 있다.
- 결과적으로 다음 항목들이 로그에 수록되었다.

```
[main] INFO reactor.Flux.SubscribeOn.1 - onSubscribe(FluxSubscribeOn.SubscribeOnSubscriber)
[main] INFO reactor.Flux.SubscribeOn.1 - request(32)
[main] INFO reactor.Flux.SubscribeOn.2 - onSubscribe(FluxSubscribeOn.SubscribeOnSubscriber)
[main] INFO reactor.Flux.SubscribeOn.2 - request(32)
[parallel-1] INFO reactor.Flux.SubscribeOn.1 - onNext(APPLE)
[parallel-2] INFO reactor.Flux.SubscribeOn.2 - onNext(KIWI)
[parallel-1] INFO reactor.Flux.SubscribeOn.1 - onNext(ORANGE)
[parallel-2] INFO reactor.Flux.SubscribeOn.2 - onNext(STRAWBERRY)
[parallel-1] INFO reactor.Flux.SubscribeOn.1 - onNext(BANANA)
[parallel-1] INFO reactor.Flux.SubscribeOn.1 - onComplete()
[parallel-2] INFO reactor.Flux.SubscribeOn.2 - onComplete()
```

- 만일 어떤 이유로든 Flux가 방출하는 모든 항목을 List로 모을 필요가 있다면 인자를 전달하지 않고 buffer()를 호출하면 된다.

```java
Flux<List<String>> bufferedFlux = fruitFlux.buffer();
```

- 이 경우 소스 Flux가 발행한 모든 항목을 포함하는 List를 방출하는 새로운 Flux가 생성된다.
- collectList() 오퍼레이션을 사용해도 같은 결과를 얻을 수 있다.

![chapter10-19](image/chapter10-19.png 'collectList() 오퍼레이션은 입력 Flux가 방출하는 모든 메시지를 갖는 List의 Mono를 생성한다.')

- collectList()는 List를 발행하는 Flux 대신 Mono를 생성한다.

```java
@Test
public void collectList() {
	Flux<String> fruitFlux = Flux.just(
		"apple", "orange", "banana", "kiwi", "strawberry");

	Mono<List<String>> fruitListMono = fruitFlux.collectList();

	StepVerifier
		.create(fruitListMono)
		.expectNext(Arrays.asList(
			"apple", "orange", "banana", "kiwi", "strawberry"))
		.verifyComplete();
}
```

- Flux가 방출하는 항목들을 모으는 훨씬 더 흥미로운 방법으로 collectMap()이 있다.
- collectMap() 오퍼레이션은 Map을 포함하는 Mono를 생성한다.
- 이때 해당 Map에는 지정된 함수로 산출된 키를 갖는 항목이 저장된다.

![chapter10-20](image/chapter10-20.png 'collectMap() 오퍼레이션은 Map을 포함하는 Mono를 생성한다. 이때 입력 Flux가 방출한 메시지가 해당  Map의 항목으로 저장되며, 각 항목의 키는 입력 메시지의 특성에 따라 추출된다.')

```java
@Test
public void collectMap() {
	Flux<String> animalFlux = Flux.just(
		"aardvark", "elephant", "koala", "eagle", "kangaroo");

	Mono<Map<Character, String>> animalMapMono =
		animalFlux.collectMap(a -> a.charAt(0));

	StepVerifier
		.create(animalMapMono)
		.expectNextMatches(map -> {
			return
				map.size() == 3 &&
				map.get('a').equals("aardvark") &&
				map.get('e').equals("eagle") &&
				map.get('k').equals("kangaroo");
		})
		.verifyComplete();
}
```

## 리액티브 타입에 로직 오퍼레이션 수행하기

- Mono나 Flux가 발행한 항목이 어떤 조건과 일치하는지만 알아야 할 경우가 있다.
- 이때는 all()이나 any() 오퍼레이션이 그런 로직을 수행한다.

![chapter10-21](image/chapter10-21.png '모든 메시지가 조건을 충족하는지 확인하기 위해 all() 오퍼레이션으로 Flux를 검사할 수 있다.')

![chapter10-22](image/chapter10-22.png '최소한 하나의 메시지가 조건을 충족하는지 확인하기 위해 any() 오퍼레이션으로 Flux를 검사할 수 있다.')

- Flux가 발행하는 모든 문자열이 문자 a나 k를 포함하는지 알고 싶다고 하자.
- 다음 테스트에서는 all()을 사용해서 이런 조건을 검사하는 방법을 보여준다.

```java
@Test
public void all() {
	Flux<String> animalFlux = Flux.just(
		"aardvark", "elephant", "koala", "eagle", "kangaroo");

	Mono<Boolean> hasAMono = animalFlux.all(a -> a.contains("a"));
	StepVerifier.create(hasAMono)
		.expectNext(true)
		.verifyComplete();

	Mono<Boolean> hasKMono = animalFlux.all(a -> a.contains("k"));
	StepVerifier.create(hasKMono)
		.expectNext(false)
		.verifyComplete();
}
```

- 최소한 하나의 항목이 일치하는지 검사할 경우가 있다.
- 이때는 any() 오퍼레이션을 사용한다.

```java
@Test
public void any() {
	Flux<String> animalFlux = Flux.just(
		"aardvark", "elephant", "koala", "eagle", "kangaroo");

	Mono<Boolean> hasTMono = animalFlux.any(a -> a.contains("t"));

	StepVerifier.create(hasTMono)
		.expectNext(true)
		.verifyComplete();

	Mono<Boolean> hasZMono = animalFlux.any(a -> a.contains("z"));
	StepVerifier.create(hasZMono)
		.expectNext(false)
		.verifyComplete();
}
```