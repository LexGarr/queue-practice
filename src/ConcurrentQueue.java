import java.util.NoSuchElementException;
import java.util.Scanner;

// don't do this for anything but learning and fun
// there are quality (debugged and performant!!) concurrent data structures
// in the standard library, java.util.concurrent
public class ConcurrentQueue {
    // simple interface, enforces read only access
    private interface Qnode {
        String first();
        Qnode rest();
    }
    private Qnode items;
    private Qnode end;

    // items is the front of the queue,
    // it's a read-only way to access all the data in the queue.
    // but we must be able to modify nodes, or how would we be
    // able to add items at the end of the queue? we'll see that trick later
    public ConcurrentQueue() {
        items = null;
        end = null;
    }

    // all objects in java have an "intrinsic" lock.
    // so all objects are lockable
    // you obtain the lock with a syncronized block, or
    // a syncronized method

    // while a thread is executing syncronized code,
    // it is guaranteed to be the only thread executing
    // syncronized code on this particular object.
    // you're the "only kid in the candy store"
    public synchronized String takeOrThrow() {
        if(items == null) // if empty, just throw
            throw new NoSuchElementException();
        String ret = items.first();
        items = items.rest();
        return ret;
    }

    public synchronized String take() {
        while(items == null) { // while empty
            try {
                wait();
                // this thread releases the lock and sleeps...
                // awaiting a notification from another thread
                // that the world has changed, and so it should
                // wake up, reacquire the lock, and see if what it's
                // waiting for has come true! (non-empty queue)
            } catch (InterruptedException e) {}
        }
        String ret = items.first();
        items = items.rest();
        return ret;
    }

    // the put method must have some secrets up it's sleeve
    // so that it can add to the end of the queue (modifying nodes)
    // even though the only thing the Qnode interface allows are reads
    public synchronized void put(String s) {

        // under the hood, these "read-only" Qnodes are really
        // instances of ModifiableNode, a local class to this method.
        // since its local, no code outside the put method can see or use it.

        // So to the rest of the world, these nodes really are immutable!! (read-only)
        // but the code inside the put method has a secret trapdoor that
        // allows it to modify these nodes
        class ModifiableNode implements Qnode {
            String first;
            Qnode rest = null;

            public ModifiableNode(String first) {
                this.first = first;
            }

            public String first() { return first; }
            public Qnode rest() { return rest; }
        }

        // TODO why can we store an instance of ModifiableNode in a variable of type Qnode?
        Qnode newEnd = new ModifiableNode(s);
        if(items == null) {
            items = newEnd;
            end = newEnd;
        } else {
            ((ModifiableNode) end).rest = newEnd;
            end = newEnd;
        }

        // if there are any waiting threads, tell them to wake up. The queue is non-empty!
        notifyAll();
    }

    public static void main(String[] args) throws InterruptedException {
        final int c = 10;
        ConcurrentQueue q = new ConcurrentQueue();

        // threads are represented by objects too!
        Thread[] consumers = new Thread[c];
        // threads need code to run, so we make Runnable objects (they have a method called run)

        // we can make Runnable anonymous inner classes (no name, just interface methods)
        Runnable explicit_task = new Runnable() {
            public void run() {
                System.out.println(Thread.currentThread().getName() + " received: " + q.take());
            }
        };

        // this is a bit verbose. in java 8 we can use lambda syntax, it's equivalent
        Runnable task = () -> {
            System.out.println(Thread.currentThread().getName() + " received: " + q.take());
        };


        for(int i = 0; i < c; ++i)
            // make threads, give them the code they should run, and a name
            consumers[i] = new Thread(task, String.format("Thread %d", i));

        // make the producer thread with code to run
        // TODO can you refactor this Runnable as a lambda expression?
        Thread producer = new Thread(new Runnable() {
            public void run() {
                for(int i = 0; i < c; ++i) {
                    try {
                        Thread.sleep((int) (Math.random() * 2000));
                    } catch (InterruptedException e) {}
                    q.put(Integer.toBinaryString(i));
                }
            }
        });

        for(int i = c - 1; i >= 0; --i)
            // consumers will try to take from the queue, but it's still empty!
            // they'll have to wait
            consumers[i].start();

        Scanner kb = new Scanner(System.in);
        System.out.println("Waiting to start producer.... type something and hit enter!");
        kb.next();
        // the race is on!
        producer.start();

        for(Thread t : consumers)
            // join means wait till the other threads are done before moving on
            t.join();

        System.out.println("All consumer threads are done!");
    }
}
