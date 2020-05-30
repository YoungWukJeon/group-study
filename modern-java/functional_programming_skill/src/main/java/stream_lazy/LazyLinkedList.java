package stream_lazy;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class LazyLinkedList<T> implements LazyList<T> {
    private final T head;
    private final Supplier<LazyLinkedList<T>> tail;

    public LazyLinkedList(T head, Supplier<LazyLinkedList<T>> tail) {
        this.head = head;
        this.tail = tail;
    }

    @Override
    public T head() {
        return head;
    }

    @Override
    public LazyLinkedList<T> tail() {
        return tail.get();
    }

    public boolean isEmpty(){
        return false;
    }

    /** 소수 구하기에서 사용할 filter 메소드 */
    public LazyLinkedList<T> filter(Predicate<T> predicate){
        if(isEmpty()){
            return this;
        }else if(predicate.test(this.head())){
            return new LazyLinkedList<T>(head(), () -> tail().filter(predicate));
        }else{
            return tail().filter(predicate);
        }
    }

    public static LazyLinkedList<Integer> from(int n){
        return new LazyLinkedList<Integer>(n, () -> from(n+1));
    }

    public static <T> void printAll(LazyList<T> lazyList){
        while(!lazyList.isEmpty()){
            System.out.println(lazyList.head());
            lazyList = lazyList.tail();
        }
    }

    public static <T> void printAllRecursive(LazyList<T> lazyList){
        if(lazyList.isEmpty()){
            return;
        }
        System.out.println(lazyList.head());
        printAllRecursive(lazyList.tail());
    }
}
