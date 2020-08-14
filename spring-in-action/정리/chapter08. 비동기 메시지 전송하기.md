- 비동기(asynchronous) 메시징은 애플리케이션 간에 응답을 기다리지 않고 간접적으로 메시지를 전송하는 방법이다.
- 따라서 통신하는 애플리케이션 간의 결합도를 낮추고 확장성을 높여준다.

# JMS로 메시지 전송하기

- JMS는 두 개 이상의 클라이언트 간에 메시지 통신을 위한 공통 API를 정의하는 자바 표준이다.
- JMS가 나오기 전에는 클라이언트 간에 메시지 통신을 중개하는 메시지 브로커(broker)들이 나름의 API를 갖고  있어서 애플리케이션의 메시징 코드가 브로커 간에 호환될 수 없었다.
- 그러나 JMS를 사용하면 이것을 준수하는 모든 구현 코드가 공통 인터페이스를 통해 함께 작동할 수 있다.
- 스프링 JmsTemplate이라는 템플릿 기반의 클래스를 통해 JMS를 지원한다.
- JmsTemplate을 사용하면 프로듀서(producer)가 큐와 토픽에 메시지를 전송하고 컨슈머(consumer)는 그 메시지들을 받을 수 있다.
- 스프링은 메시지 기반의 POJO도 지원한다.
- POJO는 큐나 토픽에 도착하는 메시지에 반응하여 비동기 방식으로 메시지를 수신하는 간단한 자바 객체다.

## JMS 설정하기

- JMS를 사용할 수 있으려면 JMS 클라이언트를 우리 프로젝트의 빌드에 추가해야 한다.
- 우선 아파치 ActiveMQ 또는 더 최신의 아파치 ActiveMQ Artemis(아르테미스) 중 어느 브로커를 사용할지 결정해야 한다.

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-activemq</artifactId>
</dependency>

<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-artemis</artifactId>
</dependency>
```

- Artemis는 ActiveMQ를 새롭게 다시 구현한 차세대 브로커다.
- 어떤 브로커를 선택하든 메시지를 송수신하는 코드 작성 방법에는 영향을 주지 않으며, 브로커에 대한 연결을 생성하기 위해 스프링을 구성하는 방법만 다르다.
- 기본적으로 스프링은 Artemis 브로커가 localhost의 61616 포트를 리스닝하는 것으로 간주한다.
- 실무 환경으로 애플리케이션을 이양할 때는 브로커를 어떻게 사용하는지 스프링에게 알려주는 몇 가지 속성을 설정해야 한다.
- Artemis 브로커의 위치와 인증 정보를 구성하는 속성

| 속성 | 설명 |
| --- | --- |
| spring.artemis.host | 브로커의 호스트 |
| spring.artemis.port | 브로커의 포트 |
| spring.artemis.user | 브로커를 사용하기 위한 사용자(선택 속성) |
| spring.artemis.password | 브로커를 사용하기 위한 사용자 암호(선택 속성) |

```yaml
spring:
	artemis:
		host: artemis.tacocloud.com
		port: 61617
		user: tacoweb
		password: l3tm31n
```

- 이것은 artemis.tacocloud.com의 61617 포트를 리스닝하는 브로커에 대한 연결을 생성하기 위해 스프링을 설정한다.
- 또한, 이 브로커와 상호작용할 애플리케이션의 인증 정보도 같이 설정한다.
- 인증 정보는 선택이지만, 실무 환경에서는 설정하는 것이 좋다.
- ActiveMQ 브로커의 위치와 인증 정보를 구성하는 속성

| 속성 | 설명 |
| --- | --- |
| spring.activemq.broker-url | 브로커의 URL |
| spring.activemq.user | 브로커를 사용하기 위한 사용자(선택 속성) |
| spring.activemq.password | 브로커를 사용하기 위한 사용자 암호(선택 속성) |
| spring.activemq.in-memory | 인메모리 브로커로 시작할 것인지의 여부(기본값은 true) |

- 브로커의 호스트 이름과 포트를 별개의 속성으로 설정하는 대신, ActiveMQ의 브로커 주소는 spring.activemq.broker-url 속성 하나로 지정한다.
- 그리고 다음의 YAML에 지정된 것처럼 URL은 tcp://URL의 형태로 지정해야 한다.

```yaml
spring:
	artemis:
		broker-url: tcp://activemq.tacocloud.com
		user: tacoweb
		password: l3tm31n
```

- Artemis나 ActiveMQ 중 어느 것을 선택하든 브로커가 로컬에서 실행되는 개발 환경에서는 앞의 속성들을 구성할 필요가 없다.
- 그러나 ActiveMQ를 사용할 때는 스프링이 인메모리 브로커로 시작하지 않도록 spring.activemq.in-memory 속성을 false로 설정해야 한다.
- 왜냐하면 인메모리 브로커가 유용한 것처럼 보일 수 있지만, 같은 애플리케이션에서 메시지를 쓰고 읽을 때만 유용하므로 사용에 제약이 따르기 때문이다.
- 스프링에 내장된 브로커를 사용하는 대신 Artemis(또는 ActiveMQ) 브로커를 따로 설치하고 시작시킬 수도 있다.
    - Artemis : [https://activemq.apache.org/artemis/docs/latest/using-server.html](https://activemq.apache.org/artemis/docs/latest/using-server.html)
    - ActiveMQ : [http://activemq.apache.org/getting-started.html#GettingStarted-PreInstallationRequirements](http://activemq.apache.org/getting-started.html#GettingStarted-PreInstallationRequirements)

## JmsTemplate을 사용해서 메시지 전송하기

- JmsTemplate은 스프링 JMS 통합 지원의 핵심이다.
- 스프링의 다른 템플릿 기반 컴포넌트와 마찬가지로, JmsTemplate은 JMS로 작업하는 데 필요한 코드를 줄여준다.
- 만일 JmsTemplate이 없다면 메시지 브로커와의 연결 및 세션을 생성한느 코드는 물론이고, 메시지를 전송하는 도중 발생하는 예외를 처리하는 수많은 코드도 우리가 작성해야 한다.
- JmsTemplate은 다음을 비롯해서 메시지 전송에 유용한 여러 메서드를 갖고 있다.

```java
// 원시 메시지를 전송한다.
void send(MessageCreator messageCreator) throws JmsException;
void send(Destination destination, MessageCreator messageCreator) throws JmsException;
void send(String destinationName, MessageCreator messageCreator) throws JmsException;

// 객체로부터 변환된 메시지를 전송한다.
void convertAndSend(Object message) throws JmsException;
void convertAndSend(Destination destination, Object message) throws JmsException;
void convertAndSend(String destinationName, Object message) throws JmsException;

