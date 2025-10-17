package com.arc_studio.brick_lib_api.core.event;

/**
 * 与模组加载器平台无关的Brick Lib事件基类
 */
public abstract class BaseEvent {
    protected boolean isCanceled = false;
    protected Result result = Result.SUCCESS;

    /**
     * 如果这个事件可以被取消，则取消这个事件
     *
     * @throws UnsupportedOperationException 如果事件不可被取消
     */
    public void cancel() {
        if (!(this instanceof ICancelableEvent)) {
            throw new UnsupportedOperationException("Tried to cancel " + this.getClass().getName() + " but it's not cancelable! Did you forgot interface ICancelableEvent?");
        }
        isCanceled = true;
    }

    /**
     * 事件是否已经被取消
     */
    public boolean isCanceled() {
        return isCanceled;
    }

    /**
     * 获取事件的结果
     */
    public Result getResult() {
        if (!(this instanceof IResultEvent)) {
            throw new UnsupportedOperationException("Tried to get the result of events " + this.getClass().getName() + ", but it didn't have result! Have you forgotten the IResultEvent interface?");
        }
        return result;
    }

    /**
     * 设置事件的结果
     */
    public void setResult(Result result) {
        if (!(this instanceof IResultEvent)) {
            throw new UnsupportedOperationException("Tried to get the result of events " + this.getClass().getName() + ", but it didn't have result! Have you forgotten the IResultEvent interface?");
        }
        this.result = result;
    }

    /**
     * 事件的结果
     */
    public enum Result {
        SUCCESS,//事件成功
        CONSUME,//事件被消耗
        FAIL,//事件失败
        PASS;//事件被跳过

        public boolean consumesAction() {
            return this == SUCCESS || this == CONSUME;
        }
    }
}
