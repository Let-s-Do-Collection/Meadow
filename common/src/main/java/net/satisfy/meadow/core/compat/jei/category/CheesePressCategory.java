package net.satisfy.meadow.core.compat.jei.category;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.client.gui.CheeseFormGui;
import net.satisfy.meadow.core.block.entity.CheeseFormBlockEntity;
import net.satisfy.meadow.core.compat.jei.MeadowJEIPlugin;
import net.satisfy.meadow.core.recipes.CheeseFormRecipe;
import net.satisfy.meadow.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

public class CheesePressCategory implements IRecipeCategory<CheeseFormRecipe> {
    public static final RecipeType<CheeseFormRecipe> CHEESE_PRESS = RecipeType.create(Meadow.MOD_ID, "cheese", CheeseFormRecipe.class);
    public static final int WIDTH = 124;
    public static final int HEIGHT = 55;
    public static final int WIDTH_OF = 26;
    public static final int HEIGHT_OF = 7;
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableAnimated arrow;
    private final IDrawableAnimated time;
    private final Component localizedName;

    public CheesePressCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(CheeseFormGui.BACKGROUND, WIDTH_OF, HEIGHT_OF, WIDTH, HEIGHT);
        this.arrow = helper.drawableBuilder(CheeseFormGui.BACKGROUND, 176, 4, 24, 10)
                .buildAnimated(CheeseFormBlockEntity.COOKING_TIME_IN_TICKS, IDrawableAnimated.StartDirection.LEFT, false);
        this.time = helper.drawableBuilder(CheeseFormGui.BACKGROUND, 180, 22, 16, 25)
                .buildAnimated(CheeseFormBlockEntity.COOKING_TIME_IN_TICKS, IDrawableAnimated.StartDirection.TOP, false);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, ObjectRegistry.CHEESE_FORM.get().asItem().getDefaultInstance());
        this.localizedName = ObjectRegistry.CHEESE_FORM.get().getName();
    }


    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CheeseFormRecipe recipe, IFocusGroup focuses) {
        NonNullList<Ingredient> ingredients = recipe.getIngredients();

        MeadowJEIPlugin.addSlot(builder, 33 - WIDTH_OF, 33 - HEIGHT_OF, ingredients.get(0));
        MeadowJEIPlugin.addSlot(builder, 51 - WIDTH_OF, 33 - HEIGHT_OF, ingredients.get(1));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 123 - WIDTH_OF, 34 - HEIGHT_OF).addItemStack(recipe.getResultItem());
    }

    @Override
    public void draw(CheeseFormRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        arrow.draw(guiGraphics, CheeseFormGui.ARROW_X - WIDTH_OF, CheeseFormGui.ARROW_Y - HEIGHT_OF);
        time.draw(guiGraphics, CheeseFormGui.TIME_X - WIDTH_OF, CheeseFormGui.TIME_Y - HEIGHT_OF);
    }

    @Override
    public @NotNull RecipeType<CheeseFormRecipe> getRecipeType() {
        return CHEESE_PRESS;
    }

    @Override
    public @NotNull Component getTitle() {
        return this.localizedName;
    }

    @Override
    @SuppressWarnings("removal")
    public @NotNull IDrawable getBackground() {
        return this.background;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return this.icon;
    }
}
