package net.satisfy.meadow.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.meadow.core.util.GeneralUtil;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class BenchBlock extends LineConnectingBlock {
    public BenchBlock(BlockBehaviour.Properties settings) {
        super(settings);
    }

    public static final VoxelShape[] TOP_SHAPE;
    public static final VoxelShape[] BOTTOM_SINGLE_SHAPE;
    public static final VoxelShape[] BOTTOM_MULTI_SHAPE;


    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        boolean isX = state.getValue(FACING).getAxis() == Direction.Axis.X;
        Direction direction = state.getValue(FACING);

        if (state.getValue(TYPE) == GeneralUtil.LineConnectingType.NONE) {
            return Shapes.or(isX ? TOP_SHAPE[0] : TOP_SHAPE[1], isX ? BOTTOM_SINGLE_SHAPE[0] : BOTTOM_SINGLE_SHAPE[1]);
        }
        if (state.getValue(TYPE) == GeneralUtil.LineConnectingType.MIDDLE) {
            return isX ? TOP_SHAPE[0] : TOP_SHAPE[1];
        }

        int i = 0;
        GeneralUtil.LineConnectingType type = state.getValue(TYPE);

        if ((direction == Direction.NORTH && type == GeneralUtil.LineConnectingType.LEFT) || (direction == Direction.SOUTH && type == GeneralUtil.LineConnectingType.RIGHT)) {
            i = 0;
        } else if ((direction == Direction.NORTH && type == GeneralUtil.LineConnectingType.RIGHT) || (direction == Direction.SOUTH && type == GeneralUtil.LineConnectingType.LEFT)) {
            i = 1;
        } else if ((direction == Direction.EAST && type == GeneralUtil.LineConnectingType.RIGHT) || (direction == Direction.WEST && type == GeneralUtil.LineConnectingType.LEFT)) {
            i = 2;
        } else if ((direction == Direction.EAST && type == GeneralUtil.LineConnectingType.LEFT) || (direction == Direction.WEST && type == GeneralUtil.LineConnectingType.RIGHT)) {
            i = 3;
        }
        return Shapes.or(isX ? TOP_SHAPE[0] : TOP_SHAPE[1], BOTTOM_MULTI_SHAPE[i]);
    }

    @Override
    public @NotNull InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return GeneralUtil.onUse(world, player, hand, hit, 0);
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        GeneralUtil.onStateReplaced(world, pos);
    }

    static {
        TOP_SHAPE = new VoxelShape[]{
                Block.box(2.0, 4.0, 0.0, 14.0, 8.0, 16.0),
                Block.box(0.0, 4.0, 2.0, 16.0, 8.0, 14.0)
        };
        BOTTOM_SINGLE_SHAPE = new VoxelShape[]{
                Shapes.or(Block.box(1.0, 0.0, 1.0, 15.0, 4.0, 5.0), Block.box(1.0, 0.0, 11.0, 15.0, 4.0, 15.0)),
                Shapes.or(Block.box(1.0, 0.0, 1.0, 5.0, 4.0, 15.0), Block.box(11.0, 0.0, 1.0, 15.0, 4.0, 15.0))
        };
        BOTTOM_MULTI_SHAPE = new VoxelShape[]{
                Block.box(1.0, 0.0, 1.0, 9.0, 4.0, 15.0),
                Block.box(7.0, 0.0, 1.0, 15.0, 4.0, 15.0),
                Block.box(1.0, 0.0, 7.0, 15.0, 4.0, 15.0),
                Block.box(1.0, 0.0, 1.0, 15.0, 4.0, 9.0),
        };
    }
}
