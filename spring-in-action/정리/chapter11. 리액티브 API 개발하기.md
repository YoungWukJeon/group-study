- 스프링 WebFlux는 스프링 MVC와 매우 유사하며, 적용하기 쉽다.
- 그리고 스프링의 REST API 생성에 관해 우리가 이미 알고 있는 것의 많은 부분을 활용할 수 있다.

# 스프링 WebFlux 사용하기

- 매 연결마다 하나의 스레드를 사용하는 스프링 MVC 같은 전형적인 서블릿(Servlet) 기반의 웹 프레임워크는 스레드 블로킹(blocking, 차단)과 다중 스레드로 수행된다.
- 즉, 요청이 처리될 때 스레드 풀에서 작업 스레드를 가져와서 해당 요청을 처리하며, 작업 스레드가 종료될 때까지 요청 스레드는 블로킹한다.
- 따라서 블로킹 웹 프레임워크는 요청량의 증가에 따른 확장이 사실상 어렵다.
- 오늘날은 (인간이 관여하지도 않는 인공지능의) 사물인터넷(IoT, Internet of Things)이 자동차, 제트 엔진, 그리고 웹 API를 사용해서 끊임없이 데이터를 교환하는 다른 비전통적인 클라이언트를 등장시킨다.
- 이처럼 웹 애플리케이션을 사용하는 클라이언트의 수가 증가함에 따라 그 어느 때보다도 확장성이 더욱 중요해졌다.
- 이에 반해서 비동기 웹 프레임워크는 더 적은 수의 스레드(일반적으로 CPU 코어당 하나)로 더 높은 확장성을 성취한다.
- 이벤트 루핑(event looping)이라는 기법을 적용한 이런 프레임워크는 한 스레드당 많은 요청을 처리할 수 있어서 한 연결당 소요 비용이 더 경제적이다.

![chapter11-01](image/chapter11-01.png '비동기 웹 프레임워크는 이벤트 루핑을 적용하여 더 적은 수의 스레드로 더 많은 요청을 처리한다.')

- 데이터베이스나 네트워크 작업과 같은 집중적인 작업의 콜백과 요청을 비롯해서, 이벤트 루프에서는 모든 것이 이벤트로 처리된다.
- 비용이 드는 작업이 필요할 때 이벤트 루프는 해당 작업의 콜백(callback)을 등록하여 병행으로 수행되게 하고 다른 이벤트 처리로 넘어간다.
- 그리고 작업이 완료될 때 이것 역시 요청과 동일하게 이벤트로 처리된다.
- 결과적으로 비동기 웹 프레임워크는 소수의 스레드로 많은 요청을 처리할 수 있어서 스레드 관리 부담이 줄어들고 확장이 용이하다.

## 스프링 WebFlux 개요

- 리액티브 프로그래밍 모델을 스프링 MVC에 억지로 집어넣는 대신에 가능한 많은 것을 스프링 MVC로부터 가져와서 별도의 리액티브 웹 프레임워크를 만들기로 결정한 것이다.
    - 스프링 WebFlux가 바로 그 산물이다.

![chapter11-02](image/chapter11-02.png '스프링 5는 WebFlux라는 새로운 웹 프레임워크로 리액티브 웹 애플리케이션을 지원한다. WebFlux라는 스프링 MVC의 많은 핵심 컴포넌트를 공유한다.')

- 왼쪽은 스프링 프레임워크 2.5 버전에 소개되었던 스프링 MVC 스택이다.
- 스프링 MVC는 실행 시에 톰캣(Tomcat)과 같은 서블릿 컨테이너가 필요한 자바 서블릿 API의 상위 계층에 위치한다.
- 이에 반해서 오른쪽의 스프링 WebFlux는 서블릿 API와 연계되지 않는다.
- 따라서 서블릿 API가 제공하는 것과 동일한 기능의 리액티브 버전인 리액티브 HTTP API의 상위 계층에 위치한다.
- 그리고 스프링 WebFlux는 서블릿 API에 연결되지 않으므로 실행하기 위해 서블릿 컨테이너를 필요로 하지 않는다.
- 대신에 블로킹이 없는 어떤 웹 컨테이너에서도 실행될 수 있으며, 이에는 Netty, Undertow, 톰캣, Jetty 또는 다른 서블릿 3.1 이상의 컨테이너가 포함된다.
- 가장 주목할 만한 것은 제일 왼쪽 위의 네모에 있다.
    - 이것은 스프링 MVC와 스프링 WebFlux 간의 공통적인 컴포넌트들을 나타내며, 주로 컨트롤러를 정의한는 데 사용되는 애노테이션이다.
    - 스프링 MVC와 스프링 WebFlux는 같은 애노테이션을 공유하므로 여러 면에서 스프링 WebFlux는 스프링 MVC와 분간하기 어려울 정도다.
- 제일 위의 오른쪽 네모는 애노테이션을 사용하는 대신 함수형 프로그래밍 패러다임으로 컨트롤러를 정의하는 대안 프로그래밍 모델을 나타낸다.
- 스프링 WebFlux를 사용할 때는 표준 웹 스타터(예를 들어, spring-boot-starter-web) 대신 스프링 부트 WebFlux 스타터 의존성을 추가해야 한다.

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

- 스프링 MVC 대신 WebFlux를 사용할 때는 기본적인 내장 서버가 톰캣 대신 Netty가 된다.
    - Netty는 몇 안되는 비동기적인 이벤트 중심의 서버 중 하나이며, 스프링 WebFlux와 같은 리액티브 웹 프레임워크에 잘 맞는다.
- 스프링 WebFlux의 컨트롤러 메서드는 대개 도메인 타입이나 컬렉션 대신 Mono나 Flux 같은 리액티브 타입을 인자로 받거나 반환한다.
- 또한, 스프링 WebFlux 컨트롤러는 Observable, Single, Completable과 같은 RxJava 타입도 처리할 수 있다.

### 리액티브 스프링 MVC?

- 스프링 MVC의 컨트롤러 메서드도 Mono나 Flux를 반환할 수 있다.
- 스프링 WebFlux는 요청이 이벤트 루프로 처리되는 진정한 리액티브 웹 프레임워크인 반면, 스프링 MVC는 다중 스레드에 의존하여 다수의 요청을 처리하는 서블릿 기반 웹 프레임워크다.

## 리액티브 컨트롤러 작성하기

