- 만일 블로킹 되는 리퍼지터리(repository)에 의존하는 스프링 WebFlux 리액티브 컨트롤러를 작성한다면, 이 컨트롤러는 해당 리퍼지터리의 데이터 생성을 기다리느라 블로킹될 것이다.
- 따라서 컨트롤러부터 데이터베이스에 이르기까지 데이터의 전체 플로우(flow)가 리액티브하고 블로킹되지 않는 것이 중요하다.

# 스프링 데이터의 리액티브 개념 이해하기

- 스프링 데이터 Kay 릴리즈 트레인부터 스프링 데이터는 리액티브 리퍼지터리의 지원을 제공하기 시작하였다.
- 여기에는 카산드라(Cassandra), 몽고DB(MongoDB), 카우치베이스(Couchbase), 레디스(Redis)로 데이터를 저장할 때 리액티브 프로그래밍 모델을 지원하는 것이 포함된다.
- 그러나 관계형 데이터베이스나 JPA는 리액티브 리퍼지터리가 지원되지 않는다.
- 관계형 데이터베이스는 업계에서 가장 많이 사용되지만, 스프링 데이터 JPA로 리액티브 프로그래밍 모델을 지원하려면 관계형 데이터베이스와 JDBC 드라이버 역시 블로킹되지 않는 리액티브 모델을 지원해야 한다.
- 아쉽게도 최소한 지금은 관계형 데이터베이스를 리액티브하게 사용하기 위한 지원이 되지 않는다.

## 스프링 데이터 리액티브 개요

- 스프링 데이터 리액티브의 핵심은 다음과 같이 요약할 수 있다.
    - 즉, 리액티브 리퍼지터리는 도메인 타입이나 컬렉션 대신 Mono나 Flux를 인자로 받거나 반환하는 메서드를 갖는다는 것이다.

    ```java
    Flux<Ingredient> findByType(Ingredient.Type type);
    <Taco> Flux<Taco> saveAll(Publisher<Taco> tacoPublisher);
    ```

- 간단히 말해, 스프링 데이터의 리액티브 리퍼지터리는 스프링 데이터의 리액티브가 아닌 리퍼지터리와 거의 동일한 프로그래밍 모델을 공유한다.
- 단, 리액티브 리퍼지터리는 도메인 타입이나 컬렉션 대신 Mono나 Flux를 인자로 받거나 반환하는 메서드를 갖는다는 것만 다르다.

## 리액티브와 리액티브가 안인 타입 간의 변환

- 리액티브 프로그래밍의 장점은 클라이언트부터 데이터베이스까지 리액티브 모델을 가질 때 완전하게 발휘한다.
- 그러나 데이터베이스가 리액티브가 아닌 경우에도 여전히 일부 장점을 살릴 수 있다.
- 심지어는 우리가 선택한 데이터베이스가 블로킹 없는 리액티브 쿼리를 지원하지 않더라도 블로킹 되는 방식으로 데이터를 가져와서 가능한 빨리 리액티브 타입으로 변환하여 상위 컴포넌트들이 리액티브의 장점을 활용하게 할 수 있다.

```java
List<Order> findByUser(User user);
```

- 이처럼 블로킹 방식의 JPA 리퍼지터리 메서드를 호출해서는 곤란하다.
- 그러나 이 경우 가능한 빨리 리액티브가 아닌 List를 Flux로 변환하여 결과를 처리할 수 있다.
- 이때는 Flux.fromIterable()을 사용하면 된다.

```java
List<Order> orders = repo.findByUser(someUser);
Flux<Order> orderFlux = Flux.fromIterable(orders);
```

- 마찬가지로 특정 ID의 주문 데이터를 가져올 때는 다음과 같이 Mono로 변환하면 된다.

```java
Order order = repo.findById(id);
Mono<Order> orderMono = Mono.just(order);
```

- 이처럼 Mono.just() 메서드와 Flux의 fromIterable(), fromArray(), fromStream() 메서드를 사용하면 리퍼지터리의 리액티브가 아닌 블로킹 코드를 격리시키고 애플리케이션의 어디서든 리액티브 타입으로 처리하게 할 수 있다.
- 예를 들어, WebFlux 컨트롤러가 `Mono<Taco>`를 받은 후 이것을 스프링 데이터 JPA 리퍼지터리의 save() 메서드를 사용해서 저장한다고 해보자.
- 이때도 아무 문제가 없다.
- Mono의 block() 메서드를 호출해서 Taco 객체로 추출하면 된다.

```java
Taco taco = tacoMono.block();
tacoRepo.save(taco);
```

- 이름이 암시하듯이 block() 메서드는 추출작업을 수행하기 위해 블로킹 오퍼레이션을 실행한다.
- Flux의 데이터를 추출할 때는 toIterable()을 사용할 수 있다.

```java
Iterable<Taco> tacos = tacoFlux.toIterable();
tacoRepo.saveAll(tacos);
```

