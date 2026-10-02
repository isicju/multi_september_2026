import java.util.Map;

public class BankTransferNotSafe {
    private final Map<Integer, Integer> ledger;

    public BankTransferNotSafe(Map<Integer, Integer> ledger) {
        if (ledger == null) {
            throw new IllegalStateException("where is ledger,bro?");
        }
        this.ledger = ledger;
    }

    public Integer getBalanceByAccountId(int accountId) {
        return ledger.get(accountId);
    }

    public STATUS transferMoney(int fromAccountId, int toAccountId, int amount) {
        if (fromAccountId == toAccountId) {
            return STATUS.SUCCESS;
        }
        if (amount <= 0) {
            return STATUS.ERROR_INSUFFICIENT_FOUNDS;
        }

        Integer balanceFrom = ledger.get(fromAccountId);
        Integer balanceTo = ledger.get(toAccountId);

        if (balanceFrom == null || balanceTo == null) {
            return STATUS.ERROR_ACCOUNT_NOT_FOUND;
        }
        if (balanceFrom < amount) {
            return STATUS.ERROR_INSUFFICIENT_FOUNDS;
        }

        ledger.put(fromAccountId, balanceFrom - amount);
        ledger.put(toAccountId, balanceTo + amount);

        return STATUS.SUCCESS;
    }
}
