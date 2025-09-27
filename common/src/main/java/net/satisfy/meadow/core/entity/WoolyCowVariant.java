package net.satisfy.meadow.core.entity;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.satisfy.meadow.core.registry.ObjectRegistry;
import net.satisfy.meadow.core.registry.TagRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

public enum WoolyCowVariant implements StringRepresentable {
    HIGHLAND(0, "highland_cattle", ObjectRegistry.HIGHLAND_WOOL.get().asItem(), ObjectRegistry.WOODEN_MILK_BUCKET.get()),
    UMBRA(1, "umbra_cow", ObjectRegistry.UMBRA_WOOL.get().asItem(), ObjectRegistry.WOODEN_MILK_BUCKET.get()),
    WARPED(2, "warped_cow", ObjectRegistry.WARPED_WOOL.get().asItem(), ObjectRegistry.WOODEN_WARPED_MILK_BUCKET.get());
    private static final IntFunction<WoolyCowVariant> BY_ID = ByIdMap.continuous(WoolyCowVariant::getId, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    private final int id;
    private final String name;
    private final Item wool;
    private final Item bucket;

    WoolyCowVariant(int id, String name, Item wool, Item bucket) {
        this.id = id;
        this.name = name;
        this.wool = wool;
        this.bucket = bucket;
    }

    public Item getWool() {
        return wool;
    }

    public Item getBucket() {
        return bucket;
    }

    public int getId() {
        return this.id;
    }

    public static WoolyCowVariant byId(int i) {
        return BY_ID.apply(i);
    }

    public @NotNull String getSerializedName() {
        return this.name;
    }


    private static final Map<WoolyCowVariant, TagKey<Biome>> SPAWNS = Util.make(new HashMap<>(), map -> {
        map.put(WoolyCowVariant.HIGHLAND, TagRegistry.IS_MEADOW);
        map.put(WoolyCowVariant.UMBRA, TagRegistry.SPAWNS_UMBRA_COW);
        map.put(WoolyCowVariant.WARPED, TagRegistry.SPAWNS_WARPED_COW);
    });

    public static WoolyCowVariant getRandomVariant(LevelAccessor levelAccessor, BlockPos blockPos, boolean spawnEgg) {
        Holder<Biome> holder = levelAccessor.getBiome(blockPos);
        RandomSource random = levelAccessor.getRandom();
        List<WoolyCowVariant> possibleVars = getShearableCowVariantsInBiome(holder);
        int size = possibleVars.size();
        if (size == 0 || spawnEgg) {
            if (spawnEgg) return Util.getRandom(WoolyCowVariant.values(), random);

            if (holder.is(BiomeTags.IS_NETHER)) return WoolyCowVariant.WARPED;
            List<WoolyCowVariant> list = new java.util.ArrayList<>(List.of(WoolyCowVariant.values()));
            list.remove(WoolyCowVariant.WARPED);
            return Util.getRandom(list, random);
        }

        return possibleVars.get(levelAccessor.getRandom().nextInt(size));
    }

    private static List<WoolyCowVariant> getShearableCowVariantsInBiome(Holder<Biome> biome) {
        return SPAWNS.keySet().stream()
                .filter(ShearableCowVariant -> biome.is(SPAWNS.get(ShearableCowVariant)))
                .collect(Collectors.toList());
    }
}