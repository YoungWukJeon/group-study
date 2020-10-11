# 서킷 브레이커 이해하기

- 서킷 브레이커(circuit breaker) 패턴은 우리가 작성한 코드가 실행에 실패하는 경우에 안전하게 처리되도록 해준다.
- 이 강력한 패턴은 마이크로서비스의 컨텍스트에서 훨씬 더 중요하다.
- 한 마이크로서비스의 실패가 다른 마이크로서비스의 연쇄적인 실패로 확산되는 것을 방지해야 하기 때문이다.
- 소프트웨어 서킷 브레이커는 메서드의 호출을 허용하며, 서킷은 닫힘 상태(closed state)에서 시작된다.
- 그리고 어떤 이유로든 메서드의 실행이 실패하면(메서드 실행 횟수나 시간 등의 정의된 한곗값을 초과하면), 서킷 브레이커가 개방되고 실패한 메서드에 대해 더 이상 호출이 수행되지 않는다.
- 그러나 소프트웨어 서킷 브레이커는 폴백(fallback)을 제공하여 자체적으로 실패를 처리한다.

![chapter15-01](image/chapter15-01.png '서킷 브레이커 패턴은 폴백을 사용하여 실패를 처리한다.')

- 서킷 브레이커를 더 강력한 형태의 try/catch라고 생각하면 이해하는 데 도움이 될 수 있다.
- 즉, 닫힘 상태는 try 블록과 유사한 반면, 폴백 메서드는 catch 블록과 유사하다.
- 그러나 try/catch와 다르게, 서킷 브레이커는 원래 호출하려던 메서드(서킷 브레이커로 보호되는 메서드)가 너무 자주 실패하면(정의된 한계값을 초과하면) 폴백 메서드를 호출한다.
- 서킷 브레이커는 메서드에 적용된다.
- 대개는 다음 유형의 메서드들이 서킷 브레이커를 선언할(달리 말해, 서킷브레이커로 실패를 보호할) 후보들이다.
    - **REST를 호출하는 메서드** : 사용할 수 없거나 HTTP 500 응답을 반환하는 원격 서비스로 인해 실패할 수 있는 메서드다.
    - **데이터베이스 쿼리를 수행하는 메서드** : 어떤 이유로든 데이터베이스가 무반응 상태가 되거나, 애플리케이션을 중단시킬 수 있는 스키마의 변경이 생기면 실패할 수 있는 메서드다.
    - **느리게 실행될 가능성이 있는 메서드(지연, latency)** : 이것은 반드시 실패하는 메서드가 아니다. 그러나 너무 오랫동안 실행된다면 비정상적인 상태를 고려할 수 있다.
- 서킷 브레이커 패턴은 코드의 실패와 지연을 처리하는 강력한 수단이다.
- Netflix Hystrix는 서킷 브레이커 패턴을 자바로 구현한 라이브러리다.
- 간단히 말해서, Hystrix 서킷 브레이커는 대상 메서드가 실패할 때 폴백 메서드를 호출하는 어스팩트(aspect)로 구현된다.
- 그리고 서킷 브레이커 패턴을 제대로 구현하기 위해 어스펙트는 대상 메서드가 얼마나 자주 실패하는지도 추적한다.
- 그 다음에 실패율이 한계값을 초과하면 모든 대상 메서드 호출을 폴백 메서드 호출로 전달한다.

# 서킷 브레이커 선언하기

- 스프링 클라우드 Netflix Hystrix 스타터를 각 서비스의 빌드에 추가해야 한다.

```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-config-server</artifactId>
</dependency>
```

```xml
<properties>
	...
	<spring-cloud.version>Hoxton.SR3</spring-cloud.version>
</properties>

...

<dependencyManagement>
	<dependencies>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-dependencies</artifactId>
			<version>${spring-cloud.version}</version>
			<type>pom</type>
			<scope>import</scope>
		</dependency>
	</dependencies>
</dependencyManagement>
```

