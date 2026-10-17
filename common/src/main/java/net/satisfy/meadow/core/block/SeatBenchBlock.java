package net.satisfy.meadow.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.foundation.block.BenchBlock;
import net.satisfy.foundation.block.LineConnectingType;
import net.satisfy.foundation.seat.SeatUtil;
import org.jetbrains.annotations.NotNull;

public class SeatBenchBlock extends BenchBlock {
    private static final VoxelShape[] TOP_SHAPE = {
            Block.box(2.0, 4.0, 0.0, 14.0, 8.0, 16.0),
            Block.box(0.0, 4.0, 2.0, 16.0, 8.0, 14.0)
    };
    private static final VoxelShape[] BOTTOM_SINGLE_SHAPE = {
            Shapes.or(Block.box(1.0, 0.0, 1.0, 15.0, 4.0, 5.0), Block.box(1.0, 0.0, 11.0, 15.0, 4.0, 15.0)),
            Shapes.or(Block.box(1.0, 0.0, 1.0, 5.0, 4.0, 15.0), Block.box(11.0, 0.0, 1.0, 15.0, 4.0, 15.0))
    };
    private static final VoxelShape[] BOTTOM_MULTI_SHAPE = {
            Block.box(1.0, 0.0, 1.0, 9.0, 4.0, 15.0),
            Block.box(7.0, 0.0, 1.0, 15.0, 4.0, 15.0),
            Block.box(1.0, 0.0, 7.0, 15.0, 4.0, 15.0),
            Block.box(1.0, 0.0, 1.0, 15.0, 4.0, 9.0)
    };

    public SeatBenchBlock(Properties settings) {
        super(settings);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(FACING);
        int axis = direction.getAxis() == Direction.Axis.X ? 0 : 1;
        LineConnectingType type = state.getValue(TYPE);
        if (type == LineConnectingType.NONE) {
            return Shapes.or(TOP_SHAPE[axis], BOTTOM_SINGLE_SHAPE[axis]);
        }
        if (type == LineConnectingType.MIDDLE) {
            return TOP_SHAPE[axis];
        }
        boolean left = type == LineConnectingType.LEFT;
        int i = switch (direction) {
            case NORTH -> left ? 0 : 1;
            case SOUTH -> left ? 1 : 0;
            case EAST -> left ? 3 : 2;
            default -> left ? 2 : 3;
        };
        return Shapes.or(TOP_SHAPE[axis], BOTTOM_MULTI_SHAPE[i]);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return SeatUtil.onUse(level, player, hand, hit, 0.2);
    }
}
