package com.example;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.openjdk.jcstress.infra.runners.ForkedTestConfig;
import org.openjdk.jcstress.infra.collectors.TestResult;
import org.openjdk.jcstress.infra.runners.Runner;
import org.openjdk.jcstress.infra.runners.WorkerSync;
import org.openjdk.jcstress.util.Counter;
import org.openjdk.jcstress.os.AffinitySupport;
import org.openjdk.jcstress.vm.AllocProfileSupport;
import org.openjdk.jcstress.infra.runners.ResourceEstimator;
import org.openjdk.jcstress.infra.runners.VoidThread;
import org.openjdk.jcstress.infra.runners.LongThread;
import org.openjdk.jcstress.infra.runners.CounterThread;
import com.example.RequestIdServiceTest;
import org.openjdk.jcstress.infra.results.II_Result;

public final class RequestIdServiceTest_jcstress extends Runner<II_Result> {

    volatile WorkerSync workerSync;

    public RequestIdServiceTest_jcstress(ForkedTestConfig config) {
        super(config);
    }

    @Override
    public void sanityCheck(Counter<II_Result> counter) throws Throwable {
        jcstress_sanityCheck_API(counter);
        jcstress_sanityCheck_Resource(counter);
    }

    private static class JcstressThread_APICheck_writer1 extends VoidThread {
        RequestIdServiceTest t;
        RequestIdServiceTest s;
        II_Result r;

        public JcstressThread_APICheck_writer1(RequestIdServiceTest t, RequestIdServiceTest s, II_Result r) {
            super("JcstressThread_APICheck_writer1");
            this.t = t;
            this.s = s;
            this.r = r;
        }

        public void internalRun() {
            s.writer1(r);
        };

        public void purge() {
            t = null;
            s = null;
            r = null;
        }
    }

    private static class JcstressThread_APICheck_writer2 extends VoidThread {
        RequestIdServiceTest t;
        RequestIdServiceTest s;
        II_Result r;

        public JcstressThread_APICheck_writer2(RequestIdServiceTest t, RequestIdServiceTest s, II_Result r) {
            super("JcstressThread_APICheck_writer2");
            this.t = t;
            this.s = s;
            this.r = r;
        }

        public void internalRun() {
            s.writer2(r);
        };

        public void purge() {
            t = null;
            s = null;
            r = null;
        }
    }

    private void jcstress_sanityCheck_API(Counter<II_Result> counter) throws Throwable {
        final RequestIdServiceTest s = new RequestIdServiceTest();
        final II_Result r = new II_Result();
        VoidThread a0 = new JcstressThread_APICheck_writer1(null, s, r);
        VoidThread a1 = new JcstressThread_APICheck_writer2(null, s, r);
        a0.start();
        a1.start();
        a0.join();
        if (a0.throwable() != null) {
            throw a0.throwable();
        }
        a0.purge();
        a1.join();
        if (a1.throwable() != null) {
            throw a1.throwable();
        }
        a1.purge();
        counter.record(r, 1);
    }

    private static class JcstressThread_ResourceCheck_writer1 extends LongThread {
        RequestIdServiceTest[] ss;
        II_Result[] rs;
        int size;

        public JcstressThread_ResourceCheck_writer1(RequestIdServiceTest[] ss, II_Result[] rs, int size) {
            super("JcstressThread_ResourceCheck_writer1");
            this.ss = ss;
            this.rs = rs;
            this.size = size;
        }

        public long internalRun() {
            long a1 = AllocProfileSupport.getAllocatedBytes();
            jcstress_check_writer1(ss, rs, size);
            long a2 = AllocProfileSupport.getAllocatedBytes();
            return a2 - a1;
        }

        private void jcstress_check_writer1(RequestIdServiceTest[] ls, II_Result[] lr, int size) {
            for (int c = 0; c < size; c++) {
                ls[c].writer1(lr[c]);
            }
        }

        public void purge() {
            ss = null;
            rs = null;
        }
    }

    private static class JcstressThread_ResourceCheck_writer2 extends LongThread {
        RequestIdServiceTest[] ss;
        II_Result[] rs;
        int size;

