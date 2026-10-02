import java.util.HashMap;
import java.util.Map;

public class BankTransferSharded {

    private static int CORE_COUNT = Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) {
        System.out.println("");
    }

    private Map<Integer, Map<Integer, Integer>> accounts;

    private Map<Integer, Map<Integer,Object>> locks;

    private final Object LOCK;

    private void initLedgersAndLocks(Map<Integer,Integer> ledger) {
        accounts = new HashMap<>();
        locks = new HashMap<>();

        for (int i = 0; i < CORE_COUNT; i++) {
            accounts.put(i, new HashMap<>());
            locks.put(i, new HashMap<>());
        }

        for (Map.Entry<Integer, Integer> entry : ledger.entrySet()) {
            int shardNumber = Math.floorMod(entry.getKey(), CORE_COUNT);
            accounts.get(shardNumber)
                    .put(entry.getKey(), entry.getValue());
            locks.get(shardNumber).put(entry.getKey(), new Object());
        }
    }

    private Map<Integer, Integer> getLedger(Integer key) {
        int shardNumber = Math.floorMod(key, CORE_COUNT);
        return accounts.get(shardNumber);
    }

    private Object getLock(Integer accountId) {
        int shardNumber = Math.floorMod(accountId, CORE_COUNT);
        return locks.get(shardNumber).get(accountId);
    }

    public BankTransferSharded(Map<Integer, Integer> ledger) {
        if (ledger == null) {
            throw new IllegalStateException("where is ledger,bro?");
        }
        initLedgersAndLocks(ledger);
        LOCK = new Object();
    }

    public Integer getBalanceByAccountId(int accountId) {
        synchronized (getLock(accountId)) {
            return getLedger(accountId).get(accountId);
        }
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
                Integer accountBalanceFrom = getLedger(fromAccountId).get(fromAccountId);
                Integer accountBalanceTo = getLedger(toAccountId).get(toAccountId);

                if (getLedger(fromAccountId).get(fromAccountId) == null || getLedger(toAccountId).get(toAccountId) == null) {
                    return STATUS.ERROR_ACCOUNT_NOT_FOUND;
                } else if (accountBalanceFrom < amount) {
                    return STATUS.ERROR_INSUFFICIENT_FOUNDS;
                }

                getLedger(fromAccountId).put(fromAccountId, accountBalanceFrom - amount);
                getLedger(fromAccountId).put(toAccountId, accountBalanceTo + amount);

                return STATUS.SUCCESS;
            }
        }
    }

}
