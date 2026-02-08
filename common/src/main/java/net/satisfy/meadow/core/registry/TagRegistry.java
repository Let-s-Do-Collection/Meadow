package net.satisfy.meadow.core.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.satisfy.meadow.Meadow;

public class TagRegistry {
    public static final TagKey<Biome> IS_MEADOW = TagKey.create(Registries.BIOME, Meadow.identifier("is_meadow"));
    public static final TagKey<Biome> SPAWNS_HIGHLAND_COW = TagKey.create(Registries.BIOME, Meadow.identifier("spawns_highland_cow"));
    public static final TagKey<Biome> SPAWNS_UMBRA_COW = TagKey.create(Registries.BIOME, Meadow.identifier("spawns_umbra_cow"));
    public static final TagKey<Biome> SPAWNS_WARPED_COW = TagKey.create(Registries.BIOME, Meadow.identifier("spawns_warped_cow"));
    public static final TagKey<Biome> SPAWNS_WATER_BUFFALO = TagKey.create(Registries.BIOME, Meadow.identifier("spawns_buffalo"));
    public static final TagKey<Item> MILK = TagKey.create(Registries.ITEM, Meadow.identifier("milk"));
    public static final TagKey<Item> CHEESE_WHEELS = TagKey.create(Registries.ITEM, Meadow.identifier("cheese_wheels"));
    public static final TagKey<Item> CHEESE = TagKey.create(Registries.ITEM, Meadow.identifier("cheese"));
    public static final TagKey<Block> ALLOWS_COOKING = TagKey.create(Registries.BLOCK, Meadow.identifier("allows_cooking"));
    public static final TagKey<Item> WOODEN_MILK_BUCKET = TagKey.create(Registries.ITEM, Meadow.identifier("wooden_milk_bucket"));
    public static final TagKey<Item> MILK_BUCKET = TagKey.create(Registries.ITEM, Meadow.identifier("milk_bucket"));
    public static final TagKey<Item> SMALL_WATER_FILL = TagKey.create(Registries.ITEM, Meadow.identifier("small_water_fill"));
    public static final TagKey<Item> LARGE_WATER_FILL = TagKey.create(Registries.ITEM, Meadow.identifier("large_water_fill"));
    public static final TagKey<Item> IS_WOODCUTTER_USABLE = TagKey.create(Registries.ITEM, Meadow.identifier("is_woodcutter_usable"));
    public static final TagKey<Item> BREAD = TagKey.create(Registries.ITEM, Meadow.identifier("bread"));
    public static final TagKey<Item> SHEARS = TagKey.create(Registries.ITEM, Meadow.identifier("shears"));

}
