package net.satisfy.meadow.core.block;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.meadow.core.registry.ObjectRegistry;
import net.satisfy.meadow.core.util.GeneralUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class FireLog extends FacingBlock {

    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 3);
    public static final Map<Direction, VoxelShape> SHAPE = Util.make(new HashMap<>(), map -> {
        VoxelShape baseShape = makeDefaultShape();
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            map.put(direction, GeneralUtil.rotateShape(Direction.NORTH, direction, baseShape));
        }
    });

    public FireLog(Properties setting) {
        super(setting);
        this.registerDefaultState(this.defaultBlockState().setValue(STAGE, 1));
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        int stage = state.getValue(STAGE);
        VoxelShape shape = switch (stage) {
            case 1 -> makeSmallShape();
            case 2 -> makeMidShape();
            case 3 -> makeBigShape();
            default -> makeAxeShape();
        };
        return GeneralUtil.rotateShape(Direction.NORTH, facing, shape);
    }

    private static VoxelShape makeDefaultShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.25, 0, 0.25, 0.75, 0.5, 0.75), BooleanOp.OR);
        return shape;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        InteractionHand hand = player.getUsedItemHand();
        if (world.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        ItemStack stack = player.getItemInHand(hand);
        int stage = state.getValue(STAGE);
        if (player.isShiftKeyDown() && stack.isEmpty() && stage > 1) {
            stage--;
            player.addItem(new ItemStack(ObjectRegistry.FIRE_LOG.get()));
            world.setBlockAndUpdate(pos, state.setValue(STAGE, stage));
            world.playSound(null, pos, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.0f, 1.0f);
            return InteractionResult.SUCCESS;
        }
        if (stack.is(this.asItem()) && stage < 3) {
            stage++;
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            world.setBlockAndUpdate(pos, state.setValue(STAGE, stage));
            world.playSound(null, pos, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.0f, 1.0f);
            return InteractionResult.SUCCESS;
        }
        if (stage == 0) {
            world.removeBlock(pos, true);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(STAGE);
    }

    private VoxelShape makeAxeShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.078125, 0, 0.125, 0.328125, 0.25, 0.875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.546875, 0, 0.0625, 0.796875, 0.25, 0.4375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.546875, 0, 0.5625, 0.796875, 0.25, 0.9375), BooleanOp.OR);
        return shape;
    }

    private VoxelShape makeSmallShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.375, 0, 0.1875, 0.625, 0.25, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.625, 0, 0.125, 0.875, 0.25, 0.875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5625, 0.25, 0.0625, 0.8125, 0.5, 0.8125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.3125, 0.25, 0.125, 0.5625, 0.5, 0.875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.4375, 0.5, 0.1875, 0.6875, 0.75, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.125, 0, 0.125, 0.375, 0.25, 0.875), BooleanOp.OR);
        return shape;
    }

    private VoxelShape makeMidShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.25, 0, 0.1875, 0.5, 0.25, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5, 0, 0.0625, 0.75, 0.25, 0.8125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.75, 0, 0.125, 1, 0.25, 0.875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0, 0.125, 0.25, 0.25, 0.875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0.25, 0.1875, 0.625, 0.5, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.625, 0.25, 0.125, 0.875, 0.5, 0.875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5625, 0.5, 0.0625, 0.8125, 0.75, 0.8125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.3125, 0.5, 0.125, 0.5625, 0.75, 0.875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.4375, 0.75, 0.1875, 0.6875, 1, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.125, 0.25, 0.125, 0.375, 0.5, 0.875), BooleanOp.OR);
        return shape;
    }

    private VoxelShape makeBigShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.25, 0, 0.1875, 0.5, 0.25, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5, 0, 0.0625, 0.75, 0.25, 0.8125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.75, 0, 0.125, 1, 0.25, 0.875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0, 0.125, 0.25, 0.25, 0.875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5, 0.25, 0.1875, 0.75, 0.5, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.75, 0.25, 0.125, 1, 0.5, 0.875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.75, 0.5, 0.0625, 1, 0.75, 0.8125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5, 0.5, 0.125, 0.75, 0.75, 0.875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.75, 0.75, 0.125, 1, 1, 0.875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.25, 0.25, 0.125, 0.5, 0.5, 0.875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.25, 0.1875, 0.25, 0.5, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.5, 0.125, 0.25, 0.75, 0.875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.25, 0.5, 0.1875, 0.5, 0.75, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.75, 0.1875, 0.25, 1, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5, 0.75, 0.0625, 0.75, 1, 0.8125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.25, 0.75, 0.1875, 0.5, 1, 0.9375), BooleanOp.OR);
        return shape;
    }
}