- 그러나 Mono.block()이나 Flux.toIterable()은 추출 작업을 할 때 블로킹이 되므로 리액티브 프로그래밍 모델을 벗어난다.
- 따라서 이런 식의 Mono나 Flux 사용은 가급적 적게 하는 것이 좋다.
- 이처럼 블로킹되는 추출 오퍼레이션을 피하는 더 리액티브한 방법이 있다.
- 즉, Mono나 Flux를 구독하면서 발행되는 요소 각각에 대해 원하는 오퍼레이션을 수행하는 것이다.

```java
tacoFlux.subscribe(taco -> {
	tacoRepo.save(taco);
});
```

- 여기서 리퍼지터리의 save() 메서드는 여전히 리액티브가 아닌 블로킹 오퍼레이션이다.
- 그러나 Flux나 Mono가 발행하는 데이터를 소비하고 처리하는 리액티브 방식의 subscribe()를 사용하므로 블로킹 방식의 일괄처리보다는 더 바람직하다.

## 리액티브 리퍼지터리 개발하기

- 스프링 데이터의 가장 놀라운 기능 중 하나는 리퍼지터리 인터페이스를 선언하면 이것을 스프링 데이터가 런타임 시에 자동으로 구현해 준다는 것이다.
- 리액티브가 아닌 리퍼지터리 지원 위에 구축된 스프링 데이터 카산드라와 스프링 데이터 몽고DB는 리액티브 모델도 지원한다.
- 따라서 데이터 퍼시스턴스를 제공하는 백엔드로 이 데이터베이스들을 사용하면, 스프링 애플리케이션이 웹 계층부터 데이터베이스까지에 걸쳐 진정한 엔드-to-엔드 리액티브 플로우를 제공할 수 있다.

# 리액티브 카산드라 리퍼지터리 사용하기

- 카산드라는 분산처리, 고성능, 상시 가용, 궁극적인 일관성을 갖는 NoSQL 데이터베이스다.
- 간단히 말해서 카산드라는 데이터를 테이블에 저장된 행(row)으로 처리하며, 각 행은 일 대 다 관계의 많은 분산 코드에 걸쳐 분할된다.
- 즉, 한 노드가 모든 데이터를 갖지는 않지만, 특정 행은 다수의 노드에 걸쳐 복제될 수 있으므로 단일 장애점(single point of failure, 한 노드에 문제가 생기면 전체가 사용 불가능)을 없애준다.
- 스프링 데이터 카산드라는 애플리케이션의 도메인 타입을 데이터베이스 구조에 매핑하는 애노테이션을 제공한다.

## 스프링 데이터 카산드라 활용하기

- 스프링 데이터 카산드라의 리액티브 리퍼지터리 지원을 사용하려면 리액티브 스프링 데이터 카산드라의 스프링 부트 스타터 의존성을 추가해야 한다.
- 우선, 카산드라의 리액티브가 아닌 리퍼지터리를 작성한다면 다음 의존성을 빌드에 추가하면 된다.

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-data-cassandra</artifactId>
</dependency>
```

- 그러나 이번 장에서는 리액티브 리퍼지터리를 작성할 것이므로 카산드라의 리액티브 리퍼지터리를 활성화하는 다음의 스타터 의존성을 추가해야 한다.

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-data-cassandra-reactive</artifactId>
</dependency>
```

- 스프링 데이터 JPA 스타터 의존성과 모든 관계형 데이터베이스 의존성(예를 들어, JDBC 드라이버 의존성이나 H2 의존성)을 빌드에서 삭제해야 한다.
- 별도의 구성 없이 리액티브 카산드라 리퍼지터리를 작성할 수 있다.
- 단, 일부 구성은 제공해야 하는데, 최소한 리퍼지터리가 운용되는 키 공간(key space)의 이름을 구성해야 하며, 이렇게 하기 위해 해당 키 공간을 생성해야 한다.
    - 카산드라에서 키 공간은 카산드라 노드의 테이블들을 모아 놓은 것이다.
    - 이것은 관계형 데이터베이스에서 테이블, 뷰, 제약조건을 모아 놓는 스키마(schema)와 유사하다.
- 키 공간을 자동으로 생성하도록 스프링 데이터 카산드라를 구성할 수 있지만, 우리가 직접 생성(또는 기존 키 공간을 사용하게)하는 것이 훨씬 쉽다.
- 이때 카산드라 CQL(Cassandra Query Language) 셸에서 다음과 같이 create keyspace 명령을 사용하면 타코 클라우드 애플리케이션의 키 공간을 생성할 수 있다.

```bash
cqlsh> create keyspace tacocloud
	... with replication={'class':'SimpleStrategy', 'replication_factor':1}
	... and durable_writes=true;
```

- 여기서는 단순 복제(replication) 및 durable_writes가 true로 설정된 tacocloud라는 키 공간을 생성한다.
- replication_factor가 1일 때는 각 행의 데이터를 여러 벌 복제하지 않고 한 벌만 유지함으로 나타낸다.
- 복제를 처리하는(어떤 노드에 복제할 것인지 결정하는) 방법은 복제 전략이 결정하며, 여기서는 SimpleStrategy를 지정하였다.
- SimpleStrategy 복제 전략은 단일 데이터 센터 사용 시에(또는 데모용 코드에) 좋다.
- 그러나 카산드라 클러스터(cluster)가 다수의 데이터 센터에 확산되어 있을 때는 NetworkTopologyStrategy를 고려할 수 있다.
- 키 공간을 생성했으므로 이제는 spring.data.cassandra.keyspace-name 속성을 구성해서 스프링 데이터 카산드라가 해당 키 공간을 사용하도록 알려주어야 한다.

