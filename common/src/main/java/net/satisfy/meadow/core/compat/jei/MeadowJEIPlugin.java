package net.satisfy.meadow.core.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.client.gui.handler.CheeseFormGuiHandler;
import net.satisfy.meadow.client.gui.handler.CookingCauldronGuiHandler;
import net.satisfy.meadow.core.compat.jei.category.CheesePressCategory;
import net.satisfy.meadow.core.compat.jei.category.CookingCauldronCategory;
import net.satisfy.meadow.core.compat.jei.category.WoodCutterCategory;
import net.satisfy.meadow.core.recipes.CheeseFormRecipe;
import net.satisfy.meadow.core.recipes.CookingCauldronRecipe;
import net.satisfy.meadow.core.recipes.WoodcuttingRecipe;
import net.satisfy.meadow.core.registry.ObjectRegistry;
import net.satisfy.meadow.core.registry.RecipeRegistry;
import net.satisfy.meadow.core.registry.ScreenHandlerRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@JeiPlugin
public class MeadowJEIPlugin implements IModPlugin {

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new CookingCauldronCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new CheesePressCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new WoodCutterCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager rm = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();

        List<RecipeHolder<CookingCauldronRecipe>> cookingHolders = rm.getAllRecipesFor(RecipeRegistry.COOKING.get());
        List<CookingCauldronRecipe> cookingRecipes = new ArrayList<>();
        cookingHolders.forEach(h -> cookingRecipes.add(h.value()));
        registration.addRecipes(CookingCauldronCategory.COOKING_CAULDRON, cookingRecipes);

        List<RecipeHolder<CheeseFormRecipe>> cheeseHolders = rm.getAllRecipesFor(RecipeRegistry.CHEESE.get());
        List<CheeseFormRecipe> cheeseRecipes = new ArrayList<>();
        cheeseHolders.forEach(h -> cheeseRecipes.add(h.value()));
        registration.addRecipes(CheesePressCategory.CHEESE_PRESS, cheeseRecipes);

        List<RecipeHolder<WoodcuttingRecipe>> woodHolders = rm.getAllRecipesFor(RecipeRegistry.WOODCUTTING.get());
        List<WoodcuttingRecipe> woodRecipes = new ArrayList<>();
        woodHolders.forEach(h -> woodRecipes.add(h.value()));
        registration.addRecipes(WoodCutterCategory.WOODCUTTER, woodRecipes);
    }

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return Meadow.identifier("jei_plugin");
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(CheeseFormGuiHandler.class, ScreenHandlerRegistry.CHEESE_FORM_SCREEN_HANDLER.get(), CheesePressCategory.CHEESE_PRESS,
                1, 2, 3, 36);

        registration.addRecipeTransferHandler(CookingCauldronGuiHandler.class, ScreenHandlerRegistry.COOKING_CAULDRON_SCREEN_HANDLER.get(), CookingCauldronCategory.COOKING_CAULDRON,
                1, 6, 7, 36);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(ObjectRegistry.WOODCUTTER.get().asItem().getDefaultInstance(), WoodCutterCategory.WOODCUTTER);
        registration.addRecipeCatalyst(ObjectRegistry.COOKING_CAULDRON.get().asItem().getDefaultInstance(), CookingCauldronCategory.COOKING_CAULDRON);
        registration.addRecipeCatalyst(ObjectRegistry.CHEESE_FORM.get().asItem().getDefaultInstance(), CheesePressCategory.CHEESE_PRESS);
    }

    public static void addSlot(IRecipeLayoutBuilder builder, int x, int y, Ingredient ingredient) {
        builder.addSlot(RecipeIngredientRole.INPUT, x, y).addIngredients(ingredient);
    }
}
