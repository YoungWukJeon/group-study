- 각 통합 패턴은 하나의 컴포넌트로 구현되며, 이것을 통해서 파이프라인으로 메시지가 데이터를 운반한다.
- 스프링 구성을 사용하면 데이터가 이동하는 파이프라인으로 이런 컴포넌트들을 조립할 수 있다.

# 간단한 통합 플로우 선언하기

- 애플리케이션은 통합 플로우를 통해서 외부 리소스나 애플리케이션 자체에 데이터를 수신 또는 전송할 수 있으며, 스프링 통합은 이런 통합 플로우를 생성할 수 있게 해준다.
- 애플리케이션이 통합할 수 있는 그런 리소스 중 하나가 파일 시스템이다.
- 이에 따라 스프링 통합의 많은 컴포넌트 중에 파일을 읽거나 쓰는 채널 어댑터(channel adapter)가 있다.
- 우선 다음과 같이 스프링 통합 의존성을 프로젝트 빌드에 추가해야 한다.

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-integration</artifactId>
</dependency>

<dependency>
	<groupId>org.springframework.integration</groupId>
	<artifactId>spring-integration-file</artifactId>
</dependency>
```

- 첫 번째 의존성은 스프링 통합의 스프링 부트 스타터다.
- 통합하려는 플로우와 무관하게 이 의존성은 스프링 통합 플로우의 개발 시에 반드시 추가해야 한다.
- 두 번째 의존성은 스프링 봍합의 파일 엔드포인트(endpoint) 모듈이다.
- 이 모듈은 외부 시스템 통합에 사용되는 24개 이상의 엔드포인트 모듈 중 하나다.
- 그 다음은 파일에 데이터를 쓸 수 있도록 애플리케이션에서 통합 플로우로 데이터를 전송하는 게이트웨이(gateway)를 생성해야 한다.

```java
package sia5;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.file.FileHeaders;
import org.springframework.messaging.handler.annotation.Header;

@MessagingGateway(defaultRequestChannel="textInChannel") // 메시지 게이트웨이를 선언한다.
public interface FileWriterGateway {
	void writeToFile(@Header(FileHeaders.FILENAME) String filename, String data); // 파일에 쓴다.
}
```

- 우선, FileWriterGateway에는 @MessagingGateway가 지정되었다.
- 이 애노테이션은 FileWriterGateway 인터페이스의 구현체(클래스)를 런타임 시에 생성하라고 스프링 통합에 알려준다.
- 이외의 다른 코드에서는 파일에 데이터를 써야 할 때 FileWriterGateway 인터페이스를 사용할 것이다.
- @MessagingGateway의 defaultRequestChannel 속성은 해당 인터페이스의 메서드 호출로 생성된 메시지가 이 속성에 지정된 메시지 채널로 전송된다는 것을 나타낸다.
- 여기서는 writeToFile()의 호출로 생긴 메시지가 textInChannel이라는 이름의 채널로 전송된다.
- writeToFile() 메서드는 두 개의 String 타입 매개변수를 갖는다.
- 파일 이름과 파일에 쓰는 텍스트를 포함하는 데이터다.
- 여기서 filename 매개변수에는 @Header가 지정되었다.
- @Header 애노테이션은 filename에 전달되는 값이 메시지 페이로드(payload)가 아닌 메시지 헤더에 있다는 것을 나타낸다.
    - FileHeaders.FILENAME 상수의 실제 값은 file_name이다.
- 반면에 data 매개변수 값은 메시지 페이로드로 전달된다.
    - 메시지는 메시지 헤더와 같은 메타데이터와 실제 데이터인 페이로드로 구성된다.
- 통합 플로우는 다음 세 가지 구성 방법으로 정의할 수 있다.
    - XML 구성
    - 자바 구성
    - DSL을 사용한 자바 구성

## XML을 사용해서 통합 플로우 정의하기

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:int="http://www.springframework.org/schema/integration"
	xmlns:int-file="http://www.springframework.org/schema/integration/file"
	xsi:schemaLocation="http://www.springframework.org/schema/beans
		http://www.springframework.org/schema/beans/spring-beans.xsd
		http://www.springframework.org/schema/integration
		http://www.springframework.org/schema/integration/spring-integration.xsd
		http://www.springframework.org/schema/integration/file
		http://www.springframework.org/schema/integration/file/springintegration-file.xsd">

	<int:channel id="textInChannel" /> <!-- textInChannel을 선언한다. -->

	<int:transformer id="upperCase"
			input-channel="textInChannel"
			output-channel="fileWriterChannel"
			expression="payload.toUpperCase()" /> <!-- 텍스트를 변환한다. -->

	<int:channel id="fileWriterChannel" /> <!-- fileWriterChannel을 선언한다. -->

	<int-file:outbound-channel-adapter id="writer" 
			channel="fileWriterChannel"
			directory="/tmp/sia5/files"
			mode="APPEND"
			append-new-line="true" /> <!-- 텍스트를 파일에 쓴다. -->
</beans>
```

- 구성에서 주목할 내용은 다음과 같다.
    - textInChannel이라는 이름의 채널을 구성하였다. 이것은 FileWriterGateway의 요청 채널로 설정된 것과 같은 채널이다. FileWriterGateway의 writeToFile() 메서드가 호출되면 결과 메시지가 textInChannel로 전달된다.
    - textInChannel로부터 메시지를 받는 변환기(int:transformer)를 구성하였다. 이 변환기는 SpEL(Spring Expression Language) 표현식을 사용해서 메시지 페이로드에 대해 toUpperCase()를 호출하여 대문자로 변환한다. 그리고 변환된 결과는 fileWriterChannel로 전달된다.
    - fileWriterChannel이라는 이름의 채널을 구성하였다. 이 채널은 변환기와 아웃바운드 채널 어댑터(outbound channel adapter)를 연결하는 전달자의 역할을 수행한다.
    - 끝으로, int-file 네임스페이스를 사용하여 아웃바운드 채널 어댑터를 구성하였다. 이 XML 네임스페이스는 파일에 데이터를 쓰기 위해 스프링 통합의 파일 모듈에서 제공한다. 아웃바운드 채널 어댑터는 fileWriterChannel로부터 메시지를 받은 후 해당 메시지 페이로드를 directory 속성에 지정된 디렉터리의 파일에 쓴다. 이때 파일 이름은 해당 메시지의 file_name 헤더에 지정된 것을 사용한다. 만일 해당 파일이 이미 있으면 기존 데이터에 덮어쓰지 않고 줄을 바꾸어 제일 끝에 추가한다.

