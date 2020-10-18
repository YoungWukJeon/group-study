- 액추에이터는 스프링 부트 애플리케이션의 모니터링이나 메트릭(metric)과 같은 기능을 HTTP와 JMX 엔드포인트(endpoint)를 통해 제공한다.

# 액추에이터 개요

- 실행 중인 애플리케이션의 내부를 볼 수 있게 하고, 어느 정도까지는 애플리케이션의 작동 방법을 제어할 수 있게 한다.
- 액추에이터가 노출하는 엔드포인트를 사용하면 실행 중인 스프링 부트 애플리케이션의 내부 상태에 관한 것을 알 수 있다.
    - 애플리케이션 환경에서 사용할 수 있는 구성 속성들
    - 애플리케이션에 포함된 다양한 패키지의 로깅 레벨(logging level)
    - 애플리케이션이 사용 중인 메모리
    - 지정된 엔드포인트가 받은 요청 횟수
    - 애플리케이션의 건강 상태 정보
- 스프링 부트 애플리케이션에 액추에이터를 활성화하려면 액추에이터의 스타터 의존성을 빌드에 추가해야 한다.

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

- 이처럼 액추에이터 스타터가 프로젝트 빌드의 일부가 되면 애플리케이션에서 여러 가지 액추에이터 엔드포인트를 사용할 수 있다.
- 실행 중인 스프링 부트 애플리케이션의 내부 상태를 볼 수 있는 액추에이터 엔드포인트

| HTTP 메서드 | 경로 | 설명 | 기본적으로 활성화되는가? |
| --- | --- | --- | --- |
| GET | /auditevents | 호출된 감사(audit) 이벤트 리포트를 생성한다. | NO |
| GET | /beans | 스프링 애플리케이션 컨텍스트의 모든 빈을 알려준다. | NO |
| GET | /conditions | 성공 또는 실패했던 자동-구성 조건의 내역을 생성한다. | NO |
| GET | /configprops | 모든 구성 속성들을 현재 값과 같이 알려준다. | NO |
| GET, POST, DELETE | /env | 스프링 애플리케이션에 사용할 수 있는 모든 속성 근원과 이 근원들의 속성을 알려준다. | NO |
| GET | /env/{toMatch} | 특정 환경 속성의 값을 알려준다. | NO |
| GET | /health | 애플리케이션의 건강 상태 정보를 반환한다. | YES |
| GET | /heapdump | 힙(heap) 덤프를 다운로드한다. | NO |
| GET | /httptrace | 가장 최근의 100개 요청에 대한 추적 기록을 생성한다. | NO |
| GET | /info | 개발자가 정의한 애플리케이션에 관한 정보를 반환한다. | YES |
| GET | /loggers | 애플리케이션의 패키지 리스트(각 패키지의 로깅 레벨이 포함된)를 생성한다. | NO |
| GET, POST | /loggers/{name} | 지정된 로거의 로깅 레벨(구성된 로깅 레벨과 유효 로깅 레벨 모두)을 반환한다. 유효 로깅 레벨은 HTTP POST 요청으로 설정할 수 있다. | NO |
| GET | /mappings | 모든 HTTP 매핑과 이 매핑들을 처리하는 핸들러 메서드들의 내역을 제공한다. | NO |
| GET | /metrics | 모든 메트릭 리스트를 반환한다. | NO |
| GET | /metrics/{name} | 지정된 메트릭의 값을 반환한다. | NO |
| GET | /scheduledtasks | 스케쥴링된 모든 태스크의 내역을 제공한다. | NO |
| GET | /threaddump | 모든 애플리케이션 스레드의 내역을 반환한다. | NO |

- /heapdump를 제외한 모든 액추에이터 엔드포인트는 HTTP 기반 엔드포인트에 추가하여 JMX MBeans로도 노출된다.

## 액추에이터의 기본 경로 구성하기

- 기본적으로 모든 엔드포인트의 경로에는 /actuator가 앞에 붙는다.
- 액추에이터의 기본 경로(/actuator)는 management.endpoint.web.base-path 속성을 설정하여 변경할 수 있다.
- management.endpoints.web.base-path 속성을 application.yml 파일에 설정하면 된다.

```yaml
management:
	endpoints:
		web:
			base-path: /management
```

## 액추에이터 엔드포인트의 활성화와 비활성화

- /health와 /info 엔드포인트만 기본적으로 활성화되는 것을 알 수 있다.
- 대부분의 액추에이터 엔드포인트는 민감한 정보를 제공하므로 보안 처리가 되어야 하기 때문이다.
- 물론 스프링 시큐리티를 사용해서 액추에이터를 보안 처리할 수 있다.
- 그러나 액추에이터 자체로는 보안 처리가 되어 있지 않으므로 대부분의 엔드포인트가 기본적으로 비활성화되어 있다.
- 엔드포인트의 노출 여부를 제어할 때는 management.endpoints.web.exposure.include와 management.endpoints.web.exposure.exclude 구성 속성을 사용할 수 있다.
- management.endpoints.web.exposure.include 속성을 사용하면 노출을 원하는 엔드포인트를 지정할 수 있다.

```yaml
management:
	endpoints:
		web:
			exposure:
				include: health,info,beans,conditions
```

- management.endpoints.web.exposure.include 속성은 와일드카드인 별표(*)도 허용한다.
- 이것은 모든 액추에이터 엔드포인트가 노출되어야 한다는 것을 나타낸다.

```yaml
management:
	endpoints:
		web:
			exposure:
				include: '*'
```

- 만일 일부 엔드포인트를 제외한 모든 엔드포인트를 노출하고 싶다면, 와일드카드로 모든 엔드포인트를 포함시킨 후 일부만 제외하면 쉽게 할 수 있다.

```yaml
management:
	endpoints:
		web:
			exposure:
				include: '*'
				exclude: threaddump,heapdump
```

- 기본적으로 활성화되는 /health와 /info 외에 더 많은 다른 스프링 엔드포인트를 노출해야 한다면 다른 엔드포인터의 접근을 제한하기 위해 스프링 시큐리티를 구성하는 것이 좋다.

# 액추에이터 엔드포인트 소비하기

- 액추에이터는 HTTP 엔드포인트이므로 다른 REST API처럼 브라우저 기반의 자바스크립트 애플리케이션 또는 명령행에서 curl을 클라이언트로 사용하여 소비할 수 있다.
- 액추에이터가 제공하는 엔드포인트에는 어떤 것들이 있는지 알아보기 위해 액추에이터의 기본 경로에 대해 GET 요청을 하면 각 엔드포인트의 HATEOAS 링크를 응답으로 받을 수 있다.

