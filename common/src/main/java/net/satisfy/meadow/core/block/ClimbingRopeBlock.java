package net.satisfy.meadow.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.meadow.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

public class ClimbingRopeBlock extends Block {

    protected static final VoxelShape SHAPE = Block.box(7, 0, 7, 9, 16, 9);

    public ClimbingRopeBlock(Properties settings) {
        super(settings);
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (!world.isClientSide()) {
            BlockState blockState = world.getBlockState(pos.above());
            if (blockState.is(ObjectRegistry.CLIMBING_ROPE.get()) || blockState.is(ObjectRegistry.CLIMBING_ROPE_TOPMOUNT.get())) {
                return state;
            }
            world.destroyBlock(pos, true);
            return world.getBlockState(pos);
        }
        return state;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
