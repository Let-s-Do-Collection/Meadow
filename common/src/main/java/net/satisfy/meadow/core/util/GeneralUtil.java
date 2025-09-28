package net.satisfy.meadow.core.util;

import com.mojang.datafixers.util.Pair;
import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.meadow.core.entity.ChairEntity;
import net.satisfy.meadow.core.registry.EntityTypeRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;

public class GeneralUtil {
    public static final EnumProperty<LineConnectingType> LINE_CONNECTING_TYPE = EnumProperty.create("type", LineConnectingType.class);
    private static final Map<ResourceLocation, Map<BlockPos, Pair<ChairEntity, BlockPos>>> CHAIRS = new HashMap<>();


    public static <T extends Block> RegistrySupplier<T> registerWithItem(DeferredRegister<Block> registerB, Registrar<Block> registrarB, DeferredRegister<Item> registerI, Registrar<Item> registrarI, ResourceLocation name, Supplier<T> block) {
        RegistrySupplier<T> toReturn = registerWithoutItem(registerB, registrarB, name, block);
        registerItem(registerI, registrarI, name, () -> new BlockItem(toReturn.get(), new Item.Properties()));
        return toReturn;
    }

    public static <T extends Block> RegistrySupplier<T> registerWithoutItem(DeferredRegister<Block> register, Registrar<Block> registrar, ResourceLocation path, Supplier<T> block) {
        return Platform.isNeoForge() ? register.register(path.getPath(), block) : registrar.register(path, block);
    }

    public static <T extends Item> RegistrySupplier<T> registerItem(DeferredRegister<Item> register, Registrar<Item> registrar, ResourceLocation path, Supplier<T> itemSupplier) {
        return Platform.isNeoForge() ? register.register(path.getPath(), itemSupplier) : registrar.register(path, itemSupplier);
    }

    public static Collection<ServerPlayer> tracking(ServerLevel world, ChunkPos pos) {
        Objects.requireNonNull(world, "The world cannot be null");
        Objects.requireNonNull(pos, "The chunk pos cannot be null");
        return world.getChunkSource().chunkMap.getPlayers(pos, false);
    }

    public static Collection<ServerPlayer> tracking(ServerLevel world, BlockPos pos) {
        Objects.requireNonNull(pos, "BlockPos cannot be null");
        return tracking(world, new ChunkPos(pos));
    }

