# 결론 그리고 자바의 미래

## Java8의 기능 리뷰

- Java8에 추가된 대부분의 새로운 기능을 Java에서 함수형 프로그래밍을 쉽게 적용할 수 있도록 도와준다는 사실을 강조할 것이다.
- Java8에 이렇게 큰 변화가 생긴 이유는 기후 변화로 비유한 커다란 두 가지 추세 때문임을 기억하자
    - 멀티코어 프로세서의 파워를 충분히 활용해야 한다는 것이다.
        - 실리콘 기술이 발전하면서 개별 CPU 코어의 속도가 빨라지고 있다.
        - 즉, 코드를 병렬로 실행해야 더 빠르게 코드를 실행할 수 있다.
    - 데이터 소스를 이용해서 주어진 조건과 일치하는 모든 데이터를 추출하고, 결과에 어떤 연산을 적용하는 등 선언형으로 데이터를 처리하는 방식, 즉 간결하게 데이터 컬렉션을 다루는 추세다.
        - 간결하게 데이터 컬렉션을 처리하려면 불변값을 생성할 수 있는 불변 객체와 불변 컬렉션이 필요하다.

### 동적 파라미터화(람다와 메서드 참조)

- 함수형 프로그래밍에서 지원하는 메서드로 코드 블록을 전달하는 기법을 Java8에서도 제공한다.

    ```
    apple -> apple.getWeight() > 150 같은 람다 코드를 전달할 수 있다.
    Apple::isHeavy 같은 기존 메서드의 메서드 참조를 전달할 수 있다.
    ```

- 람다가 전체적인 기능에서 꼭 필요한 개념은 아니지만 Java8의 새로운 Stream API에서는 람다를 효과적으로 사용하면서 Java의 핵심 기능으로 힘을 더해주고 있다.

### Stream

- Java의 컬렉션 클래스, 반복자, for-each 구문은 오랫동안 사용된 기능이다.
- 컬렉션에 어떤 문제가 있으며 Stream과 비슷한 점과 다른 점은 무엇일까?
    - Stream API는 여러 연산을 파이프라인이라는 게으른 형식의 연산으로 구성된다.
        - 한 번의 탐색으로 파이프라인의 모든 연산을 수행한다.
        - 큰 데이터 집합일수록 Stream의 데이터 처리 방식이 효율적이며, 또한 메모리 캐시 등의 관심에서도 커다란 데이터 집합일수록 탐색 횟수를 최소화하는 것이 아주 중요하다.
    - 또한 멀티코어 CPU를 활용해서 병렬로 요소를 처리하는 기능도 매우 중요하다.
        - Stream의 parallel 메서드는 Stream을 병렬로 처리하도록 지정하는 역할을 한다.
    - 상태 변화는 병렬성의 가장 큰 걸림돌이다.
        - 따라서 함수형 개념은 map, filter 등의 연산을 활용하는 Stream의 병렬 처리의 핵심으로 자리 잡았다.

### CompletableFuture 클래스

- Java5부터 Future 인터페이스를 제공한다.
- 다른 작업을 생성한 기존 작업에서 결과가 필요할 때는 get 메서드를 호출해서 생성된 Future가 완료될 때까지 기다릴 수 있다.
- CompletableFuture는 'CompletableFuture와 Future의 관계는 Stream과 컬렉션의 관계와 같다'라는 좌우명을 주장한다.
    - Stream에서는 파이프라인 연산을 구성할 수 있으므로 map, filter 등으로 동적 파라미터화를 제공한다.
        - 따라서 반복자를 사용했을 때 생기는 불필요한 코드를 피할 수 있다.
    - 마찬가지로 CompletableFuture는 Future와 관련한 공통 디자인 패턴을 함수형 프로그래밍으로 간결하게 표현할 수 있도록 thenCompose, thenCombine, allOf 등을 제공한다.
        - 따라서 명령형에서 발생하는 불필요한 코드를 피할 수 있다.

### Optional 클래스

