# 기타 언어 업데티으
## 어노테이션
#### 어노테이션이란?  
부가 정보를 프로그램에 장식할 수 있는 기능으로 문법적 메타데이타  

##### Java 제공 기본 어노테이션
@Override
- 선언한 메서드가 오버라이드 되었다는 것
- 상위 클래스에서 해당 메서드를 찾을 수 없으면 컴파일 에러 발생

@Deprecated
- 더 이상 사용되지 않는 메서드에 사용
- 사용 시 컴파일 경고 발생

@SuppressWarnings
- 선언한 곳의 컴파일 경고 무시

@SafeVarargs
- Java7 부터 지원 제네릭 같은 가변인자의 매개변수를 사용할 때 경고 무시

@FunctionalInterface
- Java8부터 지원
- 함수형 인터페이스를 지정
- 메서드가 존재하지 않거나 1개 이상의 메서드가 존재할 경우 컴파일 에러 발생

##### 커스텀 어노테이션
```java
@interface MyAnnotation {
    String value();
    String name();
    int age();
    String[] newNames();
}
```

아래의 예제코드를 보자
```java
@Before
public void setUp(){
    this.list = new ArrayList<>();
}

@Test
public void testAlgorithm(){
    assertEquals(5, list.size());
}
```

자바 8이전에서의 어노테이션 활용
- JUnit의 콘텍스트에서 어노테이션으로 설정 작업을 하는 메서드와 단위 테스트를 실행하는 메서드 구분
- 문서화에 어노테이션을 이용하여 사용하지 말아야하는 메서드에 @Deprecated 어노테이션 활용
- 자바 컴파일러에서도 어노테이션을 사용해 에러를 검출하고 경고를 줄이고 코드를 생성 할 수 있음
- 자바 EE에서 엔터프라이즈 애플리케이션을 설정할 때 어노테이션을 많이 활용

자바 8이후 추가된 어노테이션 기능
- 어노테이션 반복
- 모든 형식에 어노테이션 사용

### 어노테이션 반복
이전 자바에서는 선언에서 지정한 하나의 어노테이션만 사용
```java
@interface Author { String name(); }

// 중복 어노테이션으로 오류 발생
@Author(name="Raoul") @Author(name="Mario") @Author(name="Alan")
class Book{}
```

위의 코드를 아래와 같이 수정 가능
```java
@interface Author { String name(); ]
@interface Authors {
    Author[] value();
}

@Authors(
    { @Author(name="Raoul"), @Author(name="Mario"), @Author="Alan") }
)
class Book{}
```

위의 코드와 같이 수정이 가능하지만 Book 클래스의 중첩 어노테이션 때문에 코드가 복잡해짐
이런 이유로 자바 8에서는 반복 어노테이션과 관련한 제한을 해제  
반복 조건만 만족하면 선언을 할 때 하나의 어노테이션 형식에 여러 어노테이션을 지정할 수 있음
어노테이션 반복은 기본으로 제공되는 기능이 아니므로 반복할 수 있는 어노테이션임을 명시적으로 지정해야함  

#### 반복할 수 있는 어노테이션 만들기
그렇다면 반복할 수 있는 어노테이션은 어떻게 만들어야 할까
- 어노테이션을 @Repeatable로 표시
- 컨테이너 어노테이션 제공

```java
@Repeatable (Aurhors.class)
@interface Author { String name(); }
@interface Authors {
    Author[] value();
}

// Book 클래스에 여러 @Author 어노테이션 사용 가능
@Author(name="Raoul") @Author(name="Mario") @Author(name="Alan")
class Book()
```
위의 코드는 컴파일시 Book는 @Authors({ @Author(name="Raoul"), @Author(name="Mario"), @Author(name="Alan)})이라는 어노테이션이 사용된것으로 간주

### 형식 어노테이션
Java8 부터는 모든 형식에 어노테이션 지정이 가능
- new 연산자
- instanceof
- 형식 캐스트
- 제네릭 형식 인수
- implements
- throws

등에 어노테이션 사용 가능

@NonNull을 이용해 문자열에 null이 들어가지 못하는 예제
```java
@NonNull String name=person.getName();
```

리스트 요소 형식에 어노테이션 적용
```java
List<@NonNull car> cars = new ArrayList<>();
```
형식 어노테이션은 프로그램을 분석할 때 용이하게 사용  
getName, cars의 모든 요소가 null이 아님을 파악할 수 있음  

## 일반화된 대상 형식 추론
Java8에서는 제네릭 인수 추론 기능을 개선
Java8이전에도 콘텍스트 정보를 이용한 형식 추론 지원
```java
// emptyList를 T라는 형식 파라미터로 파라미터화 하여 메세머드를 호출할 때 명시적으로 사용
static <T> List<T> emptyList();

List<Car> cars = Collections.<Car>emptyList();

// 암시적으로 제네릭 인수 추론도 가능
List<Car> cars = Collections.emptyList();
```

Java8 이전에는 콘텍스트에 의존하여 추론이 실행
아래의 코드는 error가 발생
```java
static void cleanCars(List<Car> cars) {

}
cleanCars(Collections.emptyList());
```

cleanCars (java.util.List<Car>) cannot be applied to (java.util.List<java.lang.Object>)

명시적인 형식 인수를 이용해 에러를 해결
```java
List<Car> cleanCars = dirtyCars.stream()
                               .filter(Car::isClean)
                               .collect(Collectors.toList());

List<Car> cleanCars = dirtyCars.stream()
                               .filter(Car::isClean)
                               .collect(Collectors<Car>.toList());
```


