import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.StampedLock;

public class BankTransferStamped {
    private Map<Integer, Integer> ledger;
    private Map<Integer, StampedLock> locks;

    public BankTransferStamped(Map<Integer, Integer> ledger) {
        if (ledger == null) {
            throw new IllegalStateException("where is ledger,bro?");
        }
        this.ledger = ledger;
        locks = new HashMap<>(ledger.size());
        for (Map.Entry<Integer, Integer> ledgerEntry : ledger.entrySet()) {
            locks.put(ledgerEntry.getKey(), new StampedLock());
        }
    }

    public Integer getBalanceByAccountId(int accountId) {
        StampedLock lock = locks.get(accountId);
        if (lock == null) {
            return null;
        }

        long stamp = lock.tryOptimisticRead();
        Integer balance = ledger.get(accountId);
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                balance = ledger.get(accountId);
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return balance;
    }

    private StampedLock getLock(Integer accountId) {
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

        StampedLock minAccountLock = getLock(minAccountId);
        long minLock = minAccountLock.writeLock();
        try {
            StampedLock maxAccountLock = getLock(maxAccountId);
            long maxLock = maxAccountLock.writeLock();
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
                maxAccountLock.unlockWrite(maxLock);
            }
        } finally {
            minAccountLock.unlockWrite(minLock);
        }
    }

}