```java
@RestController
@RequestMapping(path="/design", produces="application/json")
@CrossOrigin(origins="*")
public class DesignTacoController {
	...
	@GetMapping("/recent")
	public Iterable<Taco> recentTacos() {
		PageRequest page = PageRequest.of(0, 12, Sort.by("createdAt").descending());
		return tacoRepo.findAll(page).getContent();
	}
	...
}
```

- 이 코드는 잘 작동한다.
- 그러나 Iterable은 리액티브 타입이 아니다.
- 따라서 Iterable에는 어떤 리액티브 오퍼레이션도 적용할 수 없으며, 또한 프레임워크가 Iterable 타입을 리액티브 타입으로 사용하여 여러 스레드에 걸쳐 작업을 분할하게 할 수도 없다.
- 여기서 우리가 하려는 것은 리액티브 타입인 `Flux<Taco>` 타입을 recentTacos()가 반환하게 하는 것이다.
- 그러나 Iterable 타입을 Flux 타입으로 변환하기 위해 recentTacos()를 다시 작성하는 것은 간단하지만 고려할 것이 있다.
- 그리고 이렇게 하는 김에 recentTacos()의 페이징 코드도 Flux의 take() 호출로 교체할 수 있다.

```java
@GetMapping("/recent")
public Flux<Taco> recentTacos() {
	return Flux.fromIterable(tacoRepo.findAll()).take(12);
}
```

- 지금 작성한 리액티브 코드는 잘 되었다.
- 그러나 타입을 변환할 필요가 없도록 아예 해당 리퍼지터리에서 Flux 타입을 반환한다면 더 좋을 것이다.

```java
@GetMapping("/recent")
public Flux<Taco> recentTacos() {
	return tacoRepo.findAll().take(12);
}
```

- 이상적으로는 리액티브 컨트롤러가 리액티브 엔드-to-엔드 스택의 제일 끝에 위치하며, 이 스택에는 컨트롤러, 리퍼지터리, 데이터베이스, 그리고 여타 서비스가 포함된다.

![chapter11-03](image/chapter11-03.png '리액티브 웹 프레임워크의 장점을 극대화하려면 완전한 엔드-to-엔드 리액티브 스택의 일부가 되어야 한다.')

- 이런 엔드-to-엔드 스택에서는 Iterable 대신 Flux를 반환하도록 리퍼지터리가 작성되어야 한다.

```java
public interface TacoRepository extends ReactiveCrudRepository<Taco, Long> {
}
```

- Iterable 대신 Flux를 사용하는 것 외에, 이 시점에서 리액티브 WebFlux 컨트롤러에 관해 알아 둘 것 중 가장 중요한 것이 있다.
- 리액티브 WebFlux 컨트롤러를 정의하기 위한 프로그래밍 모델을 리액티브가 아닌 스프링 MVC 컨트롤러와 크게 다르지 않다는 것이다.
- 이외에 중요한 것으로는, 리퍼지터리로부터 `Flux<Taco>`와 같은 리액티브 타입을 받을 때 subscribe()를 호출할 필요가 없다는 것이다.
    - 프레임워크가 호출해 주기 때문이다.

### 단일 값 반환하기

```java
@GetMapping("/{id}")
public Taco tacoById(@PathVariable("id") Long id) {
	Optional<Taco> optTaco = tacoRepo.findById(id);
	if (optTaco.isPresent()) {
		return optTaco.get();
	}
	return null;
}
```

- 이때 해당 리퍼지터리의 findById() 메서드가 Optional 타입을 반환하므로 위의 코드에서 보듯이 이 타입을 처리하는 코드도 추가로 작성해야 했다.

```java
@GetMapping("/{id}")
public Mono<Taco> tacoById(@PathVariable("id") Long id) {
	return tacoRepo.findById(id);
}
```

- 도메인 객체인 Taco 대신 `Mono<Taco>` 리액티브 타입 객체를 반환하므로 스프링 WebFlux가 리액티브 방식으로 응답을 처리할 수 있다는 것이다.
- 이에 따라 많은 요청에 대한 응답 처리 시에 우리 API의 확장성이 더 좋아진다.

### RxJava 타입 사용하기

- 스프링 WebFlux를 사용할 때 Flux나 Mono와 같은 리액티브 타입이 자연스러운 선택이지만, Observable이나 Single과 같은 RxJava 타입을 사용할 수도 있다는 것도 알아 두자.

```java
@GetMapping("/{recent}")
public Observable<Taco> recentTacos() {
	return tacoService.getRecentTacos();
}
```

- 이와 유사하게 Mono가 아닌 RxJava의 Single 타입을 처리하기 위해 다음과 같이 tacoById() 메서드를 작성할 수 있다.

```java
@GetMapping("/{id}")
public Single<Taco> tacoById(@PathVariable("id") Long id) {
	return tacoService.lookupTaco(id);
}
```

- 이와 더불어 스프링 WebFlux 컨트롤러의 메서드는 리액터의 `Mono<Void>` 타입과 동일한 RxJava의 Completable 타입을 반환할 수도 있다.
- WebFlux는 또한 Observable이나 리액터 Flux 타입의 대안으로 Flowable 타입을 반환할 수도 있다.

### 리액티브하게 입력 처리하기

- 스프링 WebFlux를 사용할 때 요청을 처리하는 핸들러 메서드의 입력으로도 Mono나 Flux를 받을 수 있다.

```java
@PostMapping(consumes="application/json")
@ResponseStatus(HttpStatus.CREATED)
public Taco postTaco(@RequestBody Taco taco) {
	return tacoRepo.save(taco);
}
```

- 요청은 두 번 블로킹된다.
    - postTaco()로 진입할 때와 postTaco()의 내부에서다.
- 그러나 postTaco()에 조금만 리액티브 코드를 적용하면 완전하게 블로킹되지 않는 요청 처리 메서드로 만들 수 있다.

```java
@PostMapping(consumes="application/json")
@ResponseStatus(HttpStatus.CREATED)
public Mono<Taco> postTaco(@RequestBody Mono<Taco> tacoMono) {
	return tacoRepo.saveAll(tacoMono).next();
}
```

