package net.satisfy.meadow.client.gui.handler;

import com.google.common.collect.Lists;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.satisfy.meadow.core.recipes.WoodcuttingRecipe;
import net.satisfy.meadow.core.registry.ObjectRegistry;
import net.satisfy.meadow.core.registry.RecipeRegistry;
import net.satisfy.meadow.core.registry.ScreenHandlerRegistry;
import net.satisfy.meadow.core.registry.SoundEventRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WoodcutterGuiHandler extends AbstractContainerMenu {
    private final ContainerLevelAccess context;
    private final DataSlot selectedRecipe = DataSlot.standalone();
    private final Level world;
    private List<RecipeHolder<WoodcuttingRecipe>> availableRecipes = Lists.newArrayList();
    private ItemStack inputStack = ItemStack.EMPTY;
    private long lastTakeTime;
    private Slot inputSlot;
    private Slot outputSlot;
    Runnable contentsChangedListener = () -> {
    };
    public final Container input = new SimpleContainer(1) {
        @Override
        public void setChanged() {
            super.setChanged();
            WoodcutterGuiHandler.this.slotsChanged(this);
            WoodcutterGuiHandler.this.contentsChangedListener.run();
        }
    };
    final ResultContainer output = new ResultContainer();

    public WoodcutterGuiHandler(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, ContainerLevelAccess.NULL);
    }

    public WoodcutterGuiHandler(int syncId, Inventory playerInventory, final ContainerLevelAccess context) {
        super(ScreenHandlerRegistry.WOODCUTTER_SCREEN_HANDLER.get(), syncId);
        this.context = context;
        this.world = playerInventory.player.level();

        buildBlockEntityContainer(playerInventory);
        buildPlayerContainer(playerInventory);

        this.addDataSlot(this.selectedRecipe);
    }


    private void buildBlockEntityContainer(Inventory playerInventory) {
        this.inputSlot = this.addSlot(new Slot(this.input, 0, 33, 24));
        this.outputSlot = this.addSlot(new FurnaceResultSlot(playerInventory.player, this.output, 1, 144, 34) {
            @Override
            public void onTake(Player player, ItemStack stack) {
                stack.onCraftedBy(player.level(), player, stack.getCount());
                WoodcutterGuiHandler.this.output.awardUsedRecipes(player, this.getInputStacks());
                ItemStack itemStack = WoodcutterGuiHandler.this.inputSlot.remove(1);
                if (!itemStack.isEmpty()) {
                    WoodcutterGuiHandler.this.populateResult();
                }
                context.execute((world, pos) -> {
                    long l = world.getGameTime();
                    if (WoodcutterGuiHandler.this.lastTakeTime != l) {
                        world.playSound(null, pos, SoundEventRegistry.WOODCUTTER.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
                        WoodcutterGuiHandler.this.lastTakeTime = l;
                    }
                });
                super.onTake(player, stack);
            }

            private List<ItemStack> getInputStacks() {
                return List.of(WoodcutterGuiHandler.this.inputSlot.getItem());
            }
        });
    }

    private void buildPlayerContainer(Inventory playerInventory) {
        int i;
        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    public int getSelectedRecipe() {
        return this.selectedRecipe.get();
    }

    public List<RecipeHolder<WoodcuttingRecipe>> getAvailableRecipes() {
        return this.availableRecipes;
    }

    public int getAvailableRecipeCount() {
        return this.availableRecipes.size();
    }

    public boolean canCraft() {
        return this.inputSlot.hasItem() && !this.availableRecipes.isEmpty();
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.context, player, ObjectRegistry.WOODCUTTER.get());
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (this.isInBounds(id)) {
            this.selectedRecipe.set(id);
            this.populateResult();
        }
        return true;
    }



    private boolean isInBounds(int id) {
        return id >= 0 && id < this.availableRecipes.size();
    }

    @Override
    public void slotsChanged(Container inventory) {
        ItemStack itemStack = this.inputSlot.getItem();
        if (!itemStack.is(this.inputStack.getItem())) {
            this.inputStack = itemStack.copy();
            this.updateInput(inventory, itemStack);
        }
    }

    private void updateInput(Container input, ItemStack stack) {
        this.availableRecipes.clear();
        this.selectedRecipe.set(-1);
        this.outputSlot.setByPlayer(ItemStack.EMPTY);
        if (!stack.isEmpty()) {
            this.availableRecipes = this.world.getRecipeManager().getRecipesFor(RecipeRegistry.WOODCUTTING.get(), new SingleRecipeInput(input.getItem(0)), this.world);
        }
    }

    void populateResult() {
        if (!this.availableRecipes.isEmpty() && this.isInBounds(this.selectedRecipe.get())) {
            RecipeHolder<WoodcuttingRecipe> woodcuttingRecipe = this.availableRecipes.get(this.selectedRecipe.get());
            ItemStack itemStack = woodcuttingRecipe.value().assemble(new SingleRecipeInput(input.getItem(0)), this.world.registryAccess());
            if (itemStack.isItemEnabled(this.world.enabledFeatures())) {
                this.output.setRecipeUsed(woodcuttingRecipe);
                this.outputSlot.set(itemStack);
            } else {
                this.outputSlot.set(ItemStack.EMPTY);
            }
        } else {
            this.outputSlot.set(ItemStack.EMPTY);
        }

        this.broadcastChanges();
    }

    @Override
    public @NotNull MenuType<?> getType() {
        return ScreenHandlerRegistry.WOODCUTTER_SCREEN_HANDLER.get();
    }

    public void setContentsChangedListener(Runnable contentsChangedListener) {
        this.contentsChangedListener = contentsChangedListener;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return slot.container != this.output && super.canTakeItemForPickAll(stack, slot);
    }


    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemStack2 = slot.getItem();
            Item item = itemStack2.getItem();
            itemStack = itemStack2.copy();
            if (index == 1) {
                item.onCraftedBy(itemStack2, player.level(), player);
                if (!this.moveItemStackTo(itemStack2, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemStack2, itemStack);
            } else if (index == 0 ? !this.moveItemStackTo(itemStack2, 2, 38, false) :
                    (this.world.getRecipeManager().getRecipeFor(RecipeRegistry.WOODCUTTING.get(), new SingleRecipeInput(itemStack2), this.world).isPresent() ? !this.moveItemStackTo(itemStack2, 0, 1, false) :
                            (index >= 2 && index < 29 ? !this.moveItemStackTo(itemStack2, 29, 38, false) :
                                    index >= 29 && index < 38 && !this.moveItemStackTo(itemStack2, 2, 29, false)))) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            }
            slot.setChanged();
            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, itemStack2);
            this.broadcastChanges();
        }
        return itemStack;
    }


    public void removed(Player player) {
        super.removed(player);
        this.output.removeItemNoUpdate(1);
        this.context.execute((world, pos) -> this.clearContainer(player, this.input));
    }
}