package net.satisfy.meadow.core.block;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.meadow.core.block.entity.StorageBlockEntity;
import net.satisfy.meadow.core.registry.StorageTypeRegistry;
import net.satisfy.meadow.core.util.GeneralUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class FlowerBoxBlock extends StorageBlock {
    private static final Supplier<VoxelShape> voxelShapeSupplier = () -> {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.9375, 0.0, 0.5625, 1.0, 0.375, 1.0), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.0, 0.0, 0.5625, 0.0625, 0.375, 1.0), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.0625, 0.0, 0.5625, 0.9375, 0.375, 0.625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.0625, 0.0, 0.9375, 0.9375, 0.375, 1.0), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.0625, 0.0, 0.625, 0.9375, 0.3125, 0.9375), BooleanOp.OR);
        return shape;
    };

    public static final Map<Direction, VoxelShape> SHAPE = Util.make(new HashMap<>(), map -> {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            map.put(direction, GeneralUtil.rotateShape(Direction.NORTH, direction, voxelShapeSupplier.get()));
        }
    });

    public FlowerBoxBlock(BlockBehaviour.Properties settings) {
        super(settings);
    }

    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE.get(state.getValue(FACING));
    }

    public @NotNull InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof StorageBlockEntity shelf) {
            Optional<Tuple<Float, Float>> opt = GeneralUtil.getRelativeHitCoordinatesForBlockFace(hit, Direction.UP, this.unAllowedDirections());
            if (opt.isEmpty()) return InteractionResult.PASS;
            Tuple<Float, Float> ff = opt.get();
            int i = this.getSection(ff.getA(), ff.getB());
            if (i == Integer.MIN_VALUE) return InteractionResult.PASS;
            if (!shelf.getInventory().get(i).isEmpty()) {
                this.remove(world, pos, player, shelf, i);
                return InteractionResult.sidedSuccess(world.isClientSide);
            } else {
                ItemStack stack = player.getItemInHand(hand);
                if (!stack.isEmpty() && this.canInsertStack(stack)) {
                    this.add(world, pos, player, shelf, stack, i);
                    return InteractionResult.sidedSuccess(world.isClientSide);
                } else {
                    return InteractionResult.CONSUME;
                }
            }
        }
        return InteractionResult.PASS;
    }

    public int size() {
        return 2;
    }

    public ResourceLocation type() {
        return StorageTypeRegistry.FLOWER_BOX;
    }

    public Direction[] unAllowedDirections() {
        return new Direction[]{Direction.DOWN};
    }

    public boolean canInsertStack(ItemStack stack) {
        return stack.is(ItemTags.SMALL_FLOWERS);
    }

    public int getSection(Float x, Float y) {
        return (double) x < 0.5 ? 0 : 1;
    }
}

