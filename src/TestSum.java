import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.III_Result;

import java.util.HashMap;
import java.util.Map;

@JCStressTest
@Outcome(
        id = "50, 100, 250",
        expect = Expect.ACCEPTABLE,
        desc = "Expected transfer"
)
@Outcome(
        id = ".*",
        expect = Expect.ACCEPTABLE_INTERESTING,
        desc = "Unexpected answer"
)
@State
public class TestSum {

    private BankTransfer bankTransfer;
//    private BankTransferNotSafe bankTransfer;
// 1 -> 2 100
// 2 -> 3 100
// 3 -> 1 50
// 1=50, 2=100, 250
    public TestSum() {
        Map<Integer,Integer> map = new HashMap<>();
        map.put(1,100);
        map.put(2,100);
        map.put(3,200);

//        this.bankTransfer = new BankTransferNotSafe(map);
    }

    @Actor
    public void writer1() {
        bankTransfer.transferMoney(1,2,100);
    }

    @Actor
    public void writer2() {
        bankTransfer.transferMoney(2,3,100);
    }

    @Actor
    public void writer3() {
        bankTransfer.transferMoney(3,1,50);
    }

    @Arbiter
    public void arbiter(III_Result r) {
        r.r1 = bankTransfer.getBalanceByAccountId(1);
        r.r2 = bankTransfer.getBalanceByAccountId(2);
        r.r3 = bankTransfer.getBalanceByAccountId(3);
    }

}
