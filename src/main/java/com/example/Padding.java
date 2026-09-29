package com.example;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class Padding {
    private static class Data {
        long value0;
        int value1;
    }

    static class SharedCounters {
        Data data;
        short c0;
        int c1;
        boolean c2;
        long c3;
    }

    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();
        SharedCounters sc = new SharedCounters();
        sc.data = new Data();
        CountDownLatch countDownLatch = new CountDownLatch(4);
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int j = 0; j < 100_000_000; j++) {
                    sc.data.value0++;
                }
                countDownLatch.countDown();
            }
        });
        t1.start();
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int j = 0; j < 100_000_000; j++) {
                    sc.data.value1++;
                }
                countDownLatch.countDown();
            }
        });
        t2.start();
        Thread t3 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int j = 0; j < 100_000_000; j++) {
                    sc.c0++;
                }
                countDownLatch.countDown();
            }
        });
        t3.start();
        Thread t4 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int j = 0; j < 100_000_000; j++) {
                    sc.c2 = !sc.c2;
                }
                countDownLatch.countDown();
            }
        });
        t4.start();
        Thread t5 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int j = 0; j < 100_000_000; j++) {
                    sc.c3++;
                }
                countDownLatch.countDown();
            }
        });
        t5.start();
        Thread t6 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int j = 0; j < 100_000_000; j++) {
                    sc.c1++;
                }
                countDownLatch.countDown();
            }
        });
        t6.start();

        countDownLatch.await();
        long end = System.currentTimeMillis();
        System.out.println("time elapsed: " + (end - start) / 1000f);
    }

}