// 객체로부터 변환되고 전송에 앞서 후처리(post-processing)되는 메시지를 전송한다.
void convertAndSend(Object message, MessagePostProcessor postProcessor) throws JmsException;
void convertAndSend(Destination destination, Object message, 
		MessagePostProcessor postProcessor) throws JmsException;
void convertAndSend(String destinationName, Object message, 
		MessagePostProcessor postProcessor) throws JmsException;
```

- 실제로는 send()와 convertAndSend()의 두 개 메서드만 있으며, 다음 내용을 보면 이 메서드들이 하는 일을 이해하는 데 도움이 될 것이다.
    - 제일 앞의 send() 메서드 3개는 Message 객체를 생성하기 위해 MessageCreator를 필요로 한다.
    - 중간의 convertAndSend() 메서드 3개는 Object 타입 객체를 인자로 받아 내부적으로 Message 타입으로 변환한다.
    - 제일 끝의 convertAndSend() 메서드 3개는 Object 타입 객체를 Message 타입으로 변환한다. 그러나 메시지가 전송되기 전에 Message의 커스터마이징을 할 수 있도록 MessagePostProcessor도 인자로 받는다.
- 게다가 이들 3개의 메서드 부류 각각은 3개의 오버로딩된 메서드로 구성되며, 이 메서드들은 JMS 메시지의 도착지(destination), 즉 메시지를 쓰는 곳(큐 또는 토픽)을 지정하는 방법이 다르다.
    - 첫 번째 메서드는 도착지 매개변수가 없으며, 해당 메시지를 기본 도착지로 전송한다.
    - 두 번째 메서드는 해당 메시지의 도착지를 나타내는 Destination 객체를 인자로 받는다.
    - 세 번째 메서드는 해당 메시지의 도착지를 나타내는 문자열(String 타입)을 인자로 받는다.

```java
package tacos.messaging;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.sterotype.Service;

@Service
public class JmsOrderMessagingService implements OrderMessagingService {
	private JmsTemplate jms;

	@Autowired
	public JmsOrderMessagingService(JmsTemplate jms) {
		this.jms = jms;
	}

	@Override
	public void sendOrder(Order order) {
		jms.send(new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(order);
				}
		});
	}
}
```

- sendOrder() 메서드에는 MessageCreator 인터페이스를 구현한 익명의 내부 클래스를 인자로 전달하여 jms.send()를 호출한다.
- 그리고 익명의 내부 클래스 createMessage()를 오버리이딩하여 전달된 Order 객체로부터 새로운 메시지를 생성한다.
- MessageCreator는 함수형 인터페이스
    - functional interface이므로 람다로 나타낼 수 있다.

        ```java
        @Override
        public void sendOrder(Order order) {
        	jms.send(session -> session.createObjectMessage(order));
        }
        ```

- 그러나 jms.send()는 메시지의 도착지를 지정하지 않으므로 이 코드가 제대로 실행되게 하려면 기본 도착지(큐 또는 토픽) 이름을 spring.jms.template.default-destination 속성에 지정해야 한다.
- 다음과 같이 application.yaml 파일에 이 속성을 지정할 수 있다.

```yaml
spring:
	jms:
		template:
			default-destination: tacocloud.order.queue
```

- 대부분의 경우에는 이처럼 기본 도착지를 사용하는 것이 가장 쉬운 방법이다.
- 그러나 기본 도착지가 아닌 다른 곳에 메시지를 전송해야 한다면 send() 메서드의 매개변수로 도착지를 지정해야 한다.
- 이렇게 하는 한 가지 방법은 send()의 첫 번째 매개변수로 Destination 객체를 전달하는 것이다.
- 이 경우 Destination 빈을 선언하고 메시지 전송을 수행하는 빈에 주입하면 된다.

```java
@Bean
public Destination orderQueue() {
	return new ActiveMQQueue("tacocloud.order.queue");
}
```

- 이 Destination 빈이 JmsOrderMessagingService에 주입되면 send()를 호출할 때 이 빈을 사용하여 메시지 도착지를 지정할 수 있다.

```java
private Destination orderQueue;

@Autowired
public JmsOrderMessagingService(JmsTemplate jms, Destination orderQueue) {
	this.jms = jms;
	this.orderQueue = orderQueue;
}

...

