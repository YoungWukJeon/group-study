- 실제로 마이크로서비스에서는 REST API를 많이 사용한다.
- 그러므로 스프링을 사용해서 다른 REST API와 상호작용하는 방법을 알아 둘 필요가 있다.
- 스프링 애플리케이션은 다음과 같은 방법을 사용해서 REST API를 사용할 수 있다.
    - RestTemplate : 스프링 프레임워크에서 제공하는 간단하고 동기화된 REST 클라이언트
    - Traversion : 스프링 HATEOAS에서 제공하는 하이퍼링크를 인식하는 동기화 REST 클라이언트로 같은 이름의 자바스크립트 라이브러리로부터 비롯된 것이다.
    - WebClient : 스프링 5 에서 소개된 반응형 비동기 REST 클라이언트

# REST Template으로 REST 엔드포인트 사용하기

- RestTemplate은 REST 리소스와 상호작용하기 위한 41개의 메서드를 제공한다.
- 그렇지만 고유한 작업을 수행하는 메서드는 12개이며, 나머지는 이 메서드들의 오버로딩된 버전이다.
- RestTemplate이 정의하는 고유한 작업을 수행하는 12개의 메서드

| 메서드 | 기능 설명 |
| --- | --- |
| delete(…) | 지정된 URL의 리소스에 HTTP DELETE 요청을 수행한다. |
| exchange(…) | 지정된 HTTP 메서드를 URL에 대해 실행하며, 응답 몸체와 연결되는 객체를 포함하는 ResponseEntity를 반환한다. |
| execute(…) | 지정된 HTTP 메서드를 URL에 대해 실행하며, 응답 몸체와 연결되는 객체를 반환한다. |
| getForEntity(…) | HTTP GET 요청을 전송하며, 응답 몸체와 연결되는 객체를 포함하는 ResponseEntity를 반환한다. |
| getForObject(…) | HTTP GET 요청을 전송하며, 응답 몸체와 연결되는 객체를 반환한다. |
| headForHeaders(…) | HTTP HEAD 요청을 전송하며, 지정된 리소스 URL의 HTTP 헤더를 반환한다. |
| optionsForAllow(…) | HTTP OPTIONS 요청을 전송하며, 지정된 URL의 Allow 헤더를 반환한다. |
| patchForObject(…) | HTTP PATCH 요청을 전송하며, 응답 몸체와 연결되는 결과 객체를 반환한다. |
| postForEntity(…) | URL에 데이터를 POST하며, 응답 몸체와 연결되는 객체를 포함하는 ResponseEntity를 반환한다. |
| postForLocation(…) | URL에 데이터를 POST하며, 새로 생성된 리소스의 URL을 반환한다. |
| postForObject(…) | URL에 데이터를 POST하며, 응답 몸체와 연결되는 객체를 반환한다. |
| put(…) | 리소스 데이터를 지정된 URL에 PUT 한다. |

- RestTemplate은 TRACE를 제외한 표준 HTTP 매서드 각각에 대해 최소한 하나의 메서드를 갖고 있다.
- 또한, execute()와 exchange()는 모든 HTTP 메서드의 요청을 전송하기 위한 저수준의 범용 메서드를 제공한다.
    - 가변 인자 리스트에 지정된 URL 매개변수에 URL 문자열(String 타입)을 인자로 받는다.
    - `Map<String, String>`에 지정된 URL 매개변수와 URL 문자열을 인자로 받는다.
    - java.net.URI를 URL에 대한 인자로 받으며, 매개변수화된 URL은 지원하지 않는다.
- RestTemplate을 사용하려면 우리가 필요한 시점에 RestTemplate 인스턴스를 생성해야 한다.

```java
RestTemplate rest = new RestTemplate();
```

- 또는 빈으로 선언하고 필요할 때 주입할 수도 있다.

```java
@Bean
public RestTemplate restTemplate() {
	return new Template();
}
```

## 리소스 가져오기(GET)

