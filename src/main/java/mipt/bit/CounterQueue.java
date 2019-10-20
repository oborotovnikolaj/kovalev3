package mipt.bit;

import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class CounterQueue implements Counter {

    BlockingQueue<String> queue = new PriorityBlockingQueue<>(Collections.singletonList("work"));
    private long value;
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
