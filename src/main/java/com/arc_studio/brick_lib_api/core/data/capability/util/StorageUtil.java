package com.arc_studio.brick_lib_api.core.data.capability.util;

import com.arc_studio.brick_lib_api.core.data.capability.storage.Storage;
import com.arc_studio.brick_lib_api.core.data.capability.storage.StorageView;
import com.arc_studio.brick_lib_api.core.data.capability.storage.TransferVariant;
import com.arc_studio.brick_lib_api.core.data.capability.transaction.Transaction;
import com.arc_studio.brick_lib_api.core.data.capability.transaction.TransactionContext;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * 存储工具类 — 融合 Fabric StorageUtil
 * <p>
 * 提供事务安全的资源移动、模拟插入、资源查找等常用操作。
 * </p>
 */
public final class StorageUtil {

    private StorageUtil() {
    }

    /**
     * 在两个存储之间移动资源（事务安全）
     *
     * @param from      源存储
     * @param to        目标存储
     * @param filter    资源过滤器
     * @param maxAmount 最大移动数量
     * @param tx        事务上下文
     * @return 实际移动总量
     */
    public static <T> long move(
            Storage<T> from,
            Storage<T> to,
            Predicate<TransferVariant<T>> filter,
            long maxAmount,
            TransactionContext tx
    ) {
        Preconditions.notNull(from, "Source storage must not be null");
        Preconditions.notNull(to, "Target storage must not be null");
        Preconditions.inTransaction(tx);
        Preconditions.notNegative(maxAmount);

        long totalMoved = 0;

        for (StorageView<T> view : from) {
            if (totalMoved >= maxAmount) break;
            if (view.isResourceBlank()) continue;

            TransferVariant<T> resource = view.getResource();
            if (!filter.test(resource)) continue;

            long toMove = Math.min(maxAmount - totalMoved, view.getAmount());

            // 使用嵌套事务保证原子性
            try (Transaction nested = Transaction.openNested(tx)) {
                long extracted = view.extract(toMove, nested);
                if (extracted > 0) {
                    long inserted = to.insert(resource.getObject(), extracted, nested);
                    if (inserted > 0) {
                        // 如果没有完全插入，只提取实际插入的量
                        if (inserted < extracted) {
                            // 回滚并重新尝试精确数量
                            nested.abort();
                            try (Transaction retry = Transaction.openNested(tx)) {
                                long reExtracted = view.extract(inserted, retry);
                                long reInserted = to.insert(resource.getObject(), reExtracted, retry);
                                if (reInserted == reExtracted) {
                                    retry.commit();
                                    totalMoved += reInserted;
                                }
                            }
                            continue;
                        }
                        nested.commit();
                        totalMoved += inserted;
                    }
                }
            }
        }

        return totalMoved;
    }

    /**
     * 模拟插入（不影响实际状态）
     *
     * @param storage   目标存储
     * @param resource  资源变体
     * @param maxAmount 最大插入数量
     * @return 可以插入的数量
     */
    public static <T> long simulateInsert(
            Storage<T> storage,
            TransferVariant<T> resource,
            long maxAmount
    ) {
        Preconditions.notNull(storage, "Storage must not be null");
        Preconditions.notBlank(resource);
        Preconditions.notNegative(maxAmount);

        try (Transaction tx = Transaction.openOuter()) {
            // 不提交，自动回滚
            return storage.insert(resource.getObject(), maxAmount, tx);
        }
    }

    /**
     * 查找存储中的任意可提取资源
     *
     * @param storage 存储
     * @param filter  资源过滤器
     * @return 第一个匹配的可提取资源变体
     */
    public static <T> Optional<TransferVariant<T>> findExtractableResource(
            Storage<T> storage,
            Predicate<TransferVariant<T>> filter
    ) {
        for (StorageView<T> view : storage) {
            if (!view.isResourceBlank()) {
                TransferVariant<T> resource = view.getResource();
                if (filter.test(resource)) {
                    return Optional.of(resource);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * 批量传输（多资源类型）
     *
     * @param from             源存储
     * @param to               目标存储
     * @param maxAmountPerType 每种资源类型的最大传输量
     * @param tx               事务上下文
     * @return 每种资源变体的实际传输量
     */
    public static <T> Map<TransferVariant<T>, Long> moveAll(
            Storage<T> from,
            Storage<T> to,
            long maxAmountPerType,
            TransactionContext tx
    ) {
        Preconditions.inTransaction(tx);
        Map<TransferVariant<T>, Long> result = new LinkedHashMap<>();

        for (StorageView<T> view : from) {
            if (view.isResourceBlank()) continue;

            TransferVariant<T> resource = view.getResource();
            long alreadyMoved = result.getOrDefault(resource, 0L);
            long remaining = maxAmountPerType - alreadyMoved;

            if (remaining <= 0) continue;

            long toMove = Math.min(remaining, view.getAmount());
            try (Transaction nested = Transaction.openNested(tx)) {
                long extracted = view.extract(toMove, nested);
                if (extracted > 0) {
                    long inserted = to.insert(resource.getObject(), extracted, nested);
                    if (inserted > 0) {
                        nested.commit();
                        result.merge(resource, inserted, Long::sum);
                    }
                }
            }
        }

        return result;
    }
}

