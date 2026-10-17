package net.satisfy.meadow.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.satisfy.foundation.registry.FoundationParticles;
import net.satisfy.foundation.util.ImplementedInventory;
import net.satisfy.foundation.util.LibUtil;
import net.satisfy.meadow.core.cooking.CookingLiquid;
import net.satisfy.meadow.core.cooking.CookingLiquids;
import net.satisfy.meadow.core.recipes.CookingCauldronRecipe;
import net.satisfy.meadow.core.registry.EntityTypeRegistry;
import net.satisfy.meadow.core.registry.ObjectRegistry;
import net.satisfy.meadow.core.registry.RecipeRegistry;
import net.satisfy.meadow.core.registry.TagRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.satisfy.meadow.platform.PlatformHelper;

public class CookingCauldronBlockEntity extends BlockEntity implements ImplementedInventory {
    public static final int INGREDIENT_SLOTS = 4;
    public static final int RESULT_SLOT = 4;
    public static final int MAX_STACK = 64;
    private static final int[] SLOTS_FOR_DOWN = {RESULT_SLOT};
    private static final int[] SLOTS_FOR_REST = {0, 1, 2, 3};
    private static final int FOAM_MIN_TICKS = 100;
    private static final int FOAM_MAX_TICKS = 200;
    private static final int SYNC_INTERVAL = 10;

    private final NonNullList<ItemStack> inventory = NonNullList.withSize(INGREDIENT_SLOTS + 1, ItemStack.EMPTY);

    @Nullable
    private ResourceLocation liquid;
    private int liquidAmount;
    private int liquidColor = 0xFF3F76E4;
    private boolean liquidBiomeTint;
    private ResourceLocation liquidTexture = CookingLiquid.DEFAULT_TEXTURE;

    private ItemStack resultContainer = ItemStack.EMPTY;
    private int cookProgress;
    private int cookTotal;
    private boolean foam;
    private int foamTimer;
    private long foamSince;
    private long lastSkim = Long.MIN_VALUE / 2;
    private boolean recipeDirty = true;
    @Nullable
    private CookingCauldronRecipe recipe;