- Hystrix 스타터 의존성이 추가되었으므로 다음은 Hystrix를 활성화해야 한다.
- 이때는 각 애플리케이션의 메인 구성 클래스에 @EnableHystrix 애노테이션을 지정하면 된다.

```java
@SpringBootApplication
@EnableHystrix
public class IngredientServiceApplication {
	...
}
```

- 그러나 아직 어떤 메서드에도 서킷 브레이커가 선언되지 않았다.
- 바로 이때 @HystrixCommand 애노테이션이 필요하다.
- 어떤 메서드이건 @HystrixCommand가 지정되면 서킷 브레이커가 적용된다.

```java
public Iterable<Ingredient> getAllIngredients() {
	ParameterizedTypeReference<List<Ingredient>> stringList = 
		new ParameterizedTypeReference<List<Ingredient>>() {};
	return rest.exchange("http://ingredient-service/ingredients", HttpMethod.GET, 
		HttpEntity.EMPTY, stringList).getBody();
}
```

- 여기서는 exchange()의 호출이 문제를 유발할 수 있는 잠재적 원인이다.
- exchange() 메서드 내부를 보자.
- 만일 유레카에 ingredient-service로 등록된 서비스가 없거나 해당 요청이 어떤 이유로든 실패한다면 RestClientException(unchecked 예외)이 발생한다.
- 이 경우 exchange() 메서드에서는 try/catch 블록으로 예외를 처리하지 않으므로 exchange()를 호출한 호출자(메서드)에서 RestClientException 예외를 처리해야 한다.
- 그러나 해당 호출자에서도 이 예외를 처리하지 않는다면 호출 스택의 그다음 상위 호출자로 계속 예외가 전달될 것이다.
- 그러다가 어떤 호출자도 예외를 처리하지 않는다면 결국 최상위 호출자(마이크로서비스나 클라이언트)에서 에러로 처리될 것이다.
- 이처럼 처리가 되지 않은 unchecked 예외는 어떤 애플리케이션에서도 골칫거리이며, 특히 마이크로서비스의 경우가 그렇다.
- 장애가 생기면 마이크로서비스는 베가스 규칙(Vegas Rule)을 적용해야 한다.
- 즉, 마이크로서비스에서 생긴 에러는 다른 곳에 전파하지 않고 마이크로서비스에 남긴다는 얘기다.
- 서킷 브레이커를 선언할 때는 @HystrixCommand를 메서드에 지정하고 폴백 메서드를 제공하면 된다.

```java
@HystrixCommand(fallbackMethod="getDefaultIngredients")
public Iterable<Ingredient> getAllIngredients() {
	...
}
```

- 폴백 메서드는 우리가 원하는 어떤 것도 할 수 있지만, 원래 의도했던  메서드가 실행이 불가능할 때에 대비하는 의도로 사용한다.
- 단, 폴백 메서드는 원래의 메서드와 시그니처가 같아야 한다.
    - 메서드 이름만 다르다.

```java
private Iterable<Ingredient> getDefaultIngredients() {
	List<Ingredient> ingredients = new ArrayList<>();
	ingredients.add(new Ingredient("FLTO", "Flour Tortilla", Ingredient.Type.WRAP));
	ingredients.add(new Ingredient("GRBF", "Ground Beef", Ingredient.Type.PROTEIN));
	ingredients.add(new Ingredient("CHED", "Shredded Cheddar", Ingredient.Type.CHEESE));
	return ingredients;
}
```

- 그런데 폴백 메서드 자신도 서킷 브레이커를 가질 수 있는지 궁금할 것이다.
- 이 경우 getDefaultIngredients()에 @HystrixCommand를 지정하여 또 다른 폴백 메서드를 제공할 수 있다.
- 그리고 필요하다면 이런 식으로 많은 폴백 메서드를 연쇄적으로 지정할 수 있다.
    - 이 경우 연관된 모든 폴백 메서드 호출이 폴백 스택에 저장된다.
