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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.meadow.client.gui.handler.CheeseFormGuiHandler;
import net.satisfy.meadow.core.block.CheeseFormBlock;
import net.satisfy.meadow.core.recipes.CheeseFormRecipe;
import net.satisfy.meadow.core.registry.EntityTypeRegistry;
import net.satisfy.meadow.core.registry.ObjectRegistry;
import net.satisfy.meadow.core.registry.RecipeRegistry;
import net.satisfy.meadow.core.world.ImplementedInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class CheeseFormBlockEntity extends BlockEntity implements MenuProvider, ImplementedInventory {

    private NonNullList<ItemStack> inventory;
    public static final int CAPACITY = 3;
    public static final int COOKING_TIME_IN_TICKS = 1800;
    private static final int OUTPUT_SLOT = 0;
    private static final int FIRST_INPUT_SLOT = 1;
    private static final int SECOND_INPUT_SLOT = 2;
    private int fermentationTime = 0;
    protected float experience;

    private static final int[] SLOTS_FOR_SIDE = new int[]{SECOND_INPUT_SLOT};
    private static final int[] SLOTS_FOR_UP = new int[]{FIRST_INPUT_SLOT};
    private static final int[] SLOTS_FOR_DOWN = new int[]{OUTPUT_SLOT};

    private final ContainerData propertyDelegate = new ContainerData() {

        @Override
        public int get(int index) {
            if (index == 0) {
                return fermentationTime;
            }
            if (index == 1) {
                return COOKING_TIME_IN_TICKS;
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) {
                fermentationTime = value;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public CheeseFormBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.CHEESE_FORM_BLOCK_ENTITY.get(), pos, state);
        this.inventory = NonNullList.withSize(CAPACITY, ItemStack.EMPTY);
    }

    public void tick(Level world, BlockPos pos, BlockState state) {
        if (world.isClientSide()) {
            return;
        }

        RecipeManager recipeManager = world.getRecipeManager();
        List<RecipeHolder<CheeseFormRecipe>> recipes = recipeManager.getAllRecipesFor(RecipeRegistry.CHEESE.get());
        CheeseFormRecipe matchedRecipe = getRecipe(recipes, inventory);

        RegistryAccess access = world.registryAccess();
        boolean working = canCraft(matchedRecipe, access);

        if (working) {
            fermentationTime++;

            if (fermentationTime >= COOKING_TIME_IN_TICKS) {
                fermentationTime = 0;
                craft(matchedRecipe, access);
                setChanged();
            } else {
                setChanged();
            }
        } else if (fermentationTime != 0) {
            fermentationTime = 0;
            setChanged();
        }

        boolean done = !inventory.getFirst().isEmpty();

        if (state.getValue(CheeseFormBlock.WORKING) != working || state.getValue(CheeseFormBlock.DONE) != done) {
            world.setBlockAndUpdate(pos, state.setValue(CheeseFormBlock.WORKING, working).setValue(CheeseFormBlock.DONE, done));
        }
    }

    @Override
    public int @NotNull [] getSlotsForFace(Direction side) {
        if (side.equals(Direction.UP)) {
            return SLOTS_FOR_UP;
        } else if (side.equals(Direction.DOWN)) {
            return SLOTS_FOR_DOWN;
        } else {
            return SLOTS_FOR_SIDE;
        }
    }

    @Override
    protected void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.loadAdditional(compoundTag, provider);
        this.inventory = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(compoundTag, this.inventory, provider);
        this.fermentationTime = compoundTag.getShort("fermentationTime");
        this.experience = compoundTag.getFloat("experience");
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.saveAdditional(compoundTag, provider);
        ContainerHelper.saveAllItems(compoundTag, this.inventory, provider);
        compoundTag.putShort("fermentationTime", (short) this.fermentationTime);
        compoundTag.putFloat("experience", this.experience);
    }

    private CheeseFormRecipe getRecipe(List<RecipeHolder<CheeseFormRecipe>> recipes, NonNullList<ItemStack> inventory) {
        recipeLoop:
        for (RecipeHolder<CheeseFormRecipe> recipeHolder : recipes) {
            CheeseFormRecipe recipe = recipeHolder.value();
            for (Ingredient ingredient : recipe.getIngredients()) {
                boolean ingredientFound = false;
                for (int slotIndex = FIRST_INPUT_SLOT; slotIndex < inventory.size(); slotIndex++) {
                    ItemStack slotItem = inventory.get(slotIndex);
                    if (ingredient.test(slotItem)) {
                        ingredientFound = true;
                        break;
                    }
                }
                if (!ingredientFound) {
                    continue recipeLoop;
                }
            }
            return recipe;
        }
        return null;
    }

    private boolean canCraft(CheeseFormRecipe recipe, RegistryAccess access) {
        if (recipe == null || recipe.getResultItem(access).isEmpty()) {
            return false;
        }
        if (areInputsEmpty()) {
            return false;
        }
        ItemStack outputStack = this.getItem(OUTPUT_SLOT);
        return outputStack.isEmpty() || outputStack.is(recipe.getResultItem(access).getItem());
    }

    private boolean areInputsEmpty() {
        return this.getItem(FIRST_INPUT_SLOT).isEmpty() && this.getItem(SECOND_INPUT_SLOT).isEmpty();
    }

    private void craft(CheeseFormRecipe recipe, RegistryAccess access) {
        if (!canCraft(recipe, access)) {
            return;
        }

        ItemStack recipeOutput = recipe.getResultItem(access);
        ItemStack outputSlotStack = this.getItem(OUTPUT_SLOT);

        if (outputSlotStack.isEmpty()) {
            setItem(OUTPUT_SLOT, recipeOutput.copy());
        } else if (outputSlotStack.is(recipeOutput.getItem())) {
            outputSlotStack.grow(recipeOutput.getCount());
        }

        consumeOneAndReturnRemainderIfAny(recipe, FIRST_INPUT_SLOT);
        consumeOneAndReturnRemainderIfAny(recipe, SECOND_INPUT_SLOT);
    }

    private void consumeOneAndReturnRemainderIfAny(CheeseFormRecipe recipe, int slotIndex) {
        ItemStack slotStack = this.getItem(slotIndex);
        if (slotStack.isEmpty()) {
            return;
        }

        boolean matches = recipe.getIngredients().stream().anyMatch(ingredient -> ingredient.test(slotStack));
        if (!matches) {
            return;
        }

        ItemStack remainderStack = ItemStack.EMPTY;
        if (slotStack.getItem().hasCraftingRemainingItem()) {
            remainderStack = new ItemStack(Objects.requireNonNull(slotStack.getItem().getCraftingRemainingItem()));
        }

        slotStack.shrink(1);

        if (!remainderStack.isEmpty()) {
            if (slotStack.isEmpty()) {
                this.setItem(slotIndex, remainderStack);
            } else {
                tryInsertRemainder(remainderStack);
            }
        } else {
            if (slotStack.isEmpty()) {
                this.setItem(slotIndex, ItemStack.EMPTY);
            }
        }
    }

    private void tryInsertRemainder(ItemStack remainderStack) {
        if (remainderStack.isEmpty()) {
            return;
        }

        if (this.getItem(FIRST_INPUT_SLOT).isEmpty()) {
            this.setItem(FIRST_INPUT_SLOT, remainderStack);
            return;
        }

        if (this.getItem(SECOND_INPUT_SLOT).isEmpty()) {
            this.setItem(SECOND_INPUT_SLOT, remainderStack);
            return;
        }

        if (this.level == null) {
            return;
        }

        ItemEntity itemEntity = new ItemEntity(this.level, this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5, remainderStack);
        itemEntity.setDefaultPickUpDelay();
        this.level.addFreshEntity(itemEntity);
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        ItemStack stackInSlot = this.inventory.get(slot);
        boolean dirty = !stack.isEmpty() && ItemStack.isSameItem(stack, stackInSlot) && ItemStack.matches(stack, stackInSlot);

        this.inventory.set(slot, stack);

        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }

        if (slot == FIRST_INPUT_SLOT || slot == SECOND_INPUT_SLOT) {
            if (!dirty) {
                this.fermentationTime = 0;
                setChanged();
            }
        }
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.level == null) {
            return false;
        }
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        }
        return player.distanceToSqr(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5) <= 64.0;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return ObjectRegistry.CHEESE_FORM.get().getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        return new CheeseFormGuiHandler(syncId, inv, this, this.propertyDelegate);
    }
}