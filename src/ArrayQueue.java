import java.util.NoSuchElementException;

public class ArrayQueue {
    private String[] items;
    private long added; // count of how many Strings have ever been added to this queue
    private long taken; // ditto
    private boolean debug;

    // this algorithm is interesting, let's make it easy to turn on debug printing!
    public ArrayQueue(boolean debug) {
        this.debug = debug;
        items = new String[8];
        added = taken = 0;
    }

    public int count() {
        return (int) (added - taken); // makes sense, right?
    }

    public void enqueue(String s) {
        if(debug) System.out.printf("about to enqueue %4s: have added %d, taken %d, array length %d%n",
                                    s, added, taken, items.length);

        if(count() < items.length) { // TODO what case does this represent?
            items[(int) (added % items.length)] = s;
            ++added;
            return; // done!
        }

        // TODO what case does this represent?

        String[] a = new String[items.length * 2];
        if(debug) System.out.println("Growing internal array to size: " + a.length + "!!");

        for(long i = taken; i < added; ++i)
            a[(int) (i % a.length)] = items[(int) (i % items.length)];
        // this goofy line translates item positions in the old array
        // to their new homes in the new array

        items = a; // TODO what happens to the old array after we replace it with the new one?
        enqueue(s); // TODO what the...? why are we recursing here?
    }

    public String dequeue() {
        if(count() == 0) throw new NoSuchElementException();

        int front = (int) (taken % items.length);
        String ret = items[front];
        items[front] = null; // TODO why bother?

        if(debug) System.out.printf("about to dequeue %4s: have added %d, taken %d, array length %d%n",
                                    ret, added, taken, items.length);
        ++taken;
        return ret;
    }

    // what is the size of the array if we add 10,000 strings, then remove 9,999 of them?
    // TODO how should we fix this?
    //   when should we shrink? when less than half full? how could that go wrong?

    public static void main(String[] args) {
        ArrayQueue q = new ArrayQueue(true);
        for(int i = 0; i < 6; ++i)
            q.enqueue(Integer.toBinaryString(i));

        System.out.println();
        System.out.println("q.dequeue() => " + q.dequeue());
        System.out.println("q.dequeue() => " + q.dequeue());
        System.out.println();

        for(int i = 6; i < 12; ++i)
            q.enqueue(Integer.toBinaryString(i));
    }
}
