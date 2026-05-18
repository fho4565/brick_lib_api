package com.arc_studio.brick_lib_api.core.data.capability.builtin;

import com.arc_studio.brick_lib_api.core.data.capability.core.AutoRegisterCapability;
import com.arc_studio.brick_lib_api.core.data.capability.transaction.TransactionContext;
import net.minecraft.world.level.material.Fluid;

/**
 * 流体存储能力接口
 * <p>
 * 定义流体的插入、提取、查询操作。
 * 数量单位为 droplets（1 bucket = 81000 droplets，与 Fabric 对齐）。
 * </p>
 */
@AutoRegisterCapability
public interface IFluidHandler {

    /**
     * 1 桶 = 81000 液滴
     */
    long BUCKET = 81000L;

    /**
     * 1 瓶 = 27000 液滴
     */
    long BOTTLE = 27000L;

    /**
     * 1 锭 = 9000 液滴
     */
    long INGOT = 9000L;

    /**
     * 获取流体储罐数量
     */
    int getTanks();

    /**
     * 获取指定储罐中的流体类型
     *
     * @param tank 储罐索引
     * @return 流体类型
     */
    Fluid getFluidInTank(int tank);

    /**
     * 获取指定储罐中的流体数量（droplets）
     *
     * @param tank 储罐索引
     * @return 数量
     */
    long getFluidAmountInTank(int tank);

    /**
     * 获取指定储罐的最大容量（droplets）
     *
     * @param tank 储罐索引
     * @return 最大容量
     */
    long getTankCapacity(int tank);

    /**
     * 检查指定储罐是否允许填入给定流体
     *
     * @param tank  储罐索引
     * @param fluid 流体类型
     * @return 是否允许
     */
    boolean isFluidValid(int tank, Fluid fluid);

    /**
     * 填入流体
     *
     * @param fluid     流体类型
     * @param maxAmount 最大填入量（droplets）
     * @param tx        事务上下文
     * @return 实际填入量
     */
    long fill(Fluid fluid, long maxAmount, TransactionContext tx);

    /**
     * 排出指定流体
     *
     * @param fluid     要排出的流体类型
     * @param maxAmount 最大排出量（droplets）
     * @param tx        事务上下文
     * @return 实际排出量
     */
    long drain(Fluid fluid, long maxAmount, TransactionContext tx);

    /**
     * 排出任意流体
     *
     * @param maxAmount 最大排出量（droplets）
     * @param tx        事务上下文
     * @return 实际排出量
     */
    long drain(long maxAmount, TransactionContext tx);
}
