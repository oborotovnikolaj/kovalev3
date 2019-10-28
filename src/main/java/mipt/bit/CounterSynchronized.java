package mipt.bit;

public class CounterSynchronized implements Counter {
//    По идее, при выходе из синхронайза все локальные переменные передаются в основную память Ram
//    Можно ли было здесь обойтись без волатайла?
    private volatile long value;
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
