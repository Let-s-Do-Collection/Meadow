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
import net.satisfy.meadow.core.compat.rei.display.CheeseFormDisplay;
import net.satisfy.meadow.core.registry.ObjectRegistry;

import java.util.List;
import net.satisfy.meadow.platform.PlatformHelper;

public class CheeseFormCategory implements DisplayCategory<CheeseFormDisplay> {


    public static final CategoryIdentifier<CheeseFormDisplay> CHEESE_FORM_DISPLAY = CategoryIdentifier.of(Meadow.MOD_ID, "cheese_form_display");

    @Override
    public CategoryIdentifier<CheeseFormDisplay> getCategoryIdentifier() {
        return CHEESE_FORM_DISPLAY;
    }

    @Override
    public Component getTitle() {
        return ObjectRegistry.CHEESE_FORM.get().getName();
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ObjectRegistry.CHEESE_FORM.get());
    }

    @Override
    public int getDisplayWidth(CheeseFormDisplay display) {
        return 124;
    }

    @Override
    public int getDisplayHeight() {
        return 64;
    }

    @Override
    public List<Widget> setupDisplay(CheeseFormDisplay display, Rectangle bounds) {
        Point startPoint = new Point(bounds.getX() + 16, bounds.getCenterY() - 8);
        List<Widget> widgets = Lists.newArrayList();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createArrow(new Point(startPoint.x + 41, startPoint.y))
                .animationDurationTicks(PlatformHelper.getCheesePressDuration()));
        widgets.add(Widgets.createResultSlotBackground(new Point(startPoint.x + 74, startPoint.y)));
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 74, startPoint.y)).entries(display.getOutputEntries().get(0)).disableBackground().markOutput());

        if (display.getInputEntries().size() < 1)
            widgets.add(Widgets.createSlotBackground(new Point(startPoint.x, startPoint.y)));
        else
            widgets.add(Widgets.createSlot(new Point(startPoint.x, startPoint.y)).entries(display.getInputEntries().get(0)).markInput());

        if (display.getInputEntries().size() < 2)
            widgets.add(Widgets.createSlotBackground(new Point(startPoint.x + 20, startPoint.y)));
        else
            widgets.add(Widgets.createSlot(new Point(startPoint.x + 20, startPoint.y)).entries(display.getInputEntries().get(1)).markInput());

        return widgets;
    }
}
