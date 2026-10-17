package net.satisfy.meadow.core.compat.rei.display;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.satisfy.meadow.core.compat.rei.category.CookingCauldronCategory;
import net.satisfy.meadow.core.recipes.CookingCauldronRecipe;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.satisfy.meadow.core.cooking.CookingLiquids;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

public class CookingCauldronDisplay extends BasicDisplay {
    private final int duration;
    @Nullable
    private final Component liquid;
    private final ItemStack container;

    public CookingCauldronDisplay(CookingCauldronRecipe recipe) {
        super(EntryIngredients.ofIngredients(new ArrayList<>(recipe.getIngredients())), Collections.singletonList(EntryIngredients.of(recipe.getResultItem(BasicDisplay.registryAccess()))), Optional.empty());
        this.duration = Math.max(1, recipe.getCraftingDuration() * 20);
        this.liquid = recipe.getLiquid().map(requirement -> CookingLiquids.describe(requirement.id(), requirement.amount())).orElse(null);
        this.container = recipe.getContainer();
    }

    public int getDuration() {
        return duration;
    }

    @Nullable
    public Component getLiquid() {
        return liquid;
    }

    public ItemStack getContainer() {
        return container;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return CookingCauldronCategory.COOKING_CAULDRON_DISPLAY;
    }
}
