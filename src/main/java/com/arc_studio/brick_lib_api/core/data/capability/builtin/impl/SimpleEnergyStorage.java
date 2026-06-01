package com.arc_studio.brick_lib_api.core.data.capability.builtin.impl;

import com.arc_studio.brick_lib_api.core.data.capability.builtin.IEnergyStorage;
import com.arc_studio.brick_lib_api.core.data.capability.transaction.BrickTransactionContext;
import com.arc_studio.brick_lib_api.core.data.capability.transaction.BrickSnapshotParticipant;

/**
 * 能量存储的默认实现
 * <p>
 * 支持事务快照回滚，可配置接收/提取速率。
 * </p>
 */
public class SimpleEnergyStorage extends BrickSnapshotParticipant<Long> implements IEnergyStorage {

    private long energy;
    private final long capacity;
    private final long maxReceive;
    private final long maxExtract;

    /**
     * @param capacity   最大容量
     * @param maxReceive 每次最大接收量
     * @param maxExtract 每次最大提取量
     */
    public SimpleEnergyStorage(long capacity, long maxReceive, long maxExtract) {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.energy = 0;
    }

    /**
     * @param capacity      最大容量
     * @param maxTransfer   每次最大传输量（接收和提取相同）
     */
    public SimpleEnergyStorage(long capacity, long maxTransfer) {
        this(capacity, maxTransfer, maxTransfer);
    }

    @Override
    public long receiveEnergy(long maxReceive, BrickTransactionContext tx) {
        if (!canReceive()) return 0;
        updateSnapshot(tx);

        long received = Math.min(this.maxReceive, Math.min(maxReceive, capacity - energy));
        energy += received;
        return received;
    }

    @Override
    public long extractEnergy(long maxExtract, BrickTransactionContext tx) {
        if (!canExtract()) return 0;
        updateSnapshot(tx);

        long extracted = Math.min(this.maxExtract, Math.min(maxExtract, energy));
        energy -= extracted;
        return extracted;
    }

    @Override
    public long getEnergyStored() {
        return energy;
    }

    @Override
    public long getMaxEnergyStored() {
        return capacity;
    }

    @Override
    public boolean canReceive() {
        return maxReceive > 0;
    }

    @Override
    public boolean canExtract() {
        return maxExtract > 0;
    }

    /**
     * 直接设置能量值（绕过事务，用于反序列化）
     */
    public void setEnergy(long energy) {
        this.energy = Math.max(0, Math.min(energy, capacity));
    }

    // ---- BrickSnapshotParticipant ----

    @Override
    protected Long createSnapshot() {
        return energy;
    }

    @Override
    protected void readSnapshot(Long snapshot) {
        this.energy = snapshot;
    }
}