- Java8 라이브러리는 T 형식의 값을 반환하거나 아니면 값이 없음을 의미하는 Optional.empty라는 정적 메서드를 반환할 수 있는 `Optional<T>` 클래스를 제공한다.
    - `Optional<T>` 는 프로그램을 쉽게 이해하고 문서화하는 데 큰 도움을 준다.
- 없는 값의 형식을 다른 값으로 표현하는 기능이 도대체 프로그램 구현에 무슨 도움을 주는 걸까?
    - `Optional<T>` 클래스는 map, filter, ifPresent를 제공한다.
        - Stream 클래스가 제공하는 것과 비슷한 동작으로 계산을 연결할 때 함수형으로 map, filter, ifPresent 등을 사용할 수 있으며, 값이 없는 상황을 사용자 코드에서 확인하는 것이 아니라 라이브러리에서 확인할 수 있다.
            - 값을 내부적으로 검사하는 것과 외부적으로 검사하는 것은 사용자 코드에서 시스템 라이브러리가 내부 반복을 하느냐 아니면 외부 반복을 하느냐와 같은 의미를 가진다.
    - Java9에서는 Optional API에 stream(), or(), ifPresentOrElse() 등의 새로운 메서드를 추가했다.

### Flow API

- Java9에서는 리액티브 스트림과 당김 기반 역압력 프로토콜을 표준화했다.
- Flow API는 호환성을 높일 수 있도록 라이브러리가 구현할 수 있는 네 개의 인터페이스 Publisher, Subscriber, Subscription, Processor를 포함한다.

### 디폴트 메서드

- Java8 이전에는 인터페이스에서 메서드 시그니처만 정의했다.
- 하지만 디폴트 메서드 덕분에 인터페이스 설계자는 메서드의 기본 구현을 제공할 수 있다.
- 특히 인터페이스에 새로운 기능을 추가했을 때 기존의 모든 고객(인터페이스를 구현하는 클래스)이 새로 추가된 기능을 구현하지 않을 수 있게 되었다는 점에서 디폴트 메서드는 라이브러리 설계자에게 아주 훌륭한 도구다.

## Java9 모듈 시스템