```yaml
spring:
	data:
		cassandra:
			keyspace-name: tacocloud
			schema-action: recreate-drop-unused
```

- 여기서는 키 공간 외에 spring.data.cassandra.schema-action을 recreate-drop-unused로 설정하였다.
    - 이 설정은 개발 목적에 매우 유용하다.
    - 왜냐하면, 애플리케이션이 매 번 시작할때마다 모든 테이블과 사용자 정의 타입이 삭제되고 재생성되기 때문이다.
    - 기본값은 none이며, 이것은 스키마에 대해 아무 조치를 취하지 않는다.
    - 따라서 애플리케이션이 시작하더라도 모든 테이블을 삭제하지 않는 실무 설정에 유용하다.
- 기본적으로 스프링 데이터 카산드라는 카산드라가 로컬로 실행되면서 9092 포트를 리스닝하는 것으로 간주한다.
- 그러나 이것을 실무 설정에 하듯이 변경하고 싶을 때는 다음과 같이 spring.data.cassandra.contact-points와 spring.data.cassandra.port 속성을 설정하면 된다.

    ```yaml
    spring:
    	data:
    		cassandra:
    			keyspace-name: tacocloud
    			contact-points:
    			- casshot-1.tacocloud.com
    			- casshot-2.tacocloud.com
    			- casshot-3.tacocloud.com
    			port: 9043
    ```

    - 기본적으로 localhost로 설정되지만, 이 예처럼 호스트 이름의 목록을 설정할 수 있다.
    - 이 경우 각 노드의 호스트 연결을 시도하여 카산드라 클러스터에 단일 장애점이 생기지 않게 해주며, contact-points에 지정된 호스트 중 하나를 통해 애플리케이션이 클러스터에 연결될 수 있게 해준다.
- 카산드라 클러스터의 사용자 이름과 비밀번호를 지정해야 할 수도 있다.
    - 이때는 spring.data.cassandra.username과 spring.data.cassandra.password 속성을 설정하면 된다.

    ```yaml
    spring:
    	data:
    		cassandra:
    			...
    			username: tacocloud
    			password: s3cr3tP455w0rd
    ```

## 카산드라 데이터 모델링 이해하기

- 카산드라 데이터 모델링에 관해 알아 둘 몇 가지 가장 중요한 사항은 다음과 같다.
- 카산드라 테이블은 얼마든지 많은 열(column)을 가질 수 있다.
    - 그러나 모든 행이 같은 열을 갖지 않고, 행마다 서로 다른 열을 가질 수 있다.
- 카산드라 데이터베이스는 다수의 파티션에 걸쳐 분할된다.
    - 테이블의 어떤 행도 하나 이상의 파티션에서 관리될 수 있다.
    - 그러나 각 파티션은 모든 행을 갖지 않고, 서로 다른 행을 가질 수 있다.
- 카산드라 테이블은 두 종류의 키를 갖는다.
    - 파티션 키와 클러스터링 키다.
    - 각 행이 유지 관리되는 파티션을 결정하기 위해 해시 오퍼레이션이 각 행의 파티션 키에 수행된다.
    - 클러스터링 키는 각 행이 파티션 내부에서 유지 관리되는 순서를 결정한다.
        - 쿼리의 결과에 나타나는 순서가 아님
- 카산드라는 읽기 오퍼레이션에 최적화되어 있다.
    - 따라서 테이블이 비정규화되고 데이터가 다수의 테이블에 걸쳐 중복되는 경우가 흔하다.
- 이 내용을 보면 알 수 있듯이, JPA 애노테이션을 단순히 카산드라 애노테이션으로 변경한다고 해서 타코 도메인 타입을 카산드라에 적용할 수 있는 것은 아니다.

## 카산드라 퍼시스턴스의 도메인 타입 매핑

- 카산드라로 가장 간단하게 매핑할 수 있는 Ingredient 클래스로부터 시작해 보자.

```java
package tacos;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@NoArgsConstructor(access=AccessLevel.PRIVATE, force=true)
@Table("ingredients")
public class Ingredient {
	@PrimaryKey
	private final String id;
	private final String name;
	private final Type type;

	public static enum Type {
		WRAP, PROTEIN, VEGGIES, CHEESE, SAUCE
	}
}
```

- 여기서는 JPA 퍼시스턴스에서 클래스에 지정했던 @Entity 대신 @Table을 지정하였다.
- @Table은 식재료 데이터가 ingredients 테이블에 저장 및 유지되어야 한다는 것을 나타낸다.
- 그리고 id 속성에 지정했던 @Id 대신 @PrimaryKey 애노테이션을 지정하였다.

