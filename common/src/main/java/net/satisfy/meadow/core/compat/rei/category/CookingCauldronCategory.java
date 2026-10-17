package net.satisfy.meadow.core.compat.rei.category;

import com.google.common.collect.Lists;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;
import net.satisfy.meadow.Meadow;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.satisfy.meadow.core.compat.rei.display.CookingCauldronDisplay;
import net.satisfy.meadow.core.registry.ObjectRegistry;

import java.util.List;

public class CookingCauldronCategory implements DisplayCategory<CookingCauldronDisplay> {


    public static final CategoryIdentifier<CookingCauldronDisplay> COOKING_CAULDRON_DISPLAY = CategoryIdentifier.of(Meadow.MOD_ID, "cooking_cauldron_display");

    @Override
    public CategoryIdentifier<CookingCauldronDisplay> getCategoryIdentifier() {
        return COOKING_CAULDRON_DISPLAY;
    }

    @Override
    public Component getTitle() {
        return ObjectRegistry.COOKING_CAULDRON.get().getName();
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ObjectRegistry.COOKING_CAULDRON.get());
    }

    @Override
    public int getDisplayHeight() {
        return 64;
    }

    @Override
    public List<Widget> setupDisplay(CookingCauldronDisplay display, Rectangle bounds) {
        Point startPoint = new Point(bounds.getX() + 16, bounds.getCenterY() - 18);
        List<Widget> widgets = Lists.newArrayList();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createArrow(new Point(startPoint.x + 60, startPoint.y + 9)).animationDurationTicks(display.getDuration()));
        if (display.getLiquid() != null) {
            widgets.add(Widgets.createLabel(new Point(startPoint.x, startPoint.y + 40), display.getLiquid()).leftAligned().noShadow().color(0xFF404040, 0xFFBBBBBB));
        }
        if (!display.getContainer().isEmpty()) {
            widgets.add(Widgets.createSlot(new Point(startPoint.x + 96, startPoint.y + 31)).entries(EntryIngredients.of(display.getContainer())).markInput());
        }

        widgets.add(Widgets.createResultSlotBackground(new Point(startPoint.x + 96, startPoint.y + 9)));
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 96, startPoint.y + 9)).entries(display.getOutputEntries().get(0)).disableBackground().markOutput());

        for (int slot = 0; slot < 6; slot++) {
            if (display.getInputEntries().size() < slot + 1)
                widgets.add(Widgets.createSlotBackground(new Point(startPoint.x + 18 * (slot % 3), startPoint.y + 18 * (slot / 3))));
            else
                widgets.add(Widgets.createSlot(new Point(startPoint.x + 18 * (slot % 3), startPoint.y + 18 * (slot / 3))).entries(display.getInputEntries().get(slot)).markInput());
        }

        return widgets;
    }
}
