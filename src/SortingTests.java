import java.util.Arrays;
import java.util.Random;

/** Standalone correctness checks; run using java SortingTests. */
public final class SortingTests {
    private SortingTests() { }

    public static void main(String[] args) {
        AbstractSort[] sorts = {new RecursiveMergeSort(), new IterativeHeapSort()};
        int[][] fixtures = {{}, {1}, {4, 3, 2, 1}, {1, 1, 1, 1},
                {-5, 7, 0, -5, 12, 12}, {Integer.MAX_VALUE, Integer.MIN_VALUE, 0}};
        int checks = 0;
        for (AbstractSort sort : sorts) {
            for (int[] fixture : fixtures) {
                check(sort, fixture);
                checks++;
            }
            Random random = new Random(771);
            for (int i = 0; i < 100; i++) {
                int[] input = new int[random.nextInt(300)];
                for (int k = 0; k < input.length; k++) input[k] = random.nextInt(999) - 499;
                check(sort, input);
                checks++;
            }
        }
        System.out.println("PASS: " + checks + " sorting correctness checks");
    }

    private static void check(AbstractSort sort, int[] input) {
        int[] actual = input.clone();
        int[] expected = input.clone();
        Arrays.sort(expected);
        sort.sort(actual);
        if (!Arrays.equals(actual, expected)) {
            throw new AssertionError(sort.getClass().getName() + " incorrect for " + Arrays.toString(input));
        }
    }
}