```java
package tacos;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.rest.core.annotation.RestResource;
import com.datastax.driver.core.utils.UUIDs;
import lombok.Data;

@Data
@RestResource(rel="tacos", path="tacos")
@Table("tacos")
public class Taco {
	@PrimaryKeyColumn(type=PrimaryKeyType.PARTITIONED) // 파티션 키를 정의한다.
	private UUID id = UUIDs.timeBased();

	@NotNull
	@Size(min=5, message="Name must be at least 5 characters long")
	private String name;

	@PrimaryKeyColumn(type=PrimaryKeyType.CLUSTERED, ordering=Ordering.DESCENDING) // 클러스터링 키를 정의한다.
	private Date createdAt = new Date();

	@Size(min=1, message="You must choose at least 1 ingredient")
	@Column("ingredients") // List를 ingredients 열에 매핑한다.
	private List<IngredientUDT> ingredients;
}
```

- 타코 데이터를 저장하는 테이블의 이름을 tacos로 지정하기 위해 Ingredient와 마찬가지로 @Table 애노테이션이 사용되었다.
- id 속성은 여전히 기본 키(primary key)다.
    - 그러나 여기서는 이것이 두 개의 기본 키 열 중 하나다.
    - 더 자세하게 말해서, id 속성은 PrimaryKeyType.PARTITIONED 타입으로 @PrimaryKeyColumn에 지정되어 있다.
    - 이것은 타코 데이터의 각 행이 저장되는 카산드라 파티션을 결정하기 위해 사용되는 파티션 키가 id 속성이라는 것을 나타낸다.
- 또한, id 속성의 타입은 Long 대신 UUID이며, 이것은 자동 생성되는 ID 값을 저장하는 속성에 흔히 사용하는 타입이다.
- 그리고 UUID는 새로운 Taco 객체가 생성될 때 시간 기반의 UUID 값으로 초기화된다.
    - 그러나 데이터베이스로부터 기존 Taco 객체를 읽을 때 무시할 수 있다.
- 조금 아래로 내려가다보면 또 다른 기본 키 열로 지정된 createdAt 속성이 있다.
    - 그러나 여기서 @PrimaryKeyColumn의 type 속성이 PrimaryKeyType.CLUSTERED로 설정되어 있다.
    - 이것은 createdAt 속성이 클러스터링 키라는것을 나타낸다.
    - 이미 얘기했던 대로, 클러스터링 키는 파티션 내부에서 행의 순서를 결정하기 위해 사용되며, 여기서는 내림차순(descending order)으로 설정되었다.
- 제일 끝에 정의된 ingredients 속성(여기서는 열 이름도 ingredients다.)은 Ingredient 객체를 저장하는 List 대신 IngredientUDT 객체를 저장하는 List로 정의되었다.
- 그런데 왜 새로운 IngredientUDT 클래스를 사용해야 할까?
    - Ingredient 클래스를 재사용할 수 없을까?
    - 간단히 말해, ingredients 열처럼 데이터의 컬렉션을 포함하는 열은 네이티브 타입(정수, 문자열 등)의 컬렉션이나 사용자 정의 타입(User Defined Type, UDT)의 컬렉션이어야 하기 때문이다.
- Ingredient 클래스는 사용자 정의 타입으로 사용할 수 없다.
    - 왜냐하면 @Table 애노테이션이 이미 Ingredient 클래스를 카산드라에 저장하는 엔터티(도메인 타입)로 매핑했기 때문이다.

```java
package tacos;

import org.springframework.data.cassandra.core.mapping.UserDefinedType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@NoArgsConstructor(access=AccessLevel.PRIVATE, force=true)
@UserDefinedType("ingredient")
public class IngredientUDT {
	private final String name;
	private final Ingredient.Type type;
}
```

- IngredientUDT는 Ingredient 클래스와 매우 유사하지만, 엔터티에 매핑하는 데 필요한 요구사항은 훨씬 더 간단하다.
- 우선, 이 클래스에는 카산드라의 사용자 정의 타입인 것을 알 수 있도록 @UserDefinedType이 지정되었다.
- 또한, IngredientUDT 클래스는 id 속성을 포함하지 않는다.
    - 소스 클래스인 Ingredient의 id 속성을 가질 필요가 없기 때문이다.
        - 사용자 정의 타입은 우리가 원하는 어떤 속성도 가질 수 있지만, 테이블 정의와 똑같지 않아도 된다.

![chapter12-01](image/chapter12-01.png '외부 키와 조인을 사용하는 대신 카산드라 테이블은 비정규화되며, 관련된 테이블로부터 복사된 데이터를 포함하는 사용자 정의 타입을 갖는다. 여기서 Order, User, Taco, Ingredient는 엔터티(도메인 타입)다.')

- 그림에서는 사용자 정의 타입이 포함된 타코 클라우드 데이터베이스 전체의 데이터 모델을 보여준다.
- 여기서 IngredientUDT는 ingredient 객체로부터 복사된 데이터를 갖는다.
- 그리고 Taco 객체가 tacos 테이블에 저장될 때는 IngredientUDT의 List가 ingredients 열에 저장된다.
- 카산드라에 같이 제공되는 CQL과 cqlsh를 사용해서 쿼리하면 다음과 같다.

