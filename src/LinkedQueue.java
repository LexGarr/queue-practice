import java.util.NoSuchElementException;
import java.util.Scanner;


// simple linked implementation
// it's generic, just like java.util.ArrayList

// TODO can we make a LinkedQueue (or ArrayList) of ints? How can we get around this?
public class LinkedQueue<E> {
    // private inner class, also generic on type E (whatever type our queue is supposed to hold)
    private static class Node<E> {
        E data; // Nodes contain an E (the data)
        Node<E> next;
        // TODO these members aren't declared private
        // ...can code outside the queue screw up our data structure now?
        Node(E data, Node<E> next) {
            this.data = data; this.next = next;
        }
        Node(E data) {
            this(data, null);
        }
    }

    private Node<E> first;
    private Node<E> last;
    private int count;

    public LinkedQueue() {
        first = last = null;
        count = 0;
    }

    public int size() {
        return count;
    }

    public void enqueue(E e) {
        ++count;
        Node<E> newLast = new Node<E>(e);

        if(first == null) // TODO what does this case represent?
            first = last = newLast;
        else {
            last.next = newLast;
            last = newLast;
        }
    }

    public E dequeue() {
        if(first == null)
            throw new NoSuchElementException();
        --count;
        E ret = first.data;

        first = first.next;
        if(first == null) // TODO what does this case represent?
            last = null;

        return ret;
    }

    // this implementation uses singly linked nodes to store its data
    // it adds to the back and removes from the front

    // TODO could we have done it backwards?
    //   can we add to the front of a singly linked list?
    //   can we remove from the back of a singly linked list?

    public static void gatherInput(LinkedQueue<Double> q) {
        // this is a try-with-resources block, it ensures the Scanner gets closed
        try (Scanner kb = new Scanner(System.in)) {
            System.out.print("Enter a floating point number: ");

            while(kb.hasNextDouble()) {
                double d = kb.nextDouble();
                q.enqueue(d);
                System.out.printf("Got %f, enter another: ", d);
            }
        }
    }

    public static void main(String[] args) {
        // notice the diamond operator; it's shorthand for "new LinkedQueue<Double>()"
        LinkedQueue<Double> arm_angles = new LinkedQueue<>();
        gatherInput(arm_angles);

        System.out.println("Input gathered, processing in FIFO (First in First Out) order...");

        while(arm_angles.size() > 0) {
            double degree = arm_angles.dequeue();
            System.out.printf("Math.toRadians(%f) => %f\n", degree, Math.toRadians(degree));
        }
    }
}