```bash
$ curl localhost:8081/actuator
{
	"_links": {
		"self": {
			"href": "http://localhost:8081/actuator",
			"templated": false
		},
		"auditevents": {
			"href": "http://localhost:8081/actuator/auditevents",
			"templated": false
		},
		"beans": {
			"href": "http://localhost:8081/actuator/beans",
			"templated": false
		},
		"health": {
			"href": "http://localhost:8081/actuator/health",
			"templated": false
		},
		...
	}
}
```

- 이처럼 액추에이터의 기본 경로로부터 반환된 링크들이 액추에이터가 제공하는 엔드포인트를 나타낸다.

## 애플리케이션 기본 정보 가져오기

- 액추에이터 /info와 /health 엔드포인트가 응답해 주는 것이 바로 이와 동일한 기본적인 질문에 대한 것이다.
- /info 엔드포인트는 애플리케이션에 관해 알려주고, /health 엔드포인트는 애플리케이션의 건강 상태를 알려준다.

### 애플리케이션에 관한 정보 요구하기

```bash
$ curl localhost:8081/actuator/info
{}
```

- 괄호({}) 속의 응답을 보면 아무 것도 없다.
- /info 엔드포인트가 반환하는 정보를 제공하는 방법은 몇 가지가 있다.
- 이중 이름이 info. 으로 시작하는 하나 이상의 구성 속성을 생성하는 것이 가장 쉬운 방법이다.

```yaml
info:
	contact:
		email: support@tacocloud.com
		phone: 822-625-6831
```

- info.contact.email 속성이나 info.contact.phone 속성 모두 스프링 부트나 애플리케이션 컨텍스트의 다른 빈(bean)에 특별한 의미를 주는 것은 아니다.
- 그러나 속성 이름이 info. 으로 시작하므로 이제는 /info 엔드포인트가 다음과 같이 응답한다.

```json
{
	"contact": {
		"email": "support@tacocloud.com",
		"phone": "822-625-6381"
	}
}
```

### 애플리케이션의 건강 상태 살펴보기

- /health 엔드포인트에 HTTP GET 요청을 하면 애플리케이션의 건강 상태 정보를 갖는 간단한 JSON 응답을 받는다.

```bash
$ curl localhost:8080/actuator/health
{"status":"UP"}
```

- 애플리케이션이 UP이라고 알려주는 것이 뭐가 유용한 것인지 의아할 수 있다.
- 여기에 나타난 상태는 하나 이상의 건강 지표(health indicator)를 종합한 상태다.
- 건강 지표는 애플리케이션의 상호 작용하는 외부 시스템(예를 들어, 데이터베이스, 메시지 브로커, 유레카나 구성 서버와 같은 스프링 클라우드 컴포넌트)의 건강 상태를 나타낸다.
- 각 지표의 건강 상태는 다음 중 하나가 될 수 있다.
    - UP: 외부 시스템이 작동 중(up)이고 접근 가능하다.
    - DOWN: 외부 시스템이 작동하지 않거나(down) 접근할 수 없다.
    - UNKNOWN: 외부 시스템의 상태가 분명하지 않다.
    - OUT_OF_SERVICE: 외부 시스템에 접근할 수 있지만, 현재는 사용할 수 없다.
- 모든 건강 지표의 건강 상태는 다음 규칙에 따라 애플리케이션의 전체 건강 상태로 종합된다.
    - 모든 건강 지표가 UP이면 애플리케이션의 건강 상태도 UP
    - 하나 이상의 건강 지표가 DOWN이면 애플리케이션의 건강 상태도 DOWN
    - 하나 이상의 건강 지표가 OUT_OF_SERVICE이면 애플리케이션의 건강 상태도 OUT_OF_SERVICE
    - UNKNOWN 건강 상태는 무시되며, 애플리케이션의 종합 건강 상태에 고려되지 않는다.
- 기본적으로 /health 엔드포인트의 요청 응답으로는 종합된 건강 상태만 반환된다.
- 그러나 management.endpoint.health.show-details 속성을 application.yml 파일에 구성하여 모든 건강 지표를 자세하게 볼 수 있다.

```yaml
management:
	endpoint:
		health:
			show-details: always
```

- management.endpoint.health.show-details 속성의 기본값은 never다.
- 그러나 모든 건강 지표의 상세 내역을 항상 볼 때는 always로 설정하면 된다.
- 또한 when-authorized로 설정하면 요청하는 클라이언트가 완벽하게 인가된 경우에 한해서 상세 내역을 보여준다.
- 예를 들어, 몽고 문서 데이터베이스를 사용하는 서비스의 경우 다음과 같은 응답이 반환될 수 있다.

```json
{
	"status": "UP",
	"details": {
		"mongo": {
			"status": "UP",
			"details": {
				"version": "3.2.2"
			}
		},
		"diskSpace": {
			"status": "UP",
			"details": {
				"total": 499963170816,
				"free": 177284784128,
				"threshold": 10485760
			}
		}
	}
}
```

- 다른 외부 의존성과 무관하게 모든 애플리케이션 diskSpace라는 이름의 파일 시스템 건강 지표를 갖는다.
- diskSpace 건강 지표는 파일 시스템의 건강 상태(UP이길 바라지만)를 나타내며, 빈 공간이 얼마나 남아있는지에 따라 결정된다.
- 만일 사용 가능한 디스크 공간이 한계치 밑으로 떨어지면 DOWN 상태로 알려준다.
- 자동-구성(autoconfiguration)에서는 애플리케이션과 관련된 건강 지표만 /health 엔드포인트의 응답에 나타난다.
- mongo와 diskSpace 건강 지표에 추가하여 스프링 부트는 다른 외부 데이터베이스와 시스템의 건강 지표들도 제공한다.
    - 카산드라(Cassandra)
    - 구성 서버
    - Couchbase
    - 유레카
    - Hystrix
    - JDBC 데이터 소스
    - Elasticsearch
    - InfluxDB
    - JMS 메시지 브로커
    - LDAP
    - 이메일 서버
    - Neo4j
    - Rabbit 메시지 브로커
    - Redis
    - Solr
- 또한, 서드파티 라이브러리들도 자신들의 건강 지표를 제공할 수 있다.

## 구성 상세 정보 보기

### 빈(Bean) 연결 정보 얻기

- 스프링 애플리케이션 컨텍스트를 살펴보는 데 가장 중요한 엔드포인트가 /beans 엔드포인트다.
- 이 엔드포인트는 애플리케이션 컨텍스트의 모든 빈을 나타내는 JSON 문서(빈의 이름, 자바 타입, 주입되는 다른 빈 등)를 반환한다.

```json
{
	"contexts": {
		"application-1": {
			"beans":{
				...,
				"ingredientsController": {
					"aliases": [],
					"scope": "singleton",
					"type": "tacos.ingredients.IngredientsController",
					"resource": "file [/Users/habuma/Documents/Workspaces/
						TacoCloud/ingredient-service/target/classes/tacos/
						ingredients/IngredientController.class]",
					"dependencies": [
						"ingredientRepository"
					]
				},
				...
			},
			"parentId": null
		}
	}
}
```

