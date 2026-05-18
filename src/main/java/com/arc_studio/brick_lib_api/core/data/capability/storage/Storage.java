package com.arc_studio.brick_lib_api.core.data.capability.storage;

import com.arc_studio.brick_lib_api.core.data.capability.core.Capability;
import com.arc_studio.brick_lib_api.core.data.capability.core.OperationType;
import com.arc_studio.brick_lib_api.core.data.capability.transaction.TransactionContext;

import java.util.Collections;
import java.util.Iterator;

/**
 * 泛型存储接口 — 类似 Fabric Storage，但增加能力绑定
 * <p>
 * 所有修改操作（insert/extract）必须在事务上下文中调用。
 * </p>
 *
 * @param <T> 资源类型
 */
public interface Storage<T> extends Iterable<StorageView<T>> {

    /**
     * 获取此存储对应的 Capability
     */
    Capability<T> getCapability();

    /**
     * 插入资源（事务内调用）
     *
     * @param resource  要插入的资源
     * @param maxAmount 最大插入数量
     * @param tx        事务上下文
     * @return 实际插入数量
     */
    long insert(T resource, long maxAmount, TransactionContext tx);

    /**
     * 提取资源
     *
     * @param resource  要提取的资源
     * @param maxAmount 最大提取数量
     * @param tx        事务上下文
     * @return 实际提取数量
     */
    long extract(T resource, long maxAmount, TransactionContext tx);

    /**
     * 检查操作支持（快速路径，无需事务）
     */
    boolean supports(OperationType op);

    /**
     * 获取版本号（可选实现，默认返回 0）
     * <p>可用于变更检测优化。</p>
     */
    default long getVersion() {
        return 0;
    }

    /**
     * 创建空存储工厂
     */
    static <T> Storage<T> empty(Capability<T> cap) {
        return new Storage<>() {
            @Override
            public Capability<T> getCapability() {
                return cap;
            }

            @Override
            public long insert(T resource, long maxAmount, TransactionContext tx) {
                return 0;
            }

            @Override
            public long extract(T resource, long maxAmount, TransactionContext tx) {
                return 0;
            }

            @Override
            public boolean supports(OperationType op) {
                return false;
            }

            @Override
            public Iterator<StorageView<T>> iterator() {
                return Collections.emptyIterator();
            }
        };
    }
}

