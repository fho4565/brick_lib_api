package com.arc_studio.brick_lib_api.register;

import com.arc_studio.brick_lib_api.BrickLibAPI;

import com.arc_studio.brick_lib_api.config.ModConfig;
import com.arc_studio.brick_lib_api.core.VillagerTradeEntry;
import com.arc_studio.brick_lib_api.core.register.*;
import com.arc_studio.brick_lib_api.core.json_function.JsonFunction;
import com.arc_studio.brick_lib_api.core.network.type.PacketConfig;
import com.arc_studio.brick_lib_api.datagen.DataGenerateEntry;
import com.arc_studio.brick_lib_api.update_checker.UpdateChecker;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
//? if > 1.18.2 {
import net.minecraft.commands.CommandBuildContext;
//?} else {
/*import com.arc_studio.brick_lib_api.misc.CommandBuildContext;
*///?}
import net.minecraft.commands.CommandSourceStack;
//? if > 1.18.2 {
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
//?}
import net.minecraft.core.Registry;
//? if >= 1.20.6 {
/*import net.minecraft.core.component.DataComponentType;
*///?}
import net.minecraft.core.particles.ParticleType;
//? if > 1.19.2 {
import net.minecraft.core.registries.BuiltInRegistries;
//?} else {

//?}
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.StatType;
import net.minecraft.util.valueproviders.FloatProviderType;
import net.minecraft.util.valueproviders.IntProviderType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
//? if > 1.18.2 {
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacerType;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.entity.animal.CatVariant;
    //? if < 1.21.5 {
import net.minecraft.world.entity.animal.FrogVariant;
    //?} else {
/*import net.minecraft.world.entity.animal.frog.FrogVariant;
 *///?}
import net.minecraft.world.entity.decoration.PaintingVariant;
//?}
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.ChunkGenerator;
//? if < 1.20.6 {
import net.minecraft.world.level.chunk.ChunkStatus;
//?} else {
/*import net.minecraft.world.level.chunk.status.ChunkStatus;
*///?}
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSourceType;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSizeType;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
//? if > 1.19.4 {
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifierType;
//?}
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.providers.nbt.LootNbtProviderType;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.score.LootScoreProviderType;

import java.util.function.Function;

/**
 * Brick Lib注册表
 * */