        public JcstressThread_ResourceCheck_writer2(RequestIdServiceTest[] ss, II_Result[] rs, int size) {
            super("JcstressThread_ResourceCheck_writer2");
            this.ss = ss;
            this.rs = rs;
            this.size = size;
        }

        public long internalRun() {
            long a1 = AllocProfileSupport.getAllocatedBytes();
            jcstress_check_writer2(ss, rs, size);
            long a2 = AllocProfileSupport.getAllocatedBytes();
            return a2 - a1;
        }

        private void jcstress_check_writer2(RequestIdServiceTest[] ls, II_Result[] lr, int size) {
            for (int c = 0; c < size; c++) {
                ls[c].writer2(lr[c]);
            }
        }

        public void purge() {
            ss = null;
            rs = null;
        }
    }

    private static class TestResourceEstimator implements ResourceEstimator {
        final Counter<II_Result> counter;

        public TestResourceEstimator(Counter<II_Result> counter) {
            this.counter = counter;
        }

        public void runWith(int size, long[] cnts) {
            long time1 = System.nanoTime();
            long alloc1 = AllocProfileSupport.getAllocatedBytes();
            RequestIdServiceTest[] ls = new RequestIdServiceTest[size];
            II_Result[] lr = new II_Result[size];
            for (int c = 0; c < size; c++) {
                RequestIdServiceTest s = new RequestIdServiceTest();
                II_Result r = new II_Result();
                lr[c] = r;
                ls[c] = s;
            }
            LongThread a0 = new JcstressThread_ResourceCheck_writer1(ls, lr, size);
            LongThread a1 = new JcstressThread_ResourceCheck_writer2(ls, lr, size);
            a0.start();
            a1.start();
            try {
                a0.join();
                cnts[0] += a0.result();
                a0.purge();
            } catch (InterruptedException e) {
            }
            try {
                a1.join();
                cnts[0] += a1.result();
                a1.purge();
            } catch (InterruptedException e) {
            }
            for (int c = 0; c < size; c++) {
                counter.record(lr[c], 1);
            }
            long time2 = System.nanoTime();
            long alloc2 = AllocProfileSupport.getAllocatedBytes();
            cnts[0] += alloc2 - alloc1;
            cnts[1] += time2 - time1;
        }
    }

    private void jcstress_sanityCheck_Resource(Counter<II_Result> counter) throws Throwable {
        config.adjustStrideCount(new TestResourceEstimator(counter));
    }

    @Override
    public ArrayList<CounterThread<II_Result>> internalRun() {
        int len = config.strideSize * config.strideCount;
        RequestIdServiceTest[] ls = new RequestIdServiceTest[len];
        II_Result[] lr = new II_Result[len];
        for (int c = 0; c < len; c++) {
            ls[c] = new RequestIdServiceTest();
            lr[c] = new II_Result();
        }
        workerSync = new WorkerSync(false, 2, config.spinLoopStyle);

        control.stopping = false;

        if (config.localAffinity) {
            try {
                AffinitySupport.tryBind();
            } catch (Exception e) {
                // Do not care
            }
        }

        ArrayList<CounterThread<II_Result>> threads = new ArrayList<>(2);
        threads.add(new JcstressThread_writer1(ls, lr, null));
        threads.add(new JcstressThread_writer2(ls, lr, null));

        for (CounterThread<II_Result> t : threads) {
            t.start();
        }

        if (config.time > 0) {
            try {
                TimeUnit.MILLISECONDS.sleep(config.time);
            } catch (InterruptedException e) {
            }
        }

        control.stopping = true;

        return threads;
    }

    public static void jcstress_ni_consume_final(Counter<II_Result> cnt, RequestIdServiceTest[] ls, II_Result[] lr, RequestIdServiceTest test, int len, int a) {
        int left = a * len / 2;
        int right = (a + 1) * len / 2;
        for (int c = left; c < right; c++) {
            II_Result r = lr[c];
            RequestIdServiceTest s = ls[c];
            cnt.record(r, 1);
        }
    }

