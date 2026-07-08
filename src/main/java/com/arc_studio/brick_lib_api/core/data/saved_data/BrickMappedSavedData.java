package com.arc_studio.brick_lib_api.core.data.saved_data;

import com.arc_studio.brick_lib_api.core.data.ResourceID;
import com.arc_studio.brick_lib_api.register.BrickRegistries;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
//? if >1.20.1 {
/*import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
*///? }
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings({"unchecked", "rawtypes", "deprecation", "removal"})
public class BrickMappedSavedData<K,V> extends BrickSavedData {
    private static final String DATA_NAME = "brick_mapped_data";

    //? if >= 1.21.5 {
    /*private static final com.mojang.serialization.Codec<BrickMappedSavedData> CODEC =
        CompoundTag.CODEC.xmap(BrickMappedSavedData::new, data -> data.saveData(new CompoundTag()));
    private static final net.minecraft.world.level.saveddata.SavedDataType<BrickMappedSavedData> TYPE =
        new net.minecraft.world.level.saveddata.SavedDataType<>(
            DATA_NAME,
            BrickMappedSavedData::new,
            CODEC,
            null
        );

    *///?}

    private static final Map<Codec<?>, SavedDataCodec<?>> CODEC_CACHE = new ConcurrentHashMap<>();
    private static final Map<ResourceID, SavedDataCodec<?>> CODEC_ID_CACHE = new ConcurrentHashMap<>();
    private static final Map<Class<?>, SavedDataCodec<?>> VALUE_CLASS_CODEC_CACHE = new ConcurrentHashMap<>();

    protected final Map<K, StoredEntry<K,V>> storageMap = new ConcurrentHashMap<>();

    protected static final class StoredEntry<K,V> {
        protected final Codec<K> keyCodec;
        protected final ResourceID keyCodecId;
        protected final V value;
        protected final Codec<V> valueCodec;
        protected final ResourceID valueCodecId;

        protected StoredEntry(Codec<K> keyCodec, ResourceID keyCodecId, V value, Codec<V> valueCodec, ResourceID valueCodecId) {
            this.keyCodec = keyCodec;
            this.keyCodecId = keyCodecId;
            this.value = value;
            this.valueCodec = valueCodec;
            this.valueCodecId = valueCodecId;
        }
    }

    public BrickMappedSavedData() {
    }

    @SuppressWarnings("unchecked")
    public BrickMappedSavedData(CompoundTag compoundTag) {
        //? if >= 1.21.5 {
        /*ListTag tag = compoundTag.getList("entries").orElse(new ListTag());
        loadEntryList(tag);
        *///?} else {
        ListTag tag = compoundTag.getList("entries", Tag.TAG_COMPOUND);
        loadEntryList(tag);

        //? }
    }

