import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class Experiments {
    private static final int RUNS = 5;
    private static final Random RANDOM = new Random(2026);
    private static final String ALPHABET = "abcd";
    private static volatile Object sink;
    private static String randomString(int size) {
        StringBuilder builder = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            builder.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return builder.toString();
    }
    private static double measure(Supplier<?> operation) {
        for (int i = 0; i < 3; i++) {
            sink = operation.get();
        }
        long total = 0;
        for (int i = 0; i < RUNS; i++) {
            long start = System.nanoTime();
            Object answer = operation.get();
            total += System.nanoTime() - start;
            sink = answer;
        }
        return total / (RUNS * 1_000_000.0);
    }
    private static int[] buildSuffixArray(String text) {
        int n = text.length();
        Integer[] suffixes = new Integer[n];
        for (int i = 0; i < n; i++) {
            suffixes[i] = i;
        }
        Arrays.sort(suffixes, (a, b) -> {
            int i = a;
            int j = b;
            while (i < n && j < n) {
                if (text.charAt(i) != text.charAt(j)) {
                    return Character.compare(text.charAt(i), text.charAt(j));
                }
                i++;
                j++;
            }
            return Integer.compare(n - a, n - b);
        });
        int[] result = new int[n];
        for (int i = 0; i < n; i++) {
            result[i] = suffixes[i];
        }
        return result;
    }
    private static void add(List<String> lines, String algorithm, String kind, int size,
                            int patternLength, double milliseconds, long comparisons, long units) {
        lines.add(String.format(java.util.Locale.US, "%s,%s,%d,%d,%.6f,%d,%d",
            algorithm, kind, size, patternLength, milliseconds, comparisons, units));
        System.out.printf(java.util.Locale.US, "%s %-7s %6d: %.6f ms%n",
            algorithm, kind, size, milliseconds);
    }
    private static void match(List<String> lines, String name, String kind, String text, String pattern,
                              Supplier<TextAlgorithms.SearchResult> operation) {
        TextAlgorithms.SearchResult result = operation.get();
        double time = measure(operation);
        add(lines, name, kind, text.length(), pattern.length(), time,
            result.comparisons, text.length() - pattern.length() + 1);
    }
    public static void main(String[] args) throws Exception {
        List<String> lines = new ArrayList<>();
        lines.add("algorithm,case,input_size,pattern_length,average_ms,character_comparisons,work_units");
        for (int size : new int[]{100, 500, 1000}) {
            String a = randomString(size);
            String b = randomString(size);
            add(lines, "LCS", "random", size, 0,
                measure(() -> TextAlgorithms.lcs(a, b)), 0, (long) size * size);
            add(lines, "EditDistance", "random", size, 0,
                measure(() -> TextAlgorithms.editDistance(a, b)), 0, (long) size * size);
        }
        for (int size : new int[]{10000, 50000, 100000}) {
            String text = randomString(size);
            String present = text.substring(size / 2, size / 2 + 16);
            String absent = randomString(16);
            while (text.contains(absent)) {
                absent = randomString(16);
            }
            for (String kind : new String[]{"present", "absent"}) {
                String pattern = kind.equals("present") ? present : absent;
                List<Integer> expected = TextAlgorithms.naiveSearch(text, pattern).indices;
                if (!expected.equals(TextAlgorithms.kmpSearch(text, pattern).indices)
                    || !expected.equals(TextAlgorithms.rabinKarpSearch(text, pattern).indices)) {
                    throw new IllegalStateException("String matching algorithms disagree");
                }
                match(lines, "Naive", kind, text, pattern,
                    () -> TextAlgorithms.naiveSearch(text, pattern));
                match(lines, "KMP", kind, text, pattern,
                    () -> TextAlgorithms.kmpSearch(text, pattern));
                match(lines, "RabinKarp", kind, text, pattern,
                    () -> TextAlgorithms.rabinKarpSearch(text, pattern));
            }
        }
        for (int size : new int[]{1000, 5000, 10000}) {
            String text = randomString(size);
            int[] suffixArray = buildSuffixArray(text);
            add(lines, "Kasai", "random", size, 0,
                measure(() -> TextAlgorithms.kasai(text, suffixArray)), 0, size);
        }

        Files.createDirectories(Path.of("results"));
        Files.write(Path.of("results", "results.csv"), lines);
        System.out.println("Saved results/results.csv");
    }
}
