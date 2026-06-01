package com.arc_studio.brick_lib_api.core.data.capability.builtin;

import com.arc_studio.brick_lib_api.core.data.capability.builtin.impl.SimpleFluidStorage;
import com.arc_studio.brick_lib_api.core.data.capability.transaction.BrickTransactionContext;
import net.minecraft.world.level.material.Fluid;

/**
 * <h3>流体存储能力接口</h3>
 * <p>
 * 定义流体的插入、提取、查询操作。
 * 数量单位为 droplets（1 bucket = 81000 droplets，与 Fabric 对齐）。
 * </p>
 * <h3>流体单位转换</h3>
 * <p>
 * BrickLib 内部使用 droplets（81 droplets = 1 mB）实现高精度流体传输，
 * 而 Forge / NeoForge 使用 mB（milli-buckets），转换系数为 81。
 * </p>
 *
 * <h3>公平分摊算法</h3>
 * <p>
 * {@link com.arc_studio.brick_lib_api.core.data.capability.builtin.IFluidStorage#distributeFairly(long, long[])} 用于主动能量推出时
 * 将可用能量按接收者容量公平分配，参考自 Mekanism
 * {@code EmitUtils.sendToAcceptors}。
 * </p>
 */
public interface IFluidStorage {

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
     * 1 粒 = 1000 液滴
     * */
    long NUGGET = 1000L;
    /** 1 mB = 81 droplets（UCS 内部精度单位） */
    long DROPLETS_PER_MB = 81L;

    /**
     * 将 {@code available} 按 {@code simulated} 中每个接收者可接受的比例公平分摊。
     * <p>
     * 逻辑（与 Mekanism {@code EmitUtils.sendToAcceptors} 相似）：
     * <ol>
     *   <li>先按接收者数量均分</li>
     *   <li>能接受少于均分的接收者按实际值分配，多出的部分重新分配给仍有余量的接收者</li>
     *   <li>最终所有剩余数量平分给还能继续接收的接收者</li>
     * </ol>
     * </p>
     *
     * @param available 总可用数量
     * @param simulated 每个接收者模拟阶段确认的最大可接受量
     * @return 每个接收者实际应分配的量
     */
    static long[] distributeFairly(long available, long[] simulated) {
        int count = simulated.length;
        long[] result = new long[count];
        long remaining = available;
        int activeCount = count;

        boolean[] active = new boolean[count];
        for (int i = 0; i < count; i++) {
            active[i] = simulated[i] > 0;
        }

        while (remaining > 0 && activeCount > 0) {
            long fairShare = remaining / activeCount;
            if (fairShare == 0) {
                for (int i = 0; i < count && remaining > 0; i++) {
                    if (active[i]) {
                        result[i]++;
                        remaining--;
                        if (result[i] >= simulated[i]) {
                            active[i] = false;
                            activeCount--;
                        }
                    }
                }
                break;
            }

            for (int i = 0; i < count; i++) {
                if (!active[i]) continue;
                long canTake = simulated[i] - result[i];
                long give = Math.min(fairShare, canTake);
                result[i] += give;
                remaining -= give;
                if (result[i] >= simulated[i]) {
                    active[i] = false;
                    activeCount--;
                }
            }
        }

        return result;
    }

    /**
     * Forge / NeoForge mB → UCS droplets。
     * <p>1 bucket = 1000 mB = 81000 droplets</p>
     *
     * @param mb milli-buckets 值（非负）
     * @return droplets 值，最小值 0
     */
    static long mbToDroplets(int mb) {
        return Math.max(0, mb) * DROPLETS_PER_MB;
    }

    /**
     * UCS droplets → Forge / NeoForge mB。
     * <p>81000 droplets = 1 bucket = 1000 mB</p>
     *
     * @param droplets droplets 值（非负）
     * @return mB 值，已钳位到 [0, Integer.MAX_VALUE]
     */
    static int dropletsToMb(long droplets) {
        return clampToInt(droplets / DROPLETS_PER_MB);
    }

    /**
     * 将 long 安全钳位到 int 范围 [0, Integer.MAX_VALUE]。
     * <p>
     * Forge / NeoForge 的 IEnergyStorage 和 IFluidHandler 均使用 int 作为
     * 能量/流体单位，而 UCS 内部使用 long。此方法确保跨系统转换时不会溢出。
     * </p>
     *
     * @param value 待钳位的 long 值
     * @return 钳位后的 int，始终 ≥ 0 且 ≤ Integer.MAX_VALUE
     */
    static int clampToInt(long value) {
        if (value <= 0) return 0;
        return value > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) value;
    }

    /**
     * 计算指定 SimpleFluidStorage 能接受多少 mB 的指定流体。
     *
     * @param storage UCS 流体存储
     * @param fluid   待填充的流体类型
     * @param maxFill 最大填充量（mB）
     * @return 实际可填充量（mB）
     */
    static int getFillableMb(SimpleFluidStorage storage, Fluid fluid, int maxFill) {
        var storedFluid = storage.getFluidInTank(0);
        if (storedFluid != null && storedFluid != fluid) return 0;
        long remaining = storage.getTankCapacity(0) - storage.getFluidAmountInTank(0);
        return Math.min(maxFill, dropletsToMb(remaining));
    }

    /**
     * 计算指定 SimpleFluidStorage 能排出多少 mB 的指定流体。
     *
     * @param storage   UCS 流体存储
     * @param fluid     待排出的流体类型
     * @param maxDrain  最大排出量（mB）
     * @return 实际可排出量（mB）
     */
    static int getDrainableMb(SimpleFluidStorage storage, Fluid fluid, int maxDrain) {
        if (storage.getFluidInTank(0) != fluid) return 0;
        return Math.min(maxDrain, dropletsToMb(storage.getFluidAmountInTank(0)));
    }

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
    long fill(Fluid fluid, long maxAmount, BrickTransactionContext tx);

    /**
     * 排出指定流体
     *
     * @param fluid     要排出的流体类型
     * @param maxAmount 最大排出量（droplets）
     * @param tx        事务上下文
     * @return 实际排出量
     */
    long drain(Fluid fluid, long maxAmount, BrickTransactionContext tx);

    /**
     * 排出任意流体
     *
     * @param maxAmount 最大排出量（droplets）
     * @param tx        事务上下文
     * @return 实际排出量
     */
    long drain(long maxAmount, BrickTransactionContext tx);

    static long dropletToBucket(long droplet) {
        return droplet / BUCKET;
    }

    static long bucketToDroplet(long b) {
        return b * BUCKET;
    }

    static long dropletToBottle(long droplet) {
        return droplet / BOTTLE;
    }

    static long bottleToDroplet(long bottle) {
        return bottle * BOTTLE;
    }

    static long dropletToIngot(long droplet) {
        return droplet / INGOT;
    }

    static long ingotToDroplet(long ingot) {
        return ingot * INGOT;
    }

    static long dropletToNugget(long droplet) {
        return droplet / NUGGET;
    }

    static long nuggetToDroplet(long nugget) {
        return nugget * NUGGET;
    }
}
