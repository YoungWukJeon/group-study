- WAR 파일로 애플리케이션 서버에 배포하는 종전의 자바 웹 애플리케이션과 다르게 스프링 부트는 몇 가지 배포 옵션을 제공한다.

# 배포 옵션

- 스프링 부트 애플리케이션은 다음 몇 가지 방법으로 빌드하고 실행할 수 있다.
    - STS나 Intellij IDEA 등의 IDE에서 애플리케이션을 빌드하고 실행한다.
    - 메이븐 springboot:run이나 그래들 bootRun 태스크를 사용하여 명령행에서 애플리케이션을 빌드하고 실행한다.
    - 메이븐이나 그래들을 사용해서 실행 가능한 JAR 파일(명령행에서 실행되거나 클라우드에 배포될 수 있음)을 생성한다.
    - 메이븐이나 그래들을 사용해서 WAR 파일(자바 애플리케이션 서버에 배포될 수 있음)을 생성한다.
- 개발 시에는 이중 어떤 방법을 선택하더라도 애플리케이션을 실행할 수 있다.
- 그러나 프로덕션이나 개발이 아닌 다른 환경으로 애플리케이션을 배포할 때는 어떨까?
- 실행 가능 JAR 파일이나 자바 WAR 파일은 프로덕션 환경에 애플리케이션을 배포하는 확실한 방법이다.
- 그렇다면 JAR 파일이나 자바 WAR 파일 중 어떤 것을 선택해야 할까?
- 이때는 자바 애플리케이션 서버와 클라우드 플랫폼 중 어디에 애플리케이션을 배포하는 가에 따라 선택하면 된다.
    - **자바 애플리케이션 서버에 배포하기** : 톰캣(Tomcat), 웹 스피어(WebSphere), 웹 로직(WebLogic), 또는 다른 자바 애플리케이션 서버에 애플리케이션을 배포해야 한다면, 선택의 여지없이 WAR 파일로 애플리케이션을 빌드해야 한다.
    - **클라우드에 배포하기** : 클라우드 파운드리(Cloud Foundry), AWS(Amazon Web Services), 마이크로소프트 Azure, 구글 클라우드 플랫폼(Google Cloud Platform) 또는 이외의 다른 클라우드 플랫폼으로 애플리케이션을 배포한다면, 실행 가능한 JAR 파일이 최상의 선택이다. 그리고 애플리케이션 서버에 적합한 WAR 형식보다 JAR 형식이 훨씬 간단하므로, 설사 클라우드 플랫폼에서 WAR 파일 배포를 지원하더라도 JAR 파일로 배포하는 것이 좋다.
- 이번 장에서는 다음 세 가지 시나리오에 초점을 둔다.
    - 스프링 부트 애플리케이션을 톰캣과 같은 자바 애플리케이션 서버에 WAR 파일로 배포하기
    - 스프링 부트 애플리케이션을 클라우드 파운드리에 실행 가능한 JAR 파일로 배포하기
    - 도커 배포를 지원하는 어떤 플랫폼에도 배포할 수 있도록 스프링 부트 애플리케이션을 도커 컨테이너에 패키징하기

# WAR 파일 빌드하고 배포하기

