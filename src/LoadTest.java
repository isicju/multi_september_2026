import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoadTest {
//write
//Reentrant = 1.569
//Synch = 15..
//readwrite 1.43
//stamped    1.48

    //read 3_000_000
    //sync => 0.758
//stamped 4.8 => 0.367
//reentrant 7 =>0.8
//readwrite 6.3 =>0.6
    public static void main(String[] args) throws InterruptedException {
        Map<Integer, Integer> ledger = new HashMap<>();
        for (int i = 0; i < 500; i++) {
            ledger.put(i, 10_000_000);
        }

//        BankTransferReentrant bank = new BankTransferReentrant(ledger);
//        BankTransfer bank = new BankTransfer(ledger);
        BankTransferSharded bank = new BankTransferSharded(ledger); //6.492
//        BankTransferStamped bank = new BankTransferStamped(ledger);
//        BankTransferReadWrite bank = new BankTransferReadWrite(ledger);
        int threadRuns = 1000;
        int threadCount = 50;
        ExecutorService service = Executors.newVirtualThreadPerTaskExecutor();
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        long start = System.currentTimeMillis();
        for (int i = 0; i < threadCount; i++) {
            int finalI = i;
            service.submit(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < threadRuns; j++) {
                        STATUS status = bank.transferMoney(finalI, threadCount - 1 - finalI, 30);
                        if (status != STATUS.SUCCESS) {
                            System.out.println("something went wrong!");
                        }
                    }
                    countDownLatch.countDown();
                }
            });
        }

        ExecutorService readPool = Executors.newVirtualThreadPerTaskExecutor();
        CountDownLatch readCountDown = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            int finalI = i;
            readPool.submit(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 30_000_000; j++) {
                        bank.getBalanceByAccountId(finalI);
                    }
                    readCountDown.countDown();
                }
            });
        }


        countDownLatch.await();
        readCountDown.await();


        long total = ledger.values().stream().mapToLong(e -> e).sum();

        System.out.println("sum before: " + 10_000_000L * 500);
        System.out.println("sum after : " + total);

        System.out.println("time spend: " + (System.currentTimeMillis() - start) / 1000f);

        service.shutdown();
    }
}