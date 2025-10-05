package com.arc_studio.brick_lib_api.core.task;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

/**
 * <p>在服务端上执行任务的工具类</p>
 * <p>内部维护一个队列，服务端上每tick都会遍历所有任务并执行下面两个操作之一</p>
 * <p>如果任务没有标志移除，则执行{@link Task#update()}</p>
 * <p>如果任务标志移除，则执行{@link Task#remove()}后将其从任务队列中移除</p>
 * */

public class TaskExecutor {
    private TaskExecutor(){}
    private static final ConcurrentLinkedQueue<Task> taskQueue = new ConcurrentLinkedQueue<>();

    /**
     * 创建一个延迟执行的任务并将其添加到任务队列中。
     *
     * @param tick 延迟的 ticks 数
     * @param runnable 要执行的任务
     */
    public static void newDelayTask(int tick, Runnable runnable) {
        taskQueue.add(new DelayTask(tick,runnable));
    }
    /**
     * 创建一个重复执行的任务并将其添加到任务队列中。
     *
     * @param repeatCount 重复执行的次数
     * @param runnable 要执行的任务
     */
    public static void newRepeatTask(int repeatCount, Runnable runnable) {
        taskQueue.add(new RepeatTask(repeatCount,runnable));
    }
    /**
     * 创建一个条件执行的任务并将其添加到任务队列中。
     *
     * @param condition 条件，返回true的时候任务执行
     * @param runnable 要执行的任务
     */
    public static void newConditionTask(Supplier<Boolean> condition, Runnable runnable) {
        taskQueue.add(new ConditionTask(condition,runnable));
    }

    public static void addTask(Task task){
        taskQueue.add(task);
    }
    public static void tick(){
        Iterator<Task> iterator = taskQueue.iterator();
        while (iterator.hasNext()) {
            Task task = iterator.next();
            if (task.remove) {
                task.remove();
                iterator.remove();
            }else{
                task.update();
            }
        }
    }

}
