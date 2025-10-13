package com.arc_studio.brick_lib_api.core.network;


import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Pair;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
//? if > 1.20.4 {
/*import net.minecraft.core.component.DataComponentMap;
*///?}
//? if > 1.19.2 {
import net.minecraft.core.registries.BuiltInRegistries;
import org.joml.Vector3f;

//?} else {
/*import com.mojang.math.Vector3f;
*///?}
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;


import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 网络数据包内容处理器，封装了对 {@link FriendlyByteBuf} 的读写操作，
 * 提供类型安全的链式读写方法，用于模组网络通信中数据的序列化与反序列化。
 * @author fho4565
 */
public class PacketContent {
    protected FriendlyByteBuf friendlyByteBuf;

    public PacketContent() {
        this(new FriendlyByteBuf(Unpooled.buffer()));
    }

    public PacketContent(FriendlyByteBuf buf) {
        friendlyByteBuf = buf;
    }

    public PacketContent writeUTF(String string, int maxLength) {
        friendlyByteBuf.writeUtf(string.substring(0, Math.min(string.length(), maxLength)));
        return this;
    }

    public PacketContent writeBoolean(boolean bool) {
        friendlyByteBuf.writeBoolean(bool);
        return this;
    }

    public PacketContent writeInt(int i) {
        friendlyByteBuf.writeInt(i);
        return this;
    }

    public PacketContent writeLong(long l) {
        friendlyByteBuf.writeLong(l);
        return this;
    }

    public PacketContent writeDouble(double d) {
        friendlyByteBuf.writeDouble(d);
        return this;
    }

    public PacketContent writeFloat(float f) {
        friendlyByteBuf.writeFloat(f);
        return this;
    }

    public PacketContent writeShort(short s) {
        friendlyByteBuf.writeShort(s);
        return this;
    }

    public PacketContent writeByte(byte b) {
        friendlyByteBuf.writeByte(b);
        return this;
    }

    public PacketContent writeByteArray(byte[] bytes) {
        friendlyByteBuf.writeByteArray(bytes);
        return this;
    }

    public PacketContent writeItemStack(ItemStack itemStack) {
        //? if >= 1.20.6 {
        /*if (itemStack.isEmpty()) {
            friendlyByteBuf.writeBoolean(false);
        } else {
            friendlyByteBuf.writeBoolean(true);
            Item item = itemStack.getItem();
            int i = BuiltInRegistries.ITEM.getId(item);
            if (i == -1) {
                throw new IllegalArgumentException("Can't find id for '" + item + "' in map " + BuiltInRegistries.ITEM);
            } else {
                friendlyByteBuf.writeVarInt(i);
            }
            friendlyByteBuf.writeByte(itemStack.getCount());
            DataComponentMap.CODEC.encodeStart(NbtOps.INSTANCE, itemStack.getComponents()).result().ifPresent(new Consumer<Tag>() {
                @Override
                public void accept(Tag tag) {
                    friendlyByteBuf.writeNbt(tag);
                }
            });
        }
        *///?} else {
        friendlyByteBuf.writeItem(itemStack);
        //?}
        return this;
    }

    public PacketContent writeUTF(String string) {
        friendlyByteBuf.writeUtf(string);
        return this;
    }

    public PacketContent writePosition(BlockPos blockPos) {
        friendlyByteBuf.writeBlockPos(blockPos);
        return this;
    }

    /**
     * mojang在1.19.2+版本改用了"org.joml.Vector3f"类，而在1.19.2及更早使用了com.mojang.math.Vector3f类
     * <p color = "red">此处的Vector3f会随着版本改变而修改Vector3f类！</p>
     * <p>建议使用{@link PacketContent#writeVector3f2(float, float, float)}</p>
     * */
    @Deprecated(since = "1.19.2")
    public PacketContent writeVector3f(Vector3f vector3f) {
        //? if > 1.19.2 {
        friendlyByteBuf.writeVector3f(vector3f);
        //?} else {
        /*friendlyByteBuf.writeFloat(vector3f.x());
        friendlyByteBuf.writeFloat(vector3f.y());
        friendlyByteBuf.writeFloat(vector3f.z());
        *///?}
        return this;
    }

