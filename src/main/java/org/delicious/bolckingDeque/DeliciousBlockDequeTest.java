package org.delicious.bolckingDeque;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static org.delicious.util.SleepUtil.sleep;

/**
 * @author huangcan
 * Date: 2025/4/29
 * Time: 14:50
 */
public class DeliciousBlockDequeTest {

    // ANSI 转义码前缀
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";

    public static void main(String[] args) {
        DeliciousBlockingDeque<String> parallelBlockingDeque = new DeliciousBlockingDeque<>(100);
        initBlockingDeque(parallelBlockingDeque);
        sleep(2000);

        startConsumer("[自动消费者线程1]", 5000, 4000, RED, parallelBlockingDeque);
        startConsumer("[自动消费者线程2]", 0, 8000, BLUE, parallelBlockingDeque);
        /*startProducer("[自动生产者线程]", 5000, YELLOW, parallelBlockingDeque);*/

        Thread inputThread = new Thread(() -> {
            while (true) {
                Scanner scanner = new Scanner(System.in);
                parallelBlockingDeque.offer(scanner.next());
            }
        });
        inputThread.setName("[手动生产线程]");
        inputThread.start();
    }

    private static void initBlockingDeque(DeliciousBlockingDeque<String> parallelBlockingDeque) {
        //, "D", "E", "F", "G", "H", "I", "J"};
        String[] letter = new String[]{"A", "B", "C"};
        //, "4", "5", "6", "7", "8", "9", "10"};
        String[] number = new String[]{"1", "2", "3"};
        Thread producer1 = new Thread(() -> {
            for (String s : letter) {
                sleep(20);
                parallelBlockingDeque.offer(s);
            }
            System.out.println(parallelBlockingDeque.size());
            System.out.println(parallelBlockingDeque);
        });
        producer1.setName("[生产者1]");
        producer1.start();
        Thread producer2 = new Thread(() -> {
            for (String s : number) {
                sleep(20);
                parallelBlockingDeque.offer(s);
            }
            System.out.println(parallelBlockingDeque.size());
            System.out.println(parallelBlockingDeque);
        });
        producer2.setName("[生产者2]");
        producer2.start();
    }

    private static void startConsumer(String consumerName, long waitTime, long sleepTime, String printColor, DeliciousBlockingDeque<String> parallelBlockingDeque) {
        Thread consumerThread1 = new Thread(() -> {
            while (true) {
                long startTime = System.currentTimeMillis();
                String poll = parallelBlockingDeque.poll(waitTime, TimeUnit.MILLISECONDS);
                System.out.println("\n" + printColor + Thread.currentThread().getName() + "取到元素「" + poll + "」耗时 " + (System.currentTimeMillis() - startTime)
                        + " ==队列剩余「" + parallelBlockingDeque + "」==" + RESET + "\n");
                sleep(sleepTime);
            }
        });
        consumerThread1.setName(consumerName);
        consumerThread1.start();
    }


    private static void startProducer(String consumerName, long sleepTime, String printColor, DeliciousBlockingDeque<String> parallelBlockingDeque) {
        Thread producer3 = new Thread(() -> {
            int i = 0;
            while (true) {
                if (i > 10000) {
                    i = 0;
                }
                sleep(sleepTime);
                parallelBlockingDeque.offer(String.valueOf(i++));
                System.out.println();
                System.out.println("\n" + printColor + Thread.currentThread().getName() + "添加元素"
                        + "==队列剩余「" + parallelBlockingDeque + "」==" + RESET + "\n");
                System.out.println();
            }
        });
        producer3.setName(consumerName);
        producer3.start();
    }
}
