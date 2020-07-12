# 정보 보여주기

- 타코 디자인 페이지를 지원하기 위해 다음 컴포넌트를 생성할 것이다.
    - 타코 식자재의 속성을 정의하는 도메인(domain) 클래스
    - 식자재 정보를 가져와서 뷰에 전달하는 스프링 MVC 컨트롤러 클래스
    - 식자재의 내역을 사용자의 브라우저에 보여주는 뷰 템플릿
- 이 컴포넌트들 간의 관계는 다음과 같다.

![chapter02-01](image/chapter02-01.png '전형적인 스프링 MVC의 요청 처리 흐름')

## 도메인 설정하기

- 애플리케이션의 도메인은 해당 애플리케이션의 이해에 필요한 개념을 다루는 영역이다.
- 고객이 선택한 타코 디자인, 디자인을 구성하는 삭자재, 고객, 고객의 타코 주문같은 객체가 애플리케이션 도메인에 포함된다.

```java
package tacos;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Ingredient {
	private final String id;
	private final String name;
	private final Type type;

	public static enum Type {
		WRAP, PROTEIN, VEGGIES, CHEESE, SAUCE
	}
}
```

- Ingredient 클래스에서 특이한 점은 final 속성들을 초기화하는 생성자는 물론이고 속성들의 게터(getter)와 세터(setter) 메서드가 없다는 것과 equals(), hashCode(), toString() 등의 유용한 메서드도 정의하지 않았다는 것이다.
- Lombok이라는 좋은 라이브러리를 사용해서 그런 메서드들을 런타임 시에 자동으로 생성하기 때문이다.
- @Data 애노테이션을 지정하면 소스 코드에 누락된 final 속성들을 초기화하는 생성자는 물론이고, 속성들의 게터와 세터 등을 생성하라고 Lombok에 알려준다.
- Lombok을 사용하려면 우리 프로젝트에 의존성(dependency)으로 추가해야 한다.
- 우리가 직접 pom.xml 파일에 추가할 수도 있다.

```xml
<dependency>
	<groupId>org.projectlombok</groupId>
	<artifactId>lombok</artifactId>
	<optional>true</optional>
</dependency>
```

- 그러나 개발 시점에는 해당 메서드들이 없다고 IDE가 에러를 보여준다.
- 코드 작성 중에는 Lombok 애노테이션들을 IDE가 알 수 없기 때문이다.
- IDE의 확장(extension)이나 플러그인(plugins)으로 Lombok을 추가하면 코드 작성 시점에서도 속성 관련 메서드들이 자동 생성되므로 에러가 나타나지 않게 할 수 있다.
- 컨트롤러 클래스를 작성하기 전에 잠시 Taco 클래스를 추가하자.

```java
package tacos;

import java.util.List;
import lombok.Data;

@Data
public class Taco {
	private String name;
	private List<String> ingredients;
}
```

## 컨트롤러 클래스 생성하기

- 컨트롤러는 스프링 MVC 프레임워크의 중심적인 역할을 수행한다.
- 컨트롤러는 HTTP 요청을 처리하고, 브라우저에 보여줄 HTML을 뷰에 요청하거나, 또는 REST 형태의 응답 몸체에 직접 데이터를 추가한다.
- 6장에서 REST API를 처리하는 컨트롤러의 작성 방법을 알아볼 것이다.
- 타코 클라우드 애플리케이션의 경우 다음 일을 수행하는 간단한 컨트롤러가 필요하다.
    - 요청 경로가 /design인 HTTP GET 요청을 처리한다.
    - 식자재의 내역을 생성한다.
    - 식자재 데이터의 HTML 작성을 뷰 템플릿에 요청하고, 작성된 HTML을 웹 브라우저에 전송한다.

