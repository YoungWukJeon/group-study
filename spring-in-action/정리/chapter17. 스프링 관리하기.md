# 스프링 부트 Admin 사용하기

- 스프링 부트 Admin은 관리용 프런트엔드 웹 애플리케이션이며, 액추에이터 엔드포인트를 사람들이 더 많이 소비할 수 있게 한다.
- 엑추에이터 엔드포인트는 두 개의 주요 구성 요소로 나뉜다.
- 스프링 부트 Admin과 이것의 클라이언트들이다.
- Admin 서버는 스프링 부트 클라이언트라는 하나 이상의 스프링 부트 애플리케이션으로부터 제공되는 액추에이터 데이터를 수집하고 보여준다.

![chapter17-01](image/chapter17-01.png '스프링 부트 Admin 서버는 하나 이상의 스프링 부트 애플리케이션으로부터 요청을 받아 액추에이터 엔드포인트를 소비하고 해당 데이터를 웹 기반 UI로 나타낸다.')

- 타코 클라우드에서 스프링 부트 Admin 서버를 사용하려면 타코 클라우드를 구성하는 각 애플리케이션(마이크로서비스)을 스프링 부트 Admin 클라이언트로 등록해야 한다.

## Admin 서버 생성하기

- Admin 서버를 활성화하려면 새로운 스프링 부트 애플리케이션을 생성하고 Admin 서버 의존성을 프로젝트의 빌드에 추가해야 한다.
- 일반적으로 Admin 서버는 독립 실행형(standalone) 애플리케이션으로 사용된다.

```xml
<dependency>
	<groupId>de.codecentric</groupId>
	<artifactId>spring-boot-admin-starter-server</artifactId>
</dependency>
```

- 다음과 같이 구성 클래스에 @EnableAdminServer 애노테이션을 지정하여 Admin 서버를 활성화해야 한다.

```java
package tacos.bootadmin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import de.codecentric.boot.admin.server.config.EnableAdminServer;

@SpringBootApplication
@EnableAdminServer
public class BootAdminServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(BootAdminServerApplication.class, args);
	}
}
```

- 마지막으로, Admin 서버가 로컬로 실행되는 유일한 애플리케이션이 아니므로 고유하면서도 접근이 쉬운 포트(0번 프토가 아닌)를 리스닝하도록 설정해야 한다.

```yaml
server:
	port: 9090
```

