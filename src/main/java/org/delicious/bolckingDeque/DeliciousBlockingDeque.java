package org.delicious.bolckingDeque;


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

    private final Node<T> headNode;
    private final Node<T> endNode; 
    private final AtomicInteger currentNodeCount;
    private final AtomicInteger maxNodeCount;

    private final List<Thread> pollWaitThreads = new ArrayList<>();
    private final ReentrantLock pollWaitThreadsLock = new ReentrantLock();
    private final ReentrantLock offerAndPollLock = new ReentrantLock();

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
                offerAndPollLock.lock();
                if (size() < maxNodeCount.get()) {
                    Node<T> currentNode = Node.<T>builder().data(data).build();
                    endNode.pre.next = currentNode;
                    currentNode.pre = endNode.pre;
                    currentNode.next = endNode;
                    endNode.pre = currentNode;

                    this.currentNodeCount.incrementAndGet();

                    try {
                        pollWaitThreadsLock.lock();
                        this.pollWaitThreads.stream()
                                .findFirst()
                                .ifPresent(needAwakeThread -> {
                                    //FIXME 这队列里元素越来越多
                                    this.pollWaitThreads.remove(needAwakeThread);
                                    synchronized (needAwakeThread) {
                                        needAwakeThread.notify();
                                    }
                                    System.out.println(Thread.currentThread().getName() + "已唤醒" + needAwakeThread.getName());
                                });
                    } finally {
                        pollWaitThreadsLock.unlock();
                    }
                    return true;
                }
            } finally {
                offerAndPollLock.unlock();
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
                try {
                    offerAndPollLock.lock();
                    if (size() > 0) {
                        Node<T> currentNode = this.headNode.next;
                        currentNode.next.pre = headNode;
                        headNode.next = currentNode.next;
                        this.currentNodeCount.decrementAndGet();
                        return currentNode.data;
                    }
                } finally {
                    offerAndPollLock.unlock();
                }
            } else {
                Thread currentThread = Thread.currentThread();
                try {
                    pollWaitThreadsLock.lock();
                    pollWaitThreads.add(currentThread);
                } finally {
                    pollWaitThreadsLock.unlock();
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
            System.out.println("\n" + thread.getName() + "开始阻塞等待\n");
            long startWaitTime = System.currentTimeMillis();
            synchronized (thread) {
                thread.wait(time);
            }
            if (time > 0 && System.currentTimeMillis() - startWaitTime >= time) {
                System.out.println("\n" + thread.getName() + "阻塞达最大时长\n");
                return true;
            } else {
                System.out.println("\n" + thread.getName() + "被唤醒\n");
                return false;
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
