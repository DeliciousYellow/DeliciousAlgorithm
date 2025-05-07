package org.leetcode;

/**
 * 二分
 */
class DivideC {
    public static int divide(int dividend, int divisor) {
        if (dividend == Integer.MIN_VALUE && divisor == -1) {
            return Integer.MAX_VALUE;
        }

        int resultValue = findResultValue(-Math.abs(dividend), -Math.abs(divisor));
        if ((dividend < 0 && divisor > 0) || (dividend > 0 && divisor < 0)) {
            return -resultValue;
        }
        return resultValue;
    }

    private static int findResultValue(int negDividend, int negDivisor) {
        if (negDividend > negDivisor) {
            return 0;
        }
        int left = 0;
        int right = 30;
        while (true) {
            int mid = ((right - left) / 2) + left;
            if (((1 << mid) * negDivisor >= negDividend && (1 << mid + 1) * negDivisor < negDividend) || left + 1 >= right) {
                int currentDividend = negDividend - (1 << mid) * negDivisor;
                return (1 << mid) + findResultValue(currentDividend, negDivisor);
            } else {
                if ((1 << mid) * negDivisor < negDividend) {
                    right = mid;
                } else {
                    left = mid;
                }
            }
        }
    }

    public static void main(String[] args) {
        System.out.println(divide(11, 3));
        System.out.println(divide(12, 3));
        System.out.println(divide(13, 3));
        System.out.println(divide(0, 3));
        System.out.println(divide(-9, 4));
        System.out.println(divide(100, -10));
        System.out.println(divide(-2147483648, -1));
        System.out.println(divide(2147483647, 1));
        System.out.println(divide(-2147483648, 2));
        System.out.println(divide(-1021989372, -82778243));
    }
}