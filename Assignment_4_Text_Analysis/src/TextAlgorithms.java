import java.util.ArrayList;
import java.util.List;

public class TextAlgorithms {
    public static class SearchResult {
        public final List<Integer> indices;
        public final long comparisons;

        public SearchResult(List<Integer> indices, long comparisons) {
            this.indices = indices;
            this.comparisons = comparisons;
        }
    }
    public static int lcs(String a, String b) {
        int m = a.length();
        int n = b.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp[m][n];
    }
    public static int editDistance(String a, String b) {
        int m = a.length();
        int n = b.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j;
        }
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(
                    Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                    dp[i - 1][j - 1] + cost
                );
            }
        }
        return dp[m][n];
    }
    private static SearchResult emptyPattern(int textLength) {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i <= textLength; i++) {
            indices.add(i);
        }
        return new SearchResult(indices, 0);
    }
    public static SearchResult naiveSearch(String text, String pattern) {
        int n = text.length();
        int m = pattern.length();
        if (m == 0) {
            return emptyPattern(n);
        }
        List<Integer> indices = new ArrayList<>();
        long comparisons = 0;
        for (int i = 0; i <= n - m; i++) {
            int j = 0;
            while (j < m) {
                comparisons++;
                if (text.charAt(i + j) != pattern.charAt(j)) {
                    break;
                }
                j++;
            }
            if (j == m) {
                indices.add(i);
            }
        }
        return new SearchResult(indices, comparisons);
    }
    public static SearchResult kmpSearch(String text, String pattern) {
        int n = text.length();
        int m = pattern.length();
        if (m == 0) {
            return emptyPattern(n);
        }
        List<Integer> indices = new ArrayList<>();
        long comparisons = 0;
        int[] lps = new int[m];
        int length = 0;
        int i = 1;
        while (i < m) {
            comparisons++;
            if (pattern.charAt(i) == pattern.charAt(length)) {
                length++;
                lps[i] = length;
                i++;
            } else if (length > 0) {
                length = lps[length - 1];
            } else {
                i++;
            }
        }
        i = 0;
        int j = 0;
        while (i < n) {
            comparisons++;
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
                if (j == m) {
                    indices.add(i - m);
                    j = lps[j - 1];
                }
            } else if (j > 0) {
                j = lps[j - 1];
            } else {
                i++;
            }
        }
        return new SearchResult(indices, comparisons);
    }
    public static SearchResult rabinKarpSearch(String text, String pattern) {
        int n = text.length();
        int m = pattern.length();
        if (m == 0) {
            return emptyPattern(n);
        }
        List<Integer> indices = new ArrayList<>();
        if (m > n) {
            return new SearchResult(indices, 0);
        }
        long base = 256;
        long mod = 1_000_000_007;
        long power = 1;
        long patternHash = 0;
        long windowHash = 0;
        long comparisons = 0;
        for (int i = 0; i < m; i++) {
            patternHash = (patternHash * base + pattern.charAt(i)) % mod;
            windowHash = (windowHash * base + text.charAt(i)) % mod;
            if (i < m - 1) {
                power = (power * base) % mod;
            }
        }
        for (int i = 0; i <= n - m; i++) {
            if (patternHash == windowHash) {
                int j = 0;
                while (j < m) {
                    comparisons++;
                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        break;
                    }
                    j++;
                }
                if (j == m) {
                    indices.add(i);
                }
            }
            if (i < n - m) {
                windowHash = (windowHash - text.charAt(i) * power % mod + mod) % mod;
                windowHash = (windowHash * base + text.charAt(i + m)) % mod;
            }
        }
        return new SearchResult(indices, comparisons);
    }
    public static int[] kasai(String text, int[] suffixArray) {
        int n = text.length();
        if (suffixArray.length != n) {
            throw new IllegalArgumentException("Suffix array length must match text length");
        }

        int[] rank = new int[n];
        int[] lcp = new int[n];
        for (int i = 0; i < n; i++) {
            rank[suffixArray[i]] = i;
        }
        int matched = 0;
        for (int i = 0; i < n; i++) {
            int position = rank[i];
            if (position == 0) {
                matched = 0;
                continue;
            }
            int j = suffixArray[position - 1];
            while (i + matched < n && j + matched < n
                && text.charAt(i + matched) == text.charAt(j + matched)) {
                matched++;
            }
            lcp[position] = matched;
            if (matched > 0) {
                matched--;
            }
        }
        return lcp;
    }
}
