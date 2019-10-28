package mipt.bit;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CounterLock implements Counter {
    Lock lock = new ReentrantLock();
    //    По идее, при выходе из синхронайза все локальные переменные передаются в основную память Ram
//    Можно ли было здесь обойтись без волатайла?
    private volatile long value;
    @Override
    public void increment() {
        lock.lock();
//        long tmp = value;
//        Thread.yield();
//        value = tmp + 1;
        value++;
        lock.unlock();
    }

    @Override
    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}