![chapter09-01](image/chapter09-01.png '파일-쓰기 통합 플로우')

- 스프링 부트 애플리케이션에서 XML 구성을 사용하고자 한다면 XML을 리소스로 import해야 한다.
- 이때 우리 애플리케이션의 자바 구성 클래스 중 하나에 스프링의 @ImportResource 애노테이션을 지정하는 것이 가장 쉬운 방법이다.

```java
@Configuration
@ImportResource("classpath:/filewriter-config.xml")
public class FileWriterIntegrationConfig { ... }
```

## Java로 통합 플로우 구성하기

- 현재는 대부분의 스프링 애플리케이션이 XML 구성을 피하고 자바 구성을 사용한다.

```java
package sia5;

import java.io.File;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.integration.transformer.GenericTransformer;

@Configuration
public class FileWriterIntegrationConfig {
	@Bean
	@Transformer(inputChannel="textInChannel", outputChannel="fileWriterChannel") // 변환기 빈을 선언한다.
	public GenericTransformer<String, String> upperCaseTransformer() {
		return text -> text.toUpperCase();
	}

	@Bean
	@ServiceActivator(inputChannel="fileWriterChannel")
	public FileWritingMessageHandler fileWriter() { // 파일-쓰기 빈을 선언한다.
		FileWritingMessageHandler handler = new FileWritingMessageHandler(new File("/tmp/sia5/files"));
		handler.setExpectReply(false);
		handler.setFileExistsMode(FileExistsMode.APPEND);
		handler.setAppendNewLine(true);
		return handler;
	}
}
```

- 이 자바 구성에서는 두 개의 빈을 정의한다.
- 변환기와 파일-쓰기 메시지 핸들러다.
- 변환기 빈인 GenericTransformer는 함수형 인터페이스이므로 메시지 텍스트에 toUpperCase()를 호출하는 람다(lambda)로 구현할 수 있다.
- GenericTransformer에는 @Transformer가 지정되었다.
- 이 애노테이션은 GenericTransformer가 textInChannel의 메시지를 받아서 fileWriterChannel로 쓰는 통합 플로우 변환기라는 것을 지정한다.
- 파일-쓰기 빈에는 @ServiceActivator가 지정되었다.
- 이 애노테이션은 fileWriterChannel로부터 메시지를 받아서 FileWritingMessageHandler의 인스턴스로 정의된 서비스에 넘겨줌을 나타낸다.
- FileWritingMessageHandler는 메시지 핸들러이며, 메시지 페이로드를 지정된 디렉터리의 파일에 쓴다.
- 이때 파일 이름은 해당 메시지의 file_name 헤더에 지정된 것을 사용한다.
- 그리고 XML 구성과 동일하게 해당 파일이 이미 있으면 기존 데이터에 덮어 쓰지 않고 줄을 바꾸어 제일 끝에 추가한다.
- FileWritingMessageHandler 빈의 구성에서 한 가지 특이한 것을 setExpectReply(false)를 호출한다는 것이다.
- 이 메서드는 서비스에서 응답 채널(플로우의 업스트림 컴포넌트로 값이 반환될 수 있는 채널)을 사용하지 않음을 나타낸다.
- 만일 setExpectReplay(false)를 호출하지 않으면, 통합 플로우가 정상적으로 작동하더라도 응답 채널이 구성되지 않았다는 로그 메시지들이 나타난다.
- 자바 구성에서는 채널들을 별도로 선언하지 않았다는 것에 주목하자.
- textInChannel과 fileWriterChannel이라는 이름의 빈이 없으면 이 채널들은 자동으로 생성되기 때문이다.
- 그러나 각 채널의 구성 방법을 더 제어하고 싶으면 다음과 같이 별도의 빈으로 구성할 수 있다.

```java
@Bean
public MessageChannel textInChannel() {
	return new DirectChannel();
}

...
@Bean
public MessageChannel fileWriterChannel() {
	return new DirectChannel();
}
```

## 스프링 통합의 DSL 구성 사용하기

```java
package sia5;

import java.io.File;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.file.support.FileExistsMode;

@Configuration
public class FileWriterIntegrationConfig {
	@Bean
	public IntegrationFlow fileWriterFlow() {
		return IntegrationFlows
			.from(MessageChannels.direct("textInChannel")) // 인바운드 채널
			.<String, String>transform(t -> t.toUpperCase()) // 변환기를 선언한다.
			.handle(Files // 파일에 쓰는 것을 처리한다.
				.outboundAdapter(new File("/tmp/sia5/files"))
				.fileExistsMode(FileExistsMode.APPEND)
				.appendNewLine(true))
			.get();
	}
}
```

- 이 구성은 전체 플로우를 하나의 빈 메서드에 담고 있어서 코드를 최대한 간결하게 작성할 수 있다.
- IntegrationFlows 클래스는 플로우를 선언할 수 있는 빌더 API를 시작시킨다.
- 변환기를 아웃바운드 채널 어댑터와 연결하는 채널의 경우에 이 채널을 별도로 구성할 필요가 있다면, 다음과 같이 플로우 정의에서 channel() 메서드를 호출하여 해당 채널을 이름으로 참조할 수 있다.

```java
@Bean
public IntegrationFlow fileWriterFlow() {
	return IntegrationFlows
		.from(MessageChannels.direct("textInChannel"))
		.<String, String>transform(t -> t.toUpperCase()
		.channel(MessageChannels.direct("fileWriterChannel"))
		.handle(Files
			.outboundAdapter(new File("/tmp/sia5/files"))
			.fileExistsMode(FileExistsMode.APPEND)
			.appendNewLine(true))
		.get();
}
```

- 스프링 통합의 자바 DSL을 사용할 때 한 가지 유념할 것이 있다.
- 즉, 코드의 가독성을 높이기 위해 들여쓰기를 잘 해야 한다는 것이다.
- 또한, 통합 플로우의 코드가 더 길고 복잡할 경우에는 해당 플로우의 일부분을 별도 메서드나 서브 플로우로 추출하는 것을 고려할 수 있다.

# 스프링 통합의 컴포넌트 살펴보기

