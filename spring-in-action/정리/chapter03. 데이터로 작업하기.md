- 동적인 애플리케이션을 정적인 웹사이트와 차별화하는 것은 사용자에게 보여주고 저장하는 데이터다.

# JDBC를 사용해서 데이터 읽고 쓰기

- 관계형 데이터를 사용할 경우 자바 개발자들이 선택할 수 있는 몇 가지 방법이 있다.
- 그 중 가장 많이 사용하는 두 가지 방법이 JDBC와 JPA다.
- 스프링은 이 두 가지 모두를 지원하며, 스프링을 사용하지 않을 때에 비해 더 쉽게 JDBC나 JPA를 사용할 수 있도록 해준다.
- 스프링 JDBC 지원은 JdbcTemplate 클래스에 기반을 둔다.
- JdbcTemplate은 JDBC를 사용할 때 요구되는 모든 형식적이고 상투적인 코드없이 개발자가 관계형 데이터베이스에 대한 SQL 연산을 수행할 수 있는 방법을 제공한다.

```java
@Override
public Ingredient findById(String id) {
	Connection connection = null;
	PreparedStatement statement = null;
	ResultSet resultSet = null;
	try {
		connection = dataSource.getConnection();
		statement = connection.prepareStatement(
			"select id, name, type from Ingredient where id = ?");
		statement.setString(1, id);
		resultSet = statement.executeQuery();
		Ingredient ingredient = null;
		if (resultSet.next()) {
			ingredient = new Ingredient(
				resultSet.getString("id"),
				resultSet.getString("name"),
				Ingredient.Type.valueOf(resultSet.getString("type")));
		}
		return ingredient;
	} catch (SQLException e) {
		// 여기서는 무엇을 해야 할까?
	} finally {
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException e) {}
		}
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {}
		}
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {}
		}
	}
	return null;
}
```

- 식자재가 저장된 데이터베이스를 쿼리하는 코드가 어디 있는지 찾기가 힘들다.
    - 데이터베이스 연결(connection) 생성, 명령문(statement) 생성, 그리고 연결과 명령문 및 결과 세트(result set)를 닫고 클린업하는 코드들로 쿼리 코드가 둘러싸여 있기 때문이다.
- SQLException은 catch 블록으로 반드시 처리해야 하는 checked 예외다.

```java
private JdbcTemplate jdbc;

@Override
public Ingredient findById(String id) {
	return jdbc.queryForObject(
		"select id, name, type from Ingredient where id=?",
		this::mapRowToIngredient, id);
}

private Ingredient mapRowToIngredient(ResultSet rs, int rowNum) throws SQLException {
	return new Ingredient(
		rs.getString("id"),
		rs.getString("name"),
		Ingredient.Type.valueOf(rs.getString("type")));
}
```

- 쿼리를 수행하고(JdbcTemplate의 queryForObject() 메서드), 그 결과를 Ingredient 객체로 생성하는(mapRowToIngredient() 메서드) 것에 초점을 두는 코드만 존재한다.

## 퍼시스턴스를 고려한 도메인 객체 수정하기

- 객체를 데이터베이스에 저장하고자 할 때는 해당 객체를 고유하게 식별해 주는 필드를 하나 추가하는 것이 좋다.
- 이와 더불어 타코(Taco 객체)가 언제 생성되었는지, 주문(Order 객체)이 언제 되었는지 알면 유용하다.
- 또한 객체가 저장된 날짜와 시간을 갖는 필드를 각 객체에 추가할 필요도 있다.

```java
...
import java.util.Date;

@Data
public class Taco {
	private Long id;
	private Date createdAt;
	...
}
```

```java
...
import java.util.Date;

@Data
public class Order {
	private Long id;
	private Date placedAt;
	...
}
```

## JdbcTemplate 사용하기

- JdbcTemplate를 사용하려면 이것을 우리 프로젝트의 classpath에 추가해야 한다.
- 이 때는 다음과 같은 스프링 부트의 JDBC 스타터 의존성을 빌드 명세에 추가하면 간단히 해결된다.

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
```

- 또한, 데이터를 저장하는 데이터베이스가 필요하다.
- 필자는 H2 내장 데이터베이스를 좋아해서 다음 의존성을 추가하였다.

```xml
<dependency>
	<groupId>com.h2database</groupId>
	<artifactId>h2</artifactId>
	<scope>runtime</scope>
</dependency>
```

- H2 데이터베이스의 경우에 의존성 추가와 더불어 버전 정보도 추가해야 한다.

```xml
...
<properties>
	...
	<h2.version>1.4.196</h2.version>
