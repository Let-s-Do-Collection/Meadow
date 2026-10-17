package net.satisfy.meadow.core.compat.emi;

import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.BiConsumer;

public class MeadowEmiRecipe extends BasicEmiRecipe {
    private final BiConsumer<MeadowEmiRecipe, WidgetHolder> widgets;

    public MeadowEmiRecipe(EmiRecipeCategory category, ResourceLocation id, int width, int height, List<EmiIngredient> inputs, List<EmiStack> outputs, BiConsumer<MeadowEmiRecipe, WidgetHolder> widgets) {
        super(category, id, width, height);
        this.inputs.addAll(inputs);
        this.outputs.addAll(outputs);
        this.widgets = widgets;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        this.widgets.accept(this, widgets);
    }
}
