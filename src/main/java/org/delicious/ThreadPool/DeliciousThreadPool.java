package org.delicious.ThreadPool;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.delicious.BolckingDeque.DeliciousBlockingDeque;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.Optional.ofNullable;

/**
 * @author huangcan
 * Date: 2025/4/27
 * Time: 19:19
 */
public class DeliciousThreadPool {

    private AtomicInteger currentThreadCount = new AtomicInteger(0);
    private int coreSize;
    private int maxSize;
    private int dequeSize;
    private int waitTime;
    private TimeUnit timeUnit;
    private final DeliciousBlockingDeque<MyTask> taskBlockingDeque;
    private final ReentrantLock threadCreateLock;
    private final Runnable rejectFunction;

    public DeliciousThreadPool(int coreSize, int maxSize, int waitTime, TimeUnit timeUnit, int dequeSize, Runnable rejectFunction) {
        this.threadCreateLock = new ReentrantLock();

        this.coreSize = coreSize;
        this.maxSize = maxSize;
        this.waitTime = waitTime;
        this.timeUnit = timeUnit;
        this.dequeSize = dequeSize;
        this.taskBlockingDeque = new DeliciousBlockingDeque<>(dequeSize);
        this.rejectFunction = rejectFunction;
    }

    public void submitTask(MyTask commitTask) {
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
                System.out.println("辅助线程创建失败");
                if (Objects.isNull(rejectFunction)) {
                    throw new RuntimeException("线程池满了,当前队列元素" + this.taskBlockingDeque.size() + "。" + commitTask.getTaskName() + "被抛弃");
                }
                rejectFunction.run();
            }
        }
    }

    private boolean startThread(MyTask commitTask, boolean isCoreThread) {
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
    private void startCoreThread(MyTask firstTask) {
        Thread thread = new Thread(() -> {
            if (Objects.nonNull(firstTask)) {
                //线程启动时,无需入队，直接运行提交的任务
                System.out.println("核心线程开始执行FirstTask");
                firstTask.getRunnable().run();
            }
            while (true) {
                MyTask myTask = taskBlockingDeque.poll();
                ofNullable(myTask).ifPresent(task -> {
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
    private void startAssistThread(MyTask firstTask) {
        Thread thread = new Thread(() -> {
            if (Objects.nonNull(firstTask)) {
                //线程启动时,无需入队，直接运行提交的任务
                System.out.println("辅助线程开始执行FirstTask");
                firstTask.getRunnable().run();
            }
            while (true) {
                MyTask task;
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

    @Data
    @Builder
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyTask {
        private Runnable runnable;
        private String taskName;
    }
}
