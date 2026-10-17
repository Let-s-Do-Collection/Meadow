package net.satisfy.meadow.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.foundation.util.LibUtil;
import net.satisfy.meadow.core.recipes.CheeseFormRecipe;
import net.satisfy.meadow.core.registry.EntityTypeRegistry;
import net.satisfy.meadow.core.registry.RecipeRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.satisfy.meadow.platform.PlatformHelper;

public class CheeseFormBlockEntity extends BlockEntity {
    public static final int MAX_MILK = 4;
    public static final int CRANK_TICKS = 40;
    private static final int CRANK_HOLD_TICKS = 6;
    private static final int CRANK_SOUND_INTERVAL = 5;
    private static final int SYNC_INTERVAL = 20;

    private ItemStack milk = ItemStack.EMPTY;
    private int milkCount;
    private ItemStack rennet = ItemStack.EMPTY;
    private ItemStack result = ItemStack.EMPTY;
    private int resultCount;
    private int crank;
    private int crankTicks;
    private int pressTicks;

    public CheeseFormBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.CHEESE_FORM_BLOCK_ENTITY.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CheeseFormBlockEntity press) {
        if (!level.isClientSide() && press.crank > 0 && press.pressTicks == 0 && (press.milkCount == 0 || press.rennet.isEmpty())) {
            press.crank = 0;
            press.crankTicks = 0;
            press.setChanged();
        }
        if (press.crankTicks > 0) {
            press.crankTicks--;
            press.turn(level, pos);
        }
        if (press.isPressed() && !press.isReady()) {
            press.pressTicks++;
            if (!level.isClientSide()) {
                if (press.isReady()) {
                    level.playSound(null, pos, SoundEvents.SLIME_SQUISH_SMALL, SoundSource.BLOCKS, 0.6F, 0.8F);
                    press.setChanged();
                } else if (press.pressTicks % SYNC_INTERVAL == 0) {
                    press.setChanged();
                }
            }
        }
    }

    private void turn(Level level, BlockPos pos) {
        boolean up = isReady();
        if (!up && (milkCount == 0 || rennet.isEmpty())) {
            crankTicks = 0;
            return;
        }
        int before = crank;
        crank = Math.max(0, Math.min(CRANK_TICKS, crank + (up ? -1 : 1)));
        if (crank == before) {
            crankTicks = 0;
            return;
        }
        if (level.isClientSide()) {
            return;
        }
        if (crank % CRANK_SOUND_INTERVAL == 0) {
            level.playSound(null, pos, SoundEvents.WOODEN_BUTTON_CLICK_ON, SoundSource.BLOCKS, 0.3F, 0.6F + level.random.nextFloat() * 0.2F);
        }
        if (crank == CRANK_TICKS) {
            level.playSound(null, pos, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 0.8F, 0.7F);
            setChanged();
        } else if (up && crank == 0) {
            finish(level);
        } else if (crank % CRANK_SOUND_INTERVAL == 0) {
            setChanged();
        }
    }

    private void finish(Level level) {
        CheeseFormRecipe recipe = recipeFor(level, milk);
        result = recipe == null ? ItemStack.EMPTY : recipe.getResultItem(level.registryAccess()).copy();
        resultCount = result.getCount() * milkCount;
        result.setCount(1);
        milk = ItemStack.EMPTY;
        milkCount = 0;
        rennet = ItemStack.EMPTY;
        pressTicks = 0;
        crank = 0;
        crankTicks = 0;
        level.playSound(null, worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 0.8F, 0.8F);
        setChanged();
    }

    public boolean crank() {
        if (!canCrank()) {
            return false;
        }
        boolean started = crankTicks <= 0;
        crankTicks = CRANK_HOLD_TICKS;
        if (started) {
            setChanged();
        }
        return true;
    }

    public boolean canCrank() {
        if (!result.isEmpty()) {
            return false;
        }
        if (isReady()) {
            return crank > 0;
        }
        return crank < CRANK_TICKS && milkCount > 0 && !rennet.isEmpty();
    }

    public boolean canAddMilk(Level level, ItemStack stack) {
        if (!isOpen() || milkCount >= MAX_MILK || (!milk.isEmpty() && !ItemStack.isSameItem(milk, stack))) {
            return false;
        }
        CheeseFormRecipe recipe = recipeFor(level, stack);
        return recipe != null && (rennet.isEmpty() || recipe.ingredient().test(rennet));
    }

    public boolean canAddRennet(Level level, ItemStack stack) {
        if (!isOpen() || !rennet.isEmpty()) {
            return false;
        }
        for (RecipeHolder<CheeseFormRecipe> holder : level.getRecipeManager().getAllRecipesFor(RecipeRegistry.CHEESE.get())) {
            CheeseFormRecipe recipe = holder.value();
            if (recipe.ingredient().test(stack) && (milk.isEmpty() || recipe.bucket().test(milk))) {
                return true;
            }
        }
        return false;
    }

    public void addMilk(ItemStack stack) {
        if (milk.isEmpty()) {
            milk = stack.copyWithCount(1);
        }
        milkCount++;
        setChanged();
    }

    public void addRennet(ItemStack stack) {
        rennet = stack.copyWithCount(1);
        setChanged();
    }

    public NonNullList<ItemStack> takeResult() {
        NonNullList<ItemStack> taken = NonNullList.create();
        for (int i = 0; i < resultCount; i++) {
            taken.add(result.copyWithCount(1));
        }
        result = ItemStack.EMPTY;
        resultCount = 0;
        setChanged();
        return taken;
    }

    @Nullable
    public static CheeseFormRecipe recipeFor(Level level, ItemStack milk) {
        if (milk.isEmpty()) {
            return null;
        }
        CheeseFormRecipe best = null;
        int bestChoices = Integer.MAX_VALUE;
        for (RecipeHolder<CheeseFormRecipe> holder : level.getRecipeManager().getAllRecipesFor(RecipeRegistry.CHEESE.get())) {
            CheeseFormRecipe recipe = holder.value();
            int choices = recipe.bucket().getItems().length;
            if (recipe.bucket().test(milk) && choices < bestChoices) {
                best = recipe;
                bestChoices = choices;
            }
        }
        return best;
    }

    public boolean isOpen() {
        return crank == 0 && pressTicks == 0 && result.isEmpty();
    }

    public boolean isPressed() {
        return crank >= CRANK_TICKS || pressTicks > 0;
    }

    public boolean isReady() {
        return pressTicks >= PlatformHelper.getCheesePressDuration();
    }

    public boolean isCranking() {
        return crankTicks > 0;
    }

    public float crankProgress(float partialTick) {
        float step = crankTicks > 0 ? (isReady() ? -partialTick : partialTick) : 0.0F;
        return Math.max(0.0F, Math.min(1.0F, (crank + step) / CRANK_TICKS));
    }

    public float pressProgress() {
        return Math.min(1.0F, (float) pressTicks / PlatformHelper.getCheesePressDuration());
    }

    public ItemStack getMilk() {
        return milk;
    }

    public int getMilkCount() {
        return milkCount;
    }

    public ItemStack getRennet() {
        return rennet;
    }

    public ItemStack getResult() {
        return result;
    }

    public int getResultCount() {
        return resultCount;
    }

    public NonNullList<ItemStack> drops() {
        NonNullList<ItemStack> drops = NonNullList.create();
        for (int i = 0; i < resultCount; i++) {
            drops.add(result.copyWithCount(1));
        }
        if (!rennet.isEmpty()) {
            drops.add(rennet.copy());
        }
        for (int i = 0; i < milkCount; i++) {
            drops.add(milk.copyWithCount(1));
        }
        return drops;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        milk = tag.contains("Milk") ? ItemStack.parse(provider, tag.getCompound("Milk")).orElse(ItemStack.EMPTY) : ItemStack.EMPTY;
        rennet = tag.contains("Rennet") ? ItemStack.parse(provider, tag.getCompound("Rennet")).orElse(ItemStack.EMPTY) : ItemStack.EMPTY;
        result = tag.contains("Result") ? ItemStack.parse(provider, tag.getCompound("Result")).orElse(ItemStack.EMPTY) : ItemStack.EMPTY;
        milkCount = milk.isEmpty() ? 0 : tag.getInt("MilkCount");
        resultCount = result.isEmpty() ? 0 : tag.getInt("ResultCount");
        crank = tag.getInt("Crank");
        crankTicks = tag.getInt("CrankTicks");
        pressTicks = tag.getInt("PressTicks");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        if (!milk.isEmpty()) {
            tag.put("Milk", milk.save(provider));
        }
        if (!rennet.isEmpty()) {
            tag.put("Rennet", rennet.save(provider));
        }
        if (!result.isEmpty()) {
            tag.put("Result", result.save(provider));
        }
        tag.putInt("MilkCount", milkCount);
        tag.putInt("ResultCount", resultCount);
        tag.putInt("Crank", crank);
        tag.putInt("CrankTicks", crankTicks);
        tag.putInt("PressTicks", pressTicks);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return saveWithoutMetadata(provider);
    }

    @Override
    public void setChanged() {
        if (level instanceof ServerLevel serverLevel) {
            Packet<ClientGamePacketListener> packet = getUpdatePacket();
            for (ServerPlayer player : LibUtil.tracking(serverLevel, getBlockPos())) {
                player.connection.send(packet);
            }
        }
        super.setChanged();
    }
}
