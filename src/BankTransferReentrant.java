import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BankTransferReentrant {
    private Map<Integer, Integer> ledger;
    private Map<Integer, Lock> locks;

    public BankTransferReentrant(Map<Integer, Integer> ledger) {
        if (ledger == null) {
            throw new IllegalStateException("where is ledger,bro?");
        }
        this.ledger = ledger;
        locks = new HashMap<>(ledger.size());
        for (Map.Entry<Integer, Integer> ledgerEntry : ledger.entrySet()) {
            locks.put(ledgerEntry.getKey(), new ReentrantLock());
        }
    }

    public Integer getBalanceByAccountId(int accountId) {
        Lock lock = locks.get(accountId);
        lock.lock();
        try {
            return ledger.get(accountId);
        } finally {
            lock.unlock();
        }
    }

    private Lock getLock(Integer accountId) {
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

        Lock minAccountLock = getLock(minAccountId);
        minAccountLock.lock();
        try {
            Lock maxAccountLock = getLock(maxAccountId);
            maxAccountLock.lock();
            try {
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
            } finally {
                maxAccountLock.unlock();
            }
        } finally {
            minAccountLock.unlock();
        }
    }

}
