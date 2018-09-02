package com.diegomfv.android.realestatemanager;

import org.junit.Test;

import java.util.concurrent.ExecutionException;

/**
 * Created by Diego Fajardo on 18/08/2018.
 */
public class FirebasePushIdGeneratorTest {

    @Test
    public void testGeneration() throws InterruptedException, ExecutionException {

//        final ExecutorService es = new ThreadPoolExecutor(20, 20, 10000, TimeUnit.DAYS, new ArrayBlockingQueue<>(10000));
//        final ConcurrentMap<String, Boolean> set = new ConcurrentHashMap<>(1000000);
//        final AtomicBoolean hasDuplicates = new AtomicBoolean(false);
//
//        final List<Future<?>> futures = new ArrayList<>();
//
//        for (int i=0; i<100; i++) {
//            Future<?> f = es.submit(new Runnable() {
//                @Override
//                public void run() {
//                    for (int i=0; i<10000; i++) {
//                        final String id = FirebasePushIdGenerator.generate();
//                        if (set.putIfAbsent(id, Boolean.TRUE) != null) {
//                            hasDuplicates.set(true);
//                            System.out.println("Duplicate detected! " + id);
//                        }
//                    }
//                }
//            });
//            futures.add(f);
//        }
//
//        for (Future<?> f : futures) {
//            f.get();
//        }
//
//        es.shutdown();
//        es.awaitTermination(20L, TimeUnit.MINUTES);
//
//        Assert.assertFalse("There are duplicate keys!", hasDuplicates.get());

    }

}