    public static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape) {
        VoxelShape[] buffer = new VoxelShape[]{shape, Shapes.empty()};
        int times = (to.get2DDataValue() - from.get2DDataValue() + 4) % 4;

        for (int i = 0; i < times; ++i) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = Shapes.joinUnoptimized(buffer[1], Shapes.box(1.0 - maxZ, minY, minX, 1.0 - minZ, maxY, maxX), BooleanOp.OR));
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }

        return buffer[0];
    }

    public static Optional<Tuple<Float, Float>> getRelativeHitCoordinatesForBlockFace(
            BlockHitResult blockHitResult,
            Direction direction,
            Direction[] unAllowedDirections) {

        Direction hitDirection = blockHitResult.getDirection();

        for (Direction unAllowed : unAllowedDirections) {
            if (unAllowed == hitDirection) {
                return Optional.empty();
            }
        }

        if (hitDirection != direction && hitDirection != Direction.UP && hitDirection != Direction.DOWN) {
            return Optional.empty();
        }

        BlockPos adjacentPos = blockHitResult.getBlockPos().relative(hitDirection);
        Vec3 hitLocation = blockHitResult.getLocation().subtract(
                adjacentPos.getX(),
                adjacentPos.getY(),
                adjacentPos.getZ()
        );

        float x = (float) hitLocation.x();
        float z = (float) hitLocation.z();
        float y = (float) hitLocation.y();

        Direction effectiveDirection = (hitDirection == Direction.UP || hitDirection == Direction.DOWN)
                ? direction
                : hitDirection;

        return switch (effectiveDirection) {
            case NORTH -> Optional.of(new Tuple<>(1.0f - x, y));
            case SOUTH -> Optional.of(new Tuple<>(x, y));
            case WEST -> Optional.of(new Tuple<>(z, y));
            case EAST -> Optional.of(new Tuple<>(1.0f - z, y));
            default -> Optional.empty();
        };
    }

    public enum LineConnectingType implements StringRepresentable {
        NONE("none"),
        MIDDLE("middle"),
        LEFT("left"),
        RIGHT("right");

        private final String name;

        LineConnectingType(String type) {
            this.name = type;
        }

        public @NotNull String getSerializedName() {
            return this.name;
        }
    }

    public static InteractionResult onUse(Level world, Player player, InteractionHand hand, BlockHitResult hit, double extraHeight) {
        if (world.isClientSide) return InteractionResult.PASS;
        if (player.isShiftKeyDown()) return InteractionResult.PASS;
        if (GeneralUtil.isPlayerSitting(player)) return InteractionResult.PASS;
        if (hit.getDirection() == Direction.DOWN) return InteractionResult.PASS;
        BlockPos hitPos = hit.getBlockPos();
        if (!GeneralUtil.isOccupied(world, hitPos) && player.getItemInHand(hand).isEmpty()) {
            ChairEntity chair = EntityTypeRegistry.CHAIR.get().create(world);
            assert chair != null;
            chair.moveTo(hitPos.getX() + 0.5D, hitPos.getY() + 0.25D + extraHeight, hitPos.getZ() + 0.5D, 0, 0);
            if (GeneralUtil.addChairEntity(world, hitPos, chair, player.blockPosition())) {
                world.addFreshEntity(chair);
                player.startRiding(chair);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    public static void onStateReplaced(Level world, BlockPos pos) {
        if (!world.isClientSide) {
            ChairEntity entity = GeneralUtil.getChairEntity(world, pos);
            if (entity != null) {
                GeneralUtil.removeChairEntity(world, pos);
                entity.ejectPassengers();
            }
        }
    }

    public static boolean addChairEntity(Level world, BlockPos blockPos, ChairEntity entity, BlockPos playerPos) {
        if (!world.isClientSide) {
            ResourceLocation id = getDimensionTypeId(world);
            if (!CHAIRS.containsKey(id)) CHAIRS.put(id, new HashMap<>());
            CHAIRS.get(id).put(blockPos, Pair.of(entity, playerPos));
            return true;
        }
        return false;
    }

    public static void removeChairEntity(Level world, BlockPos pos) {
        if (!world.isClientSide) {
            ResourceLocation id = getDimensionTypeId(world);
            if (CHAIRS.containsKey(id)) {
                CHAIRS.get(id).remove(pos);
            }
        }
    }

    public static ChairEntity getChairEntity(Level world, BlockPos pos) {
        if (!world.isClientSide()) {
            ResourceLocation id = getDimensionTypeId(world);
            if (CHAIRS.containsKey(id) && CHAIRS.get(id).containsKey(pos))
                return CHAIRS.get(id).get(pos).getFirst();
        }
        return null;
    }

    public static BlockPos getPreviousPlayerPosition(Player player, ChairEntity chairEntity) {
        if (!player.level().isClientSide()) {
            ResourceLocation id = getDimensionTypeId(player.level());
            if (CHAIRS.containsKey(id)) {
                for (Pair<ChairEntity, BlockPos> pair : CHAIRS.get(id).values()) {
                    if (pair.getFirst() == chairEntity)
                        return pair.getSecond();
                }
            }
        }
        return null;
    }

    public static boolean isOccupied(Level world, BlockPos pos) {
        ResourceLocation id = getDimensionTypeId(world);
        return GeneralUtil.CHAIRS.containsKey(id) && GeneralUtil.CHAIRS.get(id).containsKey(pos);
    }

    public static boolean isPlayerSitting(Player player) {
        for (ResourceLocation i : CHAIRS.keySet()) {
            for (Pair<ChairEntity, BlockPos> pair : CHAIRS.get(i).values()) {
                if (pair.getFirst().hasPassenger(player))
                    return true;
            }
        }
        return false;
    }

    private static ResourceLocation getDimensionTypeId(Level world) {
        return world.dimension().location();
    }

    public enum ShutterType implements StringRepresentable {
        TOP("top"),
        MIDDLE("middle"),
        BOTTOM("bottom"),
        NONE("none");

        private final String name;

        ShutterType(String type) {
            this.name = type;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }

        public static final EnumProperty<ShutterType> SHUTTER_TYPE;

        static {
            SHUTTER_TYPE = EnumProperty.create("type", ShutterType.class);
        }
    }
}