</properties>
...
```

### JDBC 리퍼지터리 정의하기

- 식자재 리퍼지터리는 다음 연산을 수행해야 한다.
    - 데이터베이스의 모든 식자재 데이터를 쿼리하여 Ingredient 객체 컬렉션(여기서는 List)에 넣어야 한다.
    - id를 사용해서 하나의 Ingredient를 쿼리해야 한다.
    - Ingredient 객체를 데이터베이스에 저장해야 한다.

```java
public interface IngredientRepository {
	Iterable<Ingredient> findAll();
	Ingredient findById(String id);
	Ingredient save(Ingredient ingredient);
}
```

- Ingredient 리퍼지터리가 해야 할 일을 IngredientRepository 인터페이스에 정의했으므로 이제는 JdbcTemplate을 이용해서 데이터베이스 쿼리에 사용할 수 있도록 IngredientRepository 인터페이스를 구현해야 한다.

```java
package tacos.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcIngredientRepository {
  private JdbcTemplate jdbc;
    
  @Autowired
  public JdbcIngredientRepository(JdbcTemplate jdbc) {
	  this.jdbc = jdbc;
	}
}
```

- 여기에서 JdbcIngredientRepository 클래스에는 @Repository 애노테이션이 지정되었다.
- 이것은 @Controller와 @Component 외에 스프링이 정의하는 몇 안되는 스테레오타입(stereotype) 애노테이션 중 하나다.
    - 스테레오타입 애노테이션은 스프링에서 주로 사용하는 역할 그룹을 나타내는 애노테이션이다.
    - @Repository는 @Component에서 특화된 데이터 엑세스 관련 애노테이션이다.
    - @Controller 또한 @Component에서 특화된 애노테이션이며, 이것이 지정된 클래스가 스프링 웹 MVC 컨트롤러라는 것을 알려준다.
- 즉, JdbcIngredientRepository 클래스에 @Repository를 지정하면, 스프링 컴포넌트 검색에서 이 클래스를 자동으로 찾아서 스프링 애플리케이션 컨텍스트의 빈으로 생성해 준다.
- JdbcIngredientRepository 빈이 생성되면 @Autowired 애노테이션을 통해서 스프링이 해당 빈을 JdbcTemplate에 주입(연결)한다.
    - 이 변수는 데이터베이스의 데이터를 쿼리하고 추가하기 위해 다른 메서드에서 사용될 것이다.

```java
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class JdbcIngredientRepository implements IngredientRepository {
	private JdbcTemplate jdbc;

	@Autowired
	public JdbcIngredientRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	@Override
	public Iterable<Ingredient> findAll() {
		return jdbc.query("select id, name, type from Ingredient",
			this::mapRowToIngredient);
	}

	@Override
	public Ingredient findById(String id) {
		return jdbc.queryForObject(
			"select id, name, type from Ingredient where id=?",
			this::mapRowToIngredient, id);
	}

	private Ingredient mapRowToIngredient(ResultSet rs, int rowNum) throws SQLException {
		return new Ingredient(
			rs.getString("id"),
			rs.getString("name"),
			Ingredient.Type.valueOf(rs.getString("type")));
	}
}
```

- 객체가 저장된 컬렉션을 반환하는 findAll() 메서드는 JdbcTemplate의 query() 메서드를 사용한다.
- query() 메서드는 두 개의 인자를 받는다.
    - 첫 번째 인자는 쿼리를 수행한느 SQL(select 명령)이며, 두 번째 인자는 스프링의 RowMapper 인터페이스를 우리가 구현한 mapRowToIngredient 메서드다.
- findById() 메서드는 하나의 Ingredient 객체만 반환한다.
- 따라서 query() 대신 JdbcTemplate의 queryForObject() 메서드를 사용한다.
- queryForObject() 메서드의 첫 번째와 두 번째 인자는 query()와 같으며, 세 번째 인자로는 검색할 행의 id(여기서는 식자재 id)를 전달한다.
- 그러면 이 id가 첫 번째 인자로 전달된 SQL(select 명령)에 있는 물음표(?) 대신 교체되어 쿼리에 사용된다.
- findAll()과 findById() 모두의 두 번째 인자로는 스프링 RowMapper 인터페이스를 구현한 mapRowToIngredient() 메서드의 참조가 전달된다.
- RowMapper 인터페이스의 mapRow() 메서드를 구현하는 방법을 사용할 수도 있다.

```java
@Override
public Ingredient findById(String id) {
	return jdbc.queryForObject(
		"select id, name, type from Ingredient where id=?",
		new RowMapper<Ingredient> () {
			public Ingredient mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new Ingredient(
					rs.getString("id"),
					rs.getString("name"),
					Ingredient.Type.valueOf(rs.getString("type")));
			};
		}, id);
}
```

### 데이터 추가하기

- JdbcTemplate의 update() 메서드는 데이터베이스에 데이터를 추가하거나 변경하는 어떤 쿼리에도 사용될 수 있다.

```java
...
@Override
public Ingredient save(Ingredient ingredient) {
	jdbc.update(
		"insert into Ingredient (id, name, type) values (?, ?, ?)",
		ingredient.getId(),
		ingredient.getName(),
		ingredient.getType().toString());
	return ingredient;
}
...
```

- update() 메서드에는 수행될 SQL을 포함하는 문자열과 쿼리 매개변수에 지정할 값만 인자로 전달한다.

```java
package tacos.web;

~~import java.util.Arrays;~~
import java.util.ArrayList;
...
import org.springframework.beans.factory.annotation.Autowired;

import tacos.data.IngredientRepository;
...
@Slf4j
@Controller
@RequestMapping("/design")
public class DesignTacoController {
	private final IngredientRepository ingredientRepo;

	@Autowired
	public DesignTacoController(IngredientRepository ingredientRepo) {
		this.ingredientRepo = ingredientRepo;
	}

	@GetMapping
	public String showDesignForm(Model model) {
		~~List<Ingredient> ingredients = Arrays.asList(~~
      ~~new Ingredient("FLTO", "Flour Tortilla", Type.WRAP),~~
      ~~new Ingredient("COTO", "Corn Tortilla", Type.WRAP),~~
      ~~new Ingredient("GRBF", "Ground Beef", Type.PROTEIN),~~
      ~~new Ingredient("CARN", "Carnitas", Type.PROTEIN),~~
      ~~new Ingredient("TMTO", "Diced Tomatoes", Type.VEGGIES),~~
      ~~new Ingredient("LETC", "Lettuce", Type.VEGGIES),~~
      ~~new Ingredient("CHED", "Cheddar", Type.CHEESE),~~
      ~~new Ingredient("JACK", "Monterrey Jack", Type.CHEESE),~~
      ~~new Ingredient("SLSA", "Salsa", Type.SAUCE),~~
      ~~new Ingredient("SRCR", "Sour Cream", Type.SAUCE)~~
    ~~);~~
	
		List<Ingredient> ingredients = new ArrayList<> ();
		ingredientRepo.findAll().forEach(i -> ingredients.add(i));

		Type[] types = Ingredient.Type.values();
		for (Type type : types) {
			model.addAttribute(type.toString().toLowerCase(),
				filterByType(ingredients, type))
		}

		model.addAttribute("taco", new Taco());

		return "design";
	}
	...
}
```

- findAll() 메서드는 모든 식자재 데이터를 데이터베이스로부터 가져온다.
- 그 다음에 타입별로 식자재가 필터링된다.

## 스키마 정의하고 데이터 추가하기

![chapter03-01](image/chapter03-01.png '타코 클라우드 스키마의 테이블')

- 테이블은 다음의 용도로 사용된다.
    - **Ingredient** : 식자재 정보를 저장한다.
    - **Taco** : 사용자가 식자재를 선택하여 생성한 타코 디자인에 관한 정보를 저장한다.
    - **Taco_Ingredients** : Taco와 Ingredient 테이블 간의 관계를 나타내며, Taco 테이블의 각 행(row)에 대해 하나 이상의 행(타코를 식자재와 연관시키는)을 포함한다.
        - 하나의 타코에는 하나 이상의 식자재가 포함될 수 있다
    - **Taco_Order** : 주문 정보를 저장한다.
    - **Taco_Order_Tacos** : Taco_Order와 Taco 테이블 간의 관계를 나타내며, Taco_Order 테이블의 각 행에 대해 하나 이상의 행(주문을 타코와 연관시키는)을 포함한다.
        - 한 건의 주문에는 하나 이상의 타코가 포함될 수 있다.

```sql
create table if not exists Ingredient (
	id varchar(4) not null,
	name varchar(25) not null,
	type varchar(10) not null
);