```bash
cqlsh:tacocloud> select id, name, createdAt, ingredients from tacos;
id        | name      | createdAt  | ingredients
----------+-----------+------------+--------------------------------------
827390... | Carnivore | 2018-04... | [{name: 'Flour Tortilla', type: 'WRAP'},
                                      {name: 'Carnitas', type: 'PROTEIN'},
                                      {name: 'Sour Cream', type: 'SAUCE'},
                                      {name: 'Salsa', type: 'SAUCE'},
                                      {name: 'Cheddar', type: 'CHEESE'}]
(1 rows)
```

- 이것을 보면 알 수 있듯이, id, name, createdAt 열은 단순 값을 가지며, 관계형 데이터베이스의 경우와 그리 다르지 않다.
- 그러나 ingredients 열은 조금 다르다.
    - 이 열은 ingredient의 사용자 정의 타입인 IngredientUDT의 컬렉션을 포함하도록 정의되어 있으므로, 이 열의 값은 JSON 객체로 채워진 JSON 배열이 된다.

```java
@Data
@Table("tacoorders") // tacoorders 테이블로 매핑한다.
public class Order implements Serializable {
	private static final long serialVersionUID = 1L;

	@PrimaryKey // 기본 키를 선언한다.
	private UUID id = UUIDs.timeBased();

	private Date placedAt = new Date();
	
	@Column("user") // user 열에 사용자 정의 타입을 매핑한다.
	private UserUDT user; // 여백을 줄이기 위해 배달 관련 속성과 신용카드 관련 속성은 생략하였다.

	@Column("tacos") // tacos 열에 사용자 정의 타입을 매핑한다.
	private List<TacoUDT> tacos = new ArrayList<>();

	public void addDesign(TacoUDT design) {
		this.tacos.add(design);
	}
}
```

- 관계형 데이터베이스처럼 다른 테이블의 행들을 외부 키를 통해 조인하는 것이 아니고, 주문된 모든 타코의 데이터를 tacoorders 테이블에 포함시킨다.
    - 빠른 데이터 검색에 테이블을 최적화하기 위함이다.

```java
@Data
@UserDefinedType("taco")
public class TacoUDT {
	private final String name;
	private final List<IngredientUDT> ingredients;
}
```

```java
@UserDefinedType("user")
@Data
public class UserUDT {
	private final String username;
	private final String fullname;
	private final String phoneNumber;
}
```

## 리액티브 카산드라 리퍼지터리 작성하기

- 스프링 데이터로 리액티브가 아닌 리퍼지터리를 작성할 때는 스프링 데이터의 기본 리퍼지터리 인터페이스 중 하나를 확장하는 인터페이스만 선언하면 된다.
- 그리고 선택적이지만 커스텀 쿼리의 쿼리 메서드들을 추가로 선언할 수 있다.
- 리액티브 리퍼지터리를 작성하는 것도 이와 크게 달지 않다.
- 가장 큰 차이점은 다른 종류의 기본 리퍼지터리 인터페이스를 확장하는 것과 도메인 타입이나 컬렉션 대신 Mono나 Flux 같은 리액티브 타입을 메서드에서 처리하는 것이다.
- 리액티브 카산드라 리퍼지터리를 작성할 때는 두 개의 기본 인터페이스인 ReactiveCassandraRepository나 ReactiveCrudRepository를 선택할 수 있다.
- ReactiveCassandraRepository는 ReactiveCrudRepository를 확장하여 새 객체가 저장될 때 사용되는 insert() 메서드의 몇 가지 변형 버전을 제공하며, 이외에는 ReactiveCrudRepository와 동일한 메서드를 제공한다.
- 만일 많은 데이터를 추가한다면 ReactiveCassandraRepository를 선택할 수 있으며, 그렇지 않을 때는 ReactiveCrudRepository를 선택하는 것이 좋다.
- 타코 클라우드 애플리케이션에 이미 작성된 리퍼지터리 인터페이스를 변경할 때 가장 먼저해야 할 일은 CrudRepository 대신 ReactiveCrudRepository나 ReactiveCassandraRepository를 확장하여 해당 리퍼지터리를 리액티브하게 만드는 것이다.

```java
public interface IngredientRepository extends ReactiveCrudRepository<Ingredient, String> {
}
```

- IngredientRepository에는 어떤 커스텀 쿼리 메서드도 정의하지 않았다.
- 따라서 IngredientRepository를 리액티브 리퍼지터리로 만드는 데 추가로 할 일은 없다.
- 그러나 이제는 ReactiveCrudRepository를 확장하므로 IngredientRepository의 메서드들은 Flux나 Mono 타입을 처리한다.

```java
@GetMapping
public Flux<Ingredient> allIngredients() {
	return repo.findAll();
}
```

- TacoRepository 인터페이스의 변경은 약간 더 복잡하다.
- 즉, PagingAndSortingRepository 대신 ReactiveCassandraRepository를 확장해야 한다.
- 그리고 제네릭 타입 매개변수로 Long 타입의 ID 속성을 갖는 Taco 객체 대신, ID를 UUID 속성으로 갖는 Taco 객체를 사용해야 한다.

```java
public interface TacoRepository extends ReactiveCrudRepository<Taco, UUID> {
}
```

