package net.satisfy.meadow.core.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
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

    @Inject(method = "lowerFillLevel", at = @At(value = "HEAD"), cancellable = true)
    private static void register(BlockState state, Level world, BlockPos pos, CallbackInfo ci) {
        Block b = state.getBlock();
        if(b.equals(ObjectRegistry.WOODEN_WATER_CAULDRON.get())){
            int i = state.getValue(LEVEL) - 1;
            BlockState blockState = i == 0 ? ObjectRegistry.WOODEN_CAULDRON.get().defaultBlockState() : state.setValue(LEVEL, i);

            world.setBlockAndUpdate(pos, blockState);
            world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(blockState));
            ci.cancel();
        }
    }

    @Inject(method = "entityInside", at = @At(value = "HEAD"))
    private void register(BlockState state, Level world, BlockPos pos, Entity entity, CallbackInfo ci) {
        Object o = this;
        LayeredCauldronBlock block = (LayeredCauldronBlock) o;

        if (!world.isClientSide && block.isEntityInsideContent(state, pos, entity)) {
            if(entity.isOnFire()) return;
            if(block == Blocks.POWDER_SNOW_CAULDRON){
                Blocks.POWDER_SNOW.defaultBlockState().entityInside(world, pos, entity);
            }
        }
    }

}