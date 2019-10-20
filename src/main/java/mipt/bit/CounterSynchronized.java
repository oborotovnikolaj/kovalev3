package mipt.bit;

public class CounterSynchronized implements Counter {
    private long value;
    @Override
    public synchronized void increment() {
//        long tmp = value;
//        Thread.yield();
//        value = tmp + 1;
        value++;
    }

    @Override
    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}
