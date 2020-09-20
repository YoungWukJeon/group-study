- 하나의 완전한 애플리케이션 기능을 제공하기 위해 함께 동작하는 작고 독립적인 애플리케이션인 **마이크로서비스**(microservice)를 개발하고 등록 및 사용하는 방법을 알게 될 것이다.
- 또한, 스프링 클라우드의 가장 유용한 컴포넌트인 유레카(Eureka)와 리본(Ribbon)도 알아볼 것이다.

# 마이크로서비스 이해하기

- 이때까지는 타코 클라우드 애플리케이션을 단일 애플리케이션 즉, 배포 가능한 하나의 JAR나 WAR 파일로 개발하였다.
- 이것은 작고 간단한 애플리케이션을 개발할 때 좋은 방법이다.
- 그러나 작은 애플리케이션이 점점 더 커지게 된다는 것이 문제다.
- 결국 새로운 기능이 필요할 때마다 더 많은 코드가 추가되어야 하므로 주체하기 어렵고 복잡한 단일 애플리케이션이 된다.
- 단일 애플리케이션은 언뜻 보기엔 간단하다.
- 그러나 다음과 같은 문제가 따른다.
    - **전체를 파악하기 어렵다** : 코드가 점점 더 많아질수록 애플리케이션에 있는 각 컴포넌트의 역할을 알기 어려워진다.
    - **테스트가 더 어렵다** : 애플리케이션이 커지면서 통합과 테스트가 더 복잡해진다.
    - **라이브러리 간의  충돌이 생기기 쉽다** : 애플리케이션의 한 기능에서 필요한 라이브러리 의존성이 다른 기능에서 필요한 라이브러리 의존성과 호환되지 않을 수 있다.
    - **확장 시에 비효율적이다** : 시스템 확장을 목적으로 더 많은 서버에 애플리케이션을 배포해야 할 때는 애플리케이션의 일부가 아닌 전체를 배포해야 한다. 애플리케이션 기능의 일부만 확장하더라도 마찬가지다.
    - **적용할 테크놀러지를 결정할 때도 애플리케이션 전체를 고려해야 한다** : 애플리케이션에 사용할 프로그래밍 언어, 런타임 플랫폼, 프레임워크, 라이브러리를 선택할 때 애플리케이션 전체를 고려하여 선택해야 한다.
    - **프로덕션으로 이양하기 위해 많은 노력이 필요하다** : 애플리케이션을 한 덩어리로 배포하므로 프로덕션으로 이양하는 것이 더 쉬운 것처럼 보일 수 있다. 그러나 일반적으로 단일 애플리케이션은 크기와 복잡도 때문에 더 엄격한 개발 프로세스와 더욱 철두철미한 테스트가 필요하다. 고품질과 무결함을 보장하기 위해서다.
- 마이크로서비스 아키텍처는 개별적으로 개발되는 소규모의 작은 애플리케이션들로 애플리케이션을 만드는 방법이다.
- 마이크로서비스는 상호 협력하여 더 큰 애플리케이션의 기능을 제공한다.
- 단일 애플리케이션 아키텍처와는 대조적으로 마이크로서비스 아키텍처는 다음과 같은 특성을 갖는다.
    - **마이크로서비스는 쉽게 이해할 수 있다** : 다른 마이크로서비스와 협력할 때 각 마이크로서비스는 작으면서 한정된 처리를 수행한다. 따라서 마이크로서비스는 자신의 목적에만 집중하므로 더 이해하기 쉽다.
    - **마이크로서비스는 테스트가 쉽다** : 크기가 작을수록 테스트가 쉬워지는 것은 분명한 사실이다. 마이크로서비스 테스트도 이와 마찬가지다.
    - **마이크로서비스는 라이브러리 비호환성 문제가 생기지 않는다** : 각 마이크로서비스는 다른 마이크로서비스와 공유되지 않는 빌드 의존성을 가지므로 라이브러리 충돌 문제가 생기지 않는다.
    - **마이크로서비스는 독자적으로 규모를 조정할 수 있다** : 만일 특정 마이크로서비스의 규모가 더 커야 한다면, 애플리케이션의 다른 마이크로서비스에 영향을 주지 않고 메모리 할당이나 인스턴스의 수를 더 크게 조정할 수 있다.
    - **각 마이크로서비스에 적용할 테크놀러지를 다르게 선택할 수 있다** : 각 마이크로서비스에 사용할 프로그래밍 언어, 플랫폼, 프레임워크, 라이브러리를 서로 다르게 선택할 수 있다. 실제로 자바로 개발된 마이크로서비스가 C#으로 개발된 다른 마이크로서비스와 함께 동작하도록 할 수 있다.
    - **마이크로서비스는 언제든 프로덕션으로 이양할 수 있다** : 마이크로서비스 아키텍처 기반으로 개발된 애플리케이션이 여러 개의 마이크로서비스로 구성되었더라도 각 마이크로서비스를 따로 배포 할 수 있다. 그리고 마이크로서비스는 작으면서 특정 목적에만 집중되어 있고 테스트하기 쉬우므로, 마이크로서비스를 프로덕션으로 이양하는 데 따른 노력이 거의 들지 않는다. 또한, 프로덕션으로 이양하는 데 필요한 시간도 수개월이나 수주 대신 수시간이나 수분이면 된다.
