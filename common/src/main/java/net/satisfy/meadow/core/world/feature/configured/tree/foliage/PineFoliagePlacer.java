package net.satisfy.meadow.core.world.feature.configured.tree.foliage;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.satisfy.meadow.core.registry.PlacerTypeRegistry;
import org.jetbrains.annotations.NotNull;

public class PineFoliagePlacer extends FoliagePlacer {
    public static final MapCodec<PineFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec(instance ->
            foliagePlacerParts(instance).and(
                    IntProvider.codec(0, 24)
                            .fieldOf("trunk_height")
                            .forGetter(placer -> placer.trunkHeight)
            ).apply(instance, PineFoliagePlacer::new)
    );

    private static final int ROWS_PER_TIER = 3;
    private static final int SPIRE_ROWS = 1;
    private static final int SPIRE_TOP = 0;
    private static final int MIN_CROWN_ROWS = 9;
    private static final float TIER_SHRINK_CHANCE = 0.25F;
    private static final float TIER_SHIFT_CHANCE = 0.3F;
    private static final float[] EDGE_DROP = {0.1F, 0.25F, 0.45F};

    private final IntProvider trunkHeight;

    public PineFoliagePlacer(IntProvider radius, IntProvider offset, IntProvider trunkHeight) {
        super(radius, offset);
        this.trunkHeight = trunkHeight;
    }

    @Override
    protected @NotNull FoliagePlacerType<?> type() {
        return PlacerTypeRegistry.PINE_FOLIAGE_PLACER.get();
    }

    @Override
    protected void createFoliage(@NotNull LevelSimulatedReader level, @NotNull FoliageSetter setter, @NotNull RandomSource random, @NotNull TreeConfiguration config, int treeHeight, @NotNull FoliageAttachment attachment, int foliageHeight, int radius, int offset) {
        BlockPos anchor = attachment.pos();
        int tierRadius = 0;
        int shiftX = 0;
        int shiftZ = 0;

        for (int row = 0; row < foliageHeight; row++) {
            int rowRadius = 0;
            float drop = 0.0F;

            if (row >= SPIRE_ROWS) {
                int body = row - SPIRE_ROWS;
                int tier = body / ROWS_PER_TIER + 1;
                int rowInTier = body % ROWS_PER_TIER;

                if (rowInTier == 0) {
                    tierRadius = Math.min(radius, tier + 1);
                    if (tierRadius > 2 && random.nextFloat() < TIER_SHRINK_CHANCE) {
                        tierRadius--;
                    }
                    boolean shifted = tierRadius >= 2 && random.nextFloat() < TIER_SHIFT_CHANCE;
                    shiftX = shifted ? random.nextInt(3) - 1 : 0;
                    shiftZ = shifted ? random.nextInt(3) - 1 : 0;
                }

                rowRadius = rowInTier == 0 ? Math.max(tier > 1 ? 2 : 1, tierRadius - 1) : tierRadius;
                drop = EDGE_DROP[rowInTier];
            }

            BlockPos center = anchor.offset(shiftX, SPIRE_TOP + offset - row, shiftZ);
            placeDisc(level, setter, random, config, center, rowRadius, drop);
        }
    }

    private void placeDisc(LevelSimulatedReader level, FoliageSetter setter, RandomSource random, TreeConfiguration config, BlockPos center, int radius, float dropChance) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        float outer = radius <= 1 ? 1.5F : (radius + 0.5F) * (radius + 0.5F);
        float inner = (radius - 0.5F) * (radius - 0.5F);

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                float distance = dx * dx + dz * dz;
                if (distance > outer) {
                    continue;
                }
                if (radius > 1 && distance > inner && random.nextFloat() < dropChance) {
                    continue;
                }
                pos.setWithOffset(center, dx, 0, dz);
                tryPlaceLeaf(level, setter, random, config, pos);
            }
        }
    }

    @Override
    public int foliageHeight(@NotNull RandomSource random, int height, @NotNull TreeConfiguration config) {
        return Math.max(MIN_CROWN_ROWS, height - this.trunkHeight.sample(random));
    }

    @Override
    protected boolean shouldSkipLocation(@NotNull RandomSource random, int localX, int localY, int localZ, int range, boolean large) {
        return false;
    }
}