@Override
public void sendOrder(Order order) {
	jms.send(orderQueue, session -> session.createObjectMessage(order));
}
```

- 이와 같이 Destination 객체를 사용해서 메시지 도착지를 지정하면 도착지 이름만 지정하는 것보다 더 다양하게 도착지를 구성할 수 있다.
- 그러나 실제로는 도착지 이름 외에 다른 것을 지정하는 일이 거의 없을 것이므로, 다음과 같이 send()의 첫 번째 인자로 Destination 객체 대신 도착지 이름만 지정하는 것이 더 쉽다.

```java
@Override
public void sendOrder(Order order) {
	jms.send("tacocloud.order.queue", session -> session.createObjectMessage(order));
}
```

- Message 객체를 생성하는 MessageCreator를 두 번째 인자로 전달해야 하므로 코드가 조금 복잡하다.

### 메시지 변환하고 전송하기

- JmsTemplate의 convertAndSend() 메서드는 MessageCreator를 제공하지 않아도 되므로 메시지 전송이 간단하다.

```java
@Override
public void sendOrder(Order order) {
	jms.convertAndSend("tacocloud.order.queue", order);
}
```

### 메시지 변환기 구현하기

- MessageConverter는 스프링에 정의된 인터페이스이며, 두 개의 메서드만 정의되어 있다.

```java
public interface MessageConverter {
	Message toMessage(Object object, Session session) 
			throws JMSException, MessageConversionException;
	Object fromMessage(Message message);
}
```

- 이 인터페이스는 간단해서 구현하기 쉽지만, 우리가 구현하지 않아도 된다.
- 공통적인 변환 작업을 해주는 스프링 메시지 변환기(모두 org.springframework.jms.support.converter 패키지에 있음)

| 메시지 변환기 | 하는 일 |
| --- | --- |
| MappingJackson2MessageConverter | Jackson 2 JSON 라이브러리를 사용해서 메시지를 JSON으로 상호 변환한다. |
| MarshallingMessageConverter | JAXB를 사용해서 메시지를 XML로 상호 변환한다. |
| MessagingMessageConverter | 수신된 메시지의 MessageConverter를 사용해서 해당 메시지를 Message 객체로 상호 변환한다. 또는 JMS 헤더와 연관된 JmsHeaderMapper를 표준 메시지 헤더로 상호 변환한다. |
| SimpleMessageConverter | 문자열 TextMessage로, byte 배열을 BytesMessage로, Map을 MapMessage로, Serializable 객체를 ObjectMessage로 상호 변환한다. |

- 기본적으로 SimpleMessageConverter가 사용되며, 이 경우 전송될 객체가 Serializable 인터페이스를 구현하는 것이어야 한다.
- 이 메시지 변환기를 사용하는 것이 좋지만, Serializable 인터페이스를  구현해야 한다는 제약을 피하기 위해 MappingJackson2MessageConverter와 같은 다른 메시지 변환기를 사용할 수도 있다.
- 다른 메시지 변환기를 적용할 때는 해당 변환기의 인스턴스를 빈으로 선언만 하면 된다.

```java
@Bean
public MappingJackson2MessageConverter messageConverter() {
	MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
	messageConverter.setTypeIdPropertyName("_typeId");
	return messageConverter;
}
```

- 이 경우 MappingJackson2MessageConverter의 setTypeIdPropertyName() 메서드를 호출한 후 이 메시지 변환기 인스턴스를 반환한다는 것에 유의하자.
- 수신된 메시지의 변환 타입을 메시지 수신자가 알아야 하기 때문에 이 부분이 매우 중요하다.
- 여기에는 변환되는 타입의 클래스 이름(패키지 전체 경로가 포함된)이 포함된다.
- 그러나 이것은 유연성이 다소 떨어진다.
- 메시지 수신자도 똑같은 클래스(패지키 전체 경로까지 동일한)와 타입을 가져야 하기 때문이다.
- 따라서 유연성을 높이기 위해 메시지 변환기의 setTypeIdMappings()를 호출하여 실제 타입에 임의의 타입 이름을 매핑시킬 수 있다.

```java
@Bean
public MappingJackson2MessageConverter messageConverter() {
	MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
	messageConverter.setTypeIdPropertyName("_typeId");

	Map<String, Class<?>> typeIdMappings = new HashMap<> ();
	typeIdMappings.put("order", Order.class);
	messageConverter.setTypeIdMappings(typeIdMappings);
	return messageConverter;
}
```

- 이 경우 해당 메시지의 _typeId 속성에 전성되는 클래스 이름(패키지 전체 경로가 포함된) 대신 order 값이 전송된다.

### 후처리 메시지

- 만일 send() 메서드를 사용해서 타코 주문을 전송한다면 Message 객체의 setStringProperty()를 호출하면 된다.

```java
jms.send("tacocloud.order.queue",
	session -> {
			Message message = session.createObjectMessage(order);
			message.setStringProperty("X_ORDER_SOURCE", "WEB");
	});
```

- 그러나 send()가 아닌 convertAndSend()를 사용하면 Message 객체가 내부적으로 생성되므로 우리가 접근할 수 없다.
- convertAndSend()의 마지막 인자로 MessagePostProcessor를 전달하면 Message 객체가 생성된 후 이 객체에 우리가 필요한 처리를 할 수 있다.

```java
jms.convertAndSend("tacocloud.order.queue", order, new MessagePostProcessor() {
	@Override
	public Message postProcessMessage(Message message) throws JMSException {
		message.setStringProperty("X_ORDER_SOURCE", "WEB");
		return message;
	}
});
```

- 여기서 MessagePostProcessor는 함수형 인터페이스다.
    - 따라서 다음과 같이 익명의 내부 클래스를 람다로 교체하여 간결하게 만들 수 있다.

        ```java
        jms.convertAndSend("tacocloud.order.queue", order,
        	message -> {
        		message.setStringProperty("X_ORDER_SOURCE", "WEB");
        		return message;
        	});
        ```

- 여러 개의 다른 convertAndSend() 호출에서 동일한 MessagePostProcessor를 사용한다면, 다음 과같이 람다보다는 메서드 참조를 사용하면 불필요한 코드 중복을 막을 수 있어서 더 좋다.

    ```java
    @GetMapping("/convertAndSend/order")
    public String convertAndSendOrder() {
    	Order order = buildOrder();
    	jms.convertAndSend("tacocloud.order.queue", order, this::addOrderSource);
    	return "Convert and send order";
    }

    private Message addOrderSource(Message message) throws JMSException {
    	message.setStringProperty("X_ORDER_SOURCE", "WEB");
    	return message;
    }
    ```

## JMS 메시지 수신하기

- 메시지를 수신하는 방식에는 두 가지가 있다.
- 우리 코드에서 메시지를 요청하고 도착할 때까지 기다리는 풀 모델(pull model)과 메시지가 수신 가능하게 되면 우리 코드로 자동 전달하는 푸시 모델(push model)이다.
- JmsTemplate은 모든 메서드가 풀 모델을 사용한다.
- 따라서 이 메서드 중 하나를 호출하여 메시지를 요청하면 스레드에서 메시지를 수신할 수 있을 때까지 기다린다.
    - 바로 수신될 수도 있고 또는 약간 시간이 걸릴 수도 있다.
- 이와는 달리 푸시 모델을 사용할 수도 있으며, 이때는 언제든 메시지가 수신 가능할 때 자동 호출되는 메시지 리스너를 정의한다.
- 스레드의 실행을 막지 않으므로 일반적으로는 푸시 모델이 좋은 선택이다.

### JmsTemplate을 사용해서 메시지 수신하기

- JmsTemplate은 브로커로부터 메시지를 가져오는 여러 개의 메서드를 제공한다.

```java
Message receive() throws JmsException;
Message receive(Destination destination) throws JmsException;
Message receive(String destinationName) throws JmsException;

Object receiveAndConvert() throws JmsException;
Object receiveAndConvert(Destination destination) throws JmsException;
Object receiveAndConvert(String destinationName) throws JmsException;
```

- 이 메서드들은 메시지를 전송하는 JmsTemplate의 send()와 convertAndSend() 메서드에 대응한다.
- receive() 메서드는 원시(변환되지 않은) 메시지를 수신하는 반면, receiveAndConvert() 메서드는 메시지를 도메인 타입으로 변환하기 위해 구성된 메시지 변환기를 사용한다.
- 실제 사용하는 방법을 알기 위해 tacocloud.order.queue 도착지로부터 Order 객체를 가져오는 코드를 작성해 보자.

```java
package tacos.kitchen.messaging.jms;

import javax.jms.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

@Component
public class JmsOrderReceiver implements OrderReceiver {
	private JmsTemplate jms;
	private MessageConverter converter;

	@Autowired
	public JmsOrderReceiver(JmsTemplate jms, MessageConverter converter) {
		this.jms = jms;
		this.converter = converter;
	}

	public Order receiveOrder() {
		Message message = jms.receive("tacocloud.order.queue");
		return (Order) converter.fromMessage(message);
	}
}

