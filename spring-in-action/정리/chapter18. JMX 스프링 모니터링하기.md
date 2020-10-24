- 15년 동안 JMX(Java Management Extensions)는 자바 애플리케이션을 모니터링하고 관리하는 표준 방법으로 사용되고 있다.
- MBeans(managed beans)로 알려진 컴포넌트를 노출함으로써 외부의 JMX 클라이언트는 오퍼레이션(operation) 호출, 속성 검사, MBeans의 이벤트 모니터링을 통해 애플리케이션을 관리할 수 있다.
- JMX는 스프링 부트 애플리케이션에 기본적으로 자동 활성화된다.
- 이에 따라 모든 액추에이터(actuator) 엔드포인트는 MBeans로 노출된다.

# 액추에이터 MBeans 사용하기

- /heapdump를 제외한 모든 액추에이터 엔드포인트가 MBeans로 노출되어 있다.
- 따라서 어떤 JMX 클라이언트(예를 들어, JConsole)를 사용해도 현재 실행 중인 스프링 부트 애플리케이션의 액추에이터 엔드포인트 MBeans와 연결할 수 있다.
- JConsole을 사용하면 org.springframework.boot 도메인 아래에 나타난 액추에이터 엔드포인트 MBeans들을 볼 수 있다.

![chapter18-01](image/chapter18-01.png '액추에이터 엔드포인트는 JMX MBeans로 자동 노출된다.')

- 액추에이터 엔드포인트 MBeans는 HTTP의 경우처럼 명시적으로 포함시킬 필요 없이 기본으로 노출된다는 장점이 있다.
- 그러나 management.endpoints.jmx.exposure.include와 management.endpoints.jmx.exposure.exclude를 설정하여 MBeans로 노출되는 액추에이터 엔드포인트를 선택할 수 있다.

```yaml
management:
	endpoints:
		jmx:
			exposure:
				include: health,info,bean,conditions
```

```yaml
management:
	endpoints:
		jmx:
			exposure:
				exclude: env,metrics
```

- JConsole에서 액추에이터 MBeans 중 하나의 관리용 오페레이션(managed operation)을 호출할 때는 왼쪽 패널 트리의 해당 엔드포인트 MBeans를 확장한 후 Operations 아래의 원하는 오퍼레이션을 선택하면 된다.
- 예를 들어, tacos.ingredients 패키지의 로깅 레벨을 조사하고 싶다면 Loggers MBeans을 확장하고 **loggerLevels** 오퍼레이션을 클릭한다.
- 그리고 오른쪽 위의 Name 필드에 패키지 이름(tacos.ingredients)을 입려갛고 **loggerLevels** 버튼을 클릭한다.

![chapter18-02](image/chapter18-02.png 'JConsole을 사용해서 스프링 부트 애플리케이션의 로깅 레벨 보기')

- **loggerLevels** 버튼을 누르면 /loggers 엔드포인트 MBean의 응답을 보여주는 대화상자가 나타난다.

![chapter18-03](image/chapter18-03.png 'JConsole이 보여주는 /loggers 엔드포인트 MBean의 로깅 레벨')

# 우리의 MBeans 생성하기

- 스프링은 우리가 원하는 어떤 빈(bean)도 JMX MBeans로 쉽게 노출한다.
- 따라서 빈 클래스에 @ManagedResource 애노테이션을 지정하고 메서드에는 @ManagedOperation을, 속성에는 @ManagedAttribute만 지정하면 된다.
- 나머지는 스프링이 알아서 해주기 때문이다.
- 예를 들어, 타코 클라우드 시스템을 통해 주문된 타코의 수량을 추적하는 MBeans를 제공하고 싶다고 하자.

```java
package tacos.tacos;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.data.rest.core.event.AbstractRepositoryEventListener;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

@Service
@ManagedResource
public class TacoCounter extends AbstractRepositoryEventListener<Taco> {
	private AtomicLong counter;

	public TacoCounter(TacoRepository tacoRepo) {
		long initialCount = tacoRepo.count();
		this.counter = new AtomicLong(initialCount);
	}

	@Override
	protected void onAfterCreate(Taco entity) {
		counter.incrementAndGet();
	}

	@ManagedAttribute
	public long getTacoCount() {
		return counter.get();
	}

	@ManagedOperation
	public long increment(long delta) {
		return counter.addAndGet(delta);
	}
}
```

