import java.util.NoSuchElementException;

public class LinkedQueue<E> {
    private class Node<E> {
        E data;
        Node next;
        Node(E data, Node next) {
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

        if(first == null)
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
        if(first == null)
            last = null;

        return ret;
    }
}
