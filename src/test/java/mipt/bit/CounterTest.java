package mipt.bit;

import org.junit.Test;

import java.util.List;
import java.util.concurrent.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static junit.framework.TestCase.assertEquals;

public class CounterTest {
    private static final Counter counterSynchronized = new CounterSynchronized();
    private static final Counter counterLock = new CounterLock();
    private static final Counter counterVolatile = new CounterAtomicLong();
    private static final Counter counterSemaphore = new CounterSemaphore();
    private static final Counter counterQueue = new CounterQueue();
    private static final Counter counterBakery = new CounterBakery(4);

    @Test
    public void testCounterSynchronized() throws ExecutionException, InterruptedException {
        long l = System.currentTimeMillis();
        checkCounter(1000000, 4, counterSynchronized);
        System.out.println(System.currentTimeMillis() - l);
    }

    @Test
    public void testCounterLock() throws ExecutionException, InterruptedException {
        long l = System.currentTimeMillis();
        checkCounter(1000000, 4, counterLock);
        System.out.println(System.currentTimeMillis() - l);
    }

    @Test
    public void testCounterAtomicLong() throws ExecutionException, InterruptedException {
        long l = System.currentTimeMillis();
        checkCounter(1000000, 4, counterVolatile);
        System.out.println(System.currentTimeMillis() - l);
    }

    @Test
    public void testCounterSemaphore() throws ExecutionException, InterruptedException {
        long l = System.currentTimeMillis();
        checkCounter(1000000, 4, counterSemaphore);
        System.out.println(System.currentTimeMillis() - l);
    }

//    возможно превышен мак кол-во элементов в листе
    @Test
    public void testCounterBakery() throws ExecutionException, InterruptedException {
        long l = System.currentTimeMillis();
        checkCounter(1000000, 4, counterBakery);
        System.out.println(System.currentTimeMillis() - l);
    }

    @Test
    public void testCounterQueue() throws ExecutionException, InterruptedException {
        long l = System.currentTimeMillis();
        checkCounter(1000000, 4, counterQueue);
        System.out.println(System.currentTimeMillis() - l);
    }

    private static  void checkCounter(int incrementCallsCount, int nThreads, Counter counter) throws ExecutionException, InterruptedException {

        ThreadFactory threadFactory = new ThreadFactory() {
            int threadNumber;
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName(String.valueOf(threadNumber));
                threadNumber++;
                return thread;
            }
        };

        ExecutorService executors = Executors.newFixedThreadPool(nThreads,threadFactory);


        List<Future> futures = range(0, incrementCallsCount)
                .mapToObj(i -> executors.submit(incrementRunnable(counter)))
                .collect(toList());
        for (Future future : futures) {
            future.get();
        }
        assertEquals("Oops! Smth is wrong!", incrementCallsCount, counter.getValue());
        System.out.println("count " + counter.getValue());
    }

    private static Runnable incrementRunnable(Counter counter){
        return counter::increment;
    }
}
