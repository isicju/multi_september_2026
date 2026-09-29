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
import com.example.FeatureFlagsTest;
import org.openjdk.jcstress.infra.results.ZZ_Result;

public final class FeatureFlagsTest_jcstress extends Runner<ZZ_Result> {

    volatile WorkerSync workerSync;

    public FeatureFlagsTest_jcstress(ForkedTestConfig config) {
        super(config);
    }

    @Override
    public void sanityCheck(Counter<ZZ_Result> counter) throws Throwable {
        jcstress_sanityCheck_API(counter);
        jcstress_sanityCheck_Resource(counter);
    }

    private static class JcstressThread_APICheck_writer1 extends VoidThread {
        FeatureFlagsTest t;
        FeatureFlagsTest s;
        ZZ_Result r;

        public JcstressThread_APICheck_writer1(FeatureFlagsTest t, FeatureFlagsTest s, ZZ_Result r) {
            super("JcstressThread_APICheck_writer1");
            this.t = t;
            this.s = s;
            this.r = r;
        }

        public void internalRun() {
            s.writer1();
        };

        public void purge() {
            t = null;
            s = null;
            r = null;
        }
    }

    private static class JcstressThread_APICheck_writer2 extends VoidThread {
        FeatureFlagsTest t;
        FeatureFlagsTest s;
        ZZ_Result r;

        public JcstressThread_APICheck_writer2(FeatureFlagsTest t, FeatureFlagsTest s, ZZ_Result r) {
            super("JcstressThread_APICheck_writer2");
            this.t = t;
            this.s = s;
            this.r = r;
        }

        public void internalRun() {
            s.writer2();
        };

        public void purge() {
            t = null;
            s = null;
            r = null;
        }
    }

    private void jcstress_sanityCheck_API(Counter<ZZ_Result> counter) throws Throwable {
        final FeatureFlagsTest s = new FeatureFlagsTest();
        final ZZ_Result r = new ZZ_Result();
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
            s.arbiter(r);
        counter.record(r, 1);
    }

    private static class JcstressThread_ResourceCheck_writer1 extends LongThread {
        FeatureFlagsTest[] ss;
        ZZ_Result[] rs;
        int size;

        public JcstressThread_ResourceCheck_writer1(FeatureFlagsTest[] ss, ZZ_Result[] rs, int size) {
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

        private void jcstress_check_writer1(FeatureFlagsTest[] ls, ZZ_Result[] lr, int size) {
            for (int c = 0; c < size; c++) {
                ls[c].writer1();
            }
        }

        public void purge() {
            ss = null;
            rs = null;
        }
    }

    private static class JcstressThread_ResourceCheck_writer2 extends LongThread {
        FeatureFlagsTest[] ss;
        ZZ_Result[] rs;
        int size;

        public JcstressThread_ResourceCheck_writer2(FeatureFlagsTest[] ss, ZZ_Result[] rs, int size) {
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

        private void jcstress_check_writer2(FeatureFlagsTest[] ls, ZZ_Result[] lr, int size) {
            for (int c = 0; c < size; c++) {
                ls[c].writer2();
            }
        }

        public void purge() {
            ss = null;
            rs = null;
        }
    }

    private static class TestResourceEstimator implements ResourceEstimator {
        final Counter<ZZ_Result> counter;

        public TestResourceEstimator(Counter<ZZ_Result> counter) {
            this.counter = counter;
        }

