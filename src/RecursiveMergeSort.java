/** Top-down recursive merge sort; critical operation: element-to-element comparison. */
public final class RecursiveMergeSort extends AbstractSort {
    @Override
    public void sort(int[] array) {
        if (array == null) throw new IllegalArgumentException("array must not be null");
        int[] buffer = new int[array.length];
        startSort();
        mergeSort(array, buffer, 0, array.length);
        endSort();
    }

    private void mergeSort(int[] values, int[] buffer, int begin, int end) {
        if (end - begin <= 1) return;
        int middle = begin + (end - begin) / 2;
        mergeSort(values, buffer, begin, middle);
        mergeSort(values, buffer, middle, end);
        int left = begin, right = middle, output = begin;
        while (left < middle && right < end) {
            incrementCount(); // Compare one element from each sorted half.
            if (values[left] <= values[right]) buffer[output++] = values[left++];
            else buffer[output++] = values[right++];
        }
        while (left < middle) buffer[output++] = values[left++];
        while (right < end) buffer[output++] = values[right++];
        System.arraycopy(buffer, begin, values, begin, end - begin);
    }
}
