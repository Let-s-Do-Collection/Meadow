package net.satisfy.meadow.core.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.satisfy.meadow.core.block.WateringCanBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WateringCanItem extends BlockItem {
    public WateringCanItem(Block block, Properties settings) {
        super(block, settings);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int i, boolean bl) {
        super.inventoryTick(itemStack, level, entity, i, bl);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (itemStack.getDamageValue() == 0) return InteractionResultHolder.pass(itemStack);

        BlockHitResult blockHitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (blockHitResult.getType() != HitResult.Type.MISS) {
            if (blockHitResult.getType() == HitResult.Type.BLOCK) {
                BlockPos blockPos = blockHitResult.getBlockPos();
                if (!level.mayInteract(player, blockPos)) {
                    return InteractionResultHolder.pass(itemStack);
                }

                if (level.getFluidState(blockPos).is(FluidTags.WATER)) {
                    level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
                    level.gameEvent(player, GameEvent.FLUID_PICKUP, blockPos);
                    itemStack.setDamageValue(0);
                    return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
                }
            }

        }
        return InteractionResultHolder.pass(itemStack);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Player playerEntity = context.getPlayer();
        if (playerEntity == null || playerEntity.isShiftKeyDown()) return super.useOn(context);
        ItemStack stack = context.getItemInHand();

        Level world = context.getLevel();

        if (stack.getDamageValue() >= stack.getMaxDamage() && !playerEntity.getAbilities().instabuild)
            return InteractionResult.PASS;
        BlockPos blockPos = context.getClickedPos();
        BlockPos blockPos2 = blockPos.relative(context.getClickedFace());
        if (WateringCanItem.useOnFertilizable(stack, world, blockPos, playerEntity)) {
            if (!world.isClientSide) {
                world.levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, blockPos, 0);
            }
            return InteractionResult.sidedSuccess(world.isClientSide);
        }

        BlockState blockState = world.getBlockState(blockPos);
        boolean bl = blockState.isFaceSturdy(world, blockPos, context.getClickedFace());
        if (bl && WateringCanItem.useOnGround(stack, world, blockPos2, context.getClickedFace(), playerEntity)) {
            if (!world.isClientSide) {
                world.levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, blockPos2, 0);
            }
            return InteractionResult.sidedSuccess(world.isClientSide);
        }
        return InteractionResult.PASS;
    }

    public static boolean useOnFertilizable(ItemStack stack, Level world, BlockPos pos, Player playerEntity) {
        BonemealableBlock fertilizable;
        BlockState blockState = world.getBlockState(pos);
        if (blockState.getBlock() instanceof BonemealableBlock && (fertilizable = (BonemealableBlock) blockState.getBlock()).isValidBonemealTarget(world, pos, blockState)) {
            if (world instanceof ServerLevel) {
                if (fertilizable.isBonemealSuccess(world, world.random, pos, blockState)) {
                    fertilizable.performBonemeal((ServerLevel) world, world.random, pos, blockState);
                }
                damage(stack, playerEntity);
            }
            return true;
        }
        return false;
    }

    public static boolean useOnGround(ItemStack stack, Level world, BlockPos blockPos, @Nullable Direction facing, Player playerEntity) {
        if (!world.getBlockState(blockPos).is(Blocks.WATER) || world.getFluidState(blockPos).getAmount() != 8) {
            return false;
        }
        if (!(world instanceof ServerLevel)) {
            return true;
        }
        RandomSource random = world.getRandom();
        block0:
        for (int i = 0; i < 128; ++i) {
            BlockPos blockPos2 = blockPos;
            BlockState blockState = Blocks.SEAGRASS.defaultBlockState();
            for (int j = 0; j < i / 16; ++j) {
                if (world.getBlockState(blockPos2 = blockPos2.offset(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1)).isCollisionShapeFullBlock(world, blockPos2))
                    continue block0;
            }
            Holder<Biome> registryEntry = world.getBiome(blockPos2);
            if (registryEntry.is(BiomeTags.PRODUCES_CORALS_FROM_BONEMEAL)) {
                if (i == 0 && facing != null && facing.getAxis().isHorizontal()) {
                    blockState = BuiltInRegistries.BLOCK.getTag(BlockTags.WALL_CORALS).flatMap(blocks -> blocks.getRandomElement(world.random)).map(blockEntry -> blockEntry.value().defaultBlockState()).orElse(blockState);
                    if (blockState.hasProperty(BaseCoralWallFanBlock.FACING)) {
                        blockState = blockState.setValue(BaseCoralWallFanBlock.FACING, facing);
                    }
                } else if (random.nextInt(4) == 0) {
                    blockState = BuiltInRegistries.BLOCK.getTag(BlockTags.UNDERWATER_BONEMEALS).flatMap(blocks -> blocks.getRandomElement(world.random)).map(blockEntry -> blockEntry.value().defaultBlockState()).orElse(blockState);
                }
            }
            if (blockState.is(BlockTags.WALL_CORALS, state -> state.hasProperty(BaseCoralWallFanBlock.FACING))) {
                for (int k = 0; !blockState.canSurvive(world, blockPos2) && k < 4; ++k) {
                    blockState = blockState.setValue(BaseCoralWallFanBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(random));
                }
            }
            if (!blockState.canSurvive(world, blockPos2)) continue;
            BlockState blockState2 = world.getBlockState(blockPos2);
            if (blockState2.is(Blocks.WATER) && world.getFluidState(blockPos2).getAmount() == 8) {
                world.setBlock(blockPos2, blockState, Block.UPDATE_ALL);
                continue;
            }
            if (!blockState2.is(Blocks.SEAGRASS) || random.nextInt(10) != 0) continue;
            ((BonemealableBlock) Blocks.SEAGRASS).performBonemeal((ServerLevel) world, random, blockPos2, blockState2);
        }
        damage(stack, playerEntity);
        return true;
    }

    public static void damage(ItemStack stack, Player entity) {
        if (entity.getAbilities().instabuild) return;
        int damage = stack.getDamageValue();
        if (damage < 5) {
            stack.setDamageValue(damage + 1);
        }
    }

    @Nullable
    @Override
    protected BlockState getPlacementState(BlockPlaceContext blockPlaceContext) {
        BlockState state = super.getPlacementState(blockPlaceContext);
        if (state != null)
            state = state.setValue(WateringCanBlock.DAMAGE, blockPlaceContext.getItemInHand().getDamageValue());
        return state;
    }
}