```java
public Ingredient getIngredientById(String ingredientId) {
	return rest.getForObject("http://localhost:8080/ingredients/{id}", 
			Ingredient.class, ingredientId);
}
```

- 여기서는 URL 변수의 가변 리스트와 URL 문자열을 인자로 받게 오버로딩된 getForObject()를 사용한다.
- getForObject()에 전달된 ingredientId 매개변수는 지정된 URL의 {id} 플레이스홀더에 넣기 위해 사용된다.
- 이 예에는 하나의 변수만 있지만, 변수 매개변수들은 주어진 순서대로 플레이스홀더에 지정된다는 것을 알아 두자.
- getForObject()의 두 번째 매개변수는 응답이 바인딩되는 타입이다.
- 여기서는 JSON 형식인 응답 데이터가 객체로 역직렬화되어(deserialized) 반환된다.
- 다른 방법으로는 Map을 사용해서 URL 변수들을 지정할 수 있다.

```java
public Ingredient getIngredientById(String ingredientId) {
	Map<String, String> urlVariables = new HashMap<> ();
	urlVariables.put("id", ingredientId);
	return rest.getForObject("http://localhost:8080/ingredients/{id}",
			Ingredient.class, urlVariables);
}
```

- 여기서 ingredientId 값의 키는 "id"이며, 요청이 수행될 때 {id} 플레이스홀더는 키가 id인 Map 항목 값(ingredientId 값)으로 교체된다.
- 이와는 달리 URI 매개변수를 사용할 때는 URI 객체를 구성하여 getForObject()를 호출해야 한다.

```java
public Ingredient getIngredientById(String ingredientId) {
	Map<String, String> urlVariables = new HashMap<> ();
	urlVariables.put("id", ingredientId);
	URI url = UriComponentsBuilder
		.fromHttpUrl("http://localhost:8080/ingredients/{id}")
		.build(urlVariables);
	return rest.getForObject(url, Ingredient.class);
}
```

- 여기서 URI 객체는 URL 문자열 명세로 생성되며, 이 문자열의 {id} 플레이스홀더는 바로 앞의 getForObject() 오버로딩 버전과 동일하게 Map 항목 값으로 교체된다.
- getForObject() 메서드는 리소스로 도메인 객체만 가져와서 응답 결과로 반환된다.
- getForEntity()는 getForObject()와 같은 방법으로 작동하지만, 응답 결과를 나타내는 도메인 객체를 반환하는 대신 도메인 객체를 포함하는 ResponseEntity 객체를 반환한다.
- ResponseEntity에는 응답 헤더와 같은 더 상세한 응답 컨텐츠가 포함될 수 있다.
- 예를 들어, 도메인 객체인 식자재 데이터에 추가하여 응답의 Date 헤더를 확인하고 싶다고 하자.
- 다음과 같이 getForEntity()를 사용하면 쉽다.

```java
public Ingredient getIngredientById(String ingredientId) {
	ResponseEntity<Ingredient> responseEntity =
		rest.getForEntity("http://localhost:8080/ingredients/{id}",
			Ingredient.class, IngredientId);
	log.info("" + responseEntity.getHeaders().getDate());
	return responseEntity.getBody();
}
```

## 리소스 쓰기(PUT)

- HTTP PUT 요청을 전송하기 위해 RestTemplate은 put() 메서드를 제공한다.
- 이 메서드는 3개의 오버로딩된 버전이 있으며, 직렬화된 후 지정된 URL로 정송되는 Object 타입을 인자로 받는다.

```java
public void updateIngredient(Ingredient ingredient) {
	rest.put("http://localhost:8080/ingredients/{id}",
			ingredient,
			ingredient.getId());
}
```

- 여기서 URL은 문자열로 지정되었고 인자로 전달된 Ingredient 객체의 id 속성 값으로 교체되는 플레이스홀더를 갖는다.
- put() 메서드는 Ingredient 객체 자체를 전송하며, 반환 타입은 void이므로 이 메서드의 반환값을 처리할 필요는 없다.