create table if not exists Taco (
	id identity,
	name varchar(50) not null,
	createdAt timestamp not null
);

create table if not exists Taco_Ingredients (
	taco bigint not null,
	ingredient varchar(4) not null
);

alter table Taco_Ingredients add foreign key (taco) references Taco(id);
alter table Taco_Ingredients add foreign key (ingredient) references Ingredient(id);

create table if not exists Taco_Order (
	id identity,
	deliveryName varchar(50) not null,
	deliveryStreet varchar(50) not null,
	deliveryCity varchar(50) not null,
	deliveryState varchar(2) not null,
	deliveryZip varchar(10) not null,
	ccNumber varchar(16) not null,
	ccExpiration varchar(5) not null,
	ccCVV varchar(3) not null,
	placedAt timestamp not null
);

create table if not exists Taco_Order_Tacos (
	tacoOrder bigint not null,
	taco bigint not null
);

alter table Taco_Order_Tacos
	add foreign key (tacoOrder) references Taco_Order(id);
alter table Taco_Order_Tacos
	add foreign key (taco) references Taco(id);
```

- schema.sql이라는 이름의 파일이 애플리케이션 classpath의 루트 경로에 있으면 애플리케이션이 시작될 때 schema.sql 파일의 SQL이 사용 중인 데이터베이스에서 자동 실행된다.
- 그리고 또한 식자재 데이터를 데이터베이스에 미리 저장해야 한다.
- 다행스럽게도 스프링 부트는 애플리케이션이 시작될 때 data.sql이라는 이름의 파일도 실행되도록 한다.

```sql
delete from Taco_Order_Tacos;
delete from Taco_Ingredients;
delete from Taco;
delete from Taco_Order;

delete from Ingredient;
insert into Ingredient (id, name, type)
	values('FLTO', 'Flour Tortilla', 'WRAP');
insert into Ingredient (id, name, type)
	values('COTO', 'Corn Tortilla', 'WRAP');
insert into Ingredient (id, name, type)
	values('GRBF', 'Ground Beef', 'PROTEIN');
insert into Ingredient (id, name, type)
	values('CARN', 'Carnitas', 'PROTEIN');
insert into Ingredient (id, name, type)
	values('TMTO', 'Diced Tomatoes', 'VEGGIES');
insert into Ingredient (id, name, type)
	values('LETC', 'Lettuce', 'VEGGIES');
insert into Ingredient (id, name, type)
	values('CHED', 'Cheddar', 'CHEESE');
insert into Ingredient (id, name, type)
	values('JACK', 'Monterrey Jack', 'CHEESE');
insert into Ingredient (id, name, type)
	values('SLSA', 'Salsa', 'SAUCE');
insert into Ingredient (id, name, type)
	values('SRCR', 'Sour Cream', 'SAUCE');
```

## 타코와 주문 데이터 추가하기

- JdbcTemplate을 사용해서 데이터를 저장하는 방법은 다음 두 가지가 있다.
    - 직접 update() 메서드를 사용한다.
    - SimpleJdbcInsert 래퍼(wrapper) 클래스를 사용한다.

### JdbcTemplate을 사용해서 데이터 저장하기

```java
package tacos.data;

import tacos.Taco;

public interface TacoRepository {
	Taco save(Taco design);
}
```

```java
package tacos.data;

import tacos.Order;

public interface OrderRepository {
	Order save(Order order);
}
```

```java
package tacos.data;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import tacos.Ingredient;
import tacos.Taco;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.Arrays;
import java.util.Date;

@Repository
public class JdbcTacoRepository implements TacoRepository {
	private JdbcTemplate jdbc;
	
	public JdbcTacoRepository(JdbcTemplate jdbc) {
	  this.jdbc = jdbc;
	}
	
	@Override
	public Taco save(Taco taco) {
	  long tacoId = saveTacoInfo(taco);
	  taco.setId(tacoId);
	  for (Ingredient ingredient : taco.getIngredients()) {
      saveIngredientToTaco(ingredient, tacoId);
	  }
	  return taco;
	}
	
	private long saveTacoInfo(Taco taco) {
    taco.setCreatedAt(new Date());
    PreparedStatementCreator psc = 
			new PreparedStatementCreatorFactory(
	      "insert into Taco (name, createdAt) values (?, ?)",
        Types.VARCHAR, Types.TIMESTAMP
			).newPreparedStatementCreator(
	      Arrays.asList(
		      taco.getName(),
	        new Timestamp(taco.getCreatedAt().getTime())));
	
    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbc.update(psc, keyHolder);
    return keyHolder.getKey().longValue();
	}
	
	private void saveIngredientToTaco(Ingredient ingredient, long tacoId) {
    jdbc.update(
	    "insert into Taco_Ingredients (taco, ingredient) " +
	      "values(?, ?)",
	    tacoId, ingredient.getId());
	}
}
```

- Taco 테이블에 하나의 행을 추가할 때는 데이터베이스에서 생성되는 ID를 알아야 한다.
- 여기서 사용하는 update() 메서드는 PreparedStatementCreator 객체와 KeyHolder 객체를 인자로 받는다.
    - 생성된 타코 ID를 제공하는 것이 바로 이 KeyHolder다.
    - 그러나 이것을 사용하기 위해서는 PreparedStatementCreator도 생성해야 한다.
- 실행할 SQL 명령과 각 쿼리 매개변수의 타입을 인자로 전달하여 PreparedStatementCreatorFactory 객체를 생성하는 것으로 시작한다.
    - 그리고 이 객체의 newPreparedStatementCreator()를 호출하며, 이 때 PreparedStatementCreator를 생성하기 위해 쿼리 매개변수의 값을 인자로 전달한다.
- 이렇게 하여 PreparedStatementCreator 객체가 생성되면 이 객체와 KeyHolder 객체(여기서는 GeneratedKeyHolder 인스턴스)를 인자로 전달하여 update()를 호출할 수 있다.
    - 그리고 update()의 실행이 끝나면 keyHolder.getKey().longValue()의 연속 호출로 타코 ID를 반환할 수 있다.
- Taco 클래스의 ingredients 속성을 Ingredient 객체로 저장하는 List로 변경해야 한다.
    - 여기까지 작성한 클래스에서는 ingredients의 타입이 `List<String>` 이었다.

```java
...
public class Taco {
	...
	@Size(min=1, message="You must choose at least 1 ingredient")
	private List<Ingredient> ingredients;
}
```

```java
...
import tacos.data.TacoRepository;