- 통합 플로우는 하나 이상의 컴포넌트로 구성되며, 그 내역은 다음과 같다.
    - 채널(Channel) : 한 요소로부터 다른 요소로 메시지를 전달한다.
    - 필터(Filter) : 조건에 맞는 메시지가 플로우를 통과하게 해준다.
    - 변환기(Transformer) : 메시지 값을 변경하거나 메시지 페이로드의 타입을 다른 타입으로 변환한다.
    - 라우터(Router) : 여러 채널 중 하나로 메시지를 전달하며, 대개 메시지 헤더를 기반으로 한다.
    - 분배기(Splitter) : 들어오는 메시지를 두 개 이상의 메시지로 분할하며, 분할된 각 메시지는 다른 채널로 전송된다.
    - 집적기(Aggregator) : 분배기와 상반된 것으로 별개의 채널로부터 전달되는 다수의 메시지를 하나의 메시지로 결합한다.
    - 서비스 액티베이터(Service activator) : 메시지를 처리하도록 자바 메서드에 메시지를 넘겨준 후 메서드의 반환값을 출력 채널로 전송한다.
    - 채널 어댑터(Channel adapter) : 외부 시스템에 채널을 연결한다. 외부 시스템으로부터 입력을 받거나 쓸 수 있다.
    - 게이트웨이(Gateway) : 인터페이스를 통해 통합 플로우로 데이터를 전달한다.

## 메시지 채널

- 메시지 채널은 통합 파이프라인을 통해서 메시지가 이동하는 수단이다.
- 즉, 채널은 스프링 통합의 다른 부분을 연결하는 통로다.

![chapter09-02](image/chapter09-02.png '메시지 채널은 통합 플로우의 서로 다른 컴포넌트 간에 데이터를 전달하는 통로다.')

- 스프링 통합은 다음을 포함해서 여러 채널 구현체(클래스)를 제공한다.
    - PublishSubscribeChannel : 이것으로 전송되는 메시지는 하나 이상의 컨슈머(메시지를 소비하는(읽는) 컴포넌트나 애플리케이션)로 전달된다. 컨슈머가 여럿일 때는 모든 컨슈머가 해당 메시지를 수신한다.
    - QueueChannel : 이것으로 전송되는 메시지는 FIFO(first in first out, 선입선출) 방식으로 컨슈머가 가져갈 때까지 큐에 저장한다. 컨슈머가 여럿일 때는 그중 하나의 컨슈머만 해당 메시지를 수신한다.
    - PriorityChannel : QueueChannel과 유사하지만, FIFO 방식 대신 메시지의 priority 헤더를 기반으로 컨슈머가 메시지를 가져간다.
    - RendezvousChannel : QueueChannel과 유사하지만, 컨슈머가 메시지를 수신할 때까지 메시지 전송자가 채널을 차단한다는 것이 다르다.
        - 전송자와 컨슈머를 동기화한다.
    - DirectChannel : PublishSubscribeChannel과 유사하지만, 전송자와 동일한 스레드로 실행되는 컨슈머를 호출하여 단일 컨슈머에게 메시지를 전송한다. 이 채널은 트랜잭션을 지원한다.
    - ExecutorChannel : DirectChannel과 유사하지만, TaskExecutor를 통해서 메시지가 전송된다. 이 채널 타입은 트랜잭션을 지원하지 않는다.
        - 전송자와 다른 스레드에서 처리된다.
    - FluxMessageChannel : 프로젝트 리액터(Project Reactor)의 플럭스(Flux)를 기반으로 하는 리액티브 스트림즈 퍼블리셔(Reactive Streams Publisher) 채널이다.
- 자바 구성과 자바 DSL 구성 모두에서 입력 채널은 자동으로 생성되며, 기본적으로 DirectChannel이 사용된다.
- 그러나 다른 채널 구현체를 사용하고 싶다면 해당 채널을 별도의 빈으로 선언하고 통합 플로우에서 참조해야 한다.

```java
@Bean
public MessageChannel orderChannel() {
	return new PublishSubscribeChannel();
}
```

- 그 다음에 통합 플로우 정의에서 이 채널을 이름으로 참조한다.
- 예를 들어, 이 채널을 서비스 액티베이터에서 소비(사용)한다면 @ServiceActivator 애노테이션의 inputChannel 속성에서 이 채널 이름으로 참조하면 된다.

```java
@ServiceActivator(inputChannel="orderChannel")
```

- 또는 자바 DSL 구성을 사용할 때는 channel() 메서드의 호출에서 참조한다.

```java
@Bean
public IntegrationFlow orderFlow() {
	return IntegrationFlows
		...
		.channel("orderChannel")
		...
		.get();
}
```

- QueueChannel을 사용할 때는 컨슈머가 이 채널을 폴링(polling, 도착한 메시지가 있는지 지속적으로 확인함)하도록 구성하는 것이 중요하다.

```java
@Bean
public MessageChannel orderChannel() {
	return new QueueChannel();
}
```

- 이것을 입력 채널로 사용할 때 컨슈머는 도착한 메시지 여부를 폴링해야 한다.
- 컨슈머가 서비스 액티베이터인 경우는 다음과 같이 @ServiceActivator 애노테이션을 지정할 수 있다.

```java
@ServiceActivator(inputChannel="orderChannel", poller=@Poller(fixedRate="1000"))
```

- 이 서비스 액티베이터는 orderChannel이라는 이름의 채널로부터 매 1초(또는 1,000밀리초)당 1번씩 읽을 메시지가 있는지 확인한다.

## 필터

- 필터는 통합 파이프라인의 중간에 위치할 수 있으며, 플로우의 전 단계로부터 다음 단계로의 메시지 전달을 허용 또는 불허한다.

![chapter09-03](image/chapter09-03.png '조건을 기반으로 필터는 파이프라인의 전 단계로부터 다음 단계로의 메시지 전달을 허용 또는 불허한다.')

- 예를 들어, 정수 값을 갖는 메시지가 numberChannel이라는 이름의 채널로 입력되고, 짝수인 경우만 evenNumberChannel이라는 이름의 채널로 전달된다고 해보자.
- 이 경우 다음과 같이 @Filter 애노테이션이 지정된 필터를 선언할 수 있다.

```java
@Filter(inputChannel="numberChannel", outputChannel="evenNumberChannel")
public boolean evenNumberFilter(Integer number) {
	return number % 2 == 0;
}
```

- 또는 자바 DSL 구성을 사용해서 통합 플로우를 정의한다면 다음과 같이 filter() 메서드를 호출할 수 있다.