```

- 주입된 메시지 변환기를 사용하여 receive() 메서드가 반환한 수신 메시지를 Order 객체로 변환한다.
- 수신 메시지의 타입 ID 속성은 해당 메시지를 Order 객체로 변환하라고 알려준다.
- 그러나 변환된 객체의 타입은 Object이므로 캐스팅한 후 반환해야 한다.
- 메시지의 속성과 헤더를 살펴봐야 할 때는 이처럼 원시 Message 객체를 메시지로 수신하는 것이 유용할 수 있다.
- 그러나 메시지의 그런 메타데이터는 필요 없고 페이로드(payload, 메시지에 적재된 순수한 데이터로 예를 들어, Order 객체)만 필요할 때가 있다.
- 이 경우 두 단계의 절차로 페이로드를 도메인 타입으로 변환하며, 메시지 변환기가 해당 컴포넌트에 주입되어야 한다.
- 메시지의 페이로드만 필요할 때는 receiveAndConvert()를 사용하는 것이 더 간단하다.

```java
package tacos.kitchen.messaging.jms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class JmsOrderReceiver implements OrderReceiver {
	private JmsTemplate jms;

	@Autowired
	public JmsOrderReceiver(JmsTemplate jms) {
		this.jms = jms;
	}

	public Order receiveOrder() {
		return (Order) jms.receiveAndConvert("tacocloud.order.queue");
	}
}

```

- 다음은 JMS 리스너를 선언하여 어떻게 푸시 모델이 처리되는지 알아보자.

### 메시지 리스너 선언하기

- receive()나 receiveAndConvert()를 호출해야 하는 풀 모델과 달리, 메시지 리스너는 메시지가 도착할 때까지 대기하는 수동적 컴포넌트다.
- JMS 메시지에 반응하는 메시지 리스너를 생성하려면 컴포넌트의 메서드에 @JmsListener를 지정해야 한다.

```java
package tacos.kitchen.messaging.jms.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class OrderListener {
	private KitchenUI ui;

	@Autowired
	public OrderListener(KitchenUI ui) {
		this.ui = ui;
	}

	@JmsListener(destination = "tacocloud.order.queue")
	public void receiveOrder(Order order) {
		ui.displayOrder(order);
	}
}

```

- receiveOrder() 메서드에는 tacocloud.order.queue 도착지의 메시지를 '리스닝'하기 위해 @JmsListener 애노테이션이 지정되었다.
- 대신에 스프링의 프레임워크 코드가 특정 도착지에 메시지가 도착하는 것을 기다리다가 도착하면 해당 메시지에 적재된 Order 객체가 인자로 전달되면서 receiveOrder() 메서드가 자동 호출된다.
- 메시지 리스너는 중단 없이 다수의 메시지를 빠르게 처리할 수 있어서 좋은 선택이 될 때가 있다.
- 메시지 처리기가 자신의 시간에 맞추어 더 많은 메시지를 요청할 수 있어야 한다면 JmsTemplate이 제공하는 풀 모델이 더 적합할 것이다.
- JMS 표준 자바 명세에 정의되어 있고 여러 브로커에서 지원되므로 자바의 메시징에 많이 사용된다.
- 그러나 JMS는 몇 가지 단점이 있으며, 그중에서 가장 중요한 것은 JMS가 자바 명세이므로 자바 애플리케이션에서만 사용할 수 있다는 것이다.

# RabbitMQ와 AMQP 사용하기

- AMQP의 가장 중요한 구현이라 할 수 있는 RabbitMQ는 JMS보다 더 진보된 메시지 라우팅 전략을 제공한다.
- JMS 메시지가 수신자가 가져갈 메시지 도착지의 이름을 주소로 사용하는 반면, AMQP 메시지는 수신자가 리스닝하는 큐와 분리된 거래소(exchange) 이름과 라우팅 키를 주소로 사용한다.

![chapter08-01](image/chapter08-01.png 'RabbitMQ 거래소로 전송되는 메시지는 라우팅 키와 바인딩을 기반으로 하나 이상의 큐로 전달된다.')

- 거래소 타입, 거래소와 큐 간의 바인딩, 메시지의 라우팅 키 값을 기반으로 처리한다.
- 다음을 포함해서 여러 종류의 거래소가 있다.
    - 기본(Default) : 브로커가 자동으로 생성하는 특별한 거래소. 해당 메시지의 라우팅 키와 이름이 같은 큐로 메시지를 전달한다. 모든 큐는 자동으로 기본 거래소와 연결된다.
    - 이렉트(Direct) : 바인딩 키가 해당 메시지의 라우팅 키와 같은 큐에 메시지를 전달한다.
    - 토픽(Topic) : 바인딩 키(와일드카드를 포함하는)가 해당 메시지의 라우팅 키와 일치하는 하나 이상의 큐에 메시지를 전달한다.
    - 팬아웃(Fanout) : 바인딩 키나 라우팅 키에 상관없이 모든 연결된 큐에 메시지를 전달한다.
    - 헤더(Header) : 토픽 거래소와 유사하며, 라우팅 키 대신 메시지 헤더 값을 기반으로 한다는 것만 다르다.
    - 데드 레터(Dead letter) : 전달 불가능한 즉, 정의된 어떤 거래소-큐 바인딩과도 일치하지 않는 모든 메시지를 보관하는 잡동사니 거래소다.
- 거래소의 가장 간단한 형태는 기본 거래소와 팬아웃 거래소이며, 이것들은 JMS의 큐 및 토픽과 거의 일치한다.
- 그러나 다른 거래소들을 사용하면 더 유연한 라우팅 스킴을 정의할 수 있다.
- 메시지는 라우팅 키를 갖고 거래소로 전달되고 큐에서 읽혀져 소비된다는 것을 이해하는 것이 가장 중요하다.
- 메시지는 바인딩 정의를 기반으로 거래소로부터 큐로 전달된다.

## RabbitMQ를 스프링에 추가하기

- 스프링을 사용해서 RabbitMQ 메시지를 전송 및 수신하려면, 스프링 부트의 AMQP 스타터 의존성을 빌드에 추가해야 한다.

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

- 이처럼 AMQP 스타터를 빌드에 추가하면 다른 지원 컴포넌트는 물론이고 AMQP 연결 팩토리와 RabbitTemplate 빈을 생성하는 자동-구성이 수행된다.
- RabbitMQ 브로커의 위치와 인증 정보를 구성하는 속성

| 속성 | 설명 |
| --- | --- |
| spring.rabbitmq.addresses | 쉼표로 구분된 리스트 형태의 RabbitMQ 브로커 주소 |
| spring.rabbitmq.host | 브로커의 호스트(기본값은 localhost) |
| spring.rabbitmq.port | 브로커의 포트(기본값은 5672) |
| spring.rabbitmq.username | 브로커를 사용하기 위한 사용자 이름(선택 속성임) |
| spring.rabbitmq.password | 브로커를 사용하기 위한 사용자 암호(선택 속성임) |

- 개발 목적이라면 RabbitMQ 브로커가 로컬 컴퓨터에서 실행되고 5672  포트를 리스닝할 것이라며, 인증 정보가 필요 없을 것이다.
- 그러나 애플리케이션을 실무 환경으로 이양할 때는 유용하다.
- 이 경우 application.yaml 파일에는 다음과 같이 해당 속성들이 설정될 것이다.
- 여기서는 prod 프로파일이 활성화되어 있다.

```yaml
spring:
	profiles: prod
	rabbitmq:
		host: rabbit.tacocloud.com
		port: 5673
		username: tacoweb
		password: l3tm31n
