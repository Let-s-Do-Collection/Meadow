package net.satisfy.meadow.core.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.satisfy.meadow.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Predicate;

import static net.minecraft.world.level.block.LayeredCauldronBlock.LEVEL;

@SuppressWarnings("unused")
public interface WoodenCauldronBehavior extends CauldronInteraction {
    InteractionMap EMPTY = CauldronInteraction.newInteractionMap("empty");
    InteractionMap WATER = CauldronInteraction.newInteractionMap("water");
    InteractionMap POWDER_SNOW = CauldronInteraction.newInteractionMap("powder_snow");

    CauldronInteraction FILL_WATER = (state, world, pos, player, hand, stack) -> WoodenCauldronBehavior.fillCauldron(world, pos, player, hand, stack, ObjectRegistry.WOODEN_WATER_CAULDRON.get().defaultBlockState().setValue(LEVEL, 3), SoundEvents.BUCKET_EMPTY, Items.BUCKET);
    CauldronInteraction FILL_POWDER_SNOW = (state, world, pos, player, hand, stack) -> WoodenCauldronBehavior.fillCauldron(world, pos, player, hand, stack, ObjectRegistry.WOODEN_POWDER_SNOW_CAULDRON.get().defaultBlockState().setValue(LEVEL, 3), SoundEvents.BUCKET_EMPTY_POWDER_SNOW, Items.BUCKET);
    CauldronInteraction FILL_WITH_WATER_W = (state, world, pos, player, hand, stack) -> WoodenCauldronBehavior.fillCauldron(world, pos, player, hand, stack, ObjectRegistry.WOODEN_WATER_CAULDRON.get().defaultBlockState().setValue(LEVEL, 3), SoundEvents.BUCKET_EMPTY, ObjectRegistry.WOODEN_BUCKET.get());

    @NotNull
    ItemInteractionResult interact(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, ItemStack var6);

