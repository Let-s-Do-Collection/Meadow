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

public class AlpineBirchFoliagePlacer extends FoliagePlacer {
    public static final MapCodec<AlpineBirchFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec(instance ->
            foliagePlacerParts(instance)
                    .and(IntProvider.codec(0, 24).fieldOf("trunk_height").forGetter(placer -> placer.trunkHeight))
                    .apply(instance, AlpineBirchFoliagePlacer::new)
    );

    private final IntProvider trunkHeight;

    public AlpineBirchFoliagePlacer(IntProvider radius, IntProvider offset, IntProvider height) {
        super(radius, offset);
        this.trunkHeight = height;
    }

    @Override
    protected @NotNull FoliagePlacerType<?> type() {
        return PlacerTypeRegistry.ALPINE_BIRCH_FOLIAGE_PLACER.get();
    }

    @Override
    protected void createFoliage(LevelSimulatedReader level, FoliageSetter foliageSetter, RandomSource random, TreeConfiguration config, int maxFreeTreeHeight, FoliageAttachment attachment, int foliageHeight, int foliageRadius, int offset) {
        BlockPos blockPos = attachment.pos();
        for (int i = 0, l = offset; l >= offset - 5; --l, ++i) {
            int j = calculateFoliageRadius(i, level, foliageSetter, random, config, attachment, l);
            placeLeavesRow(level, foliageSetter, random, config, blockPos, j, l, attachment.doubleTrunk());
        }
        for (int i = 4, l = offset - 6; l >= offset - 10; --l, --i) {
            int j = calculateFoliageRadius(i, level, foliageSetter, random, config, attachment, l);
            placeLeavesRow(level, foliageSetter, random, config, blockPos, j, l, attachment.doubleTrunk());
        }
    }

    private int calculateFoliageRadius(int iteration, LevelSimulatedReader level, FoliageSetter foliageSetter, RandomSource random, TreeConfiguration config, FoliageAttachment attachment, int offset) {
        return switch (iteration) {
            case 1, 3 -> 1;
            case 2, 4 -> {
                placeCornShape(level, foliageSetter, random, config, attachment, offset);
                yield 1;
            }
            case 5 -> 2;
            default -> 0;
        };
    }

    private void placeCornShape(LevelSimulatedReader level, FoliageSetter foliageSetter, RandomSource random, TreeConfiguration config, FoliageAttachment attachment, int offset) {
        this.placeLeavesRow(level, foliageSetter, random, config, attachment.pos().north(), 1, offset, attachment.doubleTrunk());
        this.placeLeavesRow(level, foliageSetter, random, config, attachment.pos().south(), 1, offset, attachment.doubleTrunk());
        this.placeLeavesRow(level, foliageSetter, random, config, attachment.pos().west(), 1, offset, attachment.doubleTrunk());
        this.placeLeavesRow(level, foliageSetter, random, config, attachment.pos().east(), 1, offset, attachment.doubleTrunk());
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource random, int localX, int localY, int localZ, int range, boolean large) {
        return localX == range && localZ == range && range > 0;
    }

    @Override
    public int foliageHeight(RandomSource random, int height, TreeConfiguration config) {
        return Math.max(4, height - this.trunkHeight.sample(random));
    }
}
