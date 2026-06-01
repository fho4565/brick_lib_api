# Brick Lib API

[![Version](https://img.shields.io/badge/version-1.0.0--beta.6-blue)](https://modrinth.com/mod/brick-lib-api)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.18.2--1.21.5-green)](https://modrinth.com/mod/brick-lib-api)
[![License](https://img.shields.io/badge/license-Apache%202.0-orange)](LICENSE)
[![Modrinth](https://img.shields.io/badge/Modrinth-CSKdjzLF-1bd96a)](https://modrinth.com/mod/brick-lib-api)
[![CurseForge](https://img.shields.io/badge/CurseForge-1367515-f16436)](https://www.curseforge.com/minecraft/mc-mods/brick-lib-api)

A cross-platform, cross-version Minecraft modding library that provides a unified API layer over **Fabric**, **Forge**, and **NeoForge**. Write your mod once, run it across 11 Minecraft versions from 1.18.2 to 1.21.5.

---

## Table of Contents

- [Supported Platforms](#supported-platforms)
- [Getting Started](#getting-started)
- [Event API](#event-api)
- [Registration API](#registration-api)
- [Network API](#network-api)
- [Capability System](#brickCapability-system)
- [Configuration API](#configuration-api)
- [Data Generator](#data-generator)
- [Update Checker](#update-checker)
- [Client Commands](#client-commands)
- [JSON Functions](#json-functions)
- [Utility Classes](#utility-classes)
- [Building from Source](#building-from-source)
- [License](#license)

---

## Supported Platforms

| Minecraft | Fabric | Forge | NeoForge |
|:----------|:------:|:-----:|:--------:|
| 1.18.2    | ✓      | ✓     |          |
| 1.19.2    | ✓      | ✓     |          |
| 1.19.4    | ✓      | ✓     |          |
| 1.20.1    | ✓      | ✓     |          |
| 1.20.4    | ✓      | ✓     | ✓        |
| 1.20.6    | ✓      |       | ✓        |
| 1.21      | ✓      |       | ✓        |
| 1.21.1    | ✓      |       | ✓        |
| 1.21.3    | ✓      |       | ✓        |
| 1.21.4    | ✓      |       | ✓        |
| 1.21.5    | ✓      |       | ✓        |

---

## Getting Started

### Add as a Dependency

Brick Lib API is published on Modrinth. Add the repository and dependency to your `build.gradle` or `build.gradle.kts`:

```gradle
repositories {
    maven { url = "https://api.modrinth.com/maven" }
}

dependencies {
    modImplementation "maven.modrinth:brick-lib-api:${brick_version}-${minecraft_version}-${loader}"
}
```

Replace the placeholders with your target values. For example: `1.0.0-beta.6-1.20.4-forge`.

### Basic Usage

Brick Lib API initializes itself automatically. Just add it as a dependency — no manual initialization is required. All APIs are accessible via static methods from the moment your mod loads.

```java
// The BrickLibAPI class exposes key constants
BrickLibAPI.MOD_ID           // "brick_lib_api"
BrickLibAPI.BRICK_LIB_API_VERSION  // 1.0.0-beta.6
BrickLibAPI.ofPath("my_path")      // ResourceID "brick_lib_api:my_path"
```

---

## Event API

The Event API provides a priority-based event bus with full side-awareness (client, server, common). It supports cancelable events, one-time events, result-bearing events, and both Forge-style and Fabric-style event patterns.

### Core Concepts

| Class / Interface | Purpose |
|:---|:---|
| `BaseEvent` | Abstract base class for all events |
| `BrickEventBus` | Static event bus — register listeners and post events |
| `EventListener<T>` | `@FunctionalInterface` — `void handle(T event)` |
| `EventListenerWrapper<T>` | Wraps a listener with an optional ID and priority |
| `EventListenerWrapper.Priority` | Priority enum: `LOWEST`, `LOW`, `NORMAL`, `HIGH`, `HIGHEST` |
| `BaseEvent.Result` | Result enum: `SUCCESS`, `CONSUME`, `FAIL`, `PASS`, `DEFAULT` |

### Marker Interfaces

| Interface | Effect |
|:---|:---|
| `ICancelableEvent` | Enables `event.cancel()` and `event.isCanceled()` |
| `IResultEvent` | Enables `event.getResult()` and `event.setResult(Result)` |
| `IOneTimeEvent` | All listeners for this event class are removed after it fires once |
| `IClientOnlyEvent` | The event routes to the client listener bus only |
| `IServerOnlyEvent` | The event routes to the server listener bus only |

### Defining a Custom Event

```java
public class BlockBreakEvent extends BaseEvent implements ICancelableEvent {
    private final BlockPos pos;
    private final Player player;

    public BlockBreakEvent(BlockPos pos, Player player) {
        this.pos = pos;
        this.player = player;
    }

    public BlockPos getPos() { return pos; }
    public Player getPlayer() { return player; }
}
```

For an event with a result:

```java
public class ToolUseEvent extends BaseEvent implements IResultEvent {
    private final ItemStack tool;

    public ToolUseEvent(ItemStack tool) {
        this.tool = tool;
    }

    public ItemStack getTool() { return tool; }
}
```

For a one-time initialization event:

```java
public class ModInitEvent extends BaseEvent implements IOneTimeEvent {
    // Fires once, then all listeners are automatically cleaned up
}
```

### Registering Listeners

All registration methods are on `BrickEventBus`.

**Auto-routing** (routes to client/server/common based on marker interfaces):

```java
// Simple listener with default NORMAL priority
BrickEventBus.registerListener(BlockBreakEvent.class, event -> {
    System.out.println("Block broken at " + event.getPos());
});

// Listener with an ID (useful for debugging)
BrickEventBus.registerListener(BlockBreakEvent.class, "mymod:logger", event -> {
    MyMod.LOGGER.info("Block broken: {}", event.getPos());
});

// Listener with explicit priority
BrickEventBus.registerListener(BlockBreakEvent.class, "mymod:high_priority",
    EventListenerWrapper.Priority.HIGH, event -> {
        // HIGH priority listeners fire before NORMAL and LOW
    });
```

**Explicit side registration:**

```java
// Server-side only
BrickEventBus.registerListenerServer(MyServerEvent.class, event -> { ... });

// Client-side only
BrickEventBus.registerListenerClient(MyClientEvent.class, event -> { ... });

// Common (both sides)
BrickEventBus.registerListenerCommon(MyCommonEvent.class, event -> { ... });
```

**Dual-side registration** (different handlers for client vs server):

```java
BrickEventBus.registerListenerBoth(MyEvent.class,
    event -> { /* client-side handler */ },
    event -> { /* server-side handler */ }
);
```

### Posting Events

```java
// Auto-routing based on event's marker interfaces
BlockBreakEvent event = new BlockBreakEvent(pos, player);
boolean wasCanceled = BrickEventBus.postEvent(event);

if (wasCanceled) {
    return; // A listener canceled the event
}
```

```java
// Post to a specific bus
BrickEventBus.postEventServer(event);   // COMMON bus → SERVER bus
BrickEventBus.postEventClient(event);   // COMMON bus → CLIENT bus
BrickEventBus.postEventCommon(event);   // COMMON bus only
```

### Priority Order

Listeners are invoked from highest priority to lowest:

```
HIGHEST (5) → HIGH (4) → NORMAL (3) → LOW (2) → LOWEST (1)
```

If a `HIGHEST` listener cancels a cancelable event, lower-priority listeners are skipped entirely.

### Event Lifecycle Flow

1. `postEvent()` is called
2. Listeners are sorted by priority (HIGHEST first)
3. Each listener's `handle(event)` is invoked in order
4. If the event is `ICancelableEvent` and `isCanceled()` returns `true`, propagation stops
5. If the event is `IOneTimeEvent`, all listeners for that event class are removed
6. On Forge, the event is also posted to `MinecraftForge.EVENT_BUS` after each listener
7. Returns `true` if the event was canceled

### Inspecting Registered Listeners

```java
// Get copies of listener maps for debugging
var serverListeners = BrickEventBus.serverListeners();
var clientListeners = BrickEventBus.clientListeners();
var commonListeners = BrickEventBus.commonListeners();
```

---

## Registration API

The Registration API provides unified deferred registration for both vanilla Minecraft registries and Brick Lib's custom registries.

### Core Concepts

| Class | Purpose |
|:---|:---|
| `BrickRegisterManager` | Central registration hub — static methods for all registration |
| `VanillaRegistry<T>` | Wraps a Minecraft `Registry<T>` |
| `BrickRegistry<T>` | Custom Brick Lib registry backed by a `HashMap` |
| `PlaceHolderRegistry<T>` | Placeholder for registries that don't exist in the current version |
| `BrickRegistries` | Contains all registry constants |

### Vanilla Registry Registration

Deferred registration means your entries are queued and committed when the platform fires its registry event. Use `BrickRegisterManager.register()`:

```java
// Register an item
BrickRegisterManager.register(
    BrickRegistries.ITEM,
    new ResourceLocation("mymod", "my_item"),
    () -> new Item(new Item.Properties())
);

// Register a block
BrickRegisterManager.register(
    BrickRegistries.BLOCK,
    new ResourceLocation("mymod", "my_block"),
    () -> new Block(BlockBehaviour.Properties.of())
);

// Register a block entity type
BrickRegisterManager.register(
    BrickRegistries.BLOCK_ENTITY_TYPE,
    new ResourceLocation("mymod", "my_be"),
    () -> BlockEntityType.Builder.of(MyBlockEntity::new, myBlock).build(null)
);

// Register an entity type
BrickRegisterManager.register(
    BrickRegistries.ENTITY_TYPE,
    new ResourceLocation("mymod", "my_entity"),
    () -> EntityType.Builder.of(MyEntity::new, MobCategory.CREATURE).build("my_entity")
);

// Register a sound event
BrickRegisterManager.register(
    BrickRegistries.SOUND_EVENT,
    new ResourceLocation("mymod", "my_sound"),
    () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("mymod", "my_sound"))
);
```

You can also pass the raw Minecraft `Registry` directly:

```java
BrickRegisterManager.register(
    BuiltInRegistries.ITEM,
    new ResourceLocation("mymod", "my_item"),
    () -> new Item(new Item.Properties())
);
```

### Available Vanilla Registries

All vanilla registries are available as `VanillaRegistry<T>` constants in `BrickRegistries`:

`ITEM`, `BLOCK`, `ENTITY_TYPE`, `BLOCK_ENTITY_TYPE`, `SOUND_EVENT`, `FLUID`, `MOB_EFFECT`, `ENCHANTMENT`, `POTION`, `PARTICLE_TYPE`, `PAINTING_VARIANT`, `CUSTOM_STAT`, `CHUNK_STATUS`, `MENU`, `RECIPE_TYPE`, `RECIPE_SERIALIZER`, `ATTRIBUTE`, `VILLAGER_TYPE`, `VILLAGER_PROFESSION`, `POINT_OF_INTEREST_TYPE`, `MEMORY_MODULE_TYPE`, `SENSOR_TYPE`, `SCHEDULE`, `ACTIVITY`, `LOOT_POOL_ENTRY_TYPE`, `LOOT_FUNCTION_TYPE`, `LOOT_CONDITION_TYPE`, `LOOT_NUMBER_PROVIDER_TYPE`, `LOOT_NBT_PROVIDER_TYPE`, `LOOT_SCORE_PROVIDER_TYPE`, `FLOAT_PROVIDER_TYPE`, `INT_PROVIDER_TYPE`, `HEIGHT_PROVIDER_TYPE`, `BLOCK_PREDICATE_TYPE`, `CARVER`, `FEATURE`, `STRUCTURE_PLACEMENT`, `STRUCTURE_PIECE`, `STRUCTURE_TYPE`, `PLACEMENT_MODIFIER_TYPE`, `BLOCKSTATE_PROVIDER_TYPE`, `FOLIAGE_PLACER_TYPE`, `TRUNK_PLACER_TYPE`, `ROOT_PLACER_TYPE`, `TREE_DECORATOR_TYPE`, `FEATURE_SIZE_TYPE`, `BIOME_SOURCE`, `CHUNK_GENERATOR`, `MATERIAL_CONDITION`, `MATERIAL_RULE`, `DENSITY_FUNCTION_TYPE`, `STRUCTURE_PROCESSOR`, `STRUCTURE_POOL_ELEMENT`, `CAT_VARIANT`, `FROG_VARIANT`, `BANNER_PATTERN`, `INSTRUMENT`, `DECORATED_POT_PATTERNS`, `CREATIVE_MODE_TAB`, `DATA_COMPONENT_TYPE`, and more.

### Custom Brick Lib Registries

In addition to vanilla registries, Brick Lib provides its own registries for mod-specific features:

```java
// Register a network packet
BrickRegisterManager.register(
    BrickRegistries.NETWORK_PACKET,
    new ResourceLocation("mymod", "my_packet"),
    () -> new PacketConfig.S2C<>(MyPacket.class, MyPacket::encode, MyPacket::new, MyPacket::handle, false)
);

// Register a server command
BrickRegisterManager.register(
    BrickRegistries.COMMAND,
    new ResourceLocation("mymod", "my_command"),
    () -> ctx -> LiteralArgumentBuilder.<CommandSourceStack>literal("mycommand")
        .executes(cmdCtx -> { /* ... */ return 1; })
);

// Register a mod configuration
BrickRegisterManager.register(
    BrickRegistries.CONFIG,
    new ResourceLocation("mymod", "config"),
    () -> new ModConfig(ModConfig.Type.COMMON, myConfigSpec, "mymod")
);

// Register a villager trade
BrickRegisterManager.register(
    BrickRegistries.VILLAGER_TRADE,
    new ResourceLocation("mymod", "trade"),
    () -> new VillagerTradeEntry(...)
);

// Register an update checker
BrickRegisterManager.register(
    BrickRegistries.UPDATE_CHECK,
    () -> UpdateChecker.Entry.modrinth("myProjectId", infoList -> {
        // Check for newer versions
    })
);
```

| Custom Registry | Type | Purpose |
|:---|:---|:---|
| `BrickRegistries.KEY_MAPPING` | `BrickRegistry<KeyMapping>` | Key bindings |
| `BrickRegistries.NETWORK_PACKET` | `BrickRegistry<PacketConfig>` | Network packets |
| `BrickRegistries.COMMAND` | `BrickRegistry<Function<...>>` | Server commands |
| `BrickRegistries.CLIENT_COMMAND` | `BrickRegistry<Function<...>>` | Client commands |
| `BrickRegistries.JSON_FUNCTION` | `BrickRegistry<JsonFunction>` | JSON-callable functions |
| `BrickRegistries.DATA_GENERATE` | `BrickRegistry<DataGenerateEntry>` | Data generation entries |
| `BrickRegistries.UPDATE_CHECK` | `BrickRegistry<UpdateChecker.Entry>` | Update checkers |
| `BrickRegistries.CONFIG` | `BrickRegistry<ModConfig>` | Mod configs |
| `BrickRegistries.VILLAGER_TRADE` | `BrickRegistry<VillagerTradeEntry>` | Villager trades |
| `BrickRegistries.WANDERING_TRADE` | `BrickRegistry<VillagerTradeEntry>` | Wandering trader trades |

### Direct BrickRegistry Usage

You can also interact with `BrickRegistry` directly:

```java
// Register with auto-generated ResourceLocation
BrickRegistries.JSON_FUNCTION.register("my_function", args -> {
    // Process JSON args and return a result
    return "Hello from JSON!";
});

// Look up values
MyConfig config = BrickRegistries.CONFIG.get(new ResourceLocation("mymod", "config"));

// Iterate entries
BrickRegistries.NETWORK_PACKET.foreachRegistered((id, packetConfig) -> {
    System.out.println("Registered packet: " + id);
});

// Check count
int count = BrickRegistries.COMMAND.count();
```

### Deferred Registration Mechanism

**Vanilla registries** use a two-phase approach:

1. **Collection phase** (mod constructor): Entries are stored in `BrickRegisterManager`'s internal `ConcurrentHashMap` as `ResourceLocation → Supplier<T>` pairs. Duplicate IDs are silently rejected.
2. **Commitment phase** (platform registry event): On Forge/NeoForge, entries are committed during `RegisterEvent`. On Fabric, during `commonSetup`. The stored suppliers are resolved and registered into the actual Minecraft registry.

**Brick Lib custom registries** store entries immediately in their internal `HashMap`. The platform calls `foreachRegistered()` on each registry at the appropriate lifecycle point (e.g., network setup for `NETWORK_PACKET`, command registration for `COMMAND`, etc.).

**Auto-clean**: Registries marked with `autoClean = true` (like `VILLAGER_TRADE` and `WANDERING_TRADE`) are automatically cleared when the Minecraft server starts, freeing memory after entries have been consumed.

### Placeholder Registries

For registries that only exist in newer Minecraft versions, Brick Lib provides `PlaceHolderRegistry`. When compiling for an older version, registering into a placeholder registry is a safe no-op — it logs a warning but does not crash:

```
Registry data_component_type is either obsolete or for future use;
registering mymod:my_component in data_component_type does nothing in version 1.18.2!
```

---

## Network API

The Network API provides a unified packet system supporting client-to-server, server-to-client, bidirectional, and login-phase communication across all mod loaders.

### Core Concepts

| Class | Purpose |
|:---|:---|
| `Packet` | Abstract base class (package-private) |
| `IHandleablePacket` | Interface — defines `encoder()` and `id()` |
| `C2SPacket` | Client-to-server packet (implements `ISHandlePacket`) |
| `S2CPacket` | Server-to-client packet (implements `ICHandlePacket`) |
| `SACPacket` | Bidirectional packet (implements both `ISHandlePacket` and `ICHandlePacket`) |
| `LoginPacket` | Login-phase packet (implements `IntSupplier`, `IHandleablePacket`) |
| `PacketConfig` | Configuration class with nested types for each packet direction |
| `BrickNetwork` | High-level API for sending messages and packets |
| `PacketContent` | Wrapper around `FriendlyByteBuf` for encoding/decoding |

### Packet Types

| Type | Direction | Handler Interface | When to Use |
|:---|:---|:---|:---|
| `C2SPacket` | Client → Server | `ISHandlePacket` → `serverHandle(C2SNetworkContext)` | Player actions, requests |
| `S2CPacket` | Server → Client | `ICHandlePacket` → `clientHandle(S2CNetworkContext)` | Sync data, UI updates |
| `SACPacket` | Both directions | Both `serverHandle` and `clientHandle` | Chat messages, shared events |
| `LoginPacket` | Login phase, both | Both `serverHandle` and `clientHandle` | Config sync, early negotiation |

### Defining a Server-to-Client Packet

```java
public class MySyncPacket extends S2CPacket {

    private final int someData;
    private final String someString;

    public MySyncPacket(int someData, String someString) {
        this.someData = someData;
        this.someString = someString;
    }

    // Decoder constructor — reads from a PacketContent
    public MySyncPacket(PacketContent content) {
        this.someData = content.readInt();
        this.someString = content.readUTF();
    }

    @Override
    public void clientHandle(S2CNetworkContext context) {
        // Runs on the client side
        context.enqueueWork(() -> {
            // Do something on the main client thread
            System.out.println("Received: " + someData + ", " + someString);
        });
    }

    @Override
    public void encoder(PacketContent content) {
        content.writeInt(someData);
        content.writeUTF(someString);
    }

    // Configuration — register this in BrickRegistries.NETWORK_PACKET
    public static final PacketConfig.S2C<MySyncPacket> CONFIG =
        new PacketConfig.S2C<>(
            MySyncPacket.class,
            MySyncPacket::encoder,          // BiConsumer<T, PacketContent>
            MySyncPacket::new,              // Function<PacketContent, T> (decoder)
            MySyncPacket::clientHandle,     // BiConsumer<T, S2CNetworkContext>
            false                           // netHandle: false = main thread, true = network thread
        );
}
```

### Defining a Client-to-Server Packet

```java
public class MyRequestPacket extends C2SPacket {

    private final String request;

    public MyRequestPacket(String request) {
        this.request = request;
    }

    public MyRequestPacket(PacketContent content) {
        this.request = content.readUTF();
    }

    @Override
    public void serverHandle(C2SNetworkContext context) {
        ServerPlayer sender = context.getSender();
        context.enqueueWork(() -> {
            // Process the request on the server main thread
            System.out.println(sender.getName() + " requested: " + request);
        });
    }

    @Override
    public void encoder(PacketContent content) {
        content.writeUTF(request);
    }

    public static final PacketConfig.C2S<MyRequestPacket> CONFIG =
        new PacketConfig.C2S<>(
            MyRequestPacket.class,
            MyRequestPacket::encoder,
            MyRequestPacket::new,
            MyRequestPacket::serverHandle,
            false
        );
}
```

### Defining a Bidirectional Packet

```java
public class MyChatPacket extends SACPacket {

    private final String message;

    public MyChatPacket(String message) {
        this.message = message;
    }

    public MyChatPacket(PacketContent content) {
        this.message = content.readUTF();
    }

    @Override
    public void serverHandle(C2SNetworkContext context) {
        // Received on server — broadcast to all players
        BrickNetwork.sendToAllPlayers(new MyChatPacket(
            context.getSender().getName() + ": " + message
        ));
    }

    @Override
    public void clientHandle(S2CNetworkContext context) {
        // Received on client — display message
        System.out.println("Chat: " + message);
    }

    @Override
    public void encoder(PacketContent content) {
        content.writeUTF(message);
    }

    public static final PacketConfig.SAC<MyChatPacket> CONFIG =
        new PacketConfig.SAC<>(
            MyChatPacket.class,
            MyChatPacket::encoder,
            MyChatPacket::new,
            MyChatPacket::serverHandle,
            MyChatPacket::clientHandle,
            false,  // serverNetHandle
            false   // clientNetHandle
        );
}
```

### Defining a Login Packet

```java
public class MyLoginPacket extends LoginPacket {

    private final String data;

    public MyLoginPacket(String data) {
        this.data = data;
    }

    public MyLoginPacket(PacketContent content) {
        this.data = content.readUTF();
    }

    @Override
    public void serverHandle(C2SNetworkContext context) {
        // Handle the login reply from the client
    }

    @Override
    public void clientHandle(S2CNetworkContext context) {
        // Handle the login data on the client
    }

    @Override
    public void encoder(PacketContent content) {
        content.writeUTF(data);
    }

    public static final PacketConfig.Login<MyLoginPacket> CONFIG =
        new PacketConfig.Login<>(
            MyLoginPacket.class,
            MyLoginPacket::encoder,
            MyLoginPacket::new,   // c2sDecoder
            MyLoginPacket::new,   // s2cDecoder
            MyLoginPacket::serverHandle,
            MyLoginPacket::clientHandle,
            isLocal -> List.of()  // packet generator
        );
}
```

### Registering Packets

All packet configs are registered through `BrickRegistries.NETWORK_PACKET`:

```java
BrickRegisterManager.register(
    BrickRegistries.NETWORK_PACKET,
    new ResourceLocation("mymod", "my_sync_packet"),
    () -> MySyncPacket.CONFIG
);

BrickRegisterManager.register(
    BrickRegistries.NETWORK_PACKET,
    new ResourceLocation("mymod", "my_request_packet"),
    () -> MyRequestPacket.CONFIG
);
```

### Sending Packets

Use `BrickNetwork` for high-level sending:

```java
// Send to specific players
BrickNetwork.sendToPlayer(new MySyncPacket(42, "hello"), player1, player2);

// Send to all players on the server
BrickNetwork.sendToAllPlayers(new MySyncPacket(42, "broadcast"));

// Send to the server (from client)
BrickNetwork.sendToServer(new MyRequestPacket("do_something"));
```

### Sending Raw Strings

`BrickNetwork` also supports sending raw strings with event monitoring:

```java
// Server → specific players
BrickNetwork.sendMessageToPlayer("my_event_id", "message content", player1, player2);

// Server → all players
BrickNetwork.sendMessageToAllPlayers("my_event_id", "broadcast message");

// Client → server
BrickNetwork.sendMessageToServer("my_event_id", "request data");
```

These methods trigger `NetworkMessageEvent` (which extends `BaseEvent` and `ICancelableEvent`). You can listen for and cancel these events:

```java
// Monitor outgoing server messages
BrickEventBus.registerListener(NetworkMessageEvent.ServerSend.class, event -> {
    System.out.println("Sending to " + event.getTargetCount() + " players: " + event.getMessage());
});

// Monitor incoming client messages
BrickEventBus.registerListener(NetworkMessageEvent.ClientReceive.class, event -> {
    System.out.println("Received: " + event.getMessage());
});

// Cancel an outgoing message
BrickEventBus.registerListener(NetworkMessageEvent.ServerSend.class, event -> {
    if (event.getId().equals("blocked_event")) {
        event.cancel();
    }
});
```

### PacketContent Encoding/Decoding

`PacketContent` wraps `FriendlyByteBuf` and provides fluent write methods and read methods:

**Write methods** (all return `PacketContent` for chaining):

```java
content.writeUTF("hello")
      .writeInt(42)
      .writeBoolean(true)
      .writeLong(1000L)
      .writeDouble(3.14)
      .writeFloat(1.5f)
      .writeShort((short) 10)
      .writeByte((byte) 0xFF)
      .writeResourceLocation(new ResourceLocation("mymod", "thing"))
      .writeItemStack(stack)
      .writePosition(blockPos)
      .writeNBT(compoundTag)
      .writeChunkPos(chunkPos);
```

**Read methods:**

```java
String str = content.readUTF();
int i = content.readInt();
boolean b = content.readBoolean();
long l = content.readLong();
double d = content.readDouble();
float f = content.readFloat();
ItemStack stack = content.readItemStack();
BlockPos pos = content.readPosition();
CompoundTag tag = content.readNBT();
byte[] bytes = content.readByteArray();
```

### Network Context

Each packet handler receives a context object that provides access to the sender (for C2S) and a way to enqueue work on the main thread:

```java
// C2S context — provides the sending player
public void serverHandle(C2SNetworkContext context) {
    ServerPlayer sender = context.getSender();
    context.enqueueWork(() -> {
        // Safe to interact with the world here
        sender.getLevel().setBlock(..., ...);
    });
}

// S2C context — no player reference (it's the local client)
public void clientHandle(S2CNetworkContext context) {
    context.enqueueWork(() -> {
        // Safe to interact with client-side systems
        Minecraft.getInstance().player.displayClientMessage(...);
    });
}
```

Use `context.direction()` to check the `NetworkDirection` (P2C, P2S, L2C, L2S).

---

## Capability System

The Capability System is a cross-loader abstraction that unifies Forge's `ICapabilityProvider`, NeoForge's `BlockCapability`, and Fabric's `BlockApiLookup` / Transfer API. It provides built-in types for energy, fluid, and item storage, along with a transaction system for safe resource operations.

### Core Concepts

| Class / Interface | Purpose |
|:---|:---|
| `Capability<T>` | Type-safe brickCapability identifier |
| `CapabilityManager` | Central registry for all capabilities |
| `CapabilityToken<T>` | Type token for safe type inference (use as `new CapabilityToken<>() {}`) |
| `OperationType` | `INSERT`, `EXTRACT`, `TRANSACT`, `QUERY` |
| `Storage<T>` | Generic storage interface with insert/extract |
| `StorageView<T>` | A single slot/tank view within storage |
| `TransferVariant<T>` | Wraps a resource object with optional NBT |
| `CapabilityProvider` | Interface for objects that provide capabilities |
| `ProviderRegistry` | Global registry for brickCapability provider factories |
| `CapabilityCompat` | Cross-loader bridge — queries Brick Lib then native APIs |

### Built-in Capabilities

Brick Lib ships three built-in brickCapability types:

| Capability | Interface | Access |
|:---|:---|:---|
| Energy | `IEnergyStorage` | `BuiltinCapabilities.ENERGY` |
| Fluid | `IFluidStorage` | `BuiltinCapabilities.FLUID_HANDLER` |
| Item | `IItemStorage` | `BuiltinCapabilities.ITEM_HANDLER` |

### IEnergyStorage

Energy uses `long` values in Forge Energy (FE) units.

```java
public interface IEnergyStorage {
    long receiveEnergy(long maxReceive, BrickTransactionContext tx);
    long extractEnergy(long maxExtract, BrickTransactionContext tx);
    long getEnergyStored();
    long getMaxEnergyStored();
    boolean canReceive();
    boolean canExtract();
}
```

### IFluidStorage

Fluid uses droplets (81000 droplets = 1 bucket), aligning with Fabric's Transfer API.

```java
public interface IFluidStorage {
    long BUCKET = 81000L;  // 1 bucket in droplets
    long BOTTLE = 27000L;  // 1/3 bucket (bottle)
    long INGOT = 9000L;    // 1/9 bucket (ingot)
    long NUGGET = 1000L;   // 1/81 bucket (nugget)

    int getTanks();
    Fluid getFluidInTank(int tank);
    long getFluidAmountInTank(int tank);
    long getTankCapacity(int tank);
    boolean isFluidValid(int tank, Fluid fluid);
    long fill(Fluid fluid, long maxAmount, BrickTransactionContext tx);
    long drain(Fluid fluid, long maxAmount, BrickTransactionContext tx);
    long drain(long maxAmount, BrickTransactionContext tx);
}
```

### IItemStorage

```java
public interface IItemStorage {
    int getSlots();
    ItemStack getStackInSlot(int slot);
    long getAmountInSlot(int slot);
    long getSlotCapacity(int slot);
    long insertItem(int slot, ItemStack resource, long maxAmount, BrickTransactionContext tx);
    long extractItem(int slot, long maxAmount, BrickTransactionContext tx);
    boolean isItemValid(int slot, ItemStack resource);
}
```

### Accessing Capabilities

Use `CapabilityCompat` to query capabilities from blocks or block entities:

```java
// From a block position
LazyOptional<IEnergyStorage> energyOpt = CapabilityCompat.getCapability(
    level, blockPos, BuiltinCapabilities.ENERGY, direction
);

// From a block entity
LazyOptional<IFluidStorage> fluidOpt = CapabilityCompat.getCapability(
    blockEntity, BuiltinCapabilities.FLUID_HANDLER, direction
);

// Consume the value
energyOpt.ifPresent(energy -> {
    long extracted = energy.extractEnergy(1000, BrickTransaction.openOuter());
    System.out.println("Extracted " + extracted + " FE");
});
```

**Query order**: Brick Lib first checks its own provider system (`ProviderRegistry`), then falls back to the native loader's brickCapability system (Forge `ICapabilityProvider`, NeoForge `BlockCapability`, or Fabric `BlockApiLookup`). Built-in mappings for ENERGY, ITEM_HANDLER, and FLUID_HANDLER are registered automatically.

### Transaction System

The transaction system provides safe, rollback-capable storage operations. Operations can be simulated, then committed or rolled back.

```java
// Open an outer transaction
try (BrickTransaction tx = BrickTransaction.openOuter()) {
    // Simulate operations
    long canExtract = energy.extractEnergy(100, tx);
    long canInsert = otherEnergy.receiveEnergy(100, tx);

    if (canExtract == 100 && canInsert == 100) {
        tx.commit();  // Both operations succeed
    }
    // If not committed, all changes are rolled back on close()
}
```

**Key transaction concepts:**

| Class | Purpose |
|:---|:---|
| `BrickTransaction` | Implements `AutoCloseable` — use with try-with-resources |
| `BrickTransactionContext` | Interface providing `nestingDepth()`, `getTransaction()`, `addParticipant()`, `addListener()` |
| `BrickSnapshotParticipant<S>` | Base class for storage that needs snapshot/rollback |
| `BrickTransactionListener` | Hooks: `beforeCommit()`, `afterCommit()`, `onAbort()` |
| `BrickTransactionException` | Runtime exception for transaction errors |

```java
// Nested transactions
try (BrickTransaction outer = BrickTransaction.openOuter()) {
    storage.insert(resource1, 10, outer);

    try (BrickTransaction inner = BrickTransaction.openNested(outer)) {
        storage.extract(resource2, 5, inner);
        inner.commit();  // Commit inner independently
    }

    // If outer is not committed, both inner and outer changes roll back
}
```

### Simple Storage Implementations

Brick Lib provides simple implementations for all three built-in capabilities, using `BrickSnapshotParticipant` for automatic transaction support:

**SimpleEnergyStorage:**

```java
SimpleEnergyStorage energy = new SimpleEnergyStorage(10000, 1000);
// capacity=10000, maxReceive=1000, maxExtract=1000

SimpleEnergyStorage energy = new SimpleEnergyStorage(10000, 500, 200);
// capacity=10000, maxReceive=500, maxExtract=200

// Transaction-aware operations
long received = energy.receiveEnergy(100, tx);
long extracted = energy.extractEnergy(50, tx);

// Direct manipulation (bypasses transactions, for deserialization)
energy.setEnergy(5000);
```

**SimpleFluidStorage:**

```java
SimpleFluidStorage fluid = new SimpleFluidStorage(IFluidStorage.BUCKET * 8);
// 8-bucket capacity

try (BrickTransaction tx = BrickTransaction.openOuter()) {
    long filled = fluid.fill(Fluids.WATER, IFluidStorage.BUCKET, tx);
    tx.commit();
}

// Unit conversion via CompatUtil
int mb = CompatUtil.dropletsToMb(fluid.getFluidAmountInTank(0));
```

**SimpleItemStorage:**

```java
SimpleItemStorage items = new SimpleItemStorage(27, 64);
// 27 slots, 64 max per slot

SimpleItemStorage items = new SimpleItemStorage(9);
// 9 slots, default 64 per slot

try (BrickTransaction tx = BrickTransaction.openOuter()) {
    items.insertItem(0, new ItemStack(Items.DIAMOND), 10, tx);
    tx.commit();
}
```

### Registering Custom Capability Providers

Use `ProviderRegistry` to register factories that create brickCapability providers for specific target types:

```java
// Register a provider for your block entity class
ProviderRegistry.register(MyBlockEntity.class, (be, existingData) -> {
    return new CapabilityProvider() {
        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
            if (cap == BuiltinCapabilities.ENERGY) {
                return LazyOptional.of(() -> (T) be.getEnergyStorage());
            }
            return LazyOptional.empty();
        }
    };
});
```

### Defining a Custom Capability

```java
// 1. Define your brickCapability interface
public interface IMagicStorage {
    long getMana();
    long getMaxMana();
    long consumeMana(long amount, BrickTransactionContext tx);
    long replenishMana(long amount, BrickTransactionContext tx);
}

// 2. Create a CapabilityToken and register
Capability<IMagicStorage> MAGIC = CapabilityManager.get(new CapabilityToken<IMagicStorage>() {});

// 3. Use it as a brickCapability
LazyOptional<IMagicStorage> magic = CapabilityCompat.getCapability(be, MAGIC, side);
```

### Block Interaction API

Register block interaction handlers that work across all loaders:

```java
BlockInteractionApi.register((player, level, pos, hand, hitResult) -> {
    if (level.getBlockState(pos).is(Blocks.FURNACE)) {
        // Custom furnace interaction
        return InteractionResult.SUCCESS;
    }
    return InteractionResult.PASS;
});
```

Initialization happens automatically, but you can also call `BlockInteractionApi.init()` manually if needed.

### Energy Ejector API

Push energy from your blocks to neighboring blocks:

```java
EnergyEjectorApi.register(
    (level, consumer) -> {
        // Iterate over all your energy-producing blocks
        FurnaceEnergyData.get(level).forEachPosition((pos, storage) -> {
            consumer.accept(pos, storage);
        });
    },
    1000  // transfer rate in FE per tick
);
```

This handles fair distribution among neighbors and automatically bridges to Fabric, Forge, and NeoForge.

### LazyOptional

`LazyOptional<T>` is a lazy value container similar to Forge's `LazyOptional`:

```java
// Create from an instance (resolved immediately)
LazyOptional<IEnergyStorage> opt = LazyOptional.of(energyStorage);

// Create from a supplier (resolved lazily)
LazyOptional<IEnergyStorage> opt = LazyOptional.of(() -> createExpensiveStorage());

// Create an empty optional
LazyOptional<IEnergyStorage> empty = LazyOptional.empty();

// Consume the value
opt.ifPresent(storage -> {
    storage.extractEnergy(100, tx);
});

// Get value or throw
IEnergyStorage storage = opt.orElseThrow();

// Get value or default
IEnergyStorage storage = opt.orElse(defaultStorage);

// Add invalidation listener
opt.addListener(() -> System.out.println("Capability invalidated"));

// Invalidate (triggers listeners)
opt.invalidate();
```

---

## Configuration API

The Configuration API provides Forge-style TOML configuration files, ported to work across all mod loaders. It supports COMMON, CLIENT, and SERVER config types with automatic server-to-client syncing.

### Config Types

| Type | Description |
|:---|:---|
| `ModConfig.Type.COMMON` | Loaded on both sides, global. Located in the global config directory. |
| `ModConfig.Type.CLIENT` | Client-only, global. Located in the global config directory. |
| `ModConfig.Type.SERVER` | Per-save/server config. Synced from server to connecting clients. |

### Building a Config Spec

Use the `BrickConfigSpec.Builder` to define config values:

```java
public class MyModConfig {
    public static final BrickConfigSpec SPEC;

    // Define config values
    public static final BrickConfigSpec.IntValue MAX_ENERGY;
    public static final BrickConfigSpec.BooleanValue ENABLE_FEATURE;
    public static final BrickConfigSpec.DoubleValue SPEED_MULTIPLIER;
    public static final BrickConfigSpec.LongValue MAX_CAPACITY;
    public static final BrickConfigSpec.ConfigValue<List<? extends String>> ALLOWED_ITEMS;

    static {
        BrickConfigSpec.Builder builder = new BrickConfigSpec.Builder();

        builder.push("general");
        MAX_ENERGY = builder
            .comment("Maximum energy storage capacity")
            .defineInRange("max_energy", 10000, 0, Integer.MAX_VALUE);
        ENABLE_FEATURE = builder
            .comment("Enable the special feature")
            .define("enable_feature", true);
        builder.pop();

        builder.push("advanced");
        SPEED_MULTIPLIER = builder
            .comment("Speed multiplier for processing")
            .defineInRange("speed_multiplier", 1.0, 0.1, 10.0);
        MAX_CAPACITY = builder
            .comment("Maximum long-term capacity")
            .defineInRange("max_capacity", 1000000L, 0L, Long.MAX_VALUE);
        ALLOWED_ITEMS = builder
            .comment("List of allowed item IDs")
            .defineList("allowed_items", List.of("minecraft:diamond"), obj -> obj instanceof String);
        builder.pop();

        SPEC = builder.build();
    }
}
```

### Registering a Config

Register your config through `BrickRegistries.CONFIG`:

```java
ModConfig config = new ModConfig(
    ModConfig.Type.COMMON,
    MyModConfig.SPEC,
    "mymod"  // modId — auto-generates filename "mymod-common.toml"
);

BrickRegisterManager.register(
    BrickRegistries.CONFIG,
    new ResourceLocation("mymod", "config"),
    () -> config
);
```

Registering through `BrickRegistries.CONFIG` automatically calls `ConfigTracker.trackConfig()`, which handles file loading, saving, and server-client sync.

### Reading Config Values

```java
int maxEnergy = MyModConfig.MAX_ENERGY.get();
boolean enabled = MyModConfig.ENABLE_FEATURE.get();
double speed = MyModConfig.SPEED_MULTIPLIER.get();
List<String> items = MyModConfig.ALLOWED_ITEMS.get();
```

### Config Events

Listen for config load, unload, and reload events:

```java
BrickEventBus.registerListener(ConfigEvent.Load.class, event -> {
    ModConfig config = event.config();
    if (config.getModId().equals("mymod")) {
        // Config was loaded — re-cache values if needed
    }
});

BrickEventBus.registerListener(ConfigEvent.Reload.class, event -> {
    // Config was reloaded (e.g., after server sync)
});

BrickEventBus.registerListener(ConfigEvent.Unload.class, event -> {
    // Config is being unloaded
});
```

### Builder Features

The `BrickConfigSpec.Builder` supports:

- **Comments**: `builder.comment("Description text")`
- **Translation keys**: `builder.translation("mymod.config.section.key")`
- **World restart required**: `builder.worldRestart()`
- **Push/pop path nesting**: `builder.push("section")` / `builder.pop()`
- **Define methods**: `define()`, `defineInRange()`, `defineInList()`, `defineList()`, `defineListAllowEmpty()`, `defineEnum()`
- **Typed values**: `IntValue`, `LongValue`, `DoubleValue`, `BooleanValue`, `EnumValue`

---

## Data Generator

Cross-version data generation supporting data packs, resource packs, and reports.

### Creating a Data Provider

```java
public class MyDataProvider extends BrickDataProvider {

    public MyDataProvider(Map<BrickDataGenerator.TargetType, Path> map) {
        super(map);
    }

    @Override
    public List<Pair<JsonElement, Path>> contents(PathProvider output) {
        List<Pair<JsonElement, Path>> result = new ArrayList<>();

        // Generate a recipe JSON
        JsonObject recipe = new JsonObject();
        recipe.addProperty("type", "minecraft:crafting_shaped");
        // ... build recipe
        result.add(new Pair<>(recipe, output.json(new ResourceLocation("mymod", "recipes/my_recipe"))));

        return result;
    }

    @Override
    public BrickDataGenerator.TargetType type() {
        return BrickDataGenerator.TargetType.DATA_PACK;
    }

    @Override
    public String registerName() {
        return "mymod_recipes";
    }
}
```

### Registering and Running

```java
// Register the data generator entry
BrickRegisterManager.register(
    BrickRegistries.DATA_GENERATE,
    new ResourceLocation("mymod", "recipes"),
    () -> new DataGenerateEntry(
        PlatformInfo.of().setServer(),  // runs on server side
        paths -> MyDataProvider::new    // factory
    )
);

// Run data generation (typically called from a Gradle task or runClient/runServer)
BrickDataGenerator.run(/* client */ false, /* server */ true);
```

---

## Update Checker

Checks for mod updates via Modrinth's API.

### Using the Update Checker

```java
// Register an update checker entry
BrickRegisterManager.register(
    BrickRegistries.UPDATE_CHECK,
    () -> UpdateChecker.Entry.modrinth("myProjectId", infoList -> {
        for (ModrinthModInfo info : infoList) {
            Version latest = Version.parse(info.version_number());
            if (latest.compareTo(MyMod.VERSION) > 0) {
                System.out.println("Update available: " + latest);
            }
        }
    })
);
```

### Programmatic Checks

```java
// Synchronous check
List<ModrinthModInfo> versions = UpdateChecker.checkFromModrinth("projectId");

// Asynchronous check
CompletableFuture<List<ModrinthModInfo>> future =
    UpdateChecker.checkFromModrinthAsync("projectId");
future.thenAccept(versions -> {
    // Process results
});

// Custom URL check
String response = UpdateChecker.checkFromCustom("https://example.com/version.json");
CompletableFuture<String> future = UpdateChecker.checkFromCustomAsync("https://example.com/version.json");
```

### Entry Types

```java
// Modrinth entry
UpdateChecker.Entry.modrinth("projectId", infoList -> { ... });

// Custom URL entry
UpdateChecker.Entry.custom("https://api.example.com/version", response -> {
    // Parse custom JSON/text response
});
```

---

## Client Commands

Register client-side commands that work across Fabric and Forge.

```java
// Register a client command
BrickRegisterManager.register(
    BrickRegistries.CLIENT_COMMAND,
    new ResourceLocation("mymod", "mycommand"),
    () -> ctx -> ClientCommands.literal("myclientcmd")
        .then(ClientCommands.argument("value", IntegerArgumentType.integer())
            .executes(cmdCtx -> {
                int value = IntegerArgumentType.getInteger(cmdCtx, "value");
                ClientCommands.sendFeedback(
                    Component.literal("You entered: " + value)
                );
                return 1;
            })
        )
        .executes(cmdCtx -> {
            ClientCommands.sendFeedback(Component.literal("Usage: /myclientcmd <value>"));
            return 1;
        })
);
```

**Key methods:**

| Method | Purpose |
|:---|:---|
| `ClientCommands.literal(name)` | Create a literal argument node |
| `ClientCommands.argument(name, type)` | Create an argument node |
| `ClientCommands.sendFeedback(message)` | Send a feedback message to the player |
| `ClientCommands.sendError(message)` | Send an error message to the player |
| `ClientCommands.getActiveDispatcher()` | Get the current command dispatcher (may be null) |

---

## JSON Functions

Allows registered handlers to be called from JSON with arguments and return values. Useful for data-driven behavior in recipes, loot tables, advancements, etc.

### Defining a JSON Function

```java
@FunctionalInterface
public interface JsonFunction {
    Object execute(JsonArray args);
}

// Register
BrickRegistries.JSON_FUNCTION.register("my_function", args -> {
    String param = args.get(0).getAsString();
    return "Processed: " + param;
});
```

### Calling from JSON

JSON functions are invoked through `JsonFunctionExecutor`. A JSON object with a function key calls the registered handler:

```java
JsonObject json = new JsonObject();
json.addProperty("function", "brick_lib_api:my_function");
JsonArray args = new JsonArray();
args.add("hello");
json.add("args", args);

Object result = JsonFunctionExecutor.execute(json);
```

The executor recognizes these keys for function identification:
- `function`, `func`, `fun`, `f`

And these keys for direct values (returned as-is):
- `value`, `var`, `val`, `v`

```java
// Direct value — returns "hello" without calling any function
Object result = JsonFunctionExecutor.execute("""
    {"value": "hello"}
    """);

// Function call
Object result = JsonFunctionExecutor.execute("""
    {"function": "mymod:my_func", "args": [1, 2, 3]}
    """);
```

---

## Utility Classes

### ResourceID

Extends Minecraft's `ResourceLocation` with additional utility methods. Can be used as a drop-in replacement.

```java
// Construction
ResourceID id = new ResourceID("mymod", "my_path");
ResourceID id = new ResourceID("mymod:my_path");
ResourceID id = new ResourceID(existingResourceLocation);
ResourceID id = ResourceID.of("mymod:my_path", ':');

// Try-parse (returns null on failure instead of throwing)
ResourceID id = ResourceID.tryParse("mymod:my_path");
ResourceID id = ResourceID.tryBuild("mymod", "my_path");

// Validation
boolean valid = ResourceID.isValidResourceLocation("mymod:my_path");
boolean validPath = ResourceID.isValidPath("my_path");
boolean validNamespace = ResourceID.isValidNamespace("mymod");
```

### Version

Full Semantic Versioning 2.0.0 parser and comparator.

```java
// Parse a version string
Version v = Version.parse("1.2.3-beta.4+build.567");

v.major();      // 1
v.minor();      // 2
v.patch();      // 3
v.preRelease(); // "beta.4"
v.buildMeta();  // "build.567"

// Compare versions
Version v1 = Version.parse("1.0.0");
Version v2 = Version.parse("1.0.1");
v1.compareTo(v2); // < 0 (v1 is older)

// Builder pattern
Version v = Version.Builder.create(1, 0, 0)
    .preRelease(Version.PreReleaseType.BETA, 6)
    .build();
// Produces: "1.0.0-beta.6"

// Pre-release type helpers
Version v = Version.Builder.create(1, 0, 0).alpha().build();    // 1.0.0-alpha
Version v = Version.Builder.create(1, 0, 0).beta().build();     // 1.0.0-beta
Version v = Version.Builder.create(1, 0, 0).rc().build();       // 1.0.0-rc
Version v = Version.Builder.create(1, 0, 0).release().build();  // 1.0.0
```

**Pre-release types** (with comparison ordering):
`BASE`(0) < `ALPHA`(1) = `PREVIEW`(1) < `BETA`(2) = `SNAPSHOT`(2) < `RC`(3) < `RELEASE`(4) = `FINAL` = `STABLE` = `GA` = `LTS`

### PlatformInfo

Bitmask-based platform descriptor for side and loader detection.

```java
// Get the current platform state
PlatformInfo info = PlatformInfo.of();

// Check side
boolean isClient = info.isClient();
boolean isServer = info.isServer();

// Check loader
boolean isForge = info.isForge();
boolean isFabric = info.isFabric();
boolean isNeoForge = info.isNeoForge();

// Build custom platform flags
PlatformInfo custom = PlatformInfo.of()
    .setOnlyClient()
    .setOnlyForge();

// Compare
info.sideEquals(other);    // compares side bits
info.loaderEquals(other);  // compares loader bits
```

### SideExecutor

Run code conditionally on specific sides or loaders.

```java
// Run on client side only (silently no-op if on server)
SideExecutor.runOnClient(() -> {
    Minecraft.getInstance().player.displayClientMessage(...);
});

// Run on server side only (throws if on client)
SideExecutor.runOnServerOrException(() -> {
    // Server-only logic
});

// Run on specific loader
SideExecutor.runOnLoader(
    PlatformInfo.of().setOnlyForge(),
    () -> { /* Forge-only code */ }
);

// Run different code on client vs server
SideExecutor.runSeparately(
    () -> { /* client code */ },
    () -> { /* server code */ }
);
```

### BrickSavedData

Cross-platform world saved data. Extend this to persist data per-save.

```java
public class MyWorldData extends BrickSavedData {

    public static final String DATA_NAME = "mymod_world_data";

    private final Map<BlockPos, Long> data = new HashMap<>();

    public MyWorldData() {}

    public MyWorldData(CompoundTag tag) {
        // Deserialize from tag
        ListTag list = tag.getList("entries", Tag.TAG_COMPOUND);
        for (Tag entry : list) {
            CompoundTag ct = (CompoundTag) entry;
            BlockPos pos = BlockPos.of(ct.getLong("pos"));
            data.put(pos, ct.getLong("value"));
        }
    }

    @Override
    public CompoundTag saveData(CompoundTag tag) {
        ListTag list = new ListTag();
        data.forEach((pos, value) -> {
            CompoundTag ct = new CompoundTag();
            ct.putLong("pos", pos.asLong());
            ct.putLong("value", value);
            list.add(ct);
        });
        tag.put("entries", list);
        return tag;
    }

    // Static accessor
    public static MyWorldData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
            MyWorldData::new, MyWorldData::new, DATA_NAME
        );
    }
}
```

---

## Building from Source

### Requirements

- **JDK 21**
- Gradle (wrapper included)

### Quick Start

```bash
git clone https://github.com/fho4565/brick_lib_api.git
cd brick_lib_api

# Build the active version (default: 1.20.4-forge)
./gradlew build

# Switch to a different target version
./gradlew setActive "1.21.1-fabric"
./gradlew build
```

### Multi-Version Architecture

The project uses [Stonecutter](https://github.com/kikugie/stonecutter) for multi-version management. 22 version/loader combinations are supported from a single codebase using preprocessor comments for conditional compilation:

```java
//? if fabric {
    FabricAPI.doSomething();
//?}

//? if forge {
    ForgeAPI.doSomethingElse();
//?}

//? if >= 1.20.6 {
    // Code for Minecraft 1.20.6 and above
//?}
```

### Build Output

The built JAR is output to `build/libs/`. Each version variant produces its own JAR with the appropriate Minecraft version and loader suffix.

---

## License

Brick Lib API is licensed under **Apache 2.0**.

The configuration system classes (`BrickConfigSpec`, `ConfigFileTypeHandler`, `ConfigTracker`, `ModConfig`) are derived from MinecraftForge and licensed under **LGPL-2.1**. See the `NOTICE` file for details.

---

## Credits

- **Arc Studio** — Development
- **YunShen** — Testing
- Built with [Stonecutter](https://github.com/kikugie/stonecutter), [Architectury Loom](https://github.com/architectury/architectury-loom), and [Fletching Table](https://github.com/kikugie/fletching-table)
