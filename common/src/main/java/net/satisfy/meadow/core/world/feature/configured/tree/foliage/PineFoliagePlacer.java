package net.satisfy.meadow.core.world.feature.configured.tree.foliage;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
            ).and(
                    IntProvider.codec(0, 24)
                            .fieldOf("height")
                            .forGetter(placer -> placer.height)
            ).apply(instance, PineFoliagePlacer::new)
    );

    private final IntProvider trunkHeight;
    private final IntProvider height;

    public PineFoliagePlacer(IntProvider radius, IntProvider offset, IntProvider trunkHeight, IntProvider height) {
        super(radius, offset);
        this.trunkHeight = trunkHeight;
        this.height = height;
    }

    @Override
    protected @NotNull FoliagePlacerType<?> type() {
        return PlacerTypeRegistry.PINE_FOLIAGE_PLACER.get();
    }

    @Override
    protected void createFoliage(@NotNull LevelSimulatedReader level, @NotNull FoliageSetter setter, @NotNull RandomSource random, @NotNull TreeConfiguration config, int trunkHeight, @NotNull FoliageAttachment attachment, int foliageHeight, int radius, int topOffset) {
        BlockPos anchor = attachment.pos();
        BlockPos.MutableBlockPos mutablePos = anchor.mutable();

        topOffset -= 1;

        int[] radii = new int[]{0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 2, 1};
        int layers = radii.length;
        int topEnd = topOffset - (layers - 1);

        for (int y = topOffset; y >= topEnd; y--) {
            int layerIndex = topOffset - y;
            int ringRadius = radii[layerIndex];

            if (ringRadius == 0) {
                mutablePos.setWithOffset(anchor, 0, y, 0);
                tryPlaceLeaf(level, setter, random, config, mutablePos);

                if (layerIndex == 1) {
                    if (random.nextInt(3) != 0) {
                        mutablePos.setWithOffset(anchor, 1, y, 0);
                        tryPlaceLeaf(level, setter, random, config, mutablePos);
                    }
                    if (random.nextInt(3) != 0) {
                        mutablePos.setWithOffset(anchor, -1, y, 0);
                        tryPlaceLeaf(level, setter, random, config, mutablePos);
                    }
                    if (random.nextInt(3) != 0) {
                        mutablePos.setWithOffset(anchor, 0, y, 1);
                        tryPlaceLeaf(level, setter, random, config, mutablePos);
                    }
                    if (random.nextInt(3) != 0) {
                        mutablePos.setWithOffset(anchor, 0, y, -1);
                        tryPlaceLeaf(level, setter, random, config, mutablePos);
                    }
                }

                if (layerIndex == 2 && random.nextInt(4) == 0) {
                    Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                    Direction secondDirection = random.nextBoolean() ? direction.getClockWise() : direction.getCounterClockWise();
                    mutablePos.setWithOffset(anchor, direction.getStepX() + secondDirection.getStepX(), y, direction.getStepZ() + secondDirection.getStepZ());
                    tryPlaceLeaf(level, setter, random, config, mutablePos);
                }

                if (layerIndex <= 2 && random.nextInt(4) == 0) {
                    Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                    Direction secondDirection = random.nextBoolean() ? direction.getClockWise() : direction.getCounterClockWise();
                    tryPlaceLeaf(level, setter, random, config, mutablePos.relative(direction, 1).relative(secondDirection, 1));
                }

                continue;
            }

            placeRing(level, setter, random, config, anchor, ringRadius, y, attachment.doubleTrunk(), layerIndex, random);
        }

        int radiusStep = random.nextInt(2);
        int maxRadius = 1;
        int previousRadius = 0;

        int bottomLimit = -2;
        int lowestRelativeY = Math.max(-foliageHeight, bottomLimit);

        for (int relativeY = topEnd - 1; relativeY >= lowestRelativeY; --relativeY) {
            placeLeavesRow(level, setter, random, config, anchor, radiusStep, relativeY, attachment.doubleTrunk());
            if (radiusStep >= maxRadius) {
                radiusStep = previousRadius;
                previousRadius = 1;
                maxRadius = Math.min(maxRadius + 1, radius + attachment.radiusOffset());
            } else {
                ++radiusStep;
            }
        }
    }

    private void placeRing(LevelSimulatedReader level, FoliageSetter setter, RandomSource random, TreeConfiguration config, BlockPos center, int ringRadius, int relativeY, boolean doubleTrunk, int layerIndex, RandomSource rng) {
        int inflate = doubleTrunk ? 1 : 0;
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        boolean thinRing = ringRadius == 1;
        boolean midRing = ringRadius == 2;
        boolean forceCardinals = layerIndex <= 4;
        int skipOdds = thinRing ? 4 : (midRing ? 6 : 7);

        for (int offsetX = -ringRadius; offsetX <= ringRadius + inflate; offsetX++) {
            for (int offsetZ = -ringRadius; offsetZ <= ringRadius + inflate; offsetZ++) {
                if (offsetX == 0 && offsetZ == 0) {
                    continue;
                }

                int absX = Math.abs(offsetX);
                int absZ = Math.abs(offsetZ);
                if ((float) (absX * absX + absZ * absZ) > (ringRadius + 0.25F) * (ringRadius + 0.25F)) {
                    continue;
                }

                boolean nearTrunk = absX <= 1 && absZ <= 1;
                if (nearTrunk) {
                    mutablePos.setWithOffset(center, offsetX, relativeY, offsetZ);
                    tryPlaceLeaf(level, setter, random, config, mutablePos);
                    continue;
                }

                boolean cardinal = absX + absZ == 1;

                if (!forceCardinals) {
                    if (thinRing) {
                        if (cardinal && rng.nextInt(skipOdds + 1) == 0) {
                            continue;
                        }
                        if (absX == 1 && absZ == 1 && rng.nextInt(skipOdds + 2) == 0) {
                            continue;
                        }
                    } else if (midRing) {
                        if ((absX == ringRadius || absZ == ringRadius) && rng.nextInt(skipOdds + 1) == 0) {
                            continue;
                        }
                    } else {
                        if ((absX == ringRadius && absZ == ringRadius) || rng.nextInt(skipOdds + 2) == 0) {
                            continue;
                        }
                    }
                } else if (!cardinal && thinRing && rng.nextInt(skipOdds + 2) == 0) {
                    continue;
                }

                mutablePos.setWithOffset(center, offsetX, relativeY, offsetZ);
                tryPlaceLeaf(level, setter, random, config, mutablePos);
            }
        }
    }

    @Override
    public int foliageHeight(@NotNull RandomSource random, int height, @NotNull TreeConfiguration config) {
        int sampled = height - this.trunkHeight.sample(random);
        return Math.max(4, sampled - 2);
    }

    @Override
    protected boolean shouldSkipLocation(@NotNull RandomSource random, int localX, int localY, int localZ, int range, boolean large) {
        return localX == range && localZ == range && range > 0;
    }
}