- 마이크로서비스가 일을 더 쉽게 해주는 것은 분명하다.
- 그러나 마이크로서비스 아키텍처는 그냥 되는 것이 아니다.
- 마이크로서비스 아키텍처는 분산 아키텍처이므로 네트워크 지연과 같은 문제들이 발생할 수 있다.
- 마이크로서비스로의 원격 호출이 많이 추가될수록 애플리케이션의 실행이 더 느려질 수 있다.
- 또한, 우리 애플리케이션을 마이크로서비스 아키텍처로 개발하는 것이 타당한지도 고려해야 한다.
- 마이크로서비스 아키텍처에서는 각 마이크로서비스가 자신과 같이 동작하는 다른 마이크로서비스를 어떻게 찾느냐가 중요하다.

# 서비스 레지스트리 설정하기

- 스프링 클라우드는 큰 프로젝트이며, 마이크로서비스 개발을 하는 데 필요한 여러 개의 부속 프로젝트로 구성된다.
- 이중 하나가 스프링 넷플릭스이며, 이것은 넷플릭스 오픈 소스로부터 다수의 컴포넌트를 제공한다.
- 이 컴포넌트 중에 넷플릭스 서비스 레지스트리인 유레가(Eureka)가 있다.

### 유레카란?

- 유레카는 마이크로서비스 애플리케이션에 있는 모든 서비스의 중앙 집중 레지스트리로 작동한다.
- 유레카 자체도 마이크로서비스로 생각할 수 있으며, 더 큰 애플리케이션에서 서로 다른 서비스들이 서로를 찾는 데 도움을 주는 것이 목적이다.
- 이러한 유레카의 역할 때문에 서비스를 등록하는 유레카 서비스 레지스트리를 가장 먼저 설정하는 것이 좋다.
- 서비스 인스턴스가 시작될 때 해당 서비스는 자신의 이름을 유레카에 등록한다.
- 그림에서는 some-service가 서비스 이름이다.
- some-service의 인스턴스는 여러 개 생성될 수 있다.
- 그러나 이것들 모두 같은 이름으로 유레카에 등록된다.

![chapter13-01](image/chapter13-01.png '다른 서비스가 찾아서 사용할 수 있도록 각 서비스는 유레카 서비스 레지스트리에 자신을 등록한다.')

- 어느 순간에는 다른 서비스가 some-service를 사용해야 한다.
- 이때 some-service의 특정 호스트 이름과 포트 정보를 other-service 코드에 하드코딩하지 않는다.
- 대신에 other-service는 some-service라는 이름의 유레카에서 찾으면 된다.
- 그러면 유레카는 모든 some-service 인스턴스의 정보를 알려준다.
- 다음으로 other-service는 some-service의 어떤 인스턴스를 사용할지 결정해야 한다.
- 이때 특정 인스턴스를 매번 선택하는 것을 피하기 위해 클라이언트 측에서 동작하는 로드 밸런싱(load-balancing) 알고리즘을 적용하는 것이 가장 좋다.
- 바로 이때 사용될 수 있는 것이 또 다른 넷플릭스 프로젝트인 리본(Ribbon)이다.
- 리본은 other-service를 대신하여 some-service 인스턴스를 선택하는 클라이언트 측의 로드 밸런서다.

### 클라이언트 측의 로드 밸런서를 사용하는 이유

- 로드 밸런서는 주로 단일의 중앙 집중화된 서비스가 서버 측에서 사용되었다.
- 그러나 이와는 반대로 리본은 클라이언트에서 실행되는 클라리언트 측의 로드 밸런서다.
- 클라이언트 측의 로드 밸런서인 리본은 중앙 집중화된 로드 밸런서에 비해 몇 가지 장점을 갖는다.
    - 각 클라이언트에 하나의 로컬 로드 밸런서가 있으므로 클라이언트의 수에 비례하여 자연스럽게 로드 밸런서의 크기가 조정된다.
    - 또한, 서버에 연결된 모든 서비스에 획일적으로 같은 구성을 사용하는 대신, 로드 밸런서는 각 클라이언트에 가장 적합한 로드 밸런싱 알고리즘을 사용하도록 구성할 수 있다.
