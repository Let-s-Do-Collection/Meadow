package net.satisfy.meadow.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.meadow.core.util.GeneralUtil;
import org.jetbrains.annotations.NotNull;

public class StoveBlockBench extends Block {
    public static final VoxelShape SHAPE = Shapes.or(Block.box(0, 0, 0, 4, 2, 4), Block.box(12, 0, 0, 16, 2, 4), Block.box(0, 0, 12, 4, 2, 16), Block.box(12, 0, 12, 16, 2, 16));
    public static final VoxelShape SHAPE_SMALL = Shapes.or(SHAPE, Block.box(0, 2, 0, 16, 6, 16));


    public StoveBlockBench(Properties settings) {
        super(settings);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE_SMALL;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
        return GeneralUtil.onUse(level, player, player.getUsedItemHand(), blockHitResult, -0.1);
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        super.onRemove(state, world, pos, newState, moved);
        GeneralUtil.onStateReplaced(world, pos);
    }
}