        public void runWith(int size, long[] cnts) {
            long time1 = System.nanoTime();
            long alloc1 = AllocProfileSupport.getAllocatedBytes();
            FeatureFlagsTest[] ls = new FeatureFlagsTest[size];
            ZZ_Result[] lr = new ZZ_Result[size];
            for (int c = 0; c < size; c++) {
                FeatureFlagsTest s = new FeatureFlagsTest();
                ZZ_Result r = new ZZ_Result();
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
                ls[c].arbiter(lr[c]);
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

    private void jcstress_sanityCheck_Resource(Counter<ZZ_Result> counter) throws Throwable {
        config.adjustStrideCount(new TestResourceEstimator(counter));
    }

    @Override
    public ArrayList<CounterThread<ZZ_Result>> internalRun() {
        int len = config.strideSize * config.strideCount;
        FeatureFlagsTest[] ls = new FeatureFlagsTest[len];
        ZZ_Result[] lr = new ZZ_Result[len];
        for (int c = 0; c < len; c++) {
            ls[c] = new FeatureFlagsTest();
            lr[c] = new ZZ_Result();
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

        ArrayList<CounterThread<ZZ_Result>> threads = new ArrayList<>(2);
        threads.add(new JcstressThread_writer1(ls, lr, null));
        threads.add(new JcstressThread_writer2(ls, lr, null));

        for (CounterThread<ZZ_Result> t : threads) {
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

    public static void jcstress_ni_consume_final(Counter<ZZ_Result> cnt, FeatureFlagsTest[] ls, ZZ_Result[] lr, FeatureFlagsTest test, int len, int a) {
        int left = a * len / 2;
        int right = (a + 1) * len / 2;
        for (int c = left; c < right; c++) {
            ZZ_Result r = lr[c];
            FeatureFlagsTest s = ls[c];
            s.arbiter(r);
            cnt.record(r, 1);
        }
    }

    public static void jcstress_consume_reinit(Counter<ZZ_Result> cnt, FeatureFlagsTest[] ls, ZZ_Result[] lr, FeatureFlagsTest test, int len, int a) {
        int left = a * len / 2;
        int right = (a + 1) * len / 2;
        for (int c = left; c < right; c++) {
            ZZ_Result r = lr[c];
            FeatureFlagsTest s = ls[c];
            s.arbiter(r);
            ls[c] = new FeatureFlagsTest();
            cnt.record(r, 1);
            r.r1 = false;
            r.r2 = false;
        }
    }

    public class JcstressThread_writer1 extends CounterThread<ZZ_Result> {
        FeatureFlagsTest[] ss;
        ZZ_Result[] rs;
        FeatureFlagsTest test;

        public JcstressThread_writer1(FeatureFlagsTest[] ss, ZZ_Result[] rs, FeatureFlagsTest test) {
            super("JcstressThread_writer1");
            this.ss = ss;
            this.rs = rs;
            this.test = test;
        }

        public Counter<ZZ_Result> internalRun() {
            return jcstress_iteration_writer1();
        }

        private Counter<ZZ_Result> jcstress_iteration_writer1() {
            int len = config.strideSize * config.strideCount;
            int stride = config.strideSize;
            Counter<ZZ_Result> counter = new Counter<>();
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
            FeatureFlagsTest[] ls = ss;
            ZZ_Result[] lr = rs;
            for (int c = start; c < end; c++) {
                FeatureFlagsTest s = ls[c];
                s.writer1();
            }
        }

        public void purge() {
            ss = null;
            rs = null;
            test = null;
        }
    }

    public class JcstressThread_writer2 extends CounterThread<ZZ_Result> {
        FeatureFlagsTest[] ss;
        ZZ_Result[] rs;
        FeatureFlagsTest test;

        public JcstressThread_writer2(FeatureFlagsTest[] ss, ZZ_Result[] rs, FeatureFlagsTest test) {
            super("JcstressThread_writer2");
            this.ss = ss;
            this.rs = rs;
            this.test = test;
        }

        public Counter<ZZ_Result> internalRun() {
            return jcstress_iteration_writer2();
        }

        private Counter<ZZ_Result> jcstress_iteration_writer2() {
            int len = config.strideSize * config.strideCount;
            int stride = config.strideSize;
            Counter<ZZ_Result> counter = new Counter<>();
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
            FeatureFlagsTest[] ls = ss;
            ZZ_Result[] lr = rs;
            for (int c = start; c < end; c++) {
                FeatureFlagsTest s = ls[c];
                s.writer2();
            }
        }

        public void purge() {
            ss = null;
            rs = null;
            test = null;
        }
    }

}