- 새롭게 변경된 TacoRepository는 이것의 findAll() 메서드로부터 `Flux<Ingredient>`를 반환한다.
- 따라서 PagingAndSortingRepository 인터페이스의 확장이나 결과 페이지의 처리에 관해 더 이상 신경 쓰지 않아도 된다.
- 대신에 DesignTacoController의 recentTacos() 메서드에서는 자신이 반환하는 Flux에 take()를 호출하여 결과의 한 페이지에 채울 Taco 객체의 수를 제한해야 한다.
- OrderRepository의 변경도 간단하다.
- CrudRepository 대신 ReactiveCassandraRepository를 확장하면 된다.

```java
public interface OrderRepository extends ReactiveCassandraRepository<Order, UUID> {
}
```

- 마지막으로 UserRepository를 살펴보자.
- UserRepository는 커스텀 쿼리 메서드인 findByUsername()을 갖고 있다.
- 리액티브 카산드라 리퍼지터리로 변경된 UserRepository 인터페이스는 다음과 같다.

```java
public interface UserRepository extends ReactiveCassandraRepository<User, UUID> {
	@AllowFiltering
	Mono<User> findByUsername(String username);
}
```

- 우선, UserRepository는 이제 리액티브 리퍼지터리이므로 findByUsername()에서 User 객체를 반환하면 안 된다.
- 따라서 `Mono<User>`를 반환하도록 변경하였다.
- 일반적으로 리액티브 리퍼지터리에 작성하는 커스텀 쿼리 메서드에서는 Mono(하나의 값만 반환되는 경우)나 Flux(여러 개의 값이 반환되는 경우)를 반환한다.
- 또한, 카산드라의 특성상 관계형 데이터베이스에서 SQL로 하듯이 테이블을 단순하게 where 절로 쿼리할 수 없다.
- 카산드라는 데이터 읽기에 최적화된다.
- 그러나 where 절을 사용한 필터링 결과는 빠른 쿼리와는 달리 너무 느리게 처리될 수 있다.
- 그렇지만 결과가 하나 이상의 열로 필터링되는 테이블 쿼리에는 매우 유용하므로 where 절을 사용할 필요가 있다.
- 이때 @AllowFiltering 애노테이션을 사용하면 된다.
- @AllowFiltering을 지정하지 않은 findByUsername()의 경우 내부적으로 다음과 같이 쿼리가 수행될 것이라고 예상할 수 있다.

```sql
select * from users where username='검색할 사용자 이름';
```

- 그러나 다시 말하지만, 이처럼 단순한 where 절은 카산드라에서 허용되지 않는다.
- 따라서 @AllowFiltering 애노테이션을 findByUsername()에 지정하여 다음과 같은 쿼리가 내부적으로 수행되게 할 수있다.

```sql
select * from users where username='검색할 사용자 이름' allow filtering;
```

- 쿼리 끝의 allow filtering 절은 '쿼리 성능에 잠재적인 영향을 준다는 것을 알고 있지만, 우쨌든 수행해야 한다'는 것을 카산드라에 알려준다.
- 이 경우 카산드라는 where 절을 허용하고 결과 데이터를 필터링한다.

# 리액티브 몽고DB 리퍼지터리 작성하기

- 몽고DB는 잘 알려진 NoSQL 데이터베이스 중 하나다.
- 카산드라가 테이블의 행으로 데이터를 저장하는 데이터베이스인 반면, 몽고DB는 문서형 데이터베이스(document database)다.
- 더 자세히 말해서, 몽고DB는 BSON(Binary JSON) 형식의 문서로 데이터를 저장하며, 다른 데이터베이스에서 데이터를 쿼리하는 것과 거의 유사한 방법으로 문서를 쿼리하거나 검색할 수 있다.

## 스프링 데이터 몽고DB 활성화하기

- 스프링 데이터 몽고DB를 사용하려면 스프링 데이터 몽고DB 스타터를 프로젝트 빌드에 추가해야 한다.
- 리액티브가 아닌 몽고 DB를 사용하고자 할 때는 다음 의존성을 빌드에 추가한다.

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

- 이번 장에서는 리액티브 리퍼지터리를 작성할 것이므로 리액티브 스프링 데이터 몽고DB 스타터 의존성을 추가해야 한다.

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-data-mongodb-reactive</artifactId>
</dependency>
```

- 기본적으로 스프링 데이터 몽고DB는 몽고DB가 로컬로 실행되면서 27017 포트를 리스닝하는 것으로 간주한다.
- 러나 테스트와 개발에 편리하도록 내장된 몽고DB를 대신 사용할 수 있다.
- 이때는 다음과 같이 Flapdoodle 내장 몽고DB 의존성을 빌드에 추가하면 된다.

```xml
<dependency>
	<groupId>de.flapdoodle.embed</groupId>
	<artifactId>de.flapdoodle.embed.mongo</artifactId>
