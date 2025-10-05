package com.arc_studio.brick_lib_api.core.task;

import java.util.function.Supplier;

public class ConditionTask extends Task{
    protected final Supplier<Boolean> condition;

    public ConditionTask(Supplier<Boolean> condition,Runnable runnable) {
        super(runnable);
        this.condition = condition;
    }

    @Override
    public void update() {
        if(condition.get()){
            runnable.run();
            markRemove();
        }
    }
}
