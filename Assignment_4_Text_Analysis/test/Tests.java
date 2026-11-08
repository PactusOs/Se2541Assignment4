import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Tests {
    private static int passed = 0;

    private static void check(String name, Object actual, Object expected) {
        if (!actual.equals(expected)) {
            throw new AssertionError(name + ": expected " + expected + ", got " + actual);
        }
        passed++;
    }
    private static void checkArray(String name, int[] actual, int[] expected) {
        if (!Arrays.equals(actual, expected)) {
            throw new AssertionError(name + ": expected " + Arrays.toString(expected)
                + ", got " + Arrays.toString(actual));
        }
        passed++;
    }
    private static void match(String name, String text, String pattern, List<Integer> expected) {
        check(name + " naive", TextAlgorithms.naiveSearch(text, pattern).indices, expected);
        check(name + " KMP", TextAlgorithms.kmpSearch(text, pattern).indices, expected);
        check(name + " Rabin-Karp", TextAlgorithms.rabinKarpSearch(text, pattern).indices, expected);
    }
    private static List<Integer> expectedMatches(String text, String pattern) {
        List<Integer> result = new ArrayList<>();
        for (int start = 0; start <= text.length(); start++) {
            int position = text.indexOf(pattern, start);
            if (position == -1) {
                break;
            }
            result.add(position);
            if (position == text.length()) {
                break;
            }
            start = position;
        }
        return result;
    }
    public static void main(String[] args) {
        check("LCS identical", TextAlgorithms.lcs("ABCDE", "ABCDE"), 5);
        check("LCS different", TextAlgorithms.lcs("AAAA", "BBBB"), 0);
        check("LCS empty", TextAlgorithms.lcs("", "ABC"), 0);
        check("LCS both empty", TextAlgorithms.lcs("", ""), 0);
        check("LCS subsequence", TextAlgorithms.lcs("ABCBDAB", "BDCABA"), 4);

        check("Edit identical", TextAlgorithms.editDistance("apple", "apple"), 0);
        check("Edit one empty", TextAlgorithms.editDistance("apple", ""), 5);
        check("Edit reverse empty", TextAlgorithms.editDistance("", "apple"), 5);
        check("Edit both empty", TextAlgorithms.editDistance("", ""), 0);
        check("Edit substitution", TextAlgorithms.editDistance("cat", "cut"), 1);
        check("Edit insertion", TextAlgorithms.editDistance("cat", "cart"), 1);
        check("Edit deletion", TextAlgorithms.editDistance("cart", "cat"), 1);
        check("Edit combination", TextAlgorithms.editDistance("kitten", "sitting"), 3);

        match("Beginning", "ABCxyz", "ABC", List.of(0));
        match("Middle", "xxABCyy", "ABC", List.of(2));
        match("End", "xxxxABC", "ABC", List.of(4));
        match("Multiple and overlapping", "aaaaa", "aa", List.of(0, 1, 2, 3));
        match("Absent", "abcabc", "xyz", List.of());
        match("Longer pattern", "abc", "abcd", List.of());
        match("Empty pattern", "abc", "", List.of(0, 1, 2, 3));
        match("Both empty", "", "", List.of(0));
        match("Empty text", "", "a", List.of());
        match("Repeated pattern", "abababa", "aba", List.of(0, 2, 4));

        checkArray("Kasai banana", TextAlgorithms.kasai("banana", new int[]{5, 3, 1, 0, 4, 2}),
            new int[]{0, 1, 3, 0, 0, 2});
        checkArray("Kasai all equal", TextAlgorithms.kasai("aaaa", new int[]{3, 2, 1, 0}),
            new int[]{0, 1, 2, 3});
        checkArray("Kasai unique", TextAlgorithms.kasai("abc", new int[]{0, 1, 2}),
            new int[]{0, 0, 0});
        checkArray("Kasai empty", TextAlgorithms.kasai("", new int[]{}), new int[]{});
        checkArray("Kasai one character", TextAlgorithms.kasai("a", new int[]{0}), new int[]{0});

        Random random = new Random(2026);
        for (int i = 0; i < 200; i++) {
            StringBuilder text = new StringBuilder();
            StringBuilder pattern = new StringBuilder();
            for (int j = 0; j < random.nextInt(30); j++) {
                text.append("abc".charAt(random.nextInt(3)));
            }
            for (int j = 0; j < random.nextInt(12); j++) {
                pattern.append("abc".charAt(random.nextInt(3)));
            }
            match("Random " + i, text.toString(), pattern.toString(),
                expectedMatches(text.toString(), pattern.toString()));
        }
        System.out.println("ALL TESTS PASSED: " + passed);
    }
}
