package net.satisfy.meadow.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.satisfy.meadow.Meadow;

public class TagRegistry {
    public static final TagKey<Biome> IS_MEADOW = TagKey.create(Registries.BIOME, Meadow.identifier("is_meadow"));
    public static final TagKey<Biome> SPAWNS_DARK_COW = TagKey.create(Registries.BIOME, Meadow.identifier("spawns_dark_cows"));
    public static final TagKey<Biome> SPAWNS_ROCKY_SHEEP = TagKey.create(Registries.BIOME, Meadow.identifier("spawns_rocky_sheep"));
    public static final TagKey<Biome> SPAWNS_WARPED_COW = TagKey.create(Registries.BIOME, Meadow.identifier("spawns_warped_cow"));
    public static final TagKey<Biome> SPAWNS_BEAR = TagKey.create(Registries.BIOME, Meadow.identifier("spawns_bear"));
    public static final TagKey<Biome> SPAWNS_SUNSET_COW = TagKey.create(Registries.BIOME, Meadow.identifier("spawns_sunset_cow"));
    public static final TagKey<Item> MILK = TagKey.create(Registries.ITEM, Meadow.identifier("milk"));
    public static final TagKey<Item> CHEESE_BLOCKS = TagKey.create(Registries.ITEM, Meadow.identifier("cheese_blocks"));
    public static final TagKey<Block> ALLOWS_COOKING = TagKey.create(Registries.BLOCK, Meadow.identifier("allows_cooking"));
    public static final TagKey<Item> WOODEN_MILK_BUCKET = TagKey.create(Registries.ITEM, Meadow.identifier("wooden_milk_bucket"));
    public static final TagKey<Item> MILK_BUCKET = TagKey.create(Registries.ITEM, Meadow.identifier("milk_bucket"));
    public static final TagKey<Item> SMALL_FLOWER = TagKey.create(Registries.ITEM, Meadow.identifier("small_flower"));
    public static final TagKey<Block> CAN_NOT_CONNECT = TagKey.create(Registries.BLOCK, Meadow.identifier("can_not_connect"));
    public static final TagKey<Item> SHOVEL = TagKey.create(Registries.ITEM, Meadow.identifier("shovel"));
}
