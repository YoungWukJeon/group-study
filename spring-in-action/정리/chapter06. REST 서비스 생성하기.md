책의 모든 예제 : [https://github.com/habuma/spring-in-action-5-samples/tree/master/ch06](https://github.com/habuma/spring-in-action-5-samples/tree/master/ch06)

# REST 컨트롤러 작성하기

- 타코 클라우드 UI를 멋지게 만드는 동안, 필자는 많이 알려진 앵귤러(Angular) 프레임워크를 사용해서 SPA(Single-Page Application, 단일-페이지 애플리케이션)로 프론트엔드를 구축하기로 결정하였다.
- 이렇게 하려면 타코 데이터를 저장하거나 가져오기 위해 앵귤러 기반의 UI와 통신하는 REST API를 생성해야 한다.
- 앵귤러 코드는 클라이언트 측에서 작동하는 방법을 아는 데 필요한 코드만 보여줄 것이다.
- 앵귤러 클라이언트 코드는 HTTP 요청을 통해 이 장 전체에 걸쳐 생성할 REST API로 통신한다.
- 2장에서 @GetMapping과 @PostMapping 애노테이션을 사용해서 서버에서 데이터를 가져오거나 전송하였다.
- REST API를 정의할 때도 그런 애노테이션들은 여전히 사용된다.
- 더불어 스프링 MVC는 다양한 타입의 HTTP 요청에 사용되는 다른 애노테이션들도 제공한다.
- 스프링 MVC의 HTTP 요청-처리 애노테이션

| 애노테이션 | HTTP 메서드 | 용도 |
| --- | --- | --- |
| @GetMapping | HTTP GET 요청 | 리소스 데이터 읽기 |
| @PostMapping | HTTP POST 요청 | 리소스 생성하기 |
| @PutMapping | HTTP PUT 요청 | 리소스 변경하기 |
| @PatchMapping | HTTP PATCH 요청 | 리소스 변경하기 |
| @DeleteMapping | HTTP DELETE 요청 | 리소스 삭제하기 |
| @RequestMapping | 다목적 요청 처리이며, HTTP 메서드가 method <br />속성에 지정된다. |  |

## 서버에서 데이터 가져오기

- 가장 최근에 생성된 타코를 보여주는 RecentTacosComponent를 앵귤러 코드에 정의하였다.

```tsx
import { Component, OnInit, Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { HttpClient } from '@angular/common/http';

@Component({
	selector: 'recent-tacos',
	templateUrl: 'recents.component.html',
	styleUrls: ['./recents.component.css']
})

@Injectable()
export class RecentTacosComponent implements OnInit {
	recentTacos: any;

	constructor(private httpClient: HttpClient) {}

	ngOnInit() {
		// 최근 생성된 타코들을 서버에서 가져온다.
		this.httpClient.get('http://localhost:8080/design/recent')
			.subscribe(data => this.recentTacos = data);
	}
}
```

- ngOnInit() 메서드에 주목하자.
- 이 메서드에서 RecentTacosComponent는 주인된 Http 모듈을 사용해서 [http://localhost:8080/design/recent](http://localhost:8080/design/recent) 에 대한 HTTP 요청을 수행한다.
- 다음으로 앵귤러 컴포넌트가 수행하는 /design/recent의 GET 요청을 처리하여 최근에 디자인된 타코들의 내역을 응답하는 엔드포인트가 필요하다.

```java
package tacos.web.api;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityLinks;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import tacos.Taco;
import tacos.data.TacoRepository;

@RestController
@RequestMapping(path="design", produces="application/json") // /design 경로의 요청 처리
@CrossOrigin(origins="*") // 서로 다른 도메인 간의 요청을 허용한다.
public class DesignTacoController {
	private TacoRepository tacoRepo;

	@Autowired
	EntityLinks entityLinks;

	public DesignTacoController(TacoRepository tacoRepo) {
		this.tacoRepo = tacoRepo;
	}

	@GetMapping("/recent")
	public Iterable<Taco> recentTacos() { // 최근 생성된 타코 디자인들을 가져와서 반환한다.
		PageRequest page = PageRequest.of(0, 12, Sort.by("createdAt").descending());
		return tacoRepo.findAll(page).getContent();
	}
}
```

- TacoRepository 인터페이스에서 CrudRepository 인터페이스를 확장하는 대신 PagingAndSorting Repository 인터페이스를 확장하면 이 findAll() 메서드를 사용할 수 있다.
- 2장에서는 다중-페이지 애플리케이션(MPA)에 사용하는 컨트롤러인 반면, 여기서 새로 생성하는 DesignTacoController는 @RestController 애노테이션으로 나타낸 REST 컨트롤러다.
- @RestController 애노테이션은 다음 두 가지를 지원한다.
    - 우선, @Controller나 @Service와 같이 스테레오타입 애노테이션이므로 이 애노테이션이 지정된 클래스를 스프링의 컴포넌트 검색으로 찾을 수 있다.
    - 그러나 REST 관점에서 가장 유용하다.
    - 즉 @RestController 애노테이션은 컨트롤러의 모든 HTTP 요청 처리 메서드에서 HTTP 응답 몸체에 직접 쓰는 값을 반환한다는 것을 스프링에게 알려준다.
    - 반환값이 뷰를 통해 HTML로 변환되지 않고 직접 HTTP 응답으로 브라우저에 전달되어 나타난다.
- 또는 일반적인 스프링 MVC 컨트롤러처럼 DesignTacoController 클래스에 @Controller를 사용할 수도 있다.
    - 그러나 이때는 이 클래스의 모든 요청 처리 메서드에 @ResponseBody 애노테이션을 지정해야만 @RestController와 같은 결과를 얻을 수 있다.
    - 이외에도 ResponseEntity 객체를 반환하는 또 다른 방법이 있다.
- @RequestMapping 애노테이션에는 produces 속성(값은 "application/json")도 설정되어 있다.
- 이것은 요청의 Accept 헤더에 "application/json"이 포함된 요청만을 DesignTacoController의 메서드에서 처리한다는 것을 나타낸다.
- 이 경우 응답 결과는 JSON 형식이 되지만, produces 속성의 값은 String 배열로 저장되므로, 다른 컨트롤러에서도 요청을 처리할 수 있도록 JSON만이 아닌 다른 콘텐트 타입을 같이 지정할 수 있다.
- 예를 들어, XML로 출력하고자 할 때는 다음과 같이 "text/xml"을 produces 속성에 추가하면 된다.

```java
@RequestMapping(path="design", produces={"application/json", "text/html"})
```

- DesignTacoController 클래스에 @CrossOrigin 애노테이션이 지정되어 있다.
    - 앵귤러 코드는 별도의 도메인(호스트와 포트 모두 또는 둘 중 하나가 다른)에서 실행 중이므로 앵귤러 클라이언트에서 API 사용하지 못하게 웹 브라우저가 막는다.
    - 이런 제약은 서버 응답에 CORS(Cross-Origin Resource Sharing) 헤더를 포함시켜 극복할 수 있으며, 스프링에서는 @CrossOrigin 애노테이션을 지정하여 쉽게 CORS를 적용할 수 있다.
    - @CrossOrigin은 다른 도메인(프로토콜과 호스트 및 포트로 구성)의 클라이언트에서 해당 REST API를 사용(공유)할 수 있게 해주는 스프링 애노테이션이다.
- 타코 ID로 특정 타코만 가져오는 엔드포인트를 제공하고 싶다면 어떻게 하면 될까?
- 이때는 메서드의 경로에 플레이스홀더 변수를 지정하고 해당 변수를 통해 ID를 인자로 받는 메서드를 DesignTacoController에 추가하면 된다.

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

- 여기서 경로의 {id} 부분이 플레이스홀더이며, @PathVariable에 의해 {id} 플레이스홀더와 대응되는 id 매개변수에 해당 요청의 실제 값이 저정된다.
    - 만일 해당 ID와 일치하는 타코가 없다면 null을 반환한다.
    - 그러나 이것은 좋은 방법이 아니다.
    - null을 반환하면 콘텐츠가 없는데도 정상 처리를 나타내는 HTTP 200(OK) 상태 코드를 클라이언트가 받기 때문이다.
    - 따라서 이때는 다음과 같이 HTTP 404(NOT FOUND) 상태 코드를 응답으로 반환하는 것이 더 좋다.

    ```java
    @GetMapping("/{id}")
    public ResponseEntity<Taco> tacoById(@PathVariable("id") Long id) {
    	Optional<Taco> optTaco = tacoRepo.findById(id);
    	if (optTaco.isPresent()) {
    		return new ResponseEntity<>(optTaco.get(), HttpStatus.OK);
    	}
    	return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }
    ```

    - 이렇게 하면 Taco 객체 대신 `ResponseEntity<Taco>`가 반환된다.
- 개발 시에 API를 테스트할 때는 curl이나 HTTPie([https://httpie.org/](https://httpie.org/)) 를 사용해도 된다.

```bash
# curl 사용
$ curl localhost:8080/design/recent

# HTTPie 사용
$ http :8080/design/recent
```

## 서버에 데이터 전송하기

- 여기서는 DesignComponent(파일 이름은 design_component.ts)라는 이름의 새로운 앵귤러 컴포넌트를 정의하여 타코 디자인 폼의 클라이언트 코드를 처리하였다.

```tsx
onSubmit() {
	this.httpClient.post(
		'http://localhost:8080/design',
		this.model, {
			headers: new HttpHeaders().set('Content-type', 'application/json'),
		}).subscribe(taco => this.cart.addToCart(taco));
	this.router.navigate(['/cart']);
}
```

- 타코 디자인 데이터를 요청하고 저장하는 메서드를 DesignTacoController에 추가해야 한다.

```java
@PostMapping(consumes="application/json")
@ResponseStatus(HttpStatus.CREATED)
public Taco postTaco(@RequestBody Taco taco) {
	return tacoRepo.save(taco);
}
```

- 여기서는 consumes 속성을 설정하였다.
    - 따라서 Content-type이 application/json과 일치하는 요청만 처리한다.
- postTaco() 메서드의 taco 매개변수에는 @RequestBody가 지정되었다.
    - 이것은 요청 몸체의 JSON 데이터가 Taco 객체로 변환되어 taco 매개변수와 바인딩된다는 것을 나타낸다.
    - @RequestBody 애노테이션은 중요하다.
    - 이것이 지정되지 않으면 매개변수(쿼리 매개변수나 폼 매개변수)가 곧바로 Taco 객체와 바인딩되는 것으로 스프링 MVC가 간주하기 때문이다.
- postTaco() 메서드에는 @ResponseStatus(HttpStatus.CREATED) 애노테이션도 지정되어 있다.
    - 따라서 해당 요청이 성공적이면서 요청의 결과로 리소스가 생성되면 HTTP 201(CREATED) 상태 코드가 클라이언트에게 전달된다.
    - 항상 @ResponseStatus를 사용하여 클라이언트에게 더 서술적이며 정확한 HTTP 상태 코드를 전달하는 것이 좋다.
- 여기서는 새로운 Taco 객체를 생성하기 위해 @PostMapping을 생성했지만 변경할 때도 사용할 수 있다.
    - 그렇지만 일반적으로 POST 요청은 데이터 생성에 사용되고 변경 시에는 PUT이나 PATCH 요청이 사용된다.

## 서버의 데이터 변경하기

- PUT은 데이터를 변경하는 데 사용되기는 하지만, 실제로는 GET과 반대의 의미를 갖는다.
- 즉 GET 요청은 서버로부터 클라이언트로 데이터를 전송하는 반면, PUT 요청은 클라이언트로부터 서버로 데이터를 전송한다.
- 이런 관점에서 PUT은 데이터 전체를 교체하는 것이며, 반면에 HTTP PATCH의 목적은 데이터의 일부분을 변경하는 것이다.

```java
@PutMapping("/{orderId}")
public Order putOrder(@RequestBody Order order) {
	return repo.save(order);
}
```

- PUT은 해당 URL에 이 데이터를 쓰라는 의미이므로 이미 존재하는 해당 데이터 전체를 교체한다.
    - 그리고 만일 해당 주문의 속성이 생략되면 이 속성의 값은 null로 변경된다.
    - 따라서 주문에 관련된 주소만 변경할지라도 해당 주문에 포함된 여러 개의 타코 데이터들이 같이 제출되어야 한다.
    - 그렇지 않으면 타코 데이터들이 삭제되기 때문이다.
- 데이터의 일부만 변경하고자 할 때는 HTTP PATCH 요청과 스프링의 @PatchMapping을 사용한다.

```java
@PatchMapping(path="/{orderId}", consumes="application/json")
public Order patchOrder(@PathVariable("orderId") Long orderId, @RequestBody Order patch) {
	Order order = repo.findById(orderId).get();
	if (patch.getDeliveryName() != null) {
		order.setDeliveryName(patch.getDeliveryName());
	}
	if (patch.getDeliveryStreet() != null) {
		order.setDeliveryStreet(patch.getDeliveryStreet());
	}
	if (patch.getDeliveryCity() != null) {
		order.setDeliveryCity(patch.getDeliveryCity());
	}
	if (patch.getDeliveryState() != null) {
		order.setDeliveryState(patch.getDeliveryState());
	}
	if (patch.getDeliveryZip() != null) {
		order.setDeliveryZip(patch.getDeliveryZip());
	}
	if (patch.getCcNumber() != null) {
		order.setCcNumber(patch.getCcNumber());
	}
	if (patch.getCcExpiration() != null) {
		order.setCcExpiration(patch.getCcExpiration());
	}
	if (patch.getCcCVV() != null) {
		order.setCcCVV(patch.getCcCVV());
	}
	
	return repo.save(order);
}
```

- @PatchMapping과 @PutMapping을 비롯해서 스프링 MVC 애노테이션들은 어떤 종류의 요청을 메서드에서 처리하는지만 나타내며, 해당 요청이 어떻게 처리되는지는 나타내지 않는다.
- 따라서 PATCH가 부분 변경의 의미를 내포하고 있더라도 실제로 변경을 수행하는 메서드 코드는 우리가 작성해야 한다.

## 서버에서 데이터 삭제하기

- 데이터를 그냥 삭제할 때는 클라이언트에서 HTTP DELETE 요청으로 삭제를 요청하면 된다.
- 이때는 DELETE 요청을 처리하는 메서드에 스프링 MVC의 @DeleteMapping을 지정한다.

```java
@DeleteMapping("/{orderId}")
@ResponseStatus(code=HttpStatus.NO_CONTENT)
public void deleteOrder(@PathVariable("orderId") Long orderId) {
	try {
		repo.deleteById(orderId);
	} catch (EmptyResultDataAccessException e) {}
}
```

- deleteOrder() 메서드의 코드가 하는 일은 특정 주문 데이터를 삭제하는 것이다.
- 이 메서드가 실행될 때 해당 주문이 존재하면 삭제되며, 없으면 EmptyResultDataAccessException이 발생된다.
- 여기서는 EmptyResultDataAccessException을 catch한 후 아무 것도 하지 않는다.
- 설사 존재하지 않는 주문 데이터를 삭제하려다가 예외가 생겨도 정상적으로 존재하는 주문이 삭제된 것처럼 특별히 할 것이 없기 때문이다.
- 물론 이렇게 하는 대신에 null로 지정된 ResponseEntity와 'NOT FOUND' HTTP 상태 코드를 deleteOrder() 메서드에서 반환하게 할 수도 있다.
- 이외에 deleteOrder() 메서드에는 @ResponseStatus가 지정되어 있다.
- 이것은 응답의 HTTP 상태 코드가 204 (NO CONTENT)가 되도록 하기 위해서다.
- 이 메서드는 주문 데이터를 삭제하는 것이므로 클라이언트에게 데이터를 반환할 필요가 없다.
- 따라서 대개의 경우 DELETE 요청의 응답은 몸체 데이터를 갖지 않으며, 반환 데이터가 없다는 것을 클라이언트가 알 수 있게 HTTP 상태 코드를 사용한다.

# 하이퍼미디어 사용하기

- API URL을 하드코딩하고 문자열로 처리하면 클라이언트 코드가 불안정해진다.
- REST API를 구현하는 또 다른 방법으로 HATEOAS(Hypermedia As The Engine Of Application State)가 있다.
- 이것은 API로부터 반환되는 리소스(데이터)에 해당 리소스와 관련된 하이퍼링크(hyperlink)들이 포함된다.
- 따라서 클라이언트가 최소한의 API URL만 알면 반환되는 리소스와 관련하여 처리 가능한 다른 API URL들을 알아내어 사용할 수 있다.
- 예를 들어, 클라이언트가 최근 생성된 타코 리스트를 요청했다고 하자.
- 하이퍼링크가 없는 형태의 최근 타코 리스트는 다음과 같이 JSON 형식으로 클라이언트에서 수신될 것이다.

```json
[
	{
		"id": 4,
		"name": "Veg-Out",
		"createdAt": "2018-01-31T20:15:53.219+0000",
		"ingredients": [
			{"id": "FLTO", "name": "Flour Tortilla", "type": "WRAP"},
			{"id": "COTO", "name": "Corn Tortilla", "type": "WRAP"},
			{"id": "TMTO", "name": "Diced Tomatoes", "type": "VEGGIES"},
			{"id": "LETC", "name": "Lettuce", "type": "VEGGIES"},
			{"id": "SLSA", "name": "Salsa", "type": "SAUCE"},
		]
	},
	...
]
```

- 이 경우 만일 클라이언트가 타코 자체에 대한 다른 HTTP 작업을 수행하고 싶다면 /design 경로의 URL에 id 속성 값을 추가해야(하드코딩을 해야) 한다는 것을 알고 있어야 한다.
- 그리고 어떤 경우든 해당 경로 앞에 http://나 https:// 및 API 호스트 이름도 붙여야 한다.
- 이와는 다르게 API에 하이퍼미디어가 활성화되면 해당 API에는 자신과 관련된 URL이 나타나므로 그것을 클라이언트가 하드코딩하지 않아도 된다.

```json
{
	"_embedded": {
		"tacoResourceList": [
			{
				"name": "Veg-Out",
				"createdAt": "2018-01-31T20:15:53.219+0000",
				"ingredients": [
					{
						"name": "Flour Tortilla", "type": "WRAP",
						"_links": {
							"self": { "href": "http://localhost:8080/ingredients/FLTO" }
						}
					},
					{
						"name": "Corn Tortilla", "type": "WRAP",
						"_links": {
							"self": { "href": "http://localhost:8080/ingredients/COTO" }
						}
					},
					{
						"name": "Diced Tomatoes", "type": "VEGGIES",
						"_links": {
							"self": { "href": "http://localhost:8080/ingredients/TMTO" }
						}
					},
					{
						"name": "Lettuce", "type": "VEGGIES",
						"_links": {
							"self": { "href": "http://localhost:8080/ingredients/LETC" }
						}
					},
					{
						"name": "Salsa", "type": "SAUCE",
						"_links": {
							"self": { "href": "http://localhost:8080/ingredients/SLSA" }
						}
					}
				],
				"_links": {
					"self": { "href": "http://localhost:8080/design/4" }
				}
			},
			...
		]
	},
	"_links": {
		"recents": {
			"href": "http://localhost:8080/design/recent"
		}
	}
}
```

- 이런 형태의 HATEOAS를 HAL(Hypertext Application Language( [http://stateless.co/hal_specification.html](http://stateless.co/hal_specification.html) )이라고 한다.
- 이것은 JSON 응답에 하이퍼링크를 포함시킬 때 주로 사용되는 형식이다.
- 이전 것보다 간결하지는 않지만, 몇 가지 유용한 정보를 제공한다.
- 이 타코 리스트의 각 요소는 _links라는 속성을 포함하는데, 이 속성은 클라이언트가 관련 API를 수행할 수 있는 하이퍼링크를 포함한다.
- 타코와 해당 타코의 식자재 모두 그들 리소스를 참조하는 self 링크를 가지며, 리스트 전체는 자신을 참조하는 recents 링크를 갖는다.
- 스프링 HATEOAS 프로젝트는 하이퍼링크를 스프링에 지원한다.
- 구체적으로 말해서 스프링 MVC 컨트롤러에서 리소스를 반환하기 전에 해당 리소스에 링크를 추가하는 데 사용할 수 있는 클래스와 리소스 어셈블러들을 제공한다.
- 스프링 HATEOAS 스타터 의존성을 해당 빌드에 추가해야 한다.

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-hateoas</artifactId>
</dependency>
```

- 도메인 타입 대신 리소스 타입을 반환하도록 컨트롤러를 수정하면 된다.
- 책의 내용이 스프링 부트 하위 버전(2.0.4)이어서 상위 버전은 아래의 링크 참조
    - [https://docs.spring.io/spring-hateoas/docs/current/reference/html/](https://docs.spring.io/spring-hateoas/docs/current/reference/html/)

## 하이퍼링크 추가하기

- 스프링 HATEOAS는 하이퍼링크 리소스를 나타내는 두 개의 기본 타입인 Resource와 Resources를 제공한다.
- Resource 타입은 단일 리소스를, 그리고 Resources는 리소스 컬렉션을 타나내며, 두 타입 모두 다른 리소스를 링크할 수 있다.
- 두 타입이 전달하는 링크는 스프링 MVC 컨트롤러 메서드에서 반환될 때 클라이언트가 받는 JSON(또는 XML)에 포함된다.
- 최근 생성된 타코 리스트에 하이퍼링크를 추가하려면 recentTacos() 메서드에서 `List<Taco>`를 반환하는 대신 Resources 객체를 반환하도록 수정해야 한다.

```java
@GetMapping("/recent")
public Resources<Resource<Taco>> recentTacos() {
	PageRequest page = PageRequest.of(0, 12, Sort.by("createdAt").descending());
	List<Taco> tacos = tacoRepo.findAll(page).getContent();
	Resources<Resource<Taco>> recentResources = Resources.wrap(tacos);
	
	recentResources.add(new Link("http://localhost:8080/design/recent", "recents"));
	return recentResources;
}
```

- 이렇게 수정된 recentTacos()에서는 직접 타코 리스트를 반환하지 않고 대신에 Resources.wrap()을 사용해서 recentTacos()의 반환 타입인 `Resources<Resource<Taco>>`의 인스턴스로 타코 리스트를 래핑한다.
- 그러나 Resources 객체를 반환하기 전에 이름이 recents이고 URL이 [http://localhost:8080/design/recent](http://localhost:8080/design/recent) 인 링크를 추가한다.

```json
"_links": {
	"recents": {
		"href": "http://localhost:8080/design/recent"
	}
}
```

- 스프링 HATEOAS는 링크 빌더를 제공하여 URL을 하드코딩하지 않는 방법을 제공한다.
- 스프링 HATEOAS 링크 빌더 중 가장 유용한 것이 ControllerLinkBuilder다.
    - 이 링크 빌더를 사용하면 URL을 하드코딩하지 않고 호스트 이름을 알 수 있다.
    - 그리고 컨트롤러의 기본 URL에 관련된 링크의 빌드를 도와주는 편리한 API를 제공한다.

    ```java
    Resources<Resource<Taco>> recentResources = Resources.wrap(tacos);
    recentResources.add(
    	ControllerLinkBuilder.linkTo(DesignTacoController.class)
    		.slash("recent")
    		.withRel("recents"));
    ```

    - 이제는 호스트 이름을 하드코딩할 필요가 없으며, /design 경로 역시 지정하지 않아도 된다.
    - 대신에 기본 경로가 /design인 링크를 DesignTacoController에 요청한다.
    - ControllerLinkBuilder는 이 컨트롤러의 기본 경로를 사용해서 Link 객체를 생성한다.
    - 그 다음에는 스프링 프로젝트에서 많이 사용하는 slash() 메서드를 호출한다.
    - 이 메서드는 이름 그대로 슬래쉬(/)와 인자로 전달된 값을 URL에 추가한다.
    - 따라서 URL의 경로는 /design/recent가 된다.
    - 제일 끝에는 해당 Link의 관계 이름(relation name, 링크 참조 시 사용)을 지정하며, 이 예에서는 recents다.

    ```java
    Resources<Resource<Taco>> recentResources = Resources.wrap(tacos);
    recentResources.add(
    	ControllerLinkBuilder.linkTo(methodOn(DesignTacoController.class).recentTacos())
    		.withRel("recents"));
    ```

    - methodOn()은 컨트롤러 클래스인 DesignTacoController를 인자로 받아 recentTacos() 메서드를 호출할 수 있게 해준다.
    - 따라서 해당 컨트롤러의 기본 경로와 recentTacos()의 매핑 경로 모두를 결정하는 데 사용한다.

## 리소스 어셈블러 생성하기

- 다음으로는 리스트에 포함된 각 타코 리소스에 대한 링크를 추가해야 한다.
- 이때 한 가지 방법은 반복 루프에서 Resources 객체가 가지는 각 `Resource<Taco>` 요소에 Link를 추가하는 것이다.
- 그러나 이 경우는 타코 리소스의 리스트를 반환하는 API 코드마다 루프를 실행하는 코드가 있어야 하므로 번거롭다.
- 따라서 다른 전략이 필요하다.
- 여기서는 Resources.wrap()에서 리스트의 각 다코를 Resource 객체로 생성하는 대신 Taco 객체를 새로운 TacoResource 객체로 변환하는 유틸리티 클래스를 정의할 것이다.

```java
package tacos.web.api;

import java.util.Date;
import java.util.List;
import org.springframework.hateoas.ResourceSupport;
import lombok.Getter;
import tacos.Ingredient;
import tacos.Taco;;

public class TacoResource extends ResourceSupport {
	@Getter
	private final String name;

	@Getter
	private final Date createdAt;

	@Getter
	private final List<Ingredient> ingredients;

	public TacoResource(Taco taco) {
		this.name = taco.getName();
		this.createdAt = taco.getCreatedAt();
		this.ingredients = taco.getIngredients();
	}
}
```

- 여러 면에서 TacoResource는 Taco 도메인 클래스와 그리 다르지 않다.
- 두 클래스 모두 name, createdAt, ingredients 속성을 갖는다.
- 그러나 TacoResource는 ResourceSupport의 서브 클래스로서 Link 객체 리스트와 이것을 관리하는 메서드를 상속받는다.
- 게다가 TacoResource는 Taco의 id 속성을 갖지 않는다.
- 왜냐하면 데이터베이스에서 필요한 ID를 API에 노출시킬 필요가 없기 때문이다.
- 그리고 API 클라이언트 관점에서는 해당 리소스의 self 링크가 리소스 식별자 역할을 할 것이다.
- 리스트의 Taco 객체들을 TacoResource 객체들로 변환하는 데 도움을 주기 위해 리소스 어셈블러 클래스를 생성해야 한다.

```java
package tacos.web.api;

import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import tacos.Taco;

public class TacoResourceAssembler extends ResourceAssemblerSupport<Taco, TacoResource> {
	public TacoResourceAssembler() {
		super(DesignTacoController.class, TacoResource.class);
	}

	@Override
	protected TacoResource instantiateResource(Taco taco) {
		return new TacoResource(taco);
	}

	@Override
	public TacoResource toResource(Taco taco) {
		return createResourceWithId(taco.getId(), taco);
	}
}
```

- TacoResourceAssembler의 기본 생성자에서는 슈퍼 클래스인 ResourceAssemblerSupport의 기본 생성자를 호출하며, 이때 TacoResource를 생성하면서 만들어지는 링크에 포함되는 URL의 기본 경로를 결정하기 위해 DesignTacoController를 사용한다.
- instantiateResource() 메서드는 인자로 전달된 Taco 객체로 TacoResource 인스턴스를 생성하도록 오버라이드되었다.
    - TacoResource가 기본 생성자를 갖고 있다면 이 메서드는 생략할 수 있다.
    - 그러나 여기서는 Taco 객체로 TacoResource 인스턴스를 생성해야 하므로 오버라이드해야 한다.
- 마지막으로 toResource() 메서드는 ResourceAssemblerSupport로부터 상속받을 때 반드시 오버라이드해야 한다.
    - 여기서는 Taco 객체로 TacoResource 인스턴스를 생성하면서 Taco 객체의 id 속성 값으로 생성되는 self 링크가 URL에 자동 지정된다.
- instantiateResource()는 Resource 인스턴스만 생성하지만, toResource()는 Resource 인스턴스를 생성하면서 링크도 추가한다.
    - 내부적으로 toResource()는 instantiateResource()를 호출한다.

```java
@GetMapping("/recent")
public Resources<TacoResource> recentTacos() {
	PageRequest page = PageRequest.of(0, 12, Sort.by("createdAt").descending());
	List<Taco> tacos = tacoRepo.findAll(page).getContent();
	List<TacoResource> tacoResources = new TacoResourceAssembler().toResources(tacos);
	Resources<TacoResource> recentResources = new Resources<TacoResource>(tacoResources);
	recentResources.add(
		ControllerLinkBuilder.linkTo(methodOn(DesignTacoController.class).recentTacos())
			.withRel("recents"));
	return recentResources;
}
```

- 이 시점에서 /design/recent에 대한 GET 요청은 각각 self 링크를 갖는 타코들과 이 타코들이 포함된 리스트 자체의 recents 링크를 갖는 타코 리스트를 생성할 것이다.
- 그러나 각 타코의 식자재(Ingredient 객체)에는 여전히 링크가 없다.

```java
package tacos.web.api;

import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import tacos.Ingredient;

class IngredientResourceAssembler extends ResourceAssemblerSupport<Ingredient, IngredientResource> {
	public IngredientResourceAssembler() {
		super(IngredientController.class, IngredientResource.class);
	}

	@Override
	public IngredientResource toResource(Ingredient ingredient) {
		return createResourceWithId(ingredient.getId(), ingredient);
	}

	@Override
	public IngredientResource instantiateResource(Ingredient ingredient) {
		return new IngredientResource(ingredient);
	}
}
```

```java
package tacos.web.api;

import org.springframework.hateoas.ResourceSupport;
import lombok.Getter;
import tacos.Ingredient;
import tacos.Ingredient.Type;

public class IngredientResource extends ResourceSupport {
	@Getter
	private String name;

	@Getter
	private Type type;

	public IngredientResource(Ingredient ingredient) {
		this.name = ingredient.getName();
		this.type = ingredient.getType();
	}
}
```

```java
package tacos.web.api;

import java.util.Date;
import java.util.List;
import org.springframework.hateoas.ResourceSupport;
import lombok.Getter;
import tacos.Taco;;

public class TacoResource extends ResourceSupport {
	private static final IngredientResourceAssembler 
		ingredientAssembler = new IngredientResourceAssembler();

	@Getter
	private final String name;

	@Getter
	private final Date createdAt;

	@Getter
	private final List<Ingredient> ingredients;

	public TacoResource(Taco taco) {
		this.name = taco.getName();
		this.createdAt = taco.getCreatedAt();
		this.ingredients = ingredientAssembler.toResources(taco.getIngredients());
	}
}
```

## embedded 관계 이름 짓기

```json
{
	"_embedded": {
		"tacoResourceList": [
			...
		]
	}
}
```

- 여기서 embedded 밑의 tacoResourceList라는 이름에 주목하자.
- 이 이름은 Resources 객체가 `List<TacoResource>`로부터 생성되었다는 것을 나타낸다.
- 만일 TacoResources 클래스의 이름을 다른 것으로 변경한다면 이 결과 JSON의 필드 이름이 그에 맞게 바뀔 것이다.
- 이럴 때 @Relation 애노테이션을 사용하면 자바로 정의된 리소스 타입 클래스 이름과 JSON 필드 이름 간의 결합도를 낮출 수 있다.

```java
@Relation(value="taco", collectionRelation="tacos")
public class TacoResource extends ResourceSupport {
	...
}
```

```json
{
	"_embedded": {
		"tacos": [
			...
		]
	}
}
```

- 스프링 HATEOAS는 직관적이고 쉬운 방법으로 API에 링크를 추가하지만, 우리가 필요로 하지 않는 몇 줄의 코드를 자동으로 추가한다.
- API의 URL 스킴이 변경되면 클라이언트 코드 실행이 중단됨에도 자동으로 추가되는 코드가 싫어서 API에 HATEOAS 사용을 고려하지 않는 개발자들도 있다.
- 하지만 HATEOAS를 적극 사용할 것을 권장한다.

# 데이터 기반 서비스 활성화하기

- 스프링 데이터는 우리가 코드에 정의한 인터페이스를 기반으로 리퍼지터리 구현체(클래스)를 자동으로 생성하고 필요한  기능을 수행한다.
- 그러나 스프링 데이터에는 애플리케이션의 API를 정의하는 데 도움을 줄 수 있는 기능도 있다.
- 스프링 데이터 REST는 스프링 데이터의 또 다른 모듈이며, 스프링 데이터가 생성하는 리퍼지터리의 REST API를 자동 생성한다.
- 따라서 스프링 데이터 REST를 우리 빌드에 추가하면 우리가 정의한 각 리퍼지터리의 인터페이스를 사용하는 API를 얻을 수 있다.
- 스프링 데이터 REST의 사용을 시작하려면 다음과 같이 의존성을 추가해야 한다.

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-data-rest</artifactId>
</dependency>
```

- 이렇게 의존성만 지정하면 이미 스프링 데이터를 사용 중인 프로젝트에서 REST API를 노출시킬 수 있다.
- 스프링 데이터가 생성한 모든 리퍼지터리의 REST API가 자동 생성될 수 있도록 스프링 데이터 REST가 자동-구성되기 때문이다.
- 스프링 데이터 REST가 생성하는 REST 엔드포인트는 우리가 직접 생성한 것만큼 좋다.
- 이 엔드포인트를 사용하려면 지금까지 생성했던 @RestController 애노테이션이 지정된 모든 클래스들을 이 시점에서 제거해야 한다.
- 스프링 데이터 REST가 엔드포인트를 자동 제공하는지 알아보려면 애플리케이션을 시작시키고 원하는 URL을 지정하면 된다.

```bash
$ curl localhost:8080/ingredients
{
	"_embedded": {
		"ingredients": [ {
			"name": "Flour Tortilla",
			"type", "WRAP",
			"_links": {
				"self": {
					"href": "http://localhost:8080/ingredients/FLTO"
				},
				"ingredient": {
					"href": "http://localhost:8080/ingredients/FLTO"
				}
			}
		},
		...
		]
	},
	"_links": {
		"self": {
			"href": "http://localhost:8080/ingredients"
		},
		"profile": {
			"href": "http://localhost:8080/profile/ingredients"
		}
	}
}
```

- 우리 빌드에 의존성만 지정했을 뿐인데 식자재 엔드포인트는 물론이고 하이퍼링크까지 포함된 리소스도 얻게 되었다.
- REST API가 자동 생성되었기 때문이다.

```bash
$ curl http://localhost:8080/ingredients/FLTO
{
	"name": "Flour Tortilla",
	"type", "WRAP",
	"_links": {
		"self": {
			"href": "http://localhost:8080/ingredients/FLTO"
		},
		"ingredient": {
			"href": "http://localhost:8080/ingredients/FLTO"
		}
	}
}
```

- 스프링 데이터 REST가 생성한 엔드포인트들은 GET은 물론이고 POST, PUT, DELETE 메서드도 지원한다는 것을 알아두자.
- 스프링 데이터 REST가 자동 생성한 API와 관련해서 한 가지 할 일은 해당 API의 기본 경로를 설정하는 것이다.
- 해당 API의 엔드포인트가 우리가 작성한 모든 다른 컨트롤러와 충돌하지 않게 하기 위함이다.
- 스프링 데이터 REST가 자동 생성한 API의 기본 경로는 다음과 같이 spring.data.rest.base-path 속성에 설정한다.

```yaml
spring:
	data:
		rest:
			base-path: /api
```

- 여기서는 스프링 데이터 REST 엔드포인트의 기본 경로를 /api로 설정하였으므로 이제는 식자재의 엔드포인트가 /api/ingredients다.

```bash
$ curl http://localhost:8080/api/tacos
{
	"timestamp": "2018-02-11T16:22:12.381+0000",
	"status": 404,
	"error": "Not Found",
	"message": "No message available",
	"path": "/api/tacos"
}
```

- 그러나 이 경우는 예상했던 대로 수행되지 않았다.

## 리소스 경로와 관계 이름 조정하기

- 스프링 데이터 리퍼지터리의 엔드포인트를 생성할 때 스프링 데이터 REST는 해당 엔드포인트와 관련된 엔터티 클래스 이름의 복수형을 사용한다.
- 스프링 데이터 REST는 'taco'의 엔드포인트를 'tacoes'로 지정하므로 타코 리스트의 요청을 수행하려면 다음과 같이 /api/tacos가 아닌 /api/tacoes로 해야 하기 때문이다.

```bash
$ curl localhost:8080/api/tacoes
{
	"_embedded": {
		"tacoes": [{
			"name": "Carnivore",
			"createdAt": "2018-02-11T17:01:32.999+0000",
			"_links": {
				"self": {
					"href": "http://localhost:8080/api/tacoes/2"
				},
				"taco": {
					"href": "http://localhost:8080/api/tacoes/2"
				},
				"ingredients": {
					"href": "http://localhost:8080/api/tacoes/2/ingredients"
				}
			}
		}]
	},
	"page": {
		"size": 20,
		"totalElements": 3,
		"totalPages": 1,
		"number": 0
	}
}
```

- 스프링 데이터 REST는 또한 노출된 모든 엔드포인트의 링크를 갖는 홈(home) 리소스도 노출시킨다.

```bash
$ curl localhost:8080/api
{
	"_links": {
		"orders": {
			"href": "http://localhost:8080/api/orders"
		},
		"ingredients": {
			"href": "http://localhost:8080/api/ingredients"
		},
		"tacoes": {
			"href": "http://localhost:8080/api/tacoes"
		},
		"users": {
			"href": "http://localhost:8080/api/users"
		},
		"profile": {
			"href": "http://localhost:8080/api/profile"
		}
	}
}
```

- 이것을 보면 알 수 있듯이, 홈 리소스는 모든 엔터티의 링크를 보여준다.
- 스프링 데이터 REST의 복수형 관련 문제점을 해결할 수 있는 방법이 있다.
- Taco 클래스에 간단한 애노테이션을 하나 추가하면 된다.

```java
@Data
@Entity
@RestResource(rel="tacos", path="tacos")
public class Taco {
	...
}
```

- 이처럼 @RestResource 애노테이션을 지정하면 관계 이름과 경로를 우리가 원하는 것으로 변경할 수 있다.

```json
"tacos": {
	"href": "http://localhost:8080/api/tacos{?page,size,sort}",
	"templated": true
},
```

## 페이징과 정렬

- 홈 리소스의 모든 링크는 선택적 매개변수인 page, size, sort를 제공한다.
- /api/tacos와 같은 컬렉션(여러 항목이  포함된) 리소스를 요청하면 기본적으로 한 페이지당 20개의 항목이 반환된다.
- 그러나 page와 size 매개변수를 지정하면 요청에 포함될 페이지 번호와 페이지 크기를 조정할 수 있다.
- 예를 들어, 페이지 크기가 5인 첫 번째 페이지를 요청할 때는 다음과 같이 GET 요청을 하면된다. (curl을 사용할 때)

```bash
$ curl "localhost:8080/api/tacos?size=5"
```

- 다음과 같이 page 매개변수를 추가하면 두 번째 페이지의 타코를 요청할 수 있다.

```bash
$ curl "localhost:8080/api/tacos?size=5&page=1"
```

- page 매개변수의 값은 0부터 시작하므로 페이지 1은 두 번째 페이지를 의미한다.
- 또한, curl와 같은 여러 명령행 셸에서는 요청 속에 앰퍼샌드(&)를 포함하므로 URL 전체를 겹따옴표("")로 둘러싸야 한다.
- HATEOAS는 처음(first), 마지막(last), 다음(next), 이전(previous) 페이지의 링크를 요청 응답에 제공한다.

```json
"_links": {
	"first": {
		"href": "http://localhost:8080/api/tacos?page=0&size=5"
	},
	"self": {
		"href": "http://localhost:8080/api/tacos"
	},
	"next": {
		"href": "http://localhost:8080/api/tacos?page=1&size=5"
	},
	"last": {
		"href": "http://localhost:8080/api/tacos?page=2&size=5"
	},
	"profile": {
		"href": "http://localhost:8080/api/profile/tacos"
	},
	"recents": {
		"href": "http://localhost:8080/api/tacos/recent"
	}
}
```

- 이처럼 링크들을 제공하므로 API의 클라이언트는 현재 페이지가 어딘지 계속 파악하면서 매개변수와 URL을 연관시킬 필요가 없다.
- 대신에 링크 이름으로 이런 페이지를 이동하는 링크들 중 하나를 찾으면 된다.
- sort 매개변수를 지정하면 엔터티의 속성을 기준으로 결과 리스트를 정렬할 수 있다.

```bash
$ curl "localhost:8080/api/tacos?sort=createdAt,desc&page=0&size=12"
```

- 그러나 작은 문제가 하나 있다.
- 앞의 매개변수들을 사용해서 타코 리스트를 요청하기 위한 UI 코드가 하드코딩되어야 한다.

## 커스텀 엔드포인트 추가하기

- 때로는 기본적인 CRUD API로부터 탈피하여 우리 나름의 엔드포인트를 생성해야 할 때가 있다.
- 이때 @RestController 애노테이션이 지정된 빈(bean)을 구현하여 스프링 데이터 REST가 자동 생성하는 엔드포인트에 보충할 수도 있다.
- 이때는 다음 두 가지를 고려하여 우리의 API 컨트롤러를 작성해야 한다.
    - 우리의 엔드포인트 컨트롤러는 스프링 데이터 REST의 기본 경로로 매핑되지 않는다. 따라서 이때는 스프링 데이터 REST의 기본 경로를 포함하여 우리가 원하는 기본 경로가 앞에 붙도록 매핑시켜야 한다. 그러나 기본 경로가 변경될 때는 해당 컨트롤러의 매핑이 일치되도록 수정해야 한다.
    - 우리 컨트롤러에 정의한 엔드포인트는 스프링 데이터 REST 엔드포인트에서 반환되는 리소스의 하이퍼링크에 자동으로 포함되지 않는다. 이것은 클라이언트가 관계 이름을 사용해서 커스텀 엔드포인트를 찾을 수 있다는 의미다.
- 우선, 기본 경로에 관한 문제를 해결해 보자.
- 스프링 데이터 REST는 @RepositoryRestController를  포함한다.
- 이것은 스프링 데이터 REST 엔드포인트에 구성되는 것과 동일한 기본 경로로 매핑되는 컨트롤러 클래스에 지정하는 새로운 애노테이션이다.
- 간단히 말해서 @RepositoryRestController가 지정된 컨트롤러의 모든 경로 매핑은 spring.data.rest.base-path 속성의 값(/api로 구성했던)이 앞에 붙은 경로를 갖는다.

```java
package tacos.web.api;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import tacos.Taco;
import tacos.data.TacoRepository;

@RepositoryRestController
public class RecentTacosController {
	private TacoRepository tacoRepo;

	public RecentTacosController(TacoRepository tacoRepo) {
		this.tacoRepo = tacoRepo;
	}

	@GetMapping(path="/tacos/recent", produces="application/hal+json")
	public ResponseEntity<Resources<TacoResource>> recentTacos() {
		PageRequest page = PageRequest.of(0, 12, Sort.by("createdAt").descending());
		List<Taco> tacos = tacoRepo.findAll(page).getContent();
		List<TacoResource> tacoResources = new TacoResourceAssembler().toResources(tacos);
		Resources<TacoResource> recentResources = new Resources<>(tacoResources);
		recentResources.add(
			linkTo(methodOn(RecentTacosController.class).recentTacos())
				.withRel("recents"));
		return new ResponseEntity<> (recentResources, HttpStatus.OK);
	}
}
```

- 여기서 @GetMapping은 /tacos/recent 경로로 매핑되지만, RecentTacosController 클래스에 @RepositoryRestController 애노테이션이 지정되어 있으므로 맨 앞에 스프링 데이터 REST의 기본 경로가 추가된다.
- 따라서 recentTaco() 메서드는 /api/tacos/recent의 GET 요청을 처리하게 된다.
- @RepositoryRestController는 핸들러 메서드의 반환값을 요청 응답의 몸체에 자동으로 수록하지 않는다.
- 따라서 해당 메서드에 @ResponseBody 애노테이션을 지정하거나 해당 메서드에서 응답 데이터를 포함하는 ResponseEntity를 반환해야 한다.
- 그러나 /api/tacos를 요청할 때는 여전히 하이퍼링크 리스트에 나타나지 않을 것이다.
- 이제는 이문제를 해결해 보자.

## 커스텀 하이퍼링크를 스프링 데이터 엔드포인트에 추가하기

- 스프링 데이터 HATEOAS는 ResourceProcessor를 제공한다.
- 이것은 API를 통해 리소스가 번환되기 전에 리소스를 조작하는 인터페이스다.

```java
@Bean
public ResourceProcessor<PagedResources<Resource<Taco>>>
	tacoProcessor(EntityLinks links) {
		return new ResourceProcessor<PagedResources<Resource<Taco>>>() {
			@Override
			public PagedResources<Resource<Taco>> process(PagedResources<Resource<Taco>> resource) {
				resource.add(
					links.linkFor(Taco.class)
						.slash("recent")
						.withRel("recents"));
				return resource;
			}
		};
	}
```

- ResourceProcessor는 익명 내부 클래스(anonymous inner class)로 정의되었고 스프링 애플리케이션 컨텍스트에 생성되는 빈으로 선언되었다.
- 따라서 스프링 HATEOAS가 자동으로 이 빈을 찾은 후(물론 ResourceProcessor 타입의 다른 빈들도 찾는다.) 해당 리소스에 적용한다.

# 앵귤러 IDE 이클립스 플러그인 설치와 프로젝트 빌드 및 실행하기

- 지금부터는 이번 장에서 알아본 타코 클라우드 애플리케이션을 터미널 창을 사용해서 빌드하고 실행할 것이다.
- 왜냐하면 서로 다른 언어의 모듈(자바를 사용하는 스프링과 앵귤러)들을 하나의 jar 배포 파일로 생성해야 하기 때문이다.
- 타코 클라우드의 각 모듈들
    - tacocloud-api : REST API 처리 클래스와 인터페이스
    - tacocloud-data : 데이터 저장을 위한 리퍼지터리 인터페이스
    - tacocloud-domain : 도메인 클래스
    - tacocloud-security : 보안 관련 클래스(아직은 모든 기능이 구현되지 않았다.)
    - tacocloud-ui : 타입스크립트 앵귤러 UI 컴포넌트와 모듈
    - tacocloud-web : 웹 처리 관련 클래스와 인터페이스(이전 장에서 사용했던 것으로 없어도 된다.)
    - tacos : 타코 애플리케이션의 스프링 부트 메인 클래스와 구성 클래스
- 실행

    ```bash
    $ ./mvnw clean package
    $ java -jar tacos/target/taco-cloud-0.0.6-SNAPSHOT.jar
    ```