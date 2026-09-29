import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class LoadTestOnRead {
    public static void main(String[] args) throws InterruptedException {
        Map<Integer, Integer> ledger = new HashMap<>();
        for (int i = 0; i < 10_000; i++) {
            ledger.put(i, 10_000_000);
        }
//reentrant 0.708
//stamped 0.43
//        BankTransferReentrant bank = new BankTransferReentrant(ledger);
        BankTransferStamped bank = new BankTransferStamped(ledger);
        long start = System.currentTimeMillis();

        ExecutorService readPool = Executors.newVirtualThreadPerTaskExecutor();
        CountDownLatch readCountDown = new CountDownLatch(10_000);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(10_000);

        for (int i = 0; i < 10_000; i++) {
            int finalI = i;
            readPool.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        cyclicBarrier.await();
                        int balance =  bank.getBalanceByAccountId(finalI);
                        readCountDown.countDown();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        readCountDown.await();
        System.out.println("time spend: " + (System.currentTimeMillis() - start) / 1000f);
    }
}