- 응답의 최상위 요소는 contexts이며, 이것은 애플리케이션에 있는 각 스프링 애플리케이션 컨텍스트의 하위 요소 하나를 포함한다.
- 그리고 각 스프링 애플리케이션 컨테긋트에는 beans 요소가 있으며, 이것은 해당 애플리케이션 컨텍스트에 있는 모든 빈의 상세 정보를 갖는다.

### 자동-구성 내역 알아보기

- /conditions 엔드포인트에서 반환된 자동-구성 내역은 세 부분을 나뉜다.
- 긍정 일치(positive matches, 성공한 조건부 구성), 부정 일치(negative matches, 실패한 조건부 구성), 조건 없는 클래스(unconditional classes)다.

```json
{
	"contexts": {
		"application-1": {
			"positiveMatches": {
				...,
				"MongoDataAutoConfigurationTemplate": [
					{
						"condition": "OnBeanCondition",
						"message": "@ConditionalOnMissingBean (types:
							org.springframework.data.mongodb.core.MongoTemplate;
							SearchStrategy: all) did not find any beans"
					}
				],
				...
			},
			"negativeMatches": {
				...,
				"DispatcherServletAutoConfiguration": {
					"notMatched": [
						{
							"condition": "OnClassCondition",
							"message": "@ConditionalOnClass did not find required
								class 'org.springframework.web.servlet.DispatcherServlet'"
						}
					],
					"matched": []
				},
				...
			},
			"unconditionalClasses": [
				...,
				"org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration",
				...
			]
		}
	}
}
```

- 이 자동-구성에는 @ConditionalOnMissingBean 애노테이션이 포함되어 있다.
- 이 애노테이션은 해당 빈이 구성되지 않았다면 구성되게 한다.
- 여기서는 MongoTemplate 타입의 빈이 없으므로 자동-구성이 수행되어 하나를 구성한 것이다.
- negativeMatches에서는 스프링 부트 자동-구성이 DispatcherServlet을 구성하려고 했지만, DispatcherServlet을 찾을 수 없어서 조건부 애노테이션인 @ConditionalOnClass가 실패하였다는 것을 보여준다.
- 마지막으로, unconditionalClasses 아래에 있는 ConfigurationPropertiesAutoConfiguration 빈은 조건 없이 구성되었다.
- 구성 속성들은 스프링 부트의 작동에 기본이 되는 것이므로 구성 속성에 관련된 모든 구성은 조건 없이 자동-구성되어야 하기 때문이다.

### 환경 속성과 구성 속성 살펴보기

- /env 엔드포인트에 GET 요청을 하면, 스프링 애플리케이션에 적용 중인 모든 속성 근원의 속성들을 포함하는 다소 긴 응답을 받는다.
- 여기에는 환경 변수, JVM 시스템 속성, application.properties와 application.yml 파일, 그리고 스프링 클라우드 구성 서버(애플리케이션이 구성 서버의 클라이언트일 때)의 속성까지도 포함된다.

```bash
$ curl localhost:8081/actuator/env | jq
{
	"activeProfiles": [
		"development"
	],
	"propertySources": [
		...,
		{
			"name": "systemEnvironment",
			"properties": {
				"PATH": {
					"value": "/usr/bin:/bin:/usr/sbin:/sbin",
					"origin": "System Environment Property \"PATH\""
				},
				"HOME": {
					"value": "/Users/habuma",
					"origin": "System Environment Property \"HOME\""
				}
			}
		},
		{
			"name": "applicationConfig: [classpath:/application.yml]",
			"properties" {
				"spring.application.name": {
					"value": "ingredient-service",
					"origin": "class path resource [application.yml]:3:11"
				},
				"server.port": {
					"value": 8081,
					"origin": "class path resource [application.yml]:9:9"
				},
				...
			}
		},
		...
	]
}
```

- application.yml 파일을 참조하는 applicationConfig 속성 근원의 경우에는 각 속성의 origin 필드에서 해당 속성이 설정된 위치(application.yml 파일 내부의 행과 열 번호)를 정확하게 알려준다.
- /env 엔드포인트는 특정 속성을 가져오는 데도 사용할 수 있으며, 이때는 해당 속성의 이름을 /env 엔드포인트 경로의 두 번째 요소로 지정하면 된다.

```bash
$ curl localhost:8081/actuator/env/server.port | jq
{
	"property": {
		"source": "systemEnvironment",
		"value": "8081"
	},
	"activeProfiles": [ "development" ],
	"propertySources": [
		{ "name": "server.ports" },
		{ "name": "mongo.ports" },
		{ "name": "systemProperties" },
		{ "name": "systemEnvironment",
			"property": {
				"value": "8081",
				"origin": "System Environment Property \"SERVER_PORT\""
			}
		},
		{ "name": "random" },
		{ "name": "applicationConfig: [classpath:/application.yml]",
			"property": {
				"value": 0,
				"origin": "class path resource [application.yml]:9:9"
			}
		},
		{ "name": "springCloudClientHostInfo" },
		{ "name": "refresh" },
		{ "name": "defaultProperties" },
		{ "name": "Management Server" }
	]
}
```

- /env 엔드포인트는 속성 값을 읽는 것은 물론 설정하는 데도 사용될 수 있다.
- 즉, name과 value 필드를 갖는 JSON 문서를 지정한 POST 요청을 /env 엔드포인트에 제출하면 실행 중인 애플리케이션의 속성을 설정할 수 있다.

```bash
$ curl localhost:8081/actuator/env \
	-d'{"name":"tacocloud.discount.code","value":"TACOS1234"}' \
	-H"Content-type: application/json"
{"tacocloud.discount.code":"TACOS1234"}
```

- 이 경우 새로 설정된 속성과 이것의 값이 괄호({}) 안에 응답으로 반환한다.
- 또한, 향후에 이 속성이 필요 없을 때는 /env 엔드포인트에 DELETE 요청을 하여 이 엔드포인트를 통해 성성되었던 속성들을 삭제할 수 있다.
- 삭제된 속성은 괄호({}) 안의 응답으로 알려준다.

```bash
$ curl localhost:8081/actuator/env -X DELETE
{"tacocloud.discount.code":"TACOS1234"}
```

### HTTP 요청-매핑 내역 보기

- 스프링 MVC(그리고 스프링 WebFlux)의 프로그래밍 모델은 HTTP 요청을 쉽게 처리한다.
- 요청-매핑을 해주는 애노테이션을 메서드에 지정만 하면 되기 때문이다.
    - HTTP 요청과 이 요청을 처리하는 핸들러(메서드)를 연결하는 것을 요청-매핑이라고 한다.