- 다음 장에서 알게 되겠지만, saveAll() 메서드는 Mono나 Flux를 포함해서 리액티브 스트림의 Publisher 인터페이스를 구현한 어떤 타입도 인자로 받을 수 있다.
- saveAll() 메서드는 `Flux<Taco>`를 반환한다.
- 그러나 postTaco()의 인자로 전달된 Mono를 saveAll()에서 인자로 받았으므로 saveAll()이 반환하는 Flux가 하나의 Taco 객체만 포함한다는 것을 우리는 알고 있다.
- 따라서 next()를 호출하여 `Mono<Taco>`로 받을 수 있으며, 이것을 postTaco()가 반환한다.

# 함수형 요청 핸들러 정의하기

- 스프링 MVC의 애노테이션 기반 프로그래밍 모델은 스프링 2.5부터 있었고 지금도 널리 사용되고 있다.
- 그러나 몇 가지 단점이 있다.
- 우선, 애노테이션 기반 프로그래밍이건 애노테이션 '무엇(what)'을 하는지와 '어떻게(how)' 해야 하는지를 정의하는 데 괴리가 있다.
    - 애노테이션 자체는 '무엇'을 정의하며, '어떻게'는 프레임워크 코드의 어딘가에 정의되어 있다.
    - 이로 인해 프로그래밍 모델을 커스터마이징하거나 확장할 때 복잡해진다.
    - 이런 변경을 하려면 애노테이션 외부에 있는 코드를 작업해야 하기 때문이다.
    - 게다가 이런 코드의 디버깅을 까다롭다.
    - 애노테이션에 중단점(breakpoint)을 설정할 수 없기 때문이다.
- 또한, 다른 언어나 프레임워크를 사용했지만 스프링이 처음인 개발자들은 애노테이션 기반의 스프링 MVC(그리고 WebFlux)가 그들이 이미 알고 있던 것과 매우 다르다는 것을 발견할 수 있다.
    - 따라서 WebFlux의 대안으로 스프링 5에는 리액티브 API를 정의하기 위한 새로운 함수형 프로그래밍 모델이 소개되었다.
- 이런 새로운 프로그래밍 모델은 프레임워크보다는 라이브러리 형태로 사용되므로 애노테이션을 사용하지 않고 요청을 핸들러 코드에 연관시킨다.
- 스프링의 함수형 프로그래밍 모델을 사용한 API의 작성에는 다음 네 가지 기본 타입이 수반된다.
    - RequestPredicate : 처리될 요청의 종류를 선언한다.
    - RouterFunction : 일치하는 요청이 어떻게 핸들러에게 전달되어야 하는지를 선언한다.
    - ServerRequest : HTTP 요청을 나타내며, 헤더와 몸체 정보를 사용할 수 있다.
    - ServerResponse : HTTP 응답을 나타내며, 헤더와 몸체 정보를 포함한다.

```java
package demo;

import static org.springframework.web.reactive.function.server.RequestPredicate.GET;
import static org.springframework.web.reactive.function.server.RouterFunction.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static reactor.core.publisher.Mono.just;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;

@Configuration
public class RouterFunctionConfig {
	@Bean
	public RouterFunction<?> helloRouterFunction() {
		return route(GET("/hello"),
			request -> ok().body(just("Hello World!"), String.class));
	}
}
```

- @Configuration이 지정된 RouterFunctionConfig 클래스에는 `RouterFunction<?>` 타입의 @Bean 메서드가 하나 있다.
- 이미 얘기했듯이, RouteFunction은 요청을 나타내는 RequestPredicate 객체가 어떤 요청 처리 함수와 연관되는지를 선언한다.
- RouterFunction의 route() 메서드는 두 개의 인자를 받는다.
    - 하나는 RequestPredicate 객체이고, 다른 하나는 일치하는 요청을 처리하는 함수다.
    - 두 번째 인자로 전달된 핸들러 함수는 메서드 참조가 될 수도 있지만, 여기서는 람다로 작성하였다.
    - 그리고 명시적으로 선언되지 않았지만, 요청 처리 람다에서는 ServerRequest를 인자로 받으며, ServerResponse의 ok() 메서드와 이 메서드에서 반환된 BodyBuilder의 body()를 사용해서 ServerResponse를 반환한다.
    - 그리고 실행이 완료되면 HTTP 200(OK) 상태 코드를 갖는 응답과 'Hello World!'를 갖는 몸체 페이로드가 생성된다.
- 다른 종류의 요청을 처리해야 하더라도 또 다른 @Bean 메서드를 작성할 필요가 없다.
- 대신에 andRoute()를 호출하여 또 다른 RequestPredicate 객체가 어떤 요청 처리 함수와 연관되는지만 선언하면 된다.

```java
@Bean
public RouterFunction<?> helloRouterFunction() {
	return route(GET("/hello"),
			request -> ok().body(just("Hello World!"), String.class))
		.andRoute(GET("/bye"),
			request -> ok().body(just("See ya!"), String.class));
}
```

```java
@Configuration
public class RouterFunctionConfig {
	@Autowired
	private TacoRepository tacoRepo;

	@Bean
	public RouterFunction<?> routerFunction() {
		return route(GET("/design/taco"), this::recents)
			.andRoute(POST("/design"), this::postTaco);
	}

	public Mono<ServerResponse> recents(ServerRequest request) {
		return ServerResponse.ok()
			.body(tacoRepo.findAll().take(12), Taco.class);
	}

	public Mono<ServerResponse> postTaco(ServerRequest request) {
		Mono<Taco> taco = request.bodyToMono(Taco.class);
		Mono<Taco> savedTaco = tacoRepo.save(taco);
		return ServerResponse
			.created(URI.create(
				"http://localhost:8080/design/taco/" +
				savedTaco.getId()))
			.body(savedTaco, Taco.class);
	}
}
```

- 여기서 특히 눈에 띄는 것은 람다가 아닌 메서드 참조로 경로가 처리된다는 것이다.
- RouterFunction의 내부 기능이 간단할 때는 람다가 아주 좋다.
- 그러나 여러 경우에서 해당 기능을 별도의 메서드(또는 별도 클래스의 메서드)로 추출하고 메서드 참조를 사용하는 것이 코드 파악에 더 좋다.

# 리액티브 컨트롤러 테스트하기

- 리액티브 컨트롤러의 테스트에서도 스프링 5는 우리를 저버리지 않고 WebTestClient를 소개하였다.
- 것은 스프링 WebFlux를 사용하는 리액티브 컨트롤러의 테스트를 쉽게 작성하게 해주는 새로운 테스트 유틸리티다.

## GET 요청 테스트하기

