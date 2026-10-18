package net.satisfy.meadow.core.world;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.satisfy.meadow.core.registry.ObjectRegistry;
import net.satisfy.meadow.core.registry.TagRegistry;
import net.satisfy.meadow.platform.PlatformHelper;

public final class MeadowCliffs {
    private static final int ROCK_RISE = 6;
    private static final int ROCK_DEPTH = 3;
    private static final int REACH = 3;

    private static final ImprovedNoise BAND = new ImprovedNoise(new XoroshiroRandomSource(0x11E570E5L));
    private static final ImprovedNoise EDGE = new ImprovedNoise(new XoroshiroRandomSource(0xED6EL));
    private static final ImprovedNoise PATCH = new ImprovedNoise(new XoroshiroRandomSource(0x0C11FFL));

    private MeadowCliffs() {
    }

    public static void paint(ChunkAccess chunk, BiomeManager biomes) {
        if (!PlatformHelper.enableLimestoneCliffs()) return;
        int minX = chunk.getPos().getMinBlockX();
        int minZ = chunk.getPos().getMinBlockZ();
        int[] heights = new int[256];
        int[] raw = new int[256];
        for (int lx = 0; lx < 16; lx++) {
            for (int lz = 0; lz < 16; lz++) {
                heights[lx << 4 | lz] = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, lx, lz);
            }
        }
        for (int lx = 0; lx < 16; lx++) {
            for (int lz = 0; lz < 16; lz++) {
                raw[lx << 4 | lz] = rawRise(heights, lx, lz);
            }
        }
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int lx = 0; lx < 16; lx++) {
            for (int lz = 0; lz < 16; lz++) {
                int topY = heights[lx << 4 | lz];
                if (topY <= chunk.getMinBuildHeight() + 1) continue;
                int x = minX + lx;
                int z = minZ + lz;
                if (!biomes.getBiome(pos.set(x, topY, z)).is(TagRegistry.IS_MEADOW)) continue;
                double dither = (EDGE.noise(x * 0.06, 0.0, z * 0.06)) * 1.5;
                if (smoothed(raw, lx, lz) + dither < ROCK_RISE) continue;
                for (int y = topY - 1; y > topY - 1 - ROCK_DEPTH && y > chunk.getMinBuildHeight(); y--) {
                    pos.set(x, y, z);
                    if (!isTerrain(chunk.getBlockState(pos))) continue;
                    chunk.setBlockState(pos, pick(x, y, z), false);
                }
            }
        }
    }

    private static BlockState pick(int x, int y, int z) {
        double band = BAND.noise(x * 0.045, y * 0.35, z * 0.045);
        double patch = PATCH.noise(x * 0.1, y * 0.1, z * 0.1);
        if (patch > 0.55) return ObjectRegistry.COBBLED_LIMESTONE.get().defaultBlockState();
        if (band > 0.38) return Blocks.CALCITE.defaultBlockState();
        return ObjectRegistry.LIMESTONE.get().defaultBlockState();
    }

    private static int smoothed(int[] raw, int lx, int lz) {
        int sum = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                sum += raw[Math.clamp(lx + dx, 0, 15) << 4 | Math.clamp(lz + dz, 0, 15)];
            }
        }
        return Math.round(sum / 9.0F);
    }

    private static int rawRise(int[] heights, int lx, int lz) {
        int west = Math.max(0, lx - REACH);
        int east = Math.min(15, lx + REACH);
        int north = Math.max(0, lz - REACH);
        int south = Math.min(15, lz + REACH);
        int alongX = scaled(heights[east << 4 | lz] - heights[west << 4 | lz], east - west);
        int alongZ = scaled(heights[lx << 4 | south] - heights[lx << 4 | north], south - north);
        return Math.max(alongX, alongZ);
    }

    private static int scaled(int difference, int span) {
        return span == 0 ? 0 : Math.round(Math.abs(difference) * 4.0F / span);
    }

    private static boolean isTerrain(BlockState state) {
        if (state.isAir() || !state.getFluidState().isEmpty()) return false;
        return state.is(BlockTags.DIRT) || state.is(BlockTags.BASE_STONE_OVERWORLD) || state.is(Blocks.GRAVEL);
    }
}