- 액추에이터의 /mappings 엔드포인트는 애플리케이션의 모든 HTTP 요청 핸들러(스프링 MVC 컨트롤러에 있거나 액추에이터 자신의 엔드포인트 중 하나에 있는 것) 내역을 제공한다.

```bash
$ curl localhost:8081/actuator/mappings | jq
{
	"contexts": {
		"application-1": {
			"mappings": {
				"dispatcherHandles": {
					"webHandlers": [
						...,
						{
							"predicate": "{[/ingredients],methods=[GET]}",
							"handler": "public
								reactor.core.publisher.Flux<tacos.ingredients.Ingredient>
								tacos.ingredients.IngredientsController.allIngredients()",
							"details": {
								"handlerMethod": {
									"className": "tacos.ingredients.IngredientsController",
									"name": "allIngredients",
									"descriptor": "()Lreactor/core/publisher/Flux;"
								},
								"handlerFunction": null,
								"requestMappingConditions": {
									"consumes": [],
									"headers": [],
									"methods": [
										"GET",
									],
									"params": [],
									"patterns": [
										"/ingredients"
									],
									"produces": []
								}
							}
						},
						...
					]
				}
			},
			"parentId": "application-1"
		},
		"bootstrap": {
			"mappings": {
				"dispatcherHandlers": {}
			},
			"parentId": null
		}
	}
}
```

### 로깅 레벨 관리하기

- 일반적으로 로깅 레벨은 패키지 단위로 적용된다.
- 실행 중인 애플리케이션에 어떤 로깅 레벨이 설정되었는지 궁금하다면 /loggers 엔드포인트에 GET 요청을 할 수 있다.

```json
{
	"levels": [ "OFF", "ERROR", "WARN", "INFO", "DEBUG", "TRACE" ],
	"loggers": {
		"ROOT": {
			"configuredLevel": "INFO",
			"effectiveLevel": "INFO",
		},
		...,
		"org.springframework.web": {
			"configuredLevel": null,
			"effectiveLevel": "INFO",
		},
		...,
		"tacos": {
			"configuredLevel": null,
			"effectiveLevel": "INFO",
		},
		"tacos.ingredients": {
			"configuredLevel": null,
			"effectiveLevel": "INFO",
		},
		"tacos.ingredients.IngredientServiceApplication": {
			"configuredLevel": null,
			"effectiveLevel": "INFO",
		}
	}
}
```

- configuredLevel 속성은 명시적으로 구성된 로깅 레벨(또는 명시적으로 구성되지 않았다면 null)을 보여준다.
- effectiveLevel 속성은 부모 패키지나 루트 로거로부터 상속받을 수 있는 유효 로깅 레벨을 제공한다.
- 만일 특정 패키지의 로깅 레벨을 알고 싶다면 해당 패키지의 이름을 /loggers 엔드포인트 경로의 두 번째 요소로 지정하면 된다.

```json
{
	"configuredLevel": null,
	"effectiveLevel": "INFO",
}
```

- 애플리케이션 패키지의 로깅 레벨을 반환하는 것 외에도 /loggers 엔드포인트는 POST 요청을 통해 configured 로깅 레벨을 변경할 수 있게 해준다.

```bash
$ curl localhost:8081/actuator/loggers/tacos/ingredients \
		-d'{"configuredLevel":"DEBUG"}' \
		-H"Content-type: application/json"
```

```bash
{
	"configuredLevel": "DEBUG",
	"effectiveLevel": "DEBUG",
}
```

- 이처럼 configuredLevel을 변경하면 effectiveLevel도 같이 변경된다는 것에 유의하자.

## 애플리케이션 활동 지켜보기

- 애플리케이션이 처리하는 HTTP 요청이나 애플리케이션에 있는 모든 스레드의 작동을 포함해서 실행 중인 애플리케이션의 활동(activity)을 지켜보는 것은 유용하다.
- 이것을 위해 액추에이터는 /httptrace, /threaddump, /heapdump 엔드포인트를 제공한다.
- /heapdump 엔드포인트는 상세하게 나타내기 가장 어려운 액추에이터 엔드포인트일 것이다.
- 간략히 말해서, 이 엔드포인트는 메모리나 스레드 문제를 찾는 데 사용할 수 있는 gzip 압축 형태의 HPROF 힙 덤프 파일을 다운로드한다.

### HTTP 요청 추적하기

- /httptrace 엔드포인트는 애플리케이션이 처리한 가장 최근의 100개 요청을 알려주며, 다음 내용이 포함된다.
- HTTP 요청 메서드와 경로, 요청이  처리된 시점을 나타내는 타임스탬프, 요청과 응답 모두의 헤더들, 요청 처리 소요 시간 등이다.

```json
{
	"traces": [
		{
			"timestamp": "2018-06-03T23:41:24.494Z",
			"principal": null,
			"session": null,
			"request": {
				"method": "GET",
				"uri": "http://localhost:8081/ingredients",
				"headers": {
					"Host": ["localhost:8081"],
					"User-Agent": ["curl/7.54.0"],
					"Accept": ["*/*"]
				},
				"remoteAddress": null
			},
			"response": {
				"status": 200,
				"headers": {
					"Content-Type": ["application/json;charset=UTF-8"]
				}
			},
			"timeTaken": 4
		},
		...
	]
}
```

### 스레드 모니터링

- HTTP 요청 추적에 추가하여 실행 중인 애플리케이션에서 무슨 일이 생기는지 결정하는 데 스레드의 활동이 유용할 수 있다.
- /threaddump 엔드포인트는 현재 실행 중인 스레드에 관한 스냅샷을 제공한다.

```json
{
	"threadName": "reactor-http-nio-8",
	"threadId": 338,
	"blockedTime": -1,
	"blockedCount": 0,
	"waitedTime": -1,
	"waitedCount": 0,
	"lockName": null,
	"lockOwnerId": -1,
	"lockOwnerName": null,
	"inNative": true,
	"suspended": false,
	"threadState": "RUNNABLE",
	"stackTrace": [
		{
			"methodName": "kevent0",
			"fileName": "KQueueArrayWrapper.java",
			"lineNumber": -2,
			"className": "sun.nio.ch.KQueueArrayWrapper",
			"nativeMethod": true
		},
		{
			"methodName": "poll",
			"fileName": "KQueueArrayWrapper.java",
			"lineNumber": 198,
			"className": "sun.nio.ch.KQueueArrayWrapper",
			"nativeMethod": false
		},
		...
	],
	"lockedMonitors": [
		{
			"className": "io.netty.channel.nio.SelectedSelectionKeySet",
			"identityHashCode": 1039768944,
			"lockedStackDepth": 3,
			"lockedStackFrame": {
				"methodName": "lockAndDoSelect",
				"fileName": "SelectorImpl.java",
				"lineNumber": 86,
				"className": "sun.nio.ch.SelectorImpl",
				"nativeMethod": false
			}
		},
		...
	],
	"lockedSynchronizers": [],
	"lockInfo": null
}
```

