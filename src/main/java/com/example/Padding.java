package com.example;

//import org.openjdk.jol.info.ClassLayout;

import java.util.concurrent.CountDownLatch;

public class Padding {
    private static class Data {
        long p0, p1, p2, p3, p4, p5, p6;
        volatile long value0;
        volatile int value1;
    }

    static class SharedCounters {
        volatile Data data; //NO OK
        Object pi1, pi2, pi3, pi4, pi5, pi6, pi7, pi8, pi9, pi10, pi11, pi12, pi13, pi14, pi15;

        volatile short c0;//OK
        short pc1, pc2, pc3, pc4, pc5, pc6, pc7, pc8, pc9, pc10, pc11, pc12, pc13, pc14, pc15,
                pc16, pc17, pc18, pc19, pc20, pc21, pc22, pc23, pc24, pc25, pc26, pc27, pc28, pc29, pc30, pc31;

        long pli1, pli2, pli3, pli4, pli5, pli6, pli7;
        volatile int i1;

        volatile boolean b2;
        boolean pb1, pb2, pb3, pb4, pb5, pb6, pb7, pb8, pb9, pb10, pb11, pb12, pb13, pb14, pb15, pb16,
                pb17, pb18, pb19, pb20, pb21, pb22, pb23, pb24, pb25, pb26, pb27, pb28, pb29, pb30, pb31, pb32,
                pb33, pb34, pb35, pb36, pb37, pb38, pb39, pb40, pb41, pb42, pb43, pb44, pb45, pb46, pb47, pb48,
                pb49, pb50, pb51, pb52, pb53, pb54, pb55, pb56, pb57, pb58, pb59, pb60, pb61, pb62, pb63;

        volatile long l3;
        long pl0, pl1, pl2, pl3, pl4, pl5, pl6, pl7;

    }

    public static void main(String[] args) throws InterruptedException {
//        System.out.println(ClassLayout.parseClass(Data.class).toPrintable());
//        System.out.println(ClassLayout.parseClass(SharedCounters.class).toPrintable());
        long start = System.currentTimeMillis();
        SharedCounters sc = new SharedCounters();
        sc.data = new Data();
        CountDownLatch countDownLatch = new CountDownLatch(6);
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
                    sc.i1++;
                }
                countDownLatch.countDown();
            }
        });
        t4.start();
        Thread t5 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int j = 0; j < 100_000_000; j++) {
                    sc.b2 = !sc.b2;
                }
                countDownLatch.countDown();
            }
        });
        t5.start();
        Thread t6 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int j = 0; j < 100_000_000; j++) {
                    sc.l3++;
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