</dependency>
```

- Flapdoodle 내장 데이터베이스는 인메모리(in-memory, 메모리에서 실행되는) 몽고DB 데이터베이스를 사용하는 것과 동일한 편의성(H2 관계형 데이터베이스의 편의성)을 제공한다.
- 내장 데이터베이스는 개발이나 테스트 목적에는 좋다.
- 그러나 애플리케이션을 실무로 이양할 때는 어디에 있는 몽고DB를 어떻게 사용할 것인지를 알려주기 위해 다음과 같이 일부 속성을 설정해야 한다.

```yaml
data:
	mongodb:
		host: mongodb.tacocloud.com
		port: 27018
		username: tacocloud
		password: s3cr3tp455w0rd
```

- 이 속성 모두가 반드시 설정되어야 하는 것은 아니지만 알아 둘 필요가 있다.
- 각 속성의 내역은 다음과 같다.
    - spring.data.mongodb.host : 몽고DB 서버가 실행 중인 호스트 이름이며, 기본값은 localhost다.
    - spring.data.mongodb.port : 몽고DB 서버가 리스닝하는 포트이며, 기본값은 27017이다.
    - spring.data.mongodb.username : 몽고DB 접근에 사용되는 사용자 이름
    - spring.data.mongodb.password : 몽고DB 접근에 사용되는 비밀번호
    - spring.data.mongodb.database : 데이터베이스 이름이며, 기본값은 test다.

## 도메인 타입을 문서로 매핑하기

- 스프링 데이터 몽고DB는 몽고DB에 저장되는 문서 구조로 도메인 타입을 매핑하는 데 유용한 애노테이션들을 제공한다.
- 이런 애노테이션들이 6개  있지만, 그 중 3개만이 대부분의 경우에 유용하다.
    - @Id : 이것이 지정된 속성을 문서 ID로 저장한다.
    - @Document : 이것이 지정된 도메인 타입을 몽고DB에 저장되는 문서로 선언한다.
    - @Field : 몽고DB의 문서에 속성을 저장하기 위해 필드 이름(과 선택적인 순서)을 지정한다.
- 이러한 세 개의 애노테이션 중에 @Id와 @Document만 반드시 필요하다.
- 그리고 @Field가 지정되지 않은 도메인 타입의 속성들은 필드 이름과 속성 이름을 같은 것으로 간주한다.

```java
package tacos;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@NoArgsConstructor(access=AccessLevel.PRIVATE, force=true)
@Document
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

- 여기서 Ingredient가 몽고DB에 저장되거나 읽을 수 있는 문서 엔터티라는 것을 나타내기 위해 클래스 수준의 @Document 애노테이션을 지정하였다.
- 기본적으로 컬렉션(관계형 데이터베이스의 테이블과 유사함) 이름은 클래스 이름과 같고 첫 자만 소문자다.
- 여기서는 컬렉션 이름을 지정하지 않으므로 Ingredient 객체는 ingredient라는 이름의 컬렉션에 저장된다.
- 그러나 다음과 같이 @Document의 collection 속성을  설정하여 변경할 수 있다.

```java
@Data
@RequiredArgsConstructor
@NoArgsConstructor(access=AccessLevel.PRIVATE, force=true)
@Document(collection="ingredients")
public class Ingredient {
	...
}
```

- 그리고 id 속성에는 @Id가 지정되었다.
- 이것은 저장된 문서의 ID로 id 속성을 지정한다.
- String과 Long 타입을 포함해서 Serializable 타입인 어떤 속성에도 @Id를 사용할 수 있다.
- 여기서는 id 속성이 String 타입으로 지정되었으므로 @Id를 사용하기 위해 다른 타입으로 변경할 필요가 없다.
    - 자바에서 String 클래스는 Serializable 인터페이스를 구현하고 있으므로 String 객체는 String 타입이면서 동시에 Serializable 타입도 된다.
- Taco의 경우에도 @Document를 지정해야 한다.
- 그리고 지정된 문서의 ID로 id 속성을 지정하기 위해 @Id를 사용한다.

```java
@Data
@RestResource(rel="tacos", path="tacos")
@Document
public class Taco {
	@Id
	private String id;

	@NotNull
	@Size(min=5, message="Name must be at least 5 characters long")
	private String name;

	private Date createdAt = new Date();

	@Size(min=1, message="You must choose at least 1 ingredient")
	private List<Ingredient> ingredients;
}
```

- 두 개이 서로 다른 기본 키 처리와 사용자 정의 타입 참조에 따른 어려움은 카산드라에만 국한된 것이었다.
- 몽고DB의 경우는 Taco 매핑이 훨씬 더 간단하다.
- 그렇지만 Taco와 관련해서 알아 둘 것이 있다.
    - 우선, id 속성의 타입이 String으로 변경되었다.
        - JPA 버전의 Long 타입이나 카산드라 버전의 UUID와 다르다.
    - 이미 얘기했듯이, @Id는 어떤 Serializable 타입에도 적용될 수 있으므로 Serializable 인터페이스를 구현하는 또 다른 타입을 사용할 수도 있을 것이다.
    - 그러나 ID로 String 타입의 속성을 사용하면 이 속성 값이 데이터베이스에 저장될 때 몽고DB가 자동으로 ID 값을 지정해 준다.
        - null일 경우
- 카산드라와는 다르게 몽고DB에서는 사용자 정의 타입을 만들 필요 없이 어떤 타입도 사용할 수 있다.
- @Document가 지정된 또 다른 타입이나 단순한 POJO(Plain Old Java Object) 모두 가능하다.