```java
@Bean
public IntegrationFlow evenNumberFlow(AtomicInteger integerSource) {
	return IntegrationFlows
		...
		.<Integer>filter((p) -> p % 2 == 0)
		...
		.get();
}
```

- 여기서는 람다를 사용해서 필터를 구현했지만, 실제로는 filter() 메서드가 GenericSelector를 인자로 받는다.
- 이것은 우리의 필요에 따라 GenericSelector를 구현하여 다양한 조건으로 필터링할 수 있다는 것을 의미한다.

## 변환기

- 변환기는 메시지 값의 변경이나 타입을  변환하는 일을 수행한다.

![chapter09-04](image/chapter09-04.png '변환기는 통합 플로우를 거쳐가는 메시지를 변경한다.')

- 예를 들어, 정수 값을 포함하는 메시지가 numberChannel이라는 이름의 채널로 입력되고, 이 숫자를 로마 숫자를 포함하는 문자열로 변환한다고 해보자.
- 이 경우 다음과 같이 @Transformer 애노테이션을 지정하여 GenericTransformer 타입의 빈을 선언할 수 있다.

```java
@Bean
@Transformer(input="numberChannel", output="romanNumberChannel")
public GenericTransformer<Integer, String> romanNumTransformer() {
	return RomanNumbers::toRoman;
}
```

- @Transformer 애노테이션은 이 빈을 변환기 빈으로 지정한다.
- 자바 DSL 구성에서는 toRaman() 메서드의 메서드 참조를 인자로 전달하여 transform()을 호출하므로 더 쉽다.

```java
@Bean
public IntegrationFlow transformerFlow() {
	return IntegrationFlows
		...
		.transform(RomanNumbers::toRoman)
		...
		.get();
}
```

- 변환기가 자바 클래스로 만들만큼 복잡하다면, 빈으로 플로우 구성에 주입하고 이 빈의 참조를 transform() 메서드의 인자로 전달할 수 있다.

```java
@Bean
public RomanNumerTransformer romanNumberTransformer() {
	return new RomanNumberTransformer();
}
@Bean
public IntegrationFlow transflormerFlow(RomanNumerTransformer romanNumberTransformer) {
	return IntegrationFlows
		...
		.transform(romanNumberTransformer)
		...
		.get();
}
```

- 여기서는 RomanNumberTransformer 타입의 빈을 선언한다.
- 이 빈은 스프링 통합의 Transformer나 GenericTransformer 인터페이스를 구현한 것이다.

## 라우터

- 라우터는 전달 조건을 기반으로 통합 플로우 내부를 분기(서로 다른 채널로 메시지를 전달)한다.

![chapter09-05](image/chapter09-05.png '라우터는 메시지에 적용된 조건을 기반으로 서로 다른 채널로 메시지를 전달한다.')

- 예를 들어, 정수 값을 전달하는 numberChannel이라는 이름의 채널이 있다고 하자.
- 그리고 모든 짝수 메시지는 evenChannel이라는 이름의 채널로 전달하고, 홀수 메시지는 oddChannel이라는 채널로 전달한다고 가정해 보자.
- 이 라우터를 통합 플로루에 생성할 때는 @Router가 지정된 AbstractMessageRouter 타입의 빈을 선언하면 된다.

```java
@Bean
@Router(inputChannel="numberChannel")
public AbstractMessageRouter evenOddRouter() {
	return new AbstractMessageRouter() {
		@Override
		protected Collection<MessageChannel> determineTargetChannels(Message<?> message) {
			Integer number = (Integer) message.getPayload();
			if (number % 2 == 0) {
				return Collections.singleton(evenChannel());
			}
			return Collections.singleton(oddChannel());
		}
	};
}

@Bean
public MessageChannel evenChannel() {
	return new DirectChannel();
}

@Bean
public MessageChannel oddChannel() {
	return new DirectChannel();
}
```

- 자바 DSL 구성에서는 다음과 같이 플로우 정의에서 route() 메서드를 호출하여  라우터를 선언한다.

```java
@Bean
public IntegrationFlow numberRoutingFlow(AtomicInteger source) {
	return IntegrationFlows
		...
			.<Integer, String>route(n -> n % 2 == 0? "EVEN": "ODD", 
				mapping -> mapping.subFlowMapping("EVEN",
					sf -> sf.<Integer, Integer>tranform(n -> n * 10)
						.handle((i, h) -> { ... })
					).subFlowMapping("ODD",
						sf -> sf.transform(RomanNumbers::toRoman)
							.handle((i, h) -> { ... })
					)
				)
			.get();
}
```

- AbstractMessageRouter를 따로 선언하고 이것을 route()의 인자로 전달하는 것도 가능하지만, 여기서는 메시지 페이로드가 홀수나 짝수 중 어느 것인지 결정하기 위해 AbstractMessageRouter 대신 람다를 사용하였다.

## 분배기

- 때로는 통합 플로우에서 하나의 메시지를 여러 개로 분할하여 독립적으로 처리하는 것이 유용할 수 있다.

![chapter09-06](image/chapter09-06.png '분배기는 메시지가 별도의 하위 플로우(subflow)에서 처리할 수 있게 두 개 이상으로 분할한다.')

- 분배기를 사용할 수 있는 중요한 두 가지 경우가 있다.
    - **메시지 페이로드가 같은 타입의 컬렉션 항목들을 포함하며, 각 메시지 페이로드 별로 처리하고자 할 때다.** 예를 들어, 여러 가지 종류의 제품이 있으며, 제품 리스트를 전달하는 메시지는 각각 한 종류 제품의 페이로드를 갖는 다수의 메시지로 분할될 수 있다.
    - **연관된 정보를 함께 전달하는 하나의 메시지 페이로드는 두 개 이상의 서로 다른 타입 메시지로 분할될 수 있다.** 예를 들어, 주문 메시지는 배달 정보, 대금 청구 정보, 주문 항목 정보를 전달할 수 있으며, 각 정보는 서로 다른 하위 플로우에서 처리될 수 있다. 이 경우는 일반적으로 분배기 다음에 페이로드 타입 별로 메시지를 전달하는 라우터가 연결된다. 적합한 하위 플로우에서 데이터가 처리되도록 하기 위해서다.