public class BrickRegistries {
    //原版注册表
    //? if > 1.19.2 {
    public static final VanillaRegistry<Item> ITEM = new VanillaRegistry<>(BuiltInRegistries.ITEM);
    public static final VanillaRegistry<Block> BLOCK = new VanillaRegistry<>(BuiltInRegistries.BLOCK);
    public static final VanillaRegistry<EntityType<?>> ENTITY_TYPE = new VanillaRegistry<>(BuiltInRegistries.ENTITY_TYPE);
    public static final VanillaRegistry<BlockEntityType<?>> BLOCK_ENTITY_TYPE = new VanillaRegistry<>(BuiltInRegistries.BLOCK_ENTITY_TYPE);
    public static final VanillaRegistry<? extends Registry<?>> REGISTRY = new VanillaRegistry<>(BuiltInRegistries.REGISTRY);
    public static final VanillaRegistry<GameEvent> GAME_EVENT = new VanillaRegistry<>(BuiltInRegistries.GAME_EVENT);
    public static final VanillaRegistry<SoundEvent> SOUND_EVENT = new VanillaRegistry<>(BuiltInRegistries.SOUND_EVENT);
    public static final VanillaRegistry<Fluid> FLUID = new VanillaRegistry<>(BuiltInRegistries.FLUID);
    public static final VanillaRegistry<MobEffect> MOB_EFFECT = new VanillaRegistry<>(BuiltInRegistries.MOB_EFFECT);
    //? if >= 1.21 {
    /*public static final BrickRegistry<Enchantment> ENCHANTMENT = placeholder("enchantment_placeholder");
     *///?} else {
    public static final VanillaRegistry<Enchantment> ENCHANTMENT = new VanillaRegistry<>(BuiltInRegistries.ENCHANTMENT);
    //?}
    public static final VanillaRegistry<Potion> POTION = new VanillaRegistry<>(BuiltInRegistries.POTION);
    public static final VanillaRegistry<ParticleType<?>> PARTICLE_TYPE = new VanillaRegistry<>(BuiltInRegistries.PARTICLE_TYPE);
    //? if >= 1.21 {
    /*public static final BrickRegistry<PaintingVariant> PAINTING_VARIANT = placeholder("painting_variant_placeholder");
     *///?} else {
    public static final VanillaRegistry<PaintingVariant> PAINTING_VARIANT = new VanillaRegistry<>(BuiltInRegistries.PAINTING_VARIANT);
    //?}
    public static final VanillaRegistry<ResourceLocation> CUSTOM_STAT = new VanillaRegistry<>(BuiltInRegistries.CUSTOM_STAT);
    //? if <= 1.20.4 {
    public static final VanillaRegistry<ChunkStatus> CHUNK_STATUS = new VanillaRegistry<>(BuiltInRegistries.CHUNK_STATUS);
    //?} else {
    /*public static final BrickRegistry<ChunkStatus> CHUNK_STATUS = placeholder("chunk_status_placeholder");
     *///?}
    public static final VanillaRegistry<RuleTestType<?>> RULE_TEST = new VanillaRegistry<>(BuiltInRegistries.RULE_TEST);
    //? if > 1.19.4 {
    public static final VanillaRegistry<RuleBlockEntityModifierType<?>> RULE_BLOCK_ENTITY_MODIFIER = new VanillaRegistry<>(BuiltInRegistries.RULE_BLOCK_ENTITY_MODIFIER);
     //?} else {
    /*public static final BrickRegistry<Object> RULE_BLOCK_ENTITY_MODIFIER = placeholder("rule_block_entity_modifier");
    *///?}
    public static final VanillaRegistry<PosRuleTestType<?>> POS_RULE_TEST = new VanillaRegistry<>(BuiltInRegistries.POS_RULE_TEST);
    public static final VanillaRegistry<MenuType<?>> MENU = new VanillaRegistry<>(BuiltInRegistries.MENU);
    public static final VanillaRegistry<RecipeType<?>> RECIPE_TYPE = new VanillaRegistry<>(BuiltInRegistries.RECIPE_TYPE);
    public static final VanillaRegistry<RecipeSerializer<?>> RECIPE_SERIALIZER = new VanillaRegistry<>(BuiltInRegistries.RECIPE_SERIALIZER);
    public static final VanillaRegistry<Attribute> ATTRIBUTE = new VanillaRegistry<>(BuiltInRegistries.ATTRIBUTE);
    public static final VanillaRegistry<PositionSourceType<?>> POSITION_SOURCE_TYPE = new VanillaRegistry<>(BuiltInRegistries.POSITION_SOURCE_TYPE);
    public static final VanillaRegistry<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPE = new VanillaRegistry<>(BuiltInRegistries.COMMAND_ARGUMENT_TYPE);
    public static final VanillaRegistry<StatType<?>> STAT_TYPE = new VanillaRegistry<>(BuiltInRegistries.STAT_TYPE);
    public static final VanillaRegistry<VillagerType> VILLAGER_TYPE = new VanillaRegistry<>(BuiltInRegistries.VILLAGER_TYPE);
    public static final VanillaRegistry<VillagerProfession> VILLAGER_PROFESSION = new VanillaRegistry<>(BuiltInRegistries.VILLAGER_PROFESSION);
    public static final VanillaRegistry<PoiType> POINT_OF_INTEREST_TYPE = new VanillaRegistry<>(BuiltInRegistries.POINT_OF_INTEREST_TYPE);
    public static final VanillaRegistry<MemoryModuleType<?>> MEMORY_MODULE_TYPE = new VanillaRegistry<>(BuiltInRegistries.MEMORY_MODULE_TYPE);
    public static final VanillaRegistry<SensorType<?>> SENSOR_TYPE = new VanillaRegistry<>(BuiltInRegistries.SENSOR_TYPE);
    public static final VanillaRegistry<Schedule> SCHEDULE = new VanillaRegistry<>(BuiltInRegistries.SCHEDULE);
    public static final VanillaRegistry<Activity> ACTIVITY = new VanillaRegistry<>(BuiltInRegistries.ACTIVITY);
    public static final VanillaRegistry<LootPoolEntryType> LOOT_POOL_ENTRY_TYPE = new VanillaRegistry<>(BuiltInRegistries.LOOT_POOL_ENTRY_TYPE);
    //? if <=1.20.4 {
    public static final VanillaRegistry<LootItemFunctionType> LOOT_FUNCTION_TYPE = new VanillaRegistry<>(BuiltInRegistries.LOOT_FUNCTION_TYPE);
    //?} else {
    /*public static final BrickRegistry<LootItemFunctionType<?>> LOOT_FUNCTION_TYPE = placeholder("loot_function_type");
     *///?}
    public static final VanillaRegistry<LootItemConditionType> LOOT_CONDITION_TYPE = new VanillaRegistry<>(BuiltInRegistries.LOOT_CONDITION_TYPE);
    public static final VanillaRegistry<LootNumberProviderType> LOOT_NUMBER_PROVIDER_TYPE = new VanillaRegistry<>(BuiltInRegistries.LOOT_NUMBER_PROVIDER_TYPE);
    public static final VanillaRegistry<LootNbtProviderType> LOOT_NBT_PROVIDER_TYPE = new VanillaRegistry<>(BuiltInRegistries.LOOT_NBT_PROVIDER_TYPE);
    public static final VanillaRegistry<LootScoreProviderType> LOOT_SCORE_PROVIDER_TYPE = new VanillaRegistry<>(BuiltInRegistries.LOOT_SCORE_PROVIDER_TYPE);
    public static final VanillaRegistry<FloatProviderType<?>> FLOAT_PROVIDER_TYPE = new VanillaRegistry<>(BuiltInRegistries.FLOAT_PROVIDER_TYPE);
    public static final VanillaRegistry<IntProviderType<?>> INT_PROVIDER_TYPE = new VanillaRegistry<>(BuiltInRegistries.INT_PROVIDER_TYPE);
    public static final VanillaRegistry<HeightProviderType<?>> HEIGHT_PROVIDER_TYPE = new VanillaRegistry<>(BuiltInRegistries.HEIGHT_PROVIDER_TYPE);
    public static final VanillaRegistry<BlockPredicateType<?>> BLOCK_PREDICATE_TYPE = new VanillaRegistry<>(BuiltInRegistries.BLOCK_PREDICATE_TYPE);
    public static final VanillaRegistry<WorldCarver<?>> CARVER = new VanillaRegistry<>(BuiltInRegistries.CARVER);
    public static final VanillaRegistry<Feature<?>> FEATURE = new VanillaRegistry<>(BuiltInRegistries.FEATURE);
    public static final VanillaRegistry<StructurePlacementType<?>> STRUCTURE_PLACEMENT = new VanillaRegistry<>(BuiltInRegistries.STRUCTURE_PLACEMENT);
    public static final VanillaRegistry<StructurePieceType> STRUCTURE_PIECE = new VanillaRegistry<>(BuiltInRegistries.STRUCTURE_PIECE);
    public static final VanillaRegistry<StructureType<?>> STRUCTURE_TYPE = new VanillaRegistry<>(BuiltInRegistries.STRUCTURE_TYPE);
    public static final VanillaRegistry<PlacementModifierType<?>> PLACEMENT_MODIFIER_TYPE = new VanillaRegistry<>(BuiltInRegistries.PLACEMENT_MODIFIER_TYPE);
    public static final VanillaRegistry<BlockStateProviderType<?>> BLOCKSTATE_PROVIDER_TYPE = new VanillaRegistry<>(BuiltInRegistries.BLOCKSTATE_PROVIDER_TYPE);
    public static final VanillaRegistry<FoliagePlacerType<?>> FOLIAGE_PLACER_TYPE = new VanillaRegistry<>(BuiltInRegistries.FOLIAGE_PLACER_TYPE);
    public static final VanillaRegistry<TrunkPlacerType<?>> TRUNK_PLACER_TYPE = new VanillaRegistry<>(BuiltInRegistries.TRUNK_PLACER_TYPE);
    public static final VanillaRegistry<RootPlacerType<?>> ROOT_PLACER_TYPE = new VanillaRegistry<>(BuiltInRegistries.ROOT_PLACER_TYPE);
    public static final VanillaRegistry<TreeDecoratorType<?>> TREE_DECORATOR_TYPE = new VanillaRegistry<>(BuiltInRegistries.TREE_DECORATOR_TYPE);
    public static final VanillaRegistry<FeatureSizeType<?>> FEATURE_SIZE_TYPE = new VanillaRegistry<>(BuiltInRegistries.FEATURE_SIZE_TYPE);
    //? if <= 1.20.4 {
    public static final VanillaRegistry<Codec<? extends BiomeSource>> BIOME_SOURCE = new VanillaRegistry<>(BuiltInRegistries.BIOME_SOURCE);
    public static final VanillaRegistry<Codec<? extends ChunkGenerator>> CHUNK_GENERATOR = new VanillaRegistry<>(BuiltInRegistries.CHUNK_GENERATOR);
    public static final VanillaRegistry<Codec<? extends SurfaceRules.ConditionSource>> MATERIAL_CONDITION = new VanillaRegistry<>(BuiltInRegistries.MATERIAL_CONDITION);
    public static final VanillaRegistry<Codec<? extends SurfaceRules.RuleSource>> MATERIAL_RULE = new VanillaRegistry<>(BuiltInRegistries.MATERIAL_RULE);
    public static final VanillaRegistry<Codec<? extends DensityFunction>> DENSITY_FUNCTION_TYPE = new VanillaRegistry<>(BuiltInRegistries.DENSITY_FUNCTION_TYPE);
    //?} else {
        /*public static final VanillaRegistry<MapCodec<? extends BiomeSource>> BIOME_SOURCE = new VanillaRegistry<>(BuiltInRegistries.BIOME_SOURCE);
        public static final VanillaRegistry<MapCodec<? extends ChunkGenerator>> CHUNK_GENERATOR = new VanillaRegistry<>(BuiltInRegistries.CHUNK_GENERATOR);
        public static final VanillaRegistry<MapCodec<? extends SurfaceRules.ConditionSource>> MATERIAL_CONDITION = new VanillaRegistry<>(BuiltInRegistries.MATERIAL_CONDITION);
        public static final VanillaRegistry<MapCodec<? extends SurfaceRules.RuleSource>> MATERIAL_RULE = new VanillaRegistry<>(BuiltInRegistries.MATERIAL_RULE);
        public static final VanillaRegistry<MapCodec<? extends DensityFunction>> DENSITY_FUNCTION_TYPE = new VanillaRegistry<>(BuiltInRegistries.DENSITY_FUNCTION_TYPE);
        *///?}
    public static final VanillaRegistry<StructureProcessorType<?>> STRUCTURE_PROCESSOR = new VanillaRegistry<>(BuiltInRegistries.STRUCTURE_PROCESSOR);
    public static final VanillaRegistry<StructurePoolElementType<?>> STRUCTURE_POOL_ELEMENT = new VanillaRegistry<>(BuiltInRegistries.STRUCTURE_POOL_ELEMENT);
    //? if >= 1.21.5 {
        /*public static final BrickRegistry<CatVariant> CAT_VARIANT = placeholder("cat_variant");
        public static final BrickRegistry<FrogVariant> FROG_VARIANT = placeholder("frog_variant");
        *///?} else {
    public static final VanillaRegistry<CatVariant> CAT_VARIANT = new VanillaRegistry<>(BuiltInRegistries.CAT_VARIANT);
    public static final VanillaRegistry<FrogVariant> FROG_VARIANT = new VanillaRegistry<>(BuiltInRegistries.FROG_VARIANT);
    //?}
    //? if <= 1.20.4 {
    public static final VanillaRegistry<BannerPattern> BANNER_PATTERN = new VanillaRegistry<>(BuiltInRegistries.BANNER_PATTERN);
    //?} else {
    /*public static final BrickRegistry<BannerPattern> BANNER_PATTERN = placeholder("banner_pattern");
     *///?}
    //? if >= 1.21.2 {
    /*public static final BrickRegistry<Instrument> INSTRUMENT = placeholder("instrument");
     *///?} else {
    public static final VanillaRegistry<Instrument> INSTRUMENT = new VanillaRegistry<>(BuiltInRegistries.INSTRUMENT);
    //?}
    //? if >= 1.21 {
    /*public static final BrickRegistry<String> DECORATED_POT_PATTERNS = placeholder("decorated_pot_patterns");
     *///?} else {
    public static final VanillaRegistry<String> DECORATED_POT_PATTERNS = new VanillaRegistry<>(BuiltInRegistries.DECORATED_POT_PATTERNS);
    //?}
    //? if > 1.19.4 {
    public static final VanillaRegistry<CreativeModeTab> CREATIVE_MODE_TAB = new VanillaRegistry<>(BuiltInRegistries.CREATIVE_MODE_TAB);
     //?} else {
    /*public static final BrickRegistry<CreativeModeTab> CREATIVE_MODE_TAB = placeholder("creative_mode_tab");
    *///?}
    //? if >= 1.20.6 {
    /*public static final VanillaRegistry<DataComponentType<?>> DATA_COMPONENT_TYPE = new VanillaRegistry<>(BuiltInRegistries.DATA_COMPONENT_TYPE);
     *///?} else {
    public static final BrickRegistry<Object> DATA_COMPONENT_TYPE = placeholder("data_component_type");
    //?}
    //?} else {
    /*public static final VanillaRegistry<Item> ITEM = new VanillaRegistry<>(Registry.ITEM);
    public static final VanillaRegistry<Block> BLOCK = new VanillaRegistry<>(Registry.BLOCK);
    public static final VanillaRegistry<EntityType<?>> ENTITY_TYPE = new VanillaRegistry<>(Registry.ENTITY_TYPE);
    public static final VanillaRegistry<BlockEntityType<?>> BLOCK_ENTITY_TYPE = new VanillaRegistry<>(Registry.BLOCK_ENTITY_TYPE);
    public static final VanillaRegistry<? extends Registry<?>> REGISTRY = new VanillaRegistry<>(Registry.REGISTRY);
    public static final VanillaRegistry<GameEvent> GAME_EVENT = new VanillaRegistry<>(Registry.GAME_EVENT);
    public static final VanillaRegistry<SoundEvent> SOUND_EVENT = new VanillaRegistry<>(Registry.SOUND_EVENT);
    public static final VanillaRegistry<Fluid> FLUID = new VanillaRegistry<>(Registry.FLUID);
    public static final VanillaRegistry<MobEffect> MOB_EFFECT = new VanillaRegistry<>(Registry.MOB_EFFECT);
    public static final VanillaRegistry<Enchantment> ENCHANTMENT = new VanillaRegistry<>(Registry.ENCHANTMENT);
    public static final VanillaRegistry<Potion> POTION = new VanillaRegistry<>(Registry.POTION);
    public static final VanillaRegistry<ParticleType<?>> PARTICLE_TYPE = new VanillaRegistry<>(Registry.PARTICLE_TYPE);
    //? if > 1.18.2 {
    public static final VanillaRegistry<PaintingVariant> PAINTING_VARIANT = new VanillaRegistry<>(Registry.PAINTING_VARIANT);
    public static final VanillaRegistry<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPE = new VanillaRegistry<>(Registry.COMMAND_ARGUMENT_TYPE);
    //?} else {
    /^public static final PlaceHolderRegistry<Object> PAINTING_VARIANT = placeholder("painting_variant");
    public static final PlaceHolderRegistry<Object> COMMAND_ARGUMENT_TYPE = placeholder("command_argument_type");
    ^///?}
    public static final VanillaRegistry<ResourceLocation> CUSTOM_STAT = new VanillaRegistry<>(Registry.CUSTOM_STAT);
    public static final VanillaRegistry<ChunkStatus> CHUNK_STATUS = new VanillaRegistry<>(Registry.CHUNK_STATUS);
    public static final VanillaRegistry<RuleTestType<?>> RULE_TEST = new VanillaRegistry<>(Registry.RULE_TEST);
    public static final BrickRegistry<Object> RULE_BLOCK_ENTITY_MODIFIER = placeholder("rule_block_entity_modifier");
    public static final VanillaRegistry<PosRuleTestType<?>> POS_RULE_TEST = new VanillaRegistry<>(Registry.POS_RULE_TEST);
    public static final VanillaRegistry<MenuType<?>> MENU = new VanillaRegistry<>(Registry.MENU);
    public static final VanillaRegistry<RecipeType<?>> RECIPE_TYPE = new VanillaRegistry<>(Registry.RECIPE_TYPE);
    public static final VanillaRegistry<RecipeSerializer<?>> RECIPE_SERIALIZER = new VanillaRegistry<>(Registry.RECIPE_SERIALIZER);
    public static final VanillaRegistry<Attribute> ATTRIBUTE = new VanillaRegistry<>(Registry.ATTRIBUTE);
    public static final VanillaRegistry<PositionSourceType<?>> POSITION_SOURCE_TYPE = new VanillaRegistry<>(Registry.POSITION_SOURCE_TYPE);
    public static final VanillaRegistry<StatType<?>> STAT_TYPE = new VanillaRegistry<>(Registry.STAT_TYPE);
    public static final VanillaRegistry<VillagerType> VILLAGER_TYPE = new VanillaRegistry<>(Registry.VILLAGER_TYPE);
    public static final VanillaRegistry<VillagerProfession> VILLAGER_PROFESSION = new VanillaRegistry<>(Registry.VILLAGER_PROFESSION);
    public static final VanillaRegistry<PoiType> POINT_OF_INTEREST_TYPE = new VanillaRegistry<>(Registry.POINT_OF_INTEREST_TYPE);
    public static final VanillaRegistry<MemoryModuleType<?>> MEMORY_MODULE_TYPE = new VanillaRegistry<>(Registry.MEMORY_MODULE_TYPE);
    public static final VanillaRegistry<SensorType<?>> SENSOR_TYPE = new VanillaRegistry<>(Registry.SENSOR_TYPE);
    public static final VanillaRegistry<Schedule> SCHEDULE = new VanillaRegistry<>(Registry.SCHEDULE);
    public static final VanillaRegistry<Activity> ACTIVITY = new VanillaRegistry<>(Registry.ACTIVITY);
    public static final VanillaRegistry<LootPoolEntryType> LOOT_POOL_ENTRY_TYPE = new VanillaRegistry<>(Registry.LOOT_POOL_ENTRY_TYPE);
    public static final VanillaRegistry<LootItemFunctionType> LOOT_FUNCTION_TYPE = new VanillaRegistry<>(Registry.LOOT_FUNCTION_TYPE);
    public static final VanillaRegistry<LootItemConditionType> LOOT_CONDITION_TYPE = new VanillaRegistry<>(Registry.LOOT_CONDITION_TYPE);
    public static final VanillaRegistry<LootNumberProviderType> LOOT_NUMBER_PROVIDER_TYPE = new VanillaRegistry<>(Registry.LOOT_NUMBER_PROVIDER_TYPE);
    public static final VanillaRegistry<LootNbtProviderType> LOOT_NBT_PROVIDER_TYPE = new VanillaRegistry<>(Registry.LOOT_NBT_PROVIDER_TYPE);
    public static final VanillaRegistry<LootScoreProviderType> LOOT_SCORE_PROVIDER_TYPE = new VanillaRegistry<>(Registry.LOOT_SCORE_PROVIDER_TYPE);
    public static final VanillaRegistry<FloatProviderType<?>> FLOAT_PROVIDER_TYPE = new VanillaRegistry<>(Registry.FLOAT_PROVIDER_TYPES);
    public static final VanillaRegistry<IntProviderType<?>> INT_PROVIDER_TYPE = new VanillaRegistry<>(Registry.INT_PROVIDER_TYPES);
    public static final VanillaRegistry<HeightProviderType<?>> HEIGHT_PROVIDER_TYPE = new VanillaRegistry<>(Registry.HEIGHT_PROVIDER_TYPES);
    public static final VanillaRegistry<BlockPredicateType<?>> BLOCK_PREDICATE_TYPE = new VanillaRegistry<>(Registry.BLOCK_PREDICATE_TYPES);
    public static final VanillaRegistry<WorldCarver<?>> CARVER = new VanillaRegistry<>(Registry.CARVER);
    public static final VanillaRegistry<Feature<?>> FEATURE = new VanillaRegistry<>(Registry.FEATURE);
    public static final VanillaRegistry<StructurePlacementType<?>> STRUCTURE_PLACEMENT = new VanillaRegistry<>(Registry.STRUCTURE_PLACEMENT_TYPE);
    public static final VanillaRegistry<StructurePieceType> STRUCTURE_PIECE = new VanillaRegistry<>(Registry.STRUCTURE_PIECE);
    //? if > 1.18.2 {
    public static final VanillaRegistry<StructureType<?>> STRUCTURE_TYPE = new VanillaRegistry<>(Registry.STRUCTURE_TYPES);
    //?} else {
    /^public static final BrickRegistry<Object> STRUCTURE_TYPE = placeholder("structure_type");
    ^///?}
    public static final VanillaRegistry<PlacementModifierType<?>> PLACEMENT_MODIFIER_TYPE = new VanillaRegistry<>(Registry.PLACEMENT_MODIFIERS);
    public static final VanillaRegistry<BlockStateProviderType<?>> BLOCKSTATE_PROVIDER_TYPE = new VanillaRegistry<>(Registry.BLOCKSTATE_PROVIDER_TYPES);
    public static final VanillaRegistry<FoliagePlacerType<?>> FOLIAGE_PLACER_TYPE = new VanillaRegistry<>(Registry.FOLIAGE_PLACER_TYPES);
    public static final VanillaRegistry<TrunkPlacerType<?>> TRUNK_PLACER_TYPE = new VanillaRegistry<>(Registry.TRUNK_PLACER_TYPES);
    //? if > 1.18.2 {
    public static final VanillaRegistry<RootPlacerType<?>> ROOT_PLACER_TYPE = new VanillaRegistry<>(Registry.ROOT_PLACER_TYPES);
    //?} else {
    /^public static final BrickRegistry<Object> ROOT_PLACER_TYPE = placeholder("root_placer_type");
    ^///?}
    public static final VanillaRegistry<TreeDecoratorType<?>> TREE_DECORATOR_TYPE = new VanillaRegistry<>(Registry.TREE_DECORATOR_TYPES);
    public static final VanillaRegistry<FeatureSizeType<?>> FEATURE_SIZE_TYPE = new VanillaRegistry<>(Registry.FEATURE_SIZE_TYPES);
    public static final VanillaRegistry<Codec<? extends BiomeSource>> BIOME_SOURCE = new VanillaRegistry<>(Registry.BIOME_SOURCE);
    public static final VanillaRegistry<Codec<? extends ChunkGenerator>> CHUNK_GENERATOR = new VanillaRegistry<>(Registry.CHUNK_GENERATOR);
    public static final VanillaRegistry<Codec<? extends SurfaceRules.ConditionSource>> MATERIAL_CONDITION = new VanillaRegistry<>(Registry.CONDITION);
    public static final VanillaRegistry<Codec<? extends SurfaceRules.RuleSource>> MATERIAL_RULE = new VanillaRegistry<>(Registry.RULE);
    public static final VanillaRegistry<Codec<? extends DensityFunction>> DENSITY_FUNCTION_TYPE = new VanillaRegistry<>(Registry.DENSITY_FUNCTION_TYPES);
    public static final VanillaRegistry<StructureProcessorType<?>> STRUCTURE_PROCESSOR = new VanillaRegistry<>(Registry.STRUCTURE_PROCESSOR);
    public static final VanillaRegistry<StructurePoolElementType<?>> STRUCTURE_POOL_ELEMENT = new VanillaRegistry<>(Registry.STRUCTURE_POOL_ELEMENT);
    //? if > 1.18.2 {
    public static final VanillaRegistry<CatVariant> CAT_VARIANT = new VanillaRegistry<>(Registry.CAT_VARIANT);
    public static final VanillaRegistry<FrogVariant> FROG_VARIANT = new VanillaRegistry<>(Registry.FROG_VARIANT);
    public static final VanillaRegistry<BannerPattern> BANNER_PATTERN = new VanillaRegistry<>(Registry.BANNER_PATTERN);
    public static final VanillaRegistry<Instrument> INSTRUMENT = new VanillaRegistry<>(Registry.INSTRUMENT);
    //?} else {
    /^public static final PlaceHolderRegistry<Object> CAT_VARIANT = placeholder("cat_variant");
    public static final PlaceHolderRegistry<Object> FROG_VARIANT = placeholder("frog_variant");
    public static final BrickRegistry<BannerPattern> BANNER_PATTERN = create("banner_pattern");
    public static final PlaceHolderRegistry<Object> INSTRUMENT = placeholder("instrument");
    ^///?}
    public static final BrickRegistry<String> DECORATED_POT_PATTERNS = placeholder("decorated_pot_patterns");
    public static final BrickRegistry<CreativeModeTab> CREATIVE_MODE_TAB = placeholder("creative_mode_tab");
    public static final BrickRegistry<Object> DATA_COMPONENT_TYPE = placeholder("data_component_type");
    *///?}

