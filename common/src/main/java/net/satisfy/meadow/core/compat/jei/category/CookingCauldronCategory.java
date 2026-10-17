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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.client.Minecraft;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.core.cooking.CookingLiquids;
import net.satisfy.meadow.core.recipes.CookingCauldronRecipe;
import net.satisfy.meadow.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

public class CookingCauldronCategory implements IRecipeCategory<CookingCauldronRecipe> {
    public static final RecipeType<CookingCauldronRecipe> COOKING_CAULDRON = RecipeType.create(Meadow.MOD_ID, "cooking_cauldron", CookingCauldronRecipe.class);
    private static final int BACKGROUND_WIDTH = 160;
    private static final int BACKGROUND_HEIGHT = 70;
    private static final int X_OFFSET = 10;
    private static final int Y_OFFSET = 10;
    private static final int ARROW_U = 176;
    private static final int ARROW_V = 14;
    private static final int ARROW_WIDTH = 24;
    private static final int ARROW_HEIGHT = 17;
    private static final int MAX_TIME = 200;

    private static final int WIDTH_OF = 26;
    private static final int HEIGHT_OF = 10;
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable burnIcon;
    private final IDrawableAnimated arrow;
    private final Component localizedName;


    private final ResourceLocation texture = Meadow.identifier("textures/gui/cooking_cauldron_gui.png");

    public CookingCauldronCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(texture, X_OFFSET, Y_OFFSET, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        this.arrow = helper.drawableBuilder(texture, ARROW_U, ARROW_V, ARROW_WIDTH, ARROW_HEIGHT)
                .buildAnimated(MAX_TIME, IDrawableAnimated.StartDirection.LEFT, false);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, ObjectRegistry.COOKING_CAULDRON.get().asItem().getDefaultInstance());
        this.burnIcon = helper.createDrawable(texture, 176, 0, 14, 14);
        this.localizedName = ObjectRegistry.COOKING_CAULDRON.get().getName();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CookingCauldronRecipe recipe, IFocusGroup focuses) {
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        for (int i = 0; i < ingredients.size(); i++) {
            builder.addSlot(RecipeIngredientRole.INPUT, 27 - WIDTH_OF + (i % 3) * 18, 17 - HEIGHT_OF + (i / 3) * 18)
                    .addIngredients(ingredients.get(i));
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 123 - WIDTH_OF, 27 - HEIGHT_OF)
                .addItemStack(recipe.getResultItem());
        if (!recipe.getContainer().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.CATALYST, 123 - WIDTH_OF, 7 - HEIGHT_OF + 52)
                    .addItemStack(recipe.getContainer());
        }
    }

    @Override
    public void draw(CookingCauldronRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        arrow.draw(guiGraphics, 86 - WIDTH_OF, 27 - HEIGHT_OF);
        burnIcon.draw(guiGraphics, 60, 52 - HEIGHT_OF);
        recipe.getLiquid().ifPresent(liquid -> guiGraphics.drawString(Minecraft.getInstance().font, CookingLiquids.describe(liquid.id(), liquid.amount()), 1, 52, 0xFF404040, false));
    }

    @Override
    public @NotNull RecipeType<CookingCauldronRecipe> getRecipeType() {
        return COOKING_CAULDRON;
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