```

## RabbitTemplate을 사용해서 메시지 전송하기

- RabbitMQ 메시징을 위한 스프링 지원의 핵심은 RabbitTemplate이다.
- RabbitTemplate은 JmsTemplate과 유사한 메서드들을 제공한다.
- 그러나 RabbitMQ 특유의 작동 방법에 따른 미세한 차이가 있다.
- 그러나 지정된 큐나 토픽에만 메시지를 전송했던 JmsTemplate 메서드와 달리 RabbitTemplate 메서드는 거래소와 라우팅 키의 형태로 메시지를 전송한다.
- RabbitTemplate을 사용한 메시지 전송에 가장 유용한 메서드를 보면 다음과 같다.
    - 이 메서드들은 RabbitTemplate에서 구현한 AmqpTemplate 인터페이스에 정의되어 있다.

```java
// 원시 메시지를 전송한다.
void send(Message message) throws AmqpException;
void send(String routingKey, Message message) throws AmqpException;
void send(String exchange, String routingKey, Message message) throws AmqpException;

// 객체로부터 변환된 메시지를 전송한다.
void convertAndSend(Object message) throws AmqpException;
void convertAndSend(String routingKey, Object message) throws AmqpException;
void convertAndSend(String exchange, String routingKey, Object message) throws AmqpException;

// 객체로부터 변환되고 전송에 앞서 후처리(post-processing)되는 메시지를 전송한다.
void convertAndSend(Object message, MessagePostProcessor mPP) throws AmqpException;
void convertAndSend(String routingKey, Object message, 
		MessagePostProcessor messagePostProcessor) throws AmqpException;
void convertAndSend(String exchange, String routingKey, Object message, 
		MessagePostProcessor messagePostProcessor) throws AmqpException;
```

- 메서드들은 JmsTemplate의 대응되는 메서드와 유사한 패턴을 따른다.
- 이 메서드들은 도착지 이름(또는 Destination 객체) 대신, 거래소와 라우팅 키를 지정하는 문자열 값을 인자로 받는다는 점에서 JmsTemplate의 대응되는 메서드들과 다르다.
- 거래소를 인자로 받지 않는 메서드들은 기본 거래소로 메시지를 전송한다.
- 마찬가지로 라우팅 키를 인자로 받지 않는 메서드들은 기본 라우팅 키로 전송되는 메시지를 갖는다.
- RabbitTemplate을 사용해서 타코 주문 데이터를 전송해보자.
- 그러나 Order 객체를 Message 객체로 변환한 후 send() 호출해야 한다.

```java
package tacos.messaging;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tacos.order;

@Service
public class RabbitOrderMessagingService implements OrderMessagingService {
	private RabbitTemplate rabbit;

	@Autowired
	public RabbitOrderMessagingService(RabbitTemplate rabbit) {
		this.rabbit = rabbit;
	}

	public void sendOrder(Order order) {
		MessageConverter converter = rabbit.getMessageConverter();
		MessageProperties props = new MessageProperties();
		Message message = converter.toMessage(order, props);
		rabbit.send("tacocloud.order", message);
	}
}
```

- 이처럼 MessageConverter가 있으면 Order 객체를 Message 객체로 변환하기 쉽다.
- 메시지 속성은 MessageProperties를 사용해서 제공해야 한다.
- 그러나 메시지 속성을 설정할 필요가 없다면 MessageProperties의 기본 인스턴스면 족하다.
- 이런 기본값은 spring.rabbitmq.template.exchange와 spring.rabbitmq.template.routing-key 속성을 설정하여 변경할 수 있다.

```yaml
spring:
	rabbitmq:
		template:
			exchange: tacocloud.orders
			routing-key: kitchens.central