@Controller
@RequestMapping("/design")
public class DesignTacoController {
	private final IngredientRepository ingredientRepo;

	private TacoRepository tacoRepo;

	@Autowired
	public DesignTacoController(
		IngredientRepository ingredientRepo, TacoRepository tacoRepo) {
		this.ingredientRepo = ingredientRepo;
		this.tacoRepo = tacoRepo;
	}
	...
}
```

```java
...
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
...
import tacos.Order;
...

@Slf4j
@Controller
@RequestMapping("/design")
@SessionAttributes("order")
public class DesignTacoController {
	...
	
	@ModelAttribute(name = "order")
	public Order order() {
		return new Order();
	}

	@ModelAttribute(name = "taco")
	public Taco taco() {
		return new Taco();
	}

	@PostMapping
	public String processDesign(
		@Valid Taco design,
		Errors errors, @ModelAttribute Order order) {
		
		if (errors.hasErrors()) {
			return "design";
		}

		~~// 이 지점에서 타코 디자인(선택된 식자재 내역)을 저장한다.~~
		~~// 이 작업은 3장에서 할 것이다.~~
		~~log.info("Processing design: " + design);~~

		Taco saved = tacoRepo.save(design);
		order.addDesign(saved);

		return "redirect:/orders/current";
	}
	...
}
```

- @ModelAttribute 애노테이션은 Order 객체가 모델에 생성되도록 해준다.
- 주문은 다수의 HTTP 요청에 걸쳐 존재해야 한다.
    - 다수의 타코를 생성하고 그것들을 하나의 주문으로 추가할 수 있게 하기 위해서다.
    - 이 때 클래스 수준의 @SessionAttributes 애노테이션을 주문과 같은 모델 객체에 지정하면 된다.
    - 그러면 세션에서 계속 보존되면서 다수의 요청에 걸쳐 사용될 수 있다.
- processDesign() 메서드에 추가된 매개변수 @ModelAttribute Order order
    - 이 매개변수의 값이 모델로부터 전달되어야 한다는 것과 스프링 MVC가 이 매개변수에 요청 매개변수를 바인딩하지 않아야 한다는 것을 나타태기 위해서다.

```java
...
import java.util.ArrayList;
import java.util.List;
...
@Data
public class Order {
	...
	@Digits(integer=3, fraction=0, message="Invalid CVV")
	private String ccCVV;

	private List<Taco> tacos = new ArrayList<> ();

	public void addDesign(Taco design) {
		this.tacos.add(design);
	}
}
```

- 사용자가 주문 폼에 입력을 완료하고 제출할 때까지 Order 객체는 세션에 남아 있고 데이터베이스에 저장되지 않는다.

### SimpleJdbcInsert를 사용해서 데이터 추가하기

- SimpleJdbcInsert는 데이터를 더 쉽게 테이블에 추가하기 위해 JdbcTemplate을 래핑한 객체다.

```java
package tacos.data;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;

import tacos.Taco;
import tacos.Order;

@Repository
public class JdbcOrderRepository implements OrderRepository {
  private SimpleJdbcInsert orderInserter;
  private SimpleJdbcInsert orderTacoInserter;
  private ObjectMapper objectMapper;
  
  @Autowired
  public JdbcOrderRepository(JdbcTemplate jdbc) {
    this.orderInserter = new SimpleJdbcInsert(jdbc)
	    .withTableName("Taco_Order")
	    .usingGeneratedKeyColumns("id");
    
    this.orderTacoInserter = new SimpleJdbcInsert(jdbc)
	    .withTableName("Taco_Order_Tacos");
    
    this.objectMapper = new ObjectMapper();
  }
}
```

- 인스턴스 변수에 JdbcTemplate을 직접 지정하는 대신, JdbcOrderRepository 생성자에서는 JdbcTemplate을 사용해서 두 개의 SimpleJdbcInsert 인스턴스를 생성한다.
- orderInserter 인스턴스 변수에 지정되는 첫 번째 SimpleJdbcInsert 인스턴스는 Taco_Order 테이블에 주문 데이터를 추가하기 위해 구성되며, 이 때 Order 객체의 id 속성 값은 데이터베이스가 생성해 주는 것을 사용한다.
- orderTacoInserter 인스턴스 변수에 지정되는 두 번째 SimpleJdbcInsert 인스턴스는 Taco_Order_Tacos 테이블에 해당 주문 id 및 이것과 연관된 타코들의 id를 추가하기 위해 구성된다.
    - 그러나 어떤 id 값들을 Taco_Order_Tacos 테이블의 데이터에 생성할 것인지는 지정하지 않는다.
        - 데이터베이스에서 생성해 주는 것을 사용하지 않고 이미 생성된 주문 id 및 이것과 연관된 타코들의 id를 우리가 지정하기 때문이다.
- JdbcOrderRepository 생성자에서는 또한 Jackson ObjectMapper 인스턴스를 생성하고 인스턴스 변수에 지정한다.

```java
package tacos.data;