- 하나의 메시지 페이로드를 두 개 이상의 서로 다른 타입 메시지로 분할할 때는 수신 페이로드의 각 부분을 추출하여 컬렉션의 요소들로 반환하는 POJO(Plain Old Java Object)를 정의하면 된다.
- 예를 들어, 주문 데이터를 전달하는 메시지는 대금 청구 정보와 주문 항목 리스트의 두 가지 메시지로 분할할 수 있다.

```java
public class OrderSplitter {
	public Collection<Object> splitOrderIntoParts(PurchaseOrder po) {
		ArrayList<Object> parts = new ArrayList<>();
		parts.add(po.getBillingInfo());
		parts.add(po.getLineItems());
		return parts;
	}
}
```

- 그 다음에 @Splitter 애노테이션을 지정하여 통합 플로우의 일부로 OrderSplitter 빈을 선언할 수 있다.

```java
@Bean
@Splitter(inputChannel="poChannel", outputChannel="splitOrderChannel")
public OrderSplitter orderSplitter() {
	return new OrderSplitter();
}
```

- 플로우의 이 지점에서 PayloadTypeRouter를 선언하여 대금 청구 정보와 주문 항목 정보를 각 정보에 적합한 하위 플로우에 전달할 수 있다.

```java
@Bean
@Router(inputChannel="splitOrderChannel")
public MessageRouter splitOrderRouter() {
	PayloadTypeRouter router = new PayloadTypeRouter();
	router.setChannelMapping(BillingInfo.class.getName(), "billingInfoChannel");
	router.setChannelMapping(List.class.getName(), "lineItemsChannel");
	return router;
}
```

- 이름이 암시하듯이, PayloadTypeRouter는 각 페이로드 타입을 기반으로 서로 다른 채널에 메시지를 전달한다.
- 즉, BillingInfo 타입의 페이로드는 billingInfoChannel로 전달되어 처리되며, java.util.List 컬렉션에 저장된 주문 항목(line item)들이 List 타입으로 lineItemsChannel에 전달된다.
- 여기서는 하나의 플로우가 두 개의 하위 플로우로 분할된다.
- BillingInfo 객체가 전달되는 플로우와 `List<LineItem>`이 전달되는 플로우다.
- 그러나 `List<LineItem>`을 다수의 메시지로 분할하기 위해 @Splitter 애노테이션을 지정한 메서드(빈이 아님)를 작성하고 이 메서드에서는 처리된 LineItem이 저장된 컬렉션을 반환하면 된다.

```java
@Splitter(inputChannel="lineItemsChannel", outputChannel="lineItemChannel")
public List<LineItem> lineItemSplitter(List<LineItem> lineItems) {
	return lineItems;
}
```

- 자바 DSL을 사용해서 이와 동일한 분배기/라우터 구성을 선언할 때는 다음과 같이 split()과 route() 메서드를 호출하면 된다.

```java
return IntegrationFlows
	...
		.split(orderSplitter())
		.<Object, String>route(
			p -> {
				if (p.getClass().isAssignableFrom(BillingInfo.class)) {
					return "BILLING_INFO";
				} else {
					return "LINE_ITEMS";
				}
			}, mapping -> mapping
				.subFlowMapping("BILLING_INFO",
					sf -> sf.<BillingInfo>handle((billingInfo, h) -> {
						...
					}))
				.subFlowMapping("LINE_ITEMS",
					sf -> sf.split()
						.<LineItem>handle((lineItem, h) -> {
							...
						}))
		).get();
```

## 서비스 액티베이터

- 서비스 액티베이터는 입력 채널로부터 메시지를 수신하고 이 메시지를 MessageHandler 인터페이스를 구현한 클래스(빈)에 전달한다.

![chapter09-07](image/chapter09-07.png '서비스 액티베이터는 메시지를 받는 즉시 MessageHandler를 통해 서비스를 호출한다.')

- 스프링 통합은 MessageHandler를 구현한 여러 클래스를 제공한다.
- 그러나 서비스 액티베이터의 기능을 수행하기 위해 커스텀 클래스를 제공해야 할 때가 있다.

```java
@Bean
@ServiceActivator(inputChannel="someChannel")
public MessageHandler sysoutHandler() {
	return message -> {
		System.out.println("Message payload: " + message.getPayload());
	};
}
```

- someChannel이라는 이름의 채널로부터 받은 메시지를 처리하는 서비스 액티베이터로 지정하기 위해 이 빈은 @ServiceActivator 애노테이션이 지정되었다.
- 또는 받은 메시지의 데이터를 처리한 후 새로운 페이로드를 반환하는 서비스 액티베이터를 선언할 수도 있다.
- 이 경우 이 빈을 MessageHandler가 아닌 GenericHandler를 구현한 것이어야 한다.

```java
@Bean
@ServiceActivator(inputChannel="orderChannel", outputChannel="completeChannel")
public GenericHandler<Order> orderHandler(OrderRepository orderRepo) {
	return (payload, headers) -> {
		return orderRepo.save(payload);
	};
}
```

- GenericHandler는 메시지 페이로드는 물론이고 메시지 헤더도 받는다는 것을 알아두자.
- 자바 DSL 구성으로도 서비스 액티베이터를 사용할 수 있다.
- 이때는 플로우 정의에서 handle() 메서드의 인자로 MessageHandler나 GenericHandler를 전달하면 된다.

```java
public IntegrationFlow someFlow() {
	return IntegrationFlows
		...
			.handle(msg -> {
				System.out.println("Message payload: " + msg.getPayload());
			})
			.get();
}
```

- 메시지 참조 또는 MessageHandler 인터페이스를 구현하는 클래스 인스턴스까지도 handle() 메서드의 인자로 제공할 수 있다.
- 단, 람다나 메서드 참조의 경우는 메시지를 매개변수로 받는다는 것을 알아두자.
- 만일 서비스 액티베이터를 플로우의 제일 끝에 두지 않는다면 MessageHandler의 경우와 유사하게 handle() 메서드에서 GenericHandler를 인자로 받을 수도 있다.

```java
public IntegrationFlow orderFlow(OrderRepository orderRepo) {
	return IntegrationFlows
		...
			.<Order>handle((payload, headers) -> {
				return orderRepo.save(payload);
			})
		...
			.get();
}
```

- GenericHandler를 플로우의 제일 끝에 사용한다면 null을 반환해야 한다.
- 그렇지 않으면 지정된 출력 채널이 없다는 에러가 발생할 것이다.

## 게이트웨이

- 게이트웨이는 애플리케이션이 통합 플로우로 데이터를 제출(submit)하고 선택적으로 플로우의 처리 결과인 응답을 받을 수 있는 수단이다.

