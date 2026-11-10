CMSC 451 - Project 1: Sorting Benchmark

This Java 21 project benchmarks two distinct sorting algorithms: recursive Merge Sort and iterative Heap Sort. The implementation adheres to the Project 1 requirements, including 12 evenly spaced input sizes, 40 trials per size, two data files, use of AbstractSort, correctness validation, and a Swing-based JFileChooser/JTable report. The accompanying PDF addresses all six updated write-up questions and presents results for five representative sizes.

Assignment clarification: The earlier specification requests two different algorithms, while the updated questions reference recursion and iteration. This implementation provides two distinct algorithms, one recursive and one iterative. As the algorithms themselves differ, observed performance differences cannot be attributed exclusively to recursion or iteration.

Files

* src/AbstractSort.java: timing/counter base class.
* src/RecursiveMergeSort.java: recursive Merge Sort; counts element comparisons.
* src/IterativeHeapSort.java: iterative Heap Sort; counts element comparisons.
* src/UnsortedException.java: raised if sorting verification fails.
* src/BenchmarkSorts.java: RNG, separate JVM warm-up, matched benchmark runs, output files.
* src/BenchmarkReport.java: interactive GUI input selection and 5-column JTable statistics.
* src/SortingTests.java: deterministic sorting correctness tests.
* results/*.txt: actual observed benchmark data and warm-up record.
* results/*_gui.png: actual JVM GUI screenshots.
* results/console_output.txt: benchmark console transcript.
* CMSC451_Project1_Report.pdf: answers 1 through 6 and evidence.

Compile and run

From this directory on a machine with Java 17 or later:

javac -d . src/*.java
java SortingTests
java BenchmarkSorts results
java BenchmarkReport

In the last command, select results/recursive_merge_sort.txt or results/iterative_heap_sort.txt in JFileChooser. Alternatively, pass a file as an argument:

java BenchmarkReport results/recursive_merge_sort.txt

The application is a desktop GUI that requires a display environment. The option --screenshot path.png may be appended after a file path to enable automated screen capture, which also requires a display.

Measurement methodology

* Twelve evenly spaced input sizes: 500 through 6000 (step 500).
* Forty randomly generated inputs at each size, seed 4512026.
* Identical input values, provided as defensive copies, are supplied to both sorting implementations for each trial.
* Warm-up: 1500 calls to each sorter on various sizes, before recorded trials; measured and logged separately.
* Sort timing via System.nanoTime, counters reset on each sort.
* Both sorts count actual element-to-element comparisons only.
* The execution order alternates across trials. After timing, sortedness is verified; an UnsortedException is thrown if verification fails.
* The report calculates the coefficient of variation as (sample standard deviation divided by mean) multiplied by 100, based on 40 trials.
* Outputs contain 12 lines, each with size + 40 (count, time) pairs (81 space-separated fields).

