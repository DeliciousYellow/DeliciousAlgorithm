package org.delicious.util;

/**
 * @author huangcan
 * Date: 2025/4/29
 * Time: 15:10
 */
public class SleepUtil {
    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