![chapter09-08](image/chapter09-08.png '서비스 게이트웨이는 애플리케이션이 통합 플로우로 메시지를 전송할 수 있는 인터페이스다.')

- FileWriterGateway는 단반향 게이트웨이이며, 파일에 쓰기 위해 문자열을 인자로 받고 void를 반환하는 메서드를 갖고 있다.
- 양방향 게이트웨이의 작성도 어렵지 않으며, 이때는 게이트웨이 인터페이스를 작성할 때 통합 플로우로 전송할 값을 메서드에서 반환해야 한다.
- 예를 들어, 문자열을 받아서 모두 대문자로 변환하는 간단한 통합 플로우의 앞 쪽에 있는 게이트웨이를 생각해 보자.
- 이 게이트웨이 인터페이스는 다음과 같다.

```java
package com.example.demo;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.stereotype.Component;

@Component
@MessagingGateway(defaultRequestChannel="inChannel", defaultReplyChannel="outChannel")
public interface UpperCaseGateWay {
	String uppercase(String in);
}
```

- 놀라운 사실은 이 인터페이스를 구현할 필요가 없다는 것이다.
- 지정된 채널을 통해 데이터를 전송하고 수신하는 구현체를 스프링 통합이 런타임 시에 자동으로 제공하기 때문이다.
- 이것을 자바 DSL 구성으로 나타내면 다음과 같다.

```java
@Bean
public IntegrationFlow uppercaseFlow() {
	return IntegrationFlows.
		.from("inChannel")
		.<String, String>transform(s -> s.toUpperCase())
		.channel("outChannel")
		.get();
}
```

## 채널 어댑터

- 채널 어댑터는 통합 플로우의 입구와 출구를 나타낸다.
- 데이터는 인바운드(inbound) 채널 어댑터를 통해 통합 플로우로 들어오고, 아웃바운드(outbound) 채널 어댑터를 통해 통합 플로우에서 나간다.

![chapter09-09](image/chapter09-09.png '채널 어댑터는 통합 플로우의 입구와 출구다.')

- 인바운드 채널 어댑터는 플로우에 지정된 데이터 소스에 따라 여러 가지 형태를 갖는다.
- 예를 들어, 증가되는 숫자를 AtomicInteger로부터 플로우로 넣는 인바운드 채널 어댑터를 선언할 수 있다.

```java
@Bean
@InboundChannelAdapter(poller=@Poller(fixedRate="1000"), channel="numberChannel")
public MessageSource<Integer> numberSource(AtomicInteger source) {
	return () -> {
		return new GenericMessage<>(source.getAndIncrement());
	}
}
```

- 이 @Bean 메서드는 @InboundChannelAdapter 애노테이션이 지정되었으므로 인바운드 채널 어댑터 빈으로 선언된다.
- 자바 DSL의 경우는 from() 메서드가 인바운드 채널 어댑터의 일을 수행한다.

```java
@Bean
public IntegrationFlow someFlow(AtomicInteger integerSource) {
	return IntegrationFlows
		.from(integerSource, "getAndIncrement",
			c -> c.poller(Pollers.fixedRate(1000)))
		...
		.get();
}
```

- 종종 채널 어댑터는 스프링 통합의 여러 엔드포인트 모듈 중 하나에서 제공된다.
- 예를 들어, 지정된 디렉터리를 모니터링하여 해당 디렉터리에 저장하는 파일을 file-channel이라는 이름의 채널에 메시지로 전달하는 인바운드 채널 어댑터가 필요하다고 해보자.
- 이 경우 스프링 통합 파일 엔드포인트 모듈의 FileReadingMessageSource를 사용하는 다음의 자바 구성으로 구현할 수 있다.

```java
@Bean
@InboundChannelAdapter(channel="file-channel", poller=@Poller(fixedDelay="1000"))
public MessageSource<File> fileReadingMessageSource() {
	FileReadingMessageSource sourceReader = new FileReadingMessageSource();
	sourceReader.setDirectory(new File(INPUT_DIR));
	sourceReader.setFilter(new SimplePatternFileListFilter(FILE_PATTERN));
	return sourceReader;
}
```

- 이것과 동일한 파일-읽기 인바운드 채널 어댑터를 자바 DSL로 작성할 때는 Files 클래스의 inboundAdapter() 메서드를 사용할 수 있다.
- 아웃바운드 채널 어댑터는 통합 플로우의 끝단이며, 최종 메시지를 애플리케이션이나 다른 시스템에 넘겨준다.

```java
@Bean
public IntegrationFlow fileReaderFlow() {
	return IntegrationFlows
		.from(Files.inboundAdapter(new File(INPUT_FILE))
			.patternFilter(FILE_PATTERN))
		.get();
}
```

- 메시지 핸들러로 구현되는 서비스 액티베이터는 아웃바운드 채널 어댑터로 자주 사용된다.
    - 특히, 데이터가 애플리케이션 자체에 전달될 필요가 있을 때다.
- 그러나 몇몇 경우에 스프링 통합 엔드포인트 모듈이 유용한 메시지 핸들러를 제공한다는 것을 알아 둘 필요가 있다.

## 엔드포인트 모듈

- 다양한 외부 시스템과의 통합을 위해 채널 어댑터가 포함된 24개 이상의 엔드 포인트 모듈(인바운드와 아웃바운드 모두)을 스프링 통합이 제공한다.
- 스프링 통합은 외부 시스템과의 통합을 위한 24개 이상의 엔드포인트 모듈 제공

| 모듈 | 의존성 ID(Group ID: org.springframework.integration) |
| --- | --- |
| AMQP | spring-integration-amqp |
|스프링 애플리케이션 이벤트 | spring-integration-event |
| RSS와 Atom | spring-integration-feed |
| 파일 시스템 | spring-integration-file |
| FTP/FTPS | spring-integration-ftp |
| GemFire | spring-integration-gemfire |
| HTTP | spring-integration-http |
| JDBC | spring-integration-jdbc |
| JPA | spring-integration-jpa |
| JMS | spring-integration-jms |
| 이메일 | spring-integration-mail |
| MongoDB | spring-integration-mongodb |
| MQTT | spring-integration-mqtt |
| Redis | spring-integration-redis |
| RMI | spring-integration-rmi |
| SFTP | spring-integration-sftp |
| STOMP | spring-integration-stomp |
| 스트림 | spring-integration-stream |
| Syslog | spring-integration-syslog |
| TCP/UDP | spring-integration-ip |
| Twitter | spring-integration-twitter |
| 웹 서비스 | spring-integration-ws |
| WebFlux | spring-integration-webflux |
| WebSocket | spring-integration-websocket |
| XMPP | spring-integration-xmpp |
| ZooKeeper | spring-integration-zookeeper |

