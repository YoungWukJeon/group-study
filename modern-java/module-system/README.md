> 모듈은 일반적으로 큰 체계의 구성요소이고, 다른 구성요소와 독립적으로 운영된다.

[https://ko.wikipedia.org/wiki/모듈성_(프로그래밍)](https://ko.wikipedia.org/wiki/%EB%AA%A8%EB%93%88%EC%84%B1_(%ED%94%84%EB%A1%9C%EA%B7%B8%EB%9E%98%EB%B0%8D))

- Java9에서 가장 많이 거론되는 새로운 기능은 바로 모듈 시스템이다.
- Java9 모듈은 클래스가 어떤 다른 클래스를 볼 수 있는지를 컴파일 시간에 정교하게 제어할 수 있다.
- 기존 Java에서 제공하는 패키지는 모듈성을 지원하지 않는다.

## 압력 : 소프트웨어 유추

- 관심사 분리(SoC, Separation of Concerns)
    - 컴퓨터 프로그램을 고유의 기능으로 나누는 동작을 권장하는 원칙
    - 모델, 뷰, 컨트롤러 같은 아키텍처 관점 그리고 복구 기법을 비즈니스 로직과 분리하는 등의 하위 수준 접근 등의 상황에 유용하다.
    - 장점
        - 개별 기능을 따로 작업할 수 있으므로 팀이 쉽게 협업할 수 있다.
        - 개별 부분을 재사용하기 쉽다.
        - 전체 시스템을 쉽게 유지보수할 수 있다.
- 정보 은닉(Information Hiding)
    - 세부 구현을 숨기도록 장려하는 원칙이다.
    - 소프트웨어를 개발할 때 요구사항은 자주 바뀐다.
        - 세부 구현을 숨김으로 프로그램의 어떤 부분을 바꿨을 때 다른 부분까지 영향을 미칠 가능성을 줄일 수 있다.
    - 캡슐화(Encapsulation)
        - 특정 코드 조각이 애플리케이션의 다른 부분과 고립되어 있음을 의미한다.
        - 캡슐화된 코드의 내부적인 변화가 의도치 않게 외부에 영향을 미칠 가능성이 줄어든다.
    - Java 9 이전까지는 **클래스와 패키지가 의도된 대로 공개되었는지**를 컴파일러로 확인할 수 있는 기능이 없었다.
- Java에서는 public, protected, private 등의 접근 제한자와 패키지 수준 접근 권한 등을 이용해 메서드, 필드 클래스의 접근을 제어했다.
    - 최종 사용자에게 원하지 않는 메서드를 공개해야 하는 상황이 발생했다.
    - Java 초창기에는 애플리케이션과 의존성 체인이 상대적으로 작았고 이런 부분이 큰 문제가 아니었다.
    - 요즘 Java 애플리케이션이 커지면서 문제가 부각되고 있다.

## 자바 모듈 시스템을 설계한 이유

- 모듈화의 한계
    - Java9  이전에 Java는 클래스, 패키지, JAR 세 가지 수준의 코드 그룹화를 제공
    - 클래스와 관련해 자바는 접근 제한자와 캡슐화를 지원
    - 패키지와 JAR 수준에서는 캡슐화를 거의 지원하지 않았다.
- 제한된 가시성 제어
    - public, protected, default, private 이렇게 네 가지 가시성 접근자가 있다.
    - 한 패키지의 클래스와 인터페이스를 다른 패키지로 공개하려면 public으로 선언해야 한다.
    - 결과적으로 이들 클래스와 인터페이스는 모두에게 공개된다.
    - 내부적으로 사용할 목적으로 만든 구현을 다른 프로그래머가 임시적으로 사용해서 정착해버릴 수 있으므로 결국 기존의 애플리케이션을 망가뜨리지 않고 라이브러리 코드를 바꾸기가 어려워진다.
- 클래스 경로(class path)
    - 클래스를 모두 컴파일한 다음 보통 한 개의 평범한 JAR 파일에 넣고 클래스 경로에 이 JAR 파일을 추가해 사용할 수 있다.
    - 그러면 JVM이 동적으로 클래스 경로에 정의된 클래스를 필요할 때 읽는다.
    - 약점
        - 클래스 경로에는 같은 클래스를 구분하는 버전 개념이 없다.
            - 다양한 컴포넌트가 같은 라이브러리의 다른 버전을 사용하는 상황이 발생할 수 있는 큰 애플리케이션에서 이런 문제가 두드러진다.
        - 클래스 경로는 명시적인 의존성을 지원하지 않는다.
            - 한 JAR가 다른 JAR에 포함된 클래스 집합을 사용하라고 명시적으로 의존성을 정의하는 기능을 제공하지 않는다.
            - 메이븐(Maven)이나 그레이들(Gradle) 같은 빌드 도구는 이런 문제를 해결하는 데 도움을 준다.
            - 결국 JVM이 ClassNotFoundException 같은 에러를 발생시키지 않고 애플리케이션을 정상적으로 실행할 때까지 클래스 경로에 클래스 파일을 더하거나 클래스 경로에서 클래스를 제거해보는 수 밖에 없다.
                - 이런 문제는 개발 초기에 발견할수록 좋다.
                - Java9의 모듈 시스템을 이용하면 컴파일 타임에 이런 종류의 에러를 모두 검출할 수 있다.
- 거대한 JDK
    - JDK도 버전업을 하면서 점점 거대해졌다.
    - 모바일이나 JDK 전부를 필요로 하지 않는 클라우드에서 문제가 발생
    - Java8에서는 컴팩트 프로파일(compact profiles)이라는 기법을 제시
        - 관련 분야에 따라 JDK 라이브러리가 세 가지 프로파일로 나뉘어 각각 다른 메모리 풋프린트를 제공
        - 땜질식 처방일 뿐이다.
    - JDK 라이브러리의 많은 내부 API는 공개되지 않아야 한다.
    - Java 언어의 낮은 캡슐화 지원 때문에 내부 API가 외부로 공개되었다.
    - 스프링(Spring), 네티(Netty), 모키토(Mockito) 등 여러 라이브러리에서 sun.misc.Unsafe라는 클래스를 사용했는데 이 클래스는 JDK 내부에서만 사용하도록 만든 클래스다.
        - 결과적으로 호환성을 깨지 않고는 관련 API를 바꾸기가 아주 어려운 상황이 되었다.
    - 이런 문제들 때문에 JDK 자체도 모듈화할 수 있는 Java 모듈 시스템 설계의 필요성이 제기되었다.
    - 즉, JDK에서 필요한 부분만 골라 사용하고, 클래스 경로를 쉽게 유추할 수 있으며, 플랫폼을 진화시킬 수 있는 강력한 캡슐화를 제공할 새로운 건축 구조가 필요했다.

## 자바 모듈 : 큰 그림

- Java 9는 모듈이라는 새로운 자바 프로그램 구조 단위를 제공한다.
- module이라는 새 키워드에 이름과 바디를 추가해서 정의한다.
- 모듈 디스크립터(module descriptor)는 module-info.java라는 특별한 파일에 저장된다.
    - 엄밀하게 따지면 텍스트 형식을 모듈 선언(module declaration)이라고 하고 module-info.class에 저장된 바이너리 형식을 모듈 디스크립터라고 한다.
- 모듈 디스크립터는 보통 패키지와 같은 폴더에 위치하며 한 개 이상의 패키지를 서술하고 캡슐화할 수 있지만 단순한 상황에서는 이들 패키지 중 한 개만 외부로 노출시킨다.

![module01](image/module01.png '자바 모듈 디스크립터의 핵심 구조(module-info.java)')


- 직소 퍼즐(직소 프로젝트도 이 퍼즐에서 이름이 유래했을 것으로 추정)에 비유하자면 exports는 돌출부, requires는 패인 부분으로 생각할 수 있다.

![module02](image/module02.png 'A, B, C, D 네 개의 모듈로 만든 자바 시스템의 직소 퍼즐 형식 예제')


- 메이븐 같은 도구를 사용할 때 모듈의 많은 세부 사항을 IDE가 처리하며 사용자에게는 잘 드러나지 않는다.

## 자바 모듈 시스템으로 애플리케이션 개발하기

- 애플리케이션 셋업
    - 애플리케이션의 개념을 모델링할 여러 클래스와 인터페이스를 정의해야 한다.
    - 실생활에서 단순한 프로젝트를 이처럼 잘게 분해해 작은 기능까지 캡슐화한다면 장점에 비해 초기 비용이 높아지고, 이것이 과연 옳은 결정인가 논란이 생길 수 있다.
    - 하지만 프로젝트가 점점 커지면서 많은 내부 구현이 추가되면 이때부터 캡슐화와 추론의 장점이 두드러진다.
- 세부적인 모듈화와 거친 모듈화
    - 시스템을 모듈화할 때 모듈 크기를 결정해야 한다.
    - 세부적인 모듈화 기법 대부분은 모든 패키지가 자신의 모듈을 갖는다.
        - 이득에 비해 설계 비용이 증가
    - 거친 모듈화 기법 대부분은 한 모듈이 시스템의 모든 패키지를 포함한다.
        - 모듈화의 모든 장점을 잃는다.
    - 가장 좋은 방법은 시스템을 실용적으로 분해하면서 진화하는 소프트웨어 프로젝트가 이해하기 쉽고 고치기 쉬운 수준으로 적절하게 모듈화되어 있는지 주기적으로 확인하는 프로세스를 갖는 것이다.
- 자바 모듈 시스템의 기초

    ```
    |-- expenses.application
    	|-- module-info.java
    	|-- com
    		|-- example
    			|-- expenses
    				|-- application
    					|--ExpensesApplication.java
    ```

    - module-info.java
        - 이 파일은 모듈 디스크립터로 모듈의 소스 코드 파일 루트에 위치해야 하며 모듈의 의존성 그리고 어떤 기능을 외부로 노출할지를 정의한다.
    - 보통 IDE와 빌드 시스템에서 이들 명령을 자동으로 처리하지만 이들 명령이 어떤 동작을 수행하는지 확인하는 것은 내부적으로 어떤 일이 일어나는지 이해하는 데 도움이 된다.

    ```bash
    javac module-info.java com/example/expenses/application/ExpensesApplication.java -d target

    jar cvfe expenses-application.jar com.example.expenses.application.ExpensesApplication -C target
    ```

    - 위 명령을 실행하면 어떤 폴더와 클래스 파일이 생성된 JAR(expenses-application.jar)에 포함되어 있는지를 보여주는 다음과 같은 결과가 출력된다.

    ```bash
    added manifest
    added module-info: module-info.class adding: com/(in = 0) (out = 0) (stored 0%)
    adding: com/example/(in = 0) (out = 0) (stored 0%)
    adding: com/example/expenses/(in = 0) (out = 0) (stored 0%)
    adding: com/example/expenses/application/(in = 0) (out = 0) (stored 0%)
    adding: com/example/expenses/application/ExpensesApplication.class(in = 456) (out = 356) (deflated 32%)
    ```

    - 마지막으로 생성된 JAR를 모듈화 애플리케이션으로 실행한다.

    ```bash
    java --module-path expenses-application.jar \
        --module expenses/com.example.expenses.application.ExpensesApplication
    ```

    - 새롭게 추가된 두 가지 옵션
        - --module-path : 어떤 모듈을 로드할 수 있는지 지정한다. 이 옵션은 클래스 파일을 지정하는 --classpath 인수와는 다르다.
        - --module : 이 옵션은 실행할 메인 모듈과 클래스를 지정한다.
    - 모듈 정의는 버전 문자열을 포함하지 않는다.
        - Java9 모듈 시스템에서 버전 선택 문제를 크게 고려하지 않았고 따라서 버전 기능은 지원하지 않는다.
        - 대신 버전 문제는 빌드 도구나 컨테이너 애플리케이션에서 해결해야 할 문제로 넘겼다.

## 여러 모듈 활용하기

- exports 구문

    ```java
    module expenses.readers {
    	exports com.example.expenses.readers; // 패키지명
    	exports com.example.expenses.readers.file; // 패키지명
    	exports com.example.expenses.readers.http; // 패키지명
    }
    ```

    - exports는 다른 모듈에서 사용할 수 있도록 특정 패키지를 공개 형식으로 만든다.
    - 기본적으로 모듈 내의 모든 것은 캡슐화된다.
    - 모듈 시스템은 화이트 리스트 기법(whitelist)을 이용해 강력한 캡슐화를 제공하므로 다른 모듈에서 사용할 수 있는 기능이 무엇인지 명시적으로 결정해야 한다.
    - 프로젝트의 두 모듈의 디렉터리 구조는 다음과 같다.

    ```
    |-- expenses.application
    	|-- module-info.java
    	|-- com
    		|-- example
    			|-- expenses
    				|-- application
    					|--ExpensesApplication.java

    |-- expenses.readers
    	|-- module-info.java
    	|-- com
    		|-- example
    			|-- expenses
    				|-- readers
    					|-- Reader.java
    				|-- file
    					|-- FileReader.java
    				|-- http
    					|-- HttpReader.java
    ```

- requires 구문

    ```java
    module expenses.readers {
    	requires java.base; // 모듈명

    	exports com.example.expenses.readers; // 패키지명
    	exports com.example.expenses.readers.file; // 패키지명
    	exports com.example.expenses.readers.http; // 패키지명
    }
    ```

    - requires는 의존하고 있는 모듈을 지정한다.
    - 기본적으로 모든 모듈은 java.base라는 플랫폼 모듈을 의존하는데 이 플랫폼 모듈은 net, io, util 등의 Java 메인 패키지를 포함한다.
        - 항상 기본적으로 필요한 모듈이므로 java.base는 명시적으로 정의할 필요가 없다.
        | 클래스 가시성 | Java9 이전 | Java9 이후 |
        | --- | --- | --- |
        | `모든 클래스가 모두에 공개됨` | O O | O O (exports와 requires 구문 혼합) |
        | `제한된 클래스만 공개됨` | X X | O O (exports와 requires 구문 혼합) |
        | `한 모듈의 내에서만 공개` | X X | O (exports 구문 없음) |
        | `Protected` | O O | O O |
        | `Package` | O O | O O |
        | `Private` | O O | O O |
    
- 이름 정하기
    - 오라클은 패키지명처럼 인터넷 도메인명을 역순으로 모듈의 이름을 정하도록 권고한다.
    - 모듈명은 노출된 주요 API 패키지와 이름이 같아야 한다는 규칙도 따라야 한다.
    - 모듈이 패키지를 포함하지 않거나 어떤 다른 이유로 노출된 패키지 중 하나와 이름이 일치하지 않는 상황을 제외하면 모듈명은 작성자의 인터넷 도메인명을 역순으로 시작해야 한다.

## 컴파일과 패키징

- 먼저 각 모듈에 pom.xml을 추가해야 한다. (메이븐을 사용한다고 가정)
- 사실 각 모듈은 독립적으로 컴파일되므로 자체적으로 각각이 한 개의 프로젝트다.
- 전체 구조는 다음과 같다.

```
|-- pom.xml
|-- expenses.application
	|-- pom.xml
	|-- src
		|-- main
			|-- java
				|-- module-info.java
				|-- com
					|-- example
						|-- expenses
							|-- application
								|--ExpensesApplication.java

|-- expenses.readers
	|-- pom.xml
	|-- src
		|-- main
			|-- java
				|-- module-info.java
				|-- com
					|-- example
						|-- expenses
							|-- readers
								|-- Reader.java
							|-- file
								|-- FileReader.java
							|-- http
								|-- HttpReader.java
```

- 모듈 디스크립터(module-info.ava)는 src/main/java 디렉터리에 위치해야 한다.
- 올바른 모듈 소스 경로를 이용하도록 메이븐이 javac를 설정한다.
- expenses.readers 프로젝트의 pom.xml 모습이다.

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <project xmlns="http://maven.apache.org/POM/4.0.0"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
        http://maven.apache.org/xsd/maven-4.0.0.xsd">
        <modelVersion>4.0.0</modelVersion>

        <groupId>com.example</groupId>
        <artifactId>expenses.readers</artifactId>
        <version>1.0</version>
        <packaging>jar</packaging>
        <parent>
            <groupId>com.example</groupId>
            <artifactId>expenses</artifactId>
            <version>1.0</version>
        </parent>
    </project>
    ```

- expenses.application 프로젝트의 pom.xml 모습이다.

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <project xmlns="http://maven.apache.org/POM/4.0.0"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
        http://maven.apache.org/xsd/maven-4.0.0.xsd">
        <modelVersion>4.0.0</modelVersion>

        <groupId>com.example</groupId>
        <artifactId>expenses.application</artifactId>
        <version>1.0</version>
        <packaging>jar</packaging>

        <parent>
            <groupId>com.example</groupId>
            <artifactId>expenses</artifactId>
            <version>1.0</version>
        </parent>

        <dependencies>
            <dependency>
                <groupId>com.example</groupId>
                <artifactId>expenses.readers</artifactId>
                <version>1.0</version>
            </dependency>
        </dependencies>
    </project>
    ```

- expenses 전역 pom.xml의 모습이다.

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <project xmlns="http://maven.apache.org/POM/4.0.0"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
        http://maven.apache.org/xsd/maven-4.0.0.xsd">
        <modelVersion>4.0.0</modelVersion>

        <groupId>com.example</groupId>
        <artifactId>expenses</artifactId>
        <version>1.0</version>
        <packaging>pom</packaging>

        <modules>
            <module>expenses.application</module>
            <module>expenses.readers</module>
        </modules>

        <build>
            <pluginManagement>
                <plugins>
                    <plugin>
                        <groupId>org.apache.maven.plugins</groupId>
                        <artifactId>maven-compiler-plugin</artifactId>
                        <version>3.7.0</version>
                        <configuration>
                            <source>9</source>
                            <target>9</target>
                        </configuration>
                    </plugin>
                </plugins>
            </pluginManagement>
        </build>
    </project>
    ```

- mvn clean package 명령을 실행해서 프로젝트의 모듈을 JAR로 만들 수 있다.
- 다음과 같은 부산물이 만들어진다.
    - ./expenses.application/target/expenses.application-1.0.jar
    - ./expenses.readers/target/expenses.readers-1.0.jar
- 두 JAR를 다음처럼 모듈 경로에 포함해서 모듈 애플리케이션을 실행할 수 있다.

    ```bash
    java --module-path \
    	./expenses.application/target/expenses.application-1.0.jar:\
    	./expenses.readers/target/expenses.readers-1.0.jar \
    	--module \
    	expenses.application/com.example.expenses.application.ExpensesApplication
    ```

## 자동 모듈

- 외부 라이브러리를 프로젝트 내에 추가하는 경우
    - 사용할 모듈의 module-info.java 뿐만 아니라 pom.xml에 dependency를 추가해줘야 한다.
    - 예제에 나오는 org.apache.httpcomponents:httpclient:4.5.3은 외부 라이브러리로 모듈화가 되어 있지 않은 라이브러리다.
- Java는 JAR를 자동 모듈이라는 형태로 적절하게 변환한다.
- 모듈 경로상에 있으나 module-info 파일을 가지지 않은 모든 JAR는 자동 모듈이 된다.
- 자동 모듈은 암묵적으로 자신의 모든 패키지를 노출시킨다.
- 자동 모듈의 이름은 JAR 이름을 이용해 정해진다.
- jar 도구의 --describe-module 인수를 이용해 자동으로 정해지는 이름을 바꿀 수있다.

```bash
jar --file=./expenses.reader/target/dependency/httpclient-4.5.3.jar \
	--describe-module httpclient@4.5.3 automatic
```

- 마지막으로 httpclient JAR를 모듈 경로에 추가한 다음 애플리케이션을 실행한다.

```bash
java --module-path \
	./expenses.application/target/expenses.application-1.0.jar:\
	./expenses.readers/target/expenses.readers-1.0.jar \
	./expenses.readers/target/dependency/httpclient-4.5.3.jar \
	--module \
	expenses.application/com.example.expenses.application.ExpensesApplication
```

## 모듈 정의와 구문들

- requires
    - 컴파일 타임과 런타임에 한 모듈이 다른 모듈에 의존함을 정의한다.
- exports
    - 지정한 패키지를 다른 모듈에서 이용할 수 있도록 공개 형식으로 만든다.
    - 아무 패키지도 공개하지 않는 것이 기본 설정이다.
    - 어떤 패키지를 공개할 것인지를 명시적으로 지정함으로 캡슐화를 높일 수 있다.
- requires transitive
    - 다른 모듈이 제공하는 공개 형식을 한 모듈에서 사용할 수 있다고 지정할 수 있다.

    ```java
    module com.iteratrlearning.ui {
    	requires transitive com.iteratrlearning.core;
    	
    	exports com.iteratrlearning.ui.panels;
    	exports com.iteratrlearning.ui.widgets;
    }

    module com.iteratrlearning.application {
    	requires com.iteratrlearning.ui;
    }

    /*
    	com.iteratrlearning.application 모듈이 com.iteratrlearning.ui 모듈을 통해
    	com.iteratrlearning.core 모듈을 사용할 수 있게 된다.
    	requires transitive를 사용하지 않으면 com.iteratrlearning.application 모듈 내부에서
    	require com.iteratrlearning.core를 추가해줘야 한다.
    */
    ```

- exports to
    - 사용자에게 공개할 기능을 제한함으로 가시성을 좀 더 정교하게 제어할 수 있다.

    ```java
    module com.iteratrlearning.ui {
    	requires com.iteratrlearning.core;

    	exports com.iteratrlearning.ui.panels;
    	exports com.iteratrlearning.ui.widgets to com.iteratrlearning.ui.widgetuser
    }
    ```

- open과 opens

    ```java
    open module com.iteratrlearning.ui {
    	...
    }
    ```

    - 모듈 선언에 open 한정자를 이용하면 모든 패키지를 다른 모듈에 반사적(Reflective)으로 접근을 허용할 수 있다.
    - Java9 이전에는 리플렉션으로 객체의 비공개 상태를 확인할 수 있었다.
    - 하이버네이트(Hibernate) 같은 객체 관계 매핑(ORM, Object-relational mapping) 도구에서는 이런 기능을 이용해 상태를 직접 고치곤 한다.
    - Java9에서는 기본적으로 리플랙션이 이런 기능을 허용하지 않는다.
    - 이제 그런 기능이 필요하면 이전 코드에서 설명한 open 구문을 명시적으로 사용해야 한다.
    - 리플렉션 때문에 전체 모듈을 개방하지 않고도 opens 구문을 모듈 선언에 이용해 필요한 개별 패키지만 개방할 수 있다.
    - exports-to로 노출한 패키지를 사용할 수 있는 모듈을 한정했던 것처럼, open-to를 사용해 반사적인 접근을 특정 모율에만 허용할 수 있다.
- uses와 providers
    - providers 구문으로 서비스 제공자를 uses 구문으로 서비스 소비자를 지정할 수 있는 기능을 제공
    - 이 주제는 고급 주제이 해당하므로 이 장의 범위를 벗어난다. (생략)

## 더 큰 예제 그리고 더 배울 수 있는 방법

```java
module com.example.foo {
	requires com.example.foo.http;
	requires java.logging;

	requires transitive com.example.foo.network;

	exports com.example.foo.bar;
	exports com.example.foo.internal to com.example.foo.probe;

	opens com.example.foo.quux;
	opens com.example.foo.internal to com.example.foo.network, com.example.foo.probe;

	uses com.example.foo.spi.Intf;
	provides com.example.foo.spi.Intff with com.example.foo.Impl;
}
```

- Java EE 개발자라면 애플리케이션을 Java9 로 이전할 때 EE와 관련한 여러 패키지가 모듈화된 Java9 가상 머신에서 기본적으로 로드되지 않는다는 사실을 기억해야 한다.
- 예를 들어, JAXB API 클래스는 이제 Java EE API로 간주되므로 Java SE9의 기본 클래스 경로에 더는 포함되지 않는다.
    - 호환성을 유지하려면 --add-modules java.xml.bind를 지정해야 한다.