- 지금 이 서버를 실행시키고 웹 브라우저에서 [http://localhost:9090](http://localhost:9090) 에 접속하면 그림과 같은 화면을 보게 될 것이다.

![chapter17-02](image/chapter17-02.png '스프링 부트 Admin UI에서 보여준 새로 생성된 서버이며, 아직은 등록된 클라이언트 애플리케이션이 없다.')

- Admin 서버가 유용하게 쓰이려면 클라이언트 애플리케이션을 등록해야 한다.

## Admin 클라이언트 등록하기

- Admin 서버는 다른 스프링 부트 애플리케이션의 액추에이터 데이터를 보여주는 별개의 애플리케이션이므로 다른 애플리케이션을 Admin 서버가 알 수 있도록 클라리언트로 등록해야 한다.
- 스프링 부트 Admin 클라이언트를 Admin 서버에 등록하는 방법하는 방법은 다음 두 가지가 있다.
    - 각 애플리케이션이 자신을 Admin 서버에 등록한다.
    - Admin 서버가 유레카 서비스 레지스트리를 통해서 서비스를 찾는다.

### Admin 클라이언트 애플리케이션 구성하기

- 스프링 부트 애플리케이션이 자신을 Admin 서버의 클라이언트로 등록하려면 해당 애플리케이션의 빌드에 스프링 부트 Admin 클라이언트 스타터를 포함시켜야 한다.

```xml
<dependency>
	<groupId>de.codecentric</groupId>
	<artifactId>spring-boot-admin-starter-client</artifactId>
</dependency>
```

- 클라이언트가 자신을 등록할 수 있는 Admin 서버의 위치도 구성해야 한다.
- 이때는 spring.boot.admin.client.url 속성을 Admin 서버의 루트 URL로 설정하면 된다.
- 그리고 다음 내용을 각 클라이언트 애플리케이션의 application.yml 파일에 추가한다.

```yaml
spring:
	application:
		name: ingredient-service
	boot:
		admin:
			client:
				url: http://localhost:9090
```

- spring.application.name 속성도 설정되었다는 것에 주목하자.
- 이것은 스프링 클라우드 구성 서버와 유레카에 마이크로서비스 이름을 알려주기 위해 이미 사용되었던 속성이다.
- 여기서는 Admin 서버에 알려주기 위해 설정하였다.

![chapter17-03](image/chapter17-03.png '스프링 부트 Admin UI가 등록된 클라이언트 애플리케이션을 보여준다.')

- 식자재 서비스를 Admin 서버에 등록하는 데 사용한 것과 동일한 구성(application.yml 파일)이 Admin 서버의 모든 클라이언트 애플리케이션에 있어야 한다.
- 이 경우 각 애플리케이션에서는 spring.application.name 속성만 다른 값(해당 애플리케이션 이름)으로 설정하고, spring.boot.admin.client.url 속성은 스프링 클라우드 구성 서버가 제공하도록 하면 더 쉽다.
- 또는 유레카를 서비스 레지스트리로 사용 중이라면 Admin 서버가 자신의 서비스들을 찾게 하면 된다.

### Admin 클라이언트 찾기

- 서비스들을 찾을 수 있게 Admin 서버를 활성화할 때는 Admin 서버 프로젝트의 빌드에 스프링 클라우드 Netflix 유레카 클라이언트 스타터만 추가하면 된다.

```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

![chapter17-04](image/chapter17-04.png '스프링 부트 Admin UI는 유레카에서 찾은 모든 서비스들을 보여줄 수 있다.')

- 여기 있는 모든 애플리케이션이 Up 상태로 나타나 있다.
- 그러나 오프라인(offline)이 되는 서비스가 있으며 별도로 나타난다.

![chapter17-05](image/chapter17-05.png '스프링 부트 Admin UI는 오프라인 서비스를 온라인 서비스와 구분해서 보여준다.')

- 현재는 Admin 서버가 유레카 클라이언트이지만, 자신을 유레카 서비스로도 등록할 수 있다.
- 이때는 Admin 서버 프로젝트의 application.yml 파일에 있는 eureka.client.register-with-eureka 속성을 false로 설정하면 된다.

```yaml
eureka:
	client:
		register-with-eureka: false
```

- 또한, 다른 유레카 클라이언트처럼 유레카 서버가 기본 호스트와 포트에서 리스닝하지 않을 때는 유레카 서버의 위치를 구성할 수도 있다.

```yaml
eureka:
	client:
		server-url:
			defaultZone: http://eureka1.tacocloud.com:8761/eureka/
```

# Admin 서버 살펴보기

- 모든 스프링 부트 애플리케이션이 Admin 서버 클라이언트로 등록되면 각 애플리케이션 내부에서 생기는 풍부한 정보를 Admin 서버가 볼 수 있다.
- 이러한 정보에는 다음 사항이 포함된다.
    - 애플리케이션의 건강 상태 정보와 일반 정보
    - Micrometer를 통해 발행되는 메트릭(metric)과 /metrics 엔드포인트
    - 환경 속성
    - 패키지와 클래스의 로깅 레벨
    - 스레드 추적 기록 정보
    - HTTP 요청의 추적 기록
    - 감사 로그(audit log)
- 사실상 액추에이터가 노출하는 거의 모든 것을 훨씬 더 인간 친화적인 형태로 Admin 서버에서 볼 수 있다.
- 여기에는 정보 추출과 파악에 도움을 주는 그래프와 필터가 포함된다.

## 애플리케이션의 건강 상태 정보와 일반 정보 보기

- 액추에이터가 제공하는 가장 기본적인 정보 중에는 건강 상태 정보와 일반 정보가 있으며, 이 정보들은 /health와 /info 엔드포인트를 통해 제공된다.
- Admin 서버는 Details 탭에서 이 정보를 보여준다.

![chapter17-06](image/chapter17-06.png '스프링 부트 Admin UI의 Details 탭에서는 애플리케이션의 건강 상태 정보와 일반 정보를 보여준다.')

- Details 탭의 화면을 스크롤하면 JVM 으로부터 받은 애플리케이션 정보의 유용한 통계를 볼 수 있으며, 여기에는 메모리와 스레드를 보여주는 그래프들과 프로세스 정보가 포함된다.

![chapter17-07](image/chapter17-07.png 'Details 탭의 화면을 스크롤하면 JVM 내부정보인 프로세스, 스레드, 메모리 통계를 추가로 볼 수 있다.')

- 각종 그래프 및 프로세스와 가비지 컬렉션의 메트릭에 보여지는 정보는 애플리케이션이 JVM 리소스를 어떻게 사용하는지 살펴보는 데 유용하다.

## 핵심 메트릭 살펴보기

- /metrics 엔드포인트로부터 제공되는 정보는 애플리케이션에서 생성되는 메트릭이다.
- 처음에는 Metrics 탭에서 어떤 메트릭 정보도 보여주지 않는다.
- 그러나 계속 지켜볼 메트릭에 대해 하나 이상의 관찰점(watch)을 설정하면 이것에 관련된 정보를 보여준다.
- http.server.requests 부류의 메트릭에 대해 두 개의 관찰점을 설정하였다.
- 첫 번째 관찰점에서는 /ingredients 엔드포인트에 대해 HTTP GET 요청이 될 때마다 관련 메트릭들을 알려주며, 여기서는 반환된 상태 코드가 200 (OK)이다.
- 두 번째 관찰점에서는 HTTP 404(NOT FOUND) 응답을 발생시키는 요청이 있을 때마다 관련 메트릭들을 알려주게끔 설정하였다.

![chapter17-08](image/chapter17-08.png 'Metrics 탭에서는 애플리케이션의 /metrics 엔드포인트를 통해 전달되는 메트릭들에 대한 관찰점을 설정할 수 있다.')

- Admin 서버에서 보여주는 대부분의 정보가 그렇듯이, 이 메트릭들은 실시간 데이터를 보여준다는 것이 장점이다.
    - 이 데이터는 페이지를 새로 갱신하지 않아도 자동 업데이트된다.

## 환경 속성 살펴보기

- 액추에이터 /env 엔드포인트는 스프링 부트 애플리케이션의 모든 속성 근원(JVM 시스템, 명령행 인자, 환경 변수 등)으로부터 해당 애플리케이션에 사용할 수 있는 모든 환경 속성을 반환한다.
- Admin 서버는 Environment 탭에서 훨씬 더 보기 좋은 형태로 응답을 보여준다.
- 여기서는 수백 개의 속성이 나타날 수 있으므로, 보려는 속성 내역을 속성 이름이나 값으로 필터링할 수 있다.

![chapter17-09](image/chapter17-09.png 'Environment 탭은 환경 속성을 보여주며, 속성 값을 변경하거나 필터링하는 옵션을 포함한다.')

## 로깅 레벨을 보거나 설정하기

- 액추에이터의 /loggers 엔드포인트는 실행 중인 애플리케이션의 로깅 레벨을 파악하거나 변경하는 데 도움이 된다.
- Admin 서버의 Loggers 탭에는 애플리케이션의 로깅 레벨 관리 작업을 쉽게 할 수 있도록 사용이 쉬운 UI가 추가되어 있다.
- 기본적으로 Admin 서버는 모든 패키지와 클래스의 로깅 레벨을 보여주지만, 이름이나 로깅 레벨로 필터링할 수 있다.

![chapter17-10](image/chapter17-10.png 'Loggers 탭에서는 애플리케이션의 패키지와 클래스의 로깅 레벨을 보여주고 변경할 수 있다.')

## 스레드 모니터링

- 어떤 애플리케이션이든 많은 스레드가 동시에 실행될 수 있다.
- /threaddump 엔드포인트는 애플리케이션에서 실행 중인 스레드의 상태 스냅샷을 제공한다.
- 스프링 부트 Admin UI는 애플리케이션의 모든 스레드에 대해 실시간으로 감시한다.

![chapter17-11](image/chapter17-11.png 'Admin UI의 Threads 탭을 사용하면 애플리케이션의 스레드를 실시간으로 감시할 수 있다.')

- 적시에 스냅샷을 캡처하는 /threaddump 엔드포인트와 다르게, 각 스레드의 상태를 보여주는 Threads 탭의 막대 그래프는 지속적으로 변경된다.
- 이때 스레드가 실행 중이면 초록색으로, 대기 중이면 노란색이 되며, 중단되면 빨간색이 된다.

## HTTP 요청 추적하기

- 스프링 부트 Admin UI와 HTTP Traces 탭에서는 액추에이터의 /httptrace 엔드포인트로부터 받은 데이터를 보여준다.
- 그러나 요청 시점에 100개의 가장 최근 HTTP 추적 기록을 반환하는 /httptrace 엔드포인트와 다르게, HTTP Traces 탭은 HTTP 요청들의 전체 이력 데이터를 보여준다.
- 그리고 이 탭에 머무는 동안 이력 데이터가 계속 변경된다.
- 만일 이 탭을 떠났다가 다시 돌아오면 처음에는 100개의 가장 최근 요청들만 보여주지만, 이후로는 추적이 계속된다.

![chapter17-12](image/chapter17-12.png 'HTTP Traces 탭은 애플리케이션의 최근 HTTP 트래픽을 추적해서 보여주며, 여기에는 에러를 발생시킨 요청에 관한 정보가 포함된다.')

- HTTP Traces 탭은 HTTP 트래픽을 계속 추적하는 중첩 그래프를 포함한다.
- 그래프에서는 여러 색을 사용하여 성공적인 요청과 그렇지 않은 요청들을 나타낸다.
- 초록색은 성공적인 요청을, 노란색은 클라이언트 에러(예를 들어, 400 레벨의 HTTP 응답)를, 빨간색은 서버 에러(예를 들어, 500 수준의 HTTP 응답)를 나타낸다.
- 그리고 그래프 위에 마우스 커서를 대면 그림의 오른쪽에 있는 것과 같은 검은 상자가 나타나서 해당 시점의 상세한 요청 횟수를 보여준다.
- 그래프 아래쪽에는 추적 이력 데이터가 나타나며, 애플리케이션에서 받은 각 요청을 한 행으로 보여준다.

![chapter17-13](image/chapter17-13.png 'HTTP Traces 탭의 특정 요청 항목을 클릭하면 해당 요청에 관한 추가 정보를 보여준다.')

# Admin 서버의 보안

- 액추에이터에 보안이 중요하듯이, Admin 서버에도 보안은 중요하다.

## Admin 서버에 로그인 활성화하기

- Admin 서버는 스프링 부트 애플리케이션이므로 다른 스프링 부트 애플리케이션에 하듯이 스프링 시큐리티를 사용해서 처리할 수 있다.
- 우선, 스프링 부트 보안 스타터를 Admin 서버의 빌드에 추가해야 한다.

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

- 그리고 무작위로 생성되는 비밀번호를 Admin 서버의 로그에서 계속 찾을 필요가 없도록 간단한 관리자 이름과 비밀번호를 application.yml 파일에 구성하면 된다.

```yaml
spring:
	security:
		user:
			name: admin
			password: 53cr3t
```

## 액추에이터로 인증하기

- Admin 서버의 클라이언트 애플리케이션은 자신을 직접 Admin 서버에 등록하거나, 유레카를 통해 발견되게 함으로써 자신의 인증 정보를 Admin 서버에 제공할 수 있다.
- 만일 Admin 서버의 클라이언트 애플리케이션이 직접 Admin 서버에 등록한다면 등록할 때 자신의 인증 정보를 Admin 서버에 전송할 수 있다.
- 이렇게 하려면 몇 가지 속성을 구성해야 한다.
- Admin 서버가 애플리케이션의 액추에이터 엔드포인트에 접근하는 데 사용할 수 있는 인증 정보는 다음과 같이 각 클라이언트의 application.yml에 spring.boot.admin.client.instance.metadata.user.name 과 spring.boot.admin.client.instance.metadata.user.password 속성을 지정한다.

```yaml
spring:
	boot:
		admin:
			client:
				url: http://localhost:9090
				instance:
					metadata:
						user.name: ${spring.security.user.name}
						user.password: ${spring.security.user.password}
```

- 이처럼 인증 정보(사용자 이름과 비밀번호 속성)는 Admin 서버에 자신을 등록하는 각 클라이언트 애플리케이션에 반드시 설정되어야 한다.
- 그리고 지정된 값은 액추에이터 엔드포인트에 대한 HTTP 기본 인증 헤더에 필요한 인증 정보와 반드시 일치해야 한다.
- 이와는 달리, Admin 서버가 유레카를 통해 클라이언트 애플리케이션을 발견하도록 한다면 eureka.instance.metadata-map.user.name과 eureka.instance.metadata-map.user.password 속성을 설정해야 한다.

```yaml
eureka:
	instance:
		metadata-map:
			user.name: admin
			user.password: password
```

- 애플리케이션이 유레카에 등록할 때 인증 정보는 유레카 등록 레코드의 메타데이터에 포함된다.
- 그리고 Admin 서버가 애플리케이션을 발견하면 애플리케이션의 다른 상세 정보와 함께 인증 정보를 유레카로부터 가져온다.