```

- 이 경우 거래소를 지정하지 않은 모든 메시지는 이름이 tacocloud.orders인 거래소로 자동 전송된다.
- 만일 send()나 convertAndSend()를 호출할 때 라우팅 키도 지정되지 않으면 해당 메시지는  kitchens.central을 라우팅 키로 갖는다.
- 메시지 변환기로 Message 객체를 생성하는 것은 매우 쉽다.
- 그러나 스프링은 다음을 포함해서 RabbitTemplate이 처리하도록 convertAndSend()를 사용하면 훨씬 더 쉽다.

```java
public void sendOrder(Order order) {
	rabbit.convertAndSend("tacocloud.order", order);
}
```

### 메시지 변환기 구성하기

- 기본적으로 메시지 변환은 SimpleMessageConverter로 수행되며, 이것은 String과 같은 간단한 타입과 Serializable 객체를 Message 객체로 변환할 수 있다.
- 그러나 스프링은 다음을 포함해서 RabbitTemplate에 사용할 수 있는 여러 개의 메시지 변환기를 제공한다.
    - Jackson2JsonMessageConverter : Jackson2JSONProcessor를 사용해서 객체를 JSON으로 상호 변환한다.
    - MarshallingMessageConverter : 스프링 Marshaller와 Unmarshaller를 사용해서 변환한다.
    - SerializerMessageConverter : 스프링의 Serializer와 Deserializer를 사용해서 String과 객체를 변환한다.
    - SimpleMessageConverter : String, byte 배열, Serializable 타입을 변환한다.
    - ContentTypeDelegatingMessageConverter : contentType 헤더를 기반으로 다른 메시지 변환기에 변환을 위임한다.
- 메시지 변환기를 변경해야 할 때는 MessageConverter 타입의 빈을 구성하면 된다.

```java
@Bean
public MessageConverter messageConverter() {
	return new Jackson2JsonMessageConverter();
}
```

- 이렇게 하면 스프링 부트 자동-구성에서 이 빈을 찾아서 기본 메시지 변환기 대신 이 빈을 RabbitTemplate으로 주입한다.

### 메시지 속성 설정하기

- JMS에서처럼 전송하는 메시지의 일부 헤더를 설정해야 할 경우가 있다.
- 이때는 Message 객체를 생성할 때 메시지 변환기에 제공하는 MessageProperties 인스턴스를 통해 헤더를 설정할 수 있다.

```java
public void sendOrder(Order order) {
	MessageConverter converter = rabbit.getMessageConverter();
	MessageProperties props = new MessageProperties();
	props.setHeader("X_ORDER_SOURCE", "WEB");
	Message message = converter.toMessage(order, props);
	rabbit.send("tacocloud.order", message);
}
```

- 그러나 convertAndSend()를 사용할 때는 MessageProperties 객체를 직접 사용할 수 없으므로 다음과 같이 MessagePostProcessor에서 해야 한다.

```java
@Override
public void sendOrder(Order order) {
	rabbit.convertAndSend("tacocloud.order.queue", order,
			new MessagePostProcessor() {
					@Override
					public Message postProcessMessage(Message message) throws AmqpException {
						MessageProperties props = message.getMessageProperties();
						props.setHeader("X_ORDER_SOURCE", "WEB");
						return message;
					}
		});
}
```

## RabbitMQ로부터 메시지 수신하기

- RabbitMQ 큐로부터의 메시지 수신도 JMS로부터의 메시지 수신과 크게 다르지 않다.
- JMS에서처럼 RabbitMQ의 경우도 다음 두 가지를 선택할 수 있다.
    - RabbitTemplate을 사용해서 큐로부터 메시지를 가져온다.
    - @RabbitListener가 지정된 메서드로 메시지가 푸시(push)된다.
- 우선, 큐로부터 메시지를 가져오는 풀(pull) 모델 기반의 RabbitTemplate.receive() 메서드부터 알아보자.

### RabbitTemplate을 사용해서 메시지 수신하기

- RabbitTemplate은 큐로부터 메시지를 가져오는 여러 메서드를 제공하며, 가장 유용한 것을 보면 다음과 같다.

```java
// 메시지를 수신한다.
Message receive() throws AmqpException;
Message receive(String queueName) throws AmqpException;
Message receive(long timeoutMillis) throws AmqpException;
Message receive(String queueName, long timeoutMillis) throws AmqpException;

// 메시지로부터 변환된 객체를 수신한다.
Object receiveAndConvert() throws AmqpException;
Object receiveAndConvert(String queueName) throws AmqpException;
Object receiveAndConvert(long timeoutMillis) throws AmqpException;
Object receiveAndConvert(String queueName, long timeoutMillis) throws AmqpException;

// 메시지로부터 변환된 타입-안전(type-safe) 객체를 수신한다.
<T> T receiveAndConvert(ParameterizedReference<T> type) throws AmqpException;
<T> T receiveAndConvert(String queueName, 
		ParameterizedReference<T> type) throws AmqpException;
<T> T receiveAndConvert(long timeoutMillis, 
		ParameterizedReference<T> type) throws AmqpException;
<T> T receiveAndConvert(String queueName, long timeoutMillis, 
		ParameterizedReference<T> type) throws AmqpException;
```

- 이 메서드들은 앞에서 설명했던 send() 및 convertAndSend() 메서드들과 대칭된다.
- 그러나 메서드 시그니처(signature) 특히 매개변수에서 분명한 차이가 있다.
- 우선, 수신 메서드의 어느 것도 거래소나 라우팅 키를 매개변수로 갖지 않는다.
- 왜냐하면 거래소와 라우팅 키는 메시지를 큐로 전달하는 데 사용되지만, 일단 메시지가 큐에 들어가면 다음 메시지 도착지는 큐로부터 메시지를 소비하는(수신하고 사용하는) 컨슈머(consumer)이기 때문이다.
- 또한, 대부분의 수신 메서드는 메시지의 수신 타임아웃을 나타내기 위해 long 타입의 매개변수를 갖는다.
- 수신 타임아웃의 기본값은 0밀리초(1/1,000초)다.
- 즉, 호출된 즉시 receive()가 결과를 반환하며, 만일 수신할 수 있는 메시지가 없으면 null 값이 반환된다.
- 이것이 JmsTemplate의 receive() 메서드와의 현격한 차이점이다.
- 타임아웃 값을 인자로 전달하면 메시지가 도착하거나 타임아웃에 걸릴 때까지 receive()와 receiveAndConvert() 메서드가 대기하게 된다.
- 그러나 0이 아닌 타임아웃 값을 지정했더라도 null 값이 반환되는 경우를 대비하여 처리하는 코드를 준비해야 한다.

```java
package tacos.kitchen.messaging.rabbit;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitOrderReceiver {
	private RabbitTemplate rabbit;
	private MessageConverter converter;

	@Autowired
	public RabbitOrderReceiver(RabbitTemplate rabbit, MessageConverter converter) {
		this.rabbit = rabbit;
		this.converter = converter;
	}

	public Order receiveOrder() {
		Message message = rabbit.receive("tacocloud.orders");
		return message != null
				? (Order) converter.fromMessage(message)
				: null;
	}
}
```

- 이때 타임아웃 값을 인자로 전달하지 않았으므로 곧바로 Message 객체 또는 null 값이 반환된다.
- 만일 30초 동안 기다리기로 결정했다면, 다음과 같이 receive() 메서드의 인자로 30,000밀리초를 전달하여 receiveOrder() 메서드를 변경하면 된다.

```java
public Order receiveOrder() {
	Message message = rabbit.receive("tacocloud.order.queue", 30000);
	return message != null
			? (Order) converter.fromMessage(message)
			: null;
}
```

- 구성 속성을 통해 타임아웃을 설정하고자 한다면, receive() 호출 코드의 타임아웃 값을 제거하고 다음과 같이 구성 파일의 spring.rabbitmq.template.receive-timeout 속성에 타임아웃 값을 설정하면 된다.

```yaml
spring:
	rabbitmq:
		template:
			receive-timeout: 30000
