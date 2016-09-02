import java.util.NoSuchElementException;

public class ArrayQueue {
    private String[] items;
    private long added;
    private long taken;
    private boolean debug;

    public ArrayQueue(boolean debug) {
        this.debug = debug;
        items = new String[8];
        added = taken = 0;
    }

    public int count() {
        return (int) (added - taken);
    }

    public void enqueue(String s) {
        if(debug) System.out.printf("enqueue: added %d taken %d\n", added, taken);

        if(added - taken < items.length) {
            items[(int) (added % items.length)] = s;
            ++added;
            return;
        }

        String[] a = new String[items.length * 2];
        if(debug) System.out.println("Growing internal array to size: " + a.length);

        for(long i = taken; i < added; ++i)
            a[(int) (i % a.length)] = items[(int) (i % items.length)];

        items = a;
        enqueue(s);
    }

    public String dequeue() {
        if(debug) System.out.printf("dequeue: added %d taken %d\n", added, taken);

        if(added > taken) {
            String ret = items[(int) (taken % items.length)];
            items[(int) (taken % items.length)] = null;
            ++taken;
            return ret;
        }
        throw new NoSuchElementException();
    }

    public static void main(String[] args) {
        ArrayQueue q = new ArrayQueue(true);
        for(int i = 0; i < 6; ++i)
            q.enqueue(Integer.toBinaryString(i));

        System.out.println(q.dequeue());
        System.out.println(q.dequeue());
        for(int i = 0; i < 6; ++i)
            q.enqueue(Integer.toBinaryString(i));
    }
}
