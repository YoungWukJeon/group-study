# 새로운 날짜와 시간 API
Java 1.0에서의 날짜 시간 클래스
java.util.Date
```java
Date date = new Date(117, 8, 21);

# 출력 결과
Thu Sep 21 00:00:00 CET 2017
```

Date class의 문제점
- 직관적이지 않음
- Date 클래스의 toString로 반환되는 문자열을 추가로 활용하기 어려움  
- CET(중앙 유럽 시간)으로 설정되어 있어 추가적인 변환작업이 필요

Java 1.1에서 새로운 java.util.Calendar class 추가
- 달의 index가 0부터 시작
- Date와 Calendar 두 가지 클래스가 통일되지 않아 개발자들간의 문제 발생(DateFormat 같은 경우 Date class에만 존재)
- Date와 Calendar 모두 가변 클래스로 유지보수가 어려움

위와 같은 문제점으로 Joda-Time와 같은 third-party 날짜/시간 라이브러리 사용  
Joda-time의 많은 기능을 java 8에서 java.time 패키지로 추가

## 12.1 LocalDate, LocalTime, Instant, Duration, Period
java.time 패키지에는 LocalDate, LocalTime, LocalDateTime, Instant, Duration, Period 클래스를 제공  

### 12.1.1 LocalDate와 LocalTime 사용
LocalDate는 어떤 시간대 정보도 가지지 않음

LocalDate를 사용하는 간단한 예
```java
LocalDate date = LocalDate.of(2017, 9, 21); // 2017-09-21 날짜 생성
int year = date.getYear();
Month month = date.getMonth();
int day = date.getDayOfMonth();
DayOfWeek dow = date.getDayOfWeek();
int len = date.lengthOfMonth();
boolean leap = date.isLeapYear();           // 윤년 여부 판단
LocalDate today = LocalDate.now();          // 현재 날짜 생성
```

get 메서드로 TemporalField를 전달해 정보를 얻는 방법  
TemporalField는 시간 관련 객체에서 어떤 필드의 값에 접근할지 정의하는 인터페이스  
열거자 ChronoField는 TemporalField 인터페이스를 정의하므로 ChronoField의 열거자를 이용해 아래와 같이 사용 가능
```java
int year = date.get(ChronoField.YEAR);
int month = date.get(ChronoField.MONTH_OF_YEAR);
int day = date.get(ChronoField.DAY_OF_MONTH);
```

날짜가 아닌 시간에 대한 것은 LocalTime를 이용해 사용  
```java
LocalTime time = LocalTime.of(13, 45, 20);
int hour = time.getHour();
int minute = time.getMinute();
int second = time.getSecond();
```

문자열로 LocalDate와 LocalTime 만들기  
parse를 이용해 문자열을 date, time로 변경
```java
LocalDate date = LocalDate.parse("2017-09-21");
LocalTime time = LocalTime.parse("13:45:20");
```

### 12.1.2 날짜와 시간 조합
날짜와 시간을 모두 표현하는 LocalDateTime  
LocalDateTime는 위의 예제에서 본 Date와 Time를 하나의 클래스로 나타냄
```java
LocalDate date = LocalDate.parse("2017-09-21");
LocalTime time = LocalTime.parse("13:45:20");

LocalDateTime dt1 = LocalDateTime.of(2017, Month.SEPTEMBER, 21, 13, 45, 20);
LocalDateTime dt2 = LocalDateTime.of(date, time);
LocalDateTime dt3 = date.atTime(13,45,20);
LocalDateTime dt4 = date.atTime(time);
LocalDateTime dt5 = time.atDate(date);

LocalDate date1 = dt1.toLocalDate();
LocalTime time1 = dt1.toLocalTime();
```

### 12.1.3 Instant 클래스 : 기계와 날짜와 시간
기계의 관점에서의 시간은 연속된 시간에서 특정 지점을 기준으로 하나의 큰 수로 표현하는 것이 자연스러움  
java.time.Instant 클래스를 이용해 유닉스 시간(1970년 1월 1일 0시 0분 0초)로 표현  
Instant 클래스를 이용해 나노초(10억분의 1초)까지 표현  
모두 같은 시간의 표현하는 예
```java
Instant t1 = Instant.ofEpochSecond(3);
Instant t2 = Instant.ofEpochSecond(3, 0);
Instant t3 = Instant.ofEpochSecond(2, 1_000_000_000);
Instant t4 = Instant.ofEpochSecond(4, -1_000_000_000);
```
Instant의 경우 기계 친화적 시간을 제공하여 사람이 읽기 편한 시간정보는 제공하지 않음
```java
int day = Instant.now().get(ChronoField.DAY_OF_MONTH);
```
위의 코드는 UnsupportedTemporalTypeException 발생

### 12.1.4 Duration과 Period 정의
Duration과 Period를 이용해 두 시간 사이의 차이를 알 수 있음  
Duration은 초 / 나노초  
Period는 연 / 월 / 일
```java
LocalDate time1 = LocalDate.of(2012, 10, 24);
LocalDate time2 = LocalDate.of(2013,12,20);

Instant t1 = Instant.ofEpochSecond(3);
Instant t2 = Instant.ofEpochSecond(5, 3);

Duration d1 = Duration.between(t1, t2);
Period d2 = Period.between(time1, time2);
```

