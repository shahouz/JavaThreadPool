package com.shahouz.javaThreadPool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @ProjectName: awsbootweb
 * @Package: com.feibo.awsbootweb.service
 * @Description: 描述
 * @Author: tdl
 * @CreateDate: 2018/8/22 下午2:18
 **/
public class ThreadPoolService {
    private static final int THREAD_MAX = 5;
    private static final int WORKERS_QUEUE_MAX = 10;
    private static final int TASK_QUEUE_MAX = 10;

    private final ArrayBlockingQueue<Worker> workers = new ArrayBlockingQueue<Worker>(WORKERS_QUEUE_MAX);
    private final LinkedBlockingQueue<Runnable> jobs = new LinkedBlockingQueue<Runnable>(TASK_QUEUE_MAX);

    public void init() {
        for (int i = 0; i < THREAD_MAX; i++) {
            Worker worker = new Worker();
            workers.add(worker);
            Thread thread = new Thread(worker);
            // 试试是否为后台线程有什么差别
            // thread.setDaemon(true);
            thread.start();
            System.out.print("Get a new worker.\n");
        }
    }

    public void execute(Runnable runnable) {
        jobs.add(runnable);
    }

    public class Worker extends Thread {
        private volatile boolean running = true;

        @Override
        public void run() {
            while (running) {
                System.out.print("Worker '"+ Thread.currentThread().getName() +"' is on.\n");
                Runnable job = null;

                synchronized (jobs) {
                    if (jobs.isEmpty()) {
                        try {
                            jobs.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    try {
                        job = jobs.take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (job != null) {
                    job.run();
                }
            }
        }

        // 终止该线程
        public void shutdown() {
            running = false;
        }
    }
}