- Java8에서는 많은 새 기능과 유용한 새 클래스를 추가했다.
- Java9에서는 새 언어 기능은 추가되지 않았지만 Stream의 takeWhile, dropWhile 그리고 CompletableFuture의 completeOnTimeout 등 Java8에서 시작된 여러 기능을 강화했다.
- Java9의 핵심은 모듈 시스템이다.
- 새 모듈 시스템에서는 [module-info.java](http://module-info.java) 파일이 추가되었지만 언어적으로는 바뀐 것이 없다.
- 하지만 모듈 시스템 덕분에 아키텍처 관점에서 애플리케이션을 설계하고 구현하는 방식이 바뀌었고 하위 부분간의 경계와 상호작용 방법 정의가 명확해졌다.
- 새로운 Java 모듈 시스템 덕분에 Java 런타임이 작은 부분으로 나눠질 수 있게 되었으며 애플리케이션에서 필요한 부분만 사용할 수 있다.
- Java 모듈 시스템이 제공하는 장점이다.
    - 안정적 설정 : 모듈 요구사항을 명시적으로 선언함으로 의존성 빠짐, 충돌, 순환 등의 문제를 런타임이 아니라 빌드 과정에서 일찍 확인할 수 있다.
    - 강한 캡슐화 : Java 모듈 시스템은 특정 패키지만 노출한 다음 각 모듈에서 공개할 부분과 내부 구현의 영역 접근을 분리할 수 있다.
    - 보안성 개선 : 사용자가 모듈의 특정 부분을 사용할 수 없도록 함으로 해커가 보안 제어를 뚫기가 어려워졌다.
    - 성능 개선 : 클래스가 런타임이 로드된 다른 클래스를 참조하는 상황보다는 적은 수의 컴포넌트를 참조할 때 최적화 기술이 더 효과를 발휘한다.
    - 확장성 : Java 모듈 시스템은 Java SE 플랫폼을 작은 부분으로 나눔으로 실행중인 애플리케이션에서 필요한 부분만 사용할 수 있다.

## Java10 지역 변수형 추론

- Java에서 기본적으로 벼수가 메서드를 정의할 때 다음 예제처럼 형식을 지정해야 한다.

    ```java
    double convertUSDToGBP(double money) { 
    	ExchangeRate e = ...; 
    }
    ```

- 시간이 지나면서 이와 같은 엄격한 형식 지정이 조금 느슨해졌다.

    ```java
    Map<String, List<String>> myMap = new HashMap<String, List<String>> ();
    Map<String, List<String>> myMap = new HashMap<> (); // Java7부터 제네릭의 형식 파라미터를 생략할 수 있다.

    Function<Integer, Boolean> p = (Integer x) -> booleanExpression;
    Function<Integer, Boolean> p = x -> booleanExpression; // 콘텍스트로 형식을 유추할 수 있는 다음과 같은 람다 표현식도 줄일 수 있다.
    ```

- 형식이 생략되면 컴파일러가 생략된 형식을 추론(infer)한다.
- 한 개의 식별자로 구성된 형식에 형식 추론을 사용하면 다양한 장점이 생긴다.
    - 우선 한 형식을 다른 형식으로 교체할 때 편집 작업이 줄어든다.
    - 하지만 형식의 크기가 커지면서 제네릭이 다른 제네릭 형식에 의해 파라미터화될 수 있다.
    - 이런 상황에서는 형식 추론으로 가독성이 좋아질 수 있다.
- Scala와 C# 언어는 지역 변수의 형식을 var 키워드로 대체할 수 있다.

```java
var myMap = new HashMap<String, List<String>> ();
```

- 이를 지역 변수형 추론(local variable type inference)이라 부르며 Java10에 추가된 기능이다.
- 하지만 지역 변수형 추론 과정에서 몇 가지 문제가 발생할 수 있다.

    ```java
    // Vehicle을 상속하는 Car가 있을 때
    var x = new Car();
    ```

    - x의 형식은 Car일까 아니면 Vehicle일까?
    - 초기화 코드가 없을 때는 var를 사용할 수 없다는 제약이 있으므로 큰 문제는 없다.
    - Java10에서는 이를 공식적으로 지원하며 초깃값이 없을 때는 var을 사용할 수 없음도 설명하고 있다.

## Java의 미래

- JDK 개선 제안(JDK Enhancement Proposal) 웹 사이트에서는 좋은 제안임에도 불구하고 기존 기능과의 상호 작용이나 기타 문제 때문에 Java에 채택되지 못한 이유를 설명한다.
    - [http://openjdk.java.net/jeps/0](http://openjdk.java.net/jeps/0)

### 선언 사이트 변종

- Java에서는 제네릭의 서브형식을 와일드카드로 지정할 수 있는 유연성(보통 이를 사용 사이트 변종(use-site variance)이라 함)을 허용함

    ```java
    List<? extends Number> numbers = new ArrayList<Integer> ();
    List<Number> numbers = new ArrayList<Integer> (); // 컴파일 에러(호환되지 않는 형식)
    ```

- C#, Scala 같은 많은 프로그래밍 언어는 선언 사이트 변종이라는 다른 변종 기법을 지원한다.
    - 선언 사이트 변종을 이용하면 제네릭 클래스를 정의할 때 프로그래머가 변종을 지정할 수 있다.
        - 선언 사이트 변종에서는 ? extends나 ? super를 사용할 필요가 없다.
    - Kotlin에서의 declaration-site variance
        - [https://kotlinlang.org/docs/reference/generics.html#declaration-site-variance](https://kotlinlang.org/docs/reference/generics.html#declaration-site-variance)
- [http://openjdk.java.net/jeps/300](http://openjdk.java.net/jeps/300)

### 패턴 매칭

- 보통 함수형 언어는 switch를 개선한 기능인 패턴 매칭을 제공한다.

```java
if (op instanceof BinOp) {
	Expr e = ((BinOp) op).getLeft();
}
```

- 위 코드에서 op의 형식이 명확한 상황인데도 형변환 코드에서 BinOp형을 반복 사용했다.
- 전통적인 객체지향 디자인에서는 switch 대신 방문자 패턴(visitor pattern) 등을 사용할 것을 권장한다는 사실을 기억하자.
    - 방문자 패턴에서는 데이터 형식에 종속된 제어 흐름이 switch가 아닌 메서드에 의해 결정된다.
        - [https://ko.wikipedia.org/wiki/비지터_패턴](https://ko.wikipedia.org/wiki/%EB%B9%84%EC%A7%80%ED%84%B0_%ED%8C%A8%ED%84%B4)
    - 함수형 프로그래밍에서는 데이터 형식의 값에 패턴 매칭을 적용하는 것이 프로그램을 가장 쉽게 설계하는 지름길이다.
- Scala 형식의 패턴 매칭을 완벽하게 Java로 적용하는 것은 쉽지 않은 일이지만 문자열을 허용하는 switch문이 등장한 것처럼 조만간 더 유연한, 즉 instanceOf 문법을 사용해서 객체를 사용하는 switch문이 등장할 날도 머지않았음을 상상할 수 있다.
    - [http://openjdk.java.net/jeps/305](http://openjdk.java.net/jeps/305)

```java
switch (someExpr) {
	case (op instanceOf BinOp):
		doSomething(op.getOpName(), op.getLeft(), op.getRight());
	case (n instanceOf Number):
		dealWithLeafNode(n.getValue());
	default:
		defaultAction(someExpr);
}
```

- 위 예제는 패턴 매칭에서 몇 가지 아이디어를 가져왔다.
    - 즉, case (op instanceOf BinOp):에서 op는 (BinOp 형식의) 새로운 지역 변수로 someExpr과 같은 값으로 바운드된다.
    - 마찬가지로 n은 Number 형식의 변수가 된다.
    - default 케이스에서는 아무 변수도 바운드되지 않는다.
- 이 제안을 적용한다면 if-then-else 그리고 서브형식 캐스팅에 사용되는 여러 불필요한 코드를 제거할 수 있다.
- 전통적인 객체지향 설계자라면 이 예제처럼 데이터 형식에 따라 코드 실행을 결정할 때 차라리 서브 형식으로 오버라이드된 방문자 형식의 메서드를 구현하는 것이 더 좋다고 생각할 수 있다.
    - 하지만 함수형 프로그래밍 관점에서는 관련 코드가 여러 클래스 정의로 흩어지는 결과를 초래할 뿐이다.
    - 이 문제는 '표현 문제'라는 양립적으로 논쟁 중인 오래된 주제다.
        - [https://en.wikipedia.org/wiki/Expression_problem](https://en.wikipedia.org/wiki/Expression_problem)

### 풍부한 형식의 제네릭

- Java 제네릭의 두 가지 한계를 살펴보고 이를 해결할 수 있는 방법을 설명한다.
- Java5에서 제네릭을 소개했을 때 제네릭이 기존 JVM과 호환성을 유지해야 했다.
    - 결과적으로 `ArrayList<String>`이나 `ArrayList<Integer>` 모두 런타임 표현이 같게 되었다.
    - 이를 제네릭 다형성의 삭제 모델(generic polymorphism erasure model)이라고 한다.
    - 이 때문에 약간의 런타임 비용을 지불하게 되었으며 제네릭 형식의 파라미터로 객체만 사용할 수 있게 되었다.
    - 만일 `ArrayList<int>`에서는 기본형 42를 얻을 수 있고, `ArrayList<String>`에서는 'abc'라는 문자열 객체를 얻을 수 있다면 왜 ArrayList 컨테이너를 구별할 수 있는지 여부를 걱정해야 할까?
        - 불행히도 가비지 컬렉션(GC) 때문이다.
        - 런타임에 ArrayList의 콘텐츠 형식 정보를 확인할 수 없으므로 ArrayList의 13이라는 요소가 Integer 참조인지(GC가 '사용 중(in-use)'으로 표시) 아니면 int 기본값인지(GC 수행 불가) 분간할 수 없다.
    - C#에서는 `ArrayList<String>`, `ArrayList<Integer>`, `ArrayList<int>`가 각각 다른 의미를 갖는다.
        - Java처럼 위와 같은 선언이 모두 같은 상황이라면 가비지 컬렉션이 참조형인지 기본형인지 알 수 있도록 충분한 형식 정보를 런타임에 유지해야 한다.
        - 이를 제네릭 다형성 구체화 모델(reified model of generic polymorphism) 또는 줄여서 구체화된 제네릭(reified generic)이라고 부른다.
            - 구체화란 '암묵적인(implicit) 어떤 것을 명시적(explicit)으로 바꾼다'라는 의미다.
- 제네릭이 함수 형식에 제공하는 문법적 유연성
    - 제네릭은 Java8의 다양한 람다 형식과 메서드 참조를 표현하는 데도 도움이 된다.

    ```java
    Function<Integer, Integer> square = x -> x * x;
    ```

    - 두 개의 인수를 받는 함수의 형식은 `BiFunction<T, U, R>`로 사용할 수 있다.
    - 하지만 TriFunction은 직접 선언해야 한다.
    - 기본적으로 Java8 람다는 코드 구현을 풍부하게 할 수 있도록 해주었지만 형식 시스템은 코드 유연성을 따라잡지 못하고 있다.
        - ex) 함수형에서는 `(Integer, Double) ⇒ String`, Java8에서는 `BiFunction<Integer, Double, String>`에 해당한다.
            - `Integer => String`은 `Function<Integer, String>`을 의미하며, 심지어 `() => String`은 `Supplier<String>`을 의미한다.
            - 여기서 `⇒`는 `Function, BiFunction, Supplier`의 인픽스 버전으로 이해할 수 있다.
        - Java 문법에 이와 같은 형식이 추가된다면 Scala와 비슷한 수준의 이해하기 쉬운 형식 시스템을 구성할 수 있다.
- 기본형 특화와 제네릭
    - 예를 들어 Java8에서 왜 `Function<Apple, Boolean>`을 `Predicate<Apple>`로 구현해야 할까?
        - Function은 Boolean을 반환하지만 Predicate는 내부 test 메서드가 boolean을 반환한다.
            - 박싱할 필요가 없다.
        - 이런 이유로 Java 언어를 개념적으로 복잡하게 만드는 LongToIntFunction과 BooleanSupplier 같은 인터페이스가 등장했다.
    - 비슷한 문제로 메서드 반환 형식이 아무 것도 없는 것임을 가리키는 void와 값으로 null을 갖는 객체형 Void가 있다.

### 더 근본적인 불변성 지원

- 문자열이나 불변 배열은 명확하게 값으로 인정할 수 있지만 가변 객체나 배열은 이를 수학적인 값으로 인정할 수 있을지 여부가 불명확하다.
- Java에서 함수형 프로그래밍을 구현하려면 '불변값'을 언어적으로 지원해야 한다.
- final로는 필드값 갱신만 막을 수 있으므로 final만으로 불변값이라는 목표를 달성하기 어렵다.
- 기본형에는 final 키워드로 값이 바뀌는 것을 막을 수 있지만 객체 참조에서는 큰 효과가 없다.
- 함수형 프로그래밍에서는 기본 구조체를 변화시키지 않는 것이 중요하므로 필드값이든 객체 참조든 직접 또는 간접적으로 모든 값의 변경을 방지하는 transitively_final 같은 만능 키워드가 있으면 좋을 것 같다.

### 값 형식

- 컴파일러가 Integer와 int를 같은 값으로 취급할 수는 없을까?
    - Java에 Complex라는 형식을 추가할 때 박싱과 관련된 어떤 문제가 발생하는지 살펴보자

    ```java
    class Complex {
    	public final double re;
    	public final double im;
    	public Complex(double re, double im) {
    		this.re = re;
    		this.im = im;
    	}
    	public static Complex add(Complex a, Complex b) {
    		return new Complex(a.re + b.re, a.im + b.im);
    	}
    }
    ```

    - Complex는 참조 형식이므로 Complex에 어떤 동작을 수행하려면 객체를 생성해야 한다.
    - 이제 Complex에 대응하는 complex라는 기본형이 필요하다.
    - Complex를 언박싱한 객체가 필요한데 Java와 JVM은 이런 기능을 지원하지 않는다.
        - 탈출 기법(escape analysis), 즉 언박싱 동작이 괜찮은지 결정하는 컴파일러 최적화 기법이 있지만 이는 Java1.1 이후에 제공되는 객체에만 적용된다.
    - 기본형에서는 비트를 비교해서 같음을 판단하지만 객체에서는  참조로 같음을 판단한다.
    - 따라서 기본형으로 해결할 수 있는 상황인데도 어쩔 수 없이 컴파일러 요구사상에 맞춰 Double 같은 객체를 생성하는 상황이 발생한다.
- 변수형 : 모든 것을 기본형이나 객체형으로 양분하지 않는다.
    - Java의 가정을 조금 바꾸면 이 문제를 해결할 수 있을 것이다.
        - 기본형이 아닌 모든 것은 객체형이므로 Object를 상속받는다.
        - 모든 참조는 객체 참조다.
    - 값에는 두 가지 형식이 존재한다.
        - 객체형은 (final로 정의되지 않았을 때) 변화할 수 있는 필드를 포함하며 ==로 값이 같음을 검사할 수 있다.
        - 값 형식은 불변이며 참조 식별자를 포함하지 않는다. 기본형값은 넓은 의미에서 값 형식의 일종이다.
    - 값 형식에는 참조 식별자가 없으므로 저장 공간을 적게 차지한다.
    - 또한 값 형식은 데이터 접근뿐만 아니라 하드웨어 캐시 활용에도 좋은 성능을 제공할 가능성이 크다.

    ![chapter21-01](image/chapter21-01.png '객체와 값 형식')

    - 값 형식에는 참조 식별자가 없으므로 컴파일러가 자유롭게 값 형식을 박싱하거나 언박싱할 수 있다.
    - 마이크로소프트는 다음과 같이 말한다.

        > 값 형식에 기반한 변수는 직접 값을 포함한다. 한 값 형식 변수의 값을 다른 값 형식 변수에 할당하면 포함된 값이 복사된다. 참조 형식 변수의 할당에서는 객체 자체가 아니라 객체의 참조가 복사된다는 점이 다르다.

    - [http://openjdk.java.net/jeps/169](http://openjdk.java.net/jeps/169)
- 박싱, 제네릭, 값 형식 : 상호 의존 문제
    - 함수형 프로그래밍에서는 식별자가 없는 불변값을 이용하므로 Java에서 값 형식을 지원한다면 좋을 것 같다.
    - Java에 값 형식이 지원된다면 기본형도 일종의 값 형식이 될 것이며 현재의 제네릭이 사라질 것이다.

## 더 빠르게 발전하는 Java

- 작은 개선 사항은 특별한 이유없이 다음 릴리스가 이루어질 때까지 기다려야 했다.
    - Java9의 컬렉션 팩토리 메서드는 모듈 시스템이 완성되기 전에 이미 출시 준비를 마쳤다.
- 이런 이유로 이제부터 Java는 6개월 개발 주기를 갖기로 결정했다.
- Java 아키텍트는 이런 빠른 개발 주기가 언어 자체에도 이로울 뿐 아니라 새로운 기술을 끊임없이 시험하는 빠르게 변화하는 회사와 개발자에게도 도움이 된다는 사실을 깨달았다.
    - 반면 느린 속도로 소프트웨어를 갱신하는 보수적인 회사라면 문제가 될 수 있다.
    - 이런 이유로 Java 아키텍트는 매 3년마다 이후 3년 동안 지원을 보장하는 장기 지원(long-term support, LTS) 릴리스도 결정했다.