```

- receiveOrder() 메서드를 다시 보면 RabbitTemplate의 메시지 변환기를 사용해서 수신 Message 객체를 Order 객체로 변환하는 것을 알 수 있다.
- 그러나 RabbitTemplate이 메시지 변환기를 갖고 있음에도 자동으로 변환해 줄 수 없는 이유가 무엇일까?
    - receiveAndConvert() 메서드가 있는 이유가 바로 그 때문이다.

    ```java
    public Order receiveOrder() {
    	return (Order) rabbit.receiveAndConvert("tacocloud.order.queue");
    }
    ```

    - 이 코드가 훨씬 더 간단하다.
    - 단지 Object 타입을 Order 타입으로 캐스팅하는 것만 고려하면 된다.
    - 그러나 캐스팅 대신 다른 방법이 있다.
    - 즉, ParameterizedTypeReference를 receiveAndConvert()의 인자로 전달하여 직접 Order 객체를 수신하게 하는 것이다.

    ```java
    public Order receiveOrder() {
    	return rabbit.receiveAndConvert("tacocloud.order.queue", 
    			new ParameterizedTypeReference<Order> () {});
    }
    ```

    - 이 방법이 캐스팅보다 더 좋을지는 논란의 여지가 있을 수 있지만, 타입-안전 측면에서는 캐스팅보다 좋다.
    - 단, receiveAndConvert()에 ParameterizedTypeReference를 사용하려면 메시지 변환기가 SmartMessageConverter 인터페이스를 구현한 클래스(예를 들어, Jackson2JsonMessageConverter)이어야 한다.

### 리스너를 사용해서 RabbitMQ 메시지 처리하기

- 메시지 기반의 RabbitMQ 빈을 위해 스프링은 RabbitListener를 제공한다.
- 메시지가 큐에 도착할 때 메서드가 자동 호출되도록 지정하기 위해서는 @RabbitListener 애노테이션을 RabbitMQ 빈의 메서드에 지정해야 한다.

```java
package tacos.kitchen.messaging.rabbit.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderListener {
	private KitchenUI ui;

	@Autowired
	public OrderListener(KitchenUI ui) {
		this.ui = ui;
	}

	@RabbitListener(queues = "tacocloud.order.queue")
	public void receiveOrder(Order order) {
		ui.displayOrder(order);
	}
}

```

- JmsListener msListener 코드와 동일하다. 단지 리스너 애토에이션을 @JmsListener에서 @RabbitListener로 변경했을 뿐이다.
- 사용하는 메시지 브로커와 리스너가 다르더라도 리스너 애노테이션만 변경하면(JMS 브로커에는 @JmsListener, RabbitMQ 브로커에는 @RabbitListener) 이처럼 거의 동일한 코드를 사용할 수 있다는 것은 참 좋은 일이다.

# 카프카 사용하기

- 아파치 카프카는 가장 새로운 메시징 시스템이며, ActiveMQ, Artemis, RabbitMQ와 유사한 메시지 브로커다.
- 그러나 카프카는 특유의 아키텍처를 갖고 있다.
- 카프카는 높은 확장성을 제공하는 클러스터(cluster)로 실행되도록 설계되었다.
- 그리고 클러스터의 모든 카프카 인스턴스에 걸쳐 토픽(topic)을 파티션(partition)으로 분할하여 메시지를 관리한다.
- RabbitMQ가 거래소와 큐를 사용해서 메시지를 처리하는 반면, 카프카는 토픽만 사용한다.
- 카프카의 토픽은 클러스터의 모든 브로커에 걸쳐 복제(replicated)된다.
- 클러스터의 각 노드는 하나 이상의 토픽에 대한 리터(leader)로 동작하며, 토픽 데이터를 관리하고 클러스터의 다른 노드로 데이터를 복제한다.

![chapter08-02](image/chapter08-02.png '카프카 클러스터는 여러 개의 브로커로 구성되며, 각 브로커는 토픽의 파티션의 리더로 동작한다.')

- 각 토픽은 여러 개의 파티션으로 분할될 수 있다.
- 이 경우 클러스터의 각 노드는 한 토픽의 하나 이상의 파티션(토픽 전체가 아닌)의 리더가 된다.

## 카프카 사용을 위해 스프링 설정하기

- 카프카를 사용해서 메시지를 처리하려면 이에 적합한 의존성을 빌드에 추가해야 한다.
- 그러나 JMS나 RabbitMQ와 달리 카프카는 스프링 부트 스타터가 없다.

```xml
<dependency>
	<groupId>org.springframework.kafka</groupId>
	<artifactId>spring-kafka</artifactId>
</dependency>
```

- 이처럼 의존성을 추가하면 스프링 부트가 카프카 사용을 위한 자동-구성을 해준다.
    - 스프링 애플리케이션에서 사용할 KafkaTemplate을 준비함
- KafkaTemplate은 기본적으로 localhost에서 실행되면서 9092 포트를 리스닝하는 카프카 브로커를 사용한다.
- 애플리케이션을 개발할 때는 로컬의 카프카 브로커를 사용하면 좋다.
- 그러나 실무 환경으로 이양할 때는 다른 호스트와 포트로 구성해야 한다.
- spring.kafka.bootstrap-servers 속성에는 카프카 클러스터로의 초기 연결에 사용되는 하나 이상의 카프카 서버들의 위치를 설정한다.

```yaml
spring:
	kafka:
		bootstrap-servers:
			- kafka.tacocloud.com:9092
```

- 여기서 spring.kafka.bootstrap-servers는 복수형이며, 서버 리스트를 받으므로 클러스터의 여러 서버로 지정할 수 있다.

```yaml
spring:
	kafka:
		bootstrap-servers:
			- kafka.tacocloud.com:9092
			- kafka.tacocloud.com:9093
			- kafka.tacocloud.com:9094
```

## KafkaTemplate을 사용해서 메시지 전송하기

- 여러 면에서 KafkaTemplate의 메서드들은 JMS나 RabbitMQ의 대응되는 메서드들과 유사 하지만 매우 다른 부분도 있다.

```java
ListenableFuture<SendResult<K, V>> send(String topic, V data);
ListenableFuture<SendResult<K, V>> send(String topic, K key, V data);
ListenableFuture<SendResult<K, V>> send(String topic, 
		Integer partition, K key, V data);
ListenableFuture<SendResult<K, V>> send(String topic, 
		Integer partition, Long timestamp, K key, V data);
ListenableFuture<SendResult<K, V>> send(ProducerRecord<K, V> record);
ListenableFuture<SendResult<K, V>> send(Message<?> message);
ListenableFuture<SendResult<K, V>> sendDefault(V data);
ListenableFuture<SendResult<K, V>> sendDefault(K key, V data);
ListenableFuture<SendResult<K, V>> sendDefault(Integer partition, K key, V data);
ListenableFuture<SendResult<K, V>> sendDefault(Integer partition, 
		Long timestamp, K key, V data);
