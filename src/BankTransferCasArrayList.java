import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class BankTransferCasArrayList {



    private static class TransferEvent {
        Integer from;
        Integer to;
        int amount;
    }

    private volatile List<TransferEvent> events;

    private AtomicReference<Integer> lastCell = new AtomicReference<>(0);

    private Map<Integer, Integer> ledger;

    public BankTransferCasArrayList(Map<Integer, Integer> ledger, int transactionNumber) {
        events = new ArrayList<>(transactionNumber);
        if (ledger == null) {
            throw new IllegalStateException("where is ledger,bro?");
        }
        this.ledger = ledger;
        for (int i = 0; i < transactionNumber; i++) {
            events.add(null);
        }
    }

    public long getTotalBalance() {
        long total = 0;

        for (Integer balance : ledger.values()) {
            total += balance;
        }

        for (TransferEvent event : events) {
            if (event == null) {
                continue;
            }

            total -= event.amount;
            total += event.amount;
        }

        return total;
    }

    public Integer getBalanceByAccountId(int accountId) {
        int balance = ledger.get(accountId);
        for (TransferEvent event : events) {
            if(event == null) continue;
            if (event.from == accountId) {
                balance -= event.amount;
            } else if (event.to == accountId) {
                balance += event.amount;
            }
        }
        return balance;
    }

    public STATUS transferMoney(int fromAccountId, int toAccountId, int amount) {
        if (fromAccountId == toAccountId) {
            return STATUS.SUCCESS;
        }
        if (amount <= 0) {
            return STATUS.ERROR_INSUFFICIENT_FOUNDS;
        }
        if (ledger.get(fromAccountId) == null || ledger.get(toAccountId) == null) {
            return STATUS.ERROR_ACCOUNT_NOT_FOUND;
        }

        Integer currentIndex;
        Integer newIndex;

        TransferEvent newEvent = new TransferEvent();
        newEvent.from = fromAccountId;
        newEvent.to = toAccountId;
        newEvent.amount = amount;

        do {
            currentIndex = lastCell.get();
            newIndex = currentIndex + 1;
        } while (!lastCell.compareAndSet(currentIndex, newIndex));

        events.set(newIndex, newEvent);

        return STATUS.SUCCESS;
    }

}