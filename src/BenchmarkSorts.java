import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

/** Generates 12 sizes x 40 identical random input cases for both sorting algorithms. */
public final class BenchmarkSorts {
    private static final int SIZE_STEP = 500;
    private static final int SIZE_COUNT = 12;
    private static final int RUNS_PER_SIZE = 40;
    private static final int WARMUP_ROUNDS = 1500;
    private static final long SEED = 4512026L;

    private BenchmarkSorts() { }

    public static void main(String[] args) throws IOException {
        Path outputDir = Paths.get(args.length == 0 ? "results" : args[0]);
        Files.createDirectories(outputDir);
        AbstractSort merge = new RecursiveMergeSort();
        AbstractSort heap = new IterativeHeapSort();

        // Warm up the exact sort implementations before collecting any results.
        // Warmup duration is separately measured and excluded from the data files.
        Random warmupRandom = new Random(SEED ^ 0x5A5A5A5AL);
        long warmupStart = System.nanoTime();
        for (int i = 0; i < WARMUP_ROUNDS; i++) {
            int[] input = randomArray(warmupRandom, 200 + (i % 12) * SIZE_STEP);
            if ((i & 1) == 0) {
                merge.sort(input.clone());
                heap.sort(input.clone());
            } else {
                heap.sort(input.clone());
                merge.sort(input.clone());
            }
        }
        long warmupNs = System.nanoTime() - warmupStart;
        System.out.printf("JVM warm-up: %d calls per algorithm, %.3f ms (excluded from results)%n",
                WARMUP_ROUNDS, warmupNs / 1_000_000.0);

        Random dataRandom = new Random(SEED);
        Path mergeFile = outputDir.resolve("recursive_merge_sort.txt");
        Path heapFile = outputDir.resolve("iterative_heap_sort.txt");
        try (BufferedWriter mergeOut = Files.newBufferedWriter(mergeFile);
             BufferedWriter heapOut = Files.newBufferedWriter(heapFile)) {
            for (int sizeNumber = 1; sizeNumber <= SIZE_COUNT; sizeNumber++) {
                int n = sizeNumber * SIZE_STEP;
                mergeOut.write(Integer.toString(n));
                heapOut.write(Integer.toString(n));
                for (int run = 0; run < RUNS_PER_SIZE; run++) {
                    int[] original = randomArray(dataRandom, n);
                    int[] first = original.clone();
                    int[] second = original.clone();
                    // Alternate algorithm order to reduce systematic measurement bias.
                    if ((run & 1) == 0) {
                        merge.sort(first);
                        heap.sort(second);
                    } else {
                        heap.sort(second);
                        merge.sort(first);
                    }
                    assertSorted(first, "Recursive merge sort", n, run);
                    assertSorted(second, "Iterative heap sort", n, run);
                    mergeOut.write(" " + merge.getCount() + " " + merge.getTime());
                    heapOut.write(" " + heap.getCount() + " " + heap.getTime());
                }
                mergeOut.newLine();
                heapOut.newLine();
                System.out.printf("Completed n=%d (%d matched runs per algorithm)%n", n, RUNS_PER_SIZE);
            }
        }
        Files.writeString(outputDir.resolve("warmup.txt"),
                "Calls per algorithm: " + WARMUP_ROUNDS + System.lineSeparator() +
                "Warm-up total nanoseconds: " + warmupNs + System.lineSeparator() +
                "Warm-up total milliseconds: " + (warmupNs / 1_000_000.0) + System.lineSeparator() +
                "Warm-up excluded from benchmark data: yes" + System.lineSeparator() +
                "Random seed: " + SEED + System.lineSeparator());
        System.out.println("Wrote " + mergeFile + " and " + heapFile);
    }

    private static int[] randomArray(Random random, int length) {
        int[] result = new int[length];
        for (int i = 0; i < length; i++) result[i] = random.nextInt(2_000_001) - 1_000_000;
        return result;
    }

    private static void assertSorted(int[] values, String label, int size, int run) {
        for (int i = 1; i < values.length; i++) {
            if (values[i - 1] > values[i]) {
                throw new UnsortedException(label + " failed at n=" + size + " run=" + run + " index=" + i);
            }
        }
    }
}
