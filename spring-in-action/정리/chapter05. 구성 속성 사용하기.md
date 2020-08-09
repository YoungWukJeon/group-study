# 자동-구성 세부 조정하기

- 스프링에는 다음 두 가지 형태의 서로 다르면서도 관련이 있는 구성이 있다는 것을 아는 것이 중요하다.
    - 빈 연결(Bean wiring) : 스프링 애플리케이션 컨텍스트에서 빈으로 생성되는 애플리케이션 컴포넌트 및 상호 간에 주입되는 방법을 선언하는 구성
    - 속성 주입(Property injection) : 스프링 애플리케이션 컨텍스트에서 빈의 속성 값을 설정하는 구성
- 이 두 가지 구성은 스프링의 XML 구성과 자바 기반 구성 모두에서 종종 같은 곳에 선언된다.
- 자바 기반 구성에서 @Bean 애노테이션이 지정된 메서드는 사용하는 빈의 인스턴스를 생성하고 속성 값도 설정한다.

```java
@Bean
public DataSource dataSource() {
	return new EmbeddedDatabaseBuilder()
		.setType(EmbeddedDatabaseType.H2)
		.addScript("schema.sql")
		.addScripts("user_data.sql", "ingredient_data.sql")
		.build();
}
```

- 만일 스프링 부트를 사용 중이 아니라면 이 메서드(dataSource())는 DataSource 빈을 구성할 수 있는 방법이 된다.
- 그러나 스프링 부트를 사용 중일 때는 자동-구성이 DataSource 빈을 구성해 주므로 dataSource() 메서드가 필요 없다.

## 스프링 환경 추상화 이해하기

- 스프링 환경 추상화(environment abstraction)는 구성 가능한 모든 속성을 한 곳에서 관리하는 개념이다.
- 즉, 속성의 근원을 추상화하여 각 속성을 필요로 하는 빈이 스프링 자체에서 해당 속성을 사용할 수 있게 해준다.
- 스프링 환경에서는 다음과 같은 속성의 근원으로부터 원천 속성을 가져온다.
    - JVM 시스템 속성
    - 명령행 인자(command-line argument)
    - 운영체제의 환경 변수
    - 애플리케이션의 속성 구성 파일

![chapter05-01](image/chapter05-01.png '스프링 환경에서는 원천 속성들을 가져와서 애플리케이션 컨텍스트의 빈이 사용할 수 있게 해준다.')

- 애플리케이션을 실행해 주는 서블릿 컨테이너가 8080 기본 포트가 아닌 다른 포트로 작동하게 하다면 다음과 같이 다른 포트 값을 갖는 server.port 속성을 src/main/resources/application.properties 파일에 지정하면 된다.

```
server.port=9090
```

