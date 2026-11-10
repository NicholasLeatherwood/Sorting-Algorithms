/** Bottom-up iterative heap sort; critical operation: element-to-element comparison. */
public final class IterativeHeapSort extends AbstractSort {
    @Override
    public void sort(int[] array) {
        if (array == null) throw new IllegalArgumentException("array must not be null");
        startSort();
        for (int root = array.length / 2 - 1; root >= 0; root--) siftDown(array, root, array.length);
        for (int last = array.length - 1; last > 0; last--) {
            swap(array, 0, last);
            siftDown(array, 0, last);
        }
        endSort();
    }

    private void siftDown(int[] array, int root, int length) {
        while (2L * root + 1 < length) {
            int child = 2 * root + 1;
            int best = root;
            incrementCount(); // Root vs. left child.
            if (array[best] < array[child]) best = child;
            if (child + 1 < length) {
                incrementCount(); // Largest so far vs. right child.
                if (array[best] < array[child + 1]) best = child + 1;
            }
            if (best == root) return;
            swap(array, root, best);
            root = best;
        }
    }

    private static void swap(int[] values, int first, int second) {
        int temp = values[first];
        values[first] = values[second];
        values[second] = temp;
    }
}