```java
package tacos.web;

...

@Slf4j
@Controller
@RequestMapping("/design")
public class DesignTacoController {
	@GetMapping
	public String showDesignForm(Model model) {
		List<Ingredient> ingredients = Arrays.asList(
			new Ingredient("FLTO", "Flour Tortilla", Type.WRAP),
			new Ingredient("COTO", "Corn Tortilla", Type.WRAP),
			new Ingredient("GRBF", "Ground Beef", Type.PROTEIN),
			new Ingredient("CARN", "Carnitas", Type.PROTEIN),
			new Ingredient("TMTO", "Diced Tomatoes", Type.VEGGIES),
			new Ingredient("LETC", "Lettuce", Type.VEGGIES),
			new Ingredient("CHED", "Cheddar", Type.CHEESE),
			new Ingredient("JACK", "Monterrey Jack", Type.CHEESE),
			new Ingredient("SLSA", "Salsa", Type.SAUCE),
			new Ingredient("SRCR", "Sour Cream", Type.SAUCE),
		);
		
		Type[] types = Ingredient.Type.values();
		for (Type type : types) {
			model.addAttribute(type.toString().toLowerCase(), filterByType(ingredients, type));
		}

		model.addAttribute("taco", new Taco());

		return "design";
	}
	
	private List<Ingredient> filterByType(List<Ingredient> ingredients, Type type) {
		return ingredients
			.stream()
			.filter(x -> x.getType().equals(type))
			.collect(Collectors.toList());
	}
}
```