- 스프링 통합은 여러 가지 통합 요구를 충족시키기 위해 광범위한 컴포넌트들을 제공한다.
- 각 엔드포인트 모듈은 채널 어댑터를 제공하며, 채널 어댑터는 자바 구성을 사용해 빈으로 선언되거나, 자바 DSL 구성을 사용해 static 메서드로 참조할 수 있다.

# 이메일 통합 플로우 생성하기

- 여기서는 타코 클라우드 받은 편지함(inbox)의 타코 주문 이메일을 지속적으로 확인하여 이메일의 주문 명세를 파싱한 후 해당 주문 데이터의 처리를 위해 타코 클라우드에 제출하는 통합 플로우를 구현할 것이다.
- 그러면 우선, 타코 클라우드 이메일을 처리하는 방법의 세부 사항을 캡쳐하기 위해 간단한 구성 속성을 정의한다.

```java
@Data
@ConfigurationProperties(prefix="tacocloud.email")
@Component
public class EmailProperties {
	private String username;
	private String password;
	private String host;
	private String mailbox;
	private long pollRate = 30000;

	public String getImapUrl() {
		return String.format("imaps://%s:%s@%s/%s",
			this.username, this.password, this.host, this.mailbox);
	}
}
```

- 이 코드를 보면 알 수 있듯이,  EmailProperties는 IMAP URL에 사용되는 속성들을 갖는다.
- EmailProperties 클래스에는 tacocloud.email로 설정된 prefix 속성을 갖는 @ConfigurationProperties 애노테이션이 지정되었다.
- 따라서 이메일을 읽는데 필요한 명세를 다음과 같이 application.yml 파일에 구성할 수 있다.

```yaml
tacocloud:
	email:
		host: imap.tacocloud.com
		mailbox: INBOX
		username: taco-in-flow
		password: 1L0v3T4c0s
		poll-rate: 10000
```

![chapter09-10](image/chapter09-10.png '이메일로 타코 주문을 받기 위한 통합 플로우')

- 이 플로우를 정의할 때 다음 두 가지 중 하나를 선택할 수 있다.
    - **플로우를 타코 클라우드 애플리케이션 자체에 정의한다** : 이 경우 타코 주문 데이터를 생성하기 위해 정의했던 리퍼지터리(인터페이스 구현 클래스와 메서드)들을 플로우의 끝에서 서비스 액티베이터가 호출할 것이다.
    - **플로우를 별도의 애플리케이션으로 정의한다** : 이 경우 서비스 액티베이터가 타코 클라우느 API에 POST 요청을 전송하여 타코 주문 데이터를 제출할 것이다.
- 서비스 액티베이터가 구현되는 방법 외에는 어느 것을 선택하든 플로우 자체와는 무관하다.
- 그러나 기존 도메인 타입과의 혼선을 피하기 위해 별도 애플리케이션에 통합 플로우를 정의하여 진행할 것이다.
- 여기서는 자바 DSL 구성을 사용하겠지만, 원한다면 다른 구성을 사용해도 좋다.

```java
package tacos.email;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;

@Configuration
public class TacoOrderEmailIntegrationConfig {
	@Bean
	public IntegrationFlow tacoOrderEmailFlow(
		EmailProperties emailProps,
		EmailToOrderTransformer emailToOrderTransformer,
		OrderSubmitMessageHandler orderSubmitHandler) {
		return IntegrationFlows
			.from(Mail.imapInboundAdapter(emailProps.getImapUrl()),
				e -> e.poller(Pollers.fixedDelay(emailProps.getPollRate())))
			.transform(emailToOrderTransformer)
			.handle(orderSubmitHandler)
			.get();
	}
}
```

- tacoOrderEmailFlow() 메서드에 정의된 타코 주문 이메일 플로우는 3개의 서로 다른 컴포넌트로 구성된다.
    - **IMAP 이메일 인바운드 채널 어댑터** : 이 채널 어댑터는 EmailProperties의 getImapUrl() 메서드로부터 생성된 IMAP URL로 생성되며, EmailProperties의 pollRate 속성에 설정된 지연 시간이 될 때마다 이메일을 확인한다. 받은 이메일은 변환기에  연결하는 채널로 전달된다.
    - **이메일을 Order 객체로 변환하는 변환기** : 이 변환기는 tacoOrderEmailFlow() 메서드로 주입되는 EmailToOrderTransformer에 구현된다. 변환된 주문 데이터(Order 객체)는 다른 채널을 통해 최종 컴포넌트로 전달된다.
    - **핸들러(아웃바운드 채널 어댑터로 작동)** : 핸들러는 Order 객체를 받아서 타코 클라우드의 REST API로 제출한다.
- Mail.imapInboundAdapter() 호출을 가능하게 하려면 Email 엔드포인트 모듈의 의존성을 프로젝트 빌드에 추가해야 한다.

```xml
<dependency>
	<groupId>org.springframework.integration</groupId>
	<artifactId>spring-integration-mail</artifactId>
</dependency>
```

```java
@Component
public class EmailToOrderTransformer extends AbstractMailMessageTransformer<Order> {
	@Override
	protected AbstractIntegrationMessageBuilder<Order> doTransform(Message mailMessage) throws Exception {
		Order tacoOrder = processPayload(mailMessage);
		return MessageBuilder.withPayload(tacoOrder);
	}
	...
}
```

- AbstractMailMessageTransformer는 페이로드가 이메일인 메시지를 처리하는 데 편리한 베이스 클래스다.
- 입력 메시지로부터 이메일 정보를 Message 객체(doTransform() 메서드의 인자로 전달)로 추출하는 일을 지원한다.
- doTransform() 메서드에서는 Message 객체를 private 메서드인 processPayload()의 인자로 전달하여 이메일을 Order 객체로 파싱한다.

```java
package tacos.email;

import java.util.ArrayList;
import java.uitl.List;
import lombok.Data;

@Data
public class Order {
	private final String email;
	private List<Taco> tacos = new ArrayList<>();

	public void addTaco(Taco taco) {
			this.tacos.add(taco);
	}
}
```

