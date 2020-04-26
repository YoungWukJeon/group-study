# 람다를 이용한 도메인 전용 언어(DSL)
### DSL(Domain-specific languages)이란 ?

특정 도메인에 특화된 언어로 특정 영역을 타겟하고 있는 언어  
어떤 목적이 있고 그 목적만 달성 할 수 있는 언어를 DSL이라고 한다.  
ex) SQL, HTML

반면 Java는 완고함, 장황함 등의 특성 때문에 기술 배경이 없는 사람들이 사용하기 적절하다고 간주  
그러나 lambda가 추가되며 변화하는 추새

"메뉴에서 400 칼로리 이하의 모든 요리를 찾으시오" 같은 쿼리를 프로그램으로 구현하면 아래와 같다.
```
while (block != null) {
    read(block, buffer)
    for (every record in buffer) {
        if (record.calorie < 400 ) {
            System.out.println (record.name);
        }
    }
}
```
위의 Java 코드를 SQL로 변경하면 아래와 같다

```
SELECT name FROM menu WHERE calorie < 400;
```

### java 코드의 2가지 문제점
- 로깅, I/O, 디스크 할당 등과 같은 지식 필요하여 경험이 부족한 프로그래머가 구현하기 어려움
- 애플리케이션 수준이 아니라 시스템 수준의 개념을 다루어야 함

Stream을 이용하면 아래와 같이 간결하게 변경할 수 있음
```
menu.stream().filter(d -> d.getCalories() < 400)
    .map(Dish::getName)
    .forEach(System.out::println)
```
Stream API 특성인 메서드 체인을 이용하여 자바 루프의 복잡함 제어와 비교해 유창함을 의미하는 플루언트 스타일이라 부르는데 이런 스타일은 쉽게 DSL에 적용시킬 수 있음

### 플루언트 인터페이스이란
메소드 체이닝에 상당 부분 기반한 객체 지향 API 설계 메소드이며 소스 코드의 가독성을 산문과 유사하게 만드는 것이 목적  
[플루언트 인터페이스 위키](https://ko.wikipedia.org/wiki/%ED%94%8C%EB%A3%A8%EC%96%B8%ED%8A%B8_%EC%9D%B8%ED%84%B0%ED%8E%98%EC%9D%B4%EC%8A%A4)

DSL을 만드려면 애플리케이션 수준 프로그래머에 어떤 동작이 필요하며 이들을 어떻게 프로그래머에게 제공하는지 고민이 필요  
내부적 DSL에서는 유창하게 코드를 구현할 수 있도록 적절하게 클래스와 메서드를 노출하는 과정이 필요  
외부 DSL은 DSL문법 뿐 아니라 DSL을 평가하는 파서도 구현해야함


# 10.1 도메인 전용 언어
DSL은 특정 비즈니스 도메인의 문제를 해결하려고 만든 언어  
범용 프로그래밍 언어가 아님  
특정 도메인에 국한되어 다른 문제는 걱정할 필요 없이 오직 해당 도메인에 있는 문제만 해결  
DSL을 잘 활용하면 특정 도메인의 복잡성을 더 잘 다룰 수 있음

### DSL의 특징
- 의사 소통의 왕 : DSL 코드는 프로그래머가 아닌 일반 사람도 이해할 수 있어야 하며 해당 코드가 비즈니스 요구사항에 부합하는지 확인할 수 있어야 함
- 한 번 코드를 구현하지만 여러 번 읽음 : 가독성을 좋게 하여 다른 사람도 쉽게 이해할 수 있어야 함

## 10.1.1 DSL의 장점과 단점
### 장점
- 간결함 : API는 비즈니스 로직을 간편하게 캡슐화하므로 반복을 피하고 코드를 간결하게 할 수 있음
- 가독성 : 도메인 영역의 용어를 사용하여 비 도메인 전문가도 코드를 쉽게 이해할 수 있도 코드와 도메인 영역이 공유될 수 있음
- 유지보수 : 잘 설계된 DSL로 구현한 코드는 쉽게 유지보수하고 바꿀 수 있음
- 높은 수준의 추상화 : DSL은 도메인과 같은 추상화 수준에서 동작하여 도메인의 문제와 직접적으로 관련되지 않은 세부 사항을 숨김
- 집중 : 비즈니스 도메인의 규칙을 표현할 목적으로 설계된 언어로 프로그래머가 특정 코드에 집중할 수 있어 생산성이 향상될 수 있음
- 관심사분리 : 지정된 언어로 비즈니스 로직을  표현해 애플리케이션의 인프라구조 관련 문제와 독립적으로 비즈니스 관련 코드에 집중하기 용이

### 단점
- DSL 설계의 어려움 : 간결하게 제한적인 언어에 도메인 지식을 담는 것이 쉽지 않음
- 개발 비용 : 코드에 DSL을 추가하는 작업은 초기 프로젝트에 많은 비용과 시간이 소모되며 DSL 유지보수와 변경은 프로젝트에 부담을 줌
- 추가 우회 계층 : 추가적인 계층으로 도메인 모델을 감싸며 이 계층을 최대한 작게 만들어 성능 문제를 회피
- 새로 배워야 하는 언어 : DSL 언어를 추가로 배워야 함
- 호스팅 언어 한계 : 자바 같은 범용 프로그래밍 언어는 장황하고 엄격한 문법을 가져 사용자 친화 중심의 DSL을 만들기 어렵지만 lambda를 이용해 해결할 수 있음

## 10.1.2 JVM에서 이용할 수 있는 다른 DSL 해결책
내부, 외부, 다중 DSL

### 내부 DSL(임베디드 DSL)
순수 자바 코드 같은 기존 호스팅 언어를 기반으로 구현  
기존의 Java(version ~7)는 유연성이 떨어지는 문법 때문에 DSL의 성격과는 맞지 않았지만 lambda가 도입되며 이러한 문제들이 해결되어 어느정도의 DSL을 만들 수 있게 변경
```
# java7
List<String> numbers = Arrays.asList("one", "two", "three");
numberrs.forEach( new Consumer<String>() {
    @Override
    public void accept( String s ) {
        System.out.println(s);
    }
});
```

```
# java8
numbers.forEach(s -> System.out.println(s));
numbers.forEach(System.out::println);
```

사용자가 기술적인 부분을 염두에 두고 있다면 Java를 이용해 이러한 DSL을 만들 수 있음
Java로 DSL을 구현하는 경우 아래와 같은 장점을 얻을 수 있다
- 기존 자바 언어를 이용하면 DSL 언어를 배워야하는 수고를 덜 수 있음
- 나머지 코드와 함께 DSL을 컴파일할 수 있어 다른 언어의 컴파일러를 이용하거나 외부 DSL을 만드는 등 추가 비용이 들지 않음
- 기존 자바 IDE를 이용해 자동 완성, 리팩터링 같은 기능을 그대로 사용할 수 있음
- 한 개의 언어로 한 개의 도메인 또는 여러 도메인을 대응하지 못해 추가로 DSL을 개발하지 않고 Java로 대응 가능

### 다중 DSL
스칼라나 그루비처럼 Java가 아니지만 JVM에서 실행되며 더 유연하고 표현력이 강한 언어를 이용해 DSL을 만든 것  
아래의 코드는 scala를 이용해 DSL형식을 구현한 예

```
# scala
def times(i: Int)(f: => Unit): Unit = { 
    f
    if ( i > 1 ) times(i - 1)(f)
}
```

```
times(3) {
    println("Hello World")
}
```

Java에 비해 DSL 친화적이지만 아래와 같은 단점을 가짐
- 새로운 프로그래밍 언어를 배우거나 팀의 누군가 해당 기술을 가지고 있어야 함
- 두 개 이상의 언어가 함께 사용되므로 컴파일러로 소스를 빌드하도록 빌드 과정 개선 필요
- JVM에서 실행되지만 Java와 100% 호환되지 않아 성능 손실이 발생할 수 있음

### 외부 DSL
호스팅 언어와는 독립적으로 자체의 문법을 가지는 DSL ex)XML, Makefile  
외부 DSL이 제공하는 무한한 유연성이 장점으로 언어를 선택할때 필요한 특성을 제공하는 언어를 선택할 수 있다는 장점  


