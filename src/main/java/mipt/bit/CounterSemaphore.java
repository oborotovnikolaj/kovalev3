package mipt.bit;

import java.util.concurrent.Semaphore;

public class CounterSemaphore implements Counter {
    private Semaphore semaphore = new Semaphore(1);
    private long value;
    @Override
    public void increment() {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        long tmp = value;
//        Thread.yield();
//        value = tmp + 1;
        value++;
        semaphore.release();
    }

    @Override
    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}
