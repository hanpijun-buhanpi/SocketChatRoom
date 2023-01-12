package service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 任务执行类
public class TaskPool {
    // 本类使用单例模式
    private static TaskPool taskPool;

    // 任务列表
    private Map<String ,List<Task>> taskLists;
    // 线程列表
    private List<Thread> threadList;
    // 线程数量
    private int threadSize;
    // 线程指针，用来注册任务
    private int threadIndex;

    // 私有化构造，初始化变量和运行线程
    private TaskPool() {
        taskLists = new HashMap<>();
        threadList = new ArrayList<>();
        threadIndex = 0;
        threadSize = 4;
        threadStart();
    }
    // 获取单例
    public static TaskPool getInstance() {
        if (taskPool == null) {
            taskPool = new TaskPool();
        }
        return taskPool;
    }

    // 任务线程创建并执行
    private void threadStart() {
        for (int i = 0; i < threadSize; i++) {
            String name = "Thread-task-" + i;
            taskLists.put(name, new ArrayList<>());
            Thread thread = new Thread(() -> {
                while (true) {
                    List<Task> list = taskLists.get(name);
                    try {
                        if (list.size() == 0) {
                            synchronized (list) {
                                list.wait();
                            }
                        }
                        list.get(0).run();
                        list.remove(0);
                    } catch (Exception ignored) { }

                }
            });
            thread.setName(name);
            thread.start();
            threadList.add(thread);
        }
    }
    // 任务注册，并唤醒等待的线程
    synchronized public void registry(Task task) {
        if (threadIndex >= threadSize) {
            threadIndex = 0;
        }
        List<Task> list = taskLists.get("Thread-task-" + threadIndex);
        synchronized (taskLists) {
            list.add(task);
        }
        synchronized (list) {
            list.notifyAll();
        }
        threadIndex++;
    }
}
