# 스프링 시큐리티 활성화하기

```xml
...
<dependencies>
	...
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-security</artifactId>
	</dependency>

	<dependency>
		<groupId>org.springframework.security</groupId>
		<artifactId>spring-security-test</artifactId>
		<scope>test</scope>
	</dependency>
</dependencies>
```

- 스프링 부트 보안 스타터 의존성과 보안 테스트 의존성을 우리가 직접 pom.xml 파일에 추가하였다.
- 방금 추가한 의존성이 스프링 애플리케이션을 안전하게 하기 위해서 필요한 전부다.
- 타코 클라우드 애플리케이션을 시작한 후에 웹 브라우저에서 홈페이지([http://localhost:8080](http://localhost:8080))나 타코 디자인 페이지( [http://localhost:8080/design](http://localhost:8080/design) )에 접속해 보자.

![chapter04-01](image/chapter04-01.png '스프링 시큐리티의 HTTP 기본 인증 대화상자')

- 그림과 같이 Username 필드에 user를 입력한다.
- Password는 무작위로 자동 생성되어 애플리케이션 로그 파일에 수록된다.

```
Using generated security password: 6838886b-edb5-40d8-bc45-395ba25a79e4
```

- 뒤쪽에 36자리의 비밀번호를 마우스로 선택하여 복사한 후 Password 필드에 붙여넣기 하고 Sign in 버튼을 클릭하면 로그인된다.
- 보안 스타터를 프로젝트 빌드 파일에 추가만 했을 때는 다음의 보안 구성이 제공된다.
    - 모든 HTTP 요청 경로는 인증(authentication)되어야 한다.
    - 어떤 특정 역할이나 권한이 없다.
    - 로그인 페이지가 따로 없다.
    - 스프링 시큐리티의 HTTP 기본 인증(그림)을 사용해서 인증된다.
    - 사용자는 하나만 있으며, 이름은 user다. 비밀번호는 암호화해 준다.
- 타코 클라우드 애플리케이션의 보안을 제대로 구축하려면 더 많은 작업이 필요하며, 최소한 다음 기능을 할 수 있도록 스프링 시큐리티를 구성해야 된다.
    - 스프링 시큐리티의 HTTP 인증 대화상자(그림) 대신 우리의 로그인 페이지로 인증한다.
    - 다수의 사용자를 제공하며, 새로운 타코 클라우드 고객이 사용자로 등록할 수 있는 페이지가 있어야 한다.
    - 서로 다른 HTTP 요청 경로마다 서로 다른 보안 규칙을 적용한다. 예를 들어, 홈페이지와 사용자 등록 페이지는 인증이 필요하지 않다.
- 타코 클라우드의 이런 보안 요구를 충족하기 위해서는 스프링 자동-구성이 하는 것을 대체하기 위한 작업을 해야 한다.

# 스프링 시큐리티 구성하기

- 최근의 여러 스프링 시큐리티 버전에서는 훨씬 더 알기 쉬운 자바 기반의 구성을 지원한다.

```java
package tacos.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.antMatchers("/design", "/orders")
					.access("hasRole('ROLE_USER')")
				.antMatchers("/", "/**")
					.access("permitAll")
			.and()
				.httpBasic();
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication()
			.withUser("user1")
			.password("{noop}password1")
			.authorities("ROLE_USER")
			.and()
			.withUser("user2")
			.password("{noop}password2")
			.authorities("ROLE_USER")
	}
}
```

- 사용자의 HTTP 요청 경로에 대해 접근 제한과 같은 보안 관련 처리를 우리가 원하는 대로 할 수 있게 해준다.
- 웹 브라우저에서 [http://localhost:8080](http://localhost:8080)에 접속해 보자.
    - 홈페이지가 바로 나타날 것이다.
    - SecurityConfig 클래스의 configure() 메서드에서 모든 사용자의 홈페이지 접근을 허용했기 때문이다.
- 그 다음에 [http://localhost:8080/design](http://localhost:8080/design) 에 접속해 보자.

![chapter04-02](image/chapter04-02.png '다른 HTTP 로그인 대화상자')

- 사용자 이름에는 user1, 비밀번호 필드는 password1을 입력하고 로그인을 클릭하면 타코 디자인 폼(고객이 원하는 식자재를 선택하여 타코를 생성함)이 나타날 것이다.
- 보안을 테스트할 때는 웹 브라우저를 private 또는 incognito 모드로 설정하는 것이 좋다.
    - 이렇게 하면 사용자의 검색 세션에 관한 데이터인 쿠키, 임시 인터넷 파일, 열어 본 페이지 목록 및 기타 데이터를 저장하지 못하도록 한다. (또는, 해당 창을 닫을 때 삭제된다.)
    - 따라서 브라우저의 창을 열 때마다 이전 세션의 사용 기록이 반영되지 않는 새로운 세션으로 시작된다.
    - 단, 애플리케이션을 테스트할 때 매번 로그인을 해야 한다.
    - 그러나 보안 관련 변경이 확실하게 적용되는지 분명하게 확인할 수 있다.
- 스프링 시큐리티에서는 여러 가지의 사용자 스토어 구성 방법을 제공한다.
    - 인메모리(in-memory) 사용자 스토어
    - JDBC 기반 사용자 스토어
    - LDAP 기반 사용자 스토어
    - 커스텀 사용자 명세 서비스
- SecurityConfig 클래스는 WebSecurityConfigurerAdapter의 서브 클래스다.
    - configure(HttpSecurity)는 HTTP 보안을 구성하는 메서드다.
    - 그리고 configure(AuthenticationManagerBuilder)는 사용자 인증 정보를 구성하는 메서드이며, 위의 사용자 스토어 중 어떤 것을 선택하든 이 메서드에서 구성한다.

    ```java
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
    ...
    }
    ```

## 인메모리 사용자 스토어

- 사용자 정보를 유지·관리할 수 있는 곳 중 하나가 메모리다.
- 만일 변경이 필요 없는 사용자만 미리 정해 놓고 애플리케이션을 사용한다면 아예 보안 구성 코드 내부에 정의할 수 있을 것이다.

```java
...
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	auth.inMemoryAuthentication()
			.withUser("user1")
			.password("{noop}password1")
			.authorities("ROLE_USER")
			.and()
			.withUser("user2")
			.password("{noop}password2")
			.authorities("ROLE_USER")
}
```

- [http://localhost:8080/design](http://localhost:8080/design) 에 접속하면 사용자 이름에 user1을 , 비밀번호에는 password1을 입력하고 로그인 버튼을 클릭(또는 Enter 누름)하면 정상적으로 인증되어 타코 디자인 폼이 나타날 것이다.
- 만일 사용자 이름이나 비밀번호가 잘못 입력된 경우엔 현재는 로그인 대화상자가 다시 나타난다.
- withUser()를 호출하면 해당 사용자의 구성이 시작되며, 이 때 사용자 이름은(username)을 인자로 전달한다.
- 반면에 비밀번호(password)와 부여 권한(granted authority)은 각각 password()와authorities() 메서드의 인자로 전달하여 호출한다. (authorities("ROLE_USER") 대신 .role("USER")를 사용해도 된다.)
- 스프링5부터는 반드시 비밀번호를 암호화해야 하므로 만일 password() 메서드를 호출하여 암호화하지 않으면 접근 거부(HTTP 403) 또는 Internal Server Error(HTTP 500)가 발생된다.
- {noop}을 지정하여 비밀번호를 암호화하지 않았다.

## JDBC 기반의 사용자 스토어

- 사용자 정보는 관계형 데이터베이스로 유지·관리되는 경우가 많으므로 JDBC 기반의 사용자 스토어가 적합해 보인다.

```java
...
import javax.sql.Datasource;
...

@Autowired
Datasource dateSource;

@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	auth
		.jdbcAuthentication()
		.dataSource(dataSource);
}
```

- configure()에서는 AuthenticationManagerBuilder의 jdbcAuthentication()을 호출한다.
- 이 때 데이터베이스를 엑세스하는 방법을 알 수 있도록 dataSource() 메서드를 호출하여 DataSource도 설정해야 한다.
- 여기서는 @Autowired 애노테이션을 지정했으므로 DataSource가 자동으로 주입된다.

### 스프링 시큐리티의 기본 사용자 쿼리를 대체하기

- 스프링 시큐리티의 사용자 정보 데이터베이스 스키마를 사용할 때는 방금 전에 작성한 configure() 메서드의 코드면 충분하다.
- 사용자 정보를 저장하는 테이블과 열이 정해져 있고 쿼리가 미리 생성되어 있기 때문이다.

```java
public static final String DEF_USERS_BY_USERNAME_QUERY = 
	"select username, password, enabled " +
	"from users " +
	"where username = ?";
public static final String DEF_AUTHORITIES_BY_USERNAME_QUERY = 
	"select username, authority " +
	"from authorities " +
	"where username = ?";
public static final String DEF_GROUP_AUTHORITIES_BY_USERNAME_QUERY =
	"select g.id, g.group_name, ga.authority " +
	"from authorities g, group_member gm, group_authorites ga " +
	"where gm.username = ? " +
	"and g.id = ga.group_id ";
	"and g.id = gm.group_id";
```

- 이것을 보면 내부적으로 기본 생성되는 테이블과 열의 이름을 알 수 있을 것이다.
- 사용자 정보는 users 테이블에, 권한은 authorities 테이블에, 그룹의 사용자는 group_members 테이블에, 그룹의 권한은 group_authorities 테이블에 있다.
- 첫 번째 쿼리에서는 해당 사용자의 이름(username), 비밀번호(password), 사용 가능한 사용자인지를 나타내는 활성화 여부(enabled)를 검색한다.
- 이 정보는 사용자 인증에 사용된다.
- 그 다음 쿼리에서는 해당 사용자에게 부여된 권한을 찾는다.
- 그리고 마지막 쿼리에서는 해당 사용자가 속한 그룹 권한을 찾는다.
- 스프링 시큐리티에 사전 지정된 데이터베이스 테이블과 SQL 쿼리를 사용하려면 관련 테이블을 생성하고 사용자 데이터를 추가해야 한다.

```sql
drop table if exists users;
drop table if exists authorities;
drop index if exists ix_auth_username;

create table if not exists users (
	username varchar2(50) not null primary key,
	password varchar2(50) not null,
	enabled char(1) default '1');

create table if not exists authorities (
	username varchar2(50) not null,
	authority varchar2(50) not null,
	constraint fk_authorities_users
		foreign key (username) references users (username));

create unique index ix_auth_username
	on authorities (username, authority);
```

```sql
insert into users (username, password) values ('user1', 'password1');
insert into users (username, password) values ('user2', 'password2');

insert into authorities (username, authority)
	values ('user1', 'ROLE_USER');
insert into authorities (username, authority)
	values ('user2', 'ROLE_USER');

commit;
```

- 이번에는 타코 디자인 페이지 대신 다음과 같은 에러가 나타날 것이다.

```
There was an unexpected error (type=Internal Server Error, status=500).
There is no PasswordEncoder mapped for the id "null"
```

- 스프링 시큐리티 5 버전부터는 의무적으로 PasswordEncoder를 사용해서 비밀번호를 암호화해야 하기 때문이다.
- 스프링 시큐리티에 사전 지정된 데이터베이스 테이블과 SQL 쿼리를 사용하고 사용자 데이터도 저장했다면 이대로 사용할 수 있다.
- 그러나 스프링 시큐리티의 것과 다른 데이터베이스(예를 들어, 테이블이나 열의 이름이 다를 때)를 사용한다면, 스프링 시큐리티의 SQL 쿼리를 우리 SQL 쿼리로 대체할 수 있다.

```java
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	auth
		.jdbcAuthentication()
			.dataSource(dataSource)
			.usersByUsernameQuery(
				"select username, password, enabled from users " +
				"where username=?")
			.authoritiesByUsernameQuery(
				"select username, authority from authorities " +
				"where username=?");
}
```

- 이 쿼리에서 사용하는 테이블의 이름은 스프링 시큐리티의 기본 데이터베이스 테이블과 달라도 된다.
- 그러나 테이블이 갖는 열의 데이터 타입과 길이는 일치해야 한다.
- 스프링 시큐리티의 기본 SQL 쿼리를 우리 것으로 대체할 때는 다음의 사항을 지켜야 한다.
    - 매개변수(where 절에 사용됨)는 하나이며, username이어야 한다.
    - 사용자 정보 인증 쿼리에서는 username, password, enabled 열의 값을 반환해야 한다.
    - 사용자 권한 쿼리에서는 해당 사용자 이름(username), 부여된 권한(authority)을 포함하는 0 또는 다수의 행을 반환할 수 있다.
    - 그리고 그룹 권한 쿼리에서는 각각 그룹 id, 그룹 이름(group_name), 권한(authority) 열을 갖는 0 또는 다수의 행을 반환한다.

### 암호화된 비밀번호 사용하기

- 비밀번호를 데이터베이스에 저장할 때와 사용자가 입력한 비밀번호는 모두 같은 암호화 알고리즘을 사용해서 암호화해야 한다.
- 비밀번호를 암호화할 때는 다음과 같이 passwordEncoder() 메서드를 호출하여 비밀번호 인코더(encoder)를 지정한다.("where username=?")

```java
...
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
...
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	auth
		.jdbcAuthentication()
			.dataSource(dataSource)
			.usersByUsernameQuery(
				"select username, password, enabled from users " +
				"where username=?")
			.authoritiesByUsernameQuery(
				"select username, authority from authorities " +
				"where username=?")
			.passwordEncoder(new BCryptPasswordEncoder());
}
```

- passwordEncoder() 메서드는 스프링 시큐리티의 PasswordEncoder 인터페이스를 구현하는 어떤 객체도 인자로 받을 수 있다.
- 암호화 알고리즘을 구현한 스프링 시큐리티의 모듈에는 다음과 같은 구현 클래스가 포함되어 있다.
    - BCryptPasswordEncoder : bcrypt를 해싱 암호화한다.
    - NoOpPasswordEncoder : 암호화하지 않는다.
    - Pbkdf2PasswordEncoder : PBKDF2를 암호화한다.
    - SCryptPasswordEncoder : scrypt를 해싱 암호화한다.
    - StandardPasswordEncoder : SHA-256을 해싱 암호화한다.
- PasswordEncoder 인터페이스는 다음과 같이 간단하게 정의되어 있다.

```java
public interface PasswordEncoder {
	String encode(CharSequence rawPassword);
	boolean matches(CharSequence rawPassword, String encodedPassword);
}
```

```java
package tacos.security;

import org.springframework.security.crypto.password.PasswordEncoder;

public class NoEncodingPasswordEncoder implements PasswordEncoder {
	@Override
	public String encode(CharSequence rawPwd) {
		return rawPwd.toString();
	}

	@Override
	public boolean matches(CharSequence rawPwd, String encodedPwd) {
		return rawPwd.toString().equals(encodedPwd);
	}
}
```

```java
...
import tacos.security.NoEncodingPasswordEncoder;
...
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	auth
		.jdbcAuthentication()
			.dataSource(dataSource)
			.usersByUsernameQuery(
				"select username, password, enabled from users " +
				"where username=?")
			.authoritiesByUsernameQuery(
				"select username, authority from authorities " +
				"where username=?")
			.passwordEncoder(new NoEncodingPasswordEncoder());
}
```

- 여기서는 인증이 제대로 되는지 확인하기 위해 비밀번호를 암호화하지 않았다.
- 그러나 이 방법은 코드를 테스트할 때만 임시로 사용한다는 것에 유의하자.

## LDAP 기반 사용자 스토어

- LDAP(LightWeight Directory Access Protocol) 기반 인증으로 스프링 시큐리티를 구성하기 위해서 ldapAuthentication() 메서드를 사용할 수 있다.

```java
...
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	auth
		.ldapAuthentication()
		.userSearchFilter("(uid={0})")
		.groupSearchFilter("member={0}");
}
```

- userSearchFilter()와 groupSearchFilter() 메서드는 LDAP 기본 쿼리의 필터를 제공하기 위해 사용되며, 여기서는 사용자와 그룹을 검색하기 위해 사용하였다.
- 기본적으로 사용자와 그룹 모두의 LDAP 기본 쿼리는 비어 있어서 쿼리에 의한 검색이 LDAP 계층의 루트부터 수행된다는 것을 나타낸다.

```java
...
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	auth
		.ldapAuthentication()
		.userSearchBase("ou=people")
		.userSearchFilter("(uid={0})")
		.groupSearchBase("ou=groups")
		.groupSearchFilter("member={0}");
}
```

- userSearchBase() 메서드는 사용자를 찾기 위한 기준점 쿼리를 제공하며, 이와 유사하게 groupSearchBase()에는 그룹을 찾기 위한 기준점 쿼리를 지정한다.
- 따라서 이 코드에서는 루트부터 검색하지 않는다.
- 즉, 사용자는 people 구성 단위(Organization Unit, OU)부터 그룹은 groups 구성 단위부터 검색이 시작된다.

### 비밀번호 비교 구성하기

- LDAP의 기본 인증 전략은 사용자가 직접 LDAP 서버에서 인증받도록 하는 것이다.
- 그러나 비밀번호를 비교하는 방법도 있다.
- 이 방법에서는 입력된 비밀번호를 LDAP 디렉터리에 전송한 후, 이 비밀번호를 사용자의 비밀번호 속성 값과 비교하도록 LDAP 서버에 요청한다.
- 이 때 비밀번호 비교는 LDAP 서버에서 수행되므로 실제 비밀번호는 노출되지 않는다.
- 만일 비밀번호를 비교하는 방법으로 LDAP 인증을 하고자 할 때는 다음과 같이 passwordCompare() 메서드를 호출하면 된다.

```java
...
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	auth
		.ldapAuthentication()
		.userSearchBase("ou=people")
		.userSearchFilter("(uid={0})")
		.groupSearchBase("ou=groups")
		.groupSearchFilter("member={0}")
		.passwordCompare();
}
```

- 이 때는 로그인 폼에 입력된 비밀번호가 사용자의 LDAP 서버에 있는 userPassword 속성 값과 비교된다.
- 따라서 비밀번호가 다른 속성에 있다면, passwordAttribute()를 사용해서 비밀번호 속성의 이름을 지정할 수 있다.

```java
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	auth
		.ldapAuthentication()
		.userSearchBase("ou=people")
		.userSearchFilter("(uid={0})")
		.groupSearchBase("ou=groups")
		.groupSearchFilter("member={0}")
		.passwordCompare()
		.passwordEncoder(new BCryptPasswordEncoder())
		.passwordAttribute("userPasscode");
}
```

- 이처럼 서버 측에서 비밀번호가 비교될 때는 실제 비밀번호가 서버에 유지된다는 것이 장점이다.

### 원격 LDAP 서버 참조하기

- 기본적으로 스프링 시큐리티의 LDAP 인증에서는 로컬 호스트(localhost)의 33389 포트로 LDAP 서버가 접속된다고 간주한다.
- 그러나 만일 LDAP 서버가 다른 컴퓨터에서 실행 중이라면 contextSource() 메서드를 사용해서 해당 서버의 위치를 구성할 수 있다.

```java
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	auth
		.ldapAuthentication()
		.userSearchBase("ou=people")
		.userSearchFilter("(uid={0})")
		.groupSearchBase("ou=groups")
		.groupSearchFilter("member={0}")
		.passwordCompare()
		.passwordEncoder(new BCryptPasswordEncoder())
		.passwordAttribute("userPasscode")
		.contextSource().url("ldap://tacocloud.com:389/dc=tacocloud,dc=com");
}
```

- contextSource() 메서드는 ContextSourceBuilder를 반환한다.
- 이것은 url() 메서드를 제공하므로 LDAP 서버의 위치를 지정할 수 있게 해준다.

### 내장된 LDAP 서버 구성하기

- 인증을 기다리는 LDAP 서버가 없는 경우에는 스프링 시큐리티에서 제공하는 내장 LDAP 서버를 사용할 수 있다.

```xml
...
<dependencies>
	...
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-data-ldap</artifactId>
	</dependency>

	<dependency>
		<groupId>org.springframework.ldap</groupId>
		<artifactId>spring-ldap-core</artifactId>
	</dependency>
	
	<dependency>
		<groupId>org.springframework.security</groupId>
		<artifactId>spring-security-ldap</artifactId>
	</dependency>
</dependencies>
```

- 내장된 LDAP 서버를 사용할 때는 원격 LDAP 서버의 URL을 설정하는 대신 root() 메서드를 사용해서 내장 LDAP 서버의 루트 경로를 지정할 수 있다.

```java
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	auth
		.ldapAuthentication()
		.userSearchBase("ou=people")
		.userSearchFilter("(uid={0})")
		.groupSearchBase("ou=groups")
		.groupSearchFilter("member={0}")
		.passwordCompare()
		.passwordEncoder(new BCryptPasswordEncoder())
		.passwordAttribute("userPasscode")
		.contextSource()
		.root("dc=tacocloud,dc=com");
}
```

- LDAP 서버가 시작될 때는 classpath에서 찾을 수 있는 LDIF(LDAP Data Interchange Format) 파일로부터 데이터를 로드한다.
- LDIF는 일반 텍스트 파일에 LDAP 데이터를 나타내는 표준화된 방법이다.
- 각 레코드는 하나 이상의 줄로 구성되며, 각 줄은 한 쌍으로 된 name:value를 포함한다.
- 그리고 각 레코드는 빈 줄로 구분된다.
- 만일 스프링이 classpath를 검색하지 않고 LDIF 파일을 찾도록 한다면, ldif() 메서드를 사용해서 LDIF 파일을 찾을 수 있는 경로를 지정할 수 있다.

```java
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	auth
		.ldapAuthentication()
		.userSearchBase("ou=people")
		.userSearchFilter("(uid={0})")
		.groupSearchBase("ou=groups")
		.groupSearchFilter("member={0}")
		.contextSource()
		.root("dc=tacocloud,dc=com")
		.ldif("classpath:users.ldif")
		.and()
		.passwordCompare()
		.passwordEncoder(new BCryptPasswordEncoder())
		.passwordAttribute("userPasscode");
}
```

```
dn: ou=groups,dc=tacocloud,dc=com
objectclass: top
objectclass: organizationalUnit
ou: groups

dn: ou=people,dc=tacocloud,dc=com
objectclass: top
objectclass: organizationalUnit
ou: people

dn: uid=tacocloud,ou=people,dc=tacocloud,dc=com
objectclass: top
objectclass: person
objectclass: organizationalPerson
objectclass: inetOrgPerson
cn: GD Hong
sn: Hong
uid: user1
userPasscode: password1

dn: uid=tacocloud,ou=people,dc=tacocloud,dc=com
objectclass: top
objectclass: person
objectclass: organizationalPerson
objectclass: inetOrgPerson
cn: MS Park
sn: Park
uid: user2
userPasscode: password2

dn: cn=USER,ou=groups,dc=tacocloud,dc=com
objectclass: top
objectclass: groupOfNames
cn: USER
member: uid=user1,ou=people,dc=tacocloud,dc=com
member: uid=user2,ou=people,dc=tacocloud,dc=com
```

## 사용자 인증의 커스터마이징

### 사용자 도메인 객체와 퍼시스턴스 정의하기

- 애플리케이션을 사용해서 타코 클라우드 고객이 등록할 때는 사용자 이름과 비밀번호 외에 전체 이름, 주소, 전화번호도 제공해야 한다.

```java
package tacos;

...

@Entity
@Data
@NoArgsConstructor(access=AccessLevel.PRIVATE, force=true)
@RequiredArgsConstructor
public class User implements UserDetails {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	private final String username;
	private final String password;
	private final String fullname;
	private final String street;
	private final String city;
	private final String state;
	private final String zip;
	private final String phoneNumber;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
```

- 몇 가지 속성 정의와 더불어 User 클래스는 스프링 시큐리티의 UserDetails 인터페이스를 구현한다.
- UserDetails를 구현한 User 클래스는 기본 사용자 정보를 프레임워크에 제공한다.
- getAuthorities() 메서드는 해당 사용자에게 부여된 권한을 저장한 컬렉션을 반환한다.
- 메서드 이름이 is로 시작하고 Expired로 끝나는 다양한 메서드들은 해당 사용자 계정의 활성화 또는 비활성화 여부를 나타내는 boolean 값을 반환한다.

```java
package tacos.data;

...

public interface UserRepository extends CrudRepository<User, Long> {
	User findByUsername(String username);
}
```

- UserRepository는 findByUsername() 메서드를 추가로 정의하고 있다.
- 이 메서드는 사용자 이름 즉, id로 User를 찾기 위해 사용자 명세 서비스에서 사용될 것이다.

### 사용자 명세 서비스 생성하기

```java
public interface UserDetailsService {
	UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
```

- 이 인터페이스를 구현하는 클래스의 메서드에는 사용자 이름이 인자로 전달되며, 메서드 실행 후 UserDetails 객체가 반환되거나, 또는 해당 사용자 이름이 없으면 UsernameNotFoundException을 발생시킨다.

```java
package tacos.security;

...

@Service
public class UserRepositoryUserDetailService implements UserDetailsService {
	private UserRepository userRepo;

	@Autowired
	public UserRepositoryUserDetailService(UserRepository userRepo) {
		this.userRepo = userRepo;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepo.findByUsername(username);
		if (user != null) {
			return user;
		}
		throw new UsernameNotFoundException("User '" + username + "' not found");
	}
}
```

- loadByUsername() 메서드에서는 절대로 null을 반환하지 않는다는 간단한 규칙이 있다.
- UserRepositoryUserDetailService 클래스에는 @Service 애노테이션이 지정되어 있다.
- 이것은 스프링의 스테레오타입 애노테이션 중 하나이며, 스프링이 컴포넌트 검색을 해준다는 것을 나타낸다.

```java
...
import org.springframework.security.core.userdetails.UserDetailsService;
...
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	...
	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth
			.userDetailsService(userDetailsService);
	}
	...
}
```

- 비밀번호가 암호화되어 데이터베이스에 저장될 수 있도록 비밀번호 인코더를 구성해야 한다.
- 이 때는 우선 PasswordEncoder 타입의 빈을 선언한다.

```java
...

public class SecurityConfig extends WebSecurityConfigurerAdapter {
	...
	@Autowired
	private UserDetailsService userDetailsService;

	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth
			.userDetailsService(userDetailsService)
			.passwordEncoder(encoder());
	}
}
```

### 사용자 등록하기

```java
package tacos.security;

...

@Controller
@RequestMapping("/register")
public class RegistrationController {
	private UserRepository userRepo;
	private PasswordEncoder passwordEncoder;

	public RegistrationController(UserRepository userRepo, PasswordEncoder passwordEncoder) {
		this.userRepo = userRepo;
		this.passwordEncoder = passwordEncoder;
	}

	@GetMapping
	public String registerForm() {
		return "registration";
	}

	@PostMapping
	public String processRegistration(RegistrationForm form) {
		userRepo.save(form.toUser(passwordEncoder));
		return "redirect:/login";
	}
}
```

```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" 
      xmlns:th="http://www.thymeleaf.org">
  <head>
    <title>Taco Cloud</title>
  </head>
  
  <body>
    <h1>Register</h1>
    <img th:src="@{/images/TacoCloud.png}"/>    
    
    <form method="POST" th:action="@{/register}" id="registerForm">
    
        <label for="username">Username: </label>
        <input type="text" name="username"/><br/>

        <label for="password">Password: </label>
        <input type="password" name="password"/><br/>

        <label for="confirm">Confirm password: </label>
        <input type="password" name="confirm"/><br/>

        <label for="fullname">Full name: </label>
        <input type="text" name="fullname"/><br/>
    
        <label for="street">Street: </label>
        <input type="text" name="street"/><br/>
    
        <label for="city">City: </label>
        <input type="text" name="city"/><br/>
    
        <label for="state">State: </label>
        <input type="text" name="state"/><br/>
    
        <label for="zip">Zip: </label>
        <input type="text" name="zip"/><br/>
    
        <label for="phone">Phone: </label>
        <input type="text" name="phone"/><br/>
    
        <input type="submit" value="Register"/>
    </form>
    
  </body>
</html>
```

```java
package tacos.security;

...

@Data
public class RegistrationForm {
	private String username;
	private String password;
	private String fullname;
	private String street;
	private String city;
	private String state;
	private String zip;
	private String phone;

	public User toUser(PasswordEncoder passwordEncoder) {
		return new User(username, passwordEncoder.encode(password),
			fullname, street, city, state, zip, phone);
	}
}
```

- 그리고 toUser() 메서드는 RegistrationForm의 속성 값을 갖는 새로운 User 객체를 생성한다.
- 지금은 애플리케이션을 시작해도 등록 페이지를 볼 수 없다.
- 기본적으로 모든 웹 요청은 인증이 필요하기 때문이다.

# 웹 요청 보안 처리하기

- 홈페이지, 로그인 페이지, 등록 페이지는 인증되지 않은 모든 사용자가 사용할 수 있어야 한다.
- 이런 보안 규칙을 구성하려면 SecurityConfig 클래스에 다음의 configure(HttpSecurity) 메서드를 오버라이딩해야 한다.

```java
@Override
protected void configure(HttpSecurity http) throws Exception {
	...
}
```

- 이 configure() 메서드는 HttpSecurity 객체를 인자로 받는다.
- 이 객체는 웹 수준에서 보안을 처리하는 방법을 구성하는 데 사용된다.
    - HTTP 요청 처리를 허용하기 전에 충족되어야 할 특정 보안 조건을 구성한다.
    - 커스텀 로그인 페이지를 구성한다.
    - 사용자가 애플리케이션의 로그아웃을 할 수 있도록 한다.
    - CSRF 공격으로부터 보호하도록 구성한다.

## 웹 요청 보안 처리하기

- /design과 /orders의 요청은 인증된 사용자에게만 허용되어야 한다.
- 그리고 이외의 모든 다른 요청은 모든 사용자에게 허용되어야 한다.

```java
...
@Override
protected void configure(HttpSecurity http) throws Exception {
	http
		.authorizeRequests()
		.antMatchers("/design", "/orders")
		.hasRole("ROLE_USER")
		.antMatchers("/", "/**").permitAll();
}
```

- authorizeRequests()는 ExpressionInterceptUrlRegistry 객체를 반환한다.
- 이 객체를 사용하면 URL 경로와 패턴 및 해당 경로의 보안 요구사항을 구성할 수 있다.
    - /design과 /orders의 요청은 ROLE_USER의 권한을 갖는 사용자에게만 허용된다.
    - 이외의 모든 요청은 모든 사용자에게 허용된다.
- 이런 규칙을 지정할 때는 순서가 중요하다.
    - antMatchers()에서 지정된 경로의 패턴 일치를 검사하므로 먼저 지정된 보안 규칙이 우선적으로 처리된다.
    - 따라서 만일 앞 코드에서 두 개의 antMatchers() 순서를 바꾸면 모든 요청의 사용자에게 permitAll()이 적용되므로 /design과 /orders의 요청은 효력이 없어진다.
- hasRole()과 permitAll()은 요청 경로의 보안 요구를 선언하는 메서드다.
    - access(String) : 인자로 전달된 SpEL 표현식이 true면 접근을 허용한다.
    - anonymous() : 익명의 사용자에게 접근을 허용한다.
    - authenticated() : 익명이 아닌 사용자로 인증된 경우 접근을 허용한다.
    - denyAll() : 무조건 접근을 거부한다.
    - fullyAuthenticated() : 익명이 아니거나 또는 remember-me가 아닌 사용자로 인증되면 접근을 허용한다.
    - hasAnyAuthority(String...) : 지정된 권한 중 어떤 것이라도 사용자가 갖고 있으면 접근을 허용한다.
    - hasAnyRole(String...) : 지정된 역할 중 어느 하나라도 사용자가 갖고 있으면 접근을 허용한다.
    - hasAuthority(String) : 지정된 권한을 사용자가 갖고 있으면 접근을 허용한다.
    - hasIpAddress(String) : 지정된 IP 주소로부터 요청이 오면 접근을 허용한다.
    - hasRole(String) : 지정된 역할을 사용자가 갖고 있으면 접근을 허용한다.
    - not() : 다른 접근 메서드들의 효력을 무효화한다.
    - permitAll() : 무조건 접근을 허용한다.
    - rememberMe() : remember-me(이전 로그인 정보를 쿠키나 데이터베이스로 저장한 후 일정 기간 내에 다시 접근 시 저장된 경보로 자동 로그인됨)를 통해 인증된 사용자의 접근을 허용한다.
- 대부분의 메서드는 요청 처리의 기본적인 보안 규칙을 제공한다.
- 각 메서드에 정의된 보안 규칙만 사용된다는 제약이 있다.
- 이의 대안으로 access() 메서드를 사용하면 더 풍부한 보안 규칙을 선언하기 위해 SpEL(Spring Expression Language, 스프링 표현식 언어)을 사용할 수 있다.
- 스프링 시큐리티에서 확장된 SpEL
    - authentication : 해당 사용자의 인증 객체
    - denyAll : 항상 false를 산출한다.
    - hasAnyRole(역할 내역) : 지정된 역할 중 어느 하나라도 해당 사용자가 갖고 있으면 true
    - hasRole(역할) : 지정된 역할을 해당 사용자가 갖고 있으면 true
    - hasIpAddress(IP 주소) : 지정된 IP 주소로부터 해당 요청이 온 것이면 true
    - isAnonymous() : 해당 사용자가 익명 사용자이면 true
    - isAuthenticated() : 해당 사용자가 익명이 아닌 사용자로 인증되었으면 true
    - isFullyAuthenticated() : 해당 사용자가 익명이 아니거나 또는 remember-me가 아닌 사용자로 인증되었으면 true
    - isRememberMe() : 해당 사용자가 remember-me 기능으로 인증되었으면 true
    - permitAll : 항상 true를 산출한다.
    - principal : 해당 사용자의 principal 객체
- 대부분의 보안 표현식의 확장과 유사한 기능의 메서드가 있다.

```java
@Override
protected void configure(HttpSecurity http) throws Exception {
	http
		.authorizeRequests()
		.antMatchers("/design", "/orders")
			.access("hasRole('ROLE_USER')")
		.antMatchers("/", "/**").access("permitAll");
}
```

- 표현식이 훨씬 더 유연하게 사용될 수 있다.
    - 이럴 일은 별로 없겠지만 예들 들어, 화요일의 타코 생성은 ROLE_USER 권한을 갖는 사용자에게만 허용하고 싶다고 해보자.

    ```java
    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	http
    		.authorizeRequests()
    		.antMatchers("/design", "/orders")
    			.access("hasRole('ROLE_USER') && " +
    				"T(java.util.Calendar).getInstance().get(" +
    				"T(java.util.Calendar).DAY_OF_WEEK) == " +
    				"T(java.util.Calendar).TUESDAY")
    		.antMatchers("/", "/**").access("permitAll");
    }
    ```

## 커스텀 로그인 페이지 생성하기

- 기본 로그인 페이지를 교체하려면 우선 우리의 커스텀 로그인 페이지가 있는 경로를 스프링 시큐리티에 알려주어야 한다.
- 이것은 configure(HttpSecurity) 메서드의 인자로 전달되는 HttpSecurity 객체의 formLogin()을 호출해서 할 수 있다.

```java
@Override
protected void configure(HttpSecurity http) throws Exception {
	http
		.authorizeRequests()
		.antMatchers("/design", "/orders")
			.access("hasRole('ROLE_USER')")
		.antMatchers("/", "/**").access("permitAll")
		.and()
		.formLogin()
		.loginPage("/login");
}
```

- and() 메서드는 인증 구성이 끝나서 추가적인 HTTP 구성을 적용할 준비가 되었다는 것을 나타낸다.
- and()는 새로운 구성을 시작할 때마다 사용할 수 있다.
- formLogin()은 우리의 커스텀 로그인 폼을 구성하기 위해 호출한다.
- 그리고 그 다음에 호출하는 loginPage()에는 커스텀 로그인 페이지의 경로를 지정한다.
- 우리의 로그인 페이지는 뷰만 있어서 매우 간단하므로 WebConfig에 뷰 컨트롤러 선언해도 충분하다.
- 다음의 addViewControllers() 메서드에서는 로그인 페이지의 뷰 컨트롤러를 설정한다.

```java
...
@Override
public void addViewControllers(ViewControllerRegistry registry) {
	registry.addViewController("/").setViewName("home");
	registry.addViewController("/login");
}
```

```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" 
      xmlns:th="http://www.thymeleaf.org">
  <head>
    <title>Taco Cloud</title>
  </head>
  
  <body>
    <h1>Login</h1>
    <img th:src="@{/images/TacoCloud.png}"/>
    
    <div th:if="${error}"> 
      Unable to login. Check your username and password.
    </div>
    
    <p>New here? Click 
       <a th:href="@{/register}">here</a> to register.</p>
    
    <!-- tag::thAction[] -->
    <form method="POST" th:action="@{/login}" id="loginForm">
    <!-- end::thAction[] -->
      <label for="username">Username: </label>
      <input type="text" name="username" id="username" /><br/>
      
      <label for="password">Password: </label>
      <input type="password" name="password" id="password" /><br/>
      
      <input type="submit" value="Login"/>
    </form>
  </body>
</html>
```

- 기본적으로 스프링 시큐리티는 /login 경로로 로그인 요청을 처리하며, 사용자 이름과 비밀번호 필드의 이름은 username과 password로 간주한다.
- 그러나 이것은 우리가 구성할 수 있다.

```java
.and()
.formLogin()
.loginPage("/login")
.loginProcessingUrl("/authenticate")
.usernameParameter("user")
.passwordParameter("pwd")
```

- 이 경우 스프링 시큐리티는 /authenticate 경로의 요청으로 로그인을 처리한다.
- 그리고 사용자 이름과 비밀번호 필드의 이름도 user와 pwd가 된다.
- 로그인하면 해당 사용자의 로그인이 필요하다고 스프링 시큐리티가 판단했을 당시에 사용자가 머물던 페이지로 바로 이동한다.
- 그러나 사용자가 직접 로그인 페이지로 이동했을 경우는 로그인한 후 루트 경로(예를 들어, 홈페이지)로 이동한다.
- 하지만 로그인한 후 이동할 페이지를 다음과 같이 변경할 수 있다.

```java
.and()
.formLogin()
.loginPage("/login")
.defaultSuccessUrl("/design")
```

- 이 경우는 사용자가 직접 로그인 페이지로 이동한 후 로그인을 성공적으로 했다면 /design 페이지로 이동할 것이다.
- 또한, 사용자가 로그인 전에 어떤 페이지에 있었는 지와 무관하게 로그인 후에는 무조건 /design 페이지로 이동하도록 할 수도 있다.
- 때는 defaultSuccessUrl의 두 번째 인자로 true를 전달하면 된다.

```java
.and()
.formLogin()
.loginPage("/login")
.defaultSuccessUrl("/design", true)
```

## 로그아웃하기

- 로그아웃을 하기 위해서는 HttpSecurity 객체의 logout을 호출해야 한다.

```java
.and()
.logout()
.logoutSuccessUrl("/")
```

- 이 코드는 /logout의 POST 요청을 가로채는 보안 필터를 설정한다.
- 라서 로그아웃 기능을 제공하기 위해 애플리케이션의 해당 뷰에 로그아웃 폼과 버튼을 추가해야 한다.

```html
<form method="POST" th:action="@{/logout}">
	<input type="submit" value="Logout" />
</form>
```

- 그리고 사용자가 로그아웃 버튼을 클릭하면 세션이 종료되고 애플리케이션에서 로그아웃된다.
- 이 때 사용자는 기본적으로 로그인 페이지로 다시 이동된다.
- 그러나 다른 페이지로 이동시키고 싶다면, 로그아웃 이후에 이동할 페이지를 지정하여 logoutSuccessUrl()을 호출하면 된다.

```java
.and()
.logout()
.logoutSuccessUrl("/")
```

- 이 경우는 로그아웃 이후에 홈페이지로 이동된다.

## CSRF 공격 방어하기

- CSRF(Cross-Site Request Forgery, 크로스 사이트 요청 위조)는 많이 알려진 보안 공격이다.
- 즉, 사용자가 웹사이트에 로그인한 상태에서 악의적인 코드(사이트 간의 요청을 위조하여 공격하는)가 삽입된 페이지를 열면 공격 대상이 되는 웹사이트에 자동으로 (그리고 은밀하게) 폼이 제출되고 이 사이트는 위조된 공격 명령이 믿을 수 있는 사용자로부터 제출된 것으로 판단하게 되어 공격에 노출된다.
- CSRF 공격을 막기 위해 애플리케이션에서는 폼의 숨김(hidden) 필드에 넣을 CSRF 토큰(token)을 생성할 수 있다.
- 그리고 해당 필드에 토큰을 넣은 후 나중에 서버에서 사용한다.
- 이후에 해당 폼이 제출될 때는 폼의 다른 데이터와 함께 토큰도 서버로 전송된다.
- 그리고 서버에서는 이 토큰을 원래 생성되었던 토큰과 비교하며, 토큰이 일치하면 해당 요청의 처리가 허용된다.
- 그러나 일치하지 않는다면 해당 폼은 토큰이 있다는 사실을 모르는 악의적인 웹사이트에서 제출된 것이다.
- 다행스럽게도 스프링 시큐리티에는 내장된 CSRF 방어 기능이 있다.
- 또한, 이 기능이 기본으로 활성화되어 있어서 우리가 별도로 구성할 필요가 없다.
- 단지 CSRF 토큰을 넣을 _csrf라는 이름의 필드를 애플리케이션이 제출하는 폼에 포함시키면 된다.

```html
<input type="hidden" name="_csrf" th:value=${_csrf.token} />
```

- 만일 스프링 MVC의 JSP 태크 라이브러리 또는 Thymeleaf를 스프링 시큐리티 dialect와 함께 사용 중이라면 숨김 필드조차도 자동으로 생성되므로 우리가  지정할 필요가 없다.

```html
<form method="POST" th:action="@{/login}" id="loginForm">
```

- CSRF 지원을 비활성화시킬 수도 있다.
- 그렇게 하고 싶다면 다음과 같이 disabled()을 호출하면 된다.

```java
.and()
.csrf()
.disable()
```

```java
package tacos.security;

...

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.antMatchers("/design", "/orders")
					.access("hasRole('ROLE_USER')")
				.antMatchers("/", "/**").access("permitAll")
			.and()
				.formLogin()
					.loginPage("/login")
			.and()
				.logout()
					.logoutSuccessUrl("/")
			.and()
				.csrf();
	}

	@Autowired
	private UserDetailsService userDetailsService;

	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth
			.userDetailsService(userDetailsService)
			.passwordEncoder(encoder());
	}
}
```

# 사용자 인지하기

- Order 객체를 최초 생성할 때 해당 주문을 하는 사용자의 이름과 주소를 주문 폼에 미리 넣을 수 있다면 좋은 것이다.
- 그러면 사용자가 매번 주문을 할 때마다 다시 입력할 필요가 없기 때문이다.
- 또한, 이보다 더 중요한 것으로 사용자 주문 데이터를 데이터베이스에 저장할 때 주문이 생성되는 User와 Order를 연관시킬 수 있어야 한다.

```java
...
import javax.persistence.ManyToOne;
...
@Data
@Entity
@Table(name="Taco_Order")
public class Order implements Serializable {
...
	private Date placedAt;

	@ManyToOne
	private User user;
...	
}
```

- user 속성의 @ManyToOne 애노테이션은 한 건의 주문이 한 명의 사용자에 속한다는 것을 나타낸다.
- 그리고 반대로 말해서, 한 명의 사용자는 여러 주문을 가질 수 있다.
- 사용자가 누구인지 결정하는 방법은 여러 가지가 있으며, 그 중 가장 많이 사용하는 방법은 다음과 같다.
    - Principal 객체를 컨트롤러 메서드에 주입한다.
    - Authentication 객체를 컨트롤러 메서드에 주입한다.
    - SecurityContextHolder를 사용해서 보안 컨텍스트를 얻는다.
    - @AuthenticationPrincipal 애노테이션을 메서드에 지정한다.
- java.security.Principal 객체의 name 속성을 사용해서 UserRepository의 사용자를 찾을 수 있다.

```java
@PostMapping
public String processOrder(@Valid Order order, Errors errors, SessionStatus sessionStatus, Principal principal) {
	...
	User user = userRepository.findByUsername(principal.getName());
	order.setUser(user);
	...
}
```

- 보안과 관련 없는 코드가 혼재하다.

```java
@PostMapping
public String processOrder(@Valid Order order, Errors errors, SessionStatus sessionStatus, Authentication authentication) {
	...
	User user = (User) authentication.getPrincipal();
	order.setUser(user);
	...
}
```

- 이 코드에서는 Authentication 객체를 얻은 다음에getPrincipal()을 호출하여 Principal 객체(여기서는 User)를 얻는다.
- 단, getPrincipal()은 java.util.Object 타입을 반환하므로 User 타입으로 변환해야 한다.
- 그러 다음과 같이 processOrder()의 인자로 User 객체를 전달하는 것이 가장 명쾌한 해결방법일 것이다.
- 여기서는 이 방법을 사용한다.
- 단, User 객체에 @AuthenticationPrincipal 애노테이션을 지정해야 한다.

```java
...
import org.springframework.security.core.annotation.AuthenticationPrincipal;
...
import tacos.User;
...
@PostMapping
public String processOrder(@Valid Order order, Errors errors, SessionStatus sessionStatus, @AuthenticationPrincipal User user) {
	if (errors.hasErrors()) {
		return "orderForm";
	}

	order.setUser(user);

	orderRepo.save(order);
	sessionStatus.setComplete();

	return "redirect:/";
}
```

- @AuthenticationPrincipal의 장점은 타입 변환이 필요 없고 Authentication과 동일하게 보안 특정 코드만 갖는다.
- 보안 특정 코드가 많아서 조금 어렵게 보이지만 인증된 사용자가 누구인지 식별하는 방법이 하나 더 있다.
- 즉, 보안 컨텍스트로부터 Authentication 객체를 얻은 후 다음과 같이 Principal 객체(인증된 사용자를 나타냄)를 요청하면 된다.
- 이 때도 반환되는 객체를 User 타입으로 변환해야 한다.

```java
Authentication authentication = SecurityContextHolder.getContext().getAuthentication());
User user = (User) authentication.getPrincipal();
```

- 이 코드에는 보안 특정 코드가 많다.
- 그러나 지금까지 얘기한 다른 방법에 비해서 한 가지 장점이 있다.
- 즉, 이 방법은 컨트롤러의 처리 메서드는 물론이고, 애플리케이션의 어디서든 사용할 수 있다는 것이다.

```java
...
import org.springframework.web.bind.anntoation.ModelAttribute;
...
@GetMapping("/current")
public String orderForm(@AuthenticationPrincipal User user, @ModelAttribute Order order) {
	if (order.getDeliveryName() == null) {
		order.setDeliveryName(user.getFullname());
	}
	if (order.getDeliveryStreet() == null) {
		order.setDeliveryStreet(user.getStreet());
	}
	if (order.getDeliveryCity() == null) {
		order.setDeliveryCity(user.getCity());
	}
	if (order.getDeliveryState() == null) {
		order.setDeliveryState(user.getState());
	}
	if (order.getDeliveryZip() == null) {
		order.setDeliveryZip(user.getZip());
	}
	return "orderForm";
}
```

- 주문 외에도 인증된 사용자 정보를 활용할 곳이 하나 더 있다.
- 즉, 사용자가 원하는 식자재를 선택하여 타코를 생성하는 디자인 폼에는 현재 사용자의 이름을 보여줄 것이다.

```java
...
import java.security.Principal;

import tacos.data.UserRepository;
import tacos.User;
...

public class DesignTacoController {
	private final IngredientRepository ingredientRepo;

	private TacoRepository tacoRepo;
	private UserRepository userRepo;

	@Autowired
	public DesignTacoController(IngredientRepository ingredientRepo, TacoRepository tacoRepo, UserRepository userRepo) {
		this.ingredientRepo = ingredientRepo;
		this.tacoRepo = tacoRepo;
		this.userRepo = userRepo;
	}

	@GetMapping
	public String showDesignForm(Model model, Principal principal) {
		...
		for (Type type : types) {
			model.addAttribute(type.toString().toLowerCase(), filterByType(inredients, type));
		}

		~~model.addAttribute("taco", new Taco());~~
		
		String username = principal.getName();
		User user = userRepo.findByUsername(username);
		model.addAttribute("user", user);

		return "design";
	}
	...
}
```

# 각 폼에 로그아웃 버튼 추가하고 사용자 정보 보여주기

- 마지막으로, 로그아웃 버튼과 사용자 정보를 보여주는 필드를 각 폼에 추가할 것이다.

```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" 
      xmlns:th="http://www.thymeleaf.org">
  <head>
    <title>Taco Cloud</title>
  </head>
  
  <body>
    <h1>Welcome to...</h1>
    <img th:src="@{/images/TacoCloud.png}"/>
    <form method="POST" th:action="@{/logout}" id="logoutForm">
      <input type="submit" value="Logout"/>
    </form>
    
    <a th:href="@{/design}" id="design">Design a taco</a>
  </body>
</html>
```

```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" 
      xmlns:th="http://www.thymeleaf.org">
  
	...
  
  <body>
    <h1>Design your taco!</h1>
    <h2>Feelin' hungry, <span th:text="${user.fullname}">NAME</span>?</h2>
    <img th:src="@{/images/TacoCloud.png}"/>
    
    <form method="POST" th:action="@{/logout}" id="logoutForm">
      <input type="submit" value="Logout"/>
    </form>
        
    <form th:method="POST" th:object="${design}" th:action="@{/design}" id="tacoForm">

	    <span class="validationError">

      ...

    </form>
  </body>
</html>
```

```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" 
      xmlns:th="http://www.thymeleaf.org">

	...
  
  <body>
  
    <form method="POST" th:action="@{/logout}" id="logoutForm">
      <input type="submit" value="Logout"/>
    </form>
  
    <form method="POST" th:action="@{/orders}" th:object="${order}"
          id="orderForm">
      <h1>Order your taco creations!</h1>
      
      <img th:src="@{/images/TacoCloud.png}"/>
    	
      <h3>Your tacos in this order:</h3>
      <a th:href="@{/design}" id="another">Design another taco</a><br/>
      ...
    </form>
  
  </body>
</html>
```