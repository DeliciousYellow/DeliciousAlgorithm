package org.delicious.BolckingDeque;

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
        /*DeliciousBlockingDeque<String> blockingDeque = new DeliciousBlockingDeque<>(5);
        System.out.println(blockingDeque.size());
        System.out.println(blockingDeque.offer("A"));
        System.out.println(blockingDeque.offer("B"));
        System.out.println(blockingDeque.offer("C"));
        System.out.println(blockingDeque.poll());
        System.out.println(blockingDeque.size());
        System.out.println(blockingDeque.offer("D"));
        System.out.println(blockingDeque.offer("E"));
        System.out.println(blockingDeque.offer("F"));
        System.out.println(blockingDeque.offer("G"));
        System.out.println(blockingDeque.size());
        System.out.println(blockingDeque);
        for (int i = 0; i < 5; i++) {
            blockingDeque.poll();
        }
        System.out.println(blockingDeque.poll());*/
        System.out.println("=============================================");

        DeliciousBlockingDeque<String> parallelBlockingDeque = new DeliciousBlockingDeque<>(100);
        String[] letter = new String[]{"A", "B", "C"};//, "D", "E", "F", "G", "H", "I", "J"};
        String[] number = new String[]{"1", "2", "3"};//, "4", "5", "6", "7", "8", "9", "10"};
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

        sleep(2000);

        Thread consumerThread1 = new Thread(() -> {
            while (true) {
                long startTime = System.currentTimeMillis();
                String poll = parallelBlockingDeque.poll(0, TimeUnit.MILLISECONDS);
                System.out.println("\n" + RED + Thread.currentThread().getName() + "取到元素「" + poll + "」耗时 " + (System.currentTimeMillis() - startTime)
                        + " ==队列剩余「" + parallelBlockingDeque + "」==" + RESET + "\n");
                sleep(4000);
            }
        });
        consumerThread1.setName("[自动消费者线程1]");
        consumerThread1.start();

        Thread consumerThread2 = new Thread(() -> {
            while (true) {
                long startTime = System.currentTimeMillis();
                String poll = parallelBlockingDeque.poll(20000, TimeUnit.MILLISECONDS);
                System.out.println("\n" + BLUE + Thread.currentThread().getName() + "取到元素「" + poll + "」耗时 " + (System.currentTimeMillis() - startTime)
                        + " ==队列剩余「" + parallelBlockingDeque + "」==" + RESET + "\n");
                sleep(8000);
            }
        });
        consumerThread2.setName("[自动消费者线程2]");
        consumerThread2.start();

        /*Thread producer3 = new Thread(() -> {
            int i = 0;
            while (true) {
                if (i > 10000) {
                    i = 0;
                }
                sleep(5000);
                parallelBlockingDeque.offer(String.valueOf(i++));
                System.out.println();
                System.out.println(Thread.currentThread().getName() + "添加元素" + "==队列剩余「" + parallelBlockingDeque + "」==");
                System.out.println();
            }
        });
        producer3.setName("[自动生产者线程]");
        producer3.start();*/

        Thread inputThread = new Thread(() -> {
            while (true) {
                Scanner scanner = new Scanner(System.in);
                boolean offer = parallelBlockingDeque.offer(scanner.next());
            }
        });
        inputThread.setName("[手动生产线程]");
        inputThread.start();

    }

}
