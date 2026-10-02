import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class BankTransferCasAtomicReference {

    private static class TransferEvent {
        Integer from;
        Integer to;
        int amount;
    }

    private volatile AtomicReference<MyNode> lastNode;

    private class MyNode {
        public MyNode(TransferEvent event, MyNode left) {
            this.event = event;
            this.left = left;
        }

        TransferEvent event;
        MyNode left;
    }


    private Map<Integer, Integer> ledger;

//    private List<TransferEvent> eventList = new ArrayList<>(100_000_000);

    public BankTransferCasAtomicReference(Map<Integer, Integer> ledger) {
        if (ledger == null) {
            throw new IllegalStateException("where is ledger,bro?");
        }
        this.ledger = ledger;
    }


    public Integer getBalanceByAccountId(int accountId) {
        int balance = ledger.get(accountId);
        MyNode node = lastNode.get();
        while (node != null) {
           TransferEvent event = node.event;
           if(event.from == accountId) {
               balance -= event.amount;
           } else if (event.to == accountId) {
                balance += event.amount;
           }
            node = node.left;
        }
        return balance;
    }

    public long  getTotalBalance() {
        long total = 0;

        for (Integer balance : ledger.values()) {
            total += balance;
        }

        MyNode node = lastNode.get();
        int counter = 0;
        while (node != null) {
            counter++;
            TransferEvent event = node.event;

            total -= event.amount;
            total += event.amount;

            node = node.left;
        }


        System.out.println(counter);
        return  total;
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

        MyNode currentNode;
        MyNode newNode;


        TransferEvent newEvent = new TransferEvent();
        newEvent.from = fromAccountId;
        newEvent.to = toAccountId;
        newEvent.amount = amount;

        do {
            currentNode = this.lastNode.get();
            newNode = new MyNode(newEvent, currentNode);
        } while (!lastNode.compareAndSet( currentNode, newNode));

        return STATUS.SUCCESS;
    }

}