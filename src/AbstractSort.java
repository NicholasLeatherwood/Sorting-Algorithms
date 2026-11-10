/** Shared measured counters and time for the sorting algorithms. */
public abstract class AbstractSort {
    private long criticalCount;
    private long startedAt;
    private long elapsedNanoseconds;

    public abstract void sort(int[] array);

    protected final void startSort() {
        criticalCount = 0;
        elapsedNanoseconds = 0;
        startedAt = System.nanoTime();
    }

    protected final void endSort() {
        elapsedNanoseconds = System.nanoTime() - startedAt;
    }

    protected final void incrementCount() {
        criticalCount++;
    }

    public final long getCount() {
        return criticalCount;
    }

    public final long getTime() {
        return elapsedNanoseconds;
    }
}
