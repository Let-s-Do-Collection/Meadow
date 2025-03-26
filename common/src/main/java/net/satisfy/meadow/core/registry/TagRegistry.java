package net.satisfy.meadow.core.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.satisfy.meadow.core.util.MeadowIdentifier;

public class TagRegistry {
    public static final TagKey<Biome> IS_MEADOW = TagKey.create(Registries.BIOME, new MeadowIdentifier("is_meadow"));
    public static final TagKey<Biome> SPAWNS_UMBRA_COW = TagKey.create(Registries.BIOME, new MeadowIdentifier("spawns_umbra_cows"));
    public static final TagKey<Biome> SPAWNS_ROCKY_SHEEP = TagKey.create(Registries.BIOME, new MeadowIdentifier("spawns_rocky_sheep"));
    public static final TagKey<Biome> SPAWNS_WARPED_COW = TagKey.create(Registries.BIOME, new MeadowIdentifier("spawns_warped_cow"));
    public static final TagKey<Item> MILK = TagKey.create(Registries.ITEM, new MeadowIdentifier("milk"));
    public static final TagKey<Item> CHEESE_BLOCKS = TagKey.create(Registries.ITEM, new MeadowIdentifier("cheese_blocks"));
    public static final TagKey<Item> CHEESE = TagKey.create(Registries.ITEM, new MeadowIdentifier("cheese"));
    public static final TagKey<Block> ALLOWS_COOKING = TagKey.create(Registries.BLOCK, new MeadowIdentifier("allows_cooking"));
    public static final TagKey<Item> WOODEN_MILK_BUCKET = TagKey.create(Registries.ITEM, new MeadowIdentifier("wooden_milk_bucket"));
    public static final TagKey<Item> MILK_BUCKET = TagKey.create(Registries.ITEM, new MeadowIdentifier("milk_bucket"));
    public static final TagKey<Item> SMALL_WATER_FILL = TagKey.create(Registries.ITEM, new MeadowIdentifier("small_water_fill"));
    public static final TagKey<Item> LARGE_WATER_FILL = TagKey.create(Registries.ITEM, new MeadowIdentifier("large_water_fill"));
    public static final TagKey<Item> BREAD = TagKey.create(Registries.ITEM, new MeadowIdentifier("bread"));
    public static final TagKey<Item> SHEARS = TagKey.create(Registries.ITEM, new MeadowIdentifier("shears"));
}