## 10.2 최신 자바 API의 작은 DSL
Java의 새로운 기능의 장점을 적용한 첫 API는 네이티브 Java 자신 (Java 1.7까지의 버전)

## 10.2.1 스트림 API는 컬렉션을 조작하는 DSL
Stream은 컬렉션 항목을 필터, 정렬, 변환, 그룹화, 조작할 수 있어 작은 DSL로 볼 수 있음  
로그 파일을 읽어 "ERROR"라는 단어로 시작하는 파일의 첫 40행을 수집하는 작업을 아래와 같은 코드로 작성
```
# Java7
List<String> errors = new ArrayList<>();
int errorCount = 0;
BufferedReader bufferedReader = new BufferReader(new FileReader(fileName));
String line = bufferedReader.readLine();
while (errorCount < 40 && line != null) {
    if (line.startsWith("ERROR")) {
        errors.add(line);
        errorCount++;
    }
    line = bufferedReader.readLine();
}
```

코드에서 의무적으로 만들어져야 하는 코드와 수집하는 코드가 필요  
의무적 코드
- FileReader가 만들어짐
- 파일이 종료되었는지를 확인하는 while 루프의 두번째 조건 if(line.startsWith("Error"))
- 파일의 다음 행을 읽는 루프의 마지막 행

데이터 수집 코드
- errorCount 변수 초기화
- while 루프의 첫번째 조건 ( errorCount < 40 && line != null )
- "Error"을 로그에서 발견하면 카운터를 증가시키는 행

Stream Interface를 이용하여 아래와 같이 변경
```
List<String> errors = Files.lines(Paths.get(fileName))
                           .filter(line -> line.startsWith("ERROR"))
                           .limit(40)
                           .collect(toList());
```

Stream을 통해 데이터 리스트를 조작하는 DSL형식 구현

## 10.2.2 데이터를 수집하는 DSL인 Collectors
Collector 인터페이스는 데이터 수집을 수행하는 DSL라고 할 수 있음

