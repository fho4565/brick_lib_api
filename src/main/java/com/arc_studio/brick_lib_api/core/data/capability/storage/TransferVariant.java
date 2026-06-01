package com.arc_studio.brick_lib_api.core.data.capability.storage;

import com.arc_studio.brick_lib_api.core.data.capability.core.BrickCapability;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * 不可变变体 — 类似 Fabric TransferVariant
 * <p>
 * 表示「对象 + 可选 NBT」的不可变组合。
 * 必须正确实现 equals/hashCode，禁止使用 == 比较。
 * </p>
 *
 * @param <O> 资源对象类型
 */
public final class TransferVariant<O> {

    private static final TransferVariant<?> BLANK = new TransferVariant<>(null, null);

    @Nullable
    private final O object;
    @Nullable
    private final CompoundTag nbt;
    private final int hashCodeCache;

    private TransferVariant(@Nullable O object, @Nullable CompoundTag nbt) {
        this.object = object;
        this.nbt = nbt != null ? nbt.copy() : null;
        this.hashCodeCache = Objects.hash(object, this.nbt);
    }

    /**
     * 创建空变体
     */
    @SuppressWarnings("unchecked")
    public static <O> TransferVariant<O> blank() {
        return (TransferVariant<O>) BLANK;
    }

    /**
     * 从能力创建空变体
     */
    public static <O> TransferVariant<O> blank(BrickCapability<O> cap) {
        return blank();
    }

    /**
     * 创建不带 NBT 的变体
     */
    public static <O> TransferVariant<O> of(O object) {
        Objects.requireNonNull(object, "Object must not be null. Use blank() for empty variants.");
        return new TransferVariant<>(object, null);
    }

    /**
     * 创建带 NBT 的变体
     */
    public static <O> TransferVariant<O> of(O object, @Nullable CompoundTag nbt) {
        Objects.requireNonNull(object, "Object must not be null. Use blank() for empty variants.");
        return new TransferVariant<>(object, nbt);
    }

    /**
     * 是否为空变体
     */
    public boolean isBlank() {
        return object == null;
    }

    /**
     * 获取资源对象
     *
     * @throws IllegalStateException 如果变体为空
     */
    public O getObject() {
        if (object == null) {
            throw new IllegalStateException("Cannot get object from a blank variant.");
        }
        return object;
    }

    /**
     * 获取附加 NBT 数据（可能为 null）
     */
    @Nullable
    public CompoundTag getNbt() {
        return nbt;
    }

    /**
     * 获取 NBT 数据的副本（永不为 null，空时返回空 CompoundTag）
     */
    public CompoundTag copyNbt() {
        return nbt != null ? nbt.copy() : new CompoundTag();
    }

    /**
     * 序列化为 NBT
     */
    public CompoundTag toNbt() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("blank", isBlank());
        if (nbt != null) {
            tag.put("nbt", nbt.copy());
        }
        // 注意：实际的对象序列化需要由具体的 BrickCapability 提供序列化器
        return tag;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof TransferVariant<?> other)) return false;
        return Objects.equals(object, other.object) && Objects.equals(nbt, other.nbt);
    }

    @Override
    public int hashCode() {
        return hashCodeCache;
    }

    @Override
    public String toString() {
        if (isBlank()) return "TransferVariant{BLANK}";
        return "TransferVariant{object=" + object + ", nbt=" + nbt + "}";
    }
}

