package net.satisfy.meadow.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.foundation.block.ChairBlock;
import net.satisfy.foundation.seat.SeatUtil;
import org.jetbrains.annotations.NotNull;

public class PineChairBlock extends ChairBlock {
    public PineChairBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (hand == InteractionHand.OFF_HAND || state.getValue(HALF) != DoubleBlockHalf.LOWER) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        return SeatUtil.onUse(level, player, hand, hit, 0.325);
    }
}