import java.util.Date;
...
	...
	@Override
	public Order save(Order order) {
		order.setPlacedAt(new Date());
		long orderId = saveOrderDetails(order);
		order.setId(orderId);
		List<Taco> tacos = order.getTacos();

		for (Taco taco : tacos) {
			saveTacoToOrder(taco, orderId);
		}
		return order;
	}

	private long saveOrderDetails(Order order) {
		@SuppressWarnings("unchecked")
		Map<String, Object> values =
			objectMapper.convertValue(order, Map.class);
		values.put("placedAt", order.getPlacedAt());

		long orderId = 
			orderInserter
				.executeAndReturnKey(values)
				.longValue();
		return orderId;
	}

	private void saveTacoToOrder(Taco taco, long orderId) {
		Map<String, Object> values = new HashMap<> ();
		values.put("tacoOrder", orderId);
		values.put("taco", taco.getId());
		orderTacoInserter.execute(values);
	}
}
```

- SimpleJdbcInsert는 데이터를 추가하는 두 개의 유용한 메서드인 execute()와 executeAndReturnKey()를 갖고 있다.
    - 두 메서드는 모두 `Map<String, Object>`를 인자로 받는다.
    - 이 Map의 키는 데이터가 추가되는 테이블의 열(column) 이름과 대응되며, Map의 값은 해당 열에 추가되는 값이다.
- Order 객체는 여러 개의 속성을 가지며, 속성 모두가 테이블의 열과 같은 이름을 갖는다.
    - 따라서 saveOrderDetails() 메서드에서는 잭슨(Jackson) ObjectMapper와 이것의 convertValue() 메서드를 사용해서 Order를 Map으로 변환한 것이다.
        - 잭슨은 원래 자바용 JSON 라이브러리다.
        - 그러나 잭슨은 스프링 부트의 웹 스타터가 시작시키며, 객체의 각 속성을 일일이 Map으로 생성하는 것보다 ObjectMapper를 사용하는 것이 훨씬 쉽다.
        - 그러므로 객체를 Map으로 생성할 때 각자 선호하는 코드가 있다면 해당 코드로 교체해도 된다.
    - Map이 생성되면 키가 placedAt인 항목의 값을 Order 객체의 placedAt 속성 값으로 변경한다.
        - 왜냐하면 ObjectMapper는 Date 타입의 값을 long 타입의 값으로 변환하므로, Taco_Order 테이블의 placedAt 열과 타입이 호환되지 않기 때문이다.
- executeAndReturnKey() 메서드를 호출하면 해당 주문 데이터가 Taco_Order 테이블에 저장된 후 데이터베이스에서 생성된 ID가 Number 객체로 반환된다.
    - 따라서 연속으로 longValue()를 호출하여 saveOrderDetails() 메서드에서 반환하는 long 타입으로 변환할 수 있다.
- 이제는 OrderRepository를 OrderController에 주입하고 사용할 수 있다.

```java
package tacos.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import tacos.Order;
import tacos.data.OrderRepository;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/orders")
@SessionAttributes("order")
public class OrderController {
  private OrderRepository orderRepo;
  
  public OrderController(OrderRepository orderRepo) {
    this.orderRepo = orderRepo;
  }
  
  @GetMapping("/current")
  public String orderForm(~~Model model~~) {
		~~model.addAttribute("order", new Order());~~
    return "orderForm";
  }

  @PostMapping
  public String processOrder(@Valid Order order, Errors errors, SessionStatus sessionStatus) {
    if (errors.hasErrors()) {
      return "orderForm";
    }

		~~log.info("Order submitted: " + order);~~
    orderRepo.save(order);
    sessionStatus.setComplete();
    
    return "redirect:/";
  }
}
```

- 주문 객체가 데이터베이스에 저장된 후에는 더 이상 세션에 보존할 필요가 없다.
- 그러나 만일 제거하지 않으면 이전 주문 및 이것과 연관된 타코가 세션에 남아 있게 되어 다음 주문은 이전 주문에 포함되었던 타코 객체들을 가지고 시작하게 될 것이다.
- 따라서 processOrder() 메서드에서는 SessionStatus를 인자로 전달받아 이것의 setComplete() 메서드를 호출하여 세션을 재설정한다.

```html
orderForm.html
...
<form method="POST" th:action="@{/orders}" th:object="${order}">
	<h1>Order your taco creations!</h1>
	
	<img th:src="@{/images/TacoCloud.png}" />
	<a th:href="@{/design}" id="another">Design another taco</a><br />

	<ul>
		<li th:each="taco : ${order.tacos}">
			<span th:text="${taco.name}">taco name</span>
		</li>
	</ul>