```java
package tacos;

import static org.mockito.Mockito.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import tacos.Ingredient.Type;
import tacos.data.TacoRepository;
import tacos.web.api.DesignTacoController;

public class DesignTacoControllerTest {
	@Test
	public void shouldReturnRecentTacos() {
		Taco[] tacos = {
			testTaco(1L), testTaco(2L), // 테스트 데이터를 생성한다.
			testTaco(3L), testTaco(4L),
			testTaco(5L), testTaco(6L),
			testTaco(7L), testTaco(8L),
			testTaco(9L), testTaco(10L),
			testTaco(11L), testTaco(12L),
			testTaco(13L), testTaco(14L),
			testTaco(15L), testTaco(16L)};
		Flux<Taco> tacoFlux = Flux.just(tacos);

		TacoRepository tacoRepo = Mockito.mock(TacoRepository.class);
		when(tacoRepo.findAll()).thenReturn(tacoFlux); // 모의 TacoRepository

		WebTestClient testClient = WebTestClient.bindToController(
				new DesignTacoController(tacoRepo))
			.build(); // WebTestClient를 생성한다.

		testClient.get().uri("/design/recent")
			.exchange() // 가장 최근 타코들을 요청한다.
			.expectStatus().isOk() // 우리가 기대한 응답인지 검사한다.
			.expectBody()
				.jsonPath("$").isArray()
				.jsonPath("$").isNotEmpty()
				.jsonPath("$[0].id").isEqualTo(tacos[0].getId().toString())
				.jsonPath("$[0].name").isEqualTo("Taco 1")
				.jsonPath("$[1].id").isEqualTo(tacos[1].getId().toString())
				.jsonPath("$[1].name").isEqualTo("Taco 2")
				.jsonPath("$[11].id").isEqualTo(tacos[11].getId().toString())
				...
				.jsonPath("$[11].name").isEqualTo("Taco 12")
				.jsonPath("$[12]").doesNotExist();
	}
	...
}
```

- Flux가 발행하는 Taco 객체는 testTaco()라는 이름의 유틸리티 메서드에서 생성되며, 이 메서드에서는 인자로 받은 숫자로 ID와 이름을 갖는 Taco 객체를 생성한다.

```java
private Taco testTaco(Long number) {
	Taco taco = new Taco();
	taco.setId(UUID.randomUUID());
	taco.setName("Taco " + number);
	List<IngredientUDT> ingredients = new ArrayList<>();
	ingredients.add(
		new IngredientUDT("INGA", "Ingredient A", Type.WRAP));
	ingredients.add(
		new IngredientUDT("INGB", "Ingredient B", Type.PROTEIN));
	taco.setIngredients(ingredients);
	return taco;
}
```

- `get().uri("/design/recent")`의 호출은 제출(submit) 요청을 나타내며, 그 다음에 exchange()를 호출하면 해당 요청을 제출한다.
- 그리고 이 요청은 WebTestClient와 연결된 컨트롤러인 DesignTacoController에 의해 처리된다.
- 마지막으로 요청 응답이 우리가 기대한 것인지 검사한다.
    - 우선, expectStatus()를 호출하여 응답이 HTTP 200 (OK) 상태 코드를 갖는지 확인한다.
    - 그다음에 jsonPath()를 여러 번 호출하여 응답 몸체의 JSON이 기대한 값을 갖는지 검사한다.
    - 제일 끝의 어서션(`.jsonPath("$[12]").doesNotExist();`)에서는 인덱스 값이 12인 요소의 존재 여부를 검사한다.
    - 왜냐하면 배열 첫 번째 요소는 인덱스 값이 0부터 시작하므로 인덱스 값이 12인 요소는 응답의 JSON에 존재하면 안 되기 때문이다.
- 응답의 JSON 데이터가 많거나 중첩이 심해서 복잡할 경우에는 jsonPath()를 사용하기 번거로울 수 있다.
- 이런 경우를 위해 WebTestClient는 json() 메서드를 제공한다.
    - json()은 JSON을 포함하는 String을 인자로 받아 이것을 응답의 것과 비교한다.
- 예를 들어, recent-tacos.json이라는 파일에 완벽한 응답 JSON을 생성하여 /tacos 경로의 classpath에 저장했다고 해보자.

```java
ClassPathResource recentsResource =
	new ClassPathResource("/tacos/recent-tacos.json");
String recentsJson = StreamUtils.copyToString(
	recentsResource.getInputStream(), Charset.defaultCharset());

testClient.get().uri("/design/recent")
	.accept(MediaType.APPLICATION_JSON)
	.exchange()
	.expectStatus().isOk()
	.expectBody()
	.json(recentsJson);
```

- json() 메서든 String 타입의 인자를 받으므로 우선 classpath의 리소스를 String 타입으로 로드해야 한다.
- 이때 스프링에서 제공하는 StreamUtils의 copyToString() 메서드를 사용하면 쉽다.
    - copyToString()이 반환하는 String 값은 우리가 요청 응답에 기대하는 전체 JSON을 포함한다.
- WebTestClient는 리스트 형태로 여러 개의 값을 갖는 응답 몸체를 비교할 수 있는 expectBodyList() 메서드도 제공한다.
    - 이 메서드는 리스트에 있는 요소의 타입을 나타내는 Class나 ParameterizedTypeReference를 인자로 받아 어서션을 수행할 ListBodySpec 객체를 반환한다.

```java
testClient.get().uri("/design/recent")
	.accept(MediaType.APPLICATION_JSON)
	.exchange()
	.expectStatus().isOk()
	.expectBodyList(Taco.class)
	.contains(Arrays.copyOf(tacos, 12));
```

- 이 코드에서는 응답 몸체가 List(테스트 메서드의 맨 앞에서 생성했던 원래의 Taco 배열에 저장된 것과 동일한 12개 요소를 갖는)를 포함하는지 검사하는 어서션을 수행한다.

## POST 요청 테스트하기

- 다음 표에서는 HTTP 메서드와 WebTestClient 메서드 간의 연관성을 보여준다.
    - WebTestClient는 스프링 WebFlux 컨트롤러에 대해 어떤 종류의 요청도 테스트 가능
    
    | HTTP 메서드 | WebTestClient 메서드 |
    | --- | --- |
    | GET | .get() |
    | POST | .post() |
    | PUT | .put() |
    | PATCH | .patch() |
    | DELETE | .delete() |
    | HEAD | .head() |

