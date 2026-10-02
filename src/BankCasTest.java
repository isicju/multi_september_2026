import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BankCasTest {
    public static void main(String[] args) throws InterruptedException {


        BankTransferCas bankTransferCas = new BankTransferCas(Map.of(1,600,2,200,3,300));

        ExecutorService service = Executors.newVirtualThreadPerTaskExecutor();
        CountDownLatch countDownLatch = new CountDownLatch(100);
        for (int i = 0; i < 100; i++) {

            service.submit(new Runnable() {
                @Override
                public void run() {
                    bankTransferCas.transferMoney(1,3,3);
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();
        System.out.println(bankTransferCas.getBalanceByAccountId(3));

    }
}