    ///////////////////////////Brick Lib额外注册表
    /**
     * 键位注册表
     * */
    public static final BrickRegistry<KeyMapping> KEY_MAPPING = createAutoClean("key_mapping");
    /**
     * 网络包注册表
     * */
    public static final BrickRegistry<PacketConfig> NETWORK_PACKET = createAutoClean("network_packet");
    /**
     * 命令注册表
     * */
    public static final BrickRegistry<Function<CommandBuildContext,LiteralArgumentBuilder<CommandSourceStack>>> COMMAND = create("command");
    /**
     * 客户端命令注册表
     * */
    public static final BrickRegistry<Function<CommandBuildContext,LiteralArgumentBuilder<ClientSuggestionProvider>>> CLIENT_COMMAND = create("client_command");
    /**
     * JSON函数注册表
     * */
    public static final BrickRegistry<JsonFunction> JSON_FUNCTION = create("json_function");

    /**
     * 数据生成注册表
     * */
    public static final BrickRegistry<DataGenerateEntry> DATA_GENERATE = create("data_generate");

    /**
     * 更新检查器注册表
     * */
    public static final BrickRegistry<UpdateChecker.Entry> UPDATE_CHECK = create("update_check");

    /**
     * 配置注册表
     * */
    public static final BrickRegistry<ModConfig> CONFIG = create("config");

    /**
     * 村民交易注册表
     * */
    public static final BrickRegistry<VillagerTradeEntry> VILLAGER_TRADE = createAutoClean("villager_trade");

    /**
     * 流浪商人交易注册表
     * */
    public static final BrickRegistry<VillagerTradeEntry> WANDERING_TRADE = createAutoClean("villager_trade");


    private static <T> BrickRegistry<T> create(String name) {
        return new BrickRegistry<>(ResourceKey.createRegistryKey(BrickLibAPI.ofPath(name)));
    }

    private static <T> BrickRegistry<T> createAutoClean(String name) {
        return new BrickRegistry<>(ResourceKey.createRegistryKey(BrickLibAPI.ofPath(name)),true);
    }

    private static <T> PlaceHolderRegistry<T> placeholder(String name) {
        return new PlaceHolderRegistry<>(ResourceKey.createRegistryKey(BrickLibAPI.ofPath(name)));
    }

}
