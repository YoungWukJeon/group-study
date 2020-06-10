# 기타 라이브러리 업데이트 

## Collection
Collection API 의 가장 큰 업데이트는 **스트림** 의 등장  
ㄴ 전에 스트립에 대해서 설명했기 때문에 스트림이 아닌 변경사항에 대해서 설명하는 듯 함  
  
### 추가 메서드 
Collection 에서 추가된 메서드들  

- Map
    * getOrDefault, forEach, compute, computeIfAbsent, computeIfPresent
- Iterable
    * forEach, spliterator
- Iterator
    * forEachRemaining
- Collection
    * removeIf, stream, parallelStream
- List
    * replaceAll, sort
- BitSet
    * stream   

### Map
가장 많이 업데이트 된 인터페이스  

- getOrDefault: Map 에 데이터가 없을 경우 default 값을 반환해줌 
```java
@Test
public void getOrDefault_비교(){
    Map<String, Integer> carInventory = new HashMap<String, Integer>();
    final String key = "Aston Martin";

    // 기존 방식
    Integer count = 0;
    if(carInventory.containsKey(key)){
        count = carInventory.get(key);
    }

    // 새로운 방식
    Integer newCount = carInventory.getOrDefault(key, 0);

    System.out.println(count + " vs " + newCount);
    Assert.assertEquals(count, newCount);
}
```

주의 할 점은 key 에 null 이 있다면 null 이 반환된다는 점이다    
```java
@Test
public void getOrDefault_value_null(){
    Map<String, Integer> carInventory = new HashMap<String, Integer>();
    final String key = "Aston Martin";

    carInventory.put(key, null);

    Integer count = carInventory.getOrDefault(key, 0);

    System.out.println(count);
    Assert.assertNull(count);
}
```
  