- 스프링 부트의 자동-구성 덕분에 스프링이 DispatcherServlet을 선언하기 위해 web.xml 파일이나 서블릿 초기화 클래스를 생성하지 않아도 되었다.
- 그러나 자바 애플리케이션 서버에 애플리케이션을 배포한다면 WAR 파일을 빌드해야 한다.
- 그리고 애플리케이션 서버가 애플리케이션을 실행하는 방법을 알도록 DispatcherServlet을 선언하는 서블릿 초기화 클래스도 WAR 파일에 포함해야 한다.
- 스프링 부트 애플리케이션을 WAR 파일로 빌드하는 것은 그리 어렵지 않다.
- 실제로, 스프링 Initializr를 통해 애플리케이션을 생성할 때 WAR 옵션을 선택했다면 우리가 더 해야 할 것은 없다.
- 그러나 Initializr에서 JAR 파일을 빌드하도록 선택했다면(또는 WAR 파일 생성과의 차이점이 궁금하다면) WAR 파일을 생성하기 위해 다음의 내용을 알아야 한다.
- 우선, 스프링 DispatcherServlet을 구성해야 한다.
- 이것은 종전에 web.xml 파일을 사용해서 처리할 수 있었다.
- 그러나 스프링 부트는 SpringBootServletInitializer를 사용해서 더 쉽게 해준다.
- SpringBootServletInitializer는 스프링 WebApplicationInitializer 인터페이스를 구현하는 스프링 부트의 특별한 구현체(클래스)다.
- 스프링의 DispatcherServlet을 구성하는 것 외에도 SpringBootServletInitializer는 Filter, Servlet, ServletContextInitializer 타입의 빈들을 스프링 애플리케이션 컨텍스트에서 찾아서 서블릿 컨테이너에 바인딩한다.
- SpringBootServletInitializer를 사용하려면 이것의 서브 클래스를 생성하고 configure() 메서드를 오버라이딩하여 스프링 구성 클래스를 지정해야 한다.

```java
package tacos.ingredients;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

public class IngredientServiceServletInitializer extends SpringBootServletInitializer {
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(IngredientServiceApplication.class);
	}
}
```

- configure() 메서드는 SpringApplicationBuilder를 인자로 받아 반환한다.
- 그리고 이때 스프링 구성 클래스를 등록하는 sources() 메서드를 호출한다.
- 이 클래스는 부트스트랩 클래스(실행 가능한 JAR 파일을 위한)와 스프링 구성 클래스의 이중 용도로 사용한다.
- 식자재 서비스 애플리케이션이 다른 스프링 구성 클래스들을 갖고 있더라도 이것들 모두를 sources() 메서드로 등록할 필요는 없다.
- IngredientServiceApplication 클래스는 @SpringBootApplication 애노테이션이 지정되어 있으므로 컴포넌트 자동 검색이 활성화된다.
- 따라서 필요한 컴포넌트를 다른 구성 클래스에서 찾아서 가져온다.
- SpringBootServletInitializer의 서브 클래스는 상용구 코드로 되어 있다.
- 따라서 참조하는 애플리케이션의 메인 구성 클래스만 다르고 이외에는 WAR 파일로 빌드되는 모든 애플리케이션이 동일하다.
- 또한, 거의 변경할 일이 없을 것이다.
- 서블릿 초기화 클래스가 작성되었으므로 이제는 프로젝트 빌드를 변경해야 한다.
- 만일 메이븐으로 빌드한다면 pom.xml 파일의 `<packaging>` 요소를 war로 설정하면 된다.

```xml
<packaging>war</packaging>
```

- 그래들을 사용할 때도 간단하다.
- build.gradle 파일에 war 플러그인만 추가하면 된다.

```groovy
apply plugin: 'war'
```

- 이제는 애플리케이션을 빌드할 준비가 되었다.
- 메이븐의 경우에는 Initializr가 패키지를 실행하기 위해 메이븐 래퍼 스크립트를 사용한다.

```bash
$ mvnw package
```

- 빌드가 성공적이면 WAR 파일이 대상 디렉터리에 생성된다.
- 이와는 달리, 그래들을 사용해서 프로젝트를 빌드할 때는 빌드 태스크를 실행하기 위해 그래들 래퍼를 사용한다.

```bash
$ gradlew build
```

- 빌드가 완료되면 WAR 파일이 build/libs 디렉터리에 생성된다.
- 서블릿 3.0 버전 이상의 서블릿 컨테이너에 배포하는 데 적합한 WAR 파일을 빌드했더라도 실행 가능한 JAR 파일처럼 여전히 명령행에서 실행할 수 있다는 것을 알아두자.

```bash
$ java -jar target/ingredient-service-0.0.19-SNAPSHOT.war
```

- 이 경우 하나의 배포 파일을 두 가지 배포 옵션(WAR와 JAR)을 모두 충족하는 셈이다.

### 애플리케이션 서버에 마이크로서비스를 배포할 때는?