- 이런 일의 대부분은 자동으로 처리되지만 서비스를 등록하고 사용하려면 우선 유레카 서버를 활성화해야 한다.
- 유레카 서버 스타터 의존성을 추가한다.

```xml
<dependencies>
	...
	<dependency>
		<groupId>org.springframework.cloud</groupId>
		<artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
	</dependency>
</dependencies>
...
```

- 그리고 그 아래의 `<dependencyManagement>`를 보면 spring-cloud.version 의존성이 지정되어 있을 것이다.

```xml
<dependencyManagement>
	<dependencies>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
			<version>${spring-cloud.version}</version>
			<type>pom</type>
			<scope>import</scope>
		</dependency>
	</dependencies>
</dependencyManagement>
...
```

- 또한, spring-cloud.version 속성의 값은 앞쪽의 `<properties>`에 자동 설정되어 있을 것이다.

```xml
<properties>
	...
	<spring-cloud.version>Hoxton.SR3</spring-cloud.version>
</properties>
```

- 만일 다른 버전의 스프링 클라우드를 사용하고 싶을 때는 `<properties>`에 있는 spring-cloud.version 속성의 값만 원하는 것으로 변경하면 된다.
- 애플리케이션이 시작되는 부트스트랩 클래스인 ServiceRegistryApplication을 편집기 창에서 열고 @EnableEurekaServer 애노테이션을 추가하자.

```java
...
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
...
@SpringBootApplication
@EnableEurekaServer
public class ServiceRegistryApplication {
	public static void main(String[] args) {
		SpringApplication.run(ServiceRegistryApplication.class, args);
	}
}
```

- 다 되었다! 애플리케이션을 시작시키면 8080 포트로 실행될 것이다.

![chapter13-02](image/chapter13-02.png '유레카 웹 대시보드')

- 유레카 대시보드는 여러 가지 유용한 정보를 제공하는데, 특히 어떤 서비스 인스턴스가 유레카에 등록되었는지 알려준다.
- 따라서 서비스를 등록할 때 기대한 대로 잘 되었는지 확인하기 위해 유레카 대시보드를 자주 보게 될 것이다.
- 현재는 아무 서비스도 등록되지 않았으므로 중앙의 Application에 'No instance available' 메시지가 나타난다.
- 애플리케이션을 시작시키면 유레카가 30초 정도에 한 번씩 각종 예외 메시지를 콘솔에 출력하는 것을 볼 수 있다.
- 그러나 유레카는 기대한 대로 잘 작동하고 있으니 개의치 말자.
- 서비스 레지스트리를 아직 완전하게 구성하지 않았다는 것을 알려주기 위해 예외를 발생시키는 것이다.
- 지금부터는 그런 예외가 나타나지 않도록 몇 가지 구성 속성을 추가할 것이다.

## 유레카 구성하기

- 하나보다는 여러 개의 유레카 서버가 함께 동작하는 것이 안전하므로 유레카 서버들이 클러스터(cluster)로 구성되는 것이 좋다.
- 왜냐하면 여러 개의 유레카 서버가 있을 경우 그중 하나에 문제가 발생하더라도 단일 장애점(single point of failure)은 생기지 않기 때문이다.
- 따라서 기본적으로 유레카는 다른 유레카 서버로부터 서비스 레지스트리를 가져오거나 다른 유레카 서비스의 서비스로 자신을 등록하기도 한다.
- 프로덕션(실무 환경) 설정에서는 유레카의 고가용성이 바람직하다.
- 그러나 개발 시에 두 개 이상의 유레카 서버를 실행하는 것은 불편하기도 하고 불필요하다.
- 개발 목적으로는 하나의 유레카 서버면 충분하기 때문이다.
- 그러나 유레카 서버를 올바르게 구성하지 않으면 30초마다 예외의 형태로 로그 메시지를 출력한다.
- 왜냐하면 유레카는 30초마다 다른 유레카 서버와 통신하면서 자신이 작동 중임을 알리고 레지스트리 정보를 공유하기 때문이다.
- 따라서 여기서 우리가 할 일은 유레카 서버가 혼자임을 알도록 구성하는 것이다.
- 이때는 application.yml에 몇 가지 구성 속성들을 설정해야 한다.

```yaml
server:
	port: 8761
eureka:
	instance:
		hostname: localhost
	client:
		fetchRegistry: false
		registerWithEureka: false
		serviceUrl:
			defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
```