```java
@Test
public void shouldSaveATaco() {
	TacoRepository tacoRepo = Mockito.mock(TacoRepository.class); // 테스트 데이터를 설정한다.
	Mono<Taco> unsavedTacoMono = Mono.just(testTaco(null));
	Taco savedTaco = testTaco(null);
	savedTaco.setId(1L);
	Mono<Taco> savedTacoMono = Mono.just(savedTaco);

	when(tacoRepo.save(any())).thenReturn(savedTacoMono); // 모의 TestRepository

	WebTestClient testClient = WebTestClient.bindToController( // WebTestClient를 생성한다.
		new DesignTacoController(tacoRepo)).build();

	testClient.post() // 타코를 POST한다.
		.uri("/design")
		.contentType(MediaType.APPLICATION_JSON)
		.body(unsavedTacoMono, Taco.class)
		.exchange()
		.expectStatus().isCreated() // 응답을 검사한다.
		.expectBody(Taco.class)
		.isEqualTo(savedTaco);
}
```

- 이 요청에는 application/json 타입의 몸체와 페이로드(JSON으로 직렬화된 형태의 Taco를 갖는 저장되지 않은 Mono)가 포함된다.
- 그 다음에 exchange()를 실행한 후 응답이 HTTP 201 (CREATED) 상태 코드를 갖는지, 그리고 저장된 Taco 객체와 동일한 페이로드를 응답 몸체에 갖는지 어서션으로 검사한다.

## 실행 중인 서버로 테스트하기

- 지금까지 작성했던 테스트는 모의 스프링 WebFlux 프레임워크를 사용했으므로 실제 서버가 필요 없었다.
- 그러나 Netty나 톰캣과 같은 서버 환경에서 리퍼지터리나 다른 의존성 모듈을 사용해서 WebFlux 컨트롤러를 테스트할 필요가 있을 수 있다.
- 다시 말해서, 통합 테스트를 작성할 수 있다.
- WebTestClient의 통합 테스트를 작성하기 위해서는 다른 스프링 부트 통합 테스트처럼 @RunWith와 @SpringBootTest 애노테이션을 테스트 클래스에 지정하는 것부터 시작해야 한다.

```java
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class DesignTacoControllerWebTest {
	@Autowired
	private WebTestClient testClient;
}
```

- webEnvironment 속성을 WebEnvironment.RANDOM_PORT로 설정하면 무작위로 선택된 포트로 실행 서버가 리스닝하도록 스프링에 요청한다.
    - webEnvironment를 WebEnvironment.DEFINED_PORT로 설정하고 특정 포트를 속성에 지정할 수도 있었지만, 이것은 권장할 만한 것이 아니다.
    - 이렇게 하면 동시적으로 실행되는 서버의 포트가 크래시되는 위험이 발생한다.
- 여기서는 또한 @Autowired를 지정하여 WebTestClient를 테스트 클래스로 자동 연결하였다.
- 따라서 테스트 메서드에서 WebTestClient 인스턴스를 더 이상 생성할 필요가 없는 것은 물론이고, 요청할 때 완전한 URL을 지정할 필요도 없다.
- 왜냐하면 테스트 서버가 어떤 포트에서 실행 중인지 알 수 있게 WebTestClient가 설정되기 때문이다.

```java
@Test
public void shouldReturnRecentTacos() throws IOException {
	testClient.get().uri("/design/recent")
		.accept(MediaType.APPLICATION_JSON).exchange()
		.expectStatus().isOk()
		.expectBody()
			.jsonPath("$[?(@.id == 'TACO1')].name").isEqualTo("Carnivore")
			.jsonPath("$[?(@.id == 'TACO2')].name").isEqualTo("Bovine Bounty")
			.jsonPath("$[?(@.id == 'TACO3')].name").isEqualTo("Veg-Out");
}
```

# REST API를 리액티브하게 사용하기

- RestTemplate을 사용해서 타코 클라우드 API의 클라이언트 요청을 하였다.
    - 스프링 3.0 버전에 소개되었던 RestTemplate은 이제 구세대가 되었다.
    - 그 당시에는 많은 애플리케이션이 무수한 요청에 RestTemplate을 사용했다.
    - 그러나 RestTemplate이 제공하는 모든 메서드는 리액티브가 아닌 도메인 타입이나 컬렉션을 처리한다.
- 따라서 리액티브 방식으로 응답 데이터를 사용하고자 한다면, 이것을 Flux나, Mono 타입으로 래핑해야 한다.
- 그리고 이미 Flux나 Mono 타입이 있으면서 POST나 PUT 요청으로 전송하고 싶다면, 요청을 하기 전에 Flux나 Mono 데이터를 리액티브가 아닌 타입으로 추출해야 한다.
- 스프링 5가 RestTemplate의 리액티브 대안으로 WebClient를 제공하니까 말이다.
    - WebClient는 외부 API로 요청을 할 때 리액티브 타입의 전송과 수신 모두를 한다.
    - WebClient의 사용은 RestTemplate을 사용하는 것과 많이 다르다.
    - 다수의 메서드로 서로 다른 종류의 요청을 처리하는 대신 WebClient는 요청을 나타내고 전송하게 해주는 빌더 방식의 인터페이스를 사용한다.
    - WebClient를 사용하는 일반적인 패턴은 다음과 같다.
        - WebClient의 인스턴스를 생성한다. (또는 WebClient 빈을 주입한다.)
        - 요청을 전송할 HTTP 메서드를 지정한다.
        - 요청에 필요한 URI와 헤더를 지정한다.
        - 요청을 제출한다.
        - 응답을 소비(사용)한다.

## 리소스 얻기(GET)

- WebClient의 사용 예로 타크 클라우드 API로부터 식자재를 나타내느 특정 Ingredient 객체를 이것의 ID를 사용해서 가져와야 한다고 해보자.

```java
Mono<Ingredient> ingredient = WebClient.create()
	.get()
	.uri("http://localhost:8080/ingredients/{id}", ingredientId)
	.retrieve()
	.bodyToMono(Ingredient.class);

ingredient.subscribe(i -> { ... })
```

