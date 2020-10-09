- 구성 서버는 애플리케이션의 모든 마이크로서비스에 대해 중앙 집중식의 구성을 제공한다.
- 따라서 구성 서버를 사용하면 애플리케이션의 모든 구성을 한 곳에서 관리할 수 있다.

# 구성 공유하기

- 만일 구성 속성이 런타임 환경을 변경하거나 런타임 환경에 고유한것이어야 한다면, 자바 시스템 속성이나 운영체제의 환경 변수를 구성 속성으로 사용하는 것이 좋다.
- 그러나 값이 변경될 가능성이 거의 없고 애플리케이션에 특정되는 속성의 경우는 애플리케이션 패키지에 포함되어 배포되는 application.yml이나 application.properties 파일에 구성 속성을 지정하는 것이 좋은 선택이다.
- 그러나 속성만 변경하기 위해 애플리케이션을 재배포하거나 재시작한다는 것은 매우 불편하며, 최악의 경우 애플리케이션에 결함이 생길 수도 있기 때문이다.
- 게다가 다수의 배포 인스턴스에서 속성을 관리해야 하는 마이크로서비스 기반 애플리케이션의 경우는 실행 중인 애플리케이션의 모든 서비스 인스턴스에 동일한 변경을 적용하는 것이 불합리하다.
- 스프링 클라우드 구성 서버는 애플리케이션의 모든 마이크로서비스가 구성에 의존할 수 있는 서버를 사용해서 중앙 집중식 구성을 제공한다.
- 따라서 모든 서비스에 공통된 구성은 물론이고, 특정 서비스에 국한된 구성도 한 곳에서 관리할 수 있다.

# 구성 서버 실행하기

- 스프링 클라우드 구성 서버는 집중화된 구성 데이터 소스를 제공한다.
- 구성 서버는 클라이언트가 되는 다른 서비스들의 구성 속성을 사용할 수 있도록 REST API를 제공한다.
- 구성 서버를 통해 제공되는 구성 데이터는 구성 서버의 외부(대개 Git 서버)에 저장된다.
- Git과 같은 소스 코드 제어 시스템에 구성 속성을 사용함으로써 애플리케이션 소스 코드처럼 구성 속성의 버전, 분기 등을 관리할 수 있다.

![chapter14-01](image/chapter14-01.png '스프링 클라우드 구성 서버는 Git이나 Vault(볼트)를 백엔드로 사용해서 구성 속성을 제공한다.')

- 해시코프(HashiCorp)의 Vault는 보안 처리된 구성 속성을 유지·관리할 때 특히 유용하다.

## 구성 서버 활성화하기

- 더 큰 애플리케이션 시스템 내부의 또 다른 마이크로서비스인 구성 서버는 별개의 애플리케이션으로 개발되어 배포된다.
- 따라서 새로운 구성 서버 프로젝트를 생성해야 한다.
- 우선, 새로운 구성 서버 프로젝틀르 생성하자.

```xml
<dependencies>
	...
	<dependency>
		<groupId>org.springframework.cloud</groupId>
		<artifactId>spring-cloud-config-server</artifactId>
	</dependency>
</dependencies>
...
```

```xml
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
...
```

```xml
<properties>
	...
	<spring-cloud.version>Hoxton.SR3</spring-cloud.version>
</properties>
```

- 구성 서버의 스타터 의존성이 지정되었으므로 이제는 구성 서버를 활성화시키면 된다.
- @EnableConfigServer 애노테이션을 추가하자.
- 이 애노테이션은 애플리케이션이 실행될 때 구성 서버를 활성화하여 자동-구성한다.

```java
...
import org.springframework.cloud.config.server.EnableConfigServer;
...
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(ConfigServerApplication.class, args);
	}
}
```

- 애플리케이션을 실행하고 구성 서버가 작동하는 것을 알아보기 전에 한 가지 더 할 것이 있다.
- 구성 서버가 처리할 구성 속성들이 있는 곳(구성 리퍼지터리)을 알려주어야 한다.
- 여기서는 Git 리퍼지터리인 github를 사용할 것이다.
- 따라서 application.yml 파일의 spring.cloud.config.server.git.uri 속성에 github 구성 리퍼지터리의 URL을 설정해야 한다.

```yaml
spring:
	cloud:
		config:
			server:
				git:
					uri: https://github.com/habuma/tacocloud-config
```

- 하지만 로컬에서 애플리케이션을 개발하는 경우에는 추가로 설정할 속성이 있다.
- 로컬에서 서비스를 테스트할 때는 다수의 서비스들이 실행되면서 localhost의 서로 다른 포트를 리스닝하게 된다.
- 그러나 스프링 부트 웹 애플리케이션인 구성 서버는 기본적으로 8080 포트를 리스닝한다.
- 따라서 다른 서비스와의 포트 충돌을 방지하기 위해 server.port 속성의 포트 번호를 고유한 값으로 설정해야 한다.

```yaml
server:
	port: 8888
```

