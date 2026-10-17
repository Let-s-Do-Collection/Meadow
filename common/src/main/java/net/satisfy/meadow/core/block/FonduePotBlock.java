package net.satisfy.meadow.core.block;

import net.satisfy.foundation.util.ShapeUtil;
import net.satisfy.foundation.block.FacingBlock;
import net.satisfy.foundation.registry.FoundationParticles;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.meadow.core.block.entity.FonduePotBlockEntity;
import net.satisfy.meadow.core.registry.EntityTypeRegistry;
import net.satisfy.meadow.core.registry.ObjectRegistry;
import net.satisfy.meadow.core.registry.SoundEventRegistry;
import net.satisfy.meadow.core.registry.TagRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class FonduePotBlock extends FacingBlock implements EntityBlock {
    private static final VoxelShape SHAPE_NORTH = Shapes.or(
            Block.box(3.0, 0.0, 3.0, 13.0, 6.0, 13.0),
            Block.box(2.0, 6.0, 2.0, 14.0, 8.0, 14.0),
            Block.box(1.0, 6.0, 5.0, 2.0, 8.0, 11.0),
            Block.box(14.0, 6.0, 5.0, 15.0, 8.0, 11.0)
    );

    public static final Map<Direction, VoxelShape> SHAPE = Util.make(new EnumMap<>(Direction.class), map -> {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            map.put(direction, ShapeUtil.rotateShape(Direction.NORTH, direction, SHAPE_NORTH));
        }
    });

    public FonduePotBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        VoxelShape shape = SHAPE.get(state.getValue(FACING));
        return shape == null ? SHAPE_NORTH : shape;
    }

    @Override
    public @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.isEmpty() || !isHeated(level, pos) || !(level.getBlockEntity(pos) instanceof FonduePotBlockEntity pot)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (stack.is(TagRegistry.BREAD) && !pot.getPortions().isEmpty() && pot.getMelting().isEmpty()) {
            if (level.isClientSide) {
                pot.splash(level, 4);
            } else {
                pot.takePortion();
                ItemStack cheeseStick = ObjectRegistry.CHEESE_STICK.get().getDefaultInstance();
                if (!player.getInventory().add(cheeseStick)) {
                    player.drop(cheeseStick, false);
                }
                stack.consume(1, player);
                shake(level, pos);
                level.playSound(null, pos, SoundEvents.HONEY_BLOCK_SLIDE, SoundSource.BLOCKS, 0.8F, 1.2F);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        if (pot.canAccept(stack)) {
            if (!level.isClientSide) {
                pot.insert(stack);
                stack.consume(1, player);
                shake(level, pos);
                level.playSound(null, pos, SoundEvents.HONEY_BLOCK_PLACE, SoundSource.BLOCKS, 0.8F, 1.0F);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!player.getMainHandItem().isEmpty() || !(level.getBlockEntity(pos) instanceof FonduePotBlockEntity pot)) {
            return InteractionResult.PASS;
        }
        pot.stir();
        if (!level.isClientSide) {
            shake(level, pos);
        }
        if (!level.isClientSide && pot.hasContents() && level.random.nextInt(4) == 0) {
            level.playSound(null, pos, SoundEvents.HONEY_BLOCK_SLIDE, SoundSource.BLOCKS, 0.4F, 0.8F + level.random.nextFloat() * 0.4F);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private void shake(Level level, BlockPos pos) {
        level.blockEvent(pos, this, FonduePotBlockEntity.SHAKE_EVENT, 0);
    }

    @Override
    protected boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity != null && blockEntity.triggerEvent(id, param);
    }

    @Override
    protected @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof FonduePotBlockEntity pot) {
            Containers.dropContents(level, pos, pot.drops());
        }
        super.onRemove(state, level, pos, newState, moved);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FonduePotBlockEntity(pos, state);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == EntityTypeRegistry.FONDUE_POT_BLOCK_ENTITY.get() ? (BlockEntityTicker<T>) (BlockEntityTicker<FonduePotBlockEntity>) FonduePotBlockEntity::tick : null;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);

        if (!isHeated(level, pos) || !(level.getBlockEntity(pos) instanceof FonduePotBlockEntity pot)) {
            return;
        }

        double centerX = pos.getX() + 0.5;
        double centerZ = pos.getZ() + 0.5;
        double surfaceY = pos.getY() + pot.surfaceY();

        if (random.nextInt(100) < 10) {
            level.playLocalSound(centerX, pos.getY() + 0.2, centerZ, SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS, 0.6F, 1.0F, false);
        }

        if (pot.getPortions().isEmpty() && pot.getMelting().isEmpty()) {
            if (random.nextInt(100) < 18) {
                level.addParticle(ParticleTypes.SMOKE, centerX, pos.getY() + 0.6, centerZ, 0.0, 0.05, 0.0);
            }
            return;
        }

        int color = 0xFF000000 | FonduePotBlockEntity.colorProvider.applyAsInt(pot);
        if (!pot.getPortions().isEmpty() && random.nextInt(100) < 95) {
            int bubbles = 1 + random.nextInt(2) + pot.getPortions().size() / 2;
            for (int i = 0; i < bubbles; i++) {
                double x = centerX + (random.nextDouble() - 0.5) * 0.35;
                double z = centerZ + (random.nextDouble() - 0.5) * 0.35;
                level.addParticle(ColorParticleOption.create(FoundationParticles.COLORED_SOUP_BUBBLE.get(), color), x, surfaceY, z, 0.0, 0.0, 0.0);
                level.addParticle(ColorParticleOption.create(FoundationParticles.COLORED_SOUP_COOKING_BUBBLE.get(), color), x, surfaceY, z, 0.0, 0.0, 0.0);
            }
        }

        int steamChance = pot.getMelting().isEmpty() ? 45 : 80;
        if (random.nextInt(100) < steamChance) {
            int steam = 1 + random.nextInt(pot.getMelting().isEmpty() ? 2 : 3);
            for (int i = 0; i < steam; i++) {
                double x = centerX + (random.nextDouble() - 0.5) * 0.3;
                double z = centerZ + (random.nextDouble() - 0.5) * 0.3;
                level.addParticle(FoundationParticles.SOUP_STEAM.get(), x, surfaceY + 0.2, z, 0.0, 0.07, 0.0);
            }
        }

        if (random.nextInt(100) < 15) {
            level.addParticle(ParticleTypes.SMOKE, centerX, surfaceY + 0.4, centerZ, 0.0, 0.06, 0.0);
        }

        if (!pot.getPortions().isEmpty() && random.nextInt(100) < 6) {
            level.playLocalSound(centerX, surfaceY, centerZ, SoundEventRegistry.COOKING_POT_BOILING.get(), SoundSource.BLOCKS, 0.75F, 0.75F, false);
        }

        if (!pot.getPortions().isEmpty() && random.nextInt(100) < 4) {
            level.playLocalSound(centerX, surfaceY, centerZ, SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, SoundSource.BLOCKS, 0.4F, 0.7F + random.nextFloat() * 0.3F, false);
        }
    }

    public static float surfaceHeight(float fill) {
        return 1.0F + fill * 1.5F;
    }

    public static boolean isHeated(Level level, BlockPos pos) {
        return level.getBlockState(pos.below()).is(TagRegistry.ALLOWS_COOKING);
    }
}