- 구성 속성을 설정할 때 YAML(YAML Ain't Markup Language)을 주로 사용한다면 application.properties 파일 대신 src/main/resources/application.yml에 server.port 값을 설정하면 된다.

```yaml
server:
	port: 9090
```

- 또한, 애플리케이션을 시작할 때 명령행 인자로 server.port 속성을 지정할 수도 있다.

```bash
$ java -jar tacocloud-0.0.5-SNAPSHOT.jar --server.port=9090
```

- 만일 애플리케이션에서 항상 특정 포트를 사용하게 하고 싶다면 다음과 같이 운영체제 환경 변수에 설정하면 된다.

```bash
$ export SERVER_PORT=9090
```

## 데이터 소스 구성하기

- 데이터 소스의 경우 우리 나름의 DataSource 빈을 명시적으로 구성할 수 있다.
- 그러나 스프링 부트 사용 시는 그럴 필요 없으며, 대신에 구성 속성을 통해서 해당 데이터베이스의 URL과 인증을 구성하는 것이 더 간단하다.

```yaml
spring:
	datasource:
		url: jdbc:mysql://localhost/tacocloud
		username: tacodb
		password: tacopassword
```

- 그 다음에 적합한 JDBC 드라이버를 추가해야 하지만, 구체적인 JDBC 드라이버 클래스를 지정할 필요는 없다.
- 스프링 부트가 데이터베이스 URL로부터 찾을 수 있기 때문이다.
- 그러나 만일 문제가 생긴다면 다음과 같이 spring.datasource.driver-class-name 속성을 설정하면 된다.

```yaml
spring:
	datasource:
		url: jdbc:mysql://localhost/tacocloud
		username: tacodb
		password: tacopassword
		driver-class-name: com.mysql.jdbc.Driver
```

- 또한, 톰캣의 JDBC 커넥션 풀(connection pool)을 classpath에서 자동으로 찾을 수 있다면 DataSource 빈이 그것을 사용한다.
- 그러나 그렇지 않다면 스프링 부트는 다음 중 하나의 다른 커넥션 풀을 classpath에서 찾아 사용한다.
    - HikariCP
    - Commons DBCP 2
- 애플리케이션이 시작될 때 데이터베이스를 초기화하는 SQL 스크립트의 실행 방법을 다음과 같이 spring.datasource.schema와 spring.datasource.data 속성을 사용하면 더 간단하게 지정할 수 있다.

```yaml
spring:
	datasource:
		schema:
			- order-schema.sql
			- ingredient-schema.sql
			- taco-schema.sql
			- user-schema.sql
		data:
			- ingredients.sql
```

- 또는 명시적인 데이터 소스 구성 대신 JNDI(Java Naming and Directory Interface)에 구성하는 것을 원할 수도 있다.
- 이 때는 다음과 같이 spring.datasource.jndi-name 속성을 구성하면 스프링이 찾아준다.

```yaml
spring:
	datasource:
		jndi-name: java:/comp/env/jdbc/tacoCloudDS
```

- 단, spring.datasource.jndi-name 속성을 설정하면 기존에 설정된 다른 데이터 소스 구성 속성은 무시된다.

## 내장 서버 구성하기

```yaml
server:
	port: 0
```

- 이처럼 우리가 server.port를 0으로 설정하더라도 서버는 0번 포트로 시작하지 않는다.
- 대신에 사용 가능한 포트를 무작위로 선택하여 시작된다.
- 이것은 자동화된 통합 테스트를 실행할 때 유용하다.
- 즉, 동시적으로 실행되는 어떤 테스트도 같은 포트 번호로 인한 충돌이 생기지 않기 때문이다.
- 이것은 또한 마이크로서비스(microservice)와 같이 애플리케이션이 시작되는 포트가  중요하지 않을 때도 유용하다.
- 서버에 관련해서는 포트 외에도 중요한 것이 더 있다.
- 그 중 하나가 HTTPS 요청 처리를 위한  컨테이너 관련 설정이다.
- 이 때는 JDK의 keytool 명령행 유틸리티를 사용해서 키스토어(keystore)를 생성하는 것이 가장 먼저 할 일이다.

```bash
$ keytool -keystore mykeys.jks -genkey -alias tomcat -keyalg RSA
```

- keytool이 실행되면 저장 위치 등의 여러 정보를 입력받는데, 무엇보다 우리가 입력한 비밀번호(password)를 잘 기억해 두는 것이 중요하다.
- 여기서는 letmein을 비밀번호로 지정하였다.
- 키스토어 생성이 끝난 후에는 내장 서버의 HTTPS를 활성화하기 위해 몇 가지 속성을 설정해야 한다.
- 이 속성들은 모두 명령행에 지정할 수 있다.
- 그러나 그렇게 하는 것은 굉장히 불편하다.
- 대신에 application.properties 또는 다음과 같이 application.yml 파일에 설정하는 것이 좋다.

```yaml
server:
	port: 8443
	ssl:
		key-store: file://path/to/mykeys.jks
		key-store-password: letmein
		key-password: letmein
```

- 여기서 server.port 속성은 8443으로 설정되었다.
- 이 값은 개발용 HTTPS 서버에 많이 사용된다.
- server.ssl.key-store 속성은 키스토어 파일이 생성된 경로로 설정되어야 한다.
- 여기서는 운영체제의 파일 시스템에서 키스토어 파일을 로드하기 위해 file://를 URL로 지정하였다.
- 그러나 애플리케이션 JAR 파일에 키스토어 파일을 넣는 경우는 classpath:를 URL로 지정하여 참조해야 한다.
- 그리고 server.ssl.key-store-password와 server.ssl.key-password 속성에는 키스토어 생성할 때 지정했던 비밀번호를 설정한다.

## 로깅 구성하기

- 대부분의 애플리케이션은 어떤 형태로든 로깅(logging)을 제공한다.
- 기본적으로 스프링 부트는 INFO 수준(level)으로 콘솔에 로그 메시지를 쓰기 위해 Logback([http://logback.qos.ch](http://logback.qos.ch))을 통해 로깅을 구성한다.
- 로깅 구성을 제어할 때는 classpath의 루트(src/main/resources)에 logback.xml 파일을 생성할 수 있다.

```xml
<configuration>
	<appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
		<encoder>
			<pattern>
				%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
			</pattern>
		</encoder>
	</appender>
	<logger name="root" level="INFO" />
	<root level="INFO">
		<appender-ref ref="STDOUT" />
	</root>
</configuration>
```

- 로깅에 사용되는 패턴을 제외하면 이 Logback 구성은 logback.xml 파일이 없을 때의 기본 로깅 구성과 동일하다.
- 그러나 logback.xml 파일을 수정하면 우리가 원하는 형태로 애플리케이션 로그 파일을 제어할 수 있다.
- 로깅 구성에서 가장 많이 변경하는 것은 로깅 수준과 로그를 수록할 파일이다.
- 스프링 부트의 구성 속성을 사용하면 logback.xml 파일을 생성하지 않고 그것을 변경할 수 있다.
- 로깅 수준을 설정할 때는 logging.level을 접두어로 갖는 속성들을 생성한다.
- 그리고 그 다음에 로깅 수준을 설정하기 원하는 로거(logger)의 이름을 붙인다.
- 예들 들어, 루트의 로깅 수준을 WARN으로 하되, 스프링 시큐리티의 로그는 DEBUG 수준으로 설정하고 싶다고 해보자.
- 이 때는 application.yml에 다음 항목을 수정하면 된다.

```yaml
logging:
	level:
		root: WARN
		org:
			springframework:
				security: DEBUG
---
logging:
	level:
		root: WARN
		org.springframework.security: DEBUG
```

- 그 다음에 로그 항목들은 /var/logs/ 경로의 TacoCloud.log 파일에 수록하고 싶다고 해보자.
- 다음과 같이 logging.path와 logging.file 속성을 사용하면 된다.

```yaml
logging:
	path: /var/logs/
	file: TacoCloud.log
	level:
		root: WARN
		org:
			springframework:
				security: DEBUG
```

- 기본적인 로그 파일의 크기인 10MB가 가득 차게 되면 새로운 로그 파일이 생성되어 로그 항목이 계속 수록된다.
    - 스프링 2.0부터는 날짜별로 로그 파일이 남으며, 지정된 일 수가 지난 로그 파일은 삭제된다.

## 다른 속성의 값 가져오기

- 하드코딩된 String과 숫자 값으로만 속성 값을 설정해야 하는 것은 아니다.
- 대신에 다른 구성 속성으로부터 값을 가져올 수도 있다.

```yaml
greeting:
	welcome: You are using ${spring.application.name}.
```

# 우리의 구성 속성 생성하기

- 구성 속성의 올바른 주입을 지원하기 위해 스프링 부트는 @ConfigurationProperties 애노테이션을 제공한다.
- 그리고 어떤 스프링 빈이건 이 애노테이션이 지정되면, 해당 빈의 속성들이 스프링 환경의 속성으로부터 주입될 수 있다.

```java
...
@GetMapping
public String ordersForUser(@AuthenticationPrincipal User user, Model model) {
	model.addAttribute("orders", orderRepo.findByUserOrderByPlacedAtDesc(user));

	return "orderList";
}
...
```

```java
...
import java.util.List;
import tacos.User;
...
public interface OrderRepository extends CrudRepository<Order, Long> {
	List<Order> findByUserOrderByPlacedAtDesc(User user);
}
```

- 이 리퍼지터리 메서드의 이름은 OrderByPlacedAtDesc 절(clause)을 사용해서 지정되었다는 것에 주목하자.
- 여기서 OrderBy 부분은 결과를 정렬하는 기준이 되는 속성을 나타낸다.
    - 이 경우는 placeAt
- 그리고 제일 끝의 Desc는 내림차순 정렬(descending order)이 실행되게 한다.
- 가장 최근의 20개 주문만 나타나도록 조회 주문 수를 제한하고 싶다고 해보자.

```java
...
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
...
@GetMapping
public String ordersForUser(@AuthenticationPrincipal User user, Model model) {
	Pageable pageable = PageRequest.of(0, 20);
	model.addAttribute("orders", orderRepo.findByUserOrderByPlacedAtDesc(user, pageable));

	return "orderList";
}
```

```java
...
import org.springframework.data.domain.Pageable;
...
List<Order> findByUserOrderByPlacedAtDesc(User user, Pageable pageable);
...
```

- 스프링 데이터의 Pageable 인터페이스를 사용하면 페이지 번호와 크기로 결과의 일부분을 선택할 수 있다.
- 커스텀 구성 속성을 사용해서 페이지 크기를 설정할 수 있다.

```java
...
import org.springframework.boot.context.properties.ConfigurationProperties;
...
@Controller
@RequestMapping("/orders")
@SessionAttributes("order")
@ConfigurationProperties(prefix="taco.orders")
public class OrderController {
	private int pageSize = 20;

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	...

	@GetMapping
	public String ordersForUser(@AuthenticationPrincipal User user, Model model) {
		Pageable pageable = PageRequest.of(0, pageSize);
		model.addAttribute("orders", orderRepo.findByUserOrderByPlacedAtDesc(user, pageable));
	
		return "orderList";
	}
}
```

- 코드에서 가장 중요한 변화는 @ConfigurationProperties이며, 이 애노테이션에 지정된 접두어는 tacos.order다.
- 따라서 pageSize 구성 속성 값을 설정할 때는 taco.orders.pageSize라는 이름을 사용해야 한다.

```yaml
taco:
	orders:
		pageSize: 10
```

- 또는 애플리케이션을 프로덕션에서 사용 중에 빨리 변경해야 한다면, 다음과 같이 환경 변수에 taco.orders.pageSize 속성을 설정할 수도 있다.
- 이 때는 애플리케이션을 다시 빌드 및 배포하지 않아도 된다.

```bash
$ export TACO_ORDERS_PAGESIZE=10
```

## 구성 속성 홀더 정의하기

- 실제로 @ConfigurationProperties는 구성 데이터의 홀더로 사용하는 빈에 지정되는 경우가 많다.
- 그리고 이렇게 컨트롤러와 이외의 다른 애플리케이션 클래스 외부에 구성 관련 정보를 따로 유지할 수 있다.
- 또한, 여러 빈에 공통적인 구성 속성을 쉽게 공유할 수 있다.

```java
package tacos.web;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;

@Component
@ConfigurationProperties(prefix="taco.orders")
@Data
public class OrderProps {
	private int pageSize = 20;
}
```

```java
...
~~import org.springframework.boot.context.properties.ConfigurationProperties;~~
...
@Controller
@RequestMapping("/orders")
@SessionAttributes("order")
~~@ConfigurationProperties(prefix="taco.orders")~~
public class OrderController {
	~~private int pageSize = 20;~~

	~~public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}~~
	private OrderProps props;

	private OrderRepository orderRepo;

	public OrderController(OrderRepository orderRepo, OrderProps props) {
		this.orderRepo = orderRepo;
		this.props = props;
	}

	...

	@GetMapping
	public String ordersForUser(@AuthenticationPrincipal User user, Model model) {
		Pageable pageable = PageRequest.of(0, props.getPageSize());
		model.addAttribute("orders", orderRepo.findByUserOrderByPlacedAtDesc(user, pageable));
	
		return "orderList";
	}
}
```

- 여러 다른 빈에서 pageSize 속성을 사용하는데, 이 속성의 값이 5부터 25 사이인지 검사하는 애노테이션(@Validated, @Min, @Max)을 적용하기로 해보자.

```java
package tacos.web;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
...

@Component
@ConfigurationProperties(prefix="taco.orders")
@Data
@Validated
public class OrderProps {
	@Min(value=5, message="must be between 5 and 25")
	@Max(value=25, message="must be between 5 and 25")
	private int pageSize = 20;
}
```

- 이처럼 구성 속성 홀더 빈을 사용하면 구성 속성 관련 코드를 한군데에 모아둘 수 있으므로 해당 속성을 사용하는 클래스들의 코드가 더 깔끔해진다.

## 구성 속성 메타데이터 선언하기

- 구성 속성 메타데이터는 선택적이므로 설사 없더라도 구성 속성이 동작하는 데 문제가 생기지는 않는다.
- 그러나 메타데이터가 있으면 해당 구성 속성에 관해 최소한의 정보를 제공해주므로 유용하다.
- 우리가 정의한 구성 속성들을 사용할 수 있는 사람들(우리 자신이 될 수도 있다)을 돕기 위해 해당 속성들에 관한 메타데이터를 생성하는 것이 좋다.
- 그리고 이렇게 하면 최소한 IDE의 성가신 경고 메시지는 나타나지 않는다.
- 다음과 같이 spring-boot-configuration-processor(스프링 부트 구성 처리기) 의존성을 pom.xml 파일에 추가하자.

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-configuration-processor</artifactId>
	<optional>true</optional>
</dependency>
```

- spring-boot-configuration-processor는 @ConfigurationProperties 애노테이션이 지정된 애플리케이션 클래스에 관한 메타데이터를 생성하는 애노테이션 처리기다.
- 그리고 생성된 메타데이터는 application.yml이나 application.properties를 작성할 때 자동-완성 기능 제공 및 속성의 문서를 보여주기 위해 STS와 같은 IDE에서 사용된다.
    - 그 다음에 우리의 커스텀 구성 속성에 관한 메타데이터를 생성하려면 프로젝트의 src/main/resources/META-INF 아래에 additional-spring-configuration-metadata.json이라는 이름의 파일을 생성해야 한다.

```json
{
	"properties": [
		{
			"name": "taco.orders.page-size",
			"type": "int",
			"description": "Sets the maximum number of orders to display in a list."
		}
	]
}
```

- 스프링 부트는 속성 이름을 유연하게 처리하므로 taco.orders.page-size와 taco.orders.pageSize를 같은 것으로 간주한다.

# 프로파일 사용해서 구성하기

- 애플리케이션이 서로 다른 런타임 환경에 배포, 설치될 때는 대개 구성 명세가 달라진다.
- 각 환경의 속성들을 application.properties나 application.yml에 정의하는 대신, 운영체제의 환경 변수를 사용해서 구성하는 것이 한 가지 방법이다.

```bash
$ export SPRING_DATASOURCE_URL=jdbc:mysql://localhost/tacocloud
$ export SPRING_DATASOURCE_USERNAME=tacouser
$ export SPRING_DATASOURCE_PASSWORD=tacopassword
```

- 하지만 하나 이상의 구성 속성을 환경 변수로 지정하는 것은 번거롭다.
- 게다가 환경 변수의 변경을 추적 관리하거나 오류가 있을 경우에 변경 전으로 바로 되돌릴 수 있는 방법이 마땅치 않다.
- 따라서 필자는 이 방법 대신 스프링 프로파일의 사용을 선호한다.
- 런타임 시에 활성화(active)되는 프로파일에 따라 서로 다른 빈, 구성 클래스, 구성 속성들이 적용 또는 무시되도록 하는 것이 프로파일이다.

## 프로파일 특정 속성 정의하기

- 프로파일에 특정한 속성을 정의하는 한 가지 방법은 프로덕션 환경이 속성들만 포함하는 또 다른 .yml이나 .properties 파일을 생성하는 것이다.
- 이 때 파일 이름은 다음 규칙을 따라야 한다.
- 즉, application-{프로파일 이름}.yml 또는 application-{프로파일 이름}.properties다.

```yaml
# application-prod.yml
spring:
	datasource:
		url: jdbc:mysql://localhost//tacocloud
		username: tacouser
		password: tacopassword
	logging:
		level:
			tacos: WARN
```

- 또한, YAML 구성에서만 가능한 또 다른 방법으로 프로파일 특정 속성을 정의할 수도 있다.
- 이 때는 프로파일에 특정되지 않고 공통으로 적용되는 기본 속성과 함께 프로파일 특정 속성을 application.yml에 지정할 수 있다.
- 즉, 프로파일에 특정되지 않는 기본 속성 다음에 3개의 하이픈(---)을 추가하고 그 다음에 해당 프로파일의 이름을 나타내는 spring.profiles 속성을 지정하면 된다.

```yaml
# application.yml
logging:
	level:
		tacos: DEBUG

---
spring:
	profiles: prod
	datasource:
		url: jdbc:mysql://localhost//tacocloud
		username: tacouser
		password: tacopassword

logging:
	level:
		tacos: WARN
```

- 이 application.yml 파일은 3개의 하이픈(---)을 기준으로 두 부분으로 구분된다.
- 첫 번째 부분에서는 spring.profiles의 값을 지정하지 않았다.
    - 따라서 이 부분의 속성 설정은 모든 프로파일에 공통으로 적용되며, 만일 이 부분의 속성과 같은 속성을 활성화된 프로파일에서 설정하지 않으면 해당 속성의 기본 설정이 된다.

## 프로파일 활성화하기

- 프로파일 특정 속성들의 설정은 해당 프로파일이 활성화되어야 유효하다.

```yaml
spring:
    profiles:
		active:
			- prod
```

- 그러나 이것은 가장 좋지 않은 프로파일 활성화 방법일 것이다.
- 만일 application.yml에서 홀성화 프로파일을 설정하면 해당 프로파일이 기본 프로파일이 된다.
- 따라서 프로덕션 환경 특정 속성을 개발 속성과 분리시키기 위해 프로파일을 사용하는 장점을 전혀 살릴 수 없게 된다.
- 그러므로 이 방법 대신에 환경 변수를 사용해서 활성화 프로파일을 설정할 것을 권한다.
- 이 때는 다음과 같이 프로덕션 환경의 SPRING_PROFILES_ACTIVE를 설정할 수 있다.

```bash
$ export SPRING_PROFILES_ACTIVE=prod
```

- 만일 실행 가능한 JAR 파일로 애플리케이션을 실행한다면, 다음과 같이 명령행 인자로 활성화 프로파일을 설정할 수도 있다.

```bash
$ java -jar taco-cloud.jar --spring.profiles.active=prod
```

- spring.profiles.active 속성에는 여러 개의 프로파일이 포함될 수 있다.
- 즉, 하나 이상의 활성화 프로파일을 지정할 수 있다는 의미다.
- 이런 경우, 환경 변수를 사용해서 프로파일을 활성화할 때는 쉼표(,)를 사용해서 지정한다.

```bash
$ java -jar taco-cloud.jar --spring.profiles.active=prod,audit,ha
```

- 그러나 YAML에서는 다음과 같이 지정하면 된다.

```yaml
spring:
	profiles:
		active:
			- prod
			- audit
			- ha
```

## 프로파일을 사용해서 조건별로 빈 생성하기

- 서로 다른 프로파일 각각에 적합한 빈들을 제공하는 것이 유용할 때가 있다.
- 일반적으로 자바 구성 클래스에 선언된 빈은 활성화되는 프로파일과는 무관하게 생성된다.
- 그러나 특정 프로파일이 활성화 될 때만 생성되어야 하는 빈들이 있다고 해보자.
- 이 경우 @Profile 애노테이션을 사용하면 지정된 프로파일에만 적합한 빈들을 나타낼 수 있다.

```java
@Bean
@Profile("dev")
public CommandLineRunner dataLoader(IngredientRepository repo, 
	UserRepository userRepo, PasswordEncoder encoder) {
	...
}
```

```java
@Bean
@Profile({"dev", "qa"})
public CommandLineRunner dataLoader(IngredientRepository repo, 
	UserRepository userRepo, PasswordEncoder encoder) {
	...
}
```

- 개발 환경에서 애플리케이션이 실행될 때는 dev 프로파일을 활성화해 주어야 한다는 것에 유의하자.
- 또한, prod 프로파일이 활성화되지 않을 때는 CommandLineRunner 빈이 항상 생성되도록 한다면 더 편리할 것이다.
- 이 때는 다음과 같이 @Profile을 지정할 수 있다.

```java
@Bean
@Profile("!prod")
public CommandLineRunner dataLoader(IngredientRepository repo, 
	UserRepository userRepo, PasswordEncoder encoder) {
	...
}
```

- 여기서 느낌표(!)는 부정의 의미이므로 prod 프로파일이 활성화되지 않을 경우 CommandLineRunner 빈이 생성됨을 나타낸다.
- @Profile은 @Configuration이 지정된 클래스 전체에 대해 사용할 수도 있다.

```java
@Profile({"!prod", "!qa"})
@Configuration
public class DevelopmentConfig {
	@Bean
	public CommandLineRunner dataLoader(IngredientRepository repo, 
		UserRepository userRepo, PasswordEncoder encoder) {
		...
	}
}
```