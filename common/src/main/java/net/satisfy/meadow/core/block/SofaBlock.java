package net.satisfy.meadow.core.block;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.meadow.core.util.GeneralUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SofaBlock extends LineConnectingBlock {
    public static final Map<Direction, VoxelShape> SHAPE;
    public static final Map<Direction, VoxelShape> MIDDLE_SHAPE;
    public static final Map<Direction, VoxelShape> LEFT_SHAPE;
    public static final Map<Direction, VoxelShape> RIGHT_SHAPE;

    private static final Supplier<VoxelShape> noneShapeSupplier = () -> {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0, 0.4375, 0.75, 1, 1, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.8125, 0.375, 0.003125, 1.0625, 0.75, 0.99375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(-0.0625, 0.375, 0.003125, 0.1875, 0.75, 0.99375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.125, 0, 1, 0.4375, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.8125, 0, 0.0625, 0.9375, 0.125, 0.1875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0, 0.0625, 0.1875, 0.125, 0.1875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.8125, 0, 0.8125, 0.9375, 0.125, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0, 0.8125, 0.1875, 0.125, 0.9375), BooleanOp.OR);
        return shape;
    };

    private static final Supplier<VoxelShape> middleShapeSupplier = () -> {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0, 0.4375, 0.75, 1, 1, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.125, 0, 1, 0.4375, 1), BooleanOp.OR);
        return shape;
    };

    private static final Supplier<VoxelShape> leftShapeSupplier = () -> {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.125, 0.4375, 0.75, 1, 1, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0.375, 0, 0.3125, 0.75, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.125, 0.125, 0, 1, 0.4375, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.1875, 0, 0.0625, 0.3125, 0.125, 0.1875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.1875, 0, 0.8125, 0.3125, 0.125, 0.9375), BooleanOp.OR);
        return shape;
    };

    private static final Supplier<VoxelShape> rightShapeSupplier = () -> {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0, 0.4375, 0.75, 0.875, 1, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.6875, 0.375, 0, 0.9375, 0.75, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.125, 0, 0.875, 0.4375, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.6875, 0, 0.0625, 0.8125, 0.125, 0.1875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.6875, 0, 0.8125, 0.8125, 0.125, 0.9375), BooleanOp.OR);
        return shape;
    };

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Map<Direction, VoxelShape> voxelShape;
        switch (state.getValue(TYPE)) {
            case MIDDLE -> voxelShape = MIDDLE_SHAPE;
            case LEFT -> voxelShape = LEFT_SHAPE;
            case RIGHT -> voxelShape = RIGHT_SHAPE;
            default -> voxelShape = SHAPE;
        }
        return voxelShape.get(state.getValue(FACING));
    }

    public SofaBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        return GeneralUtil.onUse(level, player, interactionHand, blockHitResult, -0.3);
    }

    @Override
    public void fallOn(Level world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        super.fallOn(world, state, pos, entity, fallDistance * 0.5F);
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter world, Entity entity) {
        if (entity.isSuppressingBounce()) {
            super.updateEntityAfterFallOn(world, entity);
        } else {
            bounceUp(entity);
        }
    }

    private void bounceUp(Entity entity) {
        Vec3 motion = entity.getDeltaMovement();
        if (motion.y < 0.0) {
            double bounceFactor = entity instanceof LivingEntity ? 1.0 : 0.8;
            entity.setDeltaMovement(motion.x, -motion.y * 0.66 * bounceFactor, motion.z);
        }
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        GeneralUtil.onStateReplaced(world, pos);
    }

    static {
        SHAPE = Util.make(new HashMap<>(), map -> {
            for (Direction direction : Direction.Plane.HORIZONTAL.stream().toList()) {
                map.put(direction, GeneralUtil.rotateShape(Direction.NORTH, direction, noneShapeSupplier.get()));
            }
        });
        MIDDLE_SHAPE = Util.make(new HashMap<>(), map -> {
            for (Direction direction : Direction.Plane.HORIZONTAL.stream().toList()) {
                map.put(direction, GeneralUtil.rotateShape(Direction.NORTH, direction, middleShapeSupplier.get()));
            }
        });
        LEFT_SHAPE = Util.make(new HashMap<>(), map -> {
            for (Direction direction : Direction.Plane.HORIZONTAL.stream().toList()) {
                map.put(direction, GeneralUtil.rotateShape(Direction.NORTH, direction, leftShapeSupplier.get()));
            }
        });
        RIGHT_SHAPE = Util.make(new HashMap<>(), map -> {
            for (Direction direction : Direction.Plane.HORIZONTAL.stream().toList()) {
                map.put(direction, GeneralUtil.rotateShape(Direction.NORTH, direction, rightShapeSupplier.get()));
            }
        });
    }

    @Override
    protected boolean isPathfindable(BlockState blockState, PathComputationType pathComputationType) {
        return false;
    }
}