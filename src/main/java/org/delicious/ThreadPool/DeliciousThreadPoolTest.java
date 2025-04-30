package org.delicious.ThreadPool;

import java.util.concurrent.TimeUnit;

import static org.delicious.util.SleepUtil.sleep;

/**
 * @author huangcan
 * Date: 2025/4/25
 * Time: 10:47
 */
public class DeliciousThreadPoolTest {
    public static void main(String[] args) {
        String[] taskNameArray = new String[]{"A", "B", "C", "D", "E", "F", "G",
                //G装不下
                "H", "I", "J", "K", "L", "M", "N"};

        /*ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 3, 0, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(3));
        for (int i = 0; i < taskNameArray.length; i++) {
            String taskName = taskNameArray[i];
            try {
                threadPoolExecutor.execute(() -> {
                    for (int j = 0; j < 50; j++) {
                        System.out.println(taskName + ": " + j);
                        sleep(100);
                    }
                });
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            if (i == 6) {
                System.out.println("等待执行完毕");
                sleep(20000);
            }
        }*/

        DeliciousThreadPool deliciousThreadPool = new DeliciousThreadPool(2, 3, 1000, TimeUnit.MILLISECONDS, 3,
                (commitTask, currentThreadPool) -> {
                    System.out.println("自定义行为,打印一个任务名称" + commitTask.getTaskName());
                    return null;
                });
        for (int i = 0; i < taskNameArray.length; i++) {
            try {
                String taskName = taskNameArray[i];
                deliciousThreadPool.submitTask(DeliciousThreadPool.DeliciousTask.builder()
                        .taskName(taskName + "任务")
                        .runnable(() -> {
                            for (int j = 0; j < 50; j++) {
                                System.out.println(taskName + ": " + j);
                                sleep(100);
                            }
                        })
                        .build());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            if (i == 6) {
                sleep(20000);
            }
        }
    }
}