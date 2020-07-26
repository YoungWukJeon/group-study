# 스프링 시큐리티
스프링 기반 어플리케이션의 보안(인증, 권한)을 담당하는 프레임워크  
스프링 시큐리티를 이용해서 보안을 쉽게 구현할 수 있음  
기본적으로 스프링 시큐리티는 filter 를 이용하여 구현되기 때문에 spring mvc 와 의존성 없이 동작시킬 수 있음  

## 관련 용어
- Principle : 접근 주체로 유저라고 생각하면 됨 
- Authenticate : 유저가 누구인지 확인 
- Authorize : 유저가 어떤 리소스에 권한이 있는지를 판별

## 시큐리티 인증 flow
![chapter04-01](image/chapter04-01.png '시큐리티 동작 flow')
1.유저가 로그인 시도  
2.AuthenticationFilter 는 해당 사용자의 세션이 SecurityContextHolder 에 있는지 검사하고 없으면 로그인 페이지로 이동 시킴  
ㄴ 로그인 페이지에서 AuthenticationFilter 에 도달하면 userName + password 로 UsernamePasswordAuthenticationToken 을 생성함    
ㄴ UsernamePasswordAuthenticationToken 내용이 올바른지 검증하기 위해서 AuthenticationManager 로 전달  
3.AuthenticationManager 는 AuthenticationProvider 에게 해당 사용자의 인증로직을 위임  
4.AuthenticationProvider 는 UserDetailsService 를 실행하여 사용자의 정보를 조회  
5.UserDetailsService 는 실제로 DB 에 있는 사용자 정보를 이용하여 시큐리티에서 사용할 객체를 반환하는 객체  
ㄴ UserDetails : 시큐리티에서 사용하는 사용자 정보 객체   
6.인증이 완료되면 AuthenticationManager 는 Authentication 을 반환하고 SecurityContext 에 사용자 정보를 저장    
7.모든 인증이 끝나면 AuthenticationFilter 에게 인증 성공 여부를 반환하고 성공, 실패에 따른 처리를 수행  
ㄴ 성공시 : AuthenticationSuccessHandler
ㄴ 실패시 : AuthenticationFailureHandler
   
실제로 스프링 시큐리티는 굉장히 많은 필터를 이용하여 구성되어 있음  
위에서 설명하는 것은 단순히 인증 필터 관련해서 요약해 놓은 것  
![chapter04-02](image/chapter04-02.png '시큐리티 필터 체인')

## 예제 
### 우선 시큐리티 디펜던시 추가 
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>
```

## 시큐리티 설정 (java 설정 클래스)

### 사용자 스토어 
시큐리티는 사용자 정보를 유지 관리하기 위해서 사용자 스토어가 필요함  
사용자 스토어의 종류 
- 인 메모리 사용자 스토어
```java
private void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.inMemoryAuthentication()
            .withUser("user1")
            .password("{noop}pass1")
            .authorities("ROLE_USER");
}
``` 
- JDBC 사용자 스토어 
```java
@Autowired
private DataSource dataSource;
private void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.jdbcAuthentication()
            .dataSource(dataSource);
}
```
jdbc 사용자 스토어를 사용하게 되면 시큐리티가 기본적으로 정의한 쿼리들이 있음   
spring security core 에서
ㄴ JdbcDaoImpl : 사용자 이름으로 사용자 조회 (책에 나오는 쿼리들)  
ㄴ JdbcUserDetailsManager : 사용자 정보 조회 관련 

- LDAP 사용자 스토어
- 커스텀 사용자 스토어
```java
@Autowired
private CustomUserDetailsService customUserDetailsService;
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(customUserDetailsService);
}
```
         
### 패스워드 암호화
패스워드 encoder 의 종류
- BCryptPasswordEncoder : bcrypt 를 해싱 암호화 
- NoOpPasswordEncoder : 암호화 하지 않음 
- PbKdf2PasswordEncoder : PBKDF2 를 암호화 

### 요청에 대한 보안 처리
HttpSecurity 를 이용한 접근 권한 제어  
- access(String) : 인자로 전달된 SpEL 표현식이 true 면 접근허용 
- anonymous() : 익명의 사용자에게 접근을 허용
- authenticated() : 익명이 아닌 사용자로 인증된 경우 접근허용
- denyAll() : 무조건 접근을 허용
- fullyAuthenticated() : 익명이 아니거나 또는 remember-me 가 아닌 사용자로 인증되면 접근허용
- hasAnyAuthority(String ..) : 지정된 권한 중 어떤 것이라도 사용자가 갖고 있으면 접근허용
- hasAnyRole(String ..) : 지정된 role 중 어느 하나라도 사용자가 갖고 있으면 접근허용 
- hasAuthority(String) : 지정된 권한을 사용자가 갖고 있으면 접근허용
- hasIpAddress(String) : 지정된 IP 주소로 요청이 왔으면 접근허용
- hasRole(String) : 지정된 역할을 사용자가 갖고 있으면 접근허용 
- not() : 다른 접근 메서드들의 효력을 무효화 
- permitAll() : 접근허용
- rememberMe() : remember-me (이전 로그인 정보를 쿠키나 DB 저장한 후 일정 기간 내에 다시 접근시 저장된 정보로 자동 로그인) 통한 인증된 사용자의 접근을 허용   
  
access 메서드를 이용하여 SpEL 형식으로 좀 더 다양하게 접근 권한 제어를 정의할 수 있음  
ㄴ SpEL 표현식   
- authentication : 해당 사용자의 인증 객체
- denyAll : 항상 false
- hasAnyRole(roles) : roles 중 1개라도 만족하면 true
- hasRole(role) : role 을 사용자가 갖고 있으면 true
- hasIpAddress(IP) : IP 에 만족하면 true
- isAnonymous() : 익명 사용자 true (로그인 안된 사용자)
- isAuthenticated() : 해당 사용자가 익명 사용자가 아니면 true
- isFullyAuthenticated() : 익명 + remember-me 가 아니면 true
- isRememberMe() : remember-me 기능으로 인증되면 true
- permitAll : 항상 true
- principal : 사용자의 principal 객체 
  
### CSRF 방어 
스프링 시큐리티는 CSRF 방어를 위한 로직이 이미 포함되어 있음  


## 래퍼런스
- [Bamdule 블로그](https://bamdule.tistory.com/52)