    @SuppressWarnings("unchecked")
    protected void loadEntryList(ListTag tag) {
        for (int i = 0; i < tag.size(); i++) {
            //? if >= 1.21.5 {
            /*CompoundTag it = tag.getCompound(i).orElseThrow();
            CompoundTag k = it.getCompound("k").orElseThrow();
            CompoundTag v = it.getCompound("v").orElseThrow();
            SavedDataCodec<K> kSavedDataCodec = (SavedDataCodec<K>) getSavedDataCodec(
                ResourceID.tryParse(k.getString("c").orElseThrow()));
            SavedDataCodec<V> vSavedDataCodec = (SavedDataCodec<V>) getSavedDataCodec(
                ResourceID.tryParse(v.getString("c").orElseThrow()));
            Codec<K> kCodec = kSavedDataCodec.codec();
            Codec<V> vCodec = vSavedDataCodec.codec();
            K key = kCodec.decode(NbtOps.INSTANCE, k.getCompound("v").orElseThrow())
                    .getOrThrow()
                .getFirst();
            V value = vCodec.decode(NbtOps.INSTANCE,v.getCompound("v").orElseThrow()).getOrThrow().getFirst();
            *///?} else {
            CompoundTag it = tag.getCompound(i);
            CompoundTag k = it.getCompound("k");
            CompoundTag v = it.getCompound("v");
            SavedDataCodec<K> kSavedDataCodec = (SavedDataCodec<K>) getSavedDataCodec(ResourceID.tryParse(k.getString("c")));
            SavedDataCodec<V> vSavedDataCodec = (SavedDataCodec<V>) getSavedDataCodec(ResourceID.tryParse(v.getString("c")));
            Codec<K> kCodec = kSavedDataCodec.codec();
            Codec<V> vCodec = vSavedDataCodec.codec();
            K key = kCodec.decode(NbtOps.INSTANCE, k.getCompound("v"))
                    //? if >= 1.20.6 {
                    /*.getOrThrow()

                    *///?} else {
                    .get().orThrow()
                    //?}
                    .getFirst();
            V value = vCodec.decode(NbtOps.INSTANCE,v.getCompound("v"))
                    //? if >= 1.20.6 {
                    /*.getOrThrow()

                    *///? } else {
                    .get().orThrow()
                    //? }
                    .getFirst();
            //?}
            StoredEntry<K, V> loaded = new StoredEntry<>(
                kCodec,
                kSavedDataCodec.resourceID(),
                value,
                vCodec,
                vSavedDataCodec.resourceID()
            );
            StoredEntry<K, V> previous = storageMap.put(key, loaded);
            onEntryLoaded(key, previous, loaded);
        }
    }

    @Override
    public CompoundTag saveData(CompoundTag tag) {
        ListTag list = new ListTag();
        storageMap.forEach((key, entry) -> {
            list.add(saveEntry(key, entry));
        });
        tag.put("entries", list);
        return tag;
    }

    protected CompoundTag saveEntry(K key, StoredEntry<K, V> entry) {
        CompoundTag compoundTag = new CompoundTag();
        CompoundTag keyTag = new CompoundTag();
        keyTag.putString("c", entry.keyCodecId.toString());
        keyTag.put("v",entry.keyCodec.encodeStart(NbtOps.INSTANCE,key)
                //? if >= 1.20.6 {
                /*.getOrThrow()
                *///? } else {
                .get().orThrow()
            //? }
        );
        compoundTag.put("k", keyTag);

        CompoundTag valueTag = new CompoundTag();
        valueTag.putString("c", entry.valueCodecId.toString());
        valueTag.put("v",entry.valueCodec.encodeStart(NbtOps.INSTANCE,entry.value)
                //? if >= 1.20.6 {
                /*.getOrThrow()
                *///? } else {
                .get().orThrow()
            //? }
        );
        compoundTag.put("v", valueTag);
        return compoundTag;
    }

    @SuppressWarnings("unchecked")
    public V put(V value) {
        SavedDataCodec<V> codec = findSavedDataCodec(value);
        return put((K) value, (Codec<K>) codec.codec(), value, codec.codec());
    }

    @SuppressWarnings("unchecked")
    public V put(V value, Codec<V> codec) {
        return put((K) value, (Codec<K>) codec, value, codec);
    }

    public V put(K key, V value) {
        return put(key, findSavedDataCodec(key).codec(), value, findSavedDataCodec(value).codec());
    }

    public V put(K key, Codec<K> keyCodec, V value) {
        return put(key, keyCodec, value, findSavedDataCodec(value).codec());
    }

    public V put(K key, V value, Codec<V> valueCodec) {
        return put(key, findSavedDataCodec(key).codec(), value, valueCodec);
    }

