package mipt.bit;

import java.util.ArrayList;
import java.util.List;

public class CounterBakery implements Counter {

    private int threads = 2;

    private long value;

    private List<Integer> ticket;

    private List<Boolean> entering;



    public CounterBakery(int threads) {
        this.threads = threads;
        this.ticket = new ArrayList<>(threads);
        this.entering = new ArrayList<>(threads);
        for (int i = 0; i < threads; i++) {
            this.ticket.add(0);
            this.entering.add(false);
        }
    }

    public CounterBakery() {
        this.ticket = new ArrayList<>(threads);
        this.entering = new ArrayList<>(threads);
    }

    private void doInsideCritical() {
        this.value++;
//        long tmp = this.value + 1;
//        Thread.yield();// для более верятной ошибки
//        this.value = tmp;
    }

    @Override
    public long getValue() {
        return value;
    }

    @Override
    public void increment() // thread ID
    {
//        Thread.currentThread().getId()
        int pid = Integer.parseInt(Thread.currentThread().getName());
        entering.set(pid, true);
        int max = 0;
        for (int i = 0; i < threads; i++)
        {
            int current = ticket.get(i);
            if (current > max)
            {
                max = current;
            }
        }
        ticket.set(pid, 1 + max);
        entering.set(pid, false);
        for (int i = 0; i < ticket.size(); i++)
        {
            if (i != pid)
            {
                while (entering.get(i)) { Thread.yield(); } // wait while other thread picks a ticket

                while (ticket.get(i) != 0 && ( ticket.get(pid) > ticket.get(i)  ||
                        (ticket.get(pid).equals(ticket.get(i)) && pid > i)))
                { Thread.yield(); }
            }
        }
        // The critical section goes here...
        doInsideCritical();
        unlock(pid);
    }

    public void unlock(int pid)
    {
        ticket.set(pid, 0);
    }

    public void setValue(long value) {
        this.value = value;
    }
}