- 우선 @Slf4j는 컴파일 시에 Lombok에 제공되며, 이 클래스에 자동으로 SLF4J(자바에 사용하는 Simple Logging Facade, [https://www.slf4j.org/](https://www.slf4j.org/)) Logger를 생성한다.
    - 이 애노테이션은 다음 코드를 추가한 것과 같은 효과를 낸다.

    ```java
    private static final org.slf4j.Logger log = 
    	org.slf4j.LoggerFactory.getLogger(DesignTacoController.class);
    ```

- DesignTacoController에 적용된 그 다음 애노테이션은 @Controller다.
    - 스프링이 DesignTacoController 클래스를 찾은 후 스프링 애플리케이션 컨텍스트의 빈(bean)으로 이 클래스의 인스턴스를 자동 생성한다.
- @RequestMapping 애노테이션도 지정되어 있다.
    - /design으로 시작하는 경로의 요청을 처리함을 나타낸다.

### GET 요청 처리하기

- @GetMapping 애노테이션은 /design의 HTTP GET 요청이 수신될 때 그 요청을 처리하기 위해 showDesignForm() 메서드가 호출됨을 나타낸다.
- @GetMapping은 스프링 4.3에서 소개된 새로운 애노테이션이다.

```java
@RequestMapping(method=RequestMethod.GET)
```

- @GetMapping이 더 간결하고 HTTP GET 요청에 특화되어 있다.
- 스프링 MVC 요청-대응 애노테이션
    - @RequestMapping : 다목적 요청을 처리한다.
    - @GetMapping : HTTP GET 요청을 처리한다.
    - @PostMapping : HTTP POST 요청을 처리한다.
    - @PutMapping : HTTP PUT 요청을 처리한다.
    - @DeleteMapping : HTTP DELETE 요청을 처리한다.
    - @PatchMapping : HTTP PATCH 요청을 처리한다.
- 코드를 보면
    - 우선, 식자재를 나타내는 Ingredient 객체를 저장하는 List를 생성한다.
    - 그 다음 코드에서는 식자재의 유형(고기, 치즈, 소스 등)을 List에서 필터링(filterByType 메서드)한 후 showDesignForm()의 인자로 전달되는 Model 객체의 속성으로 추가한다.
    - Model은 컨트롤러와 데이터를 보여주는 뷰 사이에서 데이터를 운반하는 객체다.
    - 궁극적으로 Model 객체의 속성에 있는 데이터는 뷰가 알 수 있는 서블릿(servlet) 요청 속성들로 복사된다.
    - 제일 마지막에 "design"을 반환한다.
        - 이것은 뷰의 논리적인 이름이다.
- 지금 애플리케이션을 실행하고 브라우저에서 /design 경로에 접속한다면 DesignTacoController의 showDesignForm() 메서드가 실행된다.
- 뷰에 요청이 전달되기 전에 List에 저장된 식자재 데이터를 모델 객체(Model)에 넣을 것이다.
- 그러나 아직 뷰를 정의하지 않았으므로 HTTP 404 (Not Found) 에러를 초래하게 된다.

## 뷰 디자인하기

- Thymeleaf를 사용하려면 우리 프로젝트의 빌드 구성 파일(pom.xml)에 또 다른 의존성(dependency)을 추가해야 한다.

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

- 스프링 부트의 자동-구성에서 런타임 시에 classpath의 Thymeleaf를 찾아 빈(스프링 MVC에 Thymeleaf 뷰를 지원하는)을 자동으로 생성한다.
- Thymeleaf와 같은 뷰 라이브러리들은 어떤 웹 프레임워크와도 사용 가능하도록 설계되었다.
- Thymeleaf 템플릿은 요청 데이터를 나타내는 요소 속성을 추가로 갖는 HTML이다.

```html
<p th:text="${message}">placeholder message</p>
```

- th:text는 교체를 수행하는 Thymeleaf 네임스페이스(namespace) 속성이다.
    - ${} 연산자는 요청 속성(여기서는 "message")의 값을 사용하라는 것을 알려준다.
- Thymeleaf는 또한 다른 속성으로 th:each를 제공한다.
    - 이 속성은 컬렉션(예를 들어, List)을 반복 처리하며, 해당 컬렉션의 각 요소를 하나씩 HTML로 나타낸다.

```html
<h3>Designate your wrap:</h3>
<div th:each="ingredient : ${wrap}">
	<input name="ingredients" type="checkbox" th:value="${ingredient.id}" />
	<span th:text="${ingredient.name}">INGREDIENT</span><br />
</div>
```

- 여기서는 "wrap" 요청 속성에 있는 컬렉션의 각 항목에 대해 하나씩 `<div>`를 반복해서 나타내기 위해 `<div>` 태크에 th:each 속성을 사용한다.
    - `<div>` 요소 내부에는 체크 상자(check box)인 `<input>` 요소와 해당 체크 상자의 라벨을 제공하기 위한 `<span>` 요소가 있다.
    - 그리고 체크 상자에서는 Thymeleaf의 th:value를 사용해서 `<input>` 요소의 value 속성을 해당 식자재의 id 속성 값으로 설정한다.
    - `<span>` 요소에서는 th:text를 사용해서 "INGREDIENT" 텍스트를 해당 식자재의 name 속성 값으로 교체한다.
    - 실제 모델 데이터를 사용했을 때 생성되는 `<div>` 중 하나를 예로 보면 다음과 같다.

    ```html
    <div>
    	<input name="ingredients" type="checkbox" value="FLTO" />
    	<span>Flour Tortilla</span><br />
    </div>
    ```

```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
	xmlns:th="http://www.thymeleaf.org">
	<head>
		<meta charset="EUC-KR">
		<title>Taco Cloud</title>
		<link rel="stylesheet" th:href="@{/styles.css}" />
	</head>

	<body>
		<h1>Design your taco!</h1>
		<img th:src="@{/images/TacoCloud.png}" />

		<form method="POST" th:object="${taco}">
			<span class="validationError"
				th:if="${#fields.hasErrors('ingredients')}"
				th:errors="*{ingredients}">Ingredient Error</span>

			<div class="grid">
				<div class="ingredient-group" id="wraps">
					<h3>Designate your wrap:</h3>
					<div th:each="ingredient : ${wrap}">
						<input name="ingredients" type="checkbox" th:value="${ingredient.id}" />
						<span th:text="${ingredient.name}">INGREDIENT</span><br />
					</div>
				</div>

				<div class="ingredient-group" id="proteins">
					<h3>Pick your protein:</h3>
					<div th:each="ingredient : ${protein}">
						<input name="ingredients" type="checkbox" th:value="${ingredient.id}" />
						<span th:text="${ingredient.name}">INGREDIENT</span><br />
					</div>
				</div>

				<div class="ingredient-group" id="cheeses">
					<h3>Choose your cheese:</h3>
					<div th:each="ingredient : ${cheese}">
						<input name="ingredients" type="checkbox" th:value="${ingredient.id}" />
						<span th:text="${ingredient.name}">INGREDIENT</span><br />
					</div>
				</div>

				<div class="ingredient-group" id="veggies">
					<h3>Determine your veggies:</h3>
					<div th:each="ingredient : ${veggies}">
						<input name="ingredients" type="checkbox" th:value="${ingredient.id}" />
						<span th:text="${ingredient.name}">INGREDIENT</span><br />
					</div>
				</div>

				<div class="ingredient-group" id="sauces">
					<h3>Select your sauce:</h3>
					<div th:each="ingredient : ${sauce}">
						<input name="ingredients" type="checkbox" th:value="${ingredient.id}" />
						<span th:text="${ingredient.name}">INGREDIENT</span><br />
					</div>
				</div>
			</div>

			<div>
				<h3>Name your taco creation:</h3>
				<input type="name" th:field="*{name}" />
				<span th:text="${#fields.hasErrors('name')}">XXX</span>
				<span class="validationError"
					th:if="${#fields.hasErrors('name')}"
					th:errors="*{name}">Name Error</span>
				<br />

				<button>Submit your taco</button>
			</div>
		</form>
	</body>
</html>
```

- `<body>` 태그 맨 앞에 있는 타코 클라우드 로고 이미지와 `<head>` 태그에 있는 `<link>` 스타일시트(stylesheet) 참조도 주목할 필요가 있다.
- 두 가지 모두 Thymeleaf의 @{} 연산자가 사용되었다.
- 참조되는 정적 콘텐츠인 로고 이미지와 스타일시트의 위치(컨텍스트 상대 경로)를 알려주기 위해서다.
- 스프링 부트 애플리케이션의 정적 콘텐츠는 classpath의 루트 밑에 있는 /static 디렉터리에 위치한다.

```css
@charset "EUC-KR";
div.ingredient-group:nth-child(odd) {
	float: left;
	padding-right: 20px;
}

div.ingredient-group:nth-child(even) {
	float: left;
	padding-right: 0;
}

div.ingredient-group {
	width: 50%;
}

.grid:after {
	content: "";
	display: table;
	clear: both;
}

*, *:after, *:before {
	-webkit-box-sizing: border-box;
	-moz-box-sizing: border-box;
	box-sizing: border-box;
}

span.validationError {
	color: red;
}
```

- 스프링 부트 애플리케이션을 실행하는 방법은 많다.
    - 예를 들어, 애플리케이션을 실행 가능한 JAR 파일로 빌드한 후 java -jar로 JAR를 실행하거나, 또는 mvn spring-boot:run을 사용해서 실행할 수도 있다.

# 폼 제출 처리하기

- `<form>` 태그를 다시 보면 method 속성이 POST로 설정되어 있는데도 `<form>`에는 action 속성이 선언되지 않은 것을 알 수 있다.
- 이 경우 폼이 제출되면 브라우저가 폼의 모든 데이터를 모아서 폼에 나타난 GET 요청과 같은 경로(/design)로 서버에 HTTP POST 요청을 전송한다.

```java
...
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
...

public class DesignTacoController {
	...
	@PostMapping
	public String processDesign(Taco design) {
		// 이 지점에서 타코 디자인(선택된 식자재 내역)을 저장한다.
		// 이 작업은 3장에서 할 것이다.
		log.info("Processing design: " + design);
		return "redirect:/orders/current";
	}
}
```

- 클래스 수준의 @RequestMapping과 연관하여 processDesign() 메서드에 지정한 @PostMapping 애노테이션은 processDesign()이 /design 경로의 POST 요청을 처리함을 나타낸다.
- 폼의 Name 필드는 간단한 텍스트 값을 가질 때만 필요하므로 Taco의 name 속성은 String 타입이다.
    - 식자재(ingredients)를 나타내는 checkbox들도 텍스트 값을 갖는다.
    - 그러나 checkbox들은 0 또는 여러 개가 선택될 수 있으므로, 이것들과 바인딩되는 Taco 클래스의 ingredients 속성은 선택된 식자재들의 id를 저장하기 위해 `List<String>` 타입이어야 한다.
- processDesign()에서 반환되는 값은 리디렉션(redirection, 변경된 경로로 재접속) 뷰를 나타내는 "redirect:"가 제일 앞에 붙는다.
    - 즉, processDesign()의 실행이 끝난 후 사용자의 브라우저가 /orders/current 상대 경로로 접속되어야 한다는 것을 나타낸다.

```java
package tacos.web;

...

@Slf4j
@Controller
@RequestMapping("/orders")
public class OrderController {
	@GetMapping("/current")
	public String orderForm(Model model) {
		model.addAttribute("order", new Order());
		return "orderForm";
	}
}
```

```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
	xmlns:th="http://www.thymeleaf.org">
	<head>
		<meta charset="EUC-KR">
		<title>Taco Cloud</title>
		<link rel="stylesheet" th:href="@{/styles.css}" />
	</head>

	<body>
		<form method="POST" th:action="@{/orders}" th:object="${order}">
			<h1>Order your taco creations!</h1>
		
			<img th:src="@{/images/TacoCloud.png}" />
			<a th:href="@{/design}" id="another">Design another taco</a><br />
			
			<div th:if="${#fields.hasErrors()}">
				<span class="validationError">
				Please correct the problems below and resubmit.
				</span>
			</div>

			<h3>Deliver my taco masterpieces to...</h3>

			<label for="deliveryName">Name: </label>
			<input type="text" th:field="*{deliveryName}" />
			<br />

			<label for="deliveryStreet">Street address: </label>
			<input type="text" th:field="*{deliveryStreet}" />
			<br />
			
			<label for="deliveryCity">City: </label>
			<input type="text" th:field="*{deliveryCity}" />
			<br />
			
			<label for="deliveryState">State: </label>
			<input type="text" th:field="*{deliveryState}" />
			<br />

			<label for="deliveryZip">Zip code: </label>
			<input type="text" th:field="*{deliveryZip}" />
			<br />

			<h3>Here's how I'll pay...</h3>
			<label for="ccNumber">Credit Card #: </label>
			<input type="text" th:field="*{ccNumber}" />
			<br />
			<label for="ccExpiration">Expiration: </label>
			<input type="text" th:field="*{ccExpiration}" />
			<br />

			<label for="ccCVV">CVV: </label>
			<input type="text" th:field="*{ccCVV}" />
			<br />

			<input type="submit" value="Submit order" />
		</form>
	</body>
</html>
```

- <form> 태그에 폼 액션(action)도 지정하고 있다.
    - 액션이 지정되지 않을 경우에는 폼에 나타났던 것과 다른 URL로 폼의 HTTP POST 요청이 제출될 것이다.
    - 그러나 여기서는 /orders 경로로 제출되도록 지정하고 있다.
        - Thymeleaf @{...} 연산자를 사용해서 컨텍스트 상대 경로인 /orders를 지정

```java
...
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
...

public class OrderController {
	...
	@PostMapping
	public String processOrder(Order order) {
		// 이 지점에서 타코 디자인(선택된 식자재 내역)을 저장한다.
		// 이 작업은 3장에서 할 것이다.
		log.info("Order submitted: " + order);
		return "redirect:/";
	}
}
```

- 제출된 주문을 처리하기 위해 processOrder() 메서드가 호출될 때는 제출된 폼 필드와 바인딩된 속성을 갖는 Order 객체가 인자로 전달된다.

```java
package tacos;

import lombok.Data;

@Data
public class Order {
	private String deliveryName;
	private String deliveryStreet;
	private String deliveryCity;
	private String deliveryState;
	private String deliveryZip;
	private String ccNumber;
	private String ccExpiration;
	private String ccCVV;
}
```

```
2020-03-30 22:42:17.095 INFO 10800 --- [nio-8080-exec-3] tacos.web.OrderController:
Order submitted: Order(deliveryName=심재철, deliveryStreet=어딘가에, deliveryCity=
서울, deliveryState=해당없음, deliveryZip=압축, ccNumber=몰라, ccExpiration=언젠가,
ccCVV=뭘까)
```

- 이 로그 항목을 살펴보면, processOrder() 메서드가 실행되어 폼 제출을 처리하는 것은 잘 되었지만, 잘못된 정보의 입력을 허용한다는 것을 알 수 있다.
    - 대부분의 폼 필드 데이터가 엉터리다.
    - 따라서 우리가 필요한 정보에 맞도록 데이터를 검사해야 한다.

# 폼 입력 유효성 검사하기

- 폼의 유효성 검사를 하는 한 가지 방법으로 processDesign()과 processOrder() 메서드에 수많은 if/then 블록을 너저분하게 추가하는 것이 있다.
- 그러나 그것은 무척 번거롭고 코드 파악과 디버깅이 어렵다.
- 스프링은 자바의 빈 유효성 검사(Bean Validation) API(JSR-303; [https://jcp.org/en/jsr/detail?id=303](https://jcp.org/en/jsr/detail?id=303) )를 지원한다.
    - 이것을 사용하면 애플리케이션에 추가 코드를 작성하지 않고 유효성 검사 규칙을 쉽게 선언할 수 있다.
    - 그리고 스프링 부트를 사용하면 유효성 검사 라이브러리를 우리 프로젝트에 쉽게 추가할 수 있다.
    - 유효성 검사 API와 이 API를 구현한 Hibernate(하이버테이트) 컴포넌트는 스프링 부트의 웹 스타터 의존성으로 자동 추가되기 때문이다.
- 스프링 MVC에 유효성 검사를 적용하려면 다음과 같이 해야 한다.
    - 유효성 검사할 클래스(여기서는 Taco와 Order)에 검사 규칙을 선언한다.
    - 유효성 검사를 해야 하는 컨트롤러 메서드에 검사를 수행하는 것을 지정한다. 여기서는 DesignTacoController의 processDesign() 메서드와  OrderController의 processOrder() 메서드가 해당된다.
    - 검사 에러를 보여주도록 폼 뷰를 수정한다.

## 유효성 검사 규칙 선언하기

- 유효성 검사 규칙을 선언하기 위해 @NotNull과 @Size를 사용하도록 변경된 Taco 클래스를 보여준다.

```java
package tacos;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class Taco {
	@NotNull
	@Size(min=5, message="Name must be at least 5 characters long")
	private String name;

	@Size(min=1, message="You must choose at least 1 ingredient")
	private List<String> ingredients;
}
```

- 사용자가 입력을 하지 않은 필드가 있는지 확인만 하면 되므로 이때는 자바 빈 유효성 검사 API의 @NotBlank 애노테이션을 사용할 것이다.

```java
package tacos;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.CreditCardNumber; 

import lombok.Data;

@Data
public class Order {
	@NotBlank(message="Name is required")
	private String deliveryName;

	@NotBlank(message="Street is required")
	private String deliveryStreet;

	@NotBlank(message="City is required")
	private String deliveryCity;

	@NotBlank(message="State is required")
	private String deliveryState;

	@NotBlank(message="Zip code is required")
	private String deliveryZip;

	@CreditCardNumber(message="Not a valid credit card number")
	private String ccNumber;

	@Pattern(regexp="^(0[1-9]|1[0-2])([\\/])([1-9][0-9])$", 
		message="Must be formatted MM/YY")
	private String ccExpiration;

	@Digits(integer=3, fraction=0, message="Invalid CVV")	
	private String ccCVV;
}
```

- ccNumber 속성에는 @CreditCardNumber가 지정되어 있다.
    - 이 애노테이션은 속성의 값이 Luhn(룬) 알고리즘 검사([https://en.wikipedia.org/wiki/Luhn_algorithm](https://en.wikipedia.org/wiki/Luhn_algorithm) )에 합격한 유효한 신용 카드 번호이어야 한다는 것을 선언한다.
    - 그러나 입력된 신용 카드 번호가 실제로 존재하는 것인지, 또는 대금 지불에 사용될 수 있는지는 검사하지 못한다.
- ccExpiration ㅅ혹성의 경우는 애석하게도 MM/YY 형식의 검사에 사용할 수 있는 애노테이션이 없다.
    - 여기서는 @Pattern 애노테이션에 정규 표현식(regular expression)을 지정하여 ccExpiration 속성 값이 해당 형식을 따르는지 확인하였다.
        - 문법은 [http://www.regular-expressions.info](http://www.regular-expressions.info) 참고
- ccCVV 속성에서는 @Digits 애노테이션을 지정하여 입력 값이 정확하게 세 자리 숫자인지 검사한다.
- 모든 유효성 검사 애노테이션은 message 속성을 갖고 있다.
- 사용자가 입력한 정보가 애노테이션으로 선언된 유효성 규칙을 충족하지 못할 때 보여줄 메시지를 message 속성에 정의한다.

## 폼과 바인딩될 때 유효성 검사 수행하기

- 제출된 Taco의 유효성 검사를 하려면 DesignTacoController의 processDesign() 메서드 인자로 전달되는 Taco에 자바 빈 유효성 검사 API의 @Valid 애노테이션을 추가해야 한다.

```java
...
import javax.validation.Valid;
import org.springframework.validation.Errors;
...

@PostMapping
public String processDesign(@Valid Taco design, Error errors) {
	if (errors.hasError()) {
		return "design";
	}

	// 이 지점에서 타코 디자인(선택된 식자재 내역)을 저장한다.
	// 이 작업은 3장에서 할 것이다.
	log.info("Processing design: " + design);
	return "redirect:/orders/current";
}
```

- @Valid 애노테이션은 제출된 Taco 객체의 유효성 검사를 수행(제출된 폼 데이터와 Taco 객체가 바인딩된 후, 그리고 processDesign() 메서드의 코드가 실행되기 전에)하라고 스프링 MVC에 알려준다.
    - 만일 어떤 검사 에러라도 있으면 에러의 상세 내역이 Errors 객체에 저장되어 processDesign()으로 전달된다.

```java
...
import javax.validation.Valid;
import org.springframework.validation.Errors;
...

@PostMapping
public String processOrder(@Valid Order order, Error errors) {
	if (errors.hasError()) {
		return "orderForm";
	}

	log.info("Order submitted: " + order);
	return "redirect:/";
}
```

## 유효성 검사 에러 보여주기

- Thymeleaf는 fields와 th:errors 속성을 통해서 Errors 객체의 편리한 사용 방법을 제공한다.

```html
<label for="ccNumber">Credit Card #: </label>
<input type="text" th:field="*{ccNumber}" />
<span class="validationError"
	th:if="${#fields.hasErrors('ccNumber')}"
	th:errors="*{ccNumber}">CC Num Error</span>
```

# 뷰 컨트롤러로 작업하기

- 뷰에 요청을 전달하는 일만  하는 컨트롤러(뷰 컨트롤러라고 함)를 선언하는 방법을 알아보자

```java
package tacos.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("home");
	}
}
```

- WebConfig는 뷰 컨트롤러의 역할을 수행하는 구성 클래스이며, 여기서 가장 중요한 것은 WebMvcConfigurer 인터페이스를 구현한다는 것이다.
- WebMvcConfigurer 인터페이스는 스프링 MVC를 구성하는 메서드를 정의하고 있다.
- 우리가 필요한 메서드만 선택해서 오버라이딩하면 된다.
- 이렇게 함으로써 구성 클래스(WebConfig)의 몇 줄 안되는 코드로 HomeController를 대체할 수 있다.
- 이제는 HomeController를 삭제해도 우리 애플리케이션이 종전처럼 잘 실행될 것이다.
- 그리고 1장에서 작성한 HomeControllerTest에서 @WebMvcTest 애노테이션의 HomeController 참조만 삭제하면 테스트 클래스에도 에러 없이 컴파일 될 수 있다.
- 어떤 구성 클래스에서도 WebMvcConfigurer 인터페이스를 구현하고 addViewController 메서드를 오버라이딩할 수 있다.

```java
// 이 예제는 실습하지 말고 참고만 하자
@SpringBootApplication
public class TacoCloudApplication implements WebMvcConfigurer {
	public static void main(String[] args) {
		SpringApplication.run(TacoCloudApplication.class, args);
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("home");
	}
}
```

- 기존의 구성 클래스를 확장하면 새로운 구성 클래스의 생성을 피할 수 있어서 프로젝트 파일 개수도 줄어든다.
- 그러나 필자는 부트스트랩 구성 클래스는 간단하게 유지하되, 서로 다른 종류의 구성 클래스(웹, 데이터, 보안 등)를 새로 생성하는 것을 선호한다.

# 뷰 템플릿 라이브러리 선택하기

- 스프링에서 지원되는 템플릿
    - FreeMarker : spring-boot-starter-freemarker
    - Groovy 템플릿 : spring-boot-starter-groovy-templates
    - JavaServer Pages(JSP) : 없음(톰캣(Tomcat)이나 제티(Jetty) 서블릿 컨테이너 자체에서 제공됨)
    - Mustache : spring-boot-starter-mustache
    - Thymeleaf : spring-boot-starter-thymeleaf
- 대개의 경우 우리가 원하는 뷰 템플릿을 선택하고 의존성으로 추가한 후 /templates 디렉터리(메이븐이나 그래들 빌드 프로젝트의 src/main/resources 디렉터리 아래에 있는)에 템플릿을 작성한다.
- 그러면 스프링 부트는 우리가 선택한 템플릿 라이브러리를 찾아서 스프링 MVC 컨트롤러의 뷰로 사용할 컴포넌트를 자동으로 구성한다.
- Thymeleaf 대신 Mustache를 사용하다고 해보자.

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-mustache</artifactId>
</dependency>
```

- Mustache 템플릿 코드 예제

```html
<h3>Designate your wrap:</h3>
{{#wrap}}
<div>
	<input name="ingredients" type="checkbox" value="{{id}}" />
	<span>{{name}}</span><br />
</div>
{{/wrap}}
```

- JSP를 선택한다면 추가로 고려할 것이 있다.
    - 알다시피, 내장된 톰캣과 제티 컨테이너를 포함해서 자바 서블릿 컨테이너는 /WEB-INF 밑에서 JSP 코드를 찾는다.
    - 그러나 우리 애플리케이션을 실행 가능한 JAR 파일로 생성한다면 그런 요구사항을 충족시킬 방법이 없다.
    - 따라서 애플리케이션을 WAR 파일로 생성하고 종전의 서블릿 컨테이너에 설치하는 경우에는 JSP를 선택해야 한다.

## 템플릿 캐싱

- 기본적으로 템플릿은 최초 사용될 때 한 번만 파싱(코드 분석)된다.
- 그리고 파싱된 결과는 향후 사용을 위해 캐시에 저장된다.
- 이것은 프로덕션에서 애플리케이션을 실행할 때 좋은 기능이다.
- 그러나 개발 시에는 템플릿 캐싱이 그리 달갑지 않다.
- 다행스럽게도 템플릿 캐싱을 비활성화하는 방법이 있다.
- 각 템플릿의 캐싱 속성만 false로 설정하면 된다.
    - FreeMarker : spring.freemarker.cache
    - Groovy Templates : spring.groovy.template.cache
    - Mustache : spring.mustache.cache
    - Thymeleaf : spring.thymeleaf.cache
- 기본적으로 모든 속성은 캐싱을 활성화하는 true로 기본값이 설정되어 있다.
- Thymeleaf의 캐싱을 비활성화할 때는 application.properties 파일(프로젝트의 src/main/resources 아래에 있음)에 다음을 추가한다.

```
spring.thymeleaf.cache=false
```

- 단, 프로덕션에서 애플리케이션을 배포할 때는 방금 추가한 설정을 삭제하거나 true로 변경해야 한다는 것을 유의하자.