    public V put(K key, Codec<K> keyCodec, V value, Codec<V> valueCodec) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(keyCodec, "keyCodec");
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(valueCodec, "valueCodec");
        SavedDataCodec<K> keySavedDataCodec = getSavedDataCodec(keyCodec);
        SavedDataCodec<V> valueSavedDataCodec = getSavedDataCodec(valueCodec);
        StoredEntry<K, V> current = new StoredEntry<>(
            keyCodec,
            keySavedDataCodec.resourceID(),
            value,
            valueCodec,
            valueSavedDataCodec.resourceID()
        );
        StoredEntry<K, V> previous = storageMap.put(key, current);
        onEntryPut(key, previous, current);
        setDirty();
        return previous == null ? null : previous.value;
    }

    public V put(Pair<K, Codec<K>> key, Pair<V, Codec<V>> value) {
        return put(key.getKey(), key.getValue(), value.getKey(), value.getValue());
    }

    public Optional<V> get(K key) {
        StoredEntry<K, V> entry = storageMap.get(key);
        return entry == null ? Optional.empty() : Optional.of(entry.value);
    }

    public Optional<V> get(K key, Codec<K> codec) {
        return Optional.ofNullable(storageMap.get(key))
            .filter(entry -> entry.keyCodec.equals(codec))
            .map(entry -> entry.value);
    }

    @Nullable
    public V getOrNull(K key) {
        StoredEntry<K, V> entry = storageMap.get(key);
        return entry == null ? null : entry.value;
    }

    public V getOrDefault(K key, V defaultValue) {
        StoredEntry<K, V> entry = storageMap.get(key);
        return entry == null ? defaultValue : entry.value;
    }

    public Optional<Codec<V>> getValueCodec(K key) {
        StoredEntry<K, V> entry = storageMap.get(key);
        return entry == null ? Optional.empty() : Optional.of(entry.valueCodec);
    }

    public Optional<Codec<K>> getKeyCodec(K key) {
        StoredEntry<K, V> entry = storageMap.get(key);
        return entry == null ? Optional.empty() : Optional.of(entry.keyCodec);
    }

    public boolean containsKey(K key) {
        return storageMap.containsKey(key);
    }

    public boolean containsKey(K key, Codec<K> codec) {
        StoredEntry<K, V> entry = storageMap.get(key);
        return entry != null && entry.keyCodec.equals(codec);
    }

    public boolean containsValue(V value) {
        return storageMap.values().stream().anyMatch(entry -> Objects.equals(entry.value, value));
    }

    public Optional<V> remove(K key) {
        StoredEntry<K, V> removed = storageMap.remove(key);
        if (removed != null) {
            onEntryRemoved(key, removed);
            setDirty();
        }
        return Optional.ofNullable(removed).map(entry -> entry.value);
    }

    public Optional<V> remove(K key, Codec<K> codec) {
        StoredEntry<K, V> entry = storageMap.get(key);
        if (entry == null || !entry.keyCodec.equals(codec)) {
            return Optional.empty();
        }
        boolean removed = storageMap.remove(key, entry);
        if (removed) {
            onEntryRemoved(key, entry);
            setDirty();
        }
        return removed ? Optional.of(entry.value) : Optional.empty();
    }

    public void clear() {
        if (!storageMap.isEmpty()) {
            storageMap.clear();
            onCleared();
            setDirty();
        }
    }

    public int size() {
        return storageMap.size();
    }

    public boolean isEmpty() {
        return storageMap.isEmpty();
    }

    public Map<Pair<K, Codec<K>>, Pair<V, Codec<V>>> asMap() {
        Map<Pair<K, Codec<K>>, Pair<V, Codec<V>>> snapshot = new HashMap<>(storageMap.size());
        storageMap.forEach((key, entry) ->
            snapshot.put(Pair.of(key, entry.keyCodec), Pair.of(entry.value, entry.valueCodec))
        );
        return Collections.unmodifiableMap(snapshot);
    }

    protected <T> SavedDataCodec<T> findSavedDataCodec(T value) {
        Objects.requireNonNull(value, "value");
        @SuppressWarnings("unchecked")
        SavedDataCodec<T> cachedByClass = (SavedDataCodec<T>) VALUE_CLASS_CODEC_CACHE.get(value.getClass());
        if (cachedByClass != null && canEncode(cachedByClass.codec(), value)) {
            return cachedByClass;
        }

        for (SavedDataCodec<?> savedDataCodec : BrickRegistries.SAVED_DATA_CODEC) {
            @SuppressWarnings("unchecked")
            Codec<T> codec = (Codec<T>) savedDataCodec.codec();
            if (canEncode(codec, value)) {
                @SuppressWarnings("unchecked")
                SavedDataCodec<T> typedCodec = (SavedDataCodec<T>) savedDataCodec;
                CODEC_CACHE.put(codec, savedDataCodec);
                CODEC_ID_CACHE.put(savedDataCodec.resourceID(), savedDataCodec);
                VALUE_CLASS_CODEC_CACHE.put(value.getClass(), savedDataCodec);
                return typedCodec;
            }
        }
        throw new IllegalArgumentException("No registered SavedDataCodec can encode value: " + value);
    }

    private static <T> boolean canEncode(Codec<T> codec, T value) {
        try {
            codec.encodeStart(NbtOps.INSTANCE, value)
                //? if >= 1.20.6 {
                /*.getOrThrow();
                *///? } else {
                .get().orThrow();
                //? }
            return true;
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    protected <T> SavedDataCodec<T> getSavedDataCodec(Codec<T> codec) {
        SavedDataCodec<?> cached = CODEC_CACHE.get(codec);
        if (cached != null) {
            return (SavedDataCodec<T>) cached;
        }
        for (SavedDataCodec<?> savedDataCodec : BrickRegistries.SAVED_DATA_CODEC) {
            if (savedDataCodec.codec().equals(codec)) {
                CODEC_CACHE.put(codec, savedDataCodec);
                CODEC_ID_CACHE.put(savedDataCodec.resourceID(), savedDataCodec);
                return (SavedDataCodec<T>) savedDataCodec;
            }
        }
        throw new IllegalArgumentException("Codec is not registered in SAVED_DATA_CODEC: " + codec);
    }

    protected SavedDataCodec<?> getSavedDataCodec(@Nullable ResourceID resourceID) {
        if (resourceID == null) {
            throw new IllegalArgumentException("SavedDataCodec is not registered: null");
        }
        SavedDataCodec<?> cached = CODEC_ID_CACHE.get(resourceID);
        if (cached != null) {
            return cached;
        }
        SavedDataCodec<?> savedDataCodec = BrickRegistries.SAVED_DATA_CODEC.get(resourceID);
        if (savedDataCodec == null) {
            throw new IllegalArgumentException("SavedDataCodec is not registered: " + resourceID);
        }
        CODEC_ID_CACHE.put(resourceID, savedDataCodec);
        CODEC_CACHE.put(savedDataCodec.codec(), savedDataCodec);
        return savedDataCodec;
    }

    protected void onEntryLoaded(K key, @Nullable StoredEntry<K, V> previous, StoredEntry<K, V> loaded) {
    }

    protected void onEntryPut(K key, @Nullable StoredEntry<K, V> previous, StoredEntry<K, V> current) {
    }

    protected void onEntryRemoved(K key, StoredEntry<K, V> removed) {
    }

    protected void onCleared() {
    }

    @Override
    public String dataName() {
        return DATA_NAME;
    }

    public<T extends BrickMappedSavedData> T get(ServerLevel level, Function<CompoundTag, T> loadFunction, Supplier<T> createFunction) {
        //? if >= 1.21.5 {
        /*return (T) level.getDataStorage().computeIfAbsent(TYPE);

        *///?} else if >= 1.20.6 {
        /*return level.getDataStorage().computeIfAbsent(
            new SavedData.Factory<>(createFunction, (compoundTag, provider) ->
                loadFunction.apply(compoundTag), DataFixTypes.CHUNK),
            dataName()
        );

        *///?} else if >= 1.20.2 {
        /*return level.getDataStorage().computeIfAbsent(
            new SavedData.Factory<>(
                createFunction,loadFunction, DataFixTypes.CHUNK),
            dataName()
        );

        *///?} else {
        return level.getDataStorage().computeIfAbsent(loadFunction, createFunction, dataName());
        //?}
    }
}
