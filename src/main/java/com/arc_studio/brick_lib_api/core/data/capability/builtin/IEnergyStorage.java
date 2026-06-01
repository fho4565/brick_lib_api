package com.arc_studio.brick_lib_api.core.data.capability.builtin;

import com.arc_studio.brick_lib_api.core.data.capability.transaction.BrickTransactionContext;

/**
 * 能量存储能力接口
 * <p>
 * 定义能量的接收、提取、查询操作，单位为FE(Forge Energy, Fabric Energy, Fancy Energy)
 * </p>
 */
public interface IEnergyStorage {

    /**
     * 接收能量
     *
     * @param maxReceive 最大接收量
     * @param tx         事务上下文
     * @return 实际接收量
     */
    long receiveEnergy(long maxReceive, BrickTransactionContext tx);

    /**
     * 提取能量
     *
     * @param maxExtract 最大提取量
     * @param tx         事务上下文
     * @return 实际提取量
     */
    long extractEnergy(long maxExtract, BrickTransactionContext tx);

    /**
     * 获取当前存储的能量
     */
    long getEnergyStored();

    /**
     * 获取最大能量容量
     */
    long getMaxEnergyStored();

    /**
     * 是否可以接收能量
     */
    boolean canReceive();

    /**
     * 是否可以提取能量
     */
    boolean canExtract();
}

