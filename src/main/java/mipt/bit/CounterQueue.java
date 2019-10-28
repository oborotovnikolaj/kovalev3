package mipt.bit;

import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class CounterQueue implements Counter {

    BlockingQueue<String> queue = new PriorityBlockingQueue<>(Collections.singletonList("work"));
    //    По идее, при выходе из синхронайза все локальные переменные передаются в основную память Ram
//    Можно ли было здесь обойтись без волатайла?
    private volatile long value;
    @Override
    public void increment() {
        try {
            String take = queue.take();

//            long tmp = value;
//            Thread.yield();//чтобы сломать
//            value = tmp + 1;
                value++;
            queue.add(take);
        } catch (InterruptedException e) {
            e.printStackTrace();
            }
    }

    @Override
    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}
