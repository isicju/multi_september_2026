import java.util.HashMap;
import java.util.Map;

public class BankTransfer {
    private Map<Integer, Integer> ledger;
    private Map<Integer, Object> locks;
    private final Object LOCK;

    public BankTransfer(Map<Integer, Integer> ledger) {
        if (ledger == null) {
            throw new IllegalStateException("where is ledger,bro?");
        }
        this.ledger = ledger;
        locks = new HashMap<>(ledger.size());
        for (Map.Entry<Integer, Integer> ledgerEntry : ledger.entrySet()) {
            locks.put(ledgerEntry.getKey(), new Object());
        }
        LOCK = new Object();
    }

    public Integer getBalanceByAccountId(int accountId) {
        synchronized (getLock(accountId)) {
            return ledger.get(accountId);
        }
    }

    private Object getLock(Integer accountId) {
        return locks.get(accountId);
    }

    public STATUS transferMoney(int fromAccountId, int toAccountId, int amount) {
        if (fromAccountId == toAccountId) {
            return STATUS.SUCCESS;
        }
        if (amount <= 0) {
            return STATUS.ERROR_INSUFFICIENT_FOUNDS;
        }
        if (getLock(fromAccountId) == null || getLock(toAccountId) == null) {
            return STATUS.ERROR_ACCOUNT_NOT_FOUND;
        }

        int minAccountId = Math.min(fromAccountId, toAccountId);
        int maxAccountId = Math.max(fromAccountId, toAccountId);

        synchronized (getLock(minAccountId)) {
            synchronized (getLock(maxAccountId)) {
                Integer accountBalanceFrom = ledger.get(fromAccountId);
                Integer accountBalanceTo = ledger.get(toAccountId);

                if (ledger.get(fromAccountId) == null || ledger.get(toAccountId) == null) {
                    return STATUS.ERROR_ACCOUNT_NOT_FOUND;
                } else if (accountBalanceFrom < amount) {
                    return STATUS.ERROR_INSUFFICIENT_FOUNDS;
                }

                ledger.put(fromAccountId, accountBalanceFrom - amount);
                ledger.put(toAccountId, accountBalanceTo + amount);

                return STATUS.SUCCESS;
            }
        }
    }

}