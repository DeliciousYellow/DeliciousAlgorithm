package org.delicious.BolckingDeque;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author huangcan
 * Date: 2025/4/29
 * Time: 14:20
 */
public class DeliciousBlockingDeque<T> {

    private Node<T> headNode;
    private Node<T> endNode;
    private AtomicInteger currentNodeCount;
    private AtomicInteger maxNodeCount;

    private List<Thread> pollThreads = new ArrayList<>();

    private ReentrantLock offerLock = new ReentrantLock();

    public DeliciousBlockingDeque(int maxNodeCount) {
        this.maxNodeCount = new AtomicInteger(maxNodeCount);
        this.currentNodeCount = new AtomicInteger(0);

        this.headNode = Node.<T>builder().build();
        this.endNode = Node.<T>builder().build();
        this.headNode.next = this.endNode;
        this.endNode.pre = this.headNode;
    }

    public int size() {
        return currentNodeCount.get();
    }

    /**
     * 从右存
     */
    public boolean offer(T data) {
        if (size() < maxNodeCount.get()) {
            try {
                offerLock.lock();
                if (size() < maxNodeCount.get()) {
                    Node<T> currentNode = Node.<T>builder().data(data).build();
                    endNode.pre.next = currentNode;
                    currentNode.pre = endNode.pre;
                    currentNode.next = endNode;
                    endNode.pre = currentNode;

                    this.currentNodeCount.incrementAndGet();

                    this.pollThreads.stream()
                            .findFirst()
                            .ifPresent(needAwakeThread -> {
                                synchronized (needAwakeThread) {
                                    needAwakeThread.notify();
                                    System.out.println(Thread.currentThread().getName() + "已唤醒" + needAwakeThread.getName());
                                    this.pollThreads.remove(needAwakeThread);
                                }
                            });
                    return true;
                }
            } finally {
                offerLock.unlock();
            }
        }
        return false;
    }

    /**
     * 从左取
     */
    public T poll() {
        return poll(0, TimeUnit.MILLISECONDS);
    }

    /**
     * 从左取
     */
    public T poll(long time, TimeUnit timeUnit) {
        time = timeUnit.toMillis(time);
        while (true) {
            if (size() > 0) {
                synchronized (this) {
                    if (size() > 0) {
                        Node<T> currentNode = this.headNode.next;
                        currentNode.next.pre = headNode;
                        headNode.next = currentNode.next;
                        this.currentNodeCount.decrementAndGet();
                        return currentNode.data;
                    }
                }
            } else {
                Thread currentThread = Thread.currentThread();
                synchronized (this) {
                    pollThreads.add(currentThread);
                }
                if (threadWait(currentThread, time)) {
                    //如果是超时唤醒，返回null
                    return null;
                }
            }
        }
    }

    private static boolean threadWait(Thread thread, long time) {
        try {
            synchronized (thread) {
                System.out.println("\n" + thread.getName() + "开始阻塞等待\n");
                long startWaitTime = System.currentTimeMillis();
                thread.wait(time);
                if (time > 0 && System.currentTimeMillis() - startWaitTime >= time) {
                    System.out.println("\n" + thread.getName() + "阻塞达最大时长\n");
                    return true;
                } else {
                    System.out.println("\n" + thread.getName() + "被唤醒\n");
                    return false;
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void threadWait(Thread thread) {
        try {
            synchronized (thread) {
                System.out.println("\n" + thread.getName() + "开始阻塞等待\n");
                thread.wait();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Node<T> currentNode = this.headNode;
        for (int i = 0; i < size(); i++) {
            currentNode = currentNode.next;
            sb.append(currentNode.data.toString());
            sb.append(" ");
        }
        return sb.toString();
    }

    @Data
    @Builder
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Node<T> {
        private T data;
        private Node<T> pre;
        private Node<T> next;
    }

}