- 여기서는 TacoCounter 클래스에는 @Service 애노테이션이 지정되었으므로 스프링이 컴포넌트를 찾아주며, 이 클래스 인스턴스는 스프링 애플리케이션 컨텍스트의 빈으로 등록된다.
- 또한, 이 빈이 MBean도 된다는 것을 나타내는 @ManagedResource도 지정되었다.
- 그리고 getTacoCounter() 메서드는 @ManagedAttribute가 지정되었으므로 MBeans 속성으로 노출되며, increment() 메서드는 @ManagedOperation이 지정되었으므로 MBeans 오퍼레이션으로 노출된다.

![chapter18-04](image/chapter18-04.png 'TacoCounter MBeans의 오퍼레이션과 속성')

- TacoCounter에는 JMX와는 관련이 없지만 주목할 만한 기능이 있다.
- AbstractRepositoryEventListener의 서브 클래스이므로 Taco 객체가 TacoRepository를 통해 저장될 때 퍼시스턴스 관련 이벤트를 받을 수 있다.
- 즉, 새로운 Taco 객체가 생성되어 리퍼지터리에 저장될 때마다 onAfterCreate() 메서드가 호출되어 카운터를 1씩 증가시킨다.
- 기본적으로 MBeans 오퍼레이션과 속성은 풀(pull) 방식을 사용한다.
- 즉, MBeans 속성의 값이 변경되더라도 자동으로 알려주지 않으므로 JMX 클라이언트를 통해 봐야만 알 수 있다.
- 그러나 MBeans는 JMX 클라이언트에 알림을 푸시(push)할 수 있는 방법이 있다.

# 알림 전송하기

- 스프링 NotificationPublisher를 사용하면 MBeans가 JMX 클라이언트에 알림을 푸시할 수 있다.
- NotificationPublisher는 하나의 sendNotification() 메서드를 갖는다.
- 이 메서드는 Notification 객체를 인자로 받아서 MBean을 구독하는 JMX 클라이언트에게 발행(전송)한다.
- MBeans가 알림을 발행하려면 NotificationPublisherAware 인터페이스의 setNotificationPublisher() 메서드를 구현해야 한다.
- 예를 들어, 100개의 타코가 생성될 때마다 알림을 전송하고 싶다고 하자.

```java
@Service
@ManagedResource
public class TacoCounter extends AbstractRepositoryEventListener<Taco> implements NotificationPublisherAware {
	private AtomicLong counter;
	private NotificationPublisher np;
	...

	@Override
	public void setNotificationPublisher(NotificationPublisher np) {
		this.np = np;
	}
	...

	@ManagedOperation
	public long increment(long delta) {
		long before = counter.get();
		long after = counter.addAndGet(delta);
		if ((after / 100) > (before / 100)) {
			Notification notification = new Notification("taco.count", this, before, after + "th taco created!");
			np.sendNotification(notification);
		}

		return after;
	}
}
```

- 이 경우 JMX 클라이언트에서 알림을 받으려면 TacoCounter MBeans를 구독해야 한다.
- 알림은 애플리케이션이 자신을 모니터링하는 클라이언트에게 능동적으로 데이터를 전송하여 알려주는 좋은 방법이다.
- 따라서 클라이언트가 지속적으로 반복해서 관리 속성을 조회하거나 관리 오퍼레이션을 호출할 필요가 없다.

# TacoCounter MBeans 빌드 및 사용하기

```bash
$ curl localhost:8081/tacos \
	-H"Content-type: application/json" \
	-d'{"name":"TEST TACO-01", "ingredients":[{"id":"FLTO","name":"Flour Tortilla"},
	{"id":"LETC","name":"Lettuce"}, {"id":"GRBF","name":"Ground Beef"}]}'
```

```json
{
	"name": "TEST TACO-01",
	"createdAt": "2020-04-03T09:55:45.955+0000",
	"ingredients": [ {
		"name": "Flour Tortilla"
	}, {
		"name": "Lettuce"
	}, {
		"name": "Ground Beef"
	} ],
	"_links": {
		"self": {
			"href": "http://localhost:8081/tacos/5e4bc18aabd4bd2d88801a9e"
		},
		"taco": {
			"href": "http://localhost:8081/tacos/5e4bc18aabd4bd2d88801a9e"
		}
	}
}
```

```bash
$ jconsole
```

![chapter18-05](image/chapter18-05.png 'JConsole')

![chapter18-06](image/chapter18-06.png 'SSL 경고 메시지')

![chapter18-07](image/chapter18-07.png '현재 프로세스의 리소스 사용 현황')

![chapter18-08](image/chapter18-08.png 'tacoCounter MBean의 실시간 정보 보기')