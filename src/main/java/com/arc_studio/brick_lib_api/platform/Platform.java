package com.arc_studio.brick_lib_api.platform;

import com.arc_studio.brick_lib_api.Constants;
import com.arc_studio.brick_lib_api.BrickLibAPI;
import com.arc_studio.brick_lib_api.core.PlatformInfo;
import com.arc_studio.brick_lib_api.core.SideExecutor;
import com.arc_studio.brick_lib_api.core.Version;
import com.arc_studio.brick_lib_api.core.VillagerTradeEntry;
import com.arc_studio.brick_lib_api.core.data.BrickLazyOptional;
import com.arc_studio.brick_lib_api.core.data.ResourceID;
import com.arc_studio.brick_lib_api.core.network.context.C2SNetworkContext;
import com.arc_studio.brick_lib_api.core.network.context.S2CNetworkContext;
import com.arc_studio.brick_lib_api.core.network.type.ICHandlePacket;
import com.arc_studio.brick_lib_api.core.network.type.ISHandlePacket;
import com.arc_studio.brick_lib_api.register.BrickRegistries;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.server.level.ServerPlayer;
//? if < 1.21.5 {
/*import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;*/
//? } else {
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.npc.villager.VillagerTrades;
//? }
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.LevelResource;
import org.apache.commons.lang3.concurrent.AtomicSafeInitializer;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.LazyInitializer;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;

//? if >= 1.21.5 {
import net.minecraft.core.registries.BuiltInRegistries;

//?}
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.nio.file.Path;
import java.util.*;

/**
 * Brick Lib使用的平台类，Mod作者<font color = "red">不建议</font>使用这个类
 * @author fho4565
 */
@ApiStatus.Internal
@SuppressWarnings("unchecked")
public class Platform {
    private static final Logger LOGGER = LoggerFactory.getLogger(Platform.class);
    //? if !fabric {
    private static final LevelResource SERVER_CONFIG = new LevelResource("serverconfig");
    //?}

    private static final AtomicSafeInitializer<Version> VERSION = new AtomicSafeInitializer<>() {
        @Override
        protected Version initialize() {
            MutableObject<Version> version = new MutableObject<>();
            SideExecutor.runSeparately(() -> version.setValue(Version.parse(Minecraft.getInstance().getLaunchedVersion())),
                () -> version.setValue(Version.parse(Constants.currentServer().getServerVersion())));
            return version.getValue();
        }
    };

    // ===========================
    //  委托方法 — 分发到各加载器
    // ===========================

    public static Path getConfigDirectory() {
        //? if fabric {
        /*return FabricPlatform.getConfigDirectory();

        *///?} else if forge {
        /*return ForgePlatform.getConfigDirectory();

        *///?} else if neoforge {
        return NeoForgePlatform.getConfigDirectory();


        //?}
    }

    public static Path versionPath() {
        //? if fabric {
        /*return FabricPlatform.versionPath();

        *///?} else if forge {
        /*return ForgePlatform.versionPath();
         *///?} else if neoforge {
        return NeoForgePlatform.versionPath();

        //?}
    }

    public static boolean isDev() {
        //? if fabric {
        /*return FabricPlatform.isDev();

        *///?} else if forge {
        /*return ForgePlatform.isDev();
         *///?} else if neoforge {
        return NeoForgePlatform.isDev();

        //?}
    }

    public static String[] launchArgs() {
        //? if fabric {
        /*return FabricPlatform.launchArgs();

        *///?} else {
        return ManagementFactory.getRuntimeMXBean().getInputArguments().toArray(new String[0]);
        //?}
    }

    public static PlatformInfo platform() {
        //? if fabric {
        /*return FabricPlatform.platform();

        *///?} else if forge {
        /*return ForgePlatform.platform();
         *///?} else if neoforge {
        return NeoForgePlatform.platform();

        //?}
    }

    public static boolean isClient() {
        //? if fabric {
        /*return FabricPlatform.isClient();

        *///?} else if forge {
        /*return ForgePlatform.isClient();
         *///?} else if neoforge {
        return NeoForgePlatform.isClient();

        //?}
    }

    public static boolean isServer() {
        //? if fabric {
        /*return FabricPlatform.isServer();

        *///?} else if forge {
        /*return ForgePlatform.isServer();
         *///?} else if neoforge {
        return NeoForgePlatform.isServer();

        //?}
    }

