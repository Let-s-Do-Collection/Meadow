package net.satisfy.meadow.core.block;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.meadow.core.registry.ObjectRegistry;
import net.satisfy.meadow.core.registry.ParticleTypeRegistry;
import net.satisfy.meadow.core.registry.SoundEventRegistry;
import net.satisfy.meadow.core.registry.TagRegistry;
import net.satisfy.meadow.core.util.GeneralUtil;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

public class FonduePotBlock extends FacingBlock {
    public static final IntegerProperty FILL_AMOUNT = IntegerProperty.create("fill_amount", 0, 3);

    private static final VoxelShape SHAPE_NORTH = Shapes.or(
            Block.box(3.0, 0.0, 3.0, 13.0, 6.0, 13.0),
            Block.box(2.0, 6.0, 2.0, 14.0, 8.0, 14.0),
            Block.box(1.0, 6.0, 5.0, 2.0, 8.0, 11.0),
            Block.box(14.0, 6.0, 5.0, 15.0, 8.0, 11.0)
    );

    public static final Map<Direction, VoxelShape> SHAPE = Util.make(new EnumMap<>(Direction.class), map -> {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            map.put(direction, GeneralUtil.rotateShape(Direction.NORTH, direction, SHAPE_NORTH));
        }
    });

    public FonduePotBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(FILL_AMOUNT, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FILL_AMOUNT);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        VoxelShape shape = SHAPE.get(state.getValue(FACING));
        return shape == null ? SHAPE_NORTH : shape;
    }

    @Override
    public @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!isHeated(level, pos)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        int fillAmount = state.getValue(FILL_AMOUNT);

        if (stack.is(TagRegistry.BREAD) && fillAmount > 0) {
            if (!level.isClientSide) {
                level.setBlock(pos, state.setValue(FILL_AMOUNT, fillAmount - 1), 11);

                ItemStack cheeseStick = ObjectRegistry.CHEESE_STICK.get().getDefaultInstance();
                if (!player.getAbilities().instabuild) {
                    if (!player.getInventory().add(cheeseStick)) {
                        player.drop(cheeseStick, false);
                    }
                    stack.shrink(1);
                } else {
                    if (!player.getInventory().add(cheeseStick)) {
                        player.drop(cheeseStick, false);
                    }
                }
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        if (fillAmount < 3 && stack.is(TagRegistry.CHEESE_WHEELS)) {
            if (!level.isClientSide) {
                level.setBlock(pos, state.setValue(FILL_AMOUNT, 3), 11);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        if (fillAmount < 3 && stack.is(TagRegistry.CHEESE)) {
            if (!level.isClientSide) {
                level.setBlock(pos, state.setValue(FILL_AMOUNT, fillAmount + 1), 11);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        return InteractionResult.PASS;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);

        if (!isHeated(level, pos)) {
            return;
        }

        int fillAmount = state.getValue(FILL_AMOUNT);

        if (fillAmount >= 1 && random.nextInt(100) < 5) {
            level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEventRegistry.COOKING_POT_BOILING.get(), SoundSource.BLOCKS, 0.75F, 0.75F, false);
        }

        if (random.nextInt(100) < 10) {
            level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
        }

        int smokeChance = fillAmount == 0 ? 35 : 20;
        if (random.nextInt(100) < smokeChance) {
            level.addParticle(ParticleTypes.SMOKE, pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.2, pos.getY() + 1.0, pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.2, 0.0, 0.07, 0.0);
        }

        if (fillAmount > 0) {
            if (random.nextInt(100) < 28) {
                int bubbleCount = fillAmount + 1;

                double baseSurfaceY = pos.getY() + 0.9;
                double pixelOffsetPerLevel = 0.125;
                double surfaceY = baseSurfaceY - ((3 - fillAmount) * pixelOffsetPerLevel);

                for (int i = 0; i < bubbleCount; i++) {
                    double offsetX = (random.nextDouble() - 0.5) * 0.35;
                    double offsetZ = (random.nextDouble() - 0.5) * 0.35;
                    double velocityX = (random.nextDouble() - 0.5) * 0.01;
                    double velocityZ = (random.nextDouble() - 0.5) * 0.01;
                    double velocityY = 0.03 + random.nextDouble() * 0.03;

                    level.addParticle(ParticleTypeRegistry.FONDUE_BUBBLE.get(), pos.getX() + 0.5 + offsetX, surfaceY, pos.getZ() + 0.5 + offsetZ, velocityX, velocityY, velocityZ);
                }
            }
        }
    }

    private static boolean isHeated(Level level, BlockPos pos) {
        return level.getBlockState(pos.below()).is(TagRegistry.ALLOWS_COOKING);
    }
}