package com.arc_studio.brick_lib_api.core.data;


import net.minecraft.nbt.CompoundTag;

//? if >= 1.20.6 {
/*import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.nbt.NbtOps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.Nullable;
import net.minecraft.core.component.DataComponentType;
import java.util.function.Function;

*///?}
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import java.util.function.Consumer;

/**
 * 物品的额外数据
 * */
@ApiStatus.Experimental
public class ItemAdditionalData extends BaseAdditionalData {
    public static final String KEY_DATA = "brick_data";
    //? if >= 1.20.6 {
    /*public static final AdditionalDataType DATA_COMPONENT_TYPE = new AdditionalDataType(new CompoundTag());
    *///?}

    public ItemAdditionalData(CompoundTag tag) {
        this.data = tag;
    }

    public static CompoundTag getData(ItemStack itemStack) {
        //? if >= 1.20.6 {
        /*return itemStack.getOrDefault(DATA_COMPONENT_TYPE, new ItemAdditionalData(new CompoundTag())).data;
        *///?} else {
        return itemStack.getOrCreateTag();
        //?}
    }

    public static void modifyData(ItemStack itemStack, Consumer<CompoundTag> consumer) {
        CompoundTag tag = getData(itemStack);
        consumer.accept(tag);
        //? if >= 1.20.6 {
        /*itemStack.getOrDefault(DATA_COMPONENT_TYPE, new ItemAdditionalData(new CompoundTag())).data = tag;
        *///?} else {
        itemStack.setTag(tag);
        //?}
    }

    //? if >= 1.20.6 {
    /*public static class AdditionalDataType implements DataComponentType<ItemAdditionalData> {
        public static final Codec<ItemAdditionalData> CODEC = RecordCodecBuilder.create(instance -> // 给定一个实例
                instance.group(
                        CompoundTag.CODEC.fieldOf("s")
                                .forGetter(itemAdditionalData -> itemAdditionalData.data)
                ).apply(instance, ItemAdditionalData::new)
        );
        CompoundTag tag;

        public AdditionalDataType(CompoundTag tag) {
            this.tag = tag;
        }

        public CompoundTag getTag() {
            return tag;
        }

        public void setTag(CompoundTag tag) {
            this.tag = tag;
        }

        @Override
        public @Nullable Codec<ItemAdditionalData> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, ItemAdditionalData> streamCodec() {
            return new StreamCodec<>() {
                @Override
                public ItemAdditionalData decode(RegistryFriendlyByteBuf byteBuf) {
                    return CODEC.decode(NbtOps.INSTANCE, byteBuf.readNbt()).getOrThrow().getFirst();
                }

                @Override
                public void encode(RegistryFriendlyByteBuf byteBuf, ItemAdditionalData data) {
                    byteBuf.writeNbt(CODEC.encodeStart(NbtOps.INSTANCE, data).getOrThrow());
                }
            };
        }
    }
    *///?}
}
