package stream_lazy;

import java.util.stream.IntStream;

public class Main {
    static void primesTest(){
        // 무한 스트림 생성
        IntStream numbers = IntStream.iterate(2, n -> n+1);

        IntStream primes = Prime.primes(numbers);
    }


    static void lazyListTest(){
        LazyList<Integer> numbers = LazyLinkedList.from(2);
        int two = numbers.head();
        int three = numbers.tail().head();
        int four = numbers.tail().tail().head();

        System.out.println(two + ", " + three + ", " + four);
    }

    static void lazyPrimesTest(){
        LazyLinkedList.printAll(Prime.lazyPrimes(LazyLinkedList.from(2)));
    }

    public static void main(String[] args){
//        primesTest();
//        lazyListTest();

        lazyPrimesTest();

    }

}
