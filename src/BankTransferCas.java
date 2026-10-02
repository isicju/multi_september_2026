import org.w3c.dom.Node;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BankTransferCas {

    private static class TransferEvent {
        Integer from;
        Integer to;
        int amount;
    }

    private volatile MyNode lastNode;

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

    private static final VarHandle VAR_HANDLE;

    static {
        try {
            VAR_HANDLE = MethodHandles.lookup().findVarHandle(BankTransferCas.class, "lastNode", MyNode.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public BankTransferCas(Map<Integer, Integer> ledger) {
        if (ledger == null) {
            throw new IllegalStateException("where is ledger,bro?");
        }
        this.ledger = ledger;
    }


    public Integer getBalanceByAccountId(int accountId) {
        int balance = ledger.get(accountId);
        MyNode node = lastNode;
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

        MyNode node = lastNode;
        while (node != null) {
            TransferEvent event = node.event;

            total -= event.amount;
            total += event.amount;

            node = node.left;
        }
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
            currentNode = (MyNode) VAR_HANDLE.get(this);
            newNode = new MyNode(newEvent, currentNode);
        } while (!VAR_HANDLE.compareAndSet(this, currentNode, newNode));

        return STATUS.SUCCESS;
    }

}