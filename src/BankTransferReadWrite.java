import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BankTransferReadWrite {
    private Map<Integer, Integer> ledger;
    private Map<Integer, ReadWriteLock> locks;

    public BankTransferReadWrite(Map<Integer, Integer> ledger) {
        if (ledger == null) {
            throw new IllegalStateException("where is ledger,bro?");
        }
        this.ledger = ledger;
        locks = new HashMap<>(ledger.size());
        for (Map.Entry<Integer, Integer> ledgerEntry : ledger.entrySet()) {
            locks.put(ledgerEntry.getKey(), new ReentrantReadWriteLock());
        }
    }

    public Integer getBalanceByAccountId(int accountId) {
        ReadWriteLock lock = locks.get(accountId);
        lock.readLock().lock();
        try {
            return ledger.get(accountId);
        } finally {
            lock.readLock().unlock();
        }
    }

    private ReadWriteLock getLock(Integer accountId) {
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

        ReadWriteLock minAccountLock = getLock(minAccountId);
        minAccountLock.writeLock().lock();
        try {
            ReadWriteLock maxAccountLock = getLock(maxAccountId);
            maxAccountLock.writeLock().lock();
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
                maxAccountLock.writeLock().unlock();
            }
        } finally {
            minAccountLock.writeLock().unlock();
        }
    }

}
