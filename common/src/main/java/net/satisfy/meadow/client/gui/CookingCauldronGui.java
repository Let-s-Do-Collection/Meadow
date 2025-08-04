package net.satisfy.meadow.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.client.gui.handler.CookingCauldronGuiHandler;
import org.joml.Vector2i;

public class CookingCauldronGui extends AbstractContainerScreen<CookingCauldronGuiHandler> {
    private static final ResourceLocation BACKGROUND = Meadow.identifier("textures/gui/cooking_cauldron_gui.png");
    private static final int ARROW_X = 70;
    private static final int ARROW_Y = 27;
    private static final int FLUID_X = 157;
    private static final int FLUID_Y = 23;
    private static final int FLUID_WIDTH = 8;
    private static final int FLUID_HEIGHT = 43;
    private final Vector2i screenPos = new Vector2i();

    public CookingCauldronGui(CookingCauldronGuiHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        screenPos.set(leftPos, topPos);
        titleLabelX = (imageWidth - font.width(title)) / 2;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, screenPos.x(), screenPos.y(), 0, 0, imageWidth, imageHeight);
        renderProgressArrow(guiGraphics);
        renderBurnIcon(guiGraphics);
        renderFluidBar(guiGraphics);
    }

    private void renderProgressArrow(GuiGraphics guiGraphics) {
        int progress = this.menu.getScaledProgress(24);
        guiGraphics.blit(BACKGROUND, screenPos.x() + ARROW_X, screenPos.y() + ARROW_Y, 176, 14, progress, 17);
    }

    private void renderBurnIcon(GuiGraphics guiGraphics) {
        if (menu.isBeingBurned()) {
            guiGraphics.blit(BACKGROUND, screenPos.x() + 108, screenPos.y() + 52, 176, 0, 14, 14);
        }
    }

    private void renderFluidBar(GuiGraphics guiGraphics) {
        int fluidLevel = menu.getFluidLevel();
        int filledHeight = (fluidLevel * FLUID_HEIGHT) / 100;
        guiGraphics.blit(BACKGROUND, screenPos.x() + FLUID_X, screenPos.y() + FLUID_Y + FLUID_HEIGHT - filledHeight, 176, 31 + FLUID_HEIGHT - filledHeight, FLUID_WIDTH, filledHeight);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        if (isMouseOverFluidArea(mouseX, mouseY)) {
            int fluidLevel = this.menu.getFluidLevel();
            Component tooltip = Component.translatable("tooltip.meadow.cooking_cauldron.water_level", fluidLevel);
            guiGraphics.renderTooltip(this.font, tooltip, mouseX, mouseY);
        }
        if (isMouseOverProgressArrow(mouseX, mouseY)) {
            int remainingTicks = this.menu.getRequiredDuration() - this.menu.getCookingTime();
            String formattedTime = formatTicks(remainingTicks);
            Component tooltip = Component.translatable("tooltip.meadow.cooking_cauldron.remaining_time", formattedTime);
            guiGraphics.renderTooltip(this.font, tooltip, mouseX, mouseY);
        }
        super.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private boolean isMouseOverFluidArea(int mouseX, int mouseY) {
        int left = screenPos.x() + FLUID_X;
        int top = screenPos.y() + FLUID_Y;
        return mouseX >= left && mouseX < left + FLUID_WIDTH && mouseY >= top && mouseY < top + FLUID_HEIGHT;
    }

    private boolean isMouseOverProgressArrow(int mouseX, int mouseY) {
        int left = screenPos.x() + ARROW_X;
        int top = screenPos.y() + ARROW_Y;
        return mouseX >= left && mouseX < left + 24 && mouseY >= top && mouseY < top + 17;
    }

    private String formatTicks(int ticks) {
        int seconds = ticks / 20;
        int minutes = seconds / 60;
        seconds %= 60;
        return String.format("%d:%02d", minutes, seconds);
    }
}