    static void bootStrap() {
        registerCauldronBehavior();
        WoodenCauldronBehavior.addDefaultInteractions(EMPTY);
        Map<Item, CauldronInteraction> map = EMPTY.map();
        map.put(Items.POTION, ((blockState, level, blockPos, player, interactionHand, itemStack) -> {
            if(itemStack.get(DataComponents.POTION_CONTENTS) != null || !itemStack.get(DataComponents.POTION_CONTENTS).is(Potions.WATER)){
                return ItemInteractionResult.FAIL;
            }

            if (!level.isClientSide) {
                Item item = itemStack.getItem();
                player.setItemInHand(interactionHand, ItemUtils.createFilledResult(itemStack, player, new ItemStack(Items.GLASS_BOTTLE)));
                player.awardStat(Stats.ITEM_USED.get(item));
                level.setBlockAndUpdate(blockPos, ObjectRegistry.WOODEN_WATER_CAULDRON.get().defaultBlockState());
                level.playSound(null, blockPos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
                level.gameEvent(null, GameEvent.FLUID_PLACE, blockPos);
            }
            return ItemInteractionResult.SUCCESS;
        }));

        WoodenCauldronBehavior.addDefaultInteractions(WATER);

        WATER.map().put(Items.BUCKET, ((blockState, level, blockPos, player, interactionHand, itemStack) -> WoodenCauldronBehavior.fillBucket(blockState, level, blockPos, player, interactionHand, itemStack, new ItemStack(Items.WATER_BUCKET), state -> state.getValue(LEVEL) == 3, SoundEvents.BUCKET_FILL)));
        WATER.map().put(ObjectRegistry.WOODEN_BUCKET.get(), ((blockState, level, blockPos, player, interactionHand, itemStack) -> WoodenCauldronBehavior.fillBucket(blockState, level, blockPos, player, interactionHand, itemStack, new ItemStack(ObjectRegistry.WOODEN_WATER_BUCKET.get()), state -> state.getValue(LEVEL) == 3, SoundEvents.BUCKET_FILL)));
        WATER.map().put(Items.GLASS_BOTTLE, ((blockState, level, blockPos, player, interactionHand, itemStack) -> {
            if (!level.isClientSide) {
                Item item = itemStack.getItem();
                player.setItemInHand(interactionHand, ItemUtils.createFilledResult(itemStack, player, PotionContents.createItemStack(Items.POTION, Potions.WATER)));
                player.awardStat(Stats.ITEM_USED.get(item));
                LayeredCauldronBlock.lowerFillLevel(blockState, level, blockPos);
                level.playSound(null, blockPos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
                level.gameEvent(null, GameEvent.FLUID_PICKUP, blockPos);
            }
            return ItemInteractionResult.SUCCESS;
        }));
        WATER.map().put(Items.POTION, ((blockState, level, blockPos, player, interactionHand, itemStack) -> {
            if (itemStack.get(DataComponents.POTION_CONTENTS) != null || !itemStack.get(DataComponents.POTION_CONTENTS).is(Potions.WATER)) {
                return ItemInteractionResult.FAIL;
            }
            if (!level.isClientSide) {
                Item item = itemStack.getItem();
                player.setItemInHand(interactionHand, ItemUtils.createFilledResult(itemStack, player, new ItemStack(Items.GLASS_BOTTLE)));
                player.awardStat(Stats.ITEM_USED.get(item));
                level.setBlockAndUpdate(blockPos, blockState.cycle(LEVEL));
                level.playSound(null, blockPos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
                level.gameEvent(null, GameEvent.FLUID_PLACE, blockPos);
            }
            return ItemInteractionResult.SUCCESS;
        }));


        WATER.map().put(Items.LEATHER_BOOTS, DYED_ITEM);
        WATER.map().put(Items.LEATHER_LEGGINGS, DYED_ITEM);
        WATER.map().put(Items.LEATHER_CHESTPLATE, DYED_ITEM);
        WATER.map().put(Items.LEATHER_HELMET, DYED_ITEM);
        WATER.map().put(Items.LEATHER_HORSE_ARMOR, DYED_ITEM);
        WATER.map().put(Items.WHITE_BANNER, BANNER);
        WATER.map().put(Items.GRAY_BANNER, BANNER);
        WATER.map().put(Items.BLACK_BANNER, BANNER);
        WATER.map().put(Items.BLUE_BANNER, BANNER);
        WATER.map().put(Items.BROWN_BANNER, BANNER);
        WATER.map().put(Items.CYAN_BANNER, BANNER);
        WATER.map().put(Items.GREEN_BANNER, BANNER);
        WATER.map().put(Items.LIGHT_BLUE_BANNER, BANNER);
        WATER.map().put(Items.LIGHT_GRAY_BANNER, BANNER);
        WATER.map().put(Items.LIME_BANNER, BANNER);
        WATER.map().put(Items.MAGENTA_BANNER, BANNER);
        WATER.map().put(Items.YELLOW_BANNER, BANNER);
        WATER.map().put(Items.PINK_BANNER, BANNER);
        WATER.map().put(Items.PURPLE_BANNER, BANNER);
        WATER.map().put(Items.RED_BANNER, BANNER);
        WATER.map().put(Items.YELLOW_BANNER, BANNER);
        WATER.map().put(Items.WHITE_SHULKER_BOX, SHULKER_BOX);
        WATER.map().put(Items.GRAY_SHULKER_BOX, SHULKER_BOX);
        WATER.map().put(Items.BLACK_SHULKER_BOX, SHULKER_BOX);
        WATER.map().put(Items.BLUE_SHULKER_BOX, SHULKER_BOX);
        WATER.map().put(Items.BROWN_SHULKER_BOX, SHULKER_BOX);
        WATER.map().put(Items.CYAN_SHULKER_BOX, SHULKER_BOX);
        WATER.map().put(Items.GREEN_SHULKER_BOX, SHULKER_BOX);
        WATER.map().put(Items.LIGHT_BLUE_SHULKER_BOX, SHULKER_BOX);
        WATER.map().put(Items.LIGHT_GRAY_SHULKER_BOX, SHULKER_BOX);
        WATER.map().put(Items.LIME_SHULKER_BOX, SHULKER_BOX);
        WATER.map().put(Items.MAGENTA_SHULKER_BOX, SHULKER_BOX);
        WATER.map().put(Items.YELLOW_SHULKER_BOX, SHULKER_BOX);
        WATER.map().put(Items.PINK_SHULKER_BOX, SHULKER_BOX);
        WATER.map().put(Items.PURPLE_SHULKER_BOX, SHULKER_BOX);
        WATER.map().put(Items.RED_SHULKER_BOX, SHULKER_BOX);
        WATER.map().put(Items.YELLOW_SHULKER_BOX, SHULKER_BOX);
        POWDER_SNOW.map().put(Items.BUCKET, (state2, world, pos, player, hand, stack) -> WoodenCauldronBehavior.fillBucket(state2, world, pos, player, hand, stack, new ItemStack(Items.POWDER_SNOW_BUCKET), state -> state.getValue(LEVEL) == 3, SoundEvents.BUCKET_FILL_POWDER_SNOW));
        WoodenCauldronBehavior.addDefaultInteractions(POWDER_SNOW);
    }

    static void addDefaultInteractions(InteractionMap behavior) {
        behavior.map().put(Items.WATER_BUCKET, FILL_WATER);
        behavior.map().put(ObjectRegistry.WOODEN_WATER_BUCKET.get(), FILL_WITH_WATER_W);
        behavior.map().put(Items.POWDER_SNOW_BUCKET, FILL_POWDER_SNOW);
    }

    static ItemInteractionResult fillBucket(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack stack, ItemStack output, Predicate<BlockState> predicate, SoundEvent soundEvent) {
        if (!predicate.test(state)) {
            return ItemInteractionResult.FAIL;
        }
        if (!world.isClientSide) {
            Item item = stack.getItem();
            player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, output));
            player.awardStat(Stats.ITEM_USED.get(item));
            world.setBlockAndUpdate(pos, ObjectRegistry.WOODEN_CAULDRON.get().defaultBlockState());
            world.playSound(null, pos, soundEvent, SoundSource.BLOCKS, 1.0f, 1.0f);
            world.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
        }
        return ItemInteractionResult.sidedSuccess(world.isClientSide);
    }

    static ItemInteractionResult fillCauldron(Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack stack, BlockState state, SoundEvent soundEvent, Item returnItem) {
        if (!world.isClientSide) {
            Item item = stack.getItem();
            player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(returnItem)));
            player.awardStat(Stats.FILL_CAULDRON);
            player.awardStat(Stats.ITEM_USED.get(item));
            world.setBlockAndUpdate(pos, state);
            world.playSound(null, pos, soundEvent, SoundSource.BLOCKS, 1.0f, 1.0f);
            world.gameEvent(null, GameEvent.FLUID_PLACE, pos);
        }
        return ItemInteractionResult.sidedSuccess(world.isClientSide);
    }

    static void registerCauldronBehavior() {
        CauldronInteraction.WATER.map().put(ObjectRegistry.WOODEN_BUCKET.get(), (state2, world, pos, player, hand, stack) -> CauldronInteraction.fillBucket(state2, world, pos, player, hand, stack, new ItemStack(ObjectRegistry.WOODEN_WATER_BUCKET.get()), state -> state.getValue(LEVEL) == 3, SoundEvents.BUCKET_FILL));
        registerBucketBehaviorForNormalCauldron(CauldronInteraction.EMPTY.map());
    }

    static void registerBucketBehaviorForNormalCauldron(Map<Item, CauldronInteraction> behavior) {
        CauldronInteraction fillWithWater = (state, world, pos, player, hand, stack) -> fillCauldron(world, pos, player, hand, stack, Blocks.WATER_CAULDRON.defaultBlockState().setValue(LEVEL, 3), SoundEvents.BUCKET_EMPTY, ObjectRegistry.WOODEN_BUCKET.get());
        CauldronInteraction fillWithPowderSnow = (state, world, pos, player, hand, stack) -> fillCauldron(world, pos, player, hand, stack, Blocks.POWDER_SNOW_CAULDRON.defaultBlockState().setValue(LEVEL, 3), SoundEvents.BUCKET_EMPTY_POWDER_SNOW, ObjectRegistry.WOODEN_BUCKET.get());
        behavior.put(ObjectRegistry.WOODEN_WATER_BUCKET.get(), fillWithWater);
    }

}