import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        System.out.println("LCS: " + TextAlgorithms.lcs("ABCBDAB", "BDCABA"));
        System.out.println("Edit Distance: " + TextAlgorithms.editDistance("kitten", "sitting"));
        String text = "ABABABA";
        String pattern = "ABA";
        System.out.println("Naive: " + TextAlgorithms.naiveSearch(text, pattern).indices);
        System.out.println("KMP: " + TextAlgorithms.kmpSearch(text, pattern).indices);
        System.out.println("Rabin-Karp: " + TextAlgorithms.rabinKarpSearch(text, pattern).indices);
        int[] suffixArray = {5, 3, 1, 0, 4, 2};
        System.out.println("Kasai LCP: " + Arrays.toString(TextAlgorithms.kasai("banana", suffixArray)));
    }
}
