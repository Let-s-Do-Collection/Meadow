package net.satisfy.meadow.client.gui;

import de.cristelknight.doapi.client.recipebook.screen.AbstractRecipeBookGUIScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.client.gui.handler.CheeseFormGuiHandler;
import net.satisfy.meadow.client.recipebook.CheeseFormRecipeBook;

public class CheeseFormGui extends AbstractRecipeBookGUIScreen<CheeseFormGuiHandler> {
    public static final ResourceLocation BACKGROUND;

    public static final int ARROW_X = 78;
    public static final int ARROW_Y = 36;

    public static final int TIME_X = 81;
    public static final int TIME_Y = 8;
    public CheeseFormGui(CheeseFormGuiHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title, new CheeseFormRecipeBook(), BACKGROUND);
        font = Minecraft.getInstance().font;
        titleLabelX = (imageWidth - font.width(title)) / 2;
    }

    @Override
    protected void renderProgressArrow(GuiGraphics guiGraphics) {
        final int progressX = this.menu.getScaledXProgress();
        guiGraphics.blit(BACKGROUND, leftPos + ARROW_X, topPos + ARROW_Y, 176, 4, progressX, 10);
        final int progressY = this.menu.getScaledYProgress();
        guiGraphics.blit(BACKGROUND, leftPos + TIME_X, topPos + TIME_Y, 180, 22, 16, progressY);
    }

    static {
        BACKGROUND = Meadow.identifier("textures/gui/cheese_form_gui.png");
    }
}
