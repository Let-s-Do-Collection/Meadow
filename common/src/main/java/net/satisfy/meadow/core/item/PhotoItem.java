package net.satisfy.meadow.core.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.satisfy.meadow.core.block.PhotoFrameBlock;
import net.satisfy.meadow.core.block.entity.PhotoFrameBlockEntity;
import net.satisfy.meadow.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PhotoItem extends MapItem {
    public PhotoItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Direction face = context.getClickedFace();
        if (face.getAxis().isVertical()) {
            return InteractionResult.PASS;
        }
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos().relative(face);
        BlockState state = ObjectRegistry.PHOTO_FRAME.get().defaultBlockState().setValue(PhotoFrameBlock.FACING, face);
        if (!level.getBlockState(pos).canBeReplaced(new BlockPlaceContext(context)) || !state.canSurvive(level, pos)) {
            return InteractionResult.FAIL;
        }
        if (!level.isClientSide) {
            ItemStack stack = context.getItemInHand();
            level.setBlock(pos, state, Block.UPDATE_ALL);
            if (level.getBlockEntity(pos) instanceof PhotoFrameBlockEntity frame) {
                frame.setPhoto(stack);
                level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
            }
            level.playSound(null, pos, SoundEvents.PAINTING_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(context.getPlayer(), GameEvent.BLOCK_PLACE, pos);
            stack.consume(1, context.getPlayer());
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
    }
}
