import java.util.Map;

public class BankTransfer {
    private Map<Integer, Integer> ledger;

    public BankTransfer(Map<Integer, Integer> ledger) {
        if (ledger == null) {
            throw new IllegalStateException("where is ledger,bro?");
        }

    }

    public Integer getBalanceByAccountId(int accountId) {
        return 0;
    }

    public STATUS transferMoney(int fromAccountId, int toAccountId, int amount) {
        return null;
    }

}
