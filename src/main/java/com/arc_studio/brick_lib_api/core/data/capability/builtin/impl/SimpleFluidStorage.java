package com.arc_studio.brick_lib_api.core.data.capability.builtin.impl;

import com.arc_studio.brick_lib_api.core.data.capability.builtin.IFluidHandler;
import com.arc_studio.brick_lib_api.core.data.capability.transaction.SnapshotParticipant;
import com.arc_studio.brick_lib_api.core.data.capability.transaction.TransactionContext;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * 单储罐流体存储的默认实现
 * <p>
 * 支持事务快照回滚。一个储罐只能存储一种流体。
 * </p>
 */
public class SimpleFluidStorage extends SnapshotParticipant<SimpleFluidStorage.FluidSnapshot> implements IFluidHandler {

    private static final long BUCKET_SNAP_EPSILON = 81L;

    @Nullable
    private Fluid fluid;
    private long amount;
    private final long capacity;

    /**
     * @param capacity 最大容量（droplets）
     */
    public SimpleFluidStorage(long capacity) {
        this.capacity = capacity;
        this.fluid = null;
        this.amount = 0;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    @Nullable
    public Fluid getFluidInTank(int tank) {
        checkTank(tank);
        return fluid;
    }

    @Override
    public long getFluidAmountInTank(int tank) {
        checkTank(tank);
        return amount;
    }

    @Override
    public long getTankCapacity(int tank) {
        checkTank(tank);
        return capacity;
    }

    @Override
    public boolean isFluidValid(int tank, Fluid fluid) {
        checkTank(tank);
        return fluid != null;
    }

    @Override
    public long fill(Fluid fluid, long maxAmount, TransactionContext tx) {
        if (fluid == null || maxAmount <= 0) return 0;

        // 如果储罐非空且流体不匹配，则不能填入
        if (this.fluid != null && this.fluid != fluid) return 0;

        updateSnapshot(tx);

        long filled = Math.min(maxAmount, capacity - amount);
        if (filled > 0) {
            this.fluid = fluid;
            amount += filled;
            amount = normalizeAmount(amount);
        }
        return filled;
    }

    @Override
    public long drain(Fluid fluid, long maxAmount, TransactionContext tx) {
        if (fluid == null || maxAmount <= 0 || this.fluid == null) return 0;
        if (this.fluid != fluid) return 0;

        return doDrain(maxAmount, tx);
    }

    @Override
    public long drain(long maxAmount, TransactionContext tx) {
        if (maxAmount <= 0 || fluid == null) return 0;
        return doDrain(maxAmount, tx);
    }

    private long doDrain(long maxAmount, TransactionContext tx) {
        updateSnapshot(tx);

        long drained = Math.min(maxAmount, amount);
        amount -= drained;
        amount = normalizeAmount(amount);
        if (amount == 0) {
            fluid = null;
        }
        return drained;
    }

    /**
     * 直接设置内容（绕过事务，用于反序列化）
     */
    public void setContent(@Nullable Fluid fluid, long amount) {
        this.fluid = fluid;
        this.amount = fluid == null ? 0 : normalizeAmount(amount);
        if (this.amount == 0) {
            this.fluid = null;
        }
    }

    /**
     * 是否为空
     */
    public boolean isEmpty() {
        return fluid == null || amount == 0;
    }

    private void checkTank(int tank) {
        if (tank != 0) {
            throw new IndexOutOfBoundsException("Tank index " + tank + " out of range for single-tank storage.");
        }
    }

    private long normalizeAmount(long amount) {
        long clamped = Math.max(0, Math.min(amount, capacity));
        if (clamped == 0 || clamped == capacity) {
            return clamped;
        }

        long remainder = clamped % IFluidHandler.BUCKET;
        if (remainder == 0) {
            return clamped;
        }
        if (remainder <= BUCKET_SNAP_EPSILON) {
            return clamped - remainder;
        }

        long upperDelta = IFluidHandler.BUCKET - remainder;
        if (upperDelta <= BUCKET_SNAP_EPSILON) {
            return Math.min(capacity, clamped + upperDelta);
        }
        return clamped;
    }

    // ---- SnapshotParticipant ----

    @Override
    protected FluidSnapshot createSnapshot() {
        return new FluidSnapshot(fluid, amount);
    }

    @Override
    protected void readSnapshot(FluidSnapshot snapshot) {
        this.fluid = snapshot.fluid;
        this.amount = snapshot.amount;
    }

    public static final class FluidSnapshot {
        private final @Nullable Fluid fluid;
        private final long amount;

        private FluidSnapshot(@Nullable Fluid fluid, long amount) {
            this.fluid = fluid;
            this.amount = amount;
        }

        public @Nullable Fluid fluid() {
            return fluid;
        }

        public long amount() {
            return amount;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (FluidSnapshot) obj;
            return Objects.equals(this.fluid, that.fluid) &&
                this.amount == that.amount;
        }

        @Override
        public int hashCode() {
            return Objects.hash(fluid, amount);
        }

        @Override
        public String toString() {
            return "FluidSnapshot[" +
                "fluid=" + fluid + ", " +
                "amount=" + amount + ']';
        }

        }
}