...
```

- 마지막으로, 데이터의 타입을 변환해 주는 컨버터(converter) 클래스를 작성하자.
    - 이 클래스는 스프링의 Converter 인터페이스에 정의된 convert() 메서드를 구현한다.
    - 우리가 Converter에 지정한 타입 변환이 필요할 때 convert() 메서드가 자동 호출된다.
    - 우리 애플리케이션에서는 String 타입의 식자재 ID를 사용해서 데이터베이스에 저장된 특정 식자재 데이터를 읽은 후 Ingredient 객체로 변환하기 위해 컨버터가 사용된다.
        - 그리고 이 컨버터로 변환된 Ingredient 객체는 다른 곳에서 List에 저장된다.

    ```java
    package tacos.web;

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.core.convert.converter.Converter;
    import org.springframework.stereotype.Component;

    import tacos.Ingredient;
    import tacos.data.IngredientRepository;

    @Component
    public class IngredientByIdConverter implements Converter<String, Ingredient> {
    	private IngredientRepository ingredientRepo;

    	@Autowired
    	public IngredientByIdConverter(IngredientRepository ingredientRepo) {
    		this.ingredientRepo = ingredientRepo;
    	}

    	@Override
    	public Ingredient convert(String id) {
    		return ingredientRepo.findById(id);
    	}
    }
    ```

    - `Converter<String, Ingredient>`에서 String은 변환할 값의 타입이고, Ingredient는 변환된 값의 타입을 나타낸다.
- 이제는 타코 클라우드 애플리케이션을 시작시키고 [http://localhost:8080/design](http://localhost:8080/design) 에 접속하여 우리가 노력한 결과를 확인할 수 있다.
- 이와 더불어 데이터베이스도 직접 살펴볼 수 있다.
    - 여기서는 내장 데이터베이스 H2를 사용하고 스프링 부트 DevTools도 이미 포함되어 있으므로, 웹 브라우저에서 [http://localhost:8080/h2-console](http://localhost:8080/h2-console) 에 접속하여 H2 콘솔을 볼 수 있다.
    - 처음에는 Login 대화상자가 나타난다.
    - JDBC URL 필드에 `jdbc:h2:mem:testdb`를 입력하고 사용자명에 `sa`를 입력한 후, 연결을 클릭한다.

    ![chapter03-02](image/chapter03-02.png 'H2 데이터베이스 로그인 대화상자')

    ![chapter03-03](image/chapter03-03.png 'H2 콘솔')

    ```sql
    select t.id "타코 ID", t.name "타코 이름", i.id "식자재 ID",
    		i.name "식자재 이름", i.type "식자재 유형"
    	from taco t, ingredient i, taco_ingredients g
    	where t.id = g.taco and i.id = g.ingredient
    ```

    - SimpleJdbcInsert와 더불어 스프링의 JdbcTemplate은 일반적인 JDBC보다 훨씬 더 쉽게 관계형 데이터베이스를 사용하도록 해준다.
    - 그러나 스프링 데이터  JPA(Java Persistence API)는 더욱 쉽게 해준다는 것을 곧 알게 될 것이다.

    # 스프링 데이터 JPA를 사용해서 데이터 저장하고 사용하기

    - 스프링 데이터 프로젝트는 여러 개의 하위 프로젝트로 구성되는 다소 규모가 큰 프로젝트다.
    - 그리고 대부분의 하위 프로젝트는 다양한 데이터베이스 유형을 사용한 데이터 퍼시스턴스에 초점을 둔다.
    - 가장 많이 알려진 스프링 데이터 프로젝트들은 다음과 같다.
        - 스프링 데이터 JPA : 관계형 데이터베이스의 JPA 퍼시스턴스
        - 스프링 데이터 MongoDB : 몽고 문서형 데이터베이스의 퍼시스턴스
        - 스프링 데이터 Neo4 : Neo4j 그래프 데이터베이스의 퍼시스턴스
        - 스프링 데이터 레디스(Redis) : 레디스 키-값 스토어의 퍼시스턴스
        - 스프링 데이터 카산드라(Cassandra) : 카산드라 데이터베이스의 퍼시스턴스
        - [https://spring.io/projects/spring-data](https://spring.io/projects/spring-data)
    - 스프링 데이터에서는 리퍼지터리 인터페이스를 기반으로 이 인터페이스를 구현하는 리퍼지터리를 자동으로 생성해 준다.

    ## 스프링 데이터 JPA를 프로젝트에 추가하기

    - 스프링 데이터 JPA는 JPA 스타터를 통해서 스프링 부트 애플리케이션에서 사용할 수 있다.
    - 이 스타터 의존성에는 스프링 데이터 JPA는 물론이고 JPA를 구현한 Hibernate까지도 포함된다.

    ```xml
    <dependency>
    	<groupId>org.springframework.boot</groupId>
    	<artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    ```

    - 만일 다른 JPA 구현 라이브러리를 사용하고 싶다면 Hibernate 의존성을 제외하고 우리가 선택한 JPA 라이브러리를 포함해야 한다.

    ```xml
    <dependency>
    	<groupId>org.springframework.boot</groupId>
    	<artifactId>spring-boot-starter-data-jpa</artifactId>
    	<exclusions>
    		<exclusion>
    			<groupId>hibernate-entitymanager</groupId>
    			<artifactId>org.hibernate</artifactId>
    		</exclusion>
    	</exclusions>
    </dependency>

    <dependency>
    	<groupId>org.eclipse.persistence</groupId>
    	<artifactId>eclipselink</artifactId>
    	<version>2.5.2</version>
    </dependency>
    ```

    - 지금부터는 우리 애플리케이션의 도메인 객체를 다시 보면서 JPA 퍼시스턴스에 필요한 애노테이션을 추가할 것이다.

    ## 도메인 객체에 애노테이션 추가하기

    - JPA 매핑(mapping) 애노테이션을 우리 도메인 객체에 추가해야 한다.

    ```java
    package tacos;

    import javax.persistence.Entity;
    import javax.persistence.Id;

    import lombok.AccessLevel;
    import lombok.NoArgsConstructor;
    import lombok.Data;
    import lombok.RequiredArgsConstructor;

    @Data
    @RequiredArgsConstructor
    @NoArgsConstructor(access=AccessLevel.PRIVATE, force=true)
    @Entity
    public class Ingredient {
    	@Id
    	private final String id;
    	private final String name;
    	private final Type type;

    	public static enum Type {
    		WRAP, PROTEIN, VEGGIES, CHEESE, SAUCE
    	}
    }
    ```

    - Ingredient를 JPA 개체(entity)로 선언하려면 반드시 @Entity 애노테이션을 추가해야 한다.
    - 그리고 이것의 id 속성에는 반드시 @Id를 지정하여 이 속성이 데이터베이스 개체를 고유하게 식별한다는 것을 나타내야 한다.
    - JPA 애노테이션과 더불어 Ingredient에는 클래스 수준의 @NoArgsConstructor 애노테이션도 추가되었음을 알 수 있다.
    - JPA에서는 개체가 인자 없는(noarguments) 생성자를 가져야 한다.
    - 따라서 Lombok의 @NoArgsConstructor를 지정한 것이다.
    - 하지만 여기서는 인자 없는 생성자의 사용을 원치 않으므로 acess 속성을 AccessLevel.PRIVATE으로 설정하여 클래스 외부에서 사용하지 못하게 했다.
    - 그리고 Ingredient에는 초기화가 필요한 final 속성들이 있으므로 force 속성을 true로 설정하였다.
        - 이에 따라 Lombok이 자동 생성한 생성자에서 그 속성들을 null로 설정한다.
    - @Data는 인자가 있는 생성자를 자동으로 추가한다.
        - 그러나 @NoArgsConstructor가 지정되면 그런 생성자는 제거된다.

    ```java
    package tacos;

    import java.util.Date;
    import java.util.List;
    import javax.persistence.Entity;
    import javax.persistence.GeneratedValue;
    import javax.persistence.GenerationType;
    import javax.persistence.Id;
    import javax.persistence.ManyToMany;
    import javax.persistence.PrePersist;

    import javax.validation.constraints.NotNull;
    import javax.validation.constraints.Size;

    import lombok.Data;

    @Data
    @Entity
    public class Taco {
    	@Id
    	@GeneratedValue(strategy=GenerationType.AUTO)
    	private Long id;

    	private Date createdAt;

    	@NotNull
    	@Size(min=5, message="Name must be at least 5 characters long")
    	private String name;

    	@ManyToMany(targetEntity=Ingredient.class)
    	@Size(min=1, message="You must choose at least 1 ingredient")
    	private List<Ingredient> ingredients;

    	@PrePersist
    	void createdAt() {
    		this.createdAt = new Date();
    	}
    }
    ```

    - Ingredient와 동일하게 Taco 클래스에도 @Entity가 지정되었으며, id 속성에는 @Id가 지정되었다.
        - id 속성에는 데이터베이스가 자동으로 생성해 주는 ID 값이 사용된다.
        - 따라서 strategy 속성의 값이 GenerationType.AUTO로 설정된 @GeneratedValue 애노테이션이 지정되었다.
    - Taco 및 이것과 연관된 Ingredient들 간의 관계를 선언하기 위해 ingredients 속성에는 @ManyToMany 애노테이션이 지정되었다.
        - 하나의 Taco 객체는 많은 Ingredient 객체를 가질 수 있는데, 하나의 Ingredient는 여러 Taco 객체에 포함될 수 있기 때문이다.
    - 또한, @PrePersist 애노테이션이 지정되어 있는 새로운 메서드인 createdAt()이 있다.
        - 이 메서드는 Taco 객체가 저장되기 전에 createdAt 속성을 현재 일자와 시간으로 설정하는데 사용될 것이다.

    ```java
    package tacos;

    import lombok.Data;
    import org.hibernate.validator.constraints.CreditCardNumber;

    import javax.persistence.*;
    import javax.validation.constraints.Digits;
    import javax.validation.constraints.NotBlank;
    import javax.validation.constraints.Pattern;
    import java.io.Serializable;
    import java.util.ArrayList;
    import java.util.Date;
    import java.util.List;

    @Data
    @Entity
    @Table(name="Taco_Order")
    public class Order implements Serializable {
      private static final long serialVersionUID = 1L;
      @Id
      @GeneratedValue(strategy = GenerationType.AUTO)
      private Long id;
      
      private Date placedAt;

      ...

      @ManyToMany(targetEntity = Taco.class)
      private List<Taco> tacos = new ArrayList<>();

      public void addDesign(Taco design) {
        this.tacos.add(design);
      }
      
      @PrePersist
      void placedAt() {
        this.placedAt = new Date();
      }
    }
    ```

    - 클래스 수준의 새로운 애노테이션인 @Table이 있다.
        - 이것은 Order 개체가 데이터베이스의 Taco_Order 테이블에 저장되어야 한다는 것을 나타낸다.
    - @Table 애노테이션은 어떤 개체(entity)에도 사용될 수 있지만, Order의 경우는 반드시 필요하다.
        - 만일 이 애노테이션을 지정하지 않으면 JPA가 Order라는 이름의 테이블로 Order 개체를 저장할 것이다.
        - 그러나 Order는 SQL 예약어이므로 문제가 생기기 때문에 @Table 애노테이션이 필요하다.

    ## JPA 리퍼지터리 선언하기

    - JDBC 버전의 리퍼지터리에서는 리퍼지터리가 제공하는 메서드를 우리가 명시적으로 선언하였다.
    - 그러나 스프링 데이터에서는 그 대신 CrudRepository 인터페이스를 확장(extends)할 수 있다.

    ```java
    package tacos.data;

    import org.springframework.data.repository.CrudRepository;

    import tacos.Ingredient;

    public interface IngredientRepository extends CrudRepository<Ingredient, String> {
    	~~Iterable<Ingredient> findAll();~~
    	~~Ingredient findById(String id);~~
    	~~Ingredient save(Ingredient ingredient);~~
    }
    ```

    - CrudRepository 인터페이스에는 데이터베이스의 CRUD(Create(생성), Read(읽기), Update(변경), Delete(삭제)) 연산을 위한 많은 메서드가 선언되어 있다.
        - CrudRepository는 매개변수화 타입이다.
        - 첫 번째 매개변수는 리퍼지터리에 저장되는 개체 타입이며, 두 번째 매개변수는 개체 ID 속성의 타입이다.

    ```java
    package tacos.data;

    import org.springframework.data.repository.CrudRepository;

    import tacos.Taco;

    public interface TacoRepository extends CrudRepository<Taco, Long> {
    	~~Taco save(Taco design);~~
    }
    ```

    ```java
    package tacos.data;

    import org.springframework.data.repository.CrudRepository;

    import tacos.Order;

    public interface OrderRepository extends CrudRepository<Order, Long> {
    	~~Order save(Order order);~~
    }
    ```

    - CrudRepository 인터페이스에 정의된 많은 메서드의 구현을 포함해서 3개의 인터페이스를 구현하는 클래스를 작성해야 한다고 생각할 수 있다.
        - 그러나 그럴 필요가 없다.
        - 바로 이것이 스프링 데이터 JPA의 장점이다!
        - 애플리케이션이 시작될 때 스프링 데이터 JPA가 각 인터페이스 구현체(클래스 등)를 자동으로 생성해 주기 때문이다.
        - JDBC 기반의 구현에서 했던 것처럼 그것들을 컨트롤러에 주입만 하면 된다.

    ```java
    package tacos;

    import org.springframework.boot.CommandLineRunner;
    import org.springframework.boot.SpringApplication;
    import org.springframework.boot.autoconfigure.SpringBootApplication;
    import org.springframework.context.annotation.Bean;

    import tacos.Ingredient.Type;
    import tacos.data.IngredientRepository;

    @SpringBootApplication
    public class TacoCloudApplication {

    	public static void main(String[] args) {
    		SpringApplication.run(TacoCloudApplication.class, args);
    	}

    	@Bean
    	public CommandLineRunner dataLoader(IngredientRepository repo) {
    		return new CommandLineRunner() {
    			@Override
    			public void run(String... args) throws Exception {
    				repo.save(new Ingredient("FLTO", "Flour Tortilla", Type.WRAP));
    				repo.save(new Ingredient("COTO", "Corn Tortilla", Type.WRAP));
    				repo.save(new Ingredient("GRBF", "Ground Beef", Type.PROTEIN));
    				repo.save(new Ingredient("CARN", "Carnitas", Type.PROTEIN));
    				repo.save(new Ingredient("TMTO", "Diced Tomatoes", Type.VEGGIES));
    				repo.save(new Ingredient("LETC", "Lettuce", Type.VEGGIES));
    				repo.save(new Ingredient("CHED", "Cheddar", Type.CHEESE));
    				repo.save(new Ingredient("JACK", "Monterrey Jack", Type.CHEESE));
    				repo.save(new Ingredient("SLSA", "Salsa", Type.SAUCE));
    				repo.save(new Ingredient("SRCR", "Sour Cream", Type.SAUCE));
    			}
    		};
    	}
    }
    ```

    - 여기서 부트스트랩 클래스를 변경한 이유는 애플리케이션이 시작되면서 호출되는 dataLoader() 메서드에서 식자재 데이터를 데이터베이스에 미리 저장할 필요가 있기 때문이다.

    ```java
    ...
    import java.util.Optional;
    ...
    @Component
    public class IngredientByIdConverter implements Converter<String, Ingredient> {
    	...
    	@Override
    	public Ingredient convert(String id) {
    		~~return ingredientRepo.findById(id);~~
    		Optional<Ingredient> optionalIngredient = ingredientRepo.findById(id);
    		return optionalIngredient.isPresent()? optionalIngredient.get(): null;
    	}
    }
    ```

    - 컨버터를 변경한 것도 우리 애플리케이션에서만 필요해서 그런 것이지 스프링 데이터 JPA를 사용하기 위해 꼭 해야 하는 것은 아니다.

    ## JPA 리퍼지터리 커스터마이징하기

    - CrudRepository에서 제공하는 기본적인 CRUD 연산에 추가하여, 특정 ZIP(우편번호) 코드로 배달된 모든 주문 데이터도 데이터베이스로부터 가져와야 한다고 하자.

    ```java
    List<Order> findByDeliveryZip(String deliveryZip);
    ```

    - 리퍼지터리 구현체를 생성할 때 스프링 데이터는 해당 리퍼지터리 인터페이스에 정의된 메서드를 찾아 메서드 이름을 분석하여, 저장되는 객체(여기서는 Order)의 컨텍스트에서 메서드의 용도가 무엇인지 파악한다.
    - 본질적으로 스프링 데이터는 일종의 DSL(Domain Specific Language)을 정의하고 있어서 퍼시스턴스에 관한 내용이 리퍼지터리 메서드의 시그니처에 표현된다.
    - 리퍼지터리 메서드 이름은 동사, 생략 가능한 처리 대상, By 단어, 그리고 서술어로 구성된다.
    - 지정된 일자 범위 내에서 특정 ZIP 코드로 배달된 모든 주문을 쿼리해야 한다고 가정해 보자.

    ```java
    List<Order> readOrdersByDeliveryZipAndPlacedAtBetween(
    	String deliveryZip, Date startDate, Date endDate);
    ```

    - 이 메서드 이름의 동사는 read다.
        - 또한, 스프링 데이터는 find, read, get이 하나 이상의 개체를 읽는 동의어임을 안다.
        - 만일 일치하는 개체의 수를 의미하는 정수를 반환하는 메서드를 원한다면 count를 동사로 사용할 수도 있다.

    ![chapter03-04](image/chapter03-04.png '스프링 데이터는 리퍼지터리 메서드 시그니처를 분석하여 수행되어야 할 쿼리를 결정한다')

    - 스프링 데이터는 처리 대상에서 대부분의 단어를 무시한다.
        - 따라서 메서드 이름이 readPuppiesBy...일 경우에도 여전히 Order 개체를 찾는다.
        - Order가 CrudRepository 인터페이스의 매개변수로 지정된 타입이기 때문이다.
    - 서술어는 메서드 이름의 By 단어 다음에 나오며, 메서드 시그니처에서 가장 복잡한 부분이다.
        - 스프링 데이터 메서드 시그니처에는 다음 연산자 중 어느 것도 포함될 수 있다.
            - IsAfter, After, IsGreaterThan, GreaterThan
            - IsGreaterThanEqual, GreaterThanEqual
            - IsBefore, Before, IsLessThan, LessThan
            - IsLessThanEqual, LessThanEqual
            - IsBetween, Between
            - IsNull, Null
            - IsNotNull, NotNull
            - IsIn, In
            - IsNotIn, NotIn
            - IsStartingWith, StartingWith, StartsWith
            - IsEndingWith, EndingWith, EndsWith
            - IsContaining, Containing, Contains
            - IsLike, Like
            - IsNotLike, NotLike
            - IsTrue, True
            - IsFalse, False
            - Is, Equals
            - IsNot, Not
            - IgnoringCase, IgnoresCase
            - [https://docs.spring.io/spring-data/jpa/docs/1.5.0.RELEASE/reference/html/jpa.repositories.html](https://docs.spring.io/spring-data/jpa/docs/1.5.0.RELEASE/reference/html/jpa.repositories.html)
- 모든 String 비교에서 대소문자를 무시하기 위해 IgnoringCase와 IgnoresCase 대신 AllIgnoringCase 또는 AllIgnoresCase를 메서드 이름으로 사용할 수 있다.

```java
List<Order> findByDeliveryToAndDeliveryCityAllIgnoresCase(
	String deliveryTo, String deliveryCity);
```

- 마지막으로, 지정된 열의 값을 기준으로 결과를 정렬하기 위해 메서드 이름의 끝에 OrderBy를 추가할 수도 있다.

```java
List<Order> findByDeliveryCityOrderByDeliveryTo(String city);
```

- 더 복잡한 쿼리의 경우는 메서드 이름만으로는 감당하기 어렵다.
- 따라서 이 때는 어떤 이름이든 우리가 원하는 것을 지정한 후 해당 메서드가 호출될 때 수행되는 쿼리에 @Query 애노테이션을 지정하자.

```java
@Query("Order o where o.deliveryCity='Seattle'")
List<Order> readOrdersDeliveredInSeattle();
```

- 그러나 우리가 생각하는 어떤 쿼리를 수행할 때도 @Query를 사용할 수 있다.
- 심지어는 이름 규칙을 준수하여 쿼리를 수행하는 것이 어렵거나 불가능할 때에도 @Query를 사용할 수 있다.