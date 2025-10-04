package net.satisfy.meadow.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SmokerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.meadow.core.block.entity.StoveBlockEntity;
import net.satisfy.meadow.core.registry.EntityTypeRegistry;
import net.satisfy.meadow.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TiledStoveBlockSmoker extends SmokerBlock {
    public static final BooleanProperty CONNECTED = BooleanProperty.create("connected");
    public static final VoxelShape SHAPE_BIG = Shapes.or(TiledStoveBlockBench.SHAPE, Block.box(0, 2, 0, 16, 16, 16));
    private final Direction directionToCheck;

    public TiledStoveBlockSmoker(Properties properties, Direction directionToCheck) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(CONNECTED, false));
        this.directionToCheck = directionToCheck;
    }


    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (directionToCheck == Direction.DOWN && state.getValue(CONNECTED))
            return super.getShape(state, world, pos, context);
        return SHAPE_BIG;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        List<Block> block = getBlocksToCheck();
        if (!block.isEmpty()) {
            if (block.contains(ctx.getLevel().getBlockState(ctx.getClickedPos().relative(directionToCheck)).getBlock())) {
                return this.defaultBlockState().setValue(CONNECTED, true).setValue(FACING, ctx.getHorizontalDirection().getOpposite());
            }
        }
        return super.getStateForPlacement(ctx);
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        List<Block> block = getBlocksToCheck();
        if (!world.isClientSide() && !block.isEmpty()) {
            if (direction == directionToCheck) {
                boolean connected = state.getValue(CONNECTED);
                if (!connected) {
                    if (block.contains(neighborState.getBlock())) return state.setValue(CONNECTED, true);
                } else if (!block.contains(neighborState.getBlock())) return state.setValue(CONNECTED, false);

            }
        }
        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CONNECTED);
    }

    private List<Block> getBlocksToCheck() {
        if (directionToCheck == Direction.UP) {
            return List.of(ObjectRegistry.TILED_STOVE.get());
        } else if (directionToCheck == Direction.DOWN) {
            return List.of(ObjectRegistry.TILED_STOVE_FIREPLACE.get(), ObjectRegistry.TILED_STOVE_SMOKER.get());
        } else return List.of();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (this == ObjectRegistry.TILED_STOVE_SMOKER.get()) return null;
        return new StoveBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {

        if (this == ObjectRegistry.TILED_STOVE_SMOKER.get()) return null;
        return createFurnaceTicker(level, type, EntityTypeRegistry.STOVE_BLOCK_ENTITY.get());
    }

}

