package net.satisfy.meadow.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FireChargeItem;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.SmokerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public class TiledStoveBlockFireplace extends TiledStoveBlock {
    public static final BooleanProperty TOP = BooleanProperty.create("top");

    public TiledStoveBlockFireplace(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(TiledStoveBlock.LIT, false).setValue(TOP, false));
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        return InteractionResult.PASS;
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return ItemInteractionResult.SUCCESS;
        boolean isLit = state.getValue(TiledStoveBlock.LIT);
        boolean isFlint = stack.getItem() instanceof FlintAndSteelItem;
        if (stack.is(ItemTags.PICKAXES)) {
            BlockState next = state.cycle(TOP);
            level.setBlockAndUpdate(pos, next);
            level.levelEvent(null, LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(next));
            level.playSound(null, pos, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1F, 1F);
            return ItemInteractionResult.SUCCESS;
        }
        if (isLit && stack.is(ItemTags.SHOVELS)) {
            propagateLit(level, pos, false);
            level.levelEvent(null, LevelEvent.SOUND_EXTINGUISH_FIRE, pos, 0);
            if (!player.getAbilities().instabuild) stack.hurtAndBreak(1, player, player.getEquipmentSlotForItem(stack));
            return ItemInteractionResult.SUCCESS;
        }
        if (!isLit && (isFlint || stack.getItem() instanceof FireChargeItem)) {
            propagateLit(level, pos, true);
            if (!player.getAbilities().instabuild) {
                if (isFlint) stack.hurtAndBreak(1, player, player.getEquipmentSlotForItem(stack));
                else stack.shrink(1);
            }
            level.playSound(null, pos, isFlint ? SoundEvents.FLINTANDSTEEL_USE : SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1F, 1F);
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(TiledStoveBlock.LIT)) {
            double centerX = pos.getX() + 0.5;
            double centerY = pos.getY() + 0.24;
            double centerZ = pos.getZ() + 0.5;
            if (random.nextDouble() < 0.1) level.playLocalSound(centerX, centerY, centerZ, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 0.5F, 0.5F, false);
            level.playLocalSound(centerX, centerY, centerZ, SoundEvents.SMOKER_SMOKE, SoundSource.BLOCKS, 0.5F, 0.5F, false);
            Direction facing = state.getValue(FACING);
            Direction.Axis axis = facing.getAxis();
            double offset = random.nextDouble() * 0.6 - 0.3;
            double ox = axis == Direction.Axis.X ? facing.getStepX() * 0.52 : offset;
            double oy = random.nextDouble() * 6.0 / 16.0;
            double oz = axis == Direction.Axis.Z ? facing.getStepZ() * 0.52 : offset;
            level.addParticle(ParticleTypes.SMOKE, centerX + ox, centerY + oy, centerZ + oz, 0.0, 0.0, 0.0);
            level.addParticle(ParticleTypes.FLAME, centerX + ox, centerY + oy, centerZ + oz, 0.0, 0.0, 0.0);
            double topY = pos.getY() + 1.5;
            level.addParticle(ParticleTypes.SMOKE, centerX, topY, centerZ, 0.0, 0.0, 0.0);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(TOP);
    }

    @SuppressWarnings("unused")
    private void propagateLit(Level level, BlockPos origin, boolean lit) {
        Deque<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();
        queue.add(origin);
        visited.add(origin);
        while (!queue.isEmpty()) {
            BlockPos blockPos = queue.removeFirst();
            BlockState blockState = level.getBlockState(blockPos);
            Block block = blockState.getBlock();
            switch (block) {
                case TiledStoveBlockFireplace fireplace -> {
                    if (blockState.getValue(TiledStoveBlock.LIT) != lit) {
                        level.setBlockAndUpdate(blockPos, blockState.setValue(TiledStoveBlock.LIT, lit));
                    }
                }
                case TiledStoveBlockSmoker smoker -> {
                    if (blockState.hasProperty(SmokerBlock.LIT) && blockState.getValue(SmokerBlock.LIT) != lit) {
                        level.setBlockAndUpdate(blockPos, blockState.setValue(SmokerBlock.LIT, lit));
                    }
                }
                case TiledStoveBlock tiled -> {
                    if (blockState.hasProperty(TiledStoveBlock.LIT) && blockState.getValue(TiledStoveBlock.LIT) != lit) {
                        level.setBlockAndUpdate(blockPos, blockState.setValue(TiledStoveBlock.LIT, lit));
                    }
                }
                default -> {
                    continue;
                }
            }
            for (Direction direction : Direction.values()) {
                BlockPos neighborPos = blockPos.relative(direction);
                if (!visited.add(neighborPos)) continue;
                Block neighborBlock = level.getBlockState(neighborPos).getBlock();
                if (neighborBlock instanceof TiledStoveBlockSmoker
                        || neighborBlock instanceof TiledStoveBlock) {
                    queue.addLast(neighborPos);
                }
            }
        }
    }

}