```java
@Data
@Document
public class Order implements Serializable {
	private static final long serialVersionID = 1L;

	@Id
	private String id;

	private Date placedAt = new Date();
	
	@Field("customer")
	private User user;

	// 간단하게 하기 위해 다른 속성들은 생략하였다.

	private List<Taco> tacos = new ArrayList<>();

	public void addDesign(Taco design) {
		this.tacos.add(design);
	}
}
```

- 다른 도메인 타입처럼 Order 역시 @Document와 @Id만 지정하면 된다.
- 그렇지만 user 속성에는 @Field를 지정하였다.
- customer 열을 문서에 저장한다는 것을 나타내기 위해서다.

```java
@Data
@NoArgsConstructor(access=AccessLevel.PRIVATE, force=true)
@RequiredArgsConstructor
@Document
public class User implements UserDetails {
	private static final long serialVersionID = 1L;

	@Id
	private String id;

	private final String username;

	private final String password;
	private final String fullname;
	private final String street;
	private final String city;
	private final String state;
	private final String zip;
	private final String phoneNumber;

	// 코드를 간략하게 하기 위해 UserDetails 메서드는 생략하였다.
}
```

## 리액티브 몽고DB 리퍼지터리 인터페이스 작성하기

- 몽고DB의 리액티브 리퍼지터리를 작성할 때는 ReactiveCrudRepository나 ReactiveMongoRepository를 선택할 수 있다.
- 둘 간의 차이점은 이렇다.
    - ReactiveCrudRepository가 새로운 문서나 기존 문서의 save() 메서드에 의존하는 반면, ReactiveMongoRepository는 새로운 문서의 저장에 최적화된 소수의 특별한 insert() 메서드를 제공한다.

```java
package tacos.data;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import tacos.Ingredient;

@CrossOrigin(origins="*")
public interface IngredientRepository extends ReactiveCrudRepository<Ingredient, String> {
}
```

- 이것은 카산드라의 IngredientRepository 인터페이스와 똑같아 보인다.
    - 그렇다.
    - 변경되지 않은 똑같은 인터페이스다.
- 이것은 ReactiveCrudRepository 인터페이스를 확장할 때의 장점 중 하나다.
- 즉, 다양한 데이터베이스 타입에 걸쳐 동일하므로 몽고DB나 카산드라의 경우에도 똑같이 사용된다.
- IngredientRepository는 리액티브 리퍼지터리이므로 이것의 메서드는 그냥 도메인 타입이나 컬렉션이 아닌 Flux나 Mono 타입으로 도메인 객체를 처리한다.
- 새로운 TacoRepository 인터페이스는 다음과 같다.

```java
package tacos.data;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import tacos.Taco;

public interface TacoRepository extends ReactiveMongoRepository<Taco, String> {
	Flux<Taco> findByOrderByCreatedAtDesc();
}
```

- ReactiveCrudRepository에 비해 ReactiveMongoRepository를 사용할 때는 유일한 단점은 몽고DB에 매우 특화되어서 다른 데이터베이스에는 사용할 수 없다는 것이다.
- 따라서 이것을 감안하고 프로젝트에 사용할 가치가 있는지 결정해야 한다.
- 만일 언젠가 다른 데이터베이스로 전환하지 않을 것이라면 ReactiveMongoRepository를 선택하는 것이 데이터 추가의 최적화에 따른 이익을 얻을 수 있다.
- 몽고DB의 경우는 최근 생성된 타코들을 리퍼지터리에서 가져올 수 있다.
- 이름이 특이하지만, findByOrderByCreatedAtDesc() 메서드는 커스텀 쿼리 메서드의 명명 규칙을 따른다.
- 즉, Taco 객체를 찾은 후 createdAt 속성의 값을 기준 내림차순(descending order)으로 결과를 정렬하라는 것을 의미한다.
- findByOrderByCreatedAtDesc()는 `Flux<Taco>`를 반환하므로 결과의 페이징(한 페이지당 반환할 개수만큼만 taco 객체를 가져옴)을 신경 쓰지 않아도 된다.
- 대신에 take() 오퍼레이션을 적용하여 Flux에서 발행되는 처음 12개의 Taco 객체만 반환할 수 있다.

```java
Flux<Taco> recents = repo.findByOrderByCreatedAtDesc()
		.take(12);
```

- 이 경우 결과로 생성되는 Flux는 12개의 Taco 항목만 갖는다.

```java
package tacos.data;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import tacos.Order;

public interface OrderRepository extends ReactiveMongoRepository<Order, String> {
}
```

- Order 문서는 자주 생성될 것이다.
- 따라서 OrderRepository는 insert() 메서드로 제공되는 최적화의 장점을 얻기 위해 ReactiveMongoRepository를 확장한다.
- 마지막으로, User 객체를 문서로 저장하는 리퍼지터리 인터페이스를 살펴보자.

```java
package tacos.data;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;
import tacos.User;

public interface UserRepository extends ReactiveMongoRepository<User, String> {
	Mono<User> findByUsername(String username);
}
```