## 리소스 삭제하기(DELETE)

```java
public void deleteIngredient(Ingredient ingredient) {
	rest.delete("http://localhost:8080/ingredients/{id}", ingredient.getId());
}
```

- 여기서는 문자열로 지정된 URL과 URL 변수 값만 delete()의 인자로 전달한다.

## 리소스 데이터 추가하기(POST)

- POST 요청이 수행된 후 새로 생성된 Ingredient 리소스를 반환받고 싶다면 다음과 같이 postForObject()를 사용한다.

```java
public Ingredient createIngredient(Ingredient ingredient) {
	return rest.postForObject("http://localhost:8080/ingredients",
			ingredient, Ingredient.class);
}
```

- postForObject() 메서드는 문자열(String 타입) URL과 서버에 전성될 객체 및 이 객체의 타입(리소스 몸체의 데이터와 연관됨)을 인자로 받는다.
- 또한, 여기서는 필요 없지만, URL 변수 값을 갖는 Map이나 URL을 대체할 가변 매개변수 리스트를 네 번째 매개변수로 전달할 수 있다.
- 만일 클라이언트에서 새로 생성된 리소스의 위치가 추가로 필요하다면 postForObject() 대신 postForLocation()을 호출할 수 있다.

```java
public URI createIngredient(Ingredient ingredient) {
	return rest.postForLocation("http://localhost:8080/ingredients", ingredient);
}
```

- postForLocation()은 postForObject()와 동일하게 작동하지만, 리소스 객체 대신 새로 생성된 리소스의 URI를 반환한다는 것이 다르다.
- 반환된 URI는 해당 응답의 Location 헤더에서 얻는다.
- 만일 새로 생성된 리소스의 위치와 리소스 객체 모두가 필요하다면 postForEntity()를 호출할 수 있다.

```java
public Ingredient createIngredient(Ingredient ingredient) {
	ResponseEntity<Ingredient> responseEntity =
			rest.postForEntity("http://localhost:8080/ingredients",
					ingredient, Ingredient.class);

	log.info("New resource created at " + responseEntity.getHeaders().getLocation());

	return responseEntity;
}
```

- 우리가 사용하는 API에서 하이퍼링크를 포함해야 한다면 RestTemplate은 도움이 안 된다.
- 물론 RestTemplate으로 더 상세한 리소스 데이터를 가져와서 그 안에 포함된 콘텐츠와 링크를 사용할 수도 있지만, 간단하지는 않다.

# Traverson으로 REST API 사용하기

