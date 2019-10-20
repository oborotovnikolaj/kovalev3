/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package mipt.bit;

import org.junit.Test;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.List;
import java.util.concurrent.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static junit.framework.TestCase.assertEquals;

public class MyBenchmark {

    private static Counter counterSynchronized = new CounterSynchronized();
    private static Counter counterLock = new CounterLock();
    private static Counter counterVolatile = new CounterAtomicLong();
    private static Counter counterSemaphore = new CounterSemaphore();
    private static Counter counterQueue = new CounterQueue();
    private static Counter counterBakery = new CounterBakery(4);

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }

    @State(Scope.Thread)
    public static class MyState {

        int incrementCallsCount = 1000000;
        int nThreads = 4;

        @Setup(Level.Invocation)
        public void doSetup() {
            counterSynchronized.setValue(0);
            counterLock.setValue(0);
            counterVolatile.setValue(0);
            counterSemaphore.setValue(0);
            counterQueue.setValue(0);
            counterBakery.setValue(0);
        }
    }


        @Benchmark
        @BenchmarkMode(Mode.All)
        @Fork(value = 3)
        @Warmup(iterations = 3)
        @Measurement(iterations = 10)
        @Group("testCounterSynchronized")
        @GroupThreads(1)
        public void testCounterSynchronized(MyState state, Blackhole blackhole) throws ExecutionException, InterruptedException {
//            System.out.println(counterSynchronized.getValue() + " при старте каунт");
            long l = System.currentTimeMillis();
            long count = checkCounter(state.incrementCallsCount, state.nThreads, counterSynchronized);
            blackhole.consume(count);
//            System.out.println(System.currentTimeMillis() - l);
        }

    @Benchmark
    @BenchmarkMode(Mode.All)
    @Fork(value = 3)
    @Warmup(iterations = 3)
    @Measurement(iterations = 10)
    @Group("testCounterLock")
    @GroupThreads(1)
    public void testCounterLock(MyState state, Blackhole blackhole) throws ExecutionException, InterruptedException {
//        long l = System.currentTimeMillis();
        long count = checkCounter(state.incrementCallsCount, state.nThreads,  counterLock);
        blackhole.consume(count);
//        System.out.println(System.currentTimeMillis() - l);
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    @Fork(value = 3)
    @Warmup(iterations = 3)
    @Measurement(iterations = 10)
    @Group("testCounterAtomicLong")
    @GroupThreads(1)
    public void testCounterAtomicLong(MyState state, Blackhole blackhole) throws ExecutionException, InterruptedException {
//        long l = System.currentTimeMillis();
        long count = checkCounter(state.incrementCallsCount, state.nThreads,  counterVolatile);
        blackhole.consume(count);
//        System.out.println(System.currentTimeMillis() - l);
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    @Fork(value = 3)
    @Warmup(iterations = 3)
    @Measurement(iterations = 10)
    @Group("testCounterSemaphore")
    @GroupThreads(1)
    public void testCounterSemaphore(MyState state, Blackhole blackhole) throws ExecutionException, InterruptedException {
//        long l = System.currentTimeMillis();
        long count = checkCounter(state.incrementCallsCount, state.nThreads,  counterSemaphore);
        blackhole.consume(count);
//        System.out.println(System.currentTimeMillis() - l);
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    @Fork(value = 3)
    @Warmup(iterations = 3)
    @Measurement(iterations = 10)
    @Group("testCounterBakery")
    @GroupThreads(1)
    public void testCounterBakery(MyState state, Blackhole blackhole) throws ExecutionException, InterruptedException {
//        long l = System.currentTimeMillis();
        long count = checkCounter(state.incrementCallsCount, state.nThreads,  counterBakery);
        blackhole.consume(count);
//        System.out.println(System.currentTimeMillis() - l);
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    @Fork(value = 3)
    @Warmup(iterations = 3)
    @Measurement(iterations = 10)
    @Group("testCounterQueue")
    @GroupThreads(1)
    public void testCounterQueue(MyState state, Blackhole blackhole) throws ExecutionException, InterruptedException {
//        long l = System.currentTimeMillis();
        long count = checkCounter(state.incrementCallsCount, state.nThreads,  counterQueue);
        blackhole.consume(count);
//        System.out.println(System.currentTimeMillis() - l);
    }

        private static long checkCounter(int incrementCallsCount, int nThreads, Counter counter) throws ExecutionException, InterruptedException {

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

            ExecutorService executors = Executors.newFixedThreadPool(nThreads, threadFactory);


            List<Future> futures = range(0, incrementCallsCount)
                    .mapToObj(i -> executors.submit(incrementRunnable(counter)))
                    .collect(toList());
            for (Future future : futures) {
                future.get();
            }
            assertEquals("Oops! Smth is wrong!", incrementCallsCount, counter.getValue());
//            System.out.println("count " + counter.getValue());
            return  counter.getValue();
        }

        private static Runnable incrementRunnable(Counter counter) {
            return counter::increment;
        }

    }
