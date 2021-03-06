INSERT INTO user (email, password, name, create_date, update_date, last_login_date)
VALUES ('test@test.com', '$2a$10$C.Okl5Uo5eWn82/ZKsbWPOf82qox/pC6RzQ9fhhfK.f4MKwaSopbm', '홍길동',
'2020-05-20 14:01:11', '2020-05-20 14:01:11', '2020-08-04 17:53:38');
-- rawPassword: testpass

INSERT INTO authority (user_no, role, create_date, update_date)
VALUES (1, 'ROLE_USER', '2020-05-20 14:01:11', '2020-05-20 14:01:11');

--insert into product(name, category, description, price, create_date, update_date)
--values
--('name', 'category', 'description', 10, '1970-01-01 00:00:00', now()),
--('name', 'category1', 'description', 10, '1970-01-01 00:00:00', now());


INSERT INTO product (name, category, description, price, image, create_date, update_date)
VALUES ('Porsche 718 Boxster', '차량',
'
<h1>🚘 Power Unit</h1>
<p>
    Number of cylinders : 4
    <br>Power (PS) : 300 PS
    <br>Bore : 91.0 mm
    <br>RPM point maximum power : 6,500 rpm
    <br>Stroke : 76.4 mm
    <br>Max. torque : 38.8 kg·m
    <br>Displacement : 1,988 cc
    <br>RPM range maximum torque : 1,950~4,500 rpm
    <br>Power (kW) : 220 kW
</p>
<h1>🚘 Performance</h1>
<p>
    Top speed : 275 km/h
    <br>Acceleration 0 - 160 km/h with Sport Chrono Package : 10.8 s
    <br>Acceleration 0 - 100 km/h : 4.9 s
    <br>Acceleration 0 - 200 km/h : 18.1 s
    <br>Acceleration 0 - 100 km/h with Sport Chrono Package : 4.7 s
    <br>Acceleration 0 - 200 km/h with Sport Chrono Package : 17.8 s
    <br>Acceleration 0 - 160 km/h : 11.1 s
    <br>In-gear acceleration (80-120km/h) (50-75 mph) : 3.2 s
</p>
<h1>🚘 Body</h1>
<p>
    Length : 4,379 mm
    <br>Wheelbase : 2,475 mm
    <br>Width : 1,801 mm
    <br>Front track : 1,515 mm
    <br>Width (with mirrors) : 1,994 mm
    <br>Rear track : 1,531 mm
    <br>Height : 1,281 mm
    <br>Maximum load : 320 kg
    <br>Drag coefficient (Cd) : 0.31 Cd
</p>
<h1>🚘 Capacities</h1>
<p>
    Luggage compartment volume, front : 150 ℓ
    <br>Fuel tank : 54 ℓ
</p>
<h1>🚘 Service and Warranty</h1>
<p>
    Warranty period : 4 years without mileage limit
    <br>Main service interval : every 60,000 km
    <br>Paint warranty period : 3 years
    <br>Rust warranty : 12 years
</p>
<h1>💵 가격</h1>
<p>
포르쉐 더블 클러치(PDK) : 89,600,000 KRW 부터
</p>
<p>
    <a href="https://www.porsche.com/korea/ko/models/718/718-boxster-models/718-boxster/">About more</a>
</p>
',
89600000, '/images/products/car/boxster.jpg', '2020-03-20 09:17:13', '2020-03-20 09:17:13');

