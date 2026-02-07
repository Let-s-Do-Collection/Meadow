package net.satisfy.meadow.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.meadow.client.gui.handler.CookingCauldronGuiHandler;
import net.satisfy.meadow.core.block.CookingCauldronBlock;
import net.satisfy.meadow.core.recipes.CookingCauldronRecipe;
import net.satisfy.meadow.core.registry.EntityTypeRegistry;
import net.satisfy.meadow.core.registry.ObjectRegistry;
import net.satisfy.meadow.core.registry.RecipeRegistry;
import net.satisfy.meadow.core.registry.TagRegistry;
import net.satisfy.meadow.core.world.ImplementedInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class CookingCauldronBlockEntity extends BlockEntity implements ImplementedInventory, MenuProvider {
    private static final int MAX_CAPACITY = 8;
    private static final int OUTPUT_SLOT = 0;
    public static final int MAX_COOKING_TIME = 200;
    private static final int INGREDIENTS_START = 1;
    private static final int INGREDIENTS_END = 6;
    private static final int FLUID_INPUT_SLOT = 7;
    private static final int[] SLOTS_FOR_REST = {1, 2, 3, 4, 5, 6};
    private static final int[] SLOTS_FOR_DOWN = {0, 7};

    private final NonNullList<ItemStack> inventory = NonNullList.withSize(MAX_CAPACITY, ItemStack.EMPTY);

    private int cookingTime;
    private boolean isBeingBurned;
    private int fluidLevel;
    private int currentCraftingDuration;
    private boolean fluidInputProcessed;

    private final ContainerData delegate = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> cookingTime;
                case 1 -> isBeingBurned ? 1 : 0;
                case 2 -> fluidLevel;
                case 3 -> currentCraftingDuration;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> cookingTime = value;
                case 1 -> isBeingBurned = value != 0;
                case 2 -> fluidLevel = value;
                case 3 -> currentCraftingDuration = value;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public CookingCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.COOKING_CAULDRON.get(), pos, state);
    }

    private RegistryAccess registries() {
        return this.level != null ? this.level.registryAccess() : RegistryAccess.EMPTY;
    }

    @Override
    public int @NotNull [] getSlotsForFace(Direction side) {
        return side == Direction.DOWN ? SLOTS_FOR_DOWN : SLOTS_FOR_REST;
    }

    @Override
    protected void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.loadAdditional(compoundTag, provider);
        ContainerHelper.loadAllItems(compoundTag, inventory, provider);
        cookingTime = compoundTag.getInt("CookingTime");
        isBeingBurned = compoundTag.getBoolean("IsBeingBurned");
        fluidLevel = compoundTag.getInt("FluidLevel");
        currentCraftingDuration = compoundTag.getInt("CurrentCraftingDuration");
        fluidInputProcessed = compoundTag.getBoolean("FluidInputProcessed");
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.saveAdditional(compoundTag, provider);
        ContainerHelper.saveAllItems(compoundTag, inventory, provider);
        compoundTag.putInt("CookingTime", cookingTime);
        compoundTag.putBoolean("IsBeingBurned", isBeingBurned);
        compoundTag.putInt("FluidLevel", fluidLevel);
        compoundTag.putInt("CurrentCraftingDuration", currentCraftingDuration);
        compoundTag.putBoolean("FluidInputProcessed", fluidInputProcessed);
    }

    public boolean isBeingBurned() {
        Level currentLevel = getLevel();
        if (currentLevel == null) return false;
        if (getBlockState().getValue(CookingCauldronBlock.HANGING)) return true;
        return currentLevel.getBlockState(getBlockPos().below()).is(TagRegistry.ALLOWS_COOKING);
    }

    public boolean isBoiling() {
        return cookingTime > 0 && currentCraftingDuration > 0;
    }

    public boolean isFinished() {
        return !getItem(OUTPUT_SLOT).isEmpty();
    }

    private boolean canCraft(CookingCauldronRecipe recipe) {
        if (recipe == null || recipe.getResultItem(registries()).isEmpty()) return false;
        ItemStack outputSlotStack = getItem(OUTPUT_SLOT);
        ItemStack recipeResult = recipe.getResultItem(registries());
        return outputSlotStack.isEmpty() || (ItemStack.isSameItem(outputSlotStack, recipeResult) && outputSlotStack.getCount() < outputSlotStack.getMaxStackSize());
    }

    private void craft(CookingCauldronRecipe recipe) {
        if (!canCraft(recipe)) return;

        ItemStack recipeOutput = recipe.assemble();
        ItemStack outputSlotStack = getItem(OUTPUT_SLOT);

        if (outputSlotStack.isEmpty()) {
            setItem(OUTPUT_SLOT, recipeOutput.copy());
        } else {
            outputSlotStack.grow(recipeOutput.getCount());
        }

        boolean[] ingredientUsed = new boolean[INGREDIENTS_END + 1];

        for (Ingredient ingredient : recipe.getIngredients()) {
            for (int slotIndex = INGREDIENTS_START; slotIndex <= INGREDIENTS_END; slotIndex++) {
                if (!ingredientUsed[slotIndex] && ingredient.test(getItem(slotIndex))) {
                    ingredientUsed[slotIndex] = true;

                    ItemStack stackInSlot = getItem(slotIndex);
                    ItemStack remainderStack = getRemainderItem(stackInSlot);

                    stackInSlot.shrink(1);

                    if (!remainderStack.isEmpty()) {
                        if (stackInSlot.isEmpty()) {
                            setItem(slotIndex, remainderStack);
                        } else {
                            handleRemainder(remainderStack, slotIndex);
                        }
                    }
                    break;
                }
            }
        }

        consumeFluid(recipe.getFluidAmount());
        setChanged();
    }

    private void handleRemainder(ItemStack remainderStack, int originalSlot) {
        if (originalSlot == FLUID_INPUT_SLOT) {
            ItemStack currentFluidSlot = getItem(FLUID_INPUT_SLOT);
            if (currentFluidSlot.isEmpty()) {
                setItem(FLUID_INPUT_SLOT, remainderStack.copy());
            } else {
                dropItemIntoWorld(remainderStack, worldPosition);
            }
            return;
        }

        for (int slotIndex = INGREDIENTS_START; slotIndex <= INGREDIENTS_END; slotIndex++) {
            ItemStack stackInSlot = getItem(slotIndex);
            if (stackInSlot.isEmpty()) {
                setItem(slotIndex, remainderStack.copy());
                return;
            }
            if (ItemStack.isSameItem(stackInSlot, remainderStack) && stackInSlot.getCount() + remainderStack.getCount() <= stackInSlot.getMaxStackSize()) {
                stackInSlot.grow(remainderStack.getCount());
                return;
            }
        }

        dropItemIntoWorld(remainderStack, worldPosition);
    }

    private ItemStack getRemainderItem(ItemStack stack) {
        if (stack.getItem().hasCraftingRemainingItem()) {
            return new ItemStack(Objects.requireNonNull(stack.getItem().getCraftingRemainingItem()));
        }
        return ItemStack.EMPTY;
    }

    private void dropItemIntoWorld(ItemStack itemStack, BlockPos pos) {
        if (level == null || level.isClientSide()) return;

        double offsetX = level.random.nextDouble() * 0.7 + 0.15;
        double offsetY = level.random.nextDouble() * 0.5 + 0.1;
        double offsetZ = level.random.nextDouble() * 0.7 + 0.15;

        ItemEntity itemEntity = new ItemEntity(level, pos.getX() + offsetX, pos.getY() + offsetY, pos.getZ() + offsetZ, itemStack);
        level.addFreshEntity(itemEntity);
    }

    public void dropContents() {
        if (level == null || level.isClientSide()) return;

        for (int slotIndex = 0; slotIndex < inventory.size(); slotIndex++) {
            ItemStack stack = inventory.get(slotIndex);
            if (!stack.isEmpty()) {
                dropItemIntoWorld(stack.copy(), worldPosition);
                inventory.set(slotIndex, ItemStack.EMPTY);
            }
        }

        setChanged();
    }

    private void processFluidInput() {
        ItemStack fluidItem = getItem(FLUID_INPUT_SLOT);
        if (fluidItem.isEmpty()) return;

        int fluidAmount;
        if (fluidItem.is(TagRegistry.SMALL_WATER_FILL)) {
            fluidAmount = 25;
        } else if (fluidItem.is(TagRegistry.LARGE_WATER_FILL)) {
            fluidAmount = 50;
        } else {
            return;
        }

        ItemStack consumedItem = fluidItem.split(1);
        setItem(FLUID_INPUT_SLOT, fluidItem);

        ItemStack remainder = getRemainderItem(consumedItem);
        if (!remainder.isEmpty()) {
            handleRemainder(remainder, FLUID_INPUT_SLOT);
        }

        addFluid(fluidAmount);
        setChanged();
    }

    public void tick(Level world) {
        if (world.isClientSide()) return;

        if (!fluidInputProcessed) {
            processFluidInput();
            fluidInputProcessed = true;
        }

        ItemStack fluidItem = getItem(FLUID_INPUT_SLOT);
        if (fluidItem.isEmpty() || (!fluidItem.is(TagRegistry.SMALL_WATER_FILL) && !fluidItem.is(TagRegistry.LARGE_WATER_FILL))) {
            fluidInputProcessed = false;
        }

        isBeingBurned = isBeingBurned();

        RecipeHolder<CookingCauldronRecipe> recipeHolder = world.getRecipeManager()
                .getRecipeFor(RecipeRegistry.COOKING.get(), CraftingInput.of(1, 6, inventory.subList(1, 7)), world)
                .orElse(null);

        CookingCauldronRecipe recipe = recipeHolder != null ? recipeHolder.value() : null;

        if (isBeingBurned && canCraft(recipe) && fluidLevel >= recipe.getFluidAmount()) {
            if (currentCraftingDuration == 0 && cookingTime == 0) {
                currentCraftingDuration = recipe.getCraftingDuration() * 20;
                delegate.set(3, currentCraftingDuration);
            }

            cookingTime++;
            delegate.set(0, cookingTime);

            if (cookingTime >= currentCraftingDuration) {
                cookingTime = 0;
                currentCraftingDuration = 0;
                delegate.set(0, cookingTime);
                delegate.set(3, currentCraftingDuration);
                craft(recipe);
            }

            setChanged();
            return;
        }

        if (cookingTime != 0 || currentCraftingDuration != 0) {
            cookingTime = 0;
            currentCraftingDuration = 0;
            delegate.set(0, cookingTime);
            delegate.set(3, currentCraftingDuration);
            setChanged();
        }
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) <= 64.0;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return ObjectRegistry.COOKING_CAULDRON.get().getName();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int syncId, @NotNull Inventory inv, @NotNull Player player) {
        return new CookingCauldronGuiHandler(syncId, inv, this, delegate);
    }

    private void addFluid(int amount) {
        fluidLevel = Math.min(fluidLevel + amount, 100);
        delegate.set(2, fluidLevel);
    }

    private void consumeFluid(int amount) {
        fluidLevel = Math.max(fluidLevel - amount, 0);
        delegate.set(2, fluidLevel);
    }
}