- computeIfAbsent: 해당 key 로 value 를 검사하고 null 이면 Function 을 호출한 결과를 map 에 put 한뒤 반환 
```java
default V computeIfAbsent(K key,
        Function<? super K, ? extends V> mappingFunction) {
    Objects.requireNonNull(mappingFunction);
    V v;
    if ((v = get(key)) == null) {
        V newValue;
        if ((newValue = mappingFunction.apply(key)) != null) {
            put(key, newValue);
            return newValue;
        }
    }

    return v;
}
```
로직 비교   
```java
public static String getDataOld(String key){
    String data = cache.get(key);
    if(data == null){
        data = db.get(key);
        cache.put(key, data);
    }
    return data;
}

public static String getDataNew(String key){
    return cache.computeIfAbsent(key, (k) -> db.get(k));
}
```
ㄴ Function 을 지정함으로써 코드가 짧아진다    
ㄴ 다른 메서드들은 [공식 자바 문서](http://goo.gl/Hf20r4) 를 확인  
  
### Collection
- removeIf : predicate 와 일치하는 모든 요소를 컬랙션에서 제거할 수 있음  
ㄴ filter 는 새로운 스트림을 생성하지만 removeIf 는 기존 데이터를 변경한다는 점에서 차이점이 존재함   
```java
@Test
public void removeIf_비교(){
    // 기존 방식
    List<Integer> baseList = IntStream.range(1, 10).boxed().collect(Collectors.toList());
    Iterator<Integer> iterator = baseList.iterator();
    while(iterator.hasNext()){
        Integer value = iterator.next();
        if(value % 3 == 0){
            iterator.remove();
        }
    }
    System.out.println("base: " + baseList.toString());

    // 새로운 방식
    List<Integer> newList = IntStream.range(1, 10).boxed().collect(Collectors.toList());
    newList.removeIf(val -> (val % 3 == 0));

    System.out.println("new: " + newList);

    Assert.assertArrayEquals(baseList.toArray(), newList.toArray());
}
```
ㄴ 사실 removeIf 내부적으로 기존 방식 코드를 구현하고 있음  
```java
default boolean removeIf(Predicate<? super E> filter) {
    Objects.requireNonNull(filter);
    boolean removed = false;
    final Iterator<E> each = iterator();
    while (each.hasNext()) {
        if (filter.test(each.next())) {
            each.remove();
            removed = true;
        }
    }
    return removed;
}
```

### List 
- replaceAll : 주어진 UnaryOperator 를 이용해서 List 모든 요소에 replace 를 수행함  
```java
@Test
public void replaceAll_비교(){
    // 기존 방식
    List<Integer> oldNumbers = Arrays.asList(1, 2, 3, 4, 5);
    for(int i = 0; i < oldNumbers.size(); i++){
        Integer num = oldNumbers.get(i);
        oldNumbers.set(i, num + 2);
    }
    System.out.println(oldNumbers);

    // 새로운 방식
    List<Integer> newNumbers = Arrays.asList(1, 2, 3, 4, 5);
    newNumbers.replaceAll(n -> n + 2);
    System.out.println(newNumbers);
}
```
실제 구현된 코드 
```java
default void replaceAll(UnaryOperator<E> operator) {
    Objects.requireNonNull(operator);
    final ListIterator<E> li = this.listIterator();
    while (li.hasNext()) {
        li.set(operator.apply(li.next()));
    }
}
```

- sort 

### Collections 
TODO: 
컬렉션 관련 동작을 수행하고 반환하는 역할을 수행하던 클래스  
불변의, 동기화된, 검사된, 빈 NavigableMap 과 


### Comparator
디폴트 메서드와 정적 메서드가 추가되었고 인스턴스 메서드도 추가되었음  
인스턴스 메서드  
- reversed: 현재 Comparator 를 역순으로 반전시킨 Comparator 반환 
- thenComparing: 두 객체가 같을 때 다른 Comparator 를 사용하는 Comparator 반환 
- thenComparingInt, thenComparingDouble, thenComparingLong : thenComparing 과 같은 동작을 수행하지만 기본형에 특화된 Function 을 인자로 받는 메서드  
    * ToIntFunction, ToDoubleFunction, ToLongFunction
  
정적 메서드  
- comparingInt, comparingDouble, comparingLong : comparing 과 동일한 동작을 수행하지만 기본형에 특화된 Function 을 인자로 받음  
- naturalOrder : Comparable 객체에 순서를 적용한 Comparable 반환 
- nullsFirst : null 객체를 우선적으로 정렬 Comparator 반환 
- nullsLast : null 객체를 나중 정렬 Comparator 반환
- reversOrder : 역순 출력할 Comparator 반환 

## 동시성 
자바 8 에서는 동시성과 관련된 기능이 많이 업데이트 되었음  
ㄴ 7장의 병렬 스트립과 11장의 CompletableFuture 가 대표적인 동시성 관련된 업데이트   
  
### 아토믹
atomic 변수는 원자성을 보장하는 변수  
멀티 스레드 환경에서 동기화 문제를 synchronized 키워드를 이용해서 변수에 lock 걸곤 했는데,  
이런 키워드 없이 동기화 문제를 해결하기 위해 고안된 방법  
ㄴ 일반적으로 동기화 문제는 synchronized, atomic, volatile 3가지 키워드를 이용해서 해결함  


[아토믹 티스토리 블로그](https://beomseok95.tistory.com/225)
  
java.util.concurrent.atomic 패키지는 AtomicInteger, AtomicLong 등 단일 변수에 아토믹 연산을 지원하는 숫자 클래스를 제공  
- getAndUpdate: 함수의 결과를 현재값에 아토믹하게 적용하고 기존값을 변경 
- updateAndGet: 함수의 결과를 쳔재값에 아토믹하게 적용하고 업데이트 된 값을 변경
- getAndAccumulate: 함수를 현재값과 인수값에 적용하고 기존 값을 반환 
- accumulateAndGet: 함수를 현재값과 인수값에 적용하고 업데이트된 값을 반환 

Adder 와 Accumulator    
여러 스레트에서 읽기 동작보다 갱신 동작을 많이 수행한다면 Atomic 은 성능이 좋지 않음  
따라서 LongAdder, LongAccumulator 등을 사용하라고 권장  
ㄴ 해당 클래스들을 스레드 간의 경쟁을 줄일 수 있다고 함  
```java
LongAdder adder = new LongAdder();
adder.add(10);
long sum = adder.sum(); // 이 시점에 합계를 구함

LongAccumulator acc = new LongAccumulator();
acc.accumulate(10);
long result = acc.get(); // 이 시점에 결과를 얻음  
```

### ConcurrentHashMap
멀티 스레드 환경에 친화적인 HashMap  
Java 8 에서 성능을 개선했음  
해시 충돌이 발생했을때, 버킷이 너무 커지지 않도록 동적으로 연결 리스트를 정렬 트리로 교체함  
ConcurrentHashMap 은 상태를 lock 하지 않고 요소에 직접 연산을 수행  
ㄴ 단, 순서에 의존하지 않아야 하며 다른 객체나 값에 의존하지 않아야 함   
모든 연산에 병렬성 한계값(parallelism threshold)을 지정해야 함   
- parallelism threshold = 1 : 공용 스레드 풀을 사용해서 병렬성을 최대화 
- parallelism threshold = Long.MAC_VALUE : 하나의 스레드를 이용 

추가된 연산들  
- forEach: key/value 에 주어진 동작을 수행
    * forEachKeys, forEachValues
- reduce: 전달한 리듀싱 함수로 key/value 결과를 도출
    * reduceKeys, reduceValues
- search: 함수가 null 이 아닌 결과를 도출할 때 까지 key/value 에 함수를 적용함 
    * searchKeys, searchValues
- mappingCount : 맵의 매핑 개수를 반환 
- keySet : key 의 집합을 반환

### Arrays
- parallelSort : 병렬로 정렬 
- setAll : 배열의 모든 요소를 순차적으로 설정
- parallelAll : 모든 요소를 병렬로 설정 
- parallelPrefix : 이항 연산자를 이용해서 배열의 각 요소를 병렬로 누적 
```java
int[] ones = new int[10];
Arrays.fill(ones, 1);
Arrays.parallelPrefix(ones, (a, b) -> a + b);
// 1, 2, 3, 4, ... 10
```

### Numbers 
- Short, Integer, Long, Float, Double 클래스에 정적 메서드 sum, min, mac 추가 
- Integer, Long 에는 부호 없는 값을 처리하는 compareUnsigned, divideUnsigned, remainUnsigned, toUnsignedString 추가 
- Integer, Long 에는 부호 없는 int, long 값으로 파싱하는 parseUnsignedInt, parseUnsignedLong 추가 
- Byte, Short 인수를 비부호 int, long 으로 변환하는 toUnsignedInt, toUnsignedLong 추가 
- Double, Float 에는 유한 소수인지 검사하는 isFinite 추가 
- Boolean 두 불리언 값을 and, or, xor 연산을 수행하는 logicalAnd, logicalOr, logicalXor 추가 
- BigInteger 기본형으로 바꿀수 있는 byteValueExact, shortValueExact, intValueExact, longValueExact 추가 
ㄴ 대신 정보 손실이 발생하면 예외가 발생  
   
### Math
연산 결과에 오버플로우가 발생했을 때 산술 예외를 발생시키는 addExact, subtractExact, multiplyExact, incrementExact, decrementExact, negateExact 등의 메서드 추가  
long 을 int 로 변경하는 정적 메서드 toIntExact 와 floorMod, floorDiv, nextDown 등의 정적 메서드 추가  

## Files
파일 관련 처리를 스트림으로 할 수 있음  
- Files.list : 주어진 디렉토리의 개체를 포함하는 Stream<Path> 를 생성
ㄴ 재궈로 수행되지 않으며, 스트림의 게으른 속성을 이용할 수 있음 
- Files.walk : Files.list 와 동일하지만 재귀적으로 처리함  (dfs)
- Files.find : 디렉터리를 재귀적으로 탐색하면서 predicate 와 일치하는 개체를 찾아서 Stream<Path> 를 생성 

## 리플랙션
이전 부록에 살펴보았던 어노테이션 수정에 따라서 리플렉션 API 도 업데이트 됨  
java.lang.reflect.Parameter 가 추가되어 메서드 파라미터 정보를 이용한 리플렉션이 가능함  

## String
구분 기호로 문자열을 연결하는 join 메서드가 추가됨  
 