- 조금 전까지 설정한 두 개의 속성은 구성 서버 자체의 구성에 필요한 속성이다.
- 구성 서버가 클라이언트에 제공하는 구성 속성은 Git이나 Vault의 리퍼지터리에서 가져온다는 것을 알아 두자.
    - 여기서는 [https://github.com/habuma/tacocloud-config](https://github.com/habuma/tacocloud-config)
- 다음과 같이 명령행에서 curl 명령을 사용해서 구성 서버의 클라이언트인 것처럼 실행해 볼 수 있다.

```bash
$ curl localhost:8888/application/default
```

- 또는 모든 운영체제의 웹 브라우저에서 [http://localhost:8888/application/default/master](http://localhost:8888/application/default/master) 에 접속하면 결과가 응답할 것이다.

```json
{
	"name": "application",
	"profiles": ["default"],
	"label": "master",
	"version": "551620858a658c9f2696c7f543f1d7effbadaef4",
	"state": null,
	"propertySources": [
		{
			"name": "https://github.com/habuma/tacocloud-config/application.yml",
			"source": {
				"server.port": 0,
				"eureka.client.service-url.defaultZone": "http://localhost:8761/eureka/",
				"spring.data.mongodb.password": "93912a660a7f3c04e811b5df9a3cf6e1f63850cdcd4aa092cf5a3f7e1662fab7"
			}
		}
	]
}
```

![chapter14-02](image/chapter14-02.png '구성 서버는 REST API를 통해서 구성 속성을 제공한다.')

- 경로의 첫 번째 부분인 'application'은 구성 서버에 요청하는 애플리케이션의 이름이다.
- 경로의 두 번째 부분은 요청하는 애플리케이션에 활성화된 스프링 프로파일의 이름이다.
- 경로의 세 번째 부분은 생략 가능하며, 구성 속성을 가져올 백엔드 Git 리퍼지터리의 라벨(label)이나 분기(branch)를 지정한다.
- 만일 지정하지 않으면 'master' 분기가 기본값이 된다.

## Git 리퍼지터리에 구성 속성 저장하기

- 구성 서버가 가져올 속성을 준비하는 방법은 여러 가지가 있다.
- 가장 기본적이고 쉬운 방법은 Git 리퍼지터리의 루트 경로로 application.properties나 application.yml 파일을 커밋하는 것이다.
- 여기서는 localhost의 gogs를 Git 리퍼지터리로 사용한다고 가정한다.
- gogs의 포트 번호는 10080이며, 구성 속성은 [localhost:10080/tacocloud/tacocloud-config](http://localhost:10080/tacocloud/tacocloud-config) 에 저장(push)한다고 하자.

```yaml
server:
	port: 0

eureka:
	client:
		server-url:
			defaultZone: http://eureka1:8761/eureka/
```

- application.yml을 Git 리퍼지터리( [localhost:10080/tacocloud/tacocloud-config](http://localhost:10080/tacocloud/tacocloud-config) )에 저장한 후 다음과 같이 curl 명령을 사용해서(또는 웹 브라우저에서 [http://localhost:8888/someapp/someconfig](http://localhost:8888/someapp/someconfig) 에 접속) 구성 서버의 클라이언트로 실행한다면 구성 서버로부터 응답을 받을 것이다.

```bash
$ curl localhost:8888/someapp/someconfig
```

```json
{
	"name": "someapp",
	"profiles": ["someconfig"],
	"label": "null",
	"version": "95df0cbc3bca106199bd804b27a1de7c3ef5c35e",
	"state": null,
	"propertySources": [
		{
			"name": "http://localhost:10080/tacocloud/tacocloud-config/application.yml",
			"source": {
				"server.port": 0,
				"eureka.client.service-url.defaultZone": "http://localhost:8761/eureka/"
			}
		}
	]
}
```

- propertySources 속성의 배열에는 두 개의 원천 속성이 포함된다.
- name 속성은 localhost의 Git 리퍼지터리를 참조하는 uri 값을 가지며, source 속성은 해당 Git 리퍼지터리에 저장했던 구성 속성을 포함한다.

### Git 하위 경로로 구성 속성 저장하기

- 필요하다면 Git 리퍼지터리의 루트 경로 대신 하위 경로(subpath)에 구성 속성을 저장할 수도 있다.

```yaml
spring:
	cloud:
		config:
			server:
				git:
					uri: http://localhost:10080/tacocloud/tacocloud-config
					search-paths: config
```

- spring.cloud.config.server.git.search-paths 속성은 복수형이다.
- 즉, 구성 서버가 가져오는 구성 속성을 여러 하위 경로에 저장할 수 있다는 의미다.
- 그리고 이때는 다음과 같이 쉼표(,)를 사용해서 각 경로를 구분한다.

```yaml
spring:
	cloud:
		config:
			server:
				git:
					uri: http://localhost:10080/tacocloud/tacocloud-config
					search-paths: config, moreConfig
```

- 또는, 와일드카드 문자인 *를 사용하여 경로를 지정할 수도 있다.

```yaml
spring:
	cloud:
		config:
			server:
				git:
					uri: http://localhost:10080/tacocloud/tacocloud-config
					search-paths: config, more*
```

### Git 리퍼지터리의 분기나 라벨에 구성 속성 저장하고 제공하기

- 기본적으로 구성 서버는 Git 리퍼지터리의 master 분기에서 구성 속성을 가져온다.
- 이때 spring.cloud.config.server.git.default-label 속성을 지정하면 기본 라벨이나 분기가 변경된다.

```yaml
spring:
	cloud:
		config:
			server:
				git:
					uri: http://localhost:10080/tacocloud/tacocloud-config
					default-label: sidework
```

### Git 백엔드를 사용한 인증

- 구성 서버가 읽는 백엔드 Git 리퍼지터리는 사용자 이름과 비밀번호로 인증될 수 있다.
- 이때는 구성 서버 자체의 속성으로 Git 리퍼지터리의 사용자 이름과 비밀번호를 설정해야 한다.
- Git 리퍼지터리의 사용자 이름은 spring.cloud.config.server.git.username 속성으로 설정하며, 비밀번호는 spring.cloud.config.server.git.password 속성으로 설정한다.

```yaml
spring:
	cloud:
		config:
			server:
				git:
					uri: http://localhost:10080/tacocloud/tacocloud-config
					username: tacocloud
					password: s3cr3tP455w0rd
```

# 공유되는 구성 데이터 사용하기

- 중앙 집중식 구성 서버를 제공하는 것에 추가하여, 스프링 클라우드 구성 서버는 클라이언트 라이브러리도 제공한다.
- 이 라이브러리가 스프링 부트 애플리케이션의 빌드에 포함되면 애플리케이션이 구성 서버의 클라이어트가 될 수 있다.

```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```

- 이처럼 의존성을 추가한 후 애플리케이션을 실행하면 자동-구성이 실행되어 구성 서버로부터 속성들을 가져오는 속성 소스(property source, 이름과 값이 한 쌍으로 된 속성들을 관리하는 메커니즘)를 등록한다.
- 기본적으로 자동-구성은 구성 서버가 localhost의 8888 포트에서 실행 중인 것으로 간주한다.
- 그러나 spring.cloud.config.uri 속성을 설정하면 구성 서버의 위치를 알려줄 수 있다.

```yaml
spring:
	cloud:
		config:
			uri: http://config.tacocloud.com:8888
```

- 이 속성은 구성 서버의 클라이언트가 되는 애플리케이션 자체에 설정되어야 한다.
- 애플리케이션이 시작되면 구성 서버 클라이언트가 제공하는 속성 소스가 구성 서버에 속성 값을 요청한 후 받으면 애플리케이션의 환경에서 이 속성들을 사용할 수 있다.
- 게다가 이 속성들은 효과적으로 캐싱되므로 구성 서버의 실행이 중단되더라도 사용할 수 있다.

# 애플리케이션이나 프로파일에 특정된 속성 제공하기

- 애플리케이션의 이름은 spring.application.name 속성을 설정하여 지정한다.
- 그리고 활성  프로파일은 spring.profiles.active 속성을 설정하여 지정할 수 있다.
    - 환경 변수의 경우는 SPRING_PROFILES_ACTIVE

## 애플리케이션에 특정된 속성 제공하기

- 한 애플리케이션의 모든 마이크로서비스들이 공통 구성 속성을 공유할 수 있다는 것이 구성 서버 사용의 장점 중 하나다.
- 그렇지만 하나의 마이크로서비스에만 공유하면서 모든 마이크로서비스가 공유할 필요 없는 속성들이 있을 때가 있다.
- spring.application.name 속성을 사용해서 유레카에 등록하는 마이크로서비스의 이름을 지정하였다.
- 구성 서버에서 구성 클라이언트를 식별할 때도 같은 속성이 사용된다.
- 구성 서버가 해당 애플리케이션에 특정된 구성 데이터를 제공할 수 있게 하기 위함이다.
- 예를 들어, 4개의 마이크로서비스(ingredient-service, order-service, taco-service, user-service)로 분할했던 타코 클라우드 애플리케이션에서는 이 서비스 이름을 각 서비스 애플리케이션의 spring.application.name 속성에 지정할 수 있다.
- 그 다음에 구성 서버에 Git 백엔드에 ingredient-service.yml, order-service.yml, taco-service.yml, user-service.yml이라는 이름의 YAML 구성 파일들을 생성하면 된다.

![chapter14-03](image/chapter14-03.png '애플리케이션에 특정한 구성 파일은 각 애플리케이션의 spring.application.name 속성의 값과 동일한 이름을 갖는다.')

- 애플리케이션 이름과 상관없이 모든 애플리케이션은 application.yml 파일의 구성 속성을 받는다.
- 만일 application.yml의 공통 속성과 애플리케이션에 특정한 구성 파일의 속성이 중복될 때는 애플리케이션에 특화된 속성들이 우선한다.

## 프로파일로부터 속성 제공하기

- 스프링 클라우드 구성 서버는 각 스프링 부트 애플리케이션에 사용했던 것과 똑같은 방법으로 프로파일에 특정된 속성들을 지원한다.
- 그 내역은 다음과 같다.
    - 프로파일에 특정된 .properties 파일이나 YAML 파일들으 제공한다. 예를 들면, application-production.yml이라는 이름의 구성 파일이 해당된다.
    - 하나의 YAML 파일 내부에 여러 개의 프로파일 구성 그룹을 포함한다. 이 경우 3개의 하이픈(-)을 추가하고 그다음에 해당 프로파일의 이름을 나타내는 spring.profiles 속성을 지정한다.
- 구성 서버의 Git 백엔드에 저장했던 기본 application.yml 파일에 추가하여 application-production.yml이라는 이름의 또 다른 YAML 파일을 저장하면 된다.
- 이 파일의 예를 들면 다음과 같다.

```yaml
server:
	port: 8080
eureka:
	client:
		service-url:
			defaultZone: http://eureka1:8761/eureka/, http://eureka2:8761/eureka/
```

- 이 경우 애플리케이션이 구성 서버로부터 구성을 가져올 때 활성 프로파일을 production으로 알려주면 application.yml과 application-production.yml 모두가 반환된다.
- 이때 application.yml의 공통 속성과 application-production.yml의 속성이 중복된 것이 있을 때는 application-production.yml의 속성들이 우선시된다.

![chapter14-04](image/chapter14-04.png '프로파일에 특정된 구성 파일의 이름은 활성 프로파일의 이름과 동일한 접미사를 갖는다.')

- 또한, 같은 명명 규칙을 사용해서 프로파일과 애플리케이션 모두에 특정된 속성들을 지정할 수도 있다.
- 이때는 애플리케이션 이름과 하이픈(-) 및 프로파일 이름의 순서로 구성 파일의 이름을 지정하면 된다.

![chapter14-05](image/chapter14-05.png '구성 파일이 애플리케이션과 프로파일 모두에 특정한 것이 될 수도 있다.')

# 구성 속성들의 보안 유지하기

- 구성 서버가 제공하는 대부분의 구성은 보안이 필요하지 않을 수 있다.
- 그러나 구성 서버에서 민감한 정보(백엔드 리퍼지터리에서 최상위 보안이 유지되는 비밀번호나 보안 토큰 등)를 포함하는 속성들을 제공해야 할 경우가 있다.
- 보안 구성 속성을 사용할 때 구성 서버는 다음 두 가지 옵션을 제공한다.
    - Git 백엔드 리퍼지터리에 저장된 구성 파일에 암호화된 값 쓰기
    - Git 백엔드 리퍼지터리에 추가(또는 대신)하여 구성 서버의 백엔드 저장소로 해시코프의 Vault 사용하기

## Git 백엔드의 속성들 암호화하기

- Git 리퍼지터리에 저장되는 암호화된 데이터를 사용하는 핵심은 암호화 키(encryption key)다.
- 암호화된 속성을 사용하려면 암호화 키를 사용해서 구성 서버를 구성해야 하며, 암호화 키는 속성 값을 클라이언트 애플리케이션에 제공하기 전에 복호화하는데 사용된다.
- 구성 서버는 대칭 키(symmetric key)와 비대칭 키(asymmetric key)를 모두 지원한다.
- 우선, 대칭 키를 설정하려면 구성 서버 자체 구성의 encrypt.key 속성에 암호화 키와 복호화 키와 같이 사용할 값을 설정하면 된다.

```yaml
encrypt:
	key: s3cr3t
```

- 이 속성은 부트스트랩 구성(예를 들어, bootstrap.properties나 bootstrap.yml)에 설정되어야 한다.
- 그래야만 자동-구성이 구성 서버를 활성화시키기 전에 로드되어 사용할 수 있기 때문이다.
- 더 강력한 보안을 위해서는 구성 서버가 한 쌍의 비대칭 RSA 키나 키스토어(keystore)의 참조를 사용하도록 구성할 수 있다.
- 이때는 다음과 같이 keytool 명령행 도구를 사용하여 키를 생성할 수 있다.

```bash
keytool -genkeypair -alias tacokey -keyalg RSA \
-dname "CN=Web Server,OU=Organization,L=City,S=State,C=US" \
-keypass s3cr3t -keystore keystore.jks -storepass l3tm31n
```

- 결과로 생성되는 키스토어는 keystore.jks라는 이름의 파일로 저장되며, 파일 시스템의 키스토어 파일로 유지하거나 애플리케이션 자체에 둘 수 있다.
- 그리고 둘 중 어떤 경우든 해당 키스토어의 위치와 인증 정보를 구성 서버의 bootstrap.yml 파일에 구성해야 한다.
    - 구성 서버에서 암호화를 사용하려면 Java Cryptography Extensions Unlimited Strength Policy 파일을 설치해야 한다.

```yaml
encrypt:
	key-store:
		alias: tacokey
		location: classpath:/keystore.jks
		password: l3tm31n
		secret: s3cr3t
```

- 이처럼 키나 키스토어가 준비된 후에는 데이터를 암호화해야 한다.
- 구성 서버는 /encrypt 엔드포인트를 제공한다.
- 따라서 암호화될 데이터를 갖는 POST 요청을 /encrypt 엔드포인트에 하면 된다.

```bash
$ curl localhost:8888/encrypt -d "s3cr3tP455w0rd"
93912a660a7f3c04e811b5df9a3cf6e1f63850cdcd4aa092cf5a3f7e1662fab7
```

- POST 요청이 제출된 후에는 암호화된 값을 응답으로 받는다.
- 그다음에 이 값을 복사하여 Git 리퍼지터리에 저장된 구성 파일에 붙여넣기하면 된다.
- 몽고DB의 비밀번호를 설정할 때는 Git 리퍼지터리에 저장된 application.yml 파일에 spring.data.mongodb.password 속성을 추가한다.

```yaml
spring:
	data:
		mongodb:
			password: '{cipher}93912a660a7f3c04e811b5df9a3cf6e1f63850...'
```

- spring.data.mongodb.password에 지정된 값이 작은 따옴포(')로 둘러싸여 있고 맨 앞에 {cipher}가 붙어 있다는 것에 유의하자.
- 이것은 해당 값이 암호화된 값이라는 것을 구성 서버에 알려주는 것이다.

```bash
$ curl localhost:8888/application/default | jq
{
	"name": "app",
	"profiles": [
		"prof"
	],
	"label": null,
	"version": "464adfd43485182e4e0af08c2aaaa64d2f78c4cf",
	"state": null,
	"propertySources": [
		{
			"name": "http://localhost:10080/tacocloud/tacocloudconfig/application.yml",
			"source": {
				"spring.data.mongodb.password": "s3cr3tP455w0rd"
			}
		}
	]
}
```

- 이 결과를 보면 알 수 있듯이, 구성 서버는 spring.data.mongodb.password 속성의 값을 복호화된 형태로 제공한다.
- 기본적으로 구성 서버가 제공하는 암호화된 값은 백엔드 Git 리퍼지터리에 저장되어 있을 때만 암호화되어 있으며, 구성 서버에 의해 복호화된 후에 제공된다.
- 만일 구성 서버가 암호화된 속성의 값을 있는 그대로(복호화하지 않고) 제공하기 원한다면 spring.cloud.config.server.encrypt.enabled 속성을 false로 설정하면 된다.

```yaml
spring:
	cloud:
		config:
			server:
				git:
					uri: http://localhost:10080/tacocloud/tacocloud-config
				encrypt:
					enabled: false
```

```bash
$ curl localhost:8888/application/default | jq
{
	...,
	"propertySources": [
		{
			"name": "http://localhost:10080/tacocloud/tacocloudconfig/application.yml",
			"source": {
				"spring.data.mongodb.password": "{cipher}AQA4JeVhf2cRXW..."
			}
		}
	]
}
```

- 당연한 얘기지만, 이때는 클라이언트에서 암호화된 속성 값을 받으므로 클라이언트가 복호화를 해야 한다.

## Vault에 보안 속성 저장하기

- 해시코프의 Vault는 보안 관리 도구다.
- 이것은 Git 서버와 다르게 Vault가 보안 정보를 자체적으로 처리한다는 의미다.
- 따라서 보안에 민감한 구성 데이터의  경우에 구성 서버의 백엔드로 Vault가 훨씬 더 매력적인 선택이 된다.

### Vault 서버 시작시키기

- 구성 서버로 보안 속성을 저장하고 제공하기에 앞서 우선 Vault 서버를 시작시켜야 한다.
- 여기서는 다음과 같이 개발 모드로 시작시킨다.

```bash
$ vault server -dev -dev-root-token-id=roottoken
$ export VAULT_ADDR='http://127.0.0.1:8200'
$ vault status
```

- Vault 서버를 사용하려면 토큰을 제공해야 한다.
- 특히 루트 토큰은 관리용 토큰이며, 더 많은 토큰을 생성할 수 있게 한다.
- 또한, 루트 토큰은 보안 정보를 읽거나 쓰는 데도 사용할 수 있다.
- 만일 개발 모드로 Vault 서버를 시작시킬 때 루트 토큰을 지정하지 않으면 Vault가 자동으로 하나를 생성하고 로그에 기록한다.
- Vault 0.10.0 이상 버전에는 구성 서버와 연계되도록 Vault를  사전 준비시키기 위해 수행해야 할 명령들이 있다.
    - 이중 일부는 구성 서버와 호환되지 않는다.

```bash
$ vault secrets disable secret
$ vault secrets enable -path=secret kv
```

- 구버전의 Vault를 사용할 때는 이렇게 할 필요가 없다.

### 보안 데이터를 VAULT에 쓰기

- vault 명령은 보안 데이터를 Vault 서버에 쓰기 쉽게 해준다.
- 예를 들어, spring.data.mongodb.password 속성으로 몽고DB의 비밀번호를 Vault 서버에 저장하고 싶다고 하자.

```bash
$ vault write secret/application spring.data.mongodb.password=s3cr3t
```

![chapter14-06](image/chapter14-06.png 'vault 명령으로 보안 데이터를 Vault 서버에 쓰기')

- 지금 여기서 가장 중요한 부분은 보안 데이터 경로, 보안 키, 보안 처리될 값이다.
- 보안 데이터 경로 앞의 secret/는 Vault 백엔드 서버를 나타내며, 여기서는 이름이 'secret'이다.
- 저장된 구성 데이터는 vault read 명령을 사용해서 확인할 수 있다.

```bash
$ vault read secret/application
Key                               Value
---                               -----
refresh_interval                  768h
spring.data.mongodb.password      s3cr3t
```

- 지정된 경로에 보안 데이터를 쓸 때는 이전에 해당 경로에 썼던 데이터를 덮어쓰기한다는 것을 알아두자.
- 다음과 같이 두 속성 모두를 같이 써야 한다.

```bash
$ vault write secret/application \
              spring.data.mongodb.password=s3cr3t \
              spring.data.mongodb.username=tacocloud
```

### 구성 서버에서 Vault 백엔드 활성화하기

- 구성 서버의 백엔드로 Vault 서버를 추가할 때 최소한으로 해야 할 것이 활성화 프로파일로 vault를 추가하는 것이다.

```yaml
spring:
	profiles:
		active:
		- vault
		- git
```

- 여기서는 vault와 git 프로파일 모두 활성화하였다.
- 따라서 구성 서버가 Vault와 Git 모두의 구성을 제공할 수 있다.
- 이 경우 보안에 민감한 구성 속성은 Vault에만 쓰고 그렇지 않은 구성 속성은 Git 백엔드를 계속 사용하면 된다.
- 기본적으로 구성 서버는 Vault 서버가 localhost에서 실행되면서 8200 포트를 리스닝한다고 간주한다.

```yaml
spring:
	cloud:
		config:
			server:
				git:
					uri: http://localhost:10080/tacocloud/tacocloud-config
					order: 2
				vault:
					host: vault.tacocloud.com
					port: 8200
					schema: https
					order: 1
```

- spring.cloud.config.server.vault의 속성들은 구성 서버의 Vault에 대한 기본값을 변경할 수 있게 한다.
- order 속성은 Vault가 제공하는 구성 속성이 Git이 제공하는 구성 속성보다 우선한다는 것을 나타낸다.
- 구성 서버가 Vault 백엔드를 사용하도록 구성한 후에는 다음과 같이 curl을 클라이언트로 사용해서 확인할 수 있다.

```bash
[habuma:habuma]$ curl localhost:8888/application/default | jq
{
	"timestamp": "2018-04-29T23:33:22.275+0000",
	"status": 400,
	"error": "Bad Request",
	"message": "Missing required header: X-Config-Token",
	"path": "/application/default"
}
```

- 이것은 구성 서버가 Vault로부터 보안 구성 속성을 제공한다는 것을 나타낸다.
- 그러나 요청 시에 Vault 토큰이 포함되지 않아서 에러가 발생한 것이다.
- Vault에 대한 모든 요청은 X-Vault-Token 헤더를 포함해야 한다.
- 이때 구성 서버 자체에 해당 토큰을 구성하는 대신, 구성 서버 클라이언트가 구성 서버에 대한 모든 요청에 X-Vault-Token 헤더에 토큰을 포함시켜야 한다.
- 그러면 구성 서버는 X-Vault-Token 헤더로 토큰을 받은 후 이 토큰을 Vault로 전송하는 요청의 X-Vault-Token 헤더로 복사한다.
- 이처럼 Vault를 Git과 함께 사용할 때는 부작용이 생길 수 있다.
- 즉, 토큰이 필요없는 Git에 저장된 속성일지라도 Vault에 적합한 토큰이 요청에 포함되지 않으면 구성 서버가 거부하기 때문이다.

```bash
$ curl localhost:8888/application/default -H"X-Config-Token: roottoken" | jq
```

### 구성 서버 클라이언트에 Vault 토큰 설정하기

```yaml
spring:
	cloud:
		config:
			token: roottoken
```

- spring.cloud.config.token 속성은 지정된 토큰 값을 구성 서버에 대한 모든 요청에 포함하라고 구성 서버 클라이언트에 알려준다.
- 이 속성은 구성 서버에 Git이나 Vault 백엔드에 저장되지 않고 애플리케이션의 로컬 구성에 설정되어야 한다.

### 애플리케이션과 프로파일에 특정된 보안 속성 쓰기

- application 경로에 저장되는 보안 속성은 구성 서버가 이름과 상관없이 모든 애플리케이션에 제공한다.
- 그러나 만일 지정된 애플리케이션에 특정된 보안 속성을 저장해야 한다면, 요청 경로의 application 부분을 해당 애플리케이션 이름으로 교체하면 된다.

```bash
$ vault write secret/ingredient-service spring.data.mongodb.password=s3cr3t
```

- 이와 유사하게, 프로파일을 지정하지 않으면 Vault에 저장된 보안 속성은 기본 프로파일의 일부로 제공된다.
- 즉, 클라이언트는 자신의 활성화 프로파일이 무엇이건 관계없이 해당 속성을 받는다.
- 그러나 다음과 같이 특정 프로파일에 관련된 보안 속성을 쓸 수 있다.

```bash
$ vault write secret/application,production \
              spring.data.mongodb.password=s3cr3t \
              spring.data.mongodb.username=tacocloud
```

# 실시간으로 구성 속성 리프레시하기

- 스프링 클라우드 구성 서버는 실행 중인 애플리케이션을 중단시키지 않고 구성 속성을 리프레시(refresh)하는 기능을 제공한다.
- 즉, 백엔드 Git 리퍼지터리나 Vault 보안 서버에 변경 데이터가 푸시되면 애플리케이션의 각 마이크로서비스는 새로운 구성으로 즉시 리프레시된다.
- 이때 다음 중 한 가지 방법을 사용한다.
    - **수동식** : 구성 서버 클라이언트는 /actuator/refresh의 특별한 액추에이터(Actuator) 엔드포인트를 활성화한다. 그리고 각 서비스에서 이 엔드포인트로 HTTP POST 요청을 하면 구성 클라이언트가 가장 최근의 구성 백엔드로부터 가져온다.
    - **자동식** : 리퍼지터리의 커밋 후크(commit hook)가 모든 서비스의 리프레시를 촉발할 수 있다. 이때는 구성 서버와 이것의 클라이언트 간의 통신을 위해 스프링 클라우드 버스(Spring Cloud Bus)라는 스프링 클라우드 프로젝트가 개입한다.
- 수동식 리프레시는 서비스가 리프레시되는 구성으로 업데이트 시점을 더 정확하게 제어할 수 있다.
- 그러나 마이크로서비스의 인스턴스에 대해 개별적인 HTTP 요청이 수행되어야 한다.
- 반면에 자동식 리프레시는 애플리케이션의 모든 마이크로서비스에 대해 즉시로 변경 구성을 적용한다.
- 그러나 이것은 구성 리퍼지터리에 커밋을 할 때 수행되므로 프로젝트에 따라서는 큰 부담이 될 수 있다.

## 구성 속성을 수동으로 리프레시하기

- 구성 서버의 클라이언트로 애플리케이션을 활성화하면, 구성 속성들을 리프레시하기 위해 자동-구성이 액추에이터 엔드포인트를 구성한다.
- 이 엔드포인트를 사용하려면 구성 클라이언트 의존성과 함께 엑추에이터 스타터 의존성을 프로젝트의 빌드에 포함해야 한다.

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

- 이제는 실행 중인 구성 클라이언트 애플리케이션에 액추에이터가 활성화되므로, /actuator/refresh에 대한 HTTP POST 요청을 제출하여 언제든 우리가 원할 때 백엔드 리퍼지터리로부터 구성 속성을 리프레시할 수 있다.
- 실제로 잘 되는지 알아보기 위해 @ConfigurationProperties 에노테이션이 지정된 GreetingProps라는 이름의 클래스가 있다고 하자.

```java
@ConfigurationProperties(prefix="greeting")
@Component
public class GreetingProps {
	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
```

- 여기에 추가하여 GreetingProps가 주입되고 GET 요청을 처리할 때 message 속성의 값을 반환하는 다음의 컨트롤러 클래스도 있다.

```java
@RestController
public class GreetingController {
	private final GreetingProps props;

	public GreetingController(GreetingProps props) {
		this.props = props;
	}

	@GetMapping("/hello")
	public String message() {
		return props.getMessage();
	}
}
```

- 이 두 클래스를 갖는 애플리케이션의 이름을 hello-world라고 하자.
- 그리고 Git 리퍼지터리에는 다음 속성들이 정의된 application.yml 파일이 있다고 하자.

```yaml
greeting:
	message: Hello World!
```

- 이제는 구성 서버와 간단한 구성 클라이언트 애플리케이션이 준비되었으므로 실행한 후에 curl을 사용해서 /hello에 대한 HTTP GET 요청을 하면 'Hello World!'라는 응답이 출력된다.

```bash
$ curl localhost:8080/hello
Hello World!
```

- 구성 서버 Git 리퍼지터리의 application.yml 파일에 있는 greeting.message 속성을 다음과 같이 변경한 후 Git 리퍼지터리에 푸시한다고 하자.

```yaml
greeting:
	message: Hiya folks!
```

- 다음과 같이 hello-world 애플리케이션 서버의 액추에이터 리프레시 엔드포인트로 POST 요청을 하면 greeting.message 속성이 리프레시 된다.

```bash
$ curl localhost:8080/actuator/refresh -X POST
```

- 그리고 다음의 응답을 반환한다.
    - 리프레시할 속성이 없는 경우는 비어 있는 []만 응답에 나타난다.

```bash
["config.client.version", "greeting.message"]
```

- 이 응답을 보면 변경된 속성 이름을 저장한 JSON 배열이 포함되어 있고 이 배열에는 greeting.message 속성이 포함된 것을 알 수 있다.
- 그리고 또한 config.client.version 속성도 포함되어 있다.
- 이 속성은 현재의 구성이 생성된 Git 커밋의 해시 값을 갖는다.

```bash
$ curl localhost:8080/hello
Hiya folks!
```

- /actuator/refresh 엔드포인트는 구성 속성의 변경이 생기는 시점을 완전하게 제어하기 원할 때 아주 좋다.
- 그러나 만일 우리의 애플리케이션이 다수의 마이크로서비스(그리고 각 서비스의 다수 인스턴스)로 구성된다면 그것들 모두의 구성을 리프레시하는 것은 매우 번거로운 일이될 것이다.

## 구성 속성을 자동으로 리프레시하기

- 한 애플리케이션의 모든 구성 서버 클라이언트들의 속성을 수동으로 리프레시하는 방법의 대안으로 구성 서버는 모든 클라이언트에게 자동으로 구성 변경을 알려줄 수 있다.
- 이때 또다른 스프링 클라우드 프로젝트인 스프링 클라우드 버스를 사용한다.

![chapter14-07](image/chapter14-07.png '스프링 클라우드 버스를 함께 사용하여 구성 서버는 각 애플리케이션에 변경 사항을 전파할 수 있다. 따라서 Git 리퍼지터리의 속성들이 변경될 때 각 애플리케이션의 속성들이 자동으로 리프레시될 수 있다.')

- 속성 리프레시 절차는 다음과 같이 요약할 수 있다.
    - 웹훅(webhook)이 Git 리퍼지터리에 생성되어 Git 리퍼지터리에 대한 변경(예를 들어, 푸시된 것)이 생겼음을 구성 서버에 알려준다. 웹훅은 GitHub, GitLab, Bitbucket, Gogs를 비롯한 많은 리퍼지터리에서 지원한다.
    - 구성 서버는 RabbitMQ나 카프카(Kafka)와 같은 메시지 브로커(broker)를 통하여 변경 관련 메시지를 전파함으로써 웹훅의 POST 요청에 반응한다.
    - 알림(notification)을 구독하는 구성 서버 클라이언트 애플리케이션은 구성 서버로부터 받은 새로운 속성 값으로 자신의 속성을 리프레시하여 알림 메시지에 반응한다.
- 따라서 모든 구성 서버 클라이언트 애플리케이션은 변경 속성이 백엔드 Git 리퍼지터리에 푸시되는 즉시 구성 서버로부터 받은 최신의 구성 속성 값을 갖는다.
- 구성 서버를 통해 속성의 자동 리프레시를 사용할 때는 몇 가지 고려할 사항이 있다.
    - 구성 서버와 이것의 클라이언트 간의 메시지 처리에 사용할 수 있는 메시지 브로커가 있어야 하며, RabbitMQ나 카프카 중 하나를 선택할 수 있다.
    - 구성 서버에 변경을 알려주기 위해 웹훅이 백엔드 Git 리퍼지터리에 생성되어야 한다.
    - 구성 서버는 구성 서버 모니터 의존성(Git 리퍼지터리로부터의 웹훅 요청을 처리하는 엔드포인트를 제공함) 및 RabbitMQ나 카프카 스프링 클라우드 스트림 의존성(속성 변경 메시지를 브로커에게 전송하기 위해서)과 함께 활성화되어야 한다.
    - 메시지 브로커가 기본 설정으로 로컬에서 실행되는 것이 아니라면, 브로커에 연결하기 위한 세부 정보를 구성 서버와 이것의 모든 클라이언테 구성해야 한다.
    - 각 구성 서버 클라이언트 애플리케이션에 스프링 클라우드 버스 의존성이 추가되어야 한다.
- 여기서는 메시지 브로커(RabbitMQ나 카프카)가 이미 실행 중이면서 속성 변경 메시지를 전달할 준비가 된 것으로 간주한다.

### 웹훅 생성하기

- 많은 종류의 Git 서버들이 Git 리퍼지터리의 푸시를 비롯한 변경을 애플리케이션에 알리기 위해 웹훅의 생성을 지원한다.
- Gogs는 로컬에서 실행하면서 로컬로 실행하는 애플리케이션에 대해 POST 요청을 쉽게 하는 웹훅을 가지므로(GitHub로는 어렵다) 선택한 것이다.
- 또한, Gogs로 웹훅을 설정하는 절차는 GitHub와 거의 동일하므로 Gogs의 절차를 설명하면 GitHub의 웹훅 설정에 필요한 과정 파악에 도움이 된다.
- 우선, 웹 브라우저에서 우리의 구성 리퍼지터리에 접속하고(localhost:10080/tacocloud/tacocloudconfig) **Settings** 링크를 클릭하자.

![chapter14-08](image/chapter14-08.png 'Gogs나 GitHub의 Settings 버튼을 클릭하여 웹훅 생성 시작하기')

- 메뉴의 **Webhooks**를 클릭하면 오른쪽에 **Add Webhooks** 버튼이 보일 것이다.
- Gogs의 경우 이 버튼을 클릭하면 서로 다은 타입의 웹훅을 선택하는 드롭다운 리스트가 나타난다.

![chapter14-09](image/chapter14-09.png 'Webhooks 메뉴의 Add a new webhook 버튼과 웹훅 타입 선택 드롭다운 리스트')

![chapter14-10](image/chapter14-10.png '웹훅을 생성하기 위해 구성 서버의 /monitor URL과 json 콘텐트 타입을 지정한다.')

- 이 폼에는 여러 필그다 있지만, 가장 중요한 것이 페이로드 URL(Payload URL)과 콘텐트 타입(Content Type)이다.
- 구성 서버는 /monitor 경로에 대한 웹훅의 POST 요청을 처리할 수 있어야 한다.
- 따라서 페이로드 URL 필드에는 구성 서버의 /monitor 엔드포인트를 참조하는 URL을 입력해야 한다.
- 필자는 도커(Docker) 컨테이너로 Gogs를 실행 중이므로 페이로드 URL 필드에는 [http://host.docker.internal:8888/monitor](http://host.docker.internal:8888/monitor) 를 지정하였다.
- 여기서 host.docker.internal은 도커 컨테이너의 호스트 이름이며, Gogs 서버가 컨테이너의 경계를 지나서 호스트 컴퓨터에서 실행 중인 구성 서버를 알 수 있게 한다.
    - 도커 컨테이너에서는 'localhost'가 도커 호스트가 아닌 컨테이너 자신을 의미한다.
- 여기서는 콘텐트 타입 필드를 application/json으로 지정하였다.
- 이것은 중요하다.
- 왜냐하면 구성 서버의 /monitor 엔드포인트가 콘텐트 타입의 다른 선택 항목인 application/x-www-formulencoded를 지원하지 않기 때문이다.
- 만일 보안(Secret) 필드의 값이 설정되면 웹훅 POST 요청에 X-Gogs-Signature(GitHub의 경우 X-Hub-Signature)라는 이름의 헤더가 포함된다.
- 이 헤더는 HMAC-SHA256 다이제스트(GitHub의 경우는 HMAC-SHA1)를 포함한다.
- 끝으로, 구성 리퍼지터리에 대한 푸시 요청에서만 웹훅이 작동하도록 **Just the Push Event** 라디오 버튼을 선택하고, 웹훅을 활성화하도록 **Active** 체크박스도 선택한다.
- 그리고 폼의 제일 끝에 있는 **Add Webhook** 버튼을 클릭하면 웹훅이 생성되며, 이후로 리퍼지터리에 푸시가 발생할 때마다 구성 서버에 POST 요청을 전송한다.

### 구성 서버에서 웹훅 처리하기

- 구성 서버의 /monitor 엔드포인트를 활성화하는 것은 간단하다.
- 스프링 클라우드 구성 모니터 의존성만 구성 서버의 프로젝트 빌드에 추가하면 된다.

```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-config-monitor</artifactId>
</dependency>
```

- 이처럼 의존성이 추가되면 자동-구성이 /monitor 엔드포인트를 활성화한다.
- 그러나 구성 서버가 변경 알림을 전파하는 수단도 가져야 하므로 스프링 클라우드 스트림 의존성도 추가해야 한다.
- 스프링 클라우드 스트림은 또 다른 스프링 클라우드 프로젝트 중 하나이며, RabbitMQ나 카프카를 통해 통신하는 서비스들을 생성할 수 있다.
- 이 서비스들은 스트림으로부터 처리할 데이터를 받으며, 하위 스트림 서비스가 처리하도록 스트림으로 데이터를 반환한다.
- /monitor 엔드포인트는 스프링 클라우드 스트림을 사용해서 구성 서버 클라이언트에 알림 메시지를 전송한다.
- 만일 RabbitMQ를 사용하고 있다면, 스프링 클라우드 스트림 RabbitMQ 바인딩 의존성을 구성 서버의 빌드에 포함시켜야 한다.

```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-stream-rabbit</artifactId>
</dependency>
```

- 이와는 달리 카프카를 사용한다면, 다음의 스프링 클라우드 스트림 카프카 의존성을 추가해야 한다.

```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-stream-kafka</artifactId>
</dependency>
```

- 이처럼 필요한 의존성이 추가되면 구성 서버가 속성의 자동 리프레시에 참여할 준비가 된 것이다.
- 그러나 메시지 브로커가 localhost가 아닌 다른 곳에서 기본 포트가 아닌 다른 포트로 실행 중이거나 해당 브로커가 접근하기 위한 인증 정보를 변경했다면, 구성 서버 자체의 구성에 있는 몇 가지 속성을 설정해야 한다.
- RabbitMQ의 바인딩의 경우는 application.yml의 다음 속성이 기본값을 변경하기 위해 사용될 수 있다.

```yaml
spring:
	rabbitmq:
		host: rabbit.tacocloud.com
		port: 5672
		username: tacocloud
		password: s3cr3t
```

- 카프카의 경우도 이와 유사한 속성들을 사용할 수 있다.

```yaml
spring:
	kafka:
		bootstrap-servers:
		- kafka.tacocloud.com:9092
		- kafka.tacocloud.com:9093
		- kafka.tacocloud.com:9094
```

### Gogs 알림 추출기 생성하기

- 서로 다른 종류의 Git 서버마다 웹훅의 POST 요청을 처리하는 방법이 다르다.
- 따라서 웹훅의 POST 요청을 처리할 때 서로 다른 데이터 형식을 /monitor 엔드포인트가 알 수 있어야 한다.
- 기본적으로 구성 서버에는 GitHub, GitLab, Bitbucket 등의 Git 서버 지원 기능이 포함되어 있다.
- 따라서 이것들 중 하나를 사용한다면 특별히 필요한 게 없다.
- 그러나 이 책을 집필하는 시점에 Gogs는 아직 공식적으로 지원되지 않았다.
- 따라서 만일 Gogs를 Git 서버로 사용한다면 Gogs에만 특정된 알림 추출기(notification extractor) 클래스를 프로젝트 빌드에 포함시켜야 한다.

```java
package tacos.gogs;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.cloud.config.monitor.PropertyPathNotification;
import org.springframework.cloud.config.monitor.PropertyPathNotificationExtractor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

@Component
@Order(Ordered.LOWEST_PRECEDENCE - 300)
public class GogsPropertyPathNotificationExtractor implements PropertyPathNotificationExtractor {
	@Override
	public PropertyPathNotification extract(MultiValueMap<String, String> headers,
			Map<String, Object> request) {
		if ("push".equals(headers.getFirst("X-Gogs-Event"))) {
			if (request.get("commits") instanceof Collection) {
				Set<String> paths = new HashSet<>();
				@SuppressWarnings("unchecked")
				Collection<Map<String, Object>> commits = (Collection<Map<String, Object>>) request.get("commits");
				for (Map<String, Object> commit : commit) {
					addAllPaths(paths, commit, "added");
					addAllPaths(paths, commit, "removed");
					addAllPaths(paths, commit, "modified");
				}
				if (!paths.isEmpty()) {
					return new PropertyPathNotification(paths.toArray(new String[0]));
				}
			}
		}
		return null;
	}

	private void addAllPaths(Set<String> paths, Map<String, Object> commit, String name) {
		@SuppressWarnings("unchecked")
		Collection<String> files = (Collection<String>) commit.get(name);
		if (files != null) {
			paths.addAll(files);
		}
	}
}
```

- 여기서는 GogsPropertyPathNotificationExtractor의 작동 방법에 관한 자세한 내용은 중요하지 않다.
- 또한, 향후에 스프링 클라우드 구성 서버에 Gogs 지원이 기본적으로 추가되면 필요 없게 될 것이다.

### 구성 서버 클라이언트에 속성의 자동 리프레시 활성화하기

- 구성 서버 클라이언트에 속성의 자동 리프레시를 활성화하는 것은 구성 서버 자체에 하는 것보다 훨씬 쉽다.
- 하나의 의존성만 추가하면 되기 때문이다.

```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-bus-amqp</artifactId>
</dependency>
```

- 이것은 AMQP(예를 들어, RabbitMQ) 스프링 클라우드 버스 스타터를 빌드에 추가한다.
- 만일 카프카를 사용 중이라면 다음의 의존성을 추가해야 한다.

```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-bus-kafka</artifactId>
</dependency>
```