    public static <T extends S2CNetworkContext> void enqueueWork(T context, Runnable runnable) {
        //? if fabric {
        /*FabricPlatform.enqueueWork(context, runnable);

        *///?} else if forge {
        /*ForgePlatform.enqueueWork(context, runnable);
         *///?} else if neoforge {
        NeoForgePlatform.enqueueWork(context, runnable);

        //?}
    }

    public static <T extends C2SNetworkContext> void enqueueWork(T context, Runnable runnable) {
        //? if fabric {
        /*FabricPlatform.enqueueWork(context, runnable);

        *///?} else if forge {
        /*ForgePlatform.enqueueWork(context, runnable);
         *///?} else if neoforge {
        NeoForgePlatform.enqueueWork(context, runnable);

        //?}
    }

    public static void sendToPlayer(ICHandlePacket packet, Iterable<ServerPlayer> serverPlayers) {
        //? if fabric {
        /*FabricPlatform.sendToPlayer(packet, serverPlayers);

        *///?} else if forge {
        /*ForgePlatform.sendToPlayer(packet, serverPlayers);
         *///?} else if neoforge {
        NeoForgePlatform.sendToPlayer(packet, serverPlayers);

        //?}
    }

    public static void sendToAllPlayers(ICHandlePacket packet) {
        sendToPlayer(packet, Constants.currentServer().getPlayerList().getPlayers());
    }

    public static void sendToServer(ISHandlePacket packet) {
        //? if fabric {
        /*FabricPlatform.sendToServer(packet);

        *///?} else if forge {
        /*ForgePlatform.sendToServer(packet);
         *///?} else if neoforge {
        NeoForgePlatform.sendToServer(packet);

        //?}
    }

    public static Set<ResourceID> networkChannels(Connection connection, ConnectionProtocol protocol) {
        //? if fabric {
        /*return FabricPlatform.networkChannels(connection, protocol);

        *///?} else if forge {
        /*return ForgePlatform.networkChannels(connection, protocol);
         *///?} else if neoforge {
        return NeoForgePlatform.networkChannels(connection, protocol);

        //?}
    }

    // ===========================
    //  共享方法（无加载器条件）
    // ===========================

    public static Version gameVersion() {
        MutableObject<Version> version = new MutableObject<>();
        SideExecutor.runSeparately(() -> version.setValue(Version.parse(Minecraft.getInstance().getLaunchedVersion())),
                () -> version.setValue(Version.parse(Constants.currentServer().getServerVersion())));
        return version.getValue();
    }

    public static String gameVersionStr() {
        MutableObject<String> version = new MutableObject<>();
        SideExecutor.runSeparately(() -> version.setValue(Minecraft.getInstance().getLaunchedVersion()),
                () -> version.setValue(Constants.currentServer().getServerVersion()));
        return version.getValue();
    }

    public static boolean itemEqual(ItemStack first, ItemStack second, boolean compareDamageValue) {
        //? if fabric {
        /*return FabricPlatform.itemEqual(first, second, compareDamageValue);

        *///?} else {
        if (first.isEmpty()) {
            return second.isEmpty();
        } else {
            ItemStack i1 = first.copy();
            ItemStack i2 = second.copy();
            if (!compareDamageValue) {
                i1.setDamageValue(0);
                i2.setDamageValue(0);
            }
            //? if >= 1.20.6 {
            if (i1.isEmpty()) {
                return i2.isEmpty();
            } else {
                return !i2.isEmpty() && i1.getCount() == i2.getCount() && i1.getItem() == i2.getItem() &&
                        (Objects.equals(i1.getComponentsPatch(), i2.getComponentsPatch()));
            }

            //?} elif > 1.19.4 {
            /*return ItemStack.isSameItem(i1, i2);
            *///?} else {
            /*return ItemStack.isSameItemSameTags(i1, i2);

            *///?}
        }
        //?}
    }

