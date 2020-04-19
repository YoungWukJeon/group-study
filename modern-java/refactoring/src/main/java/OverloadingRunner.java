import java.util.function.Consumer;

public class OverloadingRunner {

    public void run(Consumer consumer){
        consumer.accept("consumer");
    }

    public void run(Runnable runnable){
        runnable.run();
    }

    public void run(Execute runnable){
        runnable.run();
    }
}