    public CookingCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.COOKING_CAULDRON.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CookingCauldronBlockEntity pot) {
        if (level.isClientSide()) {
            return;
        }
        if (pot.recipeDirty) {
            CookingCauldronRecipe found = pot.findRecipe(level);
            if (found != pot.recipe) {
                pot.cookProgress = 0;
                pot.cookTotal = 0;
                pot.foam = false;
            }
            pot.recipe = found;
            pot.recipeDirty = false;
        }
        CookingCauldronRecipe recipe = pot.recipe;
        if (recipe == null || !pot.isHeated() || !pot.hasLiquidFor(recipe) || !pot.resultFits(recipe)) {
            if (pot.cookTotal != 0 || pot.foam) {
                pot.cookProgress = 0;
                pot.cookTotal = 0;
                pot.foam = false;
                pot.sync();
            }
            return;
        }
        if (pot.cookTotal == 0) {
            pot.cookTotal = Math.max(1, recipe.getCraftingDuration() * 20) * 2;
            pot.foamTimer = nextFoam(level);
            pot.sync();
        }
        pot.cookProgress += pot.foam ? 1 : 2;
        if (!pot.foam && --pot.foamTimer <= 0) {
            pot.foam = true;
            pot.foamSince = level.getGameTime();
            level.playSound(null, pos, SoundEvents.BUBBLE_COLUMN_UPWARDS_INSIDE, SoundSource.BLOCKS, 0.7F, 0.9F);
            pot.sync();
        }
        if (pot.cookProgress >= pot.cookTotal) {
            pot.finish(level, recipe);
        } else if (level.getGameTime() % SYNC_INTERVAL == 0) {
            pot.sync();
        }
    }

    private static int nextFoam(Level level) {
        return FOAM_MIN_TICKS + level.random.nextInt(FOAM_MAX_TICKS - FOAM_MIN_TICKS);
    }

    private void finish(Level level, CookingCauldronRecipe recipe) {
        List<ItemStack> slots = inventory.subList(0, INGREDIENT_SLOTS);
        ItemStack result = recipe.getResultItem();
        ItemStack current = inventory.get(RESULT_SLOT);
        int space = result.getMaxStackSize() - current.getCount();
        int batch = Math.max(1, Math.min(recipe.maxBatch(slots), space / Math.max(1, result.getCount())));
        NonNullList<ItemStack> remainders = NonNullList.create();
        for (ItemStack stack : slots) {
            if (!stack.isEmpty() && stack.getItem().hasCraftingRemainingItem()) {
                remainders.add(new ItemStack(stack.getItem().getCraftingRemainingItem(), batch));
            }
        }
        recipe.consume(slots, batch);
        result.setCount(result.getCount() * batch);
        recipe.getLiquid().ifPresent(requirement -> drainLiquid(requirement.amount()));
        if (current.isEmpty()) {
            inventory.set(RESULT_SLOT, result);
        } else {
            current.grow(result.getCount());
        }
        resultContainer = recipe.getContainer().copyWithCount(1);
        for (ItemStack remainder : remainders) {
            while (!remainder.isEmpty()) {
                popOut(level, remainder.split(remainder.getMaxStackSize()));
            }
        }
        cookProgress = 0;
        cookTotal = 0;
        foam = false;
        recipeDirty = true;
        level.playSound(null, worldPosition, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.5F, 1.2F);
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(FoundationParticles.SOUP_STEAM.get(), worldPosition.getX() + 0.5, worldPosition.getY() + surfaceY(), worldPosition.getZ() + 0.5, 4, 0.12, 0.02, 0.12, 0.02);
        }
        sync();
    }

    public ItemInteractionResult interact(Level level, Player player, InteractionHand hand, ItemStack stack) {
        if (foam && (stack.isEmpty() || stack.is(Items.BOWL))) {
            if (!level.isClientSide()) {
                skim(level);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        }

        ItemStack result = inventory.get(RESULT_SLOT);
        if (!result.isEmpty()) {
            if (!resultContainer.isEmpty() && ItemStack.isSameItem(stack, resultContainer)) {
                if (!level.isClientSide()) {
                    stack.consume(1, player);
                    give(player, result.split(1));
                    afterTake(level);
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide());
            }
            if (resultContainer.isEmpty() && stack.isEmpty() && !player.isShiftKeyDown()) {
                if (!level.isClientSide()) {
                    give(player, result.copy());
                    result.setCount(0);
                    afterTake(level);
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide());
            }
        }

        if (stack.isEmpty() && player.isShiftKeyDown()) {
            for (int i = INGREDIENT_SLOTS - 1; i >= 0; i--) {
                if (!inventory.get(i).isEmpty()) {
                    if (!level.isClientSide()) {
                        give(player, inventory.get(i).copy());
                        inventory.set(i, ItemStack.EMPTY);
                        level.playSound(null, worldPosition, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.4F, 1.0F);
                        changed();
                    }
                    return ItemInteractionResult.sidedSuccess(level.isClientSide());
                }
            }
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (stack.isEmpty()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        Optional<Map.Entry<ResourceLocation, CookingLiquid.Fill>> fill = CookingLiquids.findFill(stack);
        if (fill.isPresent()) {
            ResourceLocation id = fill.get().getKey();
            CookingLiquid.Fill entry = fill.get().getValue();
            if ((liquid == null || liquid.equals(id)) && liquidAmount + entry.amount() <= CookingLiquids.CAPACITY) {
                if (!level.isClientSide()) {
                    CookingLiquid definition = CookingLiquids.get(id);
                    liquid = id;
                    liquidAmount += entry.amount();
                    if (definition != null) {
                        liquidColor = definition.color();
                        liquidBiomeTint = definition.biomeTint();
                        liquidTexture = definition.texture();
                    }
                    ItemStack remainder = entry.remainderStack();
                    stack.consume(1, player);
                    if (!remainder.isEmpty()) {
                        give(player, remainder);
                    }
                    level.playSound(null, worldPosition, entry.amount() >= CookingLiquids.CAPACITY ? SoundEvents.BUCKET_EMPTY : SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 0.8F, 1.0F);
                    changed();
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide());
            }
        }

        if (isIngredient(level, stack)) {
            int amount = player.isShiftKeyDown() ? stack.getCount() : 1;
            int inserted = insert(stack, amount);
            if (inserted > 0) {
                if (!level.isClientSide()) {
                    ItemStack added = stack.copyWithCount(1);
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(inserted);
                    }
                    level.playSound(null, worldPosition, liquid != null ? SoundEvents.GENERIC_SPLASH : SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.4F, 1.3F + level.random.nextFloat() * 0.2F);
                    if (level instanceof ServerLevel serverLevel && liquid != null) {
                        serverLevel.sendParticles(ParticleTypes.SPLASH, worldPosition.getX() + 0.5, worldPosition.getY() + surfaceY(), worldPosition.getZ() + 0.5, 6, 0.1, 0.02, 0.1, 0.05);
                    }
                    changed();
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide());
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private void skim(Level level) {
        foam = false;
        cookProgress = Math.min(cookTotal - 1, cookProgress + Math.round(cookTotal * PlatformHelper.getCookingPotSkimBonus()));
        foamTimer = nextFoam(level);
        lastSkim = level.getGameTime();
        level.playSound(null, worldPosition, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 0.5F, 1.4F);
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SPLASH, worldPosition.getX() + 0.5, worldPosition.getY() + surfaceY(), worldPosition.getZ() + 0.5, 8, 0.12, 0.02, 0.12, 0.05);
        }
        sync();
    }

    private void afterTake(Level level) {
        if (inventory.get(RESULT_SLOT).isEmpty()) {
            inventory.set(RESULT_SLOT, ItemStack.EMPTY);
            resultContainer = ItemStack.EMPTY;
        }
        level.playSound(null, worldPosition, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.5F, 1.0F);
        recipeDirty = true;
        sync();
    }

    private int insert(ItemStack stack, int amount) {
        for (int i = 0; i < INGREDIENT_SLOTS; i++) {
            ItemStack slot = inventory.get(i);
            if (!slot.isEmpty() && ItemStack.isSameItemSameComponents(slot, stack)) {
                int moved = Math.min(amount, MAX_STACK - slot.getCount());
                if (moved > 0) {
                    slot.grow(moved);
                    return moved;
                }
                return 0;
            }
        }
        for (int i = 0; i < INGREDIENT_SLOTS; i++) {
            if (inventory.get(i).isEmpty()) {
                int moved = Math.min(amount, MAX_STACK);
                inventory.set(i, stack.copyWithCount(moved));
                return moved;
            }
        }
        return 0;
    }

    private void drainLiquid(int amount) {
        liquidAmount = Math.max(0, liquidAmount - amount);
        if (liquidAmount == 0) {
            liquid = null;
        }
    }

    private void give(Player player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }

    private void popOut(Level level, ItemStack stack) {
        ItemEntity entity = new ItemEntity(level, worldPosition.getX() + 0.5, worldPosition.getY() + 0.8, worldPosition.getZ() + 0.5, stack);
        entity.setDeltaMovement(level.random.nextGaussian() * 0.05, 0.25, level.random.nextGaussian() * 0.05);
        level.addFreshEntity(entity);
    }

    public static boolean isIngredient(Level level, ItemStack stack) {
        for (RecipeHolder<CookingCauldronRecipe> holder : level.getRecipeManager().getAllRecipesFor(RecipeRegistry.COOKING.get())) {
            for (Ingredient ingredient : holder.value().getIngredients()) {
                if (ingredient.test(stack)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Nullable
    private CookingCauldronRecipe findRecipe(Level level) {
        CookingCauldronRecipe best = null;
        for (RecipeHolder<CookingCauldronRecipe> holder : level.getRecipeManager().getAllRecipesFor(RecipeRegistry.COOKING.get())) {
            CookingCauldronRecipe candidate = holder.value();
            if (candidate.matches(inventory.subList(0, INGREDIENT_SLOTS)) && (best == null || candidate.getIngredients().size() > best.getIngredients().size())) {
                best = candidate;
            }
        }
        return best;
    }

    public boolean hasLiquidFor(CookingCauldronRecipe recipe) {
        return recipe.getLiquid().map(requirement -> requirement.id().equals(liquid) && liquidAmount >= requirement.amount()).orElse(true);
    }

    private boolean resultFits(CookingCauldronRecipe recipe) {
        ItemStack current = inventory.get(RESULT_SLOT);
        if (current.isEmpty()) {
            return true;
        }
        ItemStack result = recipe.getResultItem();
        return ItemStack.isSameItemSameComponents(current, result)
                && ItemStack.isSameItem(resultContainer, recipe.getContainer())
                && current.getCount() + result.getCount() <= current.getMaxStackSize();
    }

    public boolean isHeated() {
        if (level == null) {
            return false;
        }
        if (getBlockState().is(ObjectRegistry.COOKING_FRAME.get())) {
            return true;
        }
        return level.getBlockState(worldPosition.below()).is(TagRegistry.ALLOWS_COOKING);
    }

    public boolean isCooking() {
        return cookTotal > 0;
    }

    public boolean isPotEmpty() {
        return liquid == null && inventory.stream().allMatch(ItemStack::isEmpty);
    }

    public float cookProgress() {
        return cookTotal <= 0 ? 0.0F : Math.min(1.0F, cookProgress / (float) cookTotal);
    }

    public boolean hasFoam() {
        return foam;
    }

    public long getFoamSince() {
        return foamSince;
    }

    public long getLastSkim() {
        return lastSkim;
    }

    public NonNullList<ItemStack> getIngredients() {
        NonNullList<ItemStack> list = NonNullList.create();
        for (int i = 0; i < INGREDIENT_SLOTS; i++) {
            if (!inventory.get(i).isEmpty()) {
                list.add(inventory.get(i));
            }
        }
        return list;
    }

    public ItemStack getResult() {
        return inventory.get(RESULT_SLOT);
    }

    public ItemStack getResultContainer() {
        return resultContainer;
    }

    @Nullable
    public ResourceLocation getLiquid() {
        return liquid;
    }

    public int getLiquidAmount() {
        return liquidAmount;
    }

    public int getLiquidColor() {
        return liquidColor;
    }

    public boolean isLiquidBiomeTint() {
        return liquidBiomeTint;
    }

    public ResourceLocation getLiquidTexture() {
        return liquidTexture;
    }

    @Nullable
    public CookingCauldronRecipe getRecipe() {
        if (recipeDirty && level != null && level.isClientSide()) {
            recipe = findRecipe(level);
            recipeDirty = false;
        }
        return recipe;
    }

    public double surfaceY() {
        return getBlockState().is(ObjectRegistry.COOKING_FRAME.get())
                ? (11.05 + 6.5 * liquidAmount / CookingLiquids.CAPACITY) / 16.0
                : (1.0 + 7.0 * liquidAmount / CookingLiquids.CAPACITY) / 16.0;
    }

    public NonNullList<ItemStack> drops() {
        NonNullList<ItemStack> drops = NonNullList.create();
        for (ItemStack stack : inventory) {
            if (!stack.isEmpty()) {
                drops.add(stack.copy());
            }
        }
        return drops;
    }

    private void changed() {
        recipeDirty = true;
        sync();
    }

    private void sync() {
        setChanged();
        if (level instanceof ServerLevel serverLevel) {
            Packet<ClientGamePacketListener> packet = getUpdatePacket();
            for (ServerPlayer player : LibUtil.tracking(serverLevel, worldPosition)) {
                player.connection.send(packet);
            }
        }
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        inventory.set(slot, stack);
        if (slot == RESULT_SLOT && stack.isEmpty()) {
            resultContainer = ItemStack.EMPTY;
        }
        changed();
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int count) {
        ItemStack removed = ContainerHelper.removeItem(inventory, slot, count);
        if (!removed.isEmpty()) {
            if (slot == RESULT_SLOT && inventory.get(RESULT_SLOT).isEmpty()) {
                resultContainer = ItemStack.EMPTY;
            }
            changed();
        }
        return removed;
    }

    @Override
    public int getMaxStackSize() {
        return MAX_STACK;
    }

    @Override
    public int @NotNull [] getSlotsForFace(Direction side) {
        return side == Direction.DOWN ? SLOTS_FOR_DOWN : SLOTS_FOR_REST;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        if (slot >= INGREDIENT_SLOTS || level == null || !isIngredient(level, stack)) {
            return false;
        }
        ItemStack current = inventory.get(slot);
        if (current.isEmpty()) {
            for (int i = 0; i < INGREDIENT_SLOTS; i++) {
                if (i != slot && ItemStack.isSameItemSameComponents(inventory.get(i), stack)) {
                    return false;
                }
            }
            return true;
        }
        return ItemStack.isSameItemSameComponents(current, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return slot == RESULT_SLOT && resultContainer.isEmpty();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return slot < INGREDIENT_SLOTS;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        for (int i = 0; i < inventory.size(); i++) {
            inventory.set(i, ItemStack.EMPTY);
        }
        ContainerHelper.loadAllItems(tag, inventory, provider);
        liquid = tag.contains("Liquid") ? ResourceLocation.tryParse(tag.getString("Liquid")) : null;
        liquidAmount = tag.getInt("LiquidAmount");
        liquidColor = tag.contains("LiquidColor") ? tag.getInt("LiquidColor") : 0xFF3F76E4;
        liquidBiomeTint = tag.getBoolean("LiquidBiomeTint");
        ResourceLocation texture = tag.contains("LiquidTexture") ? ResourceLocation.tryParse(tag.getString("LiquidTexture")) : null;
        liquidTexture = texture != null ? texture : CookingLiquid.DEFAULT_TEXTURE;
        resultContainer = tag.contains("ResultContainer") ? ItemStack.parseOptional(provider, tag.getCompound("ResultContainer")) : ItemStack.EMPTY;
        cookProgress = tag.getInt("CookProgress");
        cookTotal = tag.getInt("CookTotal");
        foam = tag.getBoolean("Foam");
        foamTimer = tag.getInt("FoamTimer");
        foamSince = tag.getLong("FoamSince");
        lastSkim = tag.contains("LastSkim") ? tag.getLong("LastSkim") : Long.MIN_VALUE / 2;
        recipeDirty = true;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        ContainerHelper.saveAllItems(tag, inventory, provider);
        if (liquid != null) {
            tag.putString("Liquid", liquid.toString());
        }
        tag.putInt("LiquidAmount", liquidAmount);
        tag.putInt("LiquidColor", liquidColor);
        tag.putBoolean("LiquidBiomeTint", liquidBiomeTint);
        tag.putString("LiquidTexture", liquidTexture.toString());
        if (!resultContainer.isEmpty()) {
            tag.put("ResultContainer", resultContainer.save(provider));
        }
        tag.putInt("CookProgress", cookProgress);
        tag.putInt("CookTotal", cookTotal);
        tag.putBoolean("Foam", foam);
        tag.putInt("FoamTimer", foamTimer);
        tag.putLong("FoamSince", foamSince);
        tag.putLong("LastSkim", lastSkim);
    }

    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition).expandTowards(0, 1, 0);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return saveWithoutMetadata(provider);
    }
}
