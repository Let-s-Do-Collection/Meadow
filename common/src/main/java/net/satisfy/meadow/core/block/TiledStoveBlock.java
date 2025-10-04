package net.satisfy.meadow.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TiledStoveBlock extends FacingBlock {
    public static final BooleanProperty CONNECTED_UP = BooleanProperty.create("connected_up");
    public static final BooleanProperty CONNECTED_DOWN = BooleanProperty.create("connected_down");
    public static final BooleanProperty LIT = BooleanProperty.create("lit");
    public static final VoxelShape SHAPE_BIG = Shapes.or(TiledStoveBlockBench.SHAPE, Block.box(0, 2, 0, 16, 16, 16));


    public TiledStoveBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.defaultBlockState().setValue(CONNECTED_DOWN, false).setValue(CONNECTED_UP, false).setValue(LIT, false));
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (state.getValue(CONNECTED_DOWN)) return super.getShape(state, world, pos, context);
        return SHAPE_BIG;
    }

    public boolean isConnected(BlockState state, Direction direction) {
        return state.getValue(direction == Direction.UP ? CONNECTED_UP : CONNECTED_DOWN);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        boolean up = isBlockStove(ctx, Direction.UP);
        boolean down = isBlockStove(ctx, Direction.DOWN);
        if (up || down) {
            return this.defaultBlockState().setValue(CONNECTED_UP, up).setValue(CONNECTED_DOWN, down).setValue(FACING, ctx.getHorizontalDirection().getOpposite());
        }

        return super.getStateForPlacement(ctx);
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (!world.isClientSide()) {
            if (direction == Direction.DOWN || direction == Direction.UP) {
                if (!isConnected(state, direction)) {
                    if (isBlockStove(neighborState)) {
                        return state.setValue(direction == Direction.UP ? CONNECTED_UP : CONNECTED_DOWN, true);
                    }
                } else {
                    if (!isBlockStove(neighborState)) {
                        return state.setValue(direction == Direction.UP ? CONNECTED_UP : CONNECTED_DOWN, false);
                    }
                }
            }
        }
        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CONNECTED_DOWN);
        builder.add(CONNECTED_UP);
        builder.add(LIT);
    }

    public boolean isBlockStove(BlockPlaceContext ctx, Direction direction) {
        return isBlockStove(ctx.getLevel().getBlockState(ctx.getClickedPos().relative(direction)));
    }

    public boolean isBlockStove(BlockState blockState) {
        return isBlockStove(blockState.getBlock());
    }

    public boolean isBlockStove(Block block) {
        return block instanceof TiledStoveBlockSmoker || block instanceof TiledStoveBlock || block instanceof TiledStoveBlockFireplace;
    }
}