- 단, 한 가지 제약이 있다.
- 폴백 스택의 제일 밑에는 실행에 실패하지 않아서 서킷 브레이커가 필요 없는 메서드가 있어야 한다.

## 지연 시간 줄이기

- 또한, 서킷 브레이커는 메서드의 실행이 끝나고 복귀하는 시간이 너무 오래 걸릴 경우 타임아웃을 사용하여 지연 시간을 줄일 수도 있다.
- 기본적으로 @HystrixCommand가 지정된 모든 메서드는 1초 후에 타임아웃되고 이 메서드의 폴백 메서드가 호출된다.
- @HystrixCommand 애노테이션의 CommandProperties 속성을 통해 Hystrix 명령 속성을 설정할 수 있다.
- commandProperties 속성은 설정될 속성의 이름과 값은 지정하는 하나 이상의 @HystrixProperty 애노테이션을 저장한 배열이다.
- 서킷 브레이커의 타임아웃을 변경하려면 Hystrix 명령 속성인 execution.isolation.thread.timeoutInMilliseconds를 설정해야 한다.

```java
@HystrixCommand(
	faillbackMethod="getDefaultIngredients",
	commandProperties={
		@HystrixProperty(
			name="execution.isolation.thread.timeoutInMilliseconds",
			value="500")
	})
public Iterable<Ingredient> getAllIngredients() {
	...
}
```

- 타임아웃으로 지정되는 값의 단위는 1/1,000초이며, 타임아웃이 필요 없을 때는 명령 속성인 execution.timeout.enabled를 false로 설정함현 타임아웃을 없앨 수 있다.

```java
@HystrixCommand(
	faillbackMethod="getDefaultIngredients",
	commandProperties={
		@HystrixProperty(
			name="execution.timeout.enabled",
			value="false")
	})
public Iterable<Ingredient> getAllIngredients() {
	...
}
```

- 연쇄 지연 효과(cascading latency effect)가 발생할 수 있으므로 실행 타임아웃을 비활성화할 때는 조심해야 한다.

## 서킷 브레이커 한계값 관리하기

- Hystrix 명령 속성을 설정하면 실패와 재시도 한계값을 변경할 수 있다.
    - circuitBreaker.requestVolumeThreshold : 지정된 시간 내에 메서드가 호출되어야 하는 횟수
    - circuitBreaker.errorThresholdPercentage : 지정된 시간 내에 실패한 메서드 호출의 비율(%)
    - metrics.rollingStats.timeInMilliseconds : 요청 횟수와 에러 비율이 고려되는 시간
    - circuitBreaker.sleepWindowInMilliseconds : 절반-열림 상태로 진입하여 실패한 메서드가 다시 시도되기 전에 열림 상태의 서킷이 유지되는 시간
- 20초 이내에 메서드가 30번 이상 호출되어 이중에서 25% 이상이 실패일 경우다.

```java
@HystrixCommand(
	faillbackMethod="getDefaultIngredients",
	commandProperties={
		@HystrixProperty(
			name="circuitBreaker.requestVolumeThreshold",
			value="30"),
		@HystrixProperty(
			name="circuitBreaker.errorThresholdPercentage",
			value="25"),
		@HystrixProperty(
			name="metrics.rollingStats.timeInMilliseconds",
			value="20000")
	})
public List<Ingredient> getAllIngredients() {
	...
}
```

- 또한, 서킷 브레이커가 절반-열림 상태가 되기 전에 1분까지 열림 상태에 머물러야 한다면, 다음과 같이 circuitBreaker.sleepWindowInMilliseconds 명령 속성을 설정할 수도 있다.

```java
@HystrixCommand(
	faillbackMethod="getDefaultIngredients",
	commandProperties={
		...
		@HystrixProperty(
			name="circuitBreaker.sleepWindowInMilliseconds",
			value="60000")
	})
```

# 실패 모니터링하기

