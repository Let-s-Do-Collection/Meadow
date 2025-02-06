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
import net.satisfy.meadow.core.compat.rei.display.WoodCutterDisplay;
import net.satisfy.meadow.core.registry.ObjectRegistry;

import java.util.List;

public class WoodCutterCategory implements DisplayCategory<WoodCutterDisplay> {


    public static final CategoryIdentifier<WoodCutterDisplay> WOOD_CUTTER_DISPLAY = CategoryIdentifier.of(Meadow.MOD_ID, "wood_cutter_display");

    @Override
    public CategoryIdentifier<WoodCutterDisplay> getCategoryIdentifier() {
        return WOOD_CUTTER_DISPLAY;
    }

    @Override
    public Component getTitle() {
        return ObjectRegistry.WOODCUTTER.get().getName();
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ObjectRegistry.WOODCUTTER.get());
    }

    @Override
    public List<Widget> setupDisplay(WoodCutterDisplay display, Rectangle bounds) {
        Point startPoint = new Point(bounds.getCenterX() - 41, bounds.getCenterY() - 13);
        List<Widget> widgets = Lists.newArrayList();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createArrow(new Point(startPoint.x + 27, startPoint.y + 4)));
        widgets.add(Widgets.createResultSlotBackground(new Point(startPoint.x + 61, startPoint.y + 5)));
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 61, startPoint.y + 5))
                .entries(display.getOutputEntries().get(0))
                .disableBackground()
                .markOutput());
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 4, startPoint.y + 5))
                .entries(display.getInputEntries().get(0)).markInput());
        return widgets;
    }
}