```

- 제일 먼저 알아 둘 것은 convertAndSend() 메서드가 없다는 것이다.
- 왜냐하면 KafkaTemplate은 제네릭(generic) 타입을 사용하고, 메시지를 전송할 때 직접 도메인 타입을 처리할 수 있기 때문이다.
- 또한, send()와 sendDefault()에는 JMS나 Rabbit에 사용했던 것과 많이 다른 매개변수들이 있다.
- 카프카에서 메시지를 전송할 때는 메시지가 전송되는 방법을 알려주는 다음 매개변수를 지정할 수 있다.
    - 메시지가 전송될 토픽(send()에 필요함)
    - 토픽 데이터를 쓰는 파티션(선택적임)
    - 레코드 전송 키(선택적임)
    - 타임스탬프(선택적이며, 기본값은 System.currentTimeMillis())
    - 페이로드(payload 메시지에 적재된 순수한 데이터(예를 들어, Order 객체)이며 필수임)
- 토픽과 페이로드는 가장 중요한 매개변수들이다.
- send() 메서드에는 ProducerRecord를 전송하는 것도 있다.
- ProducerRecord는 모든 선행 매개변수들을 하나의 객체에 담은 타입이다.
- 대개의 경우에 ProducerRecord나 Message 객체를 생성 및 전송하는 것보다는 다른 send() 메서드 중 하나를 사용하는 게 더 쉽다.

```java
package tacos.messaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaOrderMessage implements OrderMessagingService {
	private KafkaTemplate<String, Order> kafkaTemplate;

	@Autowired
	public KafkaOrderMessage(KafkaTemplate<String, Order> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	@Override
	public void sendOrder(Order order) {
		kafkaTemplate.send("tacocloud.orders.topic", order);
	}
}
```

- 여기서 sendOrder() 메서드는 주입된 KafkaTemplate의 send() 메서드를 사용해서 tacocloud.orders.topic이라는 이름의 토픽으로 Order 객체를 전송한다.
- 만일 기본 토픽을 설정한다면 sendOrder() 메서드를 약간더 간단하게 만들 수 있다.
- 이때는 우선 spring.kafka.template.default-topic 속성에 tacocloud.orders.topic을 기본 토픽으로 설정한다.

```yaml
spring:
	kafka:
		template:
			default-topic: tacocloud.orders.topic
```

```java
@Override
public void sendOrder(Order order) {
	kafkaTemplate.sendDefault(order);
}
```

## 카프카 리스너 작성하기

- send()와 sendDefault() 특유의 메서드 시그니처 외에도 KafkaTemplate은 메시지를 수신하는 메서드를 일체 제공하지 않는다는 점에서 JmsTemplate이나 RabbitTemplate과 다르다.
- 따라서 스프링을 사용해서 카프카 토픽의 메시지를 가져오는 유일한 방법은 메시지 리스너를 작성하는 것이다.
- 카프카의 경우 메시지 리스너를 @KafkaListener 애노테이션이 지정된 메서드에 정의된다.

```java
package taco.kitchen.messaging.kafka.listener;

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tacos.Order;
import tacos.kitchen.KitchenUI;

@Component
public class OrderListener {
	private KitchenUI ui;

	@Autowired
	public OrderListener(KitchenUI ui) {
		this.ui = ui;
	}

	@KafkaListener(topics="tacocloud.orders.topic")
	public void handle(Order order) {
		ui.displayOrder(order);
	}
}
```

- tacocloud.orders.topic이라는 이름의 토픽에 메시지가 도착할 때 자동 호출되어야 한다는 것을 나타내기 위해 handle() 메서드에는 @KafkaListener 애노테이션이 지정되었다.
- 페이로드인 Order 객체만 handle()의 인자를 받는다.
- 그러나 메시지의 추가적인 메타데이터가 필요하다면 ConsumerRecord나 Message 객체도 인자로 받을 수 있다.
- 다음의 handle() 메서드에서는 수신된 메시지의 파티션과 타임스탬프를 로깅하기 위해 ConsumerRecord를 인자로 받는다.

```java
@KafkaListener(topics="tacocloud.orders.topic")
public void handle(Order order, ConsumerRecord<Order> record) {
	log.info("Received from partition {} with timestamp {}", 
		record.partition(), record.timestamp());
	ui.displayOrder(order);
}
```

- 이와 유사하게 ConsumerRecord 대신 Message 객체를 요청하여 같은 일을 처리할 수 있다.

```java
@KafkaListener(topics="tacocloud.orders.topic")
public void handle(Order order, Message<Order> message) {
	MessageHeaders headers = message.getHeaders();
	log.info("Received from partition {} with timestamp {}", 
		headers.get(KafkaHeaders.RECEIVED_PARTITION_ID),
		headers.get(KafkaHeaders.RECEIVED_TIMESTAMP));
	ui.displayOrder(order);
}
```

- 메시지 페이로드는 ConsumerRecord.value()나 Message.getPayload()를 사용해도 받을 수 있다는 것을 알아두자.

# 비동기 메시지 전송과 수신 기능이 추가된 타코 클라우드 애플리케이션 빌드 및 실행하기

- 추가된 모듈
    - tacocloud-kitchen : 타코 클라우드 주방 모듈
    - tacocloud-messaging-jms : JMS를 사용해서 비동기 주문 메시지를 전송하는 타코 클라우드 메시징 모듈
    - tacocloud-messaging-kafka : 카프카를 사용해서 비동기 주문 메시지를 전송하는 타코 클라우드 메시징 모듈
    - tacocloud-messaging-rabbitmq : RabbitMQ를 사용해서 비동기 주문 메시지를 전송하는 타코 클라우드 메시징 모듈
- 터미널 창에서 다음과 같이 타코 클라우드 애플리케이션을 실행하자.

```bash
$ java -jar tocos/target/taco-cloud-0.0.8-SNAPSHOT.jar
```

- 터미널 창에서 타코 클라우드 주방 애플리케이션을 추가로 실행하자.

```bash
$ java -jar tacocloud-kitchen/target/tacocloud-kitchen-0.0.8-SNAPSHOT.jar
```

- 타코 클라우드 주방 애플리케이션의 톰캣 서버는 로컬 호스트의 8081 포트를 리스닝한다.
    - 타코 클라우드 메인 애플리케이션의 톰캣 서버는 로컬 호스트의 8080 포트로 실행 중이다.
- 현재는 스프링에 기본값으로 설정된 artemis 브로커와 JmsTemplate을 사용하여 주문 데이터가 JMS 메시지로 처리되었다.
- 그러나 카프카를 사용하려면 별도로 다운로드하고 설치 및 실행해야 한다.
    - 현재는 tacocloud-kitchen/pom.xml에 카프카 의존성이 지정되어 있지만, 카프카가 실행되지 않아 터미널 창에 카프카 브로커를 사용할 수 없다는 메시지가 계속 나타날 것이다.