INSERT INTO product (name, category, description, price, image, create_date, update_date)
VALUES ('스프링 인 액션 : 스프링 5의 강력한 기능과 생산성을 활용한 웹 애플리케이션 개발[5판]', '도서',
'
<h1>이 상품의 분류</h1>
<hr>
<p>
    Home > 도서 > 컴퓨터/인터넷 > IT 전문서 > 프로그래밍 언어 > <b>JAVA</b>
</p>
<h1>📚 책 소개</h1>
<hr>
<p>
    <b>리액티브 프로그래밍 지원을 강화한 스프링 5의 모든 것을 탐구한다!</b>
    <br>
    <br>이 책에서는 스프링 5와 스프링 부트 2를 사용해서 애플리케이션을 개발하는 데 필요한 여러  핵심 기능을 알려줍니다. 스프링 5에서는 특히 새로운 리액티브 웹 프레임워크인 WebFlux를 중점적으로 다룹니다. 개발자들은 더 쉽고 확장성이 좋은 웹 애플리케이션을 개발할 수 있습니다. [스프링 인 액션(제5판)]에서는 스프링 최신 버전의 모든 것을 다루고 있으므로 개발자들이 스프링의 새로운 진면목을 파악하는 데 도움이 될 것입니다.
</p>
<h1>📚 출판사 서평</h1>
<hr>
<p>
    <b>한층 더 진화한 스프링 5로 웹 애플리케이션 개발에 날개를 달다!
        <br>스프링 WebFlux 등 리액티브 프로그래밍 지원을 강화한 스프링 5의 모든 것을 탐구한다!</b>
    <br>
    <br>스프링 프레임워크는 자바 개발자들을 더 편하게 해줍니다. 스프링 5의 새로운 기능들은 마이크로서비스와 리액티브 애플리케이션 개발과 같은 현대적 애플리케이션 설계 시에 생산성을 높여 줍니다. 이제는 스프링 부트가 완전히 통합되어 복잡한 프로젝트일지라도 최소한의 구성 코드로 시작할 수 있습니다. 또한, 한층 업그레이드된 WebFlux 프레임워크는 기본적으로 리액티브 애플리케이션을 지원합니다.
    <br>
    <br>[스프링 인 액션(제5판)]은 스프링의 핵심 기능을 명쾌하게 알려 줍니다. 이 책을 통해 여러분은 데이터베이스가 지원되는 웹 애플리케이션을 점진적으로 만들어 볼 것입니다. 그러면서 리액티브 프로그래밍, 마이크로서비스, 서비스 발견, Restful API, 애플리케이션 배포, 모범 사례를 배우게 될 것입니다. 또한, 원서의 오류를 모두 바로잡고 부족한 부분을 보강하는 데 심혈을 기울였습니다. 스프링 입문자와 스프링 5로 레벨업하려는 기존 스프링 사용자 모두에게 이 책은 안성맞춤입니다!
    <br>
    <br><b>이 책의 주요 내용</b>
    <br>■ 리액티브 애플리케이션 개발하기
    <br>■ 웹 애플리케이션과 RESTful 웹 서비스를 위한 스프링 MVC 사용
    <br>■ 스프링 보안을 사용한 애플리케이션 보안 처리
    <br>■ 스프링 5의 핵심 파악
</p>
<h1>📚 목차</h1>
<hr>
<p>
    <b>PART 1 스프링 기초 1</b>
    <br>CHAPTER 1 스프링 시작하기 3
    <br>1.1 스프링이란? 4
    <br>1.2 스프링 애플리케이션 초기 설정하기 7
    <br>1.2.1 STS를 사용해서 스프링 프로젝트 초기 설정하기 8
    <br>1.2.2 스프링 프로젝트 구조 살펴보기 12
    <br>1.3 스프링 애플리케이션 작성하기 18
    <br>1.3.1 웹 요청 처리하기 19
    <br>1.3.2 뷰 정의하기 20
    <br>1.3.3 컨트롤러 테스트하기 22
    <br>1.3.4 애플리케이션 빌드하고 실행하기 24
    <br>1.3.5 스프링 부트 DevTools 알아보기 26
    <br>1.3.6 리뷰하기 28
    <br>1.4 스프링 살펴보기 30
    <br>1.4.1 핵심 스프링 프레임워크 30
    <br>1.4.2 스프링 부트 31
    <br>1.4.3 스프링 데이터 31
    <br>1.4.4 스프링 시큐리티 32
    <br>1.4.5 스프링 통합과 배치 32
    <br>1.4.6 스프링 클라우드 32
    <br>요약 33
    <br>
    <br>CHAPTER 2 웹 애플리케이션 개발하기 34
    <br>2.1 정보 보여주기 35
    <br>2.1.1 도메인 설정하기 36
    <br>2.1.2 컨트롤러 클래스 생성하기 41
    <br>2.1.3 뷰 디자인하기 45
    <br>2.2 폼 제출 처리하기 51
    <br>2.3 폼 입력 유효성 검사하기 57
    <br>2.3.1 유효성 검사 규칙 선언하기 58
    <br>2.3.2 폼과 바인딩될 때 유효성 검사 수행하기 60
    <br>2.3.3 유효성 검사 에러 보여주기 62
    <br>2.4 뷰 컨트롤러로 작업하기 65
    <br>2.5 뷰 템플릿 라이브러리 선택하기 68
    <br>2.5.1 템플릿 캐싱 70
    <br>요약 71
    <br>
    <br>CHAPTER 3 데이터로 작업하기 72
    <br>3.1 JDBC를 사용해서 데이터 읽고 쓰기 73
    <br>3.1.1 퍼시스턴스를 고려한 도메인 객체 수정하기 75
    <br>3.1.2 JdbcTemplate 사용하기 76
    <br>3.1.3 스키마 정의하고 데이터 추가하기 84
    <br>3.1.4 타코와 주문 데이터 추가하기 87
    <br>3.2 스프링 데이터 JPA를 사용해서 데이터 저장하고 사용하기 103
    <br>3.2.1 스프링 데이터 JPA를 프로젝트에 추가하기 103
    <br>3.2.2 도메인 객체에 애노테이션 추가하기 104
    <br>3.2.3 JPA 리퍼지터리 선언하기 108
    <br>3.2.4 JPA 리퍼지터리 커스터마이징하기 112
    <br>요약 115
    <br>
    <br>CHAPTER 4 스프링 시큐리티 116
    <br>4.1 스프링 시큐리티 활성화하기 117
    <br>4.2 스프링 시큐리티 구성하기 120
    <br>4.2.1 인메모리 사용자 스토어 123
    <br>4.2.2 JDBC 기반의 사용자 스토어 125
    <br>4.2.3 LDAP 기반 사용자 스토어 132
    <br>4.2.4 사용자 인증의 커스터마이징 138
    <br>4.3 웹 요청 보안 처리하기 148
    <br>4.3.1 웹 요청 보안 처리하기 148
    <br>4.3.2 커스텀 로그인 페이지 생성하기 152
    <br>4.3.3 로그아웃하기 155
    <br>4.3.4 CSRF 공격 방어하기 155
    <br>4.4 사용자 인지하기 158
    <br>4.5 각 폼에 로그아웃 버튼 추가하고 사용자 정보 보여주기 164
    <br>요약 166
    <br>
    <br>CHAPTER 5 구성 속성 사용하기 167
    <br>5.1 자동-구성 세부 조정하기 168
    <br>5.1.1 스프링 환경 추상화 이해하기 169
    <br>5.1.2 데이터 소스 구성하기 171
    <br>5.1.3 내장 서버 구성하기 172
    <br>5.1.4 로깅 구성하기 174
    <br>5.1.5 다른 속성의 값 가져오기 176
    <br>5.2 우리의 구성 속성 생성하기 176
    <br>5.2.1 구성 속성 홀더 정의하기 180
    <br>5.2.2 구성 속성 메타데이터 선언하기 182
    <br>5.3 프로파일 사용해서 구성하기 185
    <br>5.3.1 프로파일 특정 속성 정의하기 186
    <br>5.3.2 프로파일 활성화하기 188
    <br>5.3.3 프로파일을 사용해서 조건별로 빈 생성하기 189
    <br>요약 191
    <br>
    <br><b>PART 2 통합된 스프링 193</b>
    <br>CHAPTER 6 REST 서비스 생성하기 195
    <br>6.1 REST 컨트롤러 작성하기 196
    <br>6.1.1 서버에서 데이터 가져오기 198
    <br>6.1.2 서버에 데이터 전송하기 204
    <br>6.1.3 서버의 데이터 변경하기 205
    <br>6.1.4 서버에서 데이터 삭제하기 208
    <br>6.2 하이퍼미디어 사용하기 209
    <br>6.2.1 하이퍼링크 추가하기 212
    <br>6.2.2 리소스 어셈블러 생성하기 215
    <br>6.2.3 embedded 관계 이름 짓기 220
    <br>6.3 데이터 기반 서비스 활성화하기 221
    <br>6.3.1 리소스 경로와 관계 이름 조정하기 224
    <br>6.3.2 페이징과 정렬 226
    <br>6.3.3 커스텀 엔드포인트 추가하기 228
    <br>6.3.4 커스텀 하이퍼링크를 스프링 데이터 엔드포인트에 추가하기 230
    <br>6.4 앵귤러 IDE 이클립스 플러그인 설치와 프로젝트 빌드 및 실행하기 231
    <br>6.4.1 앵귤러 IDE 이클립스 플러그인 설치하기 232
    <br>6.4.2 타코 클라우드 애플리케이션 빌드하고 실행하기 237
    <br>요약 244
    <br>
    <br>CHAPTER 7 REST 서비스 사용하기 245
    <br>7.1 RestTemplate으로 REST 엔드포인트 사용하기 246
    <br>7.1.1 리소스 가져오기(GET) 248
    <br>7.1.2 리소스 쓰기(PUT) 250
    <br>7.1.3 리소스 삭제하기(DELETE) 250
    <br>7.1.4 리소스 데이터 추가하기(POST) 251
    <br>7.2 Traverson으로 REST API 사용하기 252
    <br>7.3 REST API 클라이언트가 추가된 타코 클라우드 애플리케이션 빌드 및 실행하기 255
    <br>요약 258
    <br>
    <br>CHAPTER 8 비동기 메시지 전송하기 259
    <br>8.1 JMS로 메시지 전송하기 260
    <br>8.1.1 JMS 설정하기 260
    <br>8.1.2 JmsTemplate을 사용해서 메시지 전송하기 263
    <br>8.1.3 JMS 메시지 수신하기 271
    <br>8.2 RabbitMQ와 AMQP 사용하기 276
    <br>8.2.1 RabbitMQ를 스프링에 추가하기 277
    <br>8.2.2 RabbitTemplate을 사용해서 메시지 전송하기 278
    <br>8.2.3 RabbitMQ로부터 메시지 수신하기 283
    <br>8.3 카프카 사용하기 288
    <br>8.3.1 카프카 사용을 위해 스프링 설정하기 289
    <br>8.3.2 KafkaTemplate을 사용해서 메시지 전송하기 290
    <br>8.3.3 카프카 리스너 작성하기 292
    <br>8.4 비동기 메시지 전송과 수신 기능이 추가된 타코 클라우드 애플리케이션 빌드 및 실행하기 294
    <br>요약 299
    <br>
    <br>CHAPTER 9 스프링 통합하기 300
    <br>9.1 간단한 통합 플로우 선언하기 301
    <br>9.1.1 XML을 사용해서 통합 플로우 정의하기 303
    <br>9.1.2 Java로 통합 플로우 구성하기 305
    <br>9.1.3 스프링 통합의 DSL 구성 사용하기 307
    <br>9.2 스프링 통합의 컴포넌트 살펴보기 308
    <br>9.2.1 메시지 채널 310
    <br>9.2.2 필터 312
    <br>9.2.3 변환기 313
    <br>9.2.4 라우터 314
    <br>9.2.5 분배기 316
    <br>9.2.6 서비스 액티베이터 319
    <br>9.2.7 게이트웨이 321
    <br>9.2.8 채널 어댑터 322
    <br>9.2.9 엔드포인트 모듈 324
    <br>9.3 이메일 통합 플로우 생성하기 326
    <br>9.4 타코 클라우드 애플리케이션 빌드 및 실행하기 332
    <br>요약 336
    <br>
    <br><b>PART 3 리액티브 스프링 337</b>
    <br>CHAPTER 10 리액터 개요 339
    <br>10.1 리액티브 프로그래밍 이해하기 340
    <br>10.1.1 리액티브 스트림 정의하기 342
    <br>10.2 리액터 시작하기 344
    <br>10.2.1 리액티브 플로우의 다이어그램 345
    <br>10.2.2 리액터 의존성 추가하기 346
    <br>10.3 리액티브 오퍼레이션 적용하기 347
    <br>10.3.1 리액티브 타입 생성하기 348
    <br>10.3.2 리액티브 타입 조합하기 353
    <br>10.3.3 리액티브 스트림의 변환과 필터링 357
    <br>10.3.4 리액티브 타입에 로직 오퍼레이션 수행하기 368
    <br>10.4 리액티브 오퍼레이션 테스트 프로젝트 빌드 및 실행하기 370
    <br>요약 372
    <br>
    <br>CHAPTER 11 리액티브 API 개발하기 373
    <br>11.1 스프링 WebFlux 사용하기 373
    <br>11.1.1 스프링 WebFlux 개요 375
    <br>11.1.2 리액티브 컨트롤러 작성하기 377
    <br>11.2 함수형 요청 핸들러 정의하기 382
    <br>11.3 리액티브 컨트롤러 테스트하기 386
    <br>11.3.1 GET 요청 테스트하기 386
    <br>11.3.2 POST 요청 테스트하기 389
    <br>11.3.3 실행 중인 서버로 테스트하기 391
    <br>11.4 REST API를 리액티브하게 사용하기 392
    <br>11.4.1 리소스 얻기(GET) 393
    <br>11.4.2 리소스 전송하기 396
    <br>11.4.3 리소스 삭제하기 397
    <br>11.4.4 에러 처리하기 397
    <br>11.4.5 요청 교환하기 399
    <br>11.5 리액티브 웹 API 보안 401
    <br>11.5.1 리액티브 웹 보안 구성하기 402
    <br>11.5.2 리액티브 사용자 명세 서비스 구성하기 404
    <br>요약 406
    <br>
    <br>CHAPTER 12 리액티브 데이터 퍼시스턴스 407
    <br>12.1 스프링 데이터의 리액티브 개념 이해하기 408
    <br>12.1.1 스프링 데이터 리액티브 개요 409
    <br>12.1.2 리액티브와 리액티브가 아닌 타입 간의 변환 409
    <br>12.1.3 리액티브 리퍼지터리 개발하기 412
    <br>12.2 리액티브 카산드라 리퍼지터리 사용하기 412
    <br>12.2.1 스프링 데이터 카산드라 활성화하기 413
    <br>12.2.2 카산드라 데이터 모델링 이해하기 416
    <br>12.2.3 카산드라 퍼시스턴스의 도메인 타입 매핑 416
    <br>12.2.4 리액티브 카산드라 리퍼지터리 작성하기 423
    <br>12.3 리액티브 몽고DB 리퍼지터리 작성하기 426
    <br>12.3.1 스프링 데이터 몽고DB 활성화하기 427
    <br>12.3.2 도메인 타입을 문서로 매핑하기 429
    <br>12.3.3 리액티브 몽고DB 리퍼지터리 인터페이스 작성하기 432
    <br>요약 436
    <br>
    <br><b>PART 4 클라우드 네이티브 스프링 437</b>
    <br>CHAPTER 13 서비스 탐구하기 439
    <br>13.1 마이크로서비스 이해하기 440
    <br>13.2 서비스 레지스트리 설정하기 442
    <br>13.2.1 유레카 구성하기 447
    <br>13.2.2 유레카 확장하기 450
    <br>13.3 서비스 등록하고 찾기 452
    <br>13.3.1 유레카 클라이언트 속성 구성하기 453
    <br>13.3.2 서비스 사용하기 455
    <br>13.4 마이크로 서비스 관련 프로젝트의 빌드 및 실행하기 461
    <br>요약 467
    <br>
    <br>CHAPTER 14 클라우드 구성 관리 468
    <br>14.1 구성 공유하기 469
    <br>14.2 구성 서버 실행하기 470
    <br>14.2.1 구성 서버 활성화하기 471
    <br>14.2.2 Git 리퍼지터리에 구성 속성 저장하기 476
    <br>14.3 공유되는 구성 데이터 사용하기 480
    <br>14.4 애플리케이션이나 프로파일에 특정된 속성 제공하기 482
    <br>14.4.1 애플리케이션에 특정된 속성 제공하기 482
    <br>14.4.2 프로파일로부터 속성 제공하기 484
    <br>14.5 구성 속성들의 보안 유지하기 486
    <br>14.5.1 Git 백엔드의 속성들 암호화하기 486
    <br>14.5.2 Vault에 보안 속성 저장하기 490
    <br>14.6 실시간으로 구성 속성 리프레시하기 495
    <br>14.6.1 구성 속성을 수동으로 리프레시하기 496
    <br>14.6.2 구성 속성을 자동으로 리프레시하기 499
    <br>14.7 구성 서버와 구성 클라이언트 프로젝트의 빌드 및 실행하기 507
    <br>요약 511
    <br>
    <br>CHAPTER 15 실패와 지연 처리하기 512
    <br>15.1 서킷 브레이커 이해하기 512
    <br>15.2 서킷 브레이커 선언하기 515
    <br>15.2.1 지연 시간 줄이기 518
    <br>15.2.2 서킷 브레이커 한계값 관리하기 519
    <br>15.3 실패 모니터링하기 521
    <br>15.3.1 Hystrix 대시보드 개요 522
    <br>15.3.2 Hystrix 스레드 풀 이해하기 525
    <br>15.4 다수의 Hystrix 스트림 종합하기 527
    <br>15.5 Hystrix와 Turbine을 사용한 식자재 클라이언트 서비스 빌드 및 실행하기 530
    <br>요약 536
    <br>
    <br><b>PART 5 스프링 배포 537</b>
    <br>CHAPTER 16 스프링 부트 액추에이터 사용하기 539
    <br>16.1 액추에이터 개요 540
    <br>16.1.1 액추에이터의 기본 경로 구성하기 541
    <br>16.1.2 액추에이터 엔드포인트의 활성화와 비활성화 542
    <br>16.2 액추에이터 엔드포인트 소비하기 543
    <br>16.2.1 애플리케이션 기본 정보 가져오기 544
    <br>16.2.2 구성 상세 정보 보기 548
    <br>16.2.3 애플리케이션 활동 지켜보기 557
    <br>16.2.4 런타임 메트릭 활용하기 560
    <br>16.3 액추에이터 커스터마이징 563
    <br>16.3.1 /info 엔드포인트에 정보 제공하기 563
    <br>16.3.2 커스텀 건강 지표 정의하기 569
    <br>16.3.3 커스텀 메트릭 등록하기 570
    <br>16.3.4 커스텀 엔드포인트 생성하기 572
    <br>16.4 액추에이터 보안 처리하기 576
    <br>16.5 액추에이터 엔드포인트와 보안을 사용한 타코 서비스 빌드 및 실행하기 578
    <br>요약 583
    <br>
    <br>CHAPTER 17 스프링 관리하기 584
    <br>17.1 스프링 부트 Admin 사용하기 585
    <br>17.1.1 Admin 서버 생성하기 585
    <br>17.1.2 Admin 클라이언트 등록하기 587
    <br>17.2 Admin 서버 살펴보기 591
    <br>17.2.1 애플리케이션의 건강 상태 정보와 일반 정보 보기 592
    <br>17.2.2 핵심 메트릭 살펴보기 593
    <br>17.2.3 환경 속성 살펴보기 594
    <br>17.2.4 로깅 레벨을 보거나 설정하기 594
    <br>17.2.5 스레드 모니터링 595
    <br>17.2.6 HTTP 요청 추적하기 596
    <br>17.3 Admin 서버의 보안 597
    <br>17.3.1 Admin 서버에 로그인 활성화하기 598
    <br>17.3.2 액추에이터로 인증하기 598
    <br>17.4 Admin 서버 서비스 빌드 및 실행하기 600
    <br>요약 606
    <br>
    <br>CHAPTER 18 JMX로 스프링 모니터링하기 607
    <br>18.1 액추에이터 MBeans 사용하기 607
    <br>18.2 우리의 MBeans 생성하기 610
    <br>18.3 알림 전송하기 612
    <br>18.4 TacoCounter MBeans 빌드 및 사용하기 613
    <br>요약 619
    <br>
    <br>CHAPTER 19 스프링 배포하기 620
    <br>19.1 배포 옵션 621
    <br>19.2 WAR 파일 빌드하고 배포하기 622
    <br>19.3 클라우드 파운드리에 JAR 파일 푸시하기 625
    <br>19.4 도커 컨테이너에서 스프링 부트 실행하기 628
    <br>요약 633
    <br>
    <br>APPENDIX A 스프링 부트 프로젝트 생성하기 634
    <br>A.1 STS를 사용해서 프로젝트 생성하기 634
    <br>A.2 IntelliJ IDEA 설치 및 프로젝트 생성하기 638
    <br>A.2.1 윈도우 시스템에서 IntelliJ IDEA 설치하기
    <br>A.2.2 맥OS에서 IntelliJ IDEA 설치하기
    <br>A.2.3 리눅스에서 IntelliJ IDEA 설치하기
    <br>A.2.4 IntelliJ IDEA 프로젝트 생성하기
    <br>A.3 NetBeans 설치 및 프로젝트 생성하기 645
    <br>A.4 start.spring.io에 직접 접속하여 프로젝트 생성하기 651
    <br>A.5 명령행에서 프로젝트 생성하기 654
    <br>A.5.1 curl과 Initializr API
    <br>A.5.2 스프링 부트 명령행 인터페이스 사용하기
    <br>A.6 메타-프레임워크를 사용해서 스프링 애플리케이션 생성하기 658
    <br>A.7 프로젝트 빌드하고 실행하기 659
    <br>A.8 curl 설치하기 660
    <br>
    <br>찾아보기 664
</p>
<h1>📚 본문중에서</h1>
<hr>
<p>
    스프링은 스프링 애플리케이션 컨텍스트(Spring application context)라는 컨테이너(container)를 제공하는데, 이것은 애플리케이션 컴포넌트들을 생성하고 관리한다. 그리고 애플리케이션 컴포넌트 또는 빈bean들은 스프링 애플리케이션 컨텍스트 내부에서 서로 연결되어 완전한 애플리케이션을 만든다. 벽돌, 모르타르, 목재, 못, 배관, 배선이 함께 어우러져 집을 구성하는 것과 비슷하다.
    <br>(/ p.12)
    <br>
    <br>스프링 애플리케이션의 보안에서 맨 먼저 할 일은 스프링 부트 보안 스타터 의존성을 빌드 명세에 추가하는 것이다. 3장까지 작성이 끝난 taco-clould 프로젝트의 pom.xml 파일을 편집기 창에서 열고 다음의
    <br>스프링 애플리케이션의 보안에서 맨 먼저 할 일은 스프링 부트 보안 스타터 의존성을 빌드 명세에 추가하는 것이다. 3장까지 작성이 끝난 taco-clould 프로젝트의 pom.xml 파일을 편집기 창에서 열고 다음의 항목을 추가하자. 여기서 추가하는 첫 번째 항목은 스프링 부트 보안 스타터 의존성이고, 두 번째는 보안 테스트 의존성이다(이번 장에서는 3장의 스프링 데이터 JPA까지 작성된 taco-cloud 프로젝트를 계속 사용할 것이다. 이 프로젝트는 다운로드한 코드의 \Ch03-JPA 서브 디렉터리에 있다. 또한, 이번 장이 끝났을 때 완성된 taco-cloud 프로젝트는 \Ch04에 있다).
</p>
<h1>📚 관련 이미지</h1>
<hr>
<p>
    <img alt="" src="http://bimage.interpark.com/goods_image/3/7/7/9/332803779b.jpg">
</p>
<p>
    <a href="http://book.interpark.com/product/BookDisplay.do?_method=detail&sc.prdNo=332803779&gclid=EAIaIQobChMIlbzj9cGE6wIVSAVgCh1PkQTTEAYYASABEgL1WPD_BwE">About more</a>
</p>
',
31500, '/images/products/book/book01.jpg', '2020-03-20 09:17:13', '2020-03-20 09:17:13');

