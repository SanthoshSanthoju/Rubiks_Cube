package patterndb;

/**
 * Utility math functions: factorial, pick (nPk), choose (nCk).
 */
public class MathUtils {

    public static int factorial(int n) {
        return n <= 1 ? 1 : n * factorial(n - 1);
    }

    /** nPk = n! / (n-k)! */
    public static int pick(int n, int k) {
        return factorial(n) / factorial(n - k);
    }

    /** nCk = n! / ((n-k)! * k!) */
    public static int choose(int n, int k) {
        return (n < k) ? 0 : factorial(n) / (factorial(n - k) * factorial(k));
    }
}
