package com.arc_studio.brick_lib_api.core;

import net.minecraft.resources.ResourceKey;
//? if < 1.21.5 {
/*import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;*/
//? } else {
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.npc.villager.VillagerTrades;
//? }

public record VillagerTradeEntry(
    //? if >= 1.21.5 {
    ResourceKey<VillagerProfession>
        //?} else {
    /*VillagerProfession
    *///?}
    profession,int level, VillagerTrades.ItemListing trade) {
}
