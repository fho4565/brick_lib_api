package com.arc_studio.brick_lib_api.core.task;

public abstract class Task {
    protected boolean remove = false;
    protected final Runnable runnable;
    public Task(Runnable runnable){
        this.runnable = runnable;
    }
    /**
     * 任务更新方法
     * */
    public abstract void update();
    /**
     * 标志该任务需要被移除
     * */
    public void markRemove(){
        this.remove = true;
    }
    public void remove(){
        
    }
}