- 서킷 브레이커로 보호되는 메서드가 매번 호출될 때마다 해당 호출에 관한 여러 데이터가 수집되어 Hystrix 스트림으로 발행된다.
- 그리고 이 Hystrix 스트림은 실행 중인 애플리케이션의 건강 상태를 실시간으로 모니터링하는 데 사용할 수 있다.
- 각 서킷 브레이커로부터 수집한 데이터 중에서 Hystrix 스트림은 다음을 포함한다.
    - 메서드가 명 번 호출되는지
    - 성공적으로 몇 번 호출되는지
    - 폴백 메서드가 몇 번 호출되는지
    - 메서드가 몇 번 타임아웃되는지
- 이 Hystrix 스트림은 액추에이터 엔드포인트로 제공된다.
- 모든 서비스들이 Hystrix 스트림을 활성화하려면 액추에이터 의존성을 빌드에 추가해야 한다.

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

- Hystrix 스트림 엔드포인트는 /actuator/hystrix.stream 경로로 노출되어 있다.
- 대부분의 액추에이터 엔드포인트는 기본적으로 비활성화되어 있다.
- 그러나 각 애플리케이션의 application.yml 파일에 다음 구성을 추가하면 Hystrix 스트림 엔드포인트를 활성화할 수 있다.

```yaml
management:
	endpoints:
		web:
			exposure:
				include: hystrix.stream
```

- 그리고 스프링 구성 서버의 application.yml에 이와 동일한 구성 속성을 추가하면, 구성 서버의 모든 클라이언트 서비스가 이 구성 속성을 공유할 수 있다.
- Hystrix 스트림의 각 항목은 온갖 JSON 데이터로 가득 차 있으므로 이 데이터를 해석하기 위해 클라이언트 측의 작업이 많이 필요하다.
- 물론 이런 코드를 작성하는 것이 불가능한 것은 아니지만, 이때는 Hystrix 대시보드의 사용을 고려할 수 있다.

## Hystrix 대시보드 개요

- Hystrix 대시보드를 사용하려면 우선 Hystrix 대시보드 스타터 의존성을 갖는 새로운 스프링 부트 애플리케이션 프로젝트를 생성해야 한다.

```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
</dependency>
```

- 그다음에 Hystrix 대시보드를 활성화하기 위해 메인 구성 클래스에 @EnableHystrixDashboard 애노테이션을 지정해야 한다.

```java
@SpringBootApplication
@EnableHystrixDashboard
public class HystrixDashboardApplication {
	public static void main(String[] args) {
		SpringApplication.run(HystrixDashboardApplication.class, args);
	}
}
```

- 개발 시에는 로컬 컴퓨터에서 유레카와 구성 서버는 물론 다른 모든 서비스들과 함께 Hystrix 대시보드를 실행할 것이다.
- 따라서 다른 서비스와의 포트 충돌을 막기 위해 Hystrix 대시보드의 포트 번호를 고유한 것으로 선택해야 한다.

```yaml
server:
	port: 7979
```

![chapter15-02](image/chapter15-02.png 'Hystrix 대시보드 홈페이지')

