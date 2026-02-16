package patterndb;

/**
 * Computes the lexicographic rank (index) of a permutation in O(N) time
 * using the Lehmer code (factorial number system).
 *
 * Template parameter N from C++ is passed as a constructor parameter.
 */
public class PermutationIndexer {

    private final int N;
    private final int K;
    private final int[] onesCountLookup;
    private final int[] factorials;

    public PermutationIndexer(int n) {
        this(n, n);
    }

    public PermutationIndexer(int n, int k) {
        this.N = n;
        this.K = k;

        // Precompute ones-count for all N-bit numbers
        int lookupSize = (1 << N) - 1;
        onesCountLookup = new int[lookupSize];
        for (int i = 0; i < lookupSize; i++) {
            onesCountLookup[i] = Integer.bitCount(i);
        }

        // Precompute factorials (picks) in reverse order
        factorials = new int[K];
        for (int i = 0; i < K; i++) {
            factorials[i] = MathUtils.pick(N - 1 - i, K - 1 - i);
        }
    }

    /**
     * Calculate the lexicographic rank of the given permutation.
     * 
     * @param perm array of length K with values in [0, N-1]
     * @return the rank (0-indexed)
     */
    public int rank(int[] perm) {
        // Lehmer code
        int[] lehmer = new int[K];
        // "seen" digits stored as a bitmask (right-to-left indexing)
        int seen = 0;

        lehmer[0] = perm[0];
        seen |= (1 << (N - 1 - perm[0]));

        for (int i = 1; i < K; i++) {
            seen |= (1 << (N - 1 - perm[i]));

            // Count ones left of this digit's position
            int shifted = seen >>> (N - perm[i]);
            int numOnes = (shifted > 0 && shifted < onesCountLookup.length)
                    ? onesCountLookup[shifted]
                    : Integer.bitCount(shifted);
            lehmer[i] = perm[i] - numOnes;
        }

        // Convert Lehmer code to base-10 index
        int index = 0;
        for (int i = 0; i < K; i++) {
            index += lehmer[i] * factorials[i];
        }
        return index;
    }
}
