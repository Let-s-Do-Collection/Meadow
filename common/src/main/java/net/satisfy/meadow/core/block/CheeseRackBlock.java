package net.satisfy.meadow.core.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.meadow.core.block.entity.StorageBlockEntity;
import net.satisfy.meadow.core.registry.StorageTypeRegistry;
import net.satisfy.meadow.core.registry.TagRegistry;
import net.satisfy.meadow.core.util.GeneralUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class CheeseRackBlock extends StorageBlock {
    public static final MapCodec<CheeseRackBlock> CODEC = simpleCodec(CheeseRackBlock::new);

    public CheeseRackBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected @NotNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public @NotNull InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof StorageBlockEntity shelfBlockEntity)) {
            return InteractionResult.PASS;
        }

        Optional<Tuple<Float, Float>> optional = GeneralUtil.getRelativeHitCoordinatesForBlockFace(hit, state.getValue(FACING), unAllowedDirections());
        if (optional.isEmpty()) {
            return InteractionResult.PASS;
        }
        Tuple<Float, Float> ff = optional.get();
        int i = getSection(ff.getA(), ff.getB());
        if (i == Integer.MIN_VALUE || i >= shelfBlockEntity.getInventory().size()) {
            return InteractionResult.PASS;
        }
        if (!shelfBlockEntity.getInventory().get(i).isEmpty()) {
            remove(world, pos, player, shelfBlockEntity, i);
            return InteractionResult.sidedSuccess(world.isClientSide);
        } else {
            ItemStack stack = player.getItemInHand(hand);
            if (!stack.isEmpty() && canInsertStack(stack)) {
                add(world, pos, player, shelfBlockEntity, stack, i);
                return InteractionResult.sidedSuccess(world.isClientSide);
            } else {
                return InteractionResult.CONSUME;
            }
        }
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public ResourceLocation type() {
        return StorageTypeRegistry.CHEESE_RACK;
    }

    @Override
    public Direction[] unAllowedDirections() {
        return new Direction[]{Direction.UP, Direction.DOWN};
    }

    @Override
    public boolean canInsertStack(ItemStack itemStack) {
        return itemStack.is(TagRegistry.CHEESE_BLOCKS);
    }

    @Override
    public int getSection(Float x, Float y) {
        int index = y > 0.5 ? 1 : 0;
        if (index >= size()) {
            return Integer.MIN_VALUE;
        }
        return index;
    }

    @Override
    public SoundEvent getRemoveSound(Level level, BlockPos blockPos, Player player, int i) {
        return SoundEvents.WOOL_BREAK;
    }

    @Override
    public SoundEvent getAddSound(Level level, BlockPos blockPos, Player player, int i) {
        return SoundEvents.WOOL_PLACE;
    }
}