- 완전한 스레드 덤프 응답은 실행 중인 애플리케이션의 모든 스레드를 포함한다.
- 스레드 덤프에는 스레드의 블로킹과 록킹 상태의 관련 상세 정보와 스택 기록 등이 포함된다.
- /threaddump 엔드포인트는 요청 시점의 스레드 활동에 대한 스냅샷만 제공하므로, 스레드의 지속적인 모든 활동을 알기 어렵다.

## 런타임 메트릭 활용하기

- /metrics 엔드포인트는 실행 중인 애플리케이션에서 생성되는 온갖 종류의 메트릭(metric)을 제공할 수 있으며, 여기에는 메모리, 프로세스, 가비지 컬렉션(garbage collection), HTTP 요청 관련 메트릭 등이 포함된다.

```bash
$ curl localhost:8081/actuator/metrics | jq
{
	"names": [
		"jvm.memory.max",
		"process.files.max",
		"jvm.gc.memory.promoted",
		"http.server.requests",
		"system.load.average.1m",
		"jvm.memory.used",
		"jvm.gc.max.data.size",
		"jvm.memory.comitted",
		"system.cpu.count",
		"logback.events",
		"jvm.buffer.memory.used",
		"jvm.threads.daemon",
		"system.cpu.usage",
		"jvm.gc.memory.allocated",
		"jvm.threads.live",
		"jvm.threads.peak",
		"process.uptime",
		"process.cpu.usage",
		"jvm.classes.loaded",
		"jvm.gc.pause",
		"jvm.classes.unloaded",
		"jvm.gc.live.data.size",
		"process.files.open",
		"jvm.buffer.count",
		"jvm.buffer.total.capacity",
		"process.start.time",
	]
}
```

- 단순히 /metrics를 요청하는 대신에 /metrics/{메트릭 종류}에 GET 요청을 하면 해당 종류의 메트릭에 관한 더 상세한 정보를 받을 수 있다.
- http.server.requests의 경우는 /metrics/http.server.requests에 GET 요청을 하여 다음과 같은 데이터를 받을 수 있다.

```bash
$ curl localhost:8081/actuator/metrics/http.server.requests
{
	"name": "http.server.requests",
	"measurements": [
		{ "statistic": "COUNT", "value": 2103 },
		{ "statistic": "TOTAL_TIME", "value": 18.086334315 },
		{ "statistic": "MAX", "value": 0.028926313 }
	],
	"availableTags": [
		{ "tag": "exception",
			"values": [ "ResponseStatusException", "IllegalArgumentException", "none" ] },
		{ "tag": "method", "values": [ "GET" ] },
		{ "tag": "uri",
			"values": [
				"/actuator/metrics/{requiredMetricName}",
				"/actuator/health", "/actuator/info", "/ingredients",
				"/actuator/metrics", "/**" ] },
		{ "tag": "status", "values": [ "404", "500", "200" ] }
	]
}
```

- 이 응답에서 가장 중요한 부분은 measurements이며, 이것은 요청된 메트릭 종류에 속하는 모든 메트릭을 포함한다.
- 여기서는 2,103개의 HTTP 요청이 있었다고 알려준다.
- 그리고 이 요청들을 처리하는 데 소요된 전체 시간은 18.086334315초이며, 요청 처리에 소요된 최대 시간은 0.028926313초라는 것도 알려준다.
- 그러나 measurements에서는 2,103개의 요청이 있다는 것을 알 수 있지만, HTTP 200이나 HTTP 404 또는 HTTP 500 응답 상태를 초래한 요청이 각각 몇 개인지는 모른다.
- 이때는 availableTags 아래의 status 태그를 사용해서 해당 응답 상태를 초래한 모든 요청의 메트릭을 얻을 수 있다.

```bash
$ curl localhost:8081/actuator/metrics/http.server.requests?tag=status:404
{
	"name": "http.server.requests",
	"measurements": [
		{ "statistic": "COUNT", "value": 31 },
		{ "statistic": "TOTAL_TIME", "value": 0.552061212 },
		{ "statistic": "MAX", "value": 0 }
	],
	"availableTags": [
		{ "tag": "exception",
			"values": [ "ResponseStatusException", "none" ] },
		{ "tag": "method", "values": [ "GET" ] },
		{ "tag": "uri",
			"values": [
				"/actuator/metrics/{requiredMetricName}", "/**" ] }
	]
}
```