- Traverson은 스프링 데이터 HATEOAS에 같이 제공하며, 스프링 애플리케이션에서 하이퍼미디어 API를 사용할 수 있는 솔루션이다.
- 이것은 자바 기반의 라이브러리이며, 같은 이름을 갖는 유사한 기능의 자바스크립트 라이브러리부터 영감을 얻은 것이다.
    - [http://github.com/traverson/traverson](http://github.com/traverson/traverson)
- Traverson을 사용할 때는 우선 해당 API의 기본 URI를 갖는 객체를 생성해야 한다.

```java
Traverson traverson = new Traverson(
		URI.create("http://localhost:8080/api"), MediaTypes.HAL_JSON);
```

- 여기서는 Traverson을 타코 클라우드의 기본 URL(로컬에서 실행되는)로 지정하였다.
- Traverson에는 이 URL만 지정하면 되며, 이후부터는 각 링크의 관계를 이름으로 API를 사용한다.
- 또한, Traverson 생성자에는 해당 API가 HAL 스타일의 하이퍼링크를 갖는 JSON 응답을 생성한다는 것을 인자로 지정할 수도 있다.
- 이 인자를 지정하는 이유는 수신되는 리소스 데이터를 분석하는 방법을 Traverson이 알 수 있게 하기 위해서다.
- 어디서든 Traverson이 필요할 때는 RestTemplate처럼 Traverson 객체를 생성한 후에 사용하거나 또는 주입되는 빈으로 선언할 수 있다.

```java
ParameterizedTypeReference<Resources<Ingredient>> ingredientType =
		new ParameterizedTypeReference<Resources<Ingredient>>() {};

Resources<Ingredient> ingredientRes =
	traverson.follow("ingredients")
		.toObject(ingredientType);

Collection<Ingredient> ingredients = ingredientRes.getContent();
```

- 이처럼 Traverson 객체의 follow() 메서드를 호출하면 리소스 링크의 관계 이름이 ingredients인 리소스로 이동할 수 있다.
- 이 시점에서 클라이언트는 ingredients로 이동했으므로 toObject()를 호출하여 해당 리소스의 콘텐츠를 가져와야 한다.
- toObject() 메서드의 인자에는 데이터를 읽어 들이는 객체의 타입을 지정해야 한다.
- 이때 고려할 것이 있다.
- `Resources<Ingredient>` 타입의 객체로 읽어 들여야 하는데, 자바에서는 런타임 시에 제네릭 타입의 타입 정보(여기서는 `<Ingredient>`)가 소거되어 리소스 타입을 지정하기 어렵다.
- 그러나 ParameterizedTypeReference를 생성하면 리소스 타입을 지정할 수 있다.

```java
ParameterizedTypeReference<Resources<Taco>> tacoType =
		new ParameterizedTypeReference<Resources<Taco>>() {};

Resources<Taco> tacoRes = 
	traverson.follow("tacos")
		.follow("recents")
		.toObject("tacoType");

Collection<Taco> tacos = tacoRes.getContent();
```

- 여기서는 tacos 링크 다음에 recents 링크를 따라간다.
- 그러면 최근 생성된 타코 리소스에 도달하므로 toObject()를 호출하여 해당 리소스를 가져올 수 있다.
- 여기서 tacoType은 ParameterizedTypeReference 객체로 생성되었으며, 우리가 원하는 `Resources<Taco>` 타입이다.
- follow() 메서드는 다음과 같이 두 개 이상의 관계 이름들을 인자로 지정하여 한 번만 호출할 수 있다.

```java
Resources<Taco> tacoRes = 
	traverson.follow("tacos", "recents")
		.toObject("tacoType");
```

- 그러나 Traverson은 API에 리소스를 쓰거나 삭제하는 메서드를 제공하지 않는다.
- 이와는 반대로 RestTemplate은 리소스를 쓰거나 삭제할 수 있지만, API를 이동하는 것은 쉽지 않다.
- 따라서 API의 이동과 리소스의 변경이나 삭제 모두를 해야 한다면 RestTemplate과 Traverson을 함께 사용해야 한다.

```java
private Ingredient addIngredient(Ingredient ingredient) {
	String ingredientsUrl = traverson
		.follow("ingredients")
		.asLink()
		.getHref();

	return rest.postForObject(ingredientsUrl, ingredient, Ingredient.class);
}
```

- ingredients 링크를 따라간 후에는 asLink()를 호출하여 ingredients 링크 자체를 요청한다.
- 그리고 getHref()를 호출하여 이 링크의 URL을 가져온다.
- 이렇게 URL을 얻은 다음에는 RestTemplate 인스턴스의 postForObject()를 호출하여 새로운 식자재를 추가할 수 있다.

# REST API 클라이언트가 추가된 타코 클라우드 애플리케이션 빌드 및 실행하기

- 다음과 같이 타코 클라우드 애플리케이션을 실행하자.

    ```bash
    $ java -jar tacos/target/taco-cloud-0.0.7-SNAPSNOT.jar
    ```

- 다음과 같이 터미널 창에서 REST 클라이언트 애플리케이션을 실행하자.

    ```bash
    $ java -jar tacocloud-restclient/target/tacocloud-restclient-0.0.7-SNAPSHOT.jar
    ```

- 이처럼 REST API를 사용하는 REST 클라이언트를 작성하면 HTML 형태가 아닌 JSON 형식의 데이터를 받아서 우리가 원하는 대로 처리할 수 있다.