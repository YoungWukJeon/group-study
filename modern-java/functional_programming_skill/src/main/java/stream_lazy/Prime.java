package stream_lazy;

import java.util.stream.IntStream;

public class Prime {

    static IntStream primes(IntStream numbers){
        int head = numbers.findFirst().getAsInt();
        IntStream tail = numbers.skip(1);

        return IntStream.concat(
                IntStream.of(head),
                primes(tail.filter(n -> n % head != 0))
        );
    }

    static LazyLinkedList<Integer> lazyPrimes(LazyLinkedList<Integer> numbers){
        return new LazyLinkedList<Integer>(
                numbers.head(),
                () -> {
                    LazyLinkedList<Integer> tail = numbers.tail();
                    LazyLinkedList<Integer> filtered = tail.filter(n -> n % numbers.head() != 0);
                    return lazyPrimes(filtered);
                }
        );
    }


}
