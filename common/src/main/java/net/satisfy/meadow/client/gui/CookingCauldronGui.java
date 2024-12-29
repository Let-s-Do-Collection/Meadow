package net.satisfy.meadow.client.gui;


import de.cristelknight.doapi.client.recipebook.screen.AbstractRecipeBookGUIScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.client.gui.handler.CookingCauldronGuiHandler;
import net.satisfy.meadow.client.recipebook.CookingCauldronRecipeBook;


public class CookingCauldronGui extends AbstractRecipeBookGUIScreen<CookingCauldronGuiHandler> {
    public static final ResourceLocation BACKGROUND;

    public static final int ARROW_X = 92;
    public static final int ARROW_Y = 10;

    public CookingCauldronGui(CookingCauldronGuiHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title, new CookingCauldronRecipeBook(), BACKGROUND);
        font = Minecraft.getInstance().font;
        titleLabelX = (imageWidth - font.width(title)) / 2;
    }

    @Override
    protected void renderProgressArrow(GuiGraphics guiGraphics) {
        int progress = this.menu.getScaledProgress(17);
        guiGraphics.blit(BACKGROUND, leftPos + ARROW_X, topPos + ARROW_Y, 178, 16, progress, 29);
    }

    @Override
    protected void renderBurnIcon(GuiGraphics guiGraphics, int posX, int posY) {
        if (menu.isBeingBurned()) {
            guiGraphics.blit(BACKGROUND, posX + 124, posY + 51, 176, 0, 17, 15);
        }
    }


    static {
        BACKGROUND = Meadow.identifier("textures/gui/cooking_cauldron_gui.png");
    }
}