INSERT INTO product (name, category, description, price, image, create_date, update_date)
VALUES ('SN57 만사형통 스트레치 드레스셔츠', '옷',
'
<center>
    <h1>👗 상품 상세 설명</h1>
    <hr>
    <p>
       <b>아니 120수 셔츠를 이 가격에?</b>
        <br>
        <br>
        <u>
            120번수로 셔츠는 촘촘하고 부드러운 조직감이 특징으로 셔츠의 옷감중 고품질 원단로 분류됩니다.
            <br>프리미엄급 셔츠를 합리적인 가격으로 만나보십시오.
        </u>
        <br>
        <br>움직임이 편한 2WAY스판이 적용되어 활동성이 뛰어납니다.
        <br>기본 핏에 맵시가 나도록 핏이 살짝 잡혀 있는 형태입니다.
        <br>
        <br>레귤러 칼라로 기본 원형의 클래식한 드레스 셔츠입니다.
    </p>
</center>
<p>
    <a href="https://www.kantukan.co.kr/shop/mall/prdt/prdt_view.php?pidx=24013&kp_code=67043807&src=image&kw=0003A9&gclid=EAIaIQobChMIjvCEhsKE6wIVCWoqCh2zXQPwEAYYASABEgJnTPD_BwE">About more</a>
</p>
',
37800, '/images/products/dress/dress01.png', '2020-03-20 09:17:13', '2020-03-20 09:17:13');