- 이 예에서는 create() 메서드로 새로운 WebClient 인스턴스를 생성한다.
- 그다음에 get()과 uri()를 사용해서 `http://localhost:8080/ingredients/{id}` 에 대한 GET 요청을 정의한다.
- 여기서 {id} 플레이스 홀더는 ingredientId의 값으로 교체될 것이다.
- retrieve() 메서드는 해당 요청을 실행한다.
- 마지막으로 bodyToMono() 호출에서는 응답 몸체의 페이로드를 `Mono<Ingredient>`로 추출한다.
- 따라서 이 코드 다음에는 계속해서 Mono의 다른 오퍼레이션들을 연쇄 호출할 수 있다.
- bodyToMono()로부터 반환되는 Mono에 추가로 오퍼레이션을 적용하려면 해당 요청이 전송되기 전에 구독을 해야 한다.
- 따라서 이 예의 제일 끝에서는 subscribe() 메서드를 호출한다.

```java
Flux<Ingredient> ingredients = WebClient.create()
	.get()
	.uri("http://localhost:8080/ingredients")
	.retrieve()
	.bodyToFlux(Ingredient.class);

ingredients.subscribe(i -> { ... });
```

- 대체로 다수의 항목을 가져오는 것은 단일 항목을 요청하는 것과 동일하다.
- 단지 큰 차이점이라면, bodyToMono()를 사용해서 응답 몸체를 Mono로 추출하는 대신 bodyToFlux를 사용해서 Flux로 추출하는 것이다.
- bodyToMono()와 마찬가지로 bodyToFlux()로부터 반환된 Flux는 아직 구독되지 않았다.
- 따라서 추가적인 오퍼레이션(filter, map 등)을 이 Flux에 적용한 후 데이터가 이 Flux를 통해 전달되도록 할 수 있다.
- 그리고 결과 Flux를 구독하지 않으면 이 요청은 결코 전송되지 않을 것이므로 subscribe() 메서드를 호출하는 코드가 제일 끝에 추가된다.

### 기본 URI로 요청하기

- 기본 URI는 서로 다른 많은 요청에서 사용할 수 있다.
- 이 경우 기본 URI를 갖는 WebClient 빈을 생성하고 어디든지 필요한 곳에서 주입하는 것이 유용할 것이다.
- 이 빈은 다음과 같이 선언할 수 있다.

```java
@Bean
public WebClient webClient() {
	return WebClient.create("http://localhost:8080");
}
```

```java
@Autowired
WebClient webClient;

public Mono<Ingredient> getIngredientById(String ingredientId) {
	Mono<Ingredient> ingredient = webClient
		.get()
		.uri("/ingredients/{id}", ingredientId)
		.retrieve()
		.bodyToMono(Ingredient.class);
	ingredient.subscribe(i -> { ... });
}
```

- WebClient는 바로 앞의 선언 코드에서 이미 생성되었으므로 get()을 호출하여 작업을 수행할 수 있다.
- 그리고 uri() 메서드의 인자로 전달되는 URI에는 기본 URI에 대한 상대 경로만 지정하면 된다.

### 오래 실행되는 요청 타임아웃시키기

- 느려 터진 네트워크나 서비스 때문에 클라이언트의 요청이 지체되는 것을 방지하기 위해 Flux나 Mono의 timeout() 메서드를 사용해서 데이터를 기다리는 시간을 제한할 수 있다.

```java
Flux<Ingredient> ingredients = WebClient.create()
	.get()
	.uri("http://localhost:8080/ingredients")
	.retrieve()
	.bodyToFlux(Ingredient.class);

ingredients
	.timeout(Duration.ofSeconds(1))
	.subscribe(
		i -> { ... },
		e -> {
			// handle timeout error
		});
```

- 1초보다 더 오래 걸리면 타임아웃이 되어 subscribe()의 두 번째 인자로 지정된 에러 핸들러가 호출된다.

## 리소스 전송하기

```java
Mono<Ingredient> ingredientMono = ...;
Mono<Ingredient> result = webClient
	.post()
	.uri("/ingredients")
	.body(ingredientMono, Ingredient.class)
	.retrieve()
	.bodyToMono(Ingredient.class);

result.subscribe(i -> { ... });
```

- 만일 전송할 Mono나 Flux가 없는 대신 도메인 객체가 있다면 syncBody()를 사용할 수 있다.

```java
Ingredient ingredient = ...;

Mono<Ingredient> result = webClient
	.post()
	.uri("/ingredients")
	.syncBody(ingredient)
	.retrieve()
	.bodyToMono(Ingredient.class);
result.subscribe(i -> { ... });
```

- 만일 POST 요청 대신 PUT 요청으로 Ingredient 객체를 변경하고 싶다면 post() 대신 put()을 호출하고 이에 맞춰 URI 경로를 조정하면 된다.

```java
Mono<Void> result = webClient
	.put()
	.uri("/ingredients/{id}", ingredient.getId())
	.syncBody(ingredient)
	.retrieve()
	.bodyToMono(Void.class)
	.subscribe();
```

- 일반적으로 PUT 요청은 비어 있는 응답 페이로드를 갖는다.
- 따라서 Void 타입의 Mono를 반환하도록 bodyToMono()에 요구해야 한다.
    - Void.class를 인자로 전달

## 리소스 삭제하기

```java
Mono<Void> result = webClient
	.delete()
	.uri("/ingredients/{id}", ingredientId)
	.retrieve()
	.bodyToMono(Void.class)
	.subscribe();
```

- PUT 요청처럼 DELETE 요청도 응답 페이로드를 갖지 않는다.
- 다시 말하지만, 요청을 전송하려면 bodyToMono()에서 `Mono<Void>`를 반환하고 subscribe()로 구독해야 한다.

## 에러 처리하기

- 어떤 상태 코드가 반환되더라도 WebClient는 그것을 로깅하며, 그렇지 않으면 아무 조치 없이 무시한다.
- 에러를 처리해야 할 때는 onStatus() 메서드를 호출하며, 이때 처리해야 할 HTTP 상태 코드를 지정할 수 있다.
- onStatus()는 두 개의 함수를 인자로 받는다.
- 처리해야 할 HTTP 상태와 일치시키는 데 사용되는 조건 함수와 `Mono<Throwable>`을 반환하는 함수다.

```java
Mono<Ingredient> ingredientMono = webClient
	.get()
	.uri("http://localhost:8080/ingredients/{id}", ingredientId)
	.retrieve()
	.bodyToMono(Ingredient.class);
```

- 에러가 생길 수 있는 Mono나 Flux를 구독할 때는 subscribe() 메서드를 호출할 때 데이터 컨슈머는 물론 에러 컨슈머도 등록하는 것이 중요하다.