- 이 Order 클래스는 고객의 배달 정보와 대금 청구 정보를 갖지 않고 입력 이메일에서 얻는 고객의 이메일 정보만 갖는다.
- 이메일을 Order 객체로 파싱하는 것은 간단한 작업이 아니다.
- 실제로 수십 줄의 코드가 필요하며, 이 코드들은 스프링 통합이나 변환기 구현과는 관계가 없다.
- 따라서 공간을 절약하기 위해 processPayload() 메서드의 자세한 설명은 생략한다.
- EmailToOrderTransformer가 마지막으로 하는 일은 Order 객체를 포함하는 페이로드를 갖는 MessageBuilder를 반환하는 것이다.
- 그리고 MessageBuilder에 의해 생성된 메시지는 통합 플로우의 마지막 컴포넌트인 메시지 핸들러(타코 클라우드의 API로 해당 주문을 POST하는)로 전달된다.
- OrderSubmitMessageHandler는 스프링 통합의 GenericHandler를 구현하여 Order 페이로드를 갖는 메시지를 처리한다.

```java
package tacos.email;

import java.util.Map;
import org.springframework.integration.Handler.GenericHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OrderSubmitMessageHandler implements GenericHandler<Order> {
	private RestTemplate rest;
	private ApiProperties apiProps;

	public OrderSubmitMessageHandler(ApiProperties apiProps, RestTemplate rest) {
		this.apiProps = apiProps;		
		this.rest = rest;
	}

	@Override
	public Object handle(Order order, Map<String, Object> headers) {
		rest.postForObject(apiProps.getUrl(), order, String.class);
		return null;
	}
}
```

- GenericHandler 인터페이스의 요구사항을 충족하기 위해 OrderSubmitMessageHandler는 handle() 메서드를 오버라이딩한다.
- 메서드는 입력된 Order 객체를 받으며, 주입된 RestTemplate을 사용해서 주문(Order 객체)을 제출한다.
    - 주입된 ApiProperties 객체에 캡처된 URL로 POST 요청을 한다.
- 끝으로, 이 핸들러가 플로우의 제일 끝이라는 것을 나타내기 위해 handle() 메서드가 null을 반환한다.
- ApiProperties는 URL의 하드코딩을 피하기 위해 postForObject() 호출에 사용되었으며, 이것은 다음과 같은 구성 속성 파일이다.

```java
@Data
@ConfigurationProperties(prefix="tacocloud.api")
@Component
public class ApiProperties {
	private String url;
}
```

- 그리고 application.yml에는 타코 클라우드 API의 URL을 다음과 같이 구성할 수 있다.

```yaml
tacocloud:
	api:
		url: http://api.tacocloud.com
```

- RestTemplate이 OrderSubmitMessageHandler에 주입되어 프로젝트에서 사용될 수 있게 하려면 스프링 부트 웹 스타터를 프로젝트에 추가해야 한다.

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

- 독립 실행형(standalone)의 스프링 통합 플로우의 경우는 애플리케이션에서 스프링 MVC 또는 자동-구성이 제공하는  내장된 톰캣(Tomcat)조차도 필요 없다.
- 따라서 다음과 같이 application.yml에서 스프링 MVC 자동-구성을 비활성화해야 한다.

```yaml
spring:
	main:
		web-application-type: none;
```

- spring.main.web-application-type 속성은 sevlet, reactive, none 중 하나로 설정할 수 있다.
- 스프링 MVC가 classpath에 있을 때는 이 속성 값을 자동-구성이 servlet으로 설정한다.
- 그러나 여기서는 스프링 MVC와 톰캣이 자동-구성되지 않도록 none으로 변경하였다.

# 타코 클라우드 애플리케이션 빌드 및 실행하기

- 여기에는 두 개의 프로젝트가 있다.
    - simple-flow는 타코 클라우드와 관계없는 스프링 부트 프로젝트이며, 파일 통합 플로우 코드를 포함하고 있다.
    - 그리고 taco-cloud에 하나의 메이븐 프로젝트로 구성된 타코 클라우드의 모듈이 포함되어 있다.
        - 그리고 'tacocloud-email' 모듈이 새로 추가된 것이며, 이메일 통합 코드를 포함한다.
- 애플리케이션이 시작되면서 실행되는 SimleFlowApplication의 writeData() 빈에서 writeToFile()을 호출하여 simple.txt라는 이름의 파일에 쓴다.
    - 이 파일은 /tmp/sia5/files에 저장된다.
    - 예를 들어, 윈도우 시스템의 경우는 C:\tmp\sia5\files
- 따라서 simple-flow 애플리케이션을 실행하려면 우선 원하는 프로파일을 설정해야 한다.
- 여기서는 JVM 옵션을 사용해서 실행해 본다.
    - 운영체제의 환경 변수를 사용할 때는 export SPRING_PROFILES_ACTIVE=xmlconfig와 같이 하면 된다.

```bash
$ java -Dspring.profiles.active=xmlconfig -jar target/simple-flow-0.0.9-SNAPSHOT.jar
```

- simple-flow 애플리케이션의 실행이 끝난 후 조금 앞 쪽에 있는 로그 메시지의 오른쪽을 보면 Channel 'application.textInChannel' has 1 subscribe(s)와 Channel 'application.fileWriterChannel' has 1 subscriber(s) 및 started fileWriterGateway로 나타난 메시지가 있을 것이다.
- 이 시점에서 /tmp/sia5/files/simple.txt 파일을 열어 보면 'HELLO, SPRING INTEGRATION! (XMLCONFIG)'가 저장된 것을 알 수 있다.
- taco-cloud를 빌드하고 실행할 때는 이메일 통합 모듈(tacocloud-email)을 사용하기 위해 주문 메일을 수신하는 IMAP 메일 서버를 설치하는 것이 좋다.
- 주문 메일로 보내느 데이터의 형식은 tacocloud-email/src/main/java/tacos/email/EmailToOrderTransformer.java의 parseEmailToOrder() 메서드를 보면 알 수 있다.
- 즉, 타코 이름 바로 다음에 콜론(:)을 붙이며, 이 다음에는 하나 이상의 식자재 이름들을 쉼표(,)로 구분하여 추가하면 된다.
    - 예를 들어, MyTaco:FLOUR TORTILLA, GROUND BEEF
    - 만일 식자재 이름이 틀린 경우는 해당 식자재만 무시된다.