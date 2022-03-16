package data;

public class Rational {
    private int mNumerator, mDenominator;
    public Rational(int numerator, int denominator){
        if (denominator < 0) {
            numerator = -numerator;
            denominator = -denominator;
        }

        // Convert to reduced form
        if (denominator == 0 && numerator > 0) {
            mNumerator = 1; // +Inf
            mDenominator = 0;
        } else if (denominator == 0 && numerator < 0) {
            mNumerator = -1; // -Inf
            mDenominator = 0;
        } else if (denominator == 0) {
            mNumerator = 0; // NaN
            mDenominator = 0;
        } else if (numerator == 0) {
            mNumerator = 0;
            mDenominator = 1;
        } else {
            int gcd = gcd(numerator, denominator);

            mNumerator = numerator / gcd;
            mDenominator = denominator / gcd;
        }
    }

    /**
     * Calculates the greatest common divisor using Euclid's algorithm.
     *
     * <p><em>Visible for testing only.</em></p>
     *
     * @param numerator the numerator in a fraction
     * @param denominator the denominator in a fraction
     *
     * @return An int value representing the gcd. Always positive.
     * @hide
     */
    public static int gcd(int numerator, int denominator) {
        /*
         * Non-recursive implementation of Euclid's algorithm:
         *
         *  gcd(a, 0) := a
         *  gcd(a, b) := gcd(b, a mod b)
         *
         */
        int a = numerator;
        int b = denominator;

        while (b != 0) {
            int oldB = b;

            b = a % b;
            a = oldB;
        }

        return Math.abs(a);
    }

    public float floatValue() {
        float num = mNumerator;
        float den = mDenominator;

        return num / den;
    }
}