- 일반적으로 마이크로서비스는 다른 애플리케이션과 같으며, 혼자 배포할 수 있다.
- 물론 타코 클라우드의 나머지 애플리케이션과 동떨어져 배포되면 유용하지 않을 수 있다.
- 그러나 톰캣이나 다른 애플리케이션 서버에 배포될 수 없는 것은 아니다.
- 단, 개별적인 애플리케이션의 배포는 클라우드로 배포할 때와 동일한 확장성을 기대할 수 없다.
- WAR 파일은 20년 이상 자바 애플리케이션 배포의 일꾼이었지만, 종전의 자바 애플리케이션 서버에 애플리케이션을 배포하기 위해 설계되었다.
- 그러나 선택하는 플랫폼에 따라 다르지만, 현대의 클라우드 배포에는 WAR 파일이 필요하지 않으며, 지원되지 않는 경우도 있다.
- 따라서 새로운 클라우드 배포 시대에 걸맞게 JAR 파일을 선택하는 것이 더 좋다.

# 클라우드 파운드리에 JAR 파일 푸시하기

- 클라우드 파운드리는 애플리케이션 개발, 배포, 확장을 위한 오픈소스/멀티 클라우드 PaaS 플랫폼이며, 클라우드 파운드리 재단에 의해 최초 개발되었다.
- 상용 버전은 스프링 플랫폼의 스프링 프레임워크와 다른 라이브러리를 주관하는 피보탈(Pivotal)사에서 제공한다.
- 클라우드 파운드리는 오픈 소스 버전과 상용 버전이 있어서 클라우드 파운드리를 어디서 어떻게 사용하는가에 따라 원하는 버전을 선택할 수 있다.
- 또한, 기업 데이터 센터의 방화벽 내부에서도 사설 클라우드로 실행할 수 있다.
- 클라우드 파운드리는 WAR 파일도 지원하지만, 더 간단한 실행 가능 JAR 파일이 클라우드 파운드리에 배포하기 적합한 선택이다.
- 실행 가능 JAR 파일을 빌드하고 클라우드 파운드리에 배포하는 방법을 보여주기 위해 여기서는 식자재 서비스 애플리케이션을 빌드하고 PWS(Pivotal Web Services)에 배포할 것이다.
- PWS를 사용하려면 계정을 등록해야 하며, 계정이 등록되면 [https://console.run.pivotal.io/tools](https://console.run.pivotal.io/tools) 에 cf 명령행 도구를 다운로드하여 설치해야 한다.
- 그리고 cf 명령행 도구를 사용하여 클라우드 파운드리에 애플리케이션을 푸시한다.
- 하지만 PWS 계정에 로그인하기 위해 cf를 먼저 사용해야 한다.

```bash
$ cf login -a https://api.run.pivotal.io
API endpoint: https://api.run.pivotal.io

Email> {각자의 이메일}

Password> {각자의 비밀번호}

Authenticating...
OK
```

- 우선, 메이븐으로 프로젝트를 빌드할 때는 해당 패키지를 실행하기 위해 메이븐 래퍼를 사용할 수 있다.
- 그러면 JAR 파일이 대상 디렉터리에 생성될 것이다.

```bash
$ mvnw package
```

- 그래들을 사용할 때는 빌드 대스크를 실행하기 위해 그래들 래퍼를 사용한다.
- 그리고 빌드가 완료되면 JAR 파일이 build/libs 디렉터리에 생성된다.

```bash
$ gradlew build
```

- 이제는 다음과 같이 cf 명령을 사용해서 JAR 파일을 클라우드 파운드리에 푸시하는 것만 남았다.

```bash
$ cf push ingredient-service -p target/ingredient-service-0.0.19-SNAPSHOT.jar
```

- cf push의 첫 번째 인자인 ingredient-service는 클라우드 파운드리의 애플리케이션에 지정되는 이름이며, 이 애플리케이션의 전체 URL은 [https://ingredient-service.cfapps.io](https://ingredient-service.cfapps.io) 다.
- 이처럼 애플리케이션 이름은 해당 애플리케이션이 호스팅되는 하위 도메인으로 사용된다.
- 따라서 클라우드 파운드리에 배포된 다른 애플리케이션과 충돌되지 않도록 애플리케이션에 지정하는 이름은 고유한 것이어야 한다.
    - 다른 클라우드 파운드리 사용자가 배포한 애플리케이션 이름을 포함해서
- cf push 명령에서는 무작위로 하위 도메인을 생성해 주는 --random-route 옵션을 제공한다.

```bash
$ cf push ingredient-service \
	-p target/ingredient-service-0.0.19-SNAPSHOT.jar \
	--random-route
```

- --random-route를 사용할 때도 애플리케이션 이름은 여전히 필요하다.
- 그러나 무작위로 선택된 두 개의 단어가 애플리케이션 이름 뒤에 추가되어 하위 도메인이 생성된다.
- 만일 하위 도메인이 ingredient-service라고 가정하면 브라우저에서 [http://ingredient-service.cfapp.io/ingredients](http://ingredient-service.cfapp.io/ingredients) 에 접속하여 잘 실행되는지 알 수 있다.
- 현재 이 애플리케이션에서는 내장된 몽고 데이터베이스(테스트 목적으로 생성됨)를 사용해서 식자재 데이터를 저장한다.
- 그러나 프로덕션에서는 실제 데이터베이스를 사용해야 한다.
- 이 책을 저술하는 시점에는 mlab이라는 이름의 몽고DB 서비스를 PWS에서 사용할 수 있었다.
- cf marketplace 명령을 사용하면 이 서비스(그리고 다른 사용 가능한 서비스)를 찾을 수 있다.
- mlab 서비스의 인스턴스를 생성하기 위해 다음과 같이 cf create-service 명령을 실행한다.

```bash
$ cf create-service mlab sandbox ingredientdb
```

- 이것은 ingredientdb라는 이름의 서비스 플랜을 갖는 mlab 서비스를 생성한다.
- 일단 서비스가 생성되면 cf bind-service 명령으로 이 서비스를 우리 애플리케이션과 결합할 수 있다.

```bash
$ cf bind-service ingredient-service ingredientdb
```

- 서비스를 애플리케이션과 결합한다는 것은 VCAP_SERVICES라는 이름의 환경 변수를 사용해서 서비스에 연결하는 방법을 애플리케이션에 제공하는 것이며, 해당 서비스를 사용하기 위해 애플리케이션을 변경하는 것은 아니다.
- 그다음에는 결합한 서비스를 적용하기 위해 애플리케이션을 재생성(restage)해야 한다.

```bash
$ cf restage ingredient-service
```

- cf restage 명령은 클라우드 파운드리가 애플리케이션을 재배포하고 VCAP_SERVICES 값을 다시 고려한다.
- MySQL 데이터베이스, PostgreSQL 데이터베이스, 바로 사용 가능한 유레카(Eureka)와 구성 서버 서비스를 포함해서 PWS에는 우리 애플리케이션이 결합할 수 있는 서비스가 많이 있다.
- 이에 관한 자세한 내용은 [https://console.run.pivotal.io/marketplace](https://console.run.pivotal.io/marketplace) 를 참조하자.
- 그리고 PWS의 사용 방법은 [https://docs.run.pivotal.io/](https://docs.run.pivotal.io/) 의 문서를 참고하기 바란다.

# 도커 컨테이너에서 스프링 부트 실행하기

- 도커로 생성되는 것과 같은 컨테이너 애플리케이션의 아이디어는 실세계의 컨테이너에서 비롯되었다.
- 선적 물품을 담는 모든 컨테이너는 내용물과 무관하게 표준화된 크기와 형태를 갖는다.
- 따라서 컨테이너는 쉽게 배에 쌓아 올리거나 기차나 트럭으로 운반할 수 있다.
- 이와 유사한 방법으로 컨테이너 애플리케이션은 공통된 컨테이너 형식을 공유하므로 컨테이너에 포함된 애플리케이션과 무관하게 어디서는 배포 및 실행할 수 있다.
- 도커 이미지 생성이 아주 어려운 것은 아니다.
- 그러나 Spotify(스포티파이)의 메이븐 플러그인을 사용하면 스프링 부트 빌드의 결과를 더 쉽게 도커 컨테이너로 생성할 수 있다.
- 도커 플러그인을 사용하려면 스프링 부트 프로젝트에 있는 pom.xml 파일의 `<build>`/`<plugins>` 블록 아래에 다음과 같이 플러그인을 추가한다.

```xml
<build>
	<plugins>
		...
		<plugin>
			<groupId>com.spotify</groupId>
			<artifactId>dockerfile-maven-plugin</artifactId>
			<version>1.4.3</version>
			<configuration>
				<repository>
					${docker.image.prefix}/${project.artifactId}
				</repository>
				<buildArgs>
					<JAR_FILE>target/${project.build.finalName}.jar</JAR_FILE>
				</buildArgs>
			</configuration>
		</plugin>
	</plugins>
</build>

<properties>
	...
	<docker.image.prefix>tacocloud</docker.image.prefix>
</properties>
```

- `<configuration>` 블록 아래에는 도커 이미지 생성에 필요한 속성들을 설정한다.
- `<repository>` 요소에는 도커 리퍼지터리에 나타나는 도커 이미지의 이름을 지정한다.
- 여기에 지정했듯이, 이름의 제일 앞에는 docker.image.prefix라는 이름의 메이븐 속성 값이 지정되고 그 다음에는 메이븐 프로젝트의 artifact ID가 붙는다.
- 프로젝트의 artifact ID는 바로 위의 artifactId 요소에 지정되어 있으며, docker.image.prefix 속성은 다음과 같이 properties 요소에 지정해야 한다.
- 이것이 타코 클라우드 식자재 서비스의 pom.xml 파일이었다면 결과로 생성되는 도커 이미지는 도커 리퍼지터리의 tacocloud/ingredient-service에 저장되었을 것이다.
- `<buildArgs>` 요소 아래에는 메이븐 빌드가 생성하는 JAR 파일을 지정한다.
- 이때 target 디렉터리에 있는 JAR 파일의 이름을 결정하기 위해 메이븐 속성인 project.build.finalName을 사용한다.
- 이처럼 메이븐 빌드 명세에 제공한 정보 외의 다른 모든 도커 이미지 정보는 Dockerfile이라는 이름의 파일에 정의된다.
- 대부분의 스프링 부트 애플리케이션은 다음의 Dockerfile과 같이 정의할 수 있다.

```docker
FROM openjdk:8-jdk-alpine
ENV SPRING_PROFILES_ACTIVE docker
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", \
	"-Djava.security.egd=file:/dev/./urandom", \
	"-jar", \
	"/app.jar"]
```

- 이 내용을 보면 다음과 같다.
    - FROM에는 새 이미지의 기반이 되는 이미지를 지정한다. 새 이미지는 기본 이미지를 확장한다. 여기서는 기본 이미지가 OpenJDK 버전 8을 기반으로 하는 컨테이너 이미지인 openjdk:8-jdk-alpine이다.
    - ENV에는 환경 변수를 설정한다. 여기서는 활성 프로파일을 기반으로 스프링 부트 애플리케이션의 구성 속성을 변경할 것이므로 환경 변수인 SPRING_PROFILES_ACTIVE를 docker로 설정하였다. 스프링 부트 애플리케이션이 docker 활성 프로파일로 시작하도록 하기 위해서다.
    - VOLUME은 컨테이너의 마운트 지점을 생성한다. 여기서는 필요 시에 컨테이너가 /tmp 디렉터리에 데이터를 쓸 수 있도록 /tmp에 마운트 지점을 생성한다.
    - ARG에는 빌드 시에 전달할 수 있는 인자를 선언한다. 여기서는 메이븐 플러그인의 `<buildArgs>` 블록에 지정된 인자와 동일한 JAR_FILE이라는 이름의 인자를 선언한다.
    - COPY는 지정된 경로의 파일을 다른 경로로 복사한다. 여기서는 메이븐 플러그인에 지정된 JAR 파일의 app.jar라는 이름의 파일로 도커 이미지에 복사한다.
    - ENTRYPOINT는 컨테이너가 시작될 때  실행하기 위한 명령행 코드를 배열로 지정한다. 여기서는 실행 가능한 app.jar 파일을 실행시키기 위해 명령행에서 java를 사용하도록 지정한다.
- 일반적으로 스프링 부트 애플리케이션을 포함하는 컨테이너 이미지에는 SPRING_PROFILES_ACTIVE 환경 변수를 ENV에 설정하는 것이 좋다.
- 이렇게 하면 도커에서 실행되는 애플리케이션에 고유한 빈과 구성 속성을 구성할 수 있기 때문이다.
- 식자재 서비스의 경우는 별개의 컨테이너에서 실행되는 몽고 데이터베이스에 우리 애플리케이션을 연결할 방법이 필요하다.
- 따라서 spring.data.mongodb.host 속성을 구성하여 몽고 데이터베이스를 사용할 수 있는 호스트 이름을 스프링 데이터에 알려주어야 한다.
- 다음의 도커에 특정된 구성을 application.yml 파일에 추가하여 docker 프로파일이 활성화될 때 스프링 데이터가 mongo라는 호스트의 몽고 데이터베이스를 연결하도록 구성할 수 있다.

```yaml
---
spring:
	profiles: docker
	data:
		mongodb:
			host: mongo
```

- 이렇게 하면 도커 컨테이너가 시작되는 즉시 mongo 호스트가 다른 컨테이너에서 실행 중인 몽고 데이터베이스로 연결된다.
- 메이븐 래퍼를 사용해서 package와 dockerfile:build를 실행시켜 JAR 파일을 빌드하면 도커 이미지가 생성된다.

```bash
$ mvnw package dockerfile:build
```

- 이 시점에서 docker images 명령을 사용하면 생성된 이미지가 로컬 이미지 리퍼지터리에 있는지 검사할 수 있다.

```bash
$ docker images
REPOSITORY                          TAG                          IMAGE ID
tacocloud/ingredient-service        latest                       7e8ed20e768e
```

- 식자재 서비스 컨테이너를 시작하기에 앞서, 몽고 데이터베이스의 컨테이너를 먼저 시작해야 한다.

```bash
$ docker run --name tacocloud-mongo -d mongo:3.7.9-xenial
```

- 마지막으로 식자재 서비스 컨테이너를 실행하면 된다.
- 이때 방금 시작된 몽고 컨테이너와 연결한다.

```bash
$ docker run -p 8080:8081 \
		--link tacocloud-mongo:mongo \
		tacocloud/ingredient-service
```

- 여기서 docker run 명령은 몇 가지 중요한 매개변수를 갖고 있다.
    - 컨테이너의 스프링 부트 애플리케이션이 8081 포트로 실행되도록 구성했으므로 -p 매개변수에서는 내부 포트를 호스트의 8080 포트로 연결시킨다.
    - --link 매개변수는 이 컨테이너를 tacocloud-mongo 컨테이너와 연결시킨다. 그리고 이때 tacocloud-mongo 컨테이너의 호스트 이름을 mongo로 지정한다. 스프링 데이터가 이 호스트 이름을 사용해서 연결할 수 있도록 하기 위해서다.
    - 마지막으로 새 컨테이너에서 실행되는 이미지의 이름(여기서는 tacocloud/ingredient-service)을 지정한다.
- 이제는 빌드된 도커 이미지를 갖게 되었고 로컬 컨테이너로 실행되는 것을 확인하였다.
- 따라서 해당 이미지를 Dockerhub나 다른 도커 이미지 리퍼지터리에 푸시할 수 있다.
- 만일 Dockerhub에 계정이 있고 로그인하면 다음과 같이 메이븐을 사용해서 해당 이미지를 푸시할 수 있다.

```bash
$ mvnw dockerfile:push
```

- 이때부터는 AWS, 마이크로소프트 Azure, 구글 클라우드 플랫폼을 포함해서 도커 컨테이너를 지원하는 어떤 환경에도 이미지를 배포할 수 있다.