# 람다와 JVM 바이트코드

## 익명 클래스

- 익명 클래스는 애플리케이션 성능에 악영향을 주는 특성을 포함한다.
    - 컴파일러는 익명 클래스에 대응하는 새로운 클래스 파일을 생성한다.
        - 보통 익명 클래스는 ClassName$1 등의 파일명을 갖는데 여기서 ClassName은 익명 클래스를 포함하는 클래스의 이름이다.
        - 클래스 파일을 사용하려면 먼저 각각의 클래스를 로드하고  검증하는 과정이 필요하므로 애플리케이션 스타트업의 성능에 악영향을 미친다.
        - 람다를 익명 클래스로 변환한다면 람다 표현식에 대응하는 새로운 클래스 파일이 생성된다.
    - 새로운 익명 클래스는 클래스나 인터페이스의 새로운 서브형식을 만든다.
        - ex) Comparator를 표현하는 수백 개의 람다가 있다면 결국 수백 가지의 Comparator 서브형식이 생긴다는 의미다.
        - 이와 같은 상황에서는 JVM이 런타임 성능을 개선하기 어려울 수 있다.

## 바이트코드 생성

- Java 컴파일러는 Java 소스 파일을 Java 바이트코드로 컴파일한다.
- JVM은 생성된 바이트코드를 실행하면서 애플리케이션을 동작시킨다.
- 익명 클래스와 람다 표현식은 각기 다른 바이트코드 명령어로 컴파일된다.

```
# 다음 명령어로 클래스 파일의 바이트코드와 상수 풀을 확인할 수 있다.
javap -c -v ClassName
```

```java
import java.util.function.Function;

public class InnerClass {
	Function<Object, String> f = new Function<> () {
			@Override
			public String apply(Object obj) {
				return obj.toString();
			}
	};
}
```

```
 0: aload_0
 1: invokespecial #1 // Method java/lang/Object."<init>":()V
 4: aload_0
 5: new           #2 // class InnerClass$1
 8: dup
 9: aload_0
10: invokespecial #3 // Method InnerClass$1."<init>":(LInnerClass;)V
13: putfield      #4 // Field f:Ljava/util/function/Function;
16: return
```

- Java7 문법으로 Function 인터페이스의 인스턴스를 익명 내부 클래스로 구현한 코드다.
    - new라는 바이트코드 연산으로 InnerClass$1이라는 객체 형식을 인스턴스화했다. 동시에 새로 생성한 객체 참조를 스택으로 푸시했다.
    - dup 연산은 스택에 있는 참조를 복제한다.
    - 객체를 초기화하는 invokespecial 명령어로 값을 소비한다.
    - 스택의 top에는 여전히 객체 참조가 있으며 putfield 명령어로 객체 참조를 LambdaBytecode의 f1 필드에 저장한다.
    - InnerClass$1은 컴파일러가 익명 클래스에 붙인 이름이다.
- 다음은 Function 인터페이스 구현 코드다.

```
class InnerClass$1 implements java.util.function.Function<java.lang.Object, java.lang.String> {
	final InnerClass this$0;
	public java.lang.String apply(java.lang.Object);
		Code:
			0: aload_1
			1: invokevirtual #3 // Method java/lang/Object.toString:()Ljava/lang/String;
			4: areturn
}
```

## 구원투수 InvokeDynamic

- Java8 문법인 람다 표현식을 사용한 상황을 살펴보자.

```java
import java.util.function.Function;

public class Lambda {
	Function<Object, String> f = obj -> obj.toString();
}
```

```
 0: aload_0
 1: invokespecial #1 // Method java/lang/Object."<init>":()V
 4: aload_0
 5: invokedynamic #2, 0 // InvokeDynamic #0:apply:()Ljava/util/function/Function;
10: putfield      #3 // Field f:Ljava/util/function/Function;
13: return
```