- 이처럼 tag 요청 속성에 태그 이름과 값을 지정하면, HTTP 404 응답을 초래했던 요청들의 메트릭을 보게 된다.
- 실패한 요청 타입은 GET이었으며, 경로는 /actuator/metrics/{requiredMetricsName}와 /**(와일드카드 경로) 두 가지였음을 알려준다.
- 그렇다면 HTTP 404 응답을 초래한 /** 경로의 요청이 몇 개인지 알고 싶으면 어떻게 하면 될까?
- 이때는 다음과 같이 uri 태그를 추가로 지정하면 된다.

```bash
$ curl "localhost:8081/actuator/metrics/http.server.requests?tag=status:404&tag=uri:/**"
{
	"name": "http.server.requests",
	"measurements": [
		{ "statistic": "COUNT", "value": 30 },
		{ "statistic": "TOTAL_TIME", "value": 0.519791548 },
		{ "statistic": "MAX", "value": 0 }
	],
	"availableTags": [
		{ "tag": "exception", "values": [ "ResponseStatusException" ] },
		{ "tag": "method", "values": [ "GET" ] },
	]
}
```

# 액추에이터 커스터마이징

- 액추에이터의 가장 큰 특징 중 하나는 애플리케이션의 특정 요구를 충족하기 위해 커스터마이징할 수 있다는 것이다.
- 즉 커스텀 엔드포인트를 생성할 수 있다.

## /info 엔드포인트에 정보 제공하기

- /info 엔드포인트는 처음에 아무 정보도 제공하지 않는다.
- 그러나 info. 으로 시작하는 속성을 생성하면 쉽게 커스텀 데이터를 추가할 수 있다.
- 스프링 부트는 InfoContributor라는 인터페이스를 제공하며, 이 인터페이스는 우리가 원하는 어떤 정보도 /info 엔드포인트 응답에 추가할 수 있다.
    - 스프링 부트에서는 두 개의 InfoContributor 구현체(클래스)도 제공한다.

### 커스텀 정보 제공자 생성하기

```java
package tacos.tacos;

import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.actuate.info.Info.Builder;

@Component
public class TacoCountInfoContributor implements InfoContributor {
	private TacoRepository tacoRepo;

	public TacoCountInfoContributor(TacoRepository tacoRepo) {
		this.tacoRepo = tacoRepo;
	}

	@Override
	public void contribute(Builder builder) {
		long tacoCount = tacoRepo.count();
		Map<String, Object> tacoMap = new HashMap<String, Object>();
		tacoMap.put("count", tacoCount);
		builder.withDetail("taco-stats", tacoMap);
	}
}
```

- TacoCountInfoContributor 클래스는 InfoContributor의 contribute() 메서드를 구현해야 한다.
- 이 메서드에서는 TacoRepository로부터 타코 개수를 알아낸 후 인자로 받은 Builder 객체의 withDetail()을 호출하여 타코 개수 정보를 /info 엔드포인트에 추가한다.
- 생성된 타코 개수는 TacoRepository의 count() 메서드를 호출하여 알 수 있으며, 이것을 Map에 저장한다.
- 그리고 이 Map과 이것의 라벨인 taco-stats를 withDetails() 메서드의 인자로 전달하여 /info 엔드포인트에 추가한다.

```json
{
	"taco-stats": {
		"count": 44
	}
}
```

### 빌드 정보를 /info 엔드포인트에 주입하기

- 스프링 부트에는 /info 엔드포인트 응답에 자동으로 정보를 추가해 주는 몇 가지 InfoContributor 구현체가 포함되어 있다.
- 이중에 BuildInfoContributor가 있는데 이것은 프로젝트 빌드 파일의 정보를 /info 엔드포인트 응답에 추가해 준다.
- 여기에는 프로젝트 버전, 빌드 타임스탬프, 빌드를 수행했던 호스트와 사용자 등의 기본 정보가 포함된다.
- /info 엔드포인트 응답에 포함할 빌드 정보를 활성화하려면 다음과 같이 build-info goal을 스프링 부트 메이븐 플러그인 execution에 추가하면 된다.

```xml
<build>
	<plugins>
		<plugin>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-maven-plugin</artifactId>
			<executions>
				<execution>
					<goals>
						<goal>build-info</goal>
					</goals>
				</execution>
			</executions>
		</plugin>
	</plugins>
</build>
```

- 그래들을 사용해서 프로젝트를 빌드할 때는 다음을 build.gradle 파일에 추가하면 된다.

```groovy
springBoot {
	buildInfo()
}
```

- 메이븐이나 그래들 중 어떤 방법을 사용하든 빌드가 끝나면 배포 가능한 JAR나 WAR 파일에 build-info.properties 파일이 생성된다.
- 그리고 애플리케이션이 실행될 때 이 파일을 BuildInfoContributor가 사용하여 빌드 정보를 /info 엔드포인트 응답에 추가하게 된다.

```json
{
	"build": {
		"version": "0.0.16-SNAPSHOT",
		"artifact": "ingredient-service",
		"name": "ingredient-service",
		"group": "sia5",
		"time": "2018-06-04T00:24:04.373Z"
	}
}
```

- 어떤 버전의 애플리케이션이 실행 중이고, 언제 빌드되었는지 정확하게 알고자 할 때 이 정보가 유용하다.

### Git 커밋 정보 노출하기

- Git 커밋 정보를 /info 엔드포인트에 포함하고 싶을 수 있다.
- 이때는 메이븐 프로젝트의 pom.xml 파일에 다음의 플러그인을 추가해야 한다.

```xml
<build>
	<plugins>
		...
		<plugin>
			<groupId>pl.project13.maven</groupId>
			<artifactId>git-commit-id-plugin</artifactId>
		</plugin>
	</plugins>
</build>
```

- 그래들의 경우도 문제없다.
- 이와 동일한 플러그인을 build.gradle 파일에 추가하면 된다.

```groovy
plugins {
	id "com.gorylenko.gradle-git-properties" version "1.4.17"
}
```

- 두 가지 플러그인 모두 기본적으로 같은 일을 수행한다.
- 즉, 프로젝트의 모든 Git 메타데이터를 포함하는 git.properties 이라는 파일을 빌드 시점에 생성한다.
- 그리고 애플리케이션이 실행될 때 스프링 부트에 특별히 구현된 InfoContributor에서 해당 파일을 찾아서 /info 엔드포인트 응답의 일부로 파일 내용을 노출시킨다.
- /info 엔드포인트 응답에 나타나는 Git 정보에는 Git 분기와 커밋 정보 등이 포함된다.

```json
{
	"git": {
		"commit": {
			"time": "2018-06-02T18:10:58Z",
			"id": "b5c104d"
		},
		"branch": "master"
	},
	...
}
```

- 이 정보는 프로젝트가 빌드된 시점의 코드 상태를 간단하게 나타낸다.
- 그러나 management.info.git.mode 속성을 full로 설정하면 프로젝트가 빌드된 시점의 Git 커밋에 관한 굉장히 상세한 정보를 얻을 수 있다.

```yaml
management:
	info:
		git:
			mode: full
```

```yaml
{
	"git": {
		"build": {
			"host": "DarkSide.local",
			"version": "0.0.16-SNAPSHOT",
			"time": "2018-06-02T18:11:23Z",
			"user": {
				"name": "Craig Walls",
				"email": "craig@habuma.com"
			}
		},
		"branch": "master",
		"commit": {
			"message": {
				"short": "Add Spring Boot Admin and Actuator",
				"full": "Add Spring Boot Admin and Actuator"
			},
			"id": {
				"describe": "b5c104d-dirty",
				"abbrev": "b5c104d",
				"describe-short": "b5c104d-dirty",
				"full": "b5c104d1fcbe6c2b84965ea08a330595100fd44e"
			},
			"time": "2018-06-02T18:10:58Z",
			"user": {
				"name": "Craig Walls",
				"email": "craig@habuma.com"
			}
		},
		"closest": {
			"tag": {
				"name": "",
				"commit": {
					"count": ""
				}
			}
		},
		"dirty": "true",
		"remote": {
			"origin": {
				"url": "Unknown"
			}
		},
		"tags": ""
	},
	...
}
```

- 여기서는  타임스탬프와 요약된 Git 커밋 해시에 추가하여, 코드를 커밋했던 사용자의 이름과 이메일은 물론 커밋 메시지와 이외의 다른 정보를 포함한다.
- 따라서 프로젝트 빌드에 사용되었던 코드가 어떤 것인지 정확하게 알 수 있다.
- 그리고 dirty 필드를 보면 값이 true인 것을 알 수 있다.
- 이것은 프로젝트가 빌드되었을 당시에 빌드 디렉터리에 커밋되지 않은 일부 변경 사항이 있었음을 나타낸다.

## 커스텀 건강 지표 정의하기

- 스프링 부트에는 몇 가지 전강 지표가 포함되어 있으며, 이 건강 지표들은 스프링 애플리케이션에 통합할 수 있는 많은 외부 시스템의 건강 상태 정보를 제공한다.
- 그러나 때로는 스프링 부트에서 지원하지 않거나 건강 지표를 제공하지 않는 외부 시스템을 사용하는 경우가 생길 수 있다.
- 여기서는 시간에 따라 무작위로 건강 상태가 결정되는 HealthIndicator의 간단한 구현 예를 보여준다.

```java
package tacos.tacos;

import java.util.Calendar;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class WackoHealthIndicator implements HealthIndicator {
	@Override
	public Health health() {
		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		if (hour > 12) {
			return Health
				.outOfService()
				.withDetail("reason", "I'm out of service after lunchtime")
				.withDetail("hour", hour)
				.build();
		}

		if (Math.random() < 0.1) {
			return Health
				.down()
				.withDetail("reason", "I break 10% of the time")
				.build();
		}

		return Health
			.up()
			.withDetail("reason", "All is good!")
			.build();
	}
}
```

## 커스텀 메트릭 등록하기

- 궁극적으로 액추에이터 메트릭은 Micrometer( [https://micrometer.io/](https://micrometer.io/) )에 의해 구현된다.
- 이것은 벤더 중립적인 메트릭이며, 애플리케이션이 원하는 어떤 메트릭도 발행하여 서드파티 모니터링 시스템(예들 들어, Prometheus, Datadog, New Relic 등)에서 보여줄 수 있게 한다.
- Micrometer로 메트릭을 발행하는 가장 기본적인 방법은 Micrometer의 MeterRegistry를 사용하는 것이다.
- 스프링 부트 애플리케이션에서 메트릭을 발행할 때는 어디든 필요한 곳(예를 들어, 애플리케이션의 메트릭을 캡쳐하는 카운터, 타이머, 게이지 등)에 MeterRegistry만 주입하면 된다.

```java
package tacos.tacos;

import java.util.List;
import org.springframework.data.rest.core.event.AbstractRepositoryEventListener;
import org.springframework.stereotype.Component;
import io.micrometer.core.instrument.MeterRegistry;

@Component
public class TacoMetrics extends AbstractRepositoryEventListener<Taco> {
	private MeterRegistry meterRegistry;

	public TacoMetrics(MeterRegistry meterRegistry) {
		this.meterRegistry = meterRegistry;
	}

	@Override
	protected void onAfterCreate(Taco taco) {
		List<Ingredient> ingredients = taco.getIngredients();
		for (Ingredient ingredient : ingredients) {
			meterRegistry.counter("tacocloud", "ingredient", ingredient.getId()).increment();
		}
	}
}
```

- TacoMetrics는 리퍼지터리 이벤트를 가로챌 수 있는 스프링 데이터 클래스인 AbstractRepositoryEventListener의 서브 클래스이며, 새로운 Taco 객체가 저장될 때마다 호출되도록 onAfterCreate() 메서드를 오버라이딩한다.
- 그리고 onAfterCreate() 내부에서는 각 식자재에 대해 카운터가 선언되며, 이때 카운터의 태그 이름은 ingredient이고, 태그 값은 식자재 ID와 동일하다.
- 만일 해당 태그가 이미 존재하면 재사용된다.
- 그리고 각 식자재를 갖는 타코가 생성되면 해당 카운터 값이 증가한다.
- TacoMetrics 클래스가 포함된 프로젝트가 빌드된 후 타코를 몇 개 생성하면 식자재별로 /metrics 엔드포인트를 조회할 수 있다.
- 우선, 다음과 같이 /metrics/tacocloud에 GET 요청을 하자.

```bash
$ curl localhost:8087/actuator/metrics/tacocloud
{
	"name": "tacocloud",
	"measurements": [ { "statistic": "COUNT", "value": 84 } ],
	"availableTags": [
		{
			"tag": "ingredient",
			"values": [ "FLTO", "CHED", "LETC", "GRBF", "COTO", "JACK", "TMTO", "SLSA" ]
		}
	]
}
```

- 특정 식자재가 포함된 타코의 개수를 알고 있을 때는 GET 요청을 다르게 해야 한다.

```bash
$ curl localhost:8087/actuator/metrics/tacocloud?tag=ingredient:FLTO
{
	"name": "tacocloud",
	"measurements": [ { "statistic": "COUNT", "value": 39 } ],
	"availableTags": []
}
```

## 커스텀 엔드포인트 생성하기

- 엔드포인트는 HTTP 요청을 처리하는 것은 물론이고 JMX MBeans로도 노출되어 사용될 수 있다.
- 따라서 엔드포인트는 컨트롤러 클래스 이상의 것임이 분명하다.
- 실제로 액추에이터 엔드포인트는 컨트롤러와 매우 다르게 정의된다.
- @Controller나 @RestController 애노테이션으로 지정되는 클래스 대신, 액추에이터 엔드포인트는 @Endpoint로 지정되는 클래스로 정의된다.
- 게다가 @GetMapping, @PostMapping, @DeleteMapping와 같은 HTTP 애노테이션을 사용하는 대신, 액추에이터 엔드포인트 오퍼레이션은 @ReadOperation, @WriteOperation, @DeleteOperation 애노테이션이 지정된 메서드로 정의된다.
- 또한, 이 애노테이션들은 어떤 특정한 통신 메커니즘도 수반하지 않으므로 액추에이터가 다양한 통신 메커니즘(HTTP와 JMX는 기본적으로 가능)으로 통신할 수 있게 한다.

```java
package tacos.ingredients;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.boot.actuate.endpoint.annotation.DeleteOperation;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.stereotype.Component;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Component
@Endpoint(id="notes", enableByDefault=true)
public class NotesEndpoint {
	private List<Note> notes = new ArrayList<>();

	@ReadOperation
	public List<Note> notes() {
		return notes;
	}

	@WriteOperation
	public List<Note> addNote(String text) {
		notes.add(new Note(text));
		return notes;
	}

	@DeleteOperation
	public List<Note> deleteNote(int index) {
		if (index < notes.size()) {
			notes.remove(index);
		}
		return notes;
	}

	@RequiredArgsConstructor
	private class Note {
		@Getter
		private Date time = new Date();

		@Getter
		private final String text;
	}
}
```

- 이 엔드포인트는 간단한 메모 처리 엔드포인트다.
- 따라서 쓰기 오퍼레이션으로 메모를 제출하고, 읽기 오퍼레이션으로 메모 리스트를 읽을 수 있으며, 삭제 오퍼레이션으로 메모를 삭제할 수 있다.
- NotesEndpoint 클래스에는 @Component가 지정되었으므로, 스프링의 컴포넌트 검색으로 시작되고 스프링 애플리케이션 컨텍스트의 빈으로 생성된다.
- 이 클래스에는 또한 @Endpoint가 지정되었다.
- 따라서 ID가 notes인 액추에이터 엔드포인트가 된다.
- 그리고 기본적으로 활성화되었으므로(enableByDefault=true), management.web.endpoints.web.exposure.include 구성 속성에 포함해 활성화하지 않아도 된다.
- NotesEndpoint는 다음과 같은 오퍼레이션들을 제공한다.
    - notes() 메서드에는 @ReadOperation이 지정되었다. 따라서 이 메서드가 호출되면 사용 가능한 메모 List가 반환된다. HTTP에서는 /actuator/notes의 요청을 처리하여 메모의 JSON 리스트를 응답으로 반환한다.
    - addNote() 메서드에는 @WriteOperation이 지정되었다. 따라서 이 메서드가 호출되면 인자로 전달된 텍스트로 새로운 메모를 생성하고 List에 추가한다. HTTP에서는 text 속성을 갖는 JSON 객체인 요청 몸체를 갖는 POST 요청을 처리하고 현재 상태의 메모 List 응답으로 반환한다.
    - deleteNote() 메서드에는 @DeleteOperation이 지정되었다. 따라서 이 메서드가 호출되면 인자로 전달된 인덱스의 메모를 삭제한다.
    - HTTP에서 이 엔드포인트는 요청 매개변수로 인덱스가 지정된 DELETE 요청을 처리한다.

```bash
$ curl localhost:8080/actuator/notes \
		-d'{"text":"Bring home milk"}' \
		-H"Content-type: application/json"
[{"time":"2018-06-08T13:50:45.085+0000","text":"Bring home milk"}]

$ curl localhost:8080/actuator/notes \
		-d'{"text":"Take dry cleaning"}' \
		-H"Content-type: application/json"
[{"time":"2018-06-08T13:50:45.085+0000","text":"Bring home milk"},
 {"time":"2018-06-08T13:50:48.021+0000","text":"Take dry cleaning"}]
```

- 새로운 메모가 제출될 때마다 엔드포인트에서는 새로 추가된 메모 리스트를 응답한다.
- 그러나 나중에 메모 리스트를 볼 때는 간단하게 GET 요청을 하면 된다.

```bash
$ curl localhost:8080/actuator/notes
[{"time":"2018-06-08T13:50:45.085+0000","text":"Bring home milk"},
 {"time":"2018-06-08T13:50:48.021+0000","text":"Take dry cleaning"}]
```

- 만일 메모 중 하나를 삭제하고 싶을 때는 index 요청 매개변수를 갖는 DELETE 요청을 하면 된다.

```bash
$ curl localhost:8080/actuator/notes?index=1 -X DELETE
[{"time":"2018-06-08T13:50:45.085+0000","text":"Bring home milk"}]
```

- 여기서는 HTTP를 사용해서 엔드포인트와 상호 작용하는 방법만 알아보았다.
- 그러나 엔드포인트는 MBeans로 노출될 수도 있으므로 어떤 JMX 클라이언트에서도 이 엔드포인트를 사용할 수 있다.
- 그러나 만일 HTTP  엔드포인트로만 제한하고 싶다면 @Endpoint 대신 @WebEndpoint를 클래스에 지정하면 된다.

```java
@Component
@WebEndpoint(id="notes", enableByDefault=true)
public class NotesEndpoint {
	...
}
```

- 이와는 달리 MBeans 엔드포인트로만 제한하고 싶다면 @JmxEndpoint를 클래스에 지정하면 된다.

# 액추에이터 보안 처리하기

- 액추에이터 자체의 보안이 중요하지만, 보안은 액추에이터의 책임 범위를 벗어난다.
- 대신에 스프링 시큐리티를 사용해서 액추에이터의 보안을 처리해야 한다.
- 그리고 액추에이터 엔드포인트는 단지 애플리케이션의 경로이므로 액추에이터의 보안이라고 해서 특별한 것은 없다.
- 액추에이터 엔드포인트들은 공통의 기본 경로인 /actuator(또는 management.endpoints.web.base-path 속성이 설정된 경우는 이 속성의 값으로 지정된 경로) 아래에 모여 있으므로 모든 액추에이터 엔드포인트 전반에 걸쳐 인증 규칙을 적용하기 쉽다.

```java
@Override
protected void configure(HttpSecurity http) throws Exception {
	http
		.authorizeRequests()
			.antMatchers("/actuator/**").hasRole("ADMIN")
		.and()
		.httpBasic();
}
```

- 이 경우 액추에이터 엔드포인트를 사용하려면 ROLE_ADMIN 권한을 갖도록 인가한 사용자로부터 요청되어야 한다.
- 여기서는 또한 클라이언트 애플리케이션이 요청의 Authorization 헤더에 암호화된 인증 정보를 제출할 수 있도록 HTTP 기본 인증도 구성하였다.
- 이처럼 액추에이터 보안을 처리할 때 유일한 문제점은 엔드포인트의 경로 /actuator/**로 하드코딩되었다는 것이다.
- 따라서 만일 management.endpoints.web.base-path 속성이 변경된다면 엔드포인트의 기본 경로가 바뀌므로 보안이 처리되지 않을 것이다.
- 이런 경우를 고려하여 스프링 부트는 EndpointRequest 클래스도 제공한다.
- 이것은 지정된 문자열 정보에 종속되지 않으면서 보다 더 쉽게 사용할 수 있는 요청 일치 클래스다.

```java
@Override
protected void configure(HttpSecurity http) throws Exception {
	http
		.requestMatcher(EndpointRequest.toAnyEndpoint())
			.authorizeRequests()
				.anyRequest().hasRole("ADMIN")
		.and()
		.httpBasic();
}
```

- EndpointRequest.toAnyEndpoint() 메서드는 어떤 액추에이터 엔드포인트와도 일치하는 요청 matcher(일치된 요청 경로들의 모음)를 반환한다.
- 그리고 요청 matcher로부터 일부 엔드포인트를 제외하고 싶다면 해당 엔드포인트의 이름을 인자로 전달하여 excluding() 메서드를 호출하면 된다.

```java
@Override
protected void configure(HttpSecurity http) throws Exception {
	http
		.requestMatcher(
			EndpointRequest.toAnyEndpoint()
				.excluding("health", "info"))
			.authorizeRequests()
				.anyRequest().hasRole("ADMIN")
		.and()
		.httpBasic();
}
```

- 이와는 달리 일부 액추에이터 엔드포인트에만 보안을 적용하고 싶다면 toAnyEndpoint() 대신 to()를 호출하면 된다.

```java
@Override
protected void configure(HttpSecurity http) throws Exception {
	http
		.requestMatcher(EndpointRequest.to(
			"beans", "threaddump", "loggers"))
			.authorizeRequests()
				.anyRequest().hasRole("ADMIN")
		.and()
		.httpBasic();
}
```