package net.satisfy.meadow.core.compat.emi;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ItemLike;
import net.satisfy.meadow.Meadow;

public class MeadowEmiCategory extends EmiRecipeCategory {
    private final ItemLike icon;

    public MeadowEmiCategory(String id, ItemLike icon) {
        super(Meadow.identifier(id), EmiStack.of(icon));
        this.icon = icon;
    }

    @Override
    public Component getName() {
        return this.icon.asItem().getDescription();
    }
}