- [http://localhost:7979/hystrix](http://localhost:7979/hystrix) 에 접속하면 다음과 같이 Hystrix 대시보드 홈페이지가 나타날 것이다.
- Delay는 폴링 간격 시간을 나타내며, 기본값은 2초이다.
- 즉, hystrix.stream 엔드포인트로부터 2초에 한 번씩 Hystrix 스트림을 받는다는 의미다.
- Title은 모니터 페이지의 제목로 나타난다.
- 그림
- 그림
- 모니터에서 가장 주목할 만한 부분은 왼쪽 위 모서리의  그래프다.
- 선 그래프는 지정된 메서드의 지난 2분 동안의 트래픽을 나타내며, 메서드가 얼마나 바쁘게 실행되었는지 간략하게 보여준다.
- 그래프의 배경에는 크기와 색상이 수시로 변동괴는 원이 있다.
- 원의 크기는 현재의 트래픽 수량을 나타내며, 트래픽 수량이 증가하면 원이 커진다.
- 원의 색상은 해당 서킷 브레이커의 건강 상태를 나타낸다.
- 초록색은 건강함을 나타내고, 노란색은 가끔 실패하는 서킷 브레이커를 나타내며, 빨간색은 실패한 서킷 브레이커를 나타낸다.
- 모니터의 오른쪽 위에서는 다양한 카운터를 세 열로 보여준다.
- 왼쪽 열의 위에서부터 첫 번째 번호는 현재 성공한 호출 횟수, 두 번째 번호는 숏-서킷(short-circuited) 요청 횟수, 그리고 마지막 번호는 잘못된 요청의 횟수를 나타낸다.
- 중간 열의 젱리 위 번호는 타임아웃된 요청 횟수, 그 아래 번호는 스레드 풀이 거부한 횟수, 제일 아래의 번호는 실패한 요청 횟수를 나타낸다.
- 그리고 제일 오른쪽 열은 지난 10초간의 에러 비율(%)을 나타낸다.
- 카운터 아래에는 호스트와 클러스터의 초당 요청 수를 나타내는 두 개의 숫자가 있다.
- 다시 그 아래에는 해당 서킷 브레이커의 상태가 있다.
- 모니터의 제일 아래에는 지연 시간의 중간값과 평균치 및 백분위 수(90번째, 99번째, 99.5번째)를 보여준다.

## Hystrix 스레드 풀 이해하기

- Hystrix는 각 의존성 모듈의 스레드 풀을 할당한다.
- 그리고 Hystrix 명령 메서드 중 하나가 호출될 때 이 메서드는 Hystrix가 관리하는 스레드 풀의 스레드(호출 스레드와 분리된)에서 실행된다.
- 따라서 이 메서드가 너무 오래 걸린다면 호출 스레드는 해당 호출을 포기하고 벗어날 수 있으므로 잠재적인 스레드 포화를 Hystrix가 관리하는 스레드 풀에 고립시킬 수 있다.
- 그림
- 서킷 브레이커 모니터와 흡사하게, 각 스레드 풀 모니터에는 왼쪽 위 모서리에 원이 있다.
- 이 원의 크기와 색상은 해당 스레드 풀이 현재 얼마나 활성적인지와 건강 상태를 나타낸다.
- 스레드 풀 모니터의 왼쪽 아래 모서리는 다음 정보들을 보여준다.
    - **활성 스레드 카운트** : 활성 스레드의 현재 개수
    - **큐 스레드 카운트** : 현재 큐에 있는 스레드 개수, 기본적으로 큐가 비활성화되어 있으므로 이 값은 항상 0이다.
    - **풀 크기** : 스레드 풀에 있는 스레드 개수
- 그리고 오른쪽 아래 모서리에는 스레드 풀에 관란 다음 정보들을 보여준다.
    - **최대 활성 스레드 카운트** : 샘플링 시간 동안의 최대 활성 스레드 개수
    - **실행 횟수** : Hystrix 명령의 실행을 처리하기 위해 스레드 풀의 스레드가 호출된 횟수
    - **큐 크기** : 스레드 풀 큐의 크기, 스레드 큐는 기본적으로 비활성화되어 있으므로 이 값은 의미가 없다.
- 스레드 풀의 대안으로 **semaphore isolation(세마포어 격리)**의 사용을 고려해볼 만하다.

# 다수의 Hystrix 스트림 종합하기

- Hystrix 대시보드는 한 번에 하나의 Hystrix 스트림만 모니터링할 수 있다.
- 애플리케이션에 있는 마이크로서비스의 인스턴스는 자신의 Hystrix 스트림만을 발행하므로 애플리케이션 전체의 건강 상태 정보를 얻는 것은 불가능하다.
- 그러나 다행히도 또다른 Netflix 프로젝트인 Turbine이 모든 마이크로서비스로부터 모든 Hystrix 스트림을 Hystrix 대시보드가 모니터링할 수 있는 하나의 스트림으로 종합하는 방법을 제공한다.
- Turbine 서비스를 생성하려면, 새로운 스프링 부트 프로젝트를 생성하고 Turbine 스타터 의존성을 빌드에 포함시켜야 한다.

```xml
 <dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-netflix-turbine</artifactId>
</dependency>
```

- 프로젝트가 생성되었으면 Turbine을 활성화해야 한다.
- 이때는 애플리케이션의 메인 구성 클래스에 @EnableTurbine 애노테이션을 지정한다.

```java
@SpringBootApplication
@EnableTurbine
public class TurbineServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(TurbineServerApplication.class, args);
	}
}
```

- 다른 서비스와의 충돌을 막기 위해 Turbine의 포트 번호를 고유한 것으로 선택해야 한다.

```yaml
server:
	port: 8989
```

- 이제는 다수의 마이크로서비스로부터 Hystrix 스트림이 소비되면 서킷 브레이커 메트릭들이 Turbine에 의해 하나의 Hystrix 스트림으로 종합될 것이다.
- Turbine은 유레카의 클라이언트로 작동하므로 Hystrix 스트림을 종합할 서비스들을 유레카에서 찾는다.
- 그러나 유레카에 등록된 모든 서비스의 Hystrix 스트림을 종합하지는 않는다.
- 따라서 Hystrix 스트리미을 종합할 서비스들을 알 수 있게 Turbine을 구성해야 한다.
- 이때 turbine.app-config 속성을 설정한다.
- turbine.app-config 속성에는 Hystrix 스트림을 종합하기 위해 유레카에서 찾을 서비스 이름들을 설정한다.
    - 쉼표를 구분자로 사용하여 여러 개를 지정할 수 있다.

```yaml
turbine:
	app-config: ingredient-service, taco-service, order-service, user-service
	cluster-name-expression: "'default'"
```

- turbine.app-config 속성에 추가하여 turbine.cluster-name-expression 속성도 'default'로 설정해야 한다.
- 이것은 이름이 default인 클러스터에 있는 모든 종합될 스트림을 Turbine이 수집해야 한다는 것을 나타낸다.
- 이 클러스터 이름을 설정하는 것은 중요하다.
- 만일 설정하지 않으면, 지정된 애플리케이션(마이크로서비스)들로부터 종합될 어떤 스트림 데이터도 Turbine 스트림에 포함되지 않기 때문이다.
- Turbine 서버 애플리케이션을 빌드하고 실행한 후에 Hystrix 대시보드 홈페이지에서 [http://localhost:8989/turbine.stream](http://localhost:8989/turbine.stream) 을 텍스트 상자에 입력하고(Delay와 Title은 각자 알아서 설정한다.) Monitor Stream 버튼을 클릭하면 Hystrix 스트림 모니터 페이지가 나타난다.
- 그림

# Hystrix와 Turbine을 사용한 식자재 클라이언트 서비스 빌드 및 실행하기

- 우선, 웹 브라우저에서 [http://localhost:7979/hystrix](http://localhost:7979/hystrix) 에 접속하면 Hystrix 대시보드 홈페이지가 나타난다.
- 그림
- 식자재 서비스 클라이언트를 사용하기 위해 웹 브라우저에서 새로운 창을 열고 [http://localhost:8080/ingredients](http://localhost:8080/ingredients) 에 접속하면 식자재 내역을 보여주는 페이지가 나타난다.
- 그림
- 새로고침 키(F5)를 여러 번 눌러서 서킷 브레이커가 설정된 getAllIngredients() 메서드가 여러 번 호출되게 한다.
- 그리고 가급적 빨리 웹 브라우저의 Hystrix 스트림 모니터 페이지 창으로 전환한다.
- 그러면 getAllIngredients() 메서드의 호출 횟수가 나타난다.
- 그림