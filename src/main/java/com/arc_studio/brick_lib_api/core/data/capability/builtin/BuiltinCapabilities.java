package com.arc_studio.brick_lib_api.core.data.capability.builtin;

import com.arc_studio.brick_lib_api.core.data.capability.core.BrickCapability;

/**
 * 预设内置能力常量
 * <p>
 * 提供物品、流体、能量三种最常用的能力标识符。
 * 模组开发者可以直接使用这些常量查询和提供能力。
 * </p>
 *
 * <pre>{@code
 * // 查询能量能力
 * blockEntity.getCapability(BuiltinCapabilities.ENERGY, Direction.UP)
 *     .ifPresent(energy -> {
 *         long stored = energy.getEnergyStored();
 *     });
 *
 * // 查询物品能力
 * blockEntity.getCapability(BuiltinCapabilities.ITEM_HANDLER, null)
 *     .ifPresent(handler -> {
 *         int slots = handler.getSlots();
 *     });
 *
 * // 查询流体能力
 * blockEntity.getCapability(BuiltinCapabilities.FLUID_HANDLER, Direction.NORTH)
 *     .ifPresent(fluid -> {
 *         int tanks = fluid.getTanks();
 *     });
 * }</pre>
 */
public final class BuiltinCapabilities {

    private BuiltinCapabilities() {
    }

    /**
     * 物品处理能力
     * <p>
     * 用于物品的插入、提取、查询操作（类似 Forge IItemStorage）。
     * </p>
     */
    public static final BrickCapability<IItemStorage> ITEM_HANDLER = BrickCapability.of(IItemStorage.class);

    /**
     * 流体处理能力
     * <p>
     * 用于流体的填充、排出、查询操作（类似 Forge IFluidStorage）。
     * 数量单位为 droplets（1 bucket = 81000）。
     * </p>
     */
    public static final BrickCapability<IFluidStorage> FLUID_HANDLER = BrickCapability.of(IFluidStorage.class);

    /**
     * 能量存储能力
     * <p>
     * 用于能量的接收、提取、查询操作（类似 Forge IEnergyStorage / FE）。
     * </p>
     */
    public static final BrickCapability<IEnergyStorage> ENERGY = BrickCapability.of(IEnergyStorage.class);
}