```java
ingredientMono.subscribe(
	ingredient -> {
		// 식자재 데이터를 처리한다.
		...
	},
	error -> {
		// 에러를 처리한다.
		...
	});
```

- 이 경우 지정된 ID와 일치하는 식자재 리소스를 찾으면 subscribe()의 첫 번째 인자로 전달된 람다(데이터 컨슈머)가 일치된 Ingredient 객체를 받아 실행된다.
- 그러나 만일 못 찾으면 요청 응답이 HTTP 404 (NOT FOUND) 상태 코드를 갖게 되고, 두 번째 인자로 전달된 람다(에러 컨슈머)가 실행되어 기본적으로 WebClientResponseException을 발생시킨다.
- 그러나 WebClientResponseException은 구체적인 예외를 나타내는 것이 아니므로 Mono에 무엇이 잘되었는지 정확히 알 수 없다.
- WebClientResponseException이라는 이름에서 암시하듯, WebClient의 요청 응답에 에러가 생겼다는 것만 알 수 있을 뿐이다.
- 따라서 무엇이 잘못되었는지 자세히 알 수 있는 예외를 에러 컨슈머에 지정할 필요가 있다.
- 그리고 이 예외는 WebClient의 것이 아닌 우리 도메인에 관련된 것이면 좋을 것이다.
- 이때 커스텀 에러 핸들러를 추가하면 HTTP 상태 코드를 우리가 선택한 Throwable로 변환하는 실행 코드를 제공할 수 있다.
- 때는 다음과 같이 retrieve() 호출 다음에 onStatus()를 호출하면 된다.

```java
Mono<Ingredient> ingredientMono = webClient
	.get()
	.uri("http://localhost:8080/ingredients/{id}", ingredientId)
	.retrieve()
	.onStatus(HttpStatus::is4xxClientError,
		response -> Mono.just(new UnknownIngredientException()))
	.bodyToMono(Ingredient.class);
```

- onStatus()의 첫 번째 인자는 HttpStatus를 지정하는 조건식이며, 우리가 처리를 원하는 HTTP 상태 코드라면 true를 반환한다.
- 그고 상태 코드가 일치하면 두 번째 인자의 함수로 응답이 반환되고 이 함수에서는 Throwable 타입의 Mono를 반환한다.
- 이 코드의 경우에 HTTP 상태 코드가 400 수준의 상태 코드(예를 들어, 클라이언트 에러)이면 UnknownIngredientException을 포함하는 Mono를 반환한다.

```java
Mono<Ingredient> ingredientMono = webClient
	.get()
	.uri("http://localhost:8080/ingredients/{id}", ingredientId)
	.retrieve()
	.onStatus(status -> status == HttpStatus.NOT_FOUND),
		response -> Mono.just(new UnknownIngredientException()))
	.bodyToMono(Ingredient.class);
```

- 응답으로 반환될 수 있는 다양한 HTTP 상태 코드를 처리할 필요가 있을 때는 onStatus() 호출을 여러 번 할 수 있다는 것을 알아두자.

## 요청 교환하기

- 지금까지 WebClient를 사용할 때는 retrieve() 메서드를 사용해서 요청의 전송을 나타냈다.
- 이때 retrieve() 메서드는 ResponseSpec 타입의 객체를 반환하였으며, 이 객체를 통해서 onStatus(), bodyToFlux(), bodyToMono()와 같은 메서드를 호출하여 응답을 처리할 수 있었다.
- ResponseSpec을 사용하는 것이 좋다.
- 그러나 이 경우 몇 가지 면에서 제한된다.
- 예를 들어, 응답의 헤더나 쿠키 값을 사용할 필요가 있을 때는 ResponseSpec으로 처리할 수 없다.
- ResponseSpec이 기대에 미치지 못할 때는 retrieve() 대신 exchange()를 호출할 수 있다.
- exchange() 메서드는 ClientResponse 타입의 Mono를 반환한다.
- ClientResponse 타입은 리액티브 오퍼레이션을 적용할 수 있고, 응답의 모든 부분(페이로드, 헤더, 쿠키 등)에서 데이터를 사용할 수 있다.
- exchange()가 retrieve()와 무엇이 다른지 살펴보기에 앞서, 이 두 메서드가 얼마나 유사한지 먼저 알아보자.

```java
Mono<Ingredient> ingredientMono = webClient
	.get()
	.uri("http://localhost:8080/ingredients/{id}", ingredientId)
	.exchange()
	.flatMap(cr -> cr.bodyToMono(Ingredient.class));
```

```java
Mono<Ingredient> ingredientMono = webClient
	.get()
	.uri("http://localhost:8080/ingredients/{id}", ingredientId)
	.retrieve()
	.bodyToMono(Ingredient.class);
```

- 두 코드의 차이점은 다음과 같다.
    - exchange() 예에서는 ResponseSpec 객체의 bodyToMono()를 사용해서 `Mono<Ingredient>`를 가져오는 대신, 매핑 함수 중 하나인 flatMap()을 사용해서 ClientResponse를 `Mono<Ingredient>`와 연관시킬 수 있는 `Mono<ClientResponse>`를 가져온다.
- 이제는 exchange()의 다른 점을 알아보자.
    - 요청의 응답에 true 값(해당 식자재가 사용 가능하지 않다는 것을 나타냄)을 갖는 X_UNAVAILABLE이라는 이름의 헤더가 포함될 수 있다고 하자.
    - 그리고 X_UNAVAILABLE 헤더가 존재한다면 결과 Mono는 빈 것(아무 것도 반환하지 않는)이어야 한다고 가정해 보자.

    ```java
    Mono<Ingredient> ingredientMono = webClient
    	.get()
    	.uri("http://localhost:8080/ingredients/{id}", ingredientId)
    	.exchange()
    	.flatMap(cr -> {
    		if (cr.headers().header("X_UNAVAILABLE").contains("true")) {
    			return Mono.empty();
    		}
    		return Mono.just(cr);
    	})
    	.flatMap(cr -> cr.bodyToMono(Ingredient.class));
    ```

# 리액티브 웹 API 보안