INSERT INTO product (name, category, description, price, image, create_date, update_date)
VALUES ('생초밥 2인', '식품',
'
<h1>🍣 스시(초밥)는 원래 19세기초 도쿄에서 시작된 대중음식</h1>
<p>스시은 초로 간을 한 밥에 해산물 등을 올리거나 섞은 요리. 일식을 대표하는 요리로 해외에서도 인기가 많다. 「샤리」라 불리는 한입 크기로 만든 밥에 「네타」라 불리는 해산물을 올린 「스시」이 가장 일반적이다. 소량의 고추냉이가 사용되며 간장을 찍어 먹는다.</p>
<p>
    <b style="color: red;">스시의 역사</b>
    <img alt="" src="https://rimage.gnst.jp/livejapan.com/public/article/detail/a/00/00/a0000370/img/ko/a0000370_parts_580db8503c1ee.jpg?20200702132805&q=80&rw=608&rh=435">
    <br>스시의 전신은 「나레즈시」라 불리는 생선을 소금과 쌀로 절여 발효시킨 보존식이다. 스시은 19세기 초 현재 도쿄에 해당하는 에도에서 시작되었다고 한다. 당시에는 냉장 기술이 발달하지 않아 생선을 데치거나 간장으로 절여서 처리했다. 원래 스시은 포장마차에서 염가로 팔리는 대중적인 음식이었지만, 고급화된 가게가 점차 늘어났다.
</p>
<p>
    <b style="color: red;">스시의 네타(재료)</b>
    <img alt="" src="https://rimage.gnst.jp/livejapan.com/public/article/detail/a/00/00/a0000370/img/ko/a0000370_parts_580db865e8ebf.jpg?20200702132805&q=80&rw=608&rh=435">
    <br>「아카미(붉은살)」은 참치나 다랑어 등을 가리킨다. 참치의 지방이 많은 부분인 「토로」는 특히 인기가 많다. 도미나 방어를 비롯한　「시로미(흰살)」은 담백한 맛. 정어리나 고등어 등 몸이 빛나는 생선은 「히카리모노」라 불린다. 독특한 냄새를 잡기 위해 식초에 재우는 방법도 있다. 그 외에는 붕장어나 달걀말이 등 익힌 재료도 사용된다.
</p>
<p>
    <b style="color: red;">다양한 스시</b>
    <img alt="" src="https://rimage.gnst.jp/livejapan.com/public/article/detail/a/00/00/a0000370/img/ko/a0000370_parts_580db879edfef.jpg?20200702132805&q=80&rw=608&rh=435">
    <br>스시의 모양에는 재료를 밥과 김으로 말은 「마키즈시」나, 여러 재료를 밥 위에 올린 「치라시즈시」 등도 있다. 해산물을 사용하지 않고 유부에 밥을 넣은 「이나리즈시」는 달콤짭짤한 맛이 특징이다. 밥과 재료를 상자에 넣고 눌러서 굳힌 「오시즈시」는 오사카나 토야마에서 많이 만든다.
</p>
<p>
    <b style="color: red;">스시 먹는 법</b>
    <img alt="" src="https://rimage.gnst.jp/livejapan.com/public/article/detail/a/00/00/a0000370/img/ko/a0000370_parts_580db889c373b.jpg?20200702132805&q=80&rw=608&rh=435">
    <br>접시에 간장을 따르고 간장에 스시을 직접 찍어 먹는다. 샤리(밥)에 간장을 찍으면 밥이 간장을 흡수하니, 네타(재료)쪽으로 찍는 것이 포인트다. 또 스시은 손으로 집어 먹어도 매너 위반이 아니다.
    가게에서는 보통 얇게 썬 생강을 식초에 절인 「가리」, 그리고 녹차가 스시과 함께 제공된다. 초생강과 녹차로 입을 헹구면서 담백한 재료부터 진한 맛의 재료 순으로 먹는 것을 추천한다
</p>
<p>
    <b style="color: red;">스시를 먹을 수 있는 곳</b>
    <img alt="" src="https://rimage.gnst.jp/livejapan.com/public/article/detail/a/00/00/a0000370/img/ko/a0000370_parts_580db89ccd071.jpg?20200702132805&q=80&rw=608&rh=435">
    <br>현재는 요리하는 사람이 앞에서 스시을 만들어 주는 스시집부터 스시을 올린 접시가 가게 안에 설치된 벨트 컨베이어 위에 올려져, 손님이 먹고 싶은 스시을 자유롭게 고르는 「회전 스시」, 집까지 배달해주는 「배달 스시」 등 다양한 스타일로 즐길 수 있다. 가격도 천엔 정도의 저렴한 것부터 고급스러운 것까지 천차만별이니 취향에 맞게 즐겨보자.
</p>
<p>
    <a href="https://livejapan.com/ko/article-a0000370/">About more</a>
</p>
',
38000, '/images/products/food/food01.jpeg', '2020-03-20 09:17:13', '2020-03-20 09:17:13');
