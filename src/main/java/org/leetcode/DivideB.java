package org.leetcode;

class DivideB {
    public static int divide(int dividend, int divisor) {
        if (dividend == Integer.MIN_VALUE && divisor == -1) {
            return Integer.MAX_VALUE;
        }

        int resultValue = 0;
        int currentDividend = Math.abs(dividend);
        int currentDivisor = Math.abs(divisor);

        while (currentDividend >= currentDivisor) {
            int i = 0;
            while (!((1 << i) * currentDivisor <= currentDividend && (1 << (i + 1)) * currentDivisor > currentDividend)) {
                i++;
            }
            currentDividend -= (1 << i) * currentDivisor;
            resultValue += (1 << i);
        }

        if ((dividend < 0 && divisor > 0) || (dividend > 0 && divisor < 0)) {
            return -resultValue;
        }
        return resultValue;
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