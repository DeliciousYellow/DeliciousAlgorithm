package org.leetcode;

/**
 * 循环
 */
class DivideA {
    public static int divide(int dividend, int divisor) {
        if (dividend == Integer.MIN_VALUE && divisor == -1) {
            return Integer.MAX_VALUE;
        }

        int resultValue = findResultValue(Math.abs(dividend), Math.abs(divisor));
        if ((dividend < 0 && divisor > 0) || (dividend > 0 && divisor < 0)) {
            return -resultValue;
        }
        return resultValue;
    }

    private static int findResultValue(int dividend, int divisor) {
        if (Math.abs(dividend) < Math.abs(divisor)) {
            return 0;
        }
        int i = 0;
        while (!((1 << i) * divisor <= dividend && (1 << i + 1) * divisor > dividend)) {
            i++;
        }
        int currentDividend = dividend - (1 << i) * divisor;
        return (1 << i) + findResultValue(currentDividend, divisor);
    }

    public static void main(String[] args) {
        System.out.println(divide(-2147483648, -1));
        System.out.println(divide(11, 3));
        System.out.println(divide(12, 3));
        System.out.println(divide(13, 3));
        System.out.println(divide(0, 3));
        System.out.println(divide(-9, 4));
        System.out.println(divide(100, -10));
    }
}