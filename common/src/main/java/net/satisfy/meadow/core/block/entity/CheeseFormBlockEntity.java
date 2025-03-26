package net.satisfy.meadow.core.block.entity;

import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.meadow.client.gui.handler.CheeseFormGuiHandler;
import net.satisfy.meadow.core.block.CheeseFormBlock;
import net.satisfy.meadow.core.recipes.CheeseFormRecipe;
import net.satisfy.meadow.core.registry.EntityTypeRegistry;
import net.satisfy.meadow.core.registry.ObjectRegistry;
import net.satisfy.meadow.core.registry.RecipeRegistry;
import net.satisfy.meadow.core.registry.TagRegistry;
import net.satisfy.meadow.core.world.ImplementedInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class CheeseFormBlockEntity extends BlockEntity implements BlockEntityTicker<CheeseFormBlockEntity>, MenuProvider, ImplementedInventory {

    private NonNullList<ItemStack> inventory;
    public static final int CAPACITY = 3;
    public static final int COOKING_TIME_IN_TICKS = 1800;
    private static final int OUTPUT_SLOT = 0;
    private int fermentationTime = 0;
    protected float experience;

    private static final int[] SLOTS_FOR_SIDE = new int[]{2};
    private static final int[] SLOTS_FOR_UP = new int[]{1};
    private static final int[] SLOTS_FOR_DOWN = new int[]{0};

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

    @Override
    public void tick(Level world, BlockPos pos, BlockState state, CheeseFormBlockEntity blockEntity) {
        if (world.isClientSide) return;

        RecipeManager recipeManager = world.getRecipeManager();
        List<RecipeHolder<CheeseFormRecipe>> recipes = recipeManager.getAllRecipesFor(RecipeRegistry.CHEESE.get());
        Optional<CheeseFormRecipe> recipe = Optional.ofNullable(getRecipe(recipes, inventory));

        if (recipe.isPresent()) {
            RegistryAccess access = world.registryAccess();
            boolean working = canCraft(recipe.get(), access);
            if (working) {
                this.fermentationTime++;

                if (this.fermentationTime >= COOKING_TIME_IN_TICKS) {
                    this.fermentationTime = 0;
                    craft(recipe.get(), access);
                    setChanged();
                }
            } else {
                this.fermentationTime = 0;
            }
            boolean done = !inventory.getFirst().isEmpty();
            if (state.getValue(CheeseFormBlock.WORKING) != working || state.getValue(CheeseFormBlock.DONE) != done) {
                world.setBlockAndUpdate(pos, state.setValue(CheeseFormBlock.WORKING, working).setValue(CheeseFormBlock.DONE, done));
            }
        }
    }

    private CheeseFormRecipe getRecipe(List<RecipeHolder<CheeseFormRecipe>> recipes, NonNullList<ItemStack> inventory) {
        recipeLoop:
        for (RecipeHolder<CheeseFormRecipe> recipeHolder : recipes) {
            CheeseFormRecipe recipe = recipeHolder.value();
            for (Ingredient ingredient : recipe.getIngredients()) {
                boolean ingredientFound = false;
                for (int slotIndex = 1; slotIndex < inventory.size(); slotIndex++) {
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

    private boolean canCraft(CheeseFormRecipe recipe, RegistryAccess manager) {
        if (recipe == null || recipe.getResultItem(manager).isEmpty()) {
            return false;
        } else if (areInputsEmpty()) {
            return false;
        }
        ItemStack itemStack = this.getItem(OUTPUT_SLOT);
        return itemStack.isEmpty() || itemStack.is(recipe.getResultItem(manager).getItem());
    }

    private boolean areInputsEmpty() {
        int emptyStacks = 0;
        for (int i = 1; i <= 2; i++) {
            if (this.getItem(i).isEmpty()) emptyStacks++;
        }
        return emptyStacks == 2;
    }

    private void craft(CheeseFormRecipe recipe, RegistryAccess manager) {
        if (!canCraft(recipe, manager)) {
            return;
        }
        final ItemStack recipeOutput = recipe.getResultItem(manager);
        final ItemStack outputSlotStack = this.getItem(OUTPUT_SLOT);

        if (outputSlotStack.isEmpty()) {
            ItemStack output = recipeOutput.copy();
            setItem(OUTPUT_SLOT, output);
        } else if (outputSlotStack.is(recipeOutput.getItem())) {
            outputSlotStack.grow(recipeOutput.getCount());
        }

        ItemStack slot1Stack = this.getItem(1);
        if (recipe.getIngredients().stream().anyMatch(entry -> entry.test(slot1Stack))) {
            if (slot1Stack.is(TagRegistry.MILK_BUCKET)) {
                this.setItem(1, Items.BUCKET.getDefaultInstance());
            } else if (slot1Stack.is(TagRegistry.WOODEN_MILK_BUCKET)) {
                this.setItem(1, ObjectRegistry.WOODEN_BUCKET.get().getDefaultInstance());
            } else {
                removeItem(1, 1);
            }
        }

        ItemStack slot2Stack = this.getItem(2);
        if (recipe.getIngredients().stream().anyMatch(entry -> entry.test(slot2Stack))) {
            if (slot2Stack.is(TagRegistry.MILK_BUCKET)) {
                this.setItem(2, Items.BUCKET.getDefaultInstance());
            } else if (slot2Stack.is(TagRegistry.WOODEN_MILK_BUCKET)) {
                this.setItem(2, ObjectRegistry.WOODEN_BUCKET.get().getDefaultInstance());
            } else {
                if (slot2Stack.is(TagRegistry.MILK)) {
                    ItemStack bucket = slot2Stack.getItem() == ObjectRegistry.WOODEN_MILK_BUCKET.get() ? ObjectRegistry.WOODEN_BUCKET.get().getDefaultInstance() : Items.BUCKET.getDefaultInstance();
                    this.setItem(2, bucket);
                } else {
                    removeItem(2, 1);
                }
            }
        }
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        final ItemStack stackInSlot = this.inventory.get(slot);
        boolean dirty = !stack.isEmpty() && ItemStack.isSameItem(stack, stackInSlot) && ItemStack.matches(stack, stackInSlot);
        this.inventory.set(slot, stack);
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
        if (slot == 1 || slot == 2) {
            if (!dirty) {
                this.fermentationTime = 0;
                setChanged();
            }
        }
    }

    @Override
    public boolean stillValid(Player player) {
        assert this.level != null;
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return player.distanceToSqr((double) this.worldPosition.getX() + 0.5, (double) this.worldPosition.getY() + 0.5, (double) this.worldPosition.getZ() + 0.5) <= 64.0;
        }
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