    public PacketContent writeVector3f2(float x,float y,float z){
        friendlyByteBuf.writeFloat(x);
        friendlyByteBuf.writeFloat(y);
        friendlyByteBuf.writeFloat(z);
        return this;
    }

    public PacketContent writeResourceLocation(ResourceLocation resourceLocation) {
        friendlyByteBuf.writeResourceLocation(resourceLocation);
        return this;
    }

    public PacketContent writeResourceKey(ResourceKey<?> resourceKey) {
        //? if > 1.18.2 {
        friendlyByteBuf.writeResourceKey(resourceKey);
        //?} else {
        /*friendlyByteBuf.writeResourceLocation(resourceKey.location());
        *///?}
        return this;
    }

    public PacketContent writeNBT(CompoundTag compoundTag) {
        friendlyByteBuf.writeNbt(compoundTag);
        return this;
    }

    public PacketContent writeGameProfile(GameProfile gameProfile) {
        //? if >= 1.20.6 {
        /*friendlyByteBuf.writeUUID(gameProfile.getId());
        friendlyByteBuf.writeUtf(gameProfile.getName());
        friendlyByteBuf.writeCollection(gameProfile.getProperties().values(), (buf, property) -> {
            buf.writeUtf(property.name());
            buf.writeUtf(property.value());
            if (property.hasSignature()) {
                buf.writeBoolean(true);
                if (property.signature() != null) {
                    buf.writeUtf(property.signature());
                }
            } else {
                buf.writeBoolean(false);
            }
        });
        *///?} elif > 1.18.2 {
        friendlyByteBuf.writeGameProfile(gameProfile);
        //?} else {
        /*friendlyByteBuf.writeUUID(gameProfile.getId());
        friendlyByteBuf.writeUtf(gameProfile.getName());
        friendlyByteBuf.writeCollection(gameProfile.getProperties().values(), (friendlyByteBuf, property) -> {
            friendlyByteBuf.writeUtf(property.getName());
            friendlyByteBuf.writeUtf(property.getValue());
            if (property.hasSignature()) {
                friendlyByteBuf.writeBoolean(true);
                friendlyByteBuf.writeUtf(property.getSignature());
            } else {
                friendlyByteBuf.writeBoolean(false);
            }
        });
        *///?}
        return this;
    }

    public PacketContent writeChunkPos(ChunkPos chunkPos) {
        friendlyByteBuf.writeChunkPos(chunkPos);
        return this;
    }

    public int readInt() {
        return friendlyByteBuf.readInt();
    }

    public long readLong() {
        return friendlyByteBuf.readLong();
    }

    public double readDouble() {
        return friendlyByteBuf.readDouble();
    }

    public float readFloat() {
        return friendlyByteBuf.readFloat();
    }

    public short readShort() {
        return friendlyByteBuf.readShort();
    }

    public byte readByte() {
        return friendlyByteBuf.readByte();
    }

    public byte[] readByteArray() {
        return friendlyByteBuf.readByteArray();
    }

    public byte[] readBytes(int length) {
        return friendlyByteBuf.readByteArray(length);
    }

    public ItemStack readItemStack() {
        //? if >= 1.20.6 {
        /*if (!friendlyByteBuf.readBoolean()) {
            return ItemStack.EMPTY;
        } else {
            int varInt = friendlyByteBuf.readVarInt();
            Item item = BuiltInRegistries.ITEM.byId(varInt);
            int i = friendlyByteBuf.readByte();
            ItemStack itemstack = new ItemStack(item, i);
            Optional<Pair<DataComponentMap, Tag>> result = DataComponentMap.CODEC.decode(NbtOps.INSTANCE, friendlyByteBuf.readNbt()).result();
            result.ifPresent(pair -> itemstack.applyComponents(pair.getFirst()));
            return itemstack;
        }
        *///?} else {
        return friendlyByteBuf.readItem();
        //?}
    }

