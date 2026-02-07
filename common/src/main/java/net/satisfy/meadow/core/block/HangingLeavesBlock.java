package net.satisfy.meadow.core.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class HangingLeavesBlock extends LeavesBlock {
    public static final MapCodec<HangingLeavesBlock> CODEC = simpleCodec(HangingLeavesBlock::new);
    public static final BooleanProperty HANGING = BooleanProperty.create("hanging");

    public HangingLeavesBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(HANGING, false));
    }

    @Override
    public @NotNull MapCodec<? extends HangingLeavesBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HANGING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState placedState = super.getStateForPlacement(context);
        if (placedState == null) {
            return null;
        }
        boolean shouldHang = isSameHangingLeaves(context.getLevel().getBlockState(context.getClickedPos().above()));
        return placedState.setValue(HANGING, shouldHang);
    }

    @Override
    protected @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        BlockState updatedState = super.updateShape(state, direction, neighborState, level, pos, neighborPos);
        if (direction == Direction.UP) {
            return updatedState.setValue(HANGING, isSameHangingLeaves(neighborState));
        }
        return updatedState;
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.is(Items.SHEARS) && state.getValue(HANGING)) {
            if (!level.isClientSide) {
                level.setBlock(pos, state.setValue(HANGING, false), 3);
                level.levelEvent(2001, pos, Block.getId(state));
                EquipmentSlot equipmentSlot = hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
                stack.hurtAndBreak(1, player, equipmentSlot);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        if (stack.is(this.asItem()) && !state.getValue(HANGING) && isSameHangingLeaves(level.getBlockState(pos.above()))) {
            if (!level.isClientSide) {
                level.setBlock(pos, state.setValue(HANGING, true), 3);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private static boolean isSameHangingLeaves(BlockState state) {
        return state.getBlock() instanceof HangingLeavesBlock;
    }
}