package net.satisfy.meadow.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.client.gui.handler.FondueGuiHandler;

public class FondueGui extends AbstractContainerScreen<FondueGuiHandler> {

    public static final ResourceLocation BACKGROUND = Meadow.identifier("textures/gui/fondue_gui.png");

    public static final int ARROW_X = 87;
    public static final int ARROW_Y = 25;
    public FondueGui(FondueGuiHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }


    private void renderProgressArrow(GuiGraphics context, int x, int y) {
        int progress = this.menu.getScaledProgress();
        context.blit(BACKGROUND, x + ARROW_X, y + ARROW_Y, 176, 17, progress, 14); //Position Arrow
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        renderTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        titleLabelX = (imageWidth - font.width(title)) / 2;
    }

    @Override
    protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BACKGROUND);

        final int posX = this.leftPos;
        final int posY = this.topPos;
        context.blit(BACKGROUND, posX, posY, 0, 0, this.imageWidth - 1, this.imageHeight);

        renderProgressArrow(context, posX, posY);
        if (menu.getIsCooking()) {
            context.blit(BACKGROUND, posX + 40, posY + 54, 176, 0, 16, 14);
        }
    }
}