- 클래스를 생성하던 부분이 invokedynamic이라는 명령어로 대치되었음을 확인할 수 있다.
- invokedynamic 명령어
    - JVM의 동적 형식 언어를 지원할 수 있도록 JDK7에 invokedynamic 명령어가 추가되었다.
    - invokedynamic은 메서드를 호출할 때 더 깊은 수준의 재전송과 동적 언어에 의존하는 로직이 대상 호출을 결정할 수 있는 기능을 제공한다.

    ```scala
    def add(a, b) { a + b }
    ```

    - 여기서 컴파일할 때는 a와 b의 형식을 알 수 없으며 시간에 따라 a와 b의 형식이 달라질 수 있다.
    - 위 예제는 실제 호출할 메서드를 결정하는 언어 종속적 로직을 구현하는 부트스트랩 메서드의 형태로 구성된다.
    - 부트스트랩 메서드는 연결된 호출 사이트(call site)를 반환한다.
        - [https://en.wikipedia.org/wiki/Call_site](https://en.wikipedia.org/wiki/Call_site)
        - [https://docs.oracle.com/javase/8/docs/api/java/lang/invoke/CallSite.html](https://docs.oracle.com/javase/8/docs/api/java/lang/invoke/CallSite.html)
    - 두 개의 int로 add 메서드를 호출하면 이후로 이어지는 호출에도 두 개의 int가 전달된다.
    - 결과적으로 매 호출마다 호출할 메서드를 다시 찾을 필요가 없다.
    - 호출 사이트는 언제 호출 연결을 다시 계산해야 하는지 정의하는 로직을 포함할 수 있다.
- 예제의 invokedynamic은 원래 용도와는 조금 다른 의도를 갖는다.
    - 예제에서는 람다 표현식을 바이트코드로 변환하는 작업을 런타임까지 고의로 지연했다.
    - 이와 같은 방식으로 invokedynamic은 사용해서 람다 표현식을 구현하는 코드의 생성을 런타임으로 미룰 수 있다.
    - 장점
        - 람다 표현식의 바디를 바이트코드로 변환하는 작업이 독립적으로 유지된다.
            - 변환 작업이 동적으로 바뀌거나 나중에 JVM 구현에서 이를 더 최적화하거나 변환 작업을 고칠 수 있다.
            - 변환 작업은 독립적이므로 바이트코드의 과거버전 호환성을 염려할 필요가 없다.
        - 람다 덕분에 추가적인 필드나 정적 초기자 등의 오버헤드가 사라진다.
        - 상태 없는(캡처하지 않는) 람다에서 객체 인스턴스를 만들고, 캐시하고, 같은 결과를 반환할 수 있다.
            - Java8 이전에도 사람들은 이런 방식ㅇ르 사용했다.
            - ex) static final 변수에 특정 Comparator 인스턴스를 선언할 수 있다.
        - 람다를 처음 실행할 때만 변환과 결과 연결 작업이 실행되므로 추가적인 성능 비용이 들지 않는다.
            - 즉, 두 번째 호출부터는 이전 호출에서 연결된 구현을 바로 이용할 수 있다.

## 코드 생성 전략

- 런타임에 생성된 정적 메서드에 람다 표현식의 바디를 추가하면 람다 표현식이 바이트코드로 변환된다.
- 가장 간단하게 변환할 수 있는 람다 형식은 상태를 포함하지 않는 람다다.
- 이 경우 컴파일러는 람다 표현식과 같은 시그니처를 갖는 메서드를 생성한다.

```
public class Lambda {
	Function<Object, String> f = [dynamic invocation of lambda$1]

	static String lambda$1(Object obj) {
		return obj.toString();
	}
}
```

- final(또는 final에 준하는) 지역 변수나 필드를 캡처하는 다음과 같은 람다 표현식의 변환 과정은 좀 더 복잡하다.

```java
public class Lambda {
	String header = "This is a ";
	Function<Object, String> f = obj -> header + obj.toString();
}
```

- 이번에는 생성된 메서드에 람다를 감싸는 콘텍스트의 추가 상태를 전달할 인수가 필요하므로 메서드의 시그니처와 람다 표현식이 시그니처와 일치하지 않는다.
- 가장 간단한 해결 방법은 람다 표현식의 인수에 캡처한 각 변수를 추가하는 것이다.

```
public class Lambda {
	String header = "This is a ";
	Function<Object, String> f = [dynamic invocation of lambda$1]

	static String lambda$1(String header, Object obj) {
		return obj -> header + obj.toString();
	}
}
```

- 람다 표현식을 변환 처리하는 방법과 관련한 자세한 정보는 아래 링크를 참조하자.
    - [http://cr.openjdk.java.net/~briangoetz/lambda/lambda-translation.html](http://cr.openjdk.java.net/~briangoetz/lambda/lambda-translation.html)