맨 처음 기존 자바의 날짜와 시간 관련 API들은 모두 가변 클래스라 유지보수가 힘들다는 단점이 존재한다고 했다.  
그 단점들을 보완하여 현재까지 살펴본 클래스들은 모두 불변 클래스라는 특징을 가지고 있다.  
그렇다면 만들어진 날짜나 시간에 대한 연산이 필요한 경우 어떻게 해야할까

## 12.2 날짜 조정, 파싱, 포매팅
만들어진 날짜 / 시간을 조정하기 위해 withAttribute 메서드를 이용하는데 아래의 예를 살펴보자  
해당 결과는 기존의 객체는 변경하지 않고 새로운 객체를 반환한다.

절대적 변경 방법
```java
LocalDate date1 = LocalDate.of(2014,3,18);
LocalDate date2 = date1.withYear(2011);
LocalDate date3 = date2.withDayOfMonth(25);
LocalDate date4 = date3.with(ChronoField.MONTH_OF_YEAR, 9);
```

상대적 변경 방법
```java
LocalDate date5 = LocalDate.of(2014,3,18);
LocalDate date6 = date5.plusWeeks(1);
LocalDate date7 = date6.minusYears(3);
LocalDate date8 = date7.plus(6, ChronoUnit.MONTHS);
```

### 12.2.1 TemporalAdjusters 사용하기
요일에 대한 내용이나 해당 월의 첫날, 마지막 날 등 특별한 날에 대한 조정이 필요할 때 사용  
TemporalAdjuster을 이용하여 날짜 조정 가능
```java
LocalDate date1 = LocalDate.of(2014, 3, 18);
LocalDate date2 = date1.with(nextOrSame(DayOfWeek.SUNDAY));
LocalDate date3 = date2.with(lastDayOfMonth());
```

### 12.2.2 날짜와 시간 객체 출력과 파싱
날짜와 시간 관련 포매팅과 파싱은 꼭 필요한 작업  
java.time.format가 새로 추가되어 해당 작업을 강화  
DateTimeFormatter을 이용해 포매터를 정의할 수 있음  
DateTimeFormatter에 정의된 BASIC_ISO_DATE와 ISO_LOCAL_DATE를 통해 날짜난 시간을 특정 형식의 문자열로 변경 (format)
```java
LocalDate date1 = LocalDate.of(2014, 3, 18);
String s1 = date1.format(DateTimeFormatter.BASIC_ISO_DATE);
String s2 = date1.format(DateTimeFormatter.ISO_LOCAL_DATE);
```

문자열을 날짜로 변환 (parse)
```java
LocalDate date2 = LocalDate.parse("20140318", DateTimeFormatter.BASIC_ISO_DATE);
LocalDate date3 = LocalDate.parse("2014-03-20", DateTimeFormatter.ISO_LOCAL_DATE);
```

DateTimeFormat custom
지정한 형식에 맞게 날짜형식을 변경해줌
```java
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
LocalDate date1 = LocalDate.of(2014, 3, 18);
String formattedDate = date1.format(formatter);
LocalDate date2 = LocalDate.parse(formattedDate, formatter);
```

특정 지역의 언어를 이용해서 날짜를 변경하는 것도 가능
```java
DateTimeFormatter italianFormatter = DateTimeFormatter.ofPattern("d. MMMM yyyy", Locale.ITALIAN);
LocalDate date1 = LocalDate.of(2014, 3, 18);
String formattedDate = date1.format(italianFormatter);
LocalDate date2 = LocalDate.parse(formattedDate, italianFormatter);

System.out.println(date1);
System.out.println(formattedDate);
System.out.println(date2);
```

## 12.3 다양한 시간대와 캘린더 이용하기
표준 시간 지역대를 묶어서 사용하는 시간대 관련 규칙을 지정하고 사용하는 방법에 대해 알아보자  

### 12.3.1 시간대 사용하기
시간대는 ZoneId를 이용해서 사용할 수 있음.
ZoneId는 {지역}/{도시} 형식으로 사용
```java
ZoneId romeZone = ZoneId.of("Europe/Rome");
LocalDate date = LocalDate.of(2014, Month.MARCH, 18);
ZonedDateTime zdt1 = date.atStartOfDay(romeZone);
LocalDateTime dateTime = LocalDateTime.of(2014,Month.MARCH, 18, 13, 45);
ZonedDateTime zdt2 = dateTime.atZone(romeZone);
Instant instant = Instant.now();
ZonedDateTime zdt3 = instant.atZone(romeZone);
```

### 12.3.2 UTC/Greenwich 기준의 고정 오프셋
UTC/GMT를 기준으로 시간대를 표현하는 방법에 대해서 알아보자  
UTC의 기준인 런던을 기준으로 뉴욕은 5시간 느리기에 뉴욕의 시간은 UTC-5로 표현할 수 있다  
코드를 통해 살펴보면 아래와 같다.
```java
ZoneOffset newYorkOffset = ZoneOffset.of("-5:00")
```
위와 같이 사용하는 것은 권장하는 방법은 아닌데 그 이유는 썸머타임을 사용하는 외국의 경우 고정적인 오프셋 적용이 힘들어 많이 사용하는 방법은 아니다

### 12.3.3 대안 캘린더 시스템 사용하기
기본적으로 사용하는 캘린더 시스템이 아닌 다른 캘린더 시스템도 있는데  
ThaiBuddhistDate, MinguoDate, JappneseDate, HijraDate  
위의 4개 클래스가 그런 클래스이다.

타임존 관리방법(실무에서)