    public static void jcstress_consume_reinit(Counter<II_Result> cnt, RequestIdServiceTest[] ls, II_Result[] lr, RequestIdServiceTest test, int len, int a) {
        int left = a * len / 2;
        int right = (a + 1) * len / 2;
        for (int c = left; c < right; c++) {
            II_Result r = lr[c];
            RequestIdServiceTest s = ls[c];
            ls[c] = new RequestIdServiceTest();
            cnt.record(r, 1);
            r.r1 = 0;
            r.r2 = 0;
        }
    }

    public class JcstressThread_writer1 extends CounterThread<II_Result> {
        RequestIdServiceTest[] ss;
        II_Result[] rs;
        RequestIdServiceTest test;

        public JcstressThread_writer1(RequestIdServiceTest[] ss, II_Result[] rs, RequestIdServiceTest test) {
            super("JcstressThread_writer1");
            this.ss = ss;
            this.rs = rs;
            this.test = test;
        }

        public Counter<II_Result> internalRun() {
            return jcstress_iteration_writer1();
        }

        private Counter<II_Result> jcstress_iteration_writer1() {
            int len = config.strideSize * config.strideCount;
            int stride = config.strideSize;
            Counter<II_Result> counter = new Counter<>();
            if (config.localAffinity) AffinitySupport.bind(config.localAffinityMap[0]);
            while (true) {
                WorkerSync sync = workerSync;
                int check = 0;
                for (int start = 0; start < len; start += stride) {
                    jcstress_stride_writer1(start, start + stride);
                    check += 2;
                    sync.awaitCheckpoint(check);
                }
                if (sync.stopping) {
                    jcstress_ni_consume_final(counter, ss, rs, null, len, 0);
                    return counter;
                } else {
                    jcstress_consume_reinit(counter, ss, rs, null, len, 0);
                }
                if (sync.tryStartUpdate()) {
                    workerSync = new WorkerSync(control.stopping, 2, config.spinLoopStyle);
                }
                sync.postUpdate();
            }
        }

        private void jcstress_stride_writer1(int start, int end) {
            RequestIdServiceTest[] ls = ss;
            II_Result[] lr = rs;
            for (int c = start; c < end; c++) {
                RequestIdServiceTest s = ls[c];
                II_Result r = lr[c];
                int trap_r = r.jcstress_trap;
                s.writer1(r);
            }
        }

        public void purge() {
            ss = null;
            rs = null;
            test = null;
        }
    }

    public class JcstressThread_writer2 extends CounterThread<II_Result> {
        RequestIdServiceTest[] ss;
        II_Result[] rs;
        RequestIdServiceTest test;

        public JcstressThread_writer2(RequestIdServiceTest[] ss, II_Result[] rs, RequestIdServiceTest test) {
            super("JcstressThread_writer2");
            this.ss = ss;
            this.rs = rs;
            this.test = test;
        }

        public Counter<II_Result> internalRun() {
            return jcstress_iteration_writer2();
        }

        private Counter<II_Result> jcstress_iteration_writer2() {
            int len = config.strideSize * config.strideCount;
            int stride = config.strideSize;
            Counter<II_Result> counter = new Counter<>();
            if (config.localAffinity) AffinitySupport.bind(config.localAffinityMap[1]);
            while (true) {
                WorkerSync sync = workerSync;
                int check = 0;
                for (int start = 0; start < len; start += stride) {
                    jcstress_stride_writer2(start, start + stride);
                    check += 2;
                    sync.awaitCheckpoint(check);
                }
                if (sync.stopping) {
                    jcstress_ni_consume_final(counter, ss, rs, null, len, 1);
                    return counter;
                } else {
                    jcstress_consume_reinit(counter, ss, rs, null, len, 1);
                }
                if (sync.tryStartUpdate()) {
                    workerSync = new WorkerSync(control.stopping, 2, config.spinLoopStyle);
                }
                sync.postUpdate();
            }
        }

        private void jcstress_stride_writer2(int start, int end) {
            RequestIdServiceTest[] ls = ss;
            II_Result[] lr = rs;
            for (int c = start; c < end; c++) {
                RequestIdServiceTest s = ls[c];
                II_Result r = lr[c];
                int trap_r = r.jcstress_trap;
                s.writer2(r);
            }
        }

        public void purge() {
            ss = null;
            rs = null;
            test = null;
        }
    }

}
