package engg6400.project.fallalertnn;

public class CircularBuffer<T> {
    private T[] buffer;
    private int size;
    private int readPointer;
    private int writePointer;

    @SuppressWarnings("unchecked")
    public CircularBuffer(int capacity) {
        buffer = (T[]) new Object[capacity];
        size = 0;
        readPointer = 0;
        writePointer = 0;
    }

    public void addData(T item) {
        if (size < buffer.length) {
            buffer[writePointer] = item;
            writePointer = (writePointer + 1) % buffer.length;
            size++;
        } else {
            throw new IllegalStateException("Buffer is full");
        }
    }

    public T getData() {
        if (size > 0) {
            T item = buffer[readPointer];
            readPointer = (readPointer + 1) % buffer.length;
            size--;
            return item;
        } else {
            throw new IllegalStateException("Buffer is empty");
        }
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == buffer.length;
    }

    public int size() {
        return size;
    }
}