    public String readUTF() {
        return friendlyByteBuf.readUtf();
    }

    public BlockPos readPosition() {
        return friendlyByteBuf.readBlockPos();
    }

    /**
     * mojang在1.19.2+版本改用了"org.joml.Vector3f"类，而在1.19.2及更早使用了com.mojang.math.Vector3f类
     * <p color = "red">此处的Vector3f会随着版本改变而修改Vector3f类！</p>
     * <p>建议使用{@link PacketContent#readVector3f2()}</p>
     * */
    @Deprecated(since = "1.19.2")
    public Vector3f readVector3f() {
        //? if > 1.19.2 {
        return friendlyByteBuf.readVector3f();
        //?} else {
        /*return new Vector3f(friendlyByteBuf.readFloat(), friendlyByteBuf.readFloat(), friendlyByteBuf.readFloat());
        *///?}
    }

    public float[] readVector3f2(){
        return new float[]{friendlyByteBuf.readFloat(), friendlyByteBuf.readFloat(), friendlyByteBuf.readFloat()};
    }

    public ResourceLocation readResourceLocation() {
        return friendlyByteBuf.readResourceLocation();
    }

    public <T> ResourceKey<T> readResourceKey(ResourceKey<? extends Registry<T>> registryKey) {
        //? if > 1.18.2 {
        return friendlyByteBuf.readResourceKey(registryKey);
        //?} else {
        /*ResourceLocation resourceLocation = this.readResourceLocation();
        return ResourceKey.create(registryKey, resourceLocation);
        *///?}
    }

    public CompoundTag readNBT() {
        return friendlyByteBuf.readNbt();
    }

    public GameProfile readGameProfile() {
        //? if >= 1.20.6 {
        /*UUID uuid = friendlyByteBuf.readUUID();
        String s0 = friendlyByteBuf.readUtf(16);
        GameProfile gameprofile = new GameProfile(uuid, s0);
        friendlyByteBuf.readWithCount((buf) -> {
            Property property;
            String s = buf.readUtf();
            String s1 = buf.readUtf();
            if (buf.readBoolean()) {
                String s2 = buf.readUtf();
                property = new Property(s, s1, s2);
            } else {
                property = new Property(s, s1);
            }
            gameprofile.getProperties().put(property.name(), property);
        });
        return gameprofile;
        *///?} elif > 1.18.2 {
        return friendlyByteBuf.readGameProfile();
        //?} else {
        /*UUID uUID = friendlyByteBuf.readUUID();
        String string = friendlyByteBuf.readUtf(16);
        GameProfile gameProfile = new GameProfile(uUID, string);
        PropertyMap propertyMap = new PropertyMap();
        friendlyByteBuf.readWithCount(friendlyByteBuf -> {
            String string1 = friendlyByteBuf.readUtf();
            String string2 = friendlyByteBuf.readUtf();
            Property property;
            if (this.readBoolean()) {
                String string3 = friendlyByteBuf.readUtf();
                property = new Property(string1, string2, string3);
            } else {
                property = new Property(string1, string2);
            }
            propertyMap.put(property.getName(), property);
        });
        gameProfile.getProperties().putAll(propertyMap);
        return gameProfile;
        *///?}
    }

    public ChunkPos readChunkPos() {
        return friendlyByteBuf.readChunkPos();
    }

    public boolean readBoolean() {
        return friendlyByteBuf.readBoolean();
    }

    public String readUTF(int maxLength) {
        return friendlyByteBuf.readUtf(maxLength);
    }

    public FriendlyByteBuf friendlyByteBuf() {
        return new FriendlyByteBuf(this.friendlyByteBuf.retainedDuplicate());
    }
}


