
package net.satisfy.meadow.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.client.gui.handler.CheeseFormGuiHandler;
import org.joml.Vector2i;

public class CheeseFormGui extends AbstractContainerScreen<CheeseFormGuiHandler> {
    public static final ResourceLocation BACKGROUND;

    public static final int ARROW_X = 78;
    public static final int ARROW_Y = 36;
    public static final int TIME_X = 81;
    public static final int TIME_Y = 8;
    private final Vector2i screenPos = new Vector2i();

    public CheeseFormGui(CheeseFormGuiHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
        font = Minecraft.getInstance().font;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float f, int i, int j) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, CheeseFormGui.BACKGROUND);
        int posX = this.leftPos;
        int posY = this.topPos;
        guiGraphics.blit(CheeseFormGui.BACKGROUND, posX, posY, 0, 0, this.imageWidth - 1, this.imageHeight);
        this.renderProgressArrow(guiGraphics);
    }

    protected void renderProgressArrow(GuiGraphics guiGraphics) {
        final int progressX = this.menu.getScaledXProgress();
        guiGraphics.blit(BACKGROUND, leftPos + ARROW_X, topPos + ARROW_Y, 176, 4, progressX, 10);
        final int progressY = this.menu.getScaledYProgress();
        guiGraphics.blit(BACKGROUND, leftPos + TIME_X, topPos + TIME_Y, 180, 22, 16, progressY);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);

        screenPos.set(leftPos, topPos);

        if (isMouseOverProgressArrow(mouseX, mouseY)) {
            int remainingTicks = Math.max(this.menu.getRequiredDuration() - this.menu.getCookingTime(), 0);
            String formattedTime = formatTicks(remainingTicks);
            Component tooltip = Component.translatable("tooltip.meadow.cooking_cauldron.remaining_time", formattedTime);
            guiGraphics.renderTooltip(this.font, tooltip, mouseX, mouseY);
        } else {
            super.renderTooltip(guiGraphics, mouseX, mouseY);
        }
    }

    private boolean isMouseOverProgressArrow(int mouseX, int mouseY) {
        int left = screenPos.x() + ARROW_X;
        int top = screenPos.y() + ARROW_Y;
        return mouseX >= left && mouseX < left + 24 && mouseY >= top && mouseY < top + 17;
    }

    private String formatTicks(int ticks) {
        int seconds = Math.max(ticks / 20, 0);
        int minutes = seconds / 60;
        seconds %= 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    static {
        BACKGROUND = Meadow.identifier("textures/gui/cheese_form_gui.png");
    }
}
