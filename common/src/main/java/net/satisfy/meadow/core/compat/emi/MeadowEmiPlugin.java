package net.satisfy.meadow.core.compat.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.satisfy.meadow.core.cooking.CookingLiquids;
import net.satisfy.meadow.core.recipes.CheeseFormRecipe;
import net.satisfy.meadow.core.recipes.CookingCauldronRecipe;
import net.satisfy.meadow.core.recipes.WoodcuttingRecipe;
import net.satisfy.meadow.core.registry.ObjectRegistry;
import net.satisfy.meadow.core.registry.RecipeRegistry;

import java.util.ArrayList;
import java.util.List;
import net.satisfy.meadow.platform.PlatformHelper;

@EmiEntrypoint
public class MeadowEmiPlugin implements EmiPlugin {
    public static final MeadowEmiCategory COOKING_CAULDRON = new MeadowEmiCategory("cooking_cauldron", ObjectRegistry.COOKING_CAULDRON.get());
    public static final MeadowEmiCategory CHEESE_FORM = new MeadowEmiCategory("cheese_form", ObjectRegistry.CHEESE_FORM.get());
    public static final MeadowEmiCategory WOODCUTTING = new MeadowEmiCategory("woodcutting", ObjectRegistry.WOODCUTTER.get());

    private static final int SLOT = 18;

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(COOKING_CAULDRON);
        registry.addCategory(CHEESE_FORM);
        registry.addCategory(WOODCUTTING);

        registry.addWorkstation(COOKING_CAULDRON, EmiStack.of(ObjectRegistry.COOKING_CAULDRON.get()));
        registry.addWorkstation(CHEESE_FORM, EmiStack.of(ObjectRegistry.CHEESE_FORM.get()));
        registry.addWorkstation(WOODCUTTING, EmiStack.of(ObjectRegistry.WOODCUTTER.get()));

        RecipeManager manager = registry.getRecipeManager();
        for (RecipeHolder<CookingCauldronRecipe> holder : manager.getAllRecipesFor(RecipeRegistry.COOKING.get())) {
            registry.addRecipe(cookingCauldron(holder));
        }
        for (RecipeHolder<CheeseFormRecipe> holder : manager.getAllRecipesFor(RecipeRegistry.CHEESE.get())) {
            registry.addRecipe(cheeseForm(holder));
        }
        for (RecipeHolder<WoodcuttingRecipe> holder : manager.getAllRecipesFor(RecipeRegistry.WOODCUTTING.get())) {
            registry.addRecipe(woodcutting(holder));
        }
    }

    private static MeadowEmiRecipe cookingCauldron(RecipeHolder<CookingCauldronRecipe> holder) {
        CookingCauldronRecipe recipe = holder.value();
        List<EmiIngredient> ingredients = ingredients(recipe.getIngredients());
        EmiStack container = recipe.getContainer().isEmpty() ? EmiStack.EMPTY : EmiStack.of(recipe.getContainer());
        List<EmiIngredient> inputs = new ArrayList<>(ingredients);
        if (!container.isEmpty()) {
            inputs.add(container);
        }
        EmiStack output = EmiStack.of(recipe.getResultItem());
        int time = Math.max(1, recipe.getCraftingDuration()) * 1000;
        return new MeadowEmiRecipe(COOKING_CAULDRON, holder.id(), 118, 50, inputs, List.of(output), (self, widgets) -> {
            for (int i = 0; i < 6; i++) {
                widgets.addSlot(i < ingredients.size() ? ingredients.get(i) : EmiStack.EMPTY, (i % 3) * SLOT, (i / 3) * SLOT);
            }
            widgets.addFillingArrow(60, 9, time);
            widgets.addSlot(output, 96, 9).recipeContext(self);
            if (!container.isEmpty()) {
                widgets.addSlot(container, 96, 31);
            }
            recipe.getLiquid().ifPresent(liquid -> widgets.addText(CookingLiquids.describe(liquid.id(), liquid.amount()), 0, 40, 0xFF404040, false));
        });
    }

    private static MeadowEmiRecipe cheeseForm(RecipeHolder<CheeseFormRecipe> holder) {
        CheeseFormRecipe recipe = holder.value();
        List<EmiIngredient> inputs = ingredients(recipe.getIngredients());
        EmiStack output = EmiStack.of(recipe.getResultItem());
        return new MeadowEmiRecipe(CHEESE_FORM, holder.id(), 92, SLOT, inputs, List.of(output), (self, widgets) -> {
            for (int i = 0; i < 2; i++) {
                widgets.addSlot(i < inputs.size() ? inputs.get(i) : EmiStack.EMPTY, i * 20, 0);
            }
            widgets.addFillingArrow(41, 0, PlatformHelper.getCheesePressDuration() * 50);
            widgets.addSlot(output, 74, 0).recipeContext(self);
        });
    }

    private static MeadowEmiRecipe woodcutting(RecipeHolder<WoodcuttingRecipe> holder) {
        WoodcuttingRecipe recipe = holder.value();
        EmiIngredient input = EmiIngredient.of(recipe.getInput());
        EmiStack output = EmiStack.of(recipe.getResultItem());
        return new MeadowEmiRecipe(WOODCUTTING, holder.id(), 82, SLOT, List.of(input), List.of(output), (self, widgets) -> {
            widgets.addSlot(input, 4, 0);
            widgets.addTexture(EmiTexture.EMPTY_ARROW, 28, 0);
            widgets.addSlot(output, 60, 0).recipeContext(self);
        });
    }

    private static List<EmiIngredient> ingredients(List<Ingredient> ingredients) {
        return ingredients.stream().map(EmiIngredient::of).toList();
    }
}