- 여기서는 server.port 속성을 8761로, eureka.instance.hostname 속성을 localhost로 설정하였다.
- 이것은 유레카가 실행되는 호스트 이름과 포트를 나타낸다.
- 이 속성은 생략 가능하므로 만일 지정하지 않으면 유레카가 환경 변수를 참고하여 결정한다.
- 그러나 속성 값을 확실하게 알려주기 위해 지정하는 것이 좋다.
- eureka.client.fetchRegistry와 eureka.client.registerWithEureka는 유레카와 상호 작용하는 방법을 알려주기 위해 다른 마이크로서비스에 설정할 수 있는 속성들이다.
- 두 속성의 기본값은 true다.
- 여기서는 개발 시를 고려하여 다른 유레카 서버들이 필요 없으므로 두 속성의 값을 false로 설정하였다.
- 그리고 마지막으로 eureka.client.serviceUrl 속성을 설정하였다.
- 이 속성은 영역(zone) 이름과 이 영역에 해당하는 하나 이상의 유레카 서버 URL을 포함하여, 이 값은 Map에 저장된다.
- Map의 키인 defaultZone은 클라이언트(여기서는 유레카 자신)가 자신이 원하는 영역을 지정하지 않았을 때 사용된다.
- 여기서는 유레카가 하나만 있으므로 defaultZone에 해당하는 URL이 유레카 자신의 URL을 나타내며, 중괄호 안에 지정된 다른 속성(eureka.instance.hostname과 server.port)의 값으로 대체된다.
- 따라서 defaultZone은 [http://localhost:8761/eureka/](http://localhost:8761/eureka/) 가 된다.

### 유레카의 서버 포트 지정하기

- 개발 시에는 로컬 컴퓨터에서 다수의 애플리케이션(마이크로서비스)이 실행될 수 있으므로 모든 애플리케이션이 8080 포트를 같이 사용할 수는 없다.
- 따라서 설정에도 있듯이, 로컬 컴퓨터에서 개발할 때는 server.port 속성을 설정하는 것이 좋다.

```yaml
server:
	port: 8761
```

### 자체-보존 모드를 비활성화시키기

- 설정을 고려할 수 있는 다른 속성으로 eureka.server.enableSelfPreservation이 있다.
- 유레카 서버는 서비스 인스턴스(유레카 서버의 클라이언트)가 자신을 등록하고 등록 갱신 요청을 30초마다 전송하기를 기대한다.
    - 해당 서비스가 살아 있어서 사용할 수 있는지 확인하기 위함이다.
- 일반적으로 세 번의 갱신 기간(또는 90초) 동안 서비스 인스턴스로부터 등록 갱신 요청을 (유레카 서버가) 받지 못하면 해당 서비스 인스턴스의 등록을 취소하게 된다.
    - 따라서 레지스트리에서 삭제되어 해당 서비스 인스턴스를 사용할 수 없게 된다.
- 그리고 만일 이렇게 중단되는 서비스의 수가 임계값(threshold)을 초과하면 유레카 서버는 네트워크 문제가 생긴 것으로 간주하고 레지스트리에 등록된 나머지 서비스 데이터를 보존하기 위해 자체-보존(self-preservation) 모드가 된다.
- 따라서 추가적인 서비스 인스턴스의 등록 취소가 방지된다.
- 프로덕션 설정에서는 자체-보존 모드를 true로 설정하는 것이 좋다.
- 그러나 유레카를 처음 시작했지만 아직 어떤 서비스도 등록되지 않았을 때는 오히려 문제가 될 수 있다.
- 이때는 eureka.server.enableSelfPreservation 속성을 false로 설정하여 자체-보존 모드를 비활성화시킬 수 있다.

```yaml
eureka:
		...
		serviceUrl:
			defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
	server:
		enableSelfPreservation: false
```

- 네트워크 문제 외에도 여러 가지 이유로 유레카가 갱신 요청을 받을 수 없는 개발 환경에서는 이 속성을 false로 설정하는 것이 유용하다.
- 그러나 false로 설정하면 그림과 같이 빨간색의 메시지가 나타난다.

![chapter13-03](image/chapter13-03.png '자체-보존 모드가 비활성화될 때는 비활성화되었다는 것을 환기시키는 메시지가 나타난다.')

- 그러나 개발 시에는 자체-보존 모드를 비활성화해도 좋지만, 프로덕션으로 이양할 때는 활성화해야 한다.

## 유레카 확장하기

### 프로덕션 환경의 스프링 클라우드 서비스

- 마이크로서비스를 프로덕션 환경으로 배포할 때는 고려할 것이 많다.
- 유레카의 고가용성과 보안은 개발 시에는 중요하지 않은 관점들이지만, 프로덕션에서는 매우 중요하기 때문이다.
- 두 개 이상의 유레카 인스턴스를 구성하는 가장 쉽고 간단한 방법은 application.yml 파일에 스프링 프로파일을 지정하는 것이다.
- 그리고 그다음에 한 번에 하나씩 프로파일을 사용해서 유레카를 두 번 시작시키면 된다.

```yaml
eureka:
	client:
		service-url:
			defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka

---
spring:
	profiles: eureka-1
	application:
		name: eureka-1

server:
	port: 8761

eureka:
	instance:
		hostname: eureka1.tacocloud.com

other:
	eureka:
		host: eureka2.tacocloud.com
		port: 8761

---
spring:
	profiles: eureka-2
	application:
		name: eureka-2

server:
	port: 8762

eureka:
	instance:
		hostname: eureka2.tacocloud.com

other:
	eureka:
		host: eureka1.tacocloud.com
		port: 8762
```

- 제일 앞에 있는 기본 프로파일에는 eureka.client.serviceurl.defaultZone을 설정하였으며, 여기에 지정된 other.eureka.host와 other.eureka.port 변수의 값은 그 다음에 있는 각 프로파일 구성에서 설정된 값으로 대체된다.
- eureka.client.fetchRegistry나 eureka.client.registerWithEureka를 설정하지 않았다는 것에 주목하자.
- 이 속성들을 설정하지 않으면 기본값인 true가 된다.
- 따라서 각 유레카 서버가 다른 유레카 서버에 자신을 등록하고 레지스트리의 등록 정보를 가져온다.

# 서비스 등록하고 찾기

- 애플리케이션(어떤 애플리케이션도 가능하지만 마이크로서비스일 것이다.)을 서비스 레지스트리 클라이언트로 활성화하기 위해서는 해당 서비스 애플리케이션의 pom.xml 파일에 유레카 클라이언트 스타터 의존성을 추가해야 한다.

```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

- 유레카 클라이언트 스타터 의존성을 지정하면 유레카를 이용해서 서비스를 찾는 데 필요한 모든 것이 자동으로 추가된다.
- 예를 들어, 유레카의 클라이언트 라이브러리, 리본 로드 밸런스 등이다.
- 따라서 우리 애플리케이션을 유레카 서비스 레지스트리의 클라이언트로 활성화시킬 수 있다.
- 즉, 애플리케이션이 시작되면 8761 포트(개발 시에는 localhost 포트)로 서비스하는 유레카 서버에 연결하고, UNKNOWN이라는 이름으로 유레카에 애플리케이션 자신을 등록한다.

## 유레카 클라이언트 속성 구성하기

- 서비스의 기본 이름인 UNKNOWN을 그대로 두면 유레카 서버에 등록되는 모든 서비스 이름이 같게 되므로 변경해야 한다.
- 이때 spring.application.name 속성을 설정하면 된다.
- 이 경우 application.yml에 다음과 같이 설정한다.

```yaml
spring:
	application:
		name: ingredient-service
```

- 그리고 유레카 대시보드를 보면 이 서비스가 나타난다.
    - 이 서비스의 인스턴스가 여러 개일 때는 같은 이름으로 나타난다.

![chapter13-04](image/chapter13-04.png '유레카 대시보드에 나타난 ingredient-service')

- 스프링 클라우드를 계속 사용하는 동안 spring.application.name 이 우리가 설정하는 가장 중요한 속성 중 하나라는 것을 알게 될 것이다.
- 속성에 설정된 값이 유레카 서버에 등록되는 이름이기 때문이다.
- 이전에 배웠듯이, 모든 스프링 MVC와 스프링 WebFlux 애플리케이션은 기본적으로 8080 포트를 리스닝한다.
- 그러나 서비스는 유레카를 통해서만 찾게 될 것이므로, 애플리케이션이 리스닝하는 포트는 상관 없다.
- 서비스가 리스닝하는 포트를 유레카가 알고 있기 때문이다.
- 따라서 localhost에서 실행될 때 생길 수 있는 서비스의 포트 충돌을 막기 위해 각 서비스 애플리케이션의 포트 번호를 0으로 설정할 수 있다.

```yaml
server:
	port: 0
```

- 이처럼 포트를 0으로 설정하면 각 서비스 애플리케이션이 시작될 때 포트 번호가 무작위로 선택된다.
- 기본적으로 유레카 클라이언트는 유레카 서버가 localhost의 8761 포트로 리스닝한다고 간주한다.
- 이것은 개발 시에는 좋다.
- 그러나 프로덕션에서는 적합하지 않으므로 유레카 서버의 위치를 지정해야 한다.
- 이때 다음과 같이 eureka.client.service-url 속성을 사용할 수 있다.

```yaml
eureka:
	client:
		service-url:
			defaultZone: http://eureka1.tacocloud.com:8761/eureka/
```

- 이렇게 하면 eureka1.tacocloud.com 의 8761 포트로 리스닝하는 유레카 서버에 등록되도록 클라이언트가 구성된다.
- 이 경우 해당 유레카 서버가 제대로 작동 중이라면 문제가 없다.
- 그러나 만일 어떤 이유로든 해당 유레카 서버가 중단된다면 클라이언트 서비스가 등록되지 않을 것이다.
- 따라서 이것을 방지하기 위해 두 개 이상의 유레카 서버를 사용하도록 클라이언트 서비스를 구성하는 것이 좋다.

```yaml
eureka:
	client:
		service-url:
			defaultZone: http://eureka1.tacocloud.com:8761/eureka/, http://eureka2.tacocloud.com:8762/eureka/
```

- 이렇게 하면 해당 서비스가 시작될 때 첫 번째 유레카 서버에 등록을 시도한다.
- 그러나 만일 어떤 이유로든 등록에 실패하면, 두 번째 피어(peer)로 지정된 유레카 서버의 레지스트리에 등록을 시도하게 된다.
- 그리고 이후에 등록에 실패했던 유레카 서버가 다시 온라인 상태가 되면, 해당 서비스의 등록 정보가 포함된 피어 서버 레지스트리가 복제된다.

## 서비스 사용하기

- 서비스를 사용하는 컨슈머(consumer) 코드에 해당 서비스 인스턴스의 URL을 하드코딩하는 것은 좋지 않다.
- 이 경우 사용되는 서비스의 특정 인스턴스와 해당 컨슈머가 밀접하게 결합되는 것은 물론이고, 사용되는 서비스의 호스트나 포트가 변경될 경우 해당 컨슈머의 실행 중단을 초래할 수 있기 때문이다.
- 유레카 서버에서 서비스를 찾을 때 컨슈머 애플리케이션이 할 일이 있다.
- 즉, 같은 서비스의 인스턴스가 여러 개일 때도 유레카 서버는 서비스 검색에 응답할 수 있다.
- 경우 컨슈머 애플리케이션은 자신이 서비스 인스턴스를 선택하지 않아도 되며, 특정 서비스 인스턴스를 명시적으로 찾을 필요도 없다.
- 스프링 클라우드의 유레카 클라이언트 지원에 포함된 리본 클라이언트 로드 밸런서를 사용하여 서비스 인스턴스를 쉽게 찾아 선택하고 사용할 수 있기 때문이다.
- 유레카 서버에서 찾은 서비스를 선택 및 사용하는 방법에는 다음 두 가지 있다.
    - 로드 밸런싱된 RestTemplate
    - Feign에서 생성된 클라이언트 인터페이스

### RestTemplate 사용해서 서비스 사용하기

```java
public Ingredient getIngredientById(String ingredientId) {
	return rest.getForObject("http://localhost:8080/ingredients/{id}", Ingredient.class, ingredientId);
}
```

- 그러나 이 코드에는 한 가지 문제점이 있다.
- getForObject()의 인자로 전달되는 URL이 특정 호스트와 포트로 하드코딩되었다는 점이다.
- 이 문제점은 이어서 설명하는 방법으로 URL 값을 추출하여 해결할 수 있다.
- 일단 유레카 클라이언트로 애플리케이션을 활성화했다면 로드 밸런싱된 RestTemplate 빈을 선언할 수 있다.
- 이때는 기존대로 RestTemplate 빈을 선언하되, @Bean과 @LoadBalanced 애노테이션을 메서드에 같이 지정하면 된다.

```java
@Bean
@LoadBalanced
public RestTemplate restTemplate() {
	return new RestTemplate();
}
```

- @LoadBalanced 애노테이션은 다음 두 가지 목적을 갖는다.
    - 첫 번째이면서 가장 중요한 것으로 현재의 RestTemplate이 리본을 통해서만 서비스를 찾는다는 것을 스프링 클라우드에 알려준다.
    - 두 번째로 주입 식별자로 동작한다.
    - 잠시 후에 얘기하겠지만, 주입 식별자는 서비스 이름이며, getForObject() 메서드의 HTTP 요청에서 호스트와 포트 대신 사용할 수 있다.

```java
@Component
public class IngredientServiceClient {
	private RestTemplate rest;

	public IngredientServiceClient(@LoadBalanced RestTemplate rest) {
		this.rest = rest;
	}
	...
}
```

```java
public Ingredient getIngredientById(String ingredientId) {
	return rest.getForObject("http://ingredient-service/ingredients/{id}", Ingredient.class, ingredientId);
}
```

- 호스트 이름과 포트 대신 서비스 이름인 ingredient-service가 사용되었다.
- 내부적으로는 ingredient-service라는 서비스 이름을 찾아 인스턴스를 선택하도록 RestTemplate이 리본에 요청한다.

### WebClient로 서비스 사용하기

- RestTemplate을 사용했던 것과 같은 방법으로 WebClient를 로드 밸런싱된 클라이언트로 사용할 수 있다.
- 이때 제일 먼저 할 일은 @LoadBalanced 애노테이션이 지정된 WebClient.Builder 빈 메서드를 선언하는 것이다.

```java
@Bean
@LoadBalanced
public WebClient.Builder webClientBuilder() {
	return WebClient.builder();
}
```

```java
@Component
public class IngredientServiceClient {
	private WebClient.Builder wcBuilder;

	public IngredientServiceClient(@LoadBalanced WebClient.Builder wcBuilder) {
		this.wcBuilder = wcBuilder;
	}
}
```

```java
public Mono<Ingredient> getIngredientById(String ingredientId) {
	return wcBuilder.build()
		.get()
			.uri("http://ingredient-service/ingredients/{id}", ingredientId)
		.retrieve().bodyToMono(Ingredient.class);
}
```

- 해당 서비스 이름이 URL에서 추출되어 유레카에서 서비스를 찾는 데 사용된다.

### Feign 클라이언트 인터페이스 정의하기

- Feign은 REST 클라이언트 라이브러리이며, 인터페이스를 기반으로 하는 방법을 사용해서 REST 클라이언트를 정의한다.
- 간단히 말해서, 스프링 데이터가 리퍼지터리 인터페이스를 자동으로 구현하는 것과 유사한 방법을 사용한다.
- Feign은 원래 넷플릭스 프로젝트였지만, 나중에 OpenFeign([https://github.com/OpenFeign](https://github.com/OpenFeign)) 이라는 독립된 오픈 소스 프로젝트가 되었다.
- Feign을 사용하려면 우선 프로젝트의 pom.xml에 다음 의존성을 추가해야 한다.

```xml
...
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
...
```

- 그러나 의존성을 추가해도 자동-구성으로 Feign이 활성화되지는 않는다.
- 따라서 구성 클래스 중 하나에 @EnableFeignClients 애노테이션을 추가해야 한다.

```java
@Configuration
@EnableFeignClients
public RestClientConfiguration {
}
```

```java
package tacos.ingredientclient.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tacos.ingredientclient.Ingredient;

@FeignClient("ingredient-service")
public interface IngredientClient {
	@GetMapping("/ingredients/{id}")
	Ingredient getIngredient(@PathVariable("id") String id);
}
```

- 이것은 구현 코드가 없는 간단한 인터페이스다.
- 그러나 런타임 시에 Feign이 이 인터페이스를 찾으므로 아무 문제가 없다.
- 그리고 Feign이 자동으로 구현 클래스를 생성한 후 스프링 애플리케이션 컨텍스에 빈으로 노출시킨다.
- 위 코드에는 몇 가지 애노테이션이 지정되어 있다.
- 우선, IngredientClient 인터페이스에 선언된 모든 메서드는 서비스(여기서는 ingredient-service)에 대한 요청을 나타내는 것이 @FeignClient 애노테이션이다.
- 내부적으로 ingredient-service는 리본을 통해 찾게 된다.
    - 이때 로드 밸런싱된 RestTemplate의 경우와 같은 방법이 사용된다.
- 그리고 getIngredient() 메서드에는 @GetMapping이 지정되었다.
- 이것은 스프링 MVC에서 사용했던 것과 같은 애노테이션이며, 여기서는 컨트롤러 대신 클라이언트에 지정되어 있다.
- @PathVariable 애노테이션 @GetMapping의 {id}를 getIngredient() 메서드 인자로 대체한다.

```java
@Controller
@RequestMapping("/ingredients")
public class IngredientController {
	private IngredientClient client;

	@Autowired
	public IngredientController(IngredientClient client) {
		this.client = client;
	}

	@GetMapping("/{id}")
	public String ingredientDetailPage(@PathVariable("id") String id, Model model) {
		model.addAttribute("ingredient", client.getIngredient(id));
		return "ingredientDetail";
	}
}
```

- 덧붙여서, Feign에는 자신의 애노테이션인 @RequestLine과 @Param이 있다.
- 이 애노테이션들은 스프링 MVC의 @RequestMapping 및 @PathVariable과 거의 유사하지만 용도는 약간 다르다.

# 마이크로서비스 관련 프로젝트의 빌드 및 실행하기

- 여기서 ingredient-service는 식자재를 추가, 변경, 삭제, 조회하는 마이크로서비스 애플리케이션이다.
    - 타코 클라우드와 무관하게 식자재 데이터 리퍼지터리를 별도로 갖고 처리한다.
- 그리고 service-registry는 유레카 서버를 사용해서 마이크로서비스를 등록하고 찾아주는 서비스 레지스트리이며, ingredient-client는 ingredient-service를 사용하는 클라이언트 애플리케이션이다.
- 이 프로젝트들은 별도의 스프링 애플리케이션이므로 각각 따로 빌드하고 실행해야 한다.
- service-registry를 선택하고 다음과 같이 명령을 입력하여 실행한다.

```bash
$ ./mvnw clean package
```

- ingredient-service를 선택하고 같은 명령을 입력하여 실행한다.

```bash
$ ./mvnw clean package
```

- ingredient-client를 선택하고 같은 명령을 입력하여 실행한다.

```bash
$ ./mvnw clean package
```

- service-registry를 선택하고 다음과 같이 서비스 레지스트리 애플리케이션을 실행하자.
- 정상적으로 실행하면 'Tomcat started on port(s): 8761 (http) with context path'라는 메시지와 'Started Eureka Server' 메시지가 끝 부분에 나올 것이다.
- 이로써 서비스 레지스트리로 사용하는 유레카 서버가 시작되어 8761 포트를 리스닝한다.

```bash
$ java -jar target/service-registry-0.0.13-SNAPSHOT.jar
```

- ingredient-registry를 선택하고 다음과 같이 마이크로서비스 애플리케이션을 실행하자.
- 정상적으로 실행되면 'Started IngredientServiceApplication in 20.868 seconds (JVM running for 21.558)'과 같은 메시지가 끝 부분에 나타날 것이다.
- 이 마이크로서비스 애플리케이션은 무작위로 선택된 포트를 리스닝한다.
- 유레카 서버를 통해서 찾아 사용되기 때문이다.

```bash
$ java -jar target/ingredient-service-0.0.13-SNAPSHOT.jar
```

- ingredient-client를 선택하고 다음과 같이 서비스 클라이언트 애플리케이션을 실행하자.
- 정상적으로 실행되면 'Started IngredientClientApplication in 10.703 seconds (JVM running for 11.423)'과 같은 메시지가 끝 부분에 나타날 것이다.
- 이 서비스 클라이언트 애플리케이션은 8080 포트를 리스닝한다.

```bash
$ java -jar target/ingredient-client-0.0.13-SNAPSHOT.jar
```

- 그리고 service-registry를 선택하고 끝 부분의 메시지를 보면 'Registered instance INGREDIENT-SERVICE/192.168.0.6:ingredient-service:0 with status UP (replication=false)'라는 메시지가 있을 것이다.
- 이것은 앞에서 실행했던 ingredient-service의 인스턴스가 유레카 서버에 등록되었다는 것을 나타낸다.
- 또한, 더 아래의 메시지를 보면 'Registered instance INGREDIENT-CLIENT/192.168.0.6:ingredient-client with status UP (replication=false)'라는 메시지가 있을 것이다.
- 이것은 조금 전에 실행했던 ingredient-client 인스턴스도 유레카 서버에 등록되었다는 것을 나타낸다.
- 서비스 클라이언트인 ingredient-client 애플리케이션에서는 스프링의 프로파일(profile)을 사용하여 원하는 빈(bean)을 등록하고 사용한다.
- 3개의 패키지에 있는 빈(bean)들 중에서 실행할 것을 선택하기 위해 스프링의 프로파일을 사용한다.
- 각각 @Profile("feign")과 @Profile("webclient")가 컨트롤러 클래스에 지정되어 있다.
- 또한, tacos.ingredientclient.resttemplate 패키지의 IngredientController.java에는 @Conditional(NotFeignAndNotWebClientCondition.class)가 컨트롤러 클래스에 지정되어 있으므로 프로파일이 지정되지 않을 경우 기본적으로 tacos.ingredientclient.resttemplate의 IngredientController가 실행된다.
- 프로파일을 설정하는 방법은 여러 가지가 있다.
    - 환경 변수나 YAML 파일 또는 JVM 옵션 등
    - 운영체제의 환경 변수를 사용할 때는 export SPRING_PROFILES_ACTIVE=feign과 같이 하면 된다.
    - 여기서는 JVM 옵션을 사용하는 경우를 알아본다.

    ```bash
    $ java -jar -Dspring.profiles.active=feign target/ingredient-client-0.0.13-SNAPSHOT.jar
    ```

    ```bash
    $ java -jar -Dspring.profiles.active=webclient target/ingredient-client-0.0.13-SNAPSHOT.jar
    ```