package org.delicious.ThreadPool;

import lombok.*;
import lombok.experimental.Accessors;
import org.delicious.BolckingDeque.DeliciousBlockingDeque;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;

import static java.util.Optional.ofNullable;

public class DeliciousThreadPool {

    private int coreSize;
    private int maxSize;
    private int waitTime;
    private TimeUnit timeUnit;
    private final AtomicInteger currentThreadCount = new AtomicInteger(0);
    private final DeliciousBlockingDeque<DeliciousTask> taskBlockingDeque;
    private final ReentrantLock threadCreateLock;

    private final BiFunction<DeliciousTask, DeliciousThreadPool, Void> rejectFunction;

    private static final BiFunction<DeliciousTask, DeliciousThreadPool, Void> DEFAULT = (task, threadPool) -> {
        throw new RuntimeException("线程池满了,当前队列元素" + threadPool.currentThreadCount.get() + "&&" + task.getTaskName() + "被抛弃");
    };

    public DeliciousThreadPool(int coreSize, int maxSize, int waitTime, TimeUnit timeUnit, int dequeSize) {
        this(coreSize, maxSize, waitTime, timeUnit, dequeSize, DEFAULT);
    }

    public DeliciousThreadPool(int coreSize, int maxSize, int waitTime, TimeUnit timeUnit, int dequeSize, BiFunction<DeliciousTask, DeliciousThreadPool, Void> rejectFunction) {
        this.threadCreateLock = new ReentrantLock();
        this.coreSize = coreSize;
        this.maxSize = maxSize;
        this.waitTime = waitTime;
        this.timeUnit = timeUnit;
        this.taskBlockingDeque = new DeliciousBlockingDeque<>(dequeSize);
        this.rejectFunction = rejectFunction;
    }

    public void submitTask(DeliciousTask commitTask) {
        if (startThread(commitTask, true)) {
            System.out.println("核心线程创建成功");
            return;
        }

        boolean offer = taskBlockingDeque.offer(commitTask);
        if (!offer) {
            System.out.println(commitTask.getTaskName() + "入队失败，尝试创建辅助线程");
            if (startThread(commitTask, false)) {
                System.out.println("辅助线程创建成功");
            } else {
                System.out.println("辅助线程创建失败，执行拒绝策略");
                rejectFunction.apply(commitTask, this);
            }
        }
    }

    private boolean startThread(DeliciousTask commitTask, boolean isCoreThread) {
        boolean successfullyCreate = false;
        int threadSize = isCoreThread ? coreSize : maxSize;
        if (currentThreadCount.get() < threadSize) {
            try {
                this.threadCreateLock.lock();
                if (currentThreadCount.get() < threadSize) {
                    if (isCoreThread) {
                        startCoreThread(commitTask);
                    } else {
                        startAssistThread(commitTask);
                    }
                    successfullyCreate = true;
                } else {
                    System.out.println("并发创建，已拦截");
                }
            } finally {
                this.threadCreateLock.unlock();
            }
        }
        return successfullyCreate;
    }

    /**
     * 启动一个核心线程
     */
    private void startCoreThread(DeliciousTask firstTask) {
        Thread thread = new Thread(() -> {
            if (Objects.nonNull(firstTask)) {
                //线程启动时,无需入队，直接运行提交的任务
                System.out.println("核心线程开始执行FirstTask");
                firstTask.getRunnable().run();
            }
            while (true) {
                DeliciousTask deliciousTask = taskBlockingDeque.poll();
                ofNullable(deliciousTask).ifPresent(task -> {
                    System.out.println("核心线程获取到" + task.getTaskName());
                    task.getRunnable().run();
                });
            }
        });
        currentThreadCount.incrementAndGet();

        thread.start();
    }

    /**
     * 启动一个辅助线程
     */
    private void startAssistThread(DeliciousTask firstTask) {
        Thread thread = new Thread(() -> {
            if (Objects.nonNull(firstTask)) {
                //线程启动时,无需入队，直接运行提交的任务
                System.out.println("辅助线程开始执行FirstTask");
                firstTask.getRunnable().run();
            }
            while (true) {
                DeliciousTask task;
                long startTime = System.currentTimeMillis();
                try {
                    task = taskBlockingDeque.poll(this.waitTime, this.timeUnit);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                if (Objects.nonNull(task)) {
                    System.out.println("辅助线程获取到" + task.getTaskName());
                    task.getRunnable().run();
                } else {
                    System.out.println("辅助线程等待 " + (System.currentTimeMillis() - startTime) + " 准备销毁");
                    break;
                }
            }
            System.out.println("辅助线程销毁");
            currentThreadCount.decrementAndGet();
        });
        currentThreadCount.incrementAndGet();

        thread.start();
    }

    public void updateThreadPoolConfig(int coreSize, int maxSize, int waitTime, TimeUnit timeUnit) {
        this.coreSize = coreSize;
        this.maxSize = maxSize;
        this.waitTime = waitTime;
        this.timeUnit = timeUnit;
    }

    @Data
    @Builder
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeliciousTask {
        private Runnable runnable;
        private String taskName;
    }
}
