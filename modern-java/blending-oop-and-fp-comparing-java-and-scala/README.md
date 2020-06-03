# OOP와 FP의 조화 : 자바와 스칼라 비교

- Scala는 객체지향과 함수형 프로그램을 혼합한 언어다.
    - [https://ko.wikipedia.org/wiki/스칼라_(프로그래밍_언어)](https://ko.wikipedia.org/wiki/%EC%8A%A4%EC%B9%BC%EB%9D%BC_(%ED%94%84%EB%A1%9C%EA%B7%B8%EB%9E%98%EB%B0%8D_%EC%96%B8%EC%96%B4))
- Scala는 Java에 비해 많은 기능을 제공한다.
    - Scala는 복잡한 형식 시스템, 형식 추론, 패턴 매칭, 도메인 전용 언어를 단순하게 정의할 수 있는 구조 등을 제공
    - Scala 코드에서는 모든 Java 라이브러리를 사용할 수 있다.
- Scala는 Java에 비해 더 다양하고 심화된 함수형 기능을 제공한다.
- Scala를 이용하면 Java에 비해 더 간결하고 가독성이 좋은 코드를 구현할 수 있다는 사실을 발견할 수 있다.

## Scala 소개

```
Hello 2 bottles of beer
Hello 3 bottles of beer
Hello 4 bottles of beer
Hello 5 bottles of beer
Hello 6 bottles of beer
```

- 위의 결과를 출력하게  Java 코드와 Scala 코드로 작성해보자.

    ```java
    public class Beer {
    	public static void main(String[] args) {
    		// 명령형
    		int n = 2;
    		while (n <= 6) {
    			System.out.println("Hello " + n + " bottles of beer");
    			n += 1;
    		}

    		// 함수형
    		IntStream.rangedClosed(2, 6)
    				.forEach(n -> System.out.println("Hello " + n + " bottles of beer"));
    	}
    }
    ```

    ```scala
    object Beer {
    	def main(args:  Array[String]) {
    		//  명령형
    		var n: Int = 2
    		while (n <= 6) {
    			println(s"Hello ${n} bottles of beer") // 문자형 보간법
    			n += 1
    		}

    		// 함수형
    		2 to 6 foreach { n => println(s"Hello ${n} bottles of beer") }
    	}
    }
    ```

- 코드의 차이점
    - 명령형 Java와 명령형 Scala의 차이
        - main 메서드는 반환값이 없는데 Java에서는 void를 사용하지만 Scala에서는 아무것도 선언하지 않는다.
            - 보통 Scala의 비재귀 메서드에서는 반환형식을 추론할 수 있으므로 명시적으로 반환형식을 정의하지 않아도 된다.
        - Scala에서는 object로 직접 싱글턴 객체를 만들 수 있다.
            - object로 Beer 클래스를 정의하고 동시에 인스턴스화했다.
            - object 내부에 선언된 메서드는 정적 메서드로 간주할 수 있다.
                - main 메서드의 시그니처에 명시적으로 static이 없는 것도 이것 때문이다.
        - main의 바디에 구문이 세미콜론으로 끝나지 않는다.
            - 세미콜론은 선택사항
        - println 메서드는 Scala의 문자형 보간법(string interpolation)이라는 기능을 보여준다.
            - 문자열 보간법은 문자열 자체에 변수와 표현식을 바로 삽입하는 기능이다.
    - 함수형 Java와 함수형 Scala의 차이
        - Scala의 모든 것은 객체다.
            - Java와 달리 Scala는 기본형이 없다.
            - Scala는 Java보다 완전한 객체지향 언어다.
        - Scala의 Int 객체는 다른 Int를 인수로 받아 범위를 반환하는 to라는 메서드를 지원한다.

            ```scala
            2.to(6) // Java와 유사한 메서드 호출 형식
            2 to 6 // 인픽스 형식
            ```

            - 인수 하나를 받는 메서드는 인픽스(infix) 형식으로 구현할 수 있다.
        - foreach는 범위에 사용할 수 있는 메서드로 람다 표현식을 인수로 받아서 각 요소에 적용한다.
            - Java에서는 forEach 메서드이지만 Scala는 foreach 메서드다.
        - 람다 표현식의 문법은 Java와 비슷하다.
            - 다만 → 대신 ⇒를 사용한다.
- Scala의 실행 방법과 언어 사용법은 아래 링크를 참조
    - [https://docs.scala-lang.org/getting-started/index.html](https://docs.scala-lang.org/getting-started/index.html)
    - [https://docs.scala-lang.org/ko/tutorials/scala-for-java-programmers.html](https://docs.scala-lang.org/ko/tutorials/scala-for-java-programmers.html)
- 기본 자료구조 : 리스트, 집합, 맵, 튜플, 스트림, 옵션
    - 컬렉션 만들기

        ```scala
        val authorsToAge = Map("Raoul" -> 23, "Mario" -> 40, "Alan" -> 53)
        ```

        - 첫 번째로 → 라는 문법으로 키를 값에 대응시켜 맵을 만들수 있다.
        - 두 번째로 변수 authorsToAge의 형식을 지정하지 않았다.
            - Scala는 자동으로 변수형을 추론하는 기능이 있다.
            - Scala는 코드를 정적으로 확인한다. 즉, 모든 변수의 형식은 컴파일을 할 때 결정된다.
        - 세 번째로 var 대신 val이라는 키워드를 사용했다.
            - val은 변수가 읽기 전용, 즉 변수에 값을 할당할 수 없음을 의미한다. (Java의 final과 같다.)
            - var라는 키워드는 읽고 쓸 수 있는 변수를 가리킨다.

        ```java
        Map<String, Integer> authorsToAge = 
        	Map.ofEntries(entry("Raoul", 23), entry("Mario", 40), entry("Alan", 53));
        ```

        - Java9에서는 Scala에서 영감을 받은 여러 팩토리 메서드를 제공해서 위의 문법을 제공

        ```scala
        val authors = List("Raoul", "Mario", "Alan")
        val numbers = Set(1, 1, 2, 3, 5, 8)
        ```

    - 불변과 가변
        - 지금까지 만든 컬렉션은 기본적으로 불변(immutable)이다.
        - 기존 버전과 가능한 한 많은 자료를 공유하는 새로운 컬렉션을 만드는 방법으로 자료 구조를 갱신한다.
        - 결과적으로 암묵적인 데이터 의존성을 줄일 수 있다.
        - 즉, 언제, 어디서 컬렉션(또는 다른 공유된 자료구조 등)을 갱신했는지 크게 신경 쓰지 않아도 된다.

        ```scala
        val numbers = Set(2, 5, 3)
        val newNumbers = numbers + 8 // 여기서 +는 집합에 8을 더하는 메서드의 연산 결과로 새로운 Set 객체를 생성한다.
        println(newNumbers) // result => Set(2, 5, 3, 8)
        println(numbers) // result => Set(2, 5, 3)
        ```

        - 패키지 scala.collection.mutable에서는 가변 버전의 컬렉션을 제공한다.
    - 컬렉션 사용하기
        - Scala의 컬렉션 동작은 Java의 Stream API와  비슷하다.

        ```scala
        val fileLines = Source.fromFile("data.txt").getLines.toList()
        val linesLongUpper = fileLines.filter(l => l.length() > 10)
        		.map(l => l.toUpperCase())
        ```

        - 인픽스 개념과 언더스코어(_)를 사용해 다음과 같이 표현할 수 있다.
            - 언더스코어(_)는 인수로 대치된다.

        ```scala
        val linesLongUpper = fileLines filter (_.length() > 10) map (_.toUpperCase())
        ```

        - Scala의 API는 Stream API에 비해 풍부한 기능을 제공한다.
            - 예를 들면 두 리스트의 요청을 합치는 지핑(zipping)이라는 연산을 제공한다.
        - Java에서는 Stream에 parallel을 호출해서 파이프라인을 병렬로 실행할 수 있었다.
            - Scala도 par라는 메서드로 비슷한 기능을 제공한다.

            ```scala
            val lineLongUpper = fileLines.par filter (_.length() > 10) map (_.toUpperCase())
            ```

    - 튜플
        - Java는 튜플을 지원하지 않는다.

        ```java
        public class Pair<X, Y> {
        	public final X x;
        	public final Y y;
        	public Pair(X x, Y y) {
        		this.x = x;
        		this.y = y;
        	}
        }

        Pair<String, String> raoul = new Pair<> ("Raoul", "+ 44 007007007");
        Pair<String, String> alan = new Pair<> ("Alan", "+44 003133700");
        ```

        - Java로 튜플을 구현한다는 것은 쉽지 않은 작업이며 프로그램의 가독성과 유지보수성을 떨어뜨린다.
        - Scala는 튜플 축약어, 즉 간단한 문법으로 튜플을 만들 수 있는 기능을 제공한다.

            ```scala
            val raoul = ("Raoul", "+ 44 007007007")
            val numbers = ("Alan", "+44 003133700")
            ```

        - Scala는 임의 크기의 튜플을 제공한다.

            ```scala
            val book = (2018, "Modern Java In Action", "Manning") // (Int, String, String) 형식의 튜플
            val numbers = (42, 1337, 0, 3, 14) // (Int, Int, Int, Int, Int) 형식의 튜플

            println(book._1) // 2018 출력
            println(numbers._4) // 3 출력
            ```

            - 최대 23개 요소를  그룹화하는 튜플을 만들 수 있다.
            - _1, _2 등의 접근자로 튜플의 요소에 접근할 수 있다.
    - 스트림
        - Scala에서도 Stream이라는 게으르게 평가되는 자료구조를 제공한다.
        - Scala의 Stream은 Java의 Stream보다 다양한 기능을 제공한다.
        - Scala의 Stream은 이전 요소가 접근할 수 있도록 기존 계산값을 기억한다.
        - 또한 인덱스를 제공하므로 리스트처럼 인덱스로 Stream의 요소에 접근할 수 있다.
        - 이러한 기능이 추가되면서 Scala의 Stream은 Java의 Stream에 비해 메모리 효율성이 조금 떨어진다.
            - 이전 요소를 참조하려면 요소를 '기억(캐시)'해야 하기 때문이다.
    - 옵션
        - 옵션이라는 자료구조도 있다.
        - Java의 Optional과 같은 기능을 제공한다.

        ```java
        public String getCarInsuranceName(Optional<Person> person, int minAge) {
        	return person.filter(p -> p.getAge() >= minAge)
        		.flatMap(Person::getCar)
        		.flatMap(Car::getInsurance)
        		.map(Insurance::getName)
        		.orElse("Unknown");
        }
        ```

        ```scala
        def getCarInsuranceName(person: Option[Person], minAge: Int) =
        	person.filter(_.getAge() >= minAge)
        		.flatMap(_.getCar)
        		.flatMap(_.getInsurance)
        		.map(_.getName)
        		.getOrElse("Unknown")
        ```

        - Java에서는 orElse라는 메서드를 사용했고, Scala에서는 getOrElse라는 메서드를 사용했다는 점을 제외하면 두 코드는 구조가 같다는 것을 확인할 수 있다.
        - 안타깝게도 Java와의 호환성 때문에 Scala에도 null이 존재한다.
        - 하지만 되도록 null은 사용하지 않는 것이 좋다.
        - _.getCar() 대신 _.getCar를 사용했는데 Scala에서는 인수가 없는 메서드를 호출할 때 괄호를 생략할 수 있다.

## 함수

- Scala의 함수는 어떤 작업을 수행하는 일련의 명령어 그룹이다.
- Java에서는 클래스와 관련된 함수에 메서드라는 이름이 사용된다.
- Scala에서는 Java에 비해 풍부한 함수 기능을 제공한다.
    - 함수 형식 : 함수 형식은 Java 함수 디스크립터의 개념을 표현하는 편의 문법(즉, 함수형 인터페이스에 선언된 추상 메서드의 시그니처를 표현하는 개념)이다.
    - 익명 함수 : 익명 함수는 Java의 람다 표현식과 달리 비지역 변수 기록에 제한을 받지 않는다.
    - 커링 지원 : 커링은 여러 인수를 받는 함수를 일부 인수를 받는 여러 함수로 분리하는 기법이다.
- Scala의 일급 함수
    - Scala의 함수는 일급값(first-class value)이다.

    ```scala
    def isJavaMentioned(tweet: String): Boolean = tweet.contains("Java") // Predicate
    def isShortTweet(tweet: String): Boolean = tweet.length() < 20 // Predicate
    ```

    - Scala에서 기본 제공하는 filter로 위의 메서드들을 바로 전달할 수 있다.

    ```scala
    val tweets = List(
    	"I love the new features in Java 8",
    	"How's it going",
    	"An SQL query walks into a bar, sees two tables and say 'Can I join you?'"
    )
    tweets.filter(isJavaMentioned).foreach(println)
    tweets.filter(isShortTweet).foreach(println)
    ```

    - Scala에서 제공하는 내장 메서드 filter의 시그니처를 확인하자.

        ```scala
        def filter[T](p: (T) => Boolean): List[T]
        ```

        - 위 코드의 파라미터 p의 형식은 ``(T) ⇒ Boolean``이다.
        - Java로는 ``Predicate<T>`` 또는 ``Funtion<T, Boolean>``과 같은 의미다.
        - Java 언어 설계자는 기존 버전의 언어와 일관성을 유지할 수 있도록 이와 같은 함수 형식을 지원하지 않기로 결정했다.
- 익명 함수와 클로저
    - Scala도 익명 함수(anonymous function)의 개념을 지원한다.

    ```scala
    // 익명 함수
    val isLongTweet: String => Boolean = (tweet: String) => tweet.length() > 60

    // 함수 원형
    val isLongTweet: String => Boolean =
    	new Function1[String, Boolean] {
    		def apply(tweet: String): Boolean = tweet.length() > 60
    	}

    // 다음처럼 메서드 호출 가능
    isLongTweet.apply("A very short tweet") // <- false를 반환
    ```

    - 위의 익명 함수는 apply 메서드의 구현을 제공하는 scala.Function1(한 개의 인수를 받는 함수) 형식의 익명 클래스를 축약한 것이다.
    - Java로는 다음처럼 구현할 수 있다.

        ```java
        Function<String, Boolean> isLongTweet = (String s) -> s.length() > 60;
        boolean a = isLongTweet.apply("A very short tweet");
        ```

    - Java에서는 람다 표현식을 사용할 수 있도록 Predicate, Function, Consumer 등의 내장 함수형 인터페이스를 제공했다.
    - 마찬가지로 Scala는 트레이트를 지원한다.
        - 일단은 트레이트(trait)가 인터페이스와 같다고 생각하자.
    - Scala에서는 Function0(인수가 없으며 결과를 반환)에서 Function22(22개의 인수를 받음)를 제공한다.
        - 모두 apply 메서드를 정의한다.

    ```scala
    isLongTweet("A very short tweet") // <- false를 반환
    ```

    - Scala 컴파일러는 f(a)라는 호출을 자동으로 f.apply(a)로 변환한다.
        - 즉, 일반적으로 컴파일러는 f(a1, ..., an)을 f.apply(a1, ..., an)으로 변환할 수 있으며 여기서 f는 apply 메서드를 지원하는 객체다.
        - apply의 인수 개수는 제한이 없다.
- 클로저
    - 클로저(closure)란 함수의 비지역 변수를 자유롭게 참조할 수 있는 함수의 인스턴스를 가리킨다.
        - [https://ko.wikipedia.org/wiki/클로저_(컴퓨터_프로그래밍)](https://ko.wikipedia.org/wiki/%ED%81%B4%EB%A1%9C%EC%A0%80_(%EC%BB%B4%ED%93%A8%ED%84%B0_%ED%94%84%EB%A1%9C%EA%B7%B8%EB%9E%98%EB%B0%8D))
    - 하지만 Java의 람다 표현식에는 람다가 정의된 메서드의 지역 변수를 고칠수 없다는 제약이 있다.
    - 이들 변수는 암시적으로 final로 취급된다.
    - 즉, 람다는 변수가 아닌 값을 닫는다는 사실을 기억하자.
    - Scala의 익명 함수는 값이 아니라 변수를 캡쳐할 수 있다.

    ```scala
    def main(args: Array[String]) {
    	var count = 0
    	val inc = () => count += 1 // count를 캡쳐하고 증가시키는 클로저
    	println(count) // 1이 출력
    	inc()
    	println(count) // 2가 출력
    }
    ```

    - Java에서는 컴파일 에러가 발생한다.

    ```java
    public static void main(String[] args) {
    	int count = 0;
    	Runnable inc = () -> count += 1; // 에러: count는 명시적으로 final 또는 final에 준하는 변수여야 함
    	inc.run();
    	System.out.println(count);
    	inc.run();
    }
    ```

    - 프로그램을 쉽게 유지보수하고 병렬화할 수 있도록 변화를 피하라고 조언했다.
    - 따라서 꼭 필요할 때만 클로저 기능을 사용하는 것이 바람직하다.
- 커링
    - x, y라는 두 인수를 가진 f라는 함수가 있을 때, 이는 하나의 인수를 받는 g라는 함수 그리고 g라는 함수는 다시 나머지 인수를 받는 함수로 반환되는 상황으로 볼 수 있다.

    ```java
    // 기본 메서드
    static int multiply(int x, int y) {
    	return x * y;
    }
    int r = multiply(2, 10);

    // 커링이 적용된 메서드
    static Function<Integer, Integer> multiplyCurry(int x) {
    	return (Integer y) -> x * y;
    }
    Stream.of(1, 3, 5, 7)
    	.map(multiplyCurry(2))
    	.forEach(System.out::println);
    ```

    - Java에서 함수를  커리 형식으로 분할하려면 조금 복잡한 과정을 거쳐야 한다.
        - 특히 함수의 인수가 많을수록 복잡해진다.
    - Scala는 이 과정을 자동으로 처리하는 특수 문법을 제공한다.

    ```scala
    // 일반 함수
    def multiply(x: Int, y: Int) = x * y
    val r = multiply(2, 10)

    // 커링이 적용된 함수
    def multiplyCurry(x: Int)(y: Int) = x * y
    val r = multiplyCurry(2)(10)
    ```

    - multiplyCurry(2)는 모든 인수를 사용하지 않았으므로 이 상황을 함수가 부분 적용되었다고 표현한다.
    - 처음 호출한 결과를 내부 변수에 저장했다가 재사용한다.

    ```scala
    val multiplyByTwo: Int => multiplyCurry(2)
    val r = multiplyByTwo(10) // <- 20
    ```

## 클래스와 트레이트

- Scala의 클래스와 인터페이스는 Java에 비해 더 유연함을 제공한다.
- 간결성을 제공하는 Scala의 클래스
    - Getter와 Setter
        - Scala에서는 생성자, Getter, Setter가 암시적으로 생성되므로 코드가 훨씬 단순해진다.

        ```scala
        class Student(var name: String, var id: Int)
        val s = new Student("Raoul", 1)
        println(s.name) // Raoul 출력
        s.id = 1337
        println(s.id) // 1337 출력
        ```

- Scala 트레이트와 Java 인터페이스
    - Scala의 트레이트는 Java의 인터페이스를 대체한다.
    - 트레이트로 추상 메서드와 기본 구현을 가진 메서드 두 가지를 모두 정의할 수 있다.
    - Java의 인터페이스처럼 트레이트는 다중 상속을 지원하므로 Java의 인터페이스와 디폴트 메서드 기능이 합쳐진 것으로 이해할 수 있다.
    - 하지만 Java8에서는 디폴트 메서드 덕분에 동작을 다중 상속할 수 있게 되었지만 Scala의 트레이트와는 달리 상태는 다중 상속할 수 없다.

    ```scala
    trait Sized {
    	var size: Int = 0
    	def isEmpty() = size == 0
    }

    class Empty extends Sized
    println(new Empty().isEmpty()) // true 출력
    ```

    - 흥미롭게도 Java 인터페이스와는 달리 객체 트레이트는 인스턴스화 과정에서도 조합할 수 있다.
        - 하지만 조합 결과는 컴파일할 때 결정된다.

    ```scala
    class Box
    val b1 = new Box() with Sized
    println(b1.isEmpty()) // true ㅜㄹ력
    val b2 = new Box()
    b2.isEmpty() // 컴파일 에러: Box 클래스 선언이 Sized를 상속하지 않았음
    ```

    - 같은 시그니처를 갖는 메서드나 같은 이름을 갖는 필드를 정의하는 트레이트를 다중 상속하면 어떻게 될까?
        - Scala에서는 Java에서 디폴트 메서드가 이 문제를 해결한 방법과 비슷한 제한을 둔다.