- 이제까지 스프링 시큐리티(훨씬 전에 Acegi라고 알려졌던) 웹 보안 모델은 서블릿 필터를 중심으로 만들어졌다.
- 만일 요청자가 올바른 권한을 갖고 있는지 확인하기 위해 서블릿 기반 웹 프레임워크의 요청 바운드를(클라이언트의 요청을 서블릿이 받기 전에) 가로채야 한다면 서블릿 필터가 확실한 선택이다.
- 그러나 스프링 WebFlux에서는 이런 방법이 곤란하다.
- 스프링 WebFlux로 웹 애플리케이션을 작성할 때는 서블릿이 개입된다는 보장이 없다.
- 실제로 리액티브 웹 애플리케이션은 Netty나 일부 다른 non-서블릿(서블릿으로 실행하지 않는) 서버에 구축될 가능성이 많다.
- 스프링 WebFlux 애플리케이션의 보안에 서블릿 필터를 사용할 수 없는 것은 사실이다.
- 그러나 5.0.0 버전부터 스프링 시큐리티는 서블릿 기반의 스프링 MVC와 리액티브 스프링 WebFlux 애플리케이션 모두의 보안에 사용될 수 있다.
- 스프링 WebFilter가 이 일을 해준다.
- WebFilter는 서블릿 API에 의존하지 않는 스프링 특유의 서블릿 필터 같은 것이다.
- 스프링 MVC와 다른 의존성을 갖는 스프링 WebFlux와는 다르게, 스프링 시큐리티는 스프링 MVC와 동일한 스프링 부트 보안 스타터를 사용한다.
- 따라서 스프링 MVC 웹 애플리케이션이나 스프링 WebFlux 애플리케이션 중 어디에 스프링 시큐리티를 사용하든 상관없다.

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

- 그렇지만 스프링 시큐리티의 리액티브 구성 모델과 리액티브가 아닌 구성 모델 간에는 사소한 차이가 있다.

## 리액티브 웹 보안 구성하기

- 스프링 MVC 웹 애플리케이션의 보안을 구성할 때는 WebSecurityConfigurerAdapter의 서브 클래스로 새로운 구성 클래스를 생성하며, 이 클래스에는 @EnableWebSecurity 애노테이션을 지정한다.
- 그리고 이 구성 클래스에는 configuration() 메서드를 오버라이딩하여 요청 경로에 필요한 권한 등과 같은 웹 보안 명세를 지정한다.

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.antMatchers("/design", "/orders").hasAuthority("USER")
				.antMatchers("/**").permitAll();
	}
}
```

- 다음은 이것과 동일한 구성을 리액티브 스프링 WebFlux 애플리케이션에서는 어떻게 하는지 알아보자.

```java
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
		return http
			.authorizeExchange()
			.pathMatchers("/design", "/orders").hasAuthority("USER")
			.anyExchange().permitAll()
			.and()
			.build();
	}
}
```

- 보면 알 수 있듯이, 같은 것이 있는가 하면 다른 것도 있다.
- 이 구성 클래스에는 우선, @EnableWebSecurity 대신 @EnableWebFluxSecurity가 지정되어 있다.
- 게다가 구성 클래스가 WebSecurityConfigurerAdapter의 서브 클래스도 아니며, 다른 베이스 클래스로부터 상속받지도 않는다.
- 따라서 configure() 메서드도 오버라이딩하지 않는다.
- 그리고 configure() 메서드를 대신해서 securityWebFilterChain() 메서드를 갖는 SecurityWebFilterChain 타입의 빈을 선언한다.
- securityWebFilterChain() 메서드 내부의 실행 코드는 앞의 구성에 있는 configure() 메서드와 크게  다르지 않지만, 일부 변경된 것이 있다.
- 우선, HttpSecurity 객체 대신 ServerHttpSecurity 객체를 사용하여 구성을 선언한다.
    - ServerHttpSecurity는 스프링 시큐리티 5에 새로 추가되었으며, HttpSecurity의 리액티브 버전이다.
- 그리고 인자로 전달된 ServerHttpSecurity를 사용해서 authorizeExchange()를 호출할 수 있다.
    - 이 메서드는 요청 수준의 보안을 선언하는 authorizeRequests()와 거의 같다.
- 경로 일치 확인의 경우에 여전히 Ant 방식의 와일드카드 경로를 사용할 수 있지만, 메서드는 antMatchers() 대신 pathMatchers()를 사용한다.
- 그리고 든 경로를 의미하는 Ant 방식의 `/**`를 더 이상 지정할 필요가 없다.
    - anyExchange() 메서드가 `/**`를 반환하기 때문이다.
- 끝으로, 프레임워크 메서드를 오버라이딩하는 대신 SecurityWebFilterChain을 빈으로 선언하므로 반드시 build() 메서드를 호출하여 모든 보안 규칙을 SecurityWebFilterChain으로 조립하고 반환해야 한다.
- 그런데 이런 차이점 외에 사용자 명세(user details)의 경우는 어떤 차이가 있을까?

## 리액티브 사용자 명세 서비스 구성하기

- WebSecurityConfigurerAdapter의 서브 클래스로 구성 클래스를 작성할 때는 하나의 configure() 메서드를 오버라이딩하여 웹 보안 규칙을 선언하며, 또 다른 configure() 메서드를 오버라이딩하여 UserDetails 객체로 정의하는 인증 로직을 구성한다.

```java
@Autowired
UserRepository userRepo;

@Override
protected void configure(AuthenticationManageBuilder auth) throws Exception {
	auth
		.userDetailsService(new UserDetailsService() {
			@Override
			public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
				User user = userRepo.findByUsername(username);
				if (user == null) {
					throw new UsernameNotFoundException(
						username + " not found");
				}
				return user.toUserDetails();
			}
		});
}
```

- 이와 같은 리액티브가 아닌 구성에서는 UserDetailsService에서 필요한 loadUserByUsername() 메서드만 오버라이딩한다.
- 그러나 리액티브 보안 구성에서는 configure() 메서드를 오버라이딩하지 않고 대신에 ReactiveUserDetailsService 빈을 선언한다.
- 이것의 UserDetailsService의 리액티브 버전이며, UserDetailsService처럼 하나의 메서드만 구현하면 된다.
- 특히 findByUsername() 메서드는 UserDetails 객체 대신 `Mono<UserDetails>`를 반환한다.

```java
@Service
public ReactiveUserDetailsService userDetailsService(UserRepository userRepo) {
	return new ReactiveUserDetailsService() {
		@Override
		public Mono<UserDetails> findByUsername(String username) {
			return userRepo.findByUsername(username)
				.map(user -> {
					return user.toUserDetails();
				});
		}
	};
}
```