    /**
     * 执行后置操作，包括：
     * <ul>
     *     <li>一些Brick Lib注册表的注册</li>
     *     <li>在fabric：
     *         <ul>
     *             <li>原版注册表注册</li>
     *             <li>网络包注册</li>
     *             <li>键位注册</li>
     *         </ul>
     *     </li>
     * </ul>
     * */
    public static <T> void brickFinalizeRegistry() {
        HashMap<Pair<VillagerProfession, Integer>, ArrayList<VillagerTrades.ItemListing>> map = new HashMap<>();
        for (VillagerTradeEntry entry : BrickRegistries.VILLAGER_TRADE) {
            //? if >= 1.21.5 {
            Pair<VillagerProfession, Integer> key = Pair.of(BuiltInRegistries.VILLAGER_PROFESSION.getValueOrThrow(entry.profession()), entry.level());

            //?} else {
            /*Pair<VillagerProfession, Integer> key = Pair.of(entry.profession(), entry.level());
            *///?}
            ArrayList<VillagerTrades.ItemListing> list = map.getOrDefault(key, new ArrayList<>());
            list.add(entry.trade());
            map.put(key, list);
        }
        map.forEach((pair, itemListings) -> registerVillagerOffers(pair.getKey(), pair.getValue(), itemListings));
        BrickRegistries.VILLAGER_TRADE.clean();
        HashMap<Integer, ArrayList<VillagerTrades.ItemListing>> map1 = new HashMap<>();
        for (VillagerTradeEntry entry : BrickRegistries.WANDERING_TRADE) {
            ArrayList<VillagerTrades.ItemListing> list = map1.getOrDefault(entry.level(), new ArrayList<>());
            list.add(entry.trade());
            map1.put(entry.level(), list);
        }
        map1.forEach(Platform::registerWanderingOffers);
        BrickRegistries.WANDERING_TRADE.clean();
        //? if fabric {
        /*FabricPlatform.brickFinalizeRegistryPost();

        *///?}
    }

    static void registerVillagerOffers(VillagerProfession profession, int level, List<VillagerTrades.ItemListing> trades) {
        //? if < 1.21.5 {
        /*Int2ObjectMap<VillagerTrades.ItemListing[]> map = VillagerTrades.TRADES.getOrDefault(profession, new Int2ObjectOpenHashMap<>());
        Optional<VillagerTrades.ItemListing[]> optional = Optional.ofNullable(map.get(level));
        if (optional.isPresent()) {
            ArrayList<VillagerTrades.ItemListing> list = new ArrayList<>(Arrays.asList(optional.get()));
            list.addAll(trades);
            map.put(level, list.toArray(new VillagerTrades.ItemListing[0]));
            VillagerTrades.TRADES.put(profession, map);
        }
        *///?} else {
        BuiltInRegistries.VILLAGER_PROFESSION.getResourceKey(profession).ifPresentOrElse(resourceKey -> {
            Int2ObjectMap<VillagerTrades.ItemListing[]> map = VillagerTrades.TRADES.getOrDefault(resourceKey, new Int2ObjectOpenHashMap<>());
            Optional<VillagerTrades.ItemListing[]> optional = Optional.ofNullable(map.get(level));
            if (optional.isPresent()) {
                ArrayList<VillagerTrades.ItemListing> list = new ArrayList<>(Arrays.asList(optional.get()));
                list.addAll(trades);
                map.put(level, list.toArray(new VillagerTrades.ItemListing[0]));
                VillagerTrades.TRADES.put(resourceKey, map);
            }
        }, () -> {
            BrickLibAPI.LOGGER.error("No profession " + profession.name().toString());
        });

        //?}
    }

    static void registerWanderingOffers(int level, List<VillagerTrades.ItemListing> trades) {
        //? if < 1.21.5 {
        /*Int2ObjectMap<VillagerTrades.ItemListing[]> map = VillagerTrades.WANDERING_TRADER_TRADES;
        Optional<VillagerTrades.ItemListing[]> optional = Optional.ofNullable(map.get(level));
        if (optional.isPresent()) {
            ArrayList<VillagerTrades.ItemListing> list = new ArrayList<>(Arrays.asList(optional.get()));
            list.addAll(trades);
            map.put(level, list.toArray(new VillagerTrades.ItemListing[0]));
            VillagerTrades.WANDERING_TRADER_TRADES.put(level, list.toArray(new VillagerTrades.ItemListing[0]));
        }
        *///?} else {
        List<Pair<VillagerTrades.ItemListing[], Integer>> map = VillagerTrades.WANDERING_TRADER_TRADES;
        Optional<Pair<VillagerTrades.ItemListing[], Integer>> optional = Optional.ofNullable(map.get(level));
        if (optional.isPresent()) {
            VillagerTrades.WANDERING_TRADER_TRADES.add(Pair.of(trades.toArray(new VillagerTrades.ItemListing[0]), level));
        }

        //?}
    }
}
