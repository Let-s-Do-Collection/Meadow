package net.satisfy.meadow.neoforge.core.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.satisfy.meadow.core.registry.ObjectRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.world.level.block.LayeredCauldronBlock.LEVEL;

@Mixin(LayeredCauldronBlock.class)
public class FluidDecrementMixin {
    @Inject(method = "lowerFillLevel", at = @At("HEAD"), cancellable = true)
    private static void meadow$lowerFillLevel(BlockState state, Level world, BlockPos pos, CallbackInfo ci) {
        if (state.getBlock().equals(ObjectRegistry.WOODEN_WATER_CAULDRON.get())) {
            int i = state.getValue(LEVEL) - 1;
            BlockState next = i == 0 ? ObjectRegistry.WOODEN_CAULDRON.get().defaultBlockState() : state.setValue(LEVEL, i);
            world.setBlockAndUpdate(pos, next);
            world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(next));
            ci.cancel();
        }
    }
}
