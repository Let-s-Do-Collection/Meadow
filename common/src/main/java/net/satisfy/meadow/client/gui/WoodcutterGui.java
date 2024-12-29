package net.satisfy.meadow.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.client.gui.handler.WoodcutterGuiHandler;
import net.satisfy.meadow.recipes.WoodcuttingRecipe;

import java.util.List;

public class WoodcutterGui extends AbstractContainerScreen<WoodcutterGuiHandler> {
    private static final ResourceLocation TEXTURE = Meadow.identifier( "textures/gui/woodcutter.png");
    private final int recipeIconPosX = 58;
    private final int recipeIconPosY = 15;
    private final int maxRecipeIcons = 12;
    private final int recipeIconWidth = 16;
    private final int recipeIconHeight = 18;
    private final int recipeIconPerLine = 4;
    private boolean canCraft;
    private float scrollAmount;
    private boolean mouseClicked;
    private int scrollOffset;

    public WoodcutterGui(WoodcutterGuiHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
        handler.setContentsChangedListener(this::onInventoryChange);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.renderTooltip(context, mouseX, mouseY);
    }



    @Override
    protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
        this.renderBackground(context);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int posX = this.leftPos;
        int posY = this.topPos;
        context.blit(TEXTURE, posX, posY, 0, 0, this.imageWidth, this.imageHeight);

        int k = (int)(41.0f * this.scrollAmount);
        if(this.shouldScroll()) context.blit(TEXTURE, posX + 124, posY + 14 + k, 176, 0, 11, 15);
        int recipeX = posX + recipeIconPosX;
        int recipeY = posY + recipeIconPosY;

        int scrollOffsetOFF = scrollOffset + maxRecipeIcons;
        this.renderRecipeBackground(context, mouseX, mouseY, recipeX, recipeY, scrollOffsetOFF);
        this.renderRecipeIcons(context, recipeX, recipeY, scrollOffsetOFF);
    }

    private void renderRecipeBackground(GuiGraphics context, int mouseX, int mouseY, int x, int y, int scrollOffsetOFF) {
        for (int i = this.scrollOffset; i < scrollOffsetOFF && i < this.menu.getAvailableRecipeCount(); i++) {
            int offsetedI = i - this.scrollOffset;

            int posX = x + recipeIconWidth * (offsetedI % recipeIconPerLine);
            int l = offsetedI / recipeIconPerLine;
            int posY = y + recipeIconHeight * l;
            int offsetY = this.imageHeight;
            if (i == this.menu.getSelectedRecipe()) {
                offsetY += recipeIconHeight;
            } else if (mouseX >= posX && mouseY >= posY && mouseX < posX + recipeIconWidth && mouseY < posY + recipeIconHeight) {
                offsetY += recipeIconHeight * 2;
            }
            context.blit(TEXTURE, posX, posY, 0, offsetY, recipeIconWidth, recipeIconHeight);
        }
    }

    private void renderRecipeIcons(GuiGraphics context, int x, int y, int scrollOffsetOFF) {
        List<WoodcuttingRecipe> list = this.menu.getAvailableRecipes();
        for (int i = this.scrollOffset; i < scrollOffsetOFF && i < this.menu.getAvailableRecipeCount(); i++) {
            int offsetedI = i - this.scrollOffset;
            int k = x + recipeIconWidth * (offsetedI % recipeIconPerLine);
            int l = offsetedI / recipeIconPerLine;
            int m = y + recipeIconHeight * l + 1;
            context.renderItem(list.get(i).getResultItem(this.minecraft.level.registryAccess()), k, m);
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics context, int x, int y) {
        super.renderTooltip(context, x, y);
        if (this.canCraft) {
            int i = this.leftPos + recipeIconPosX;
            int j = this.topPos + recipeIconPosY;
            int scrollOffsetOFF = this.scrollOffset + maxRecipeIcons;
            List<WoodcuttingRecipe> list = this.menu.getAvailableRecipes();
            for (int l = scrollOffset; l < scrollOffsetOFF && l < this.menu.getAvailableRecipeCount(); l++) {
                int offsetedL = l - this.scrollOffset;

                int n = i + offsetedL % recipeIconPerLine * recipeIconWidth;
                int o = j + offsetedL / recipeIconPerLine * recipeIconHeight;
                if (x < n || x >= n + recipeIconWidth || y < o || y >= o + recipeIconHeight) continue;
                context.renderTooltip(this.font, list.get(l).getResultItem(this.minecraft.level.registryAccess()), x, y);
            }
        }
    }

    private void onInventoryChange() {
        this.canCraft = this.menu.canCraft();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.mouseClicked = false;
        if (this.canCraft) {
            int i = this.leftPos + recipeIconPosX;
            int j = this.topPos + recipeIconPosY;

            int k = scrollOffset + maxRecipeIcons;
            for (int l = scrollOffset; l < k; l++) {
                int offsetedL = l - this.scrollOffset;
                double d = mouseX - (double) (i + offsetedL % recipeIconPerLine * recipeIconWidth);
                double e = mouseY - (double) (j + offsetedL / recipeIconPerLine * recipeIconHeight);
                if (!(d >= 0.0) || !(e >= 0.0) || !(d < (float) recipeIconWidth) || !(e < (float) recipeIconHeight) || !menu.clickMenuButton(minecraft.player, l))
                    continue;
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0f));
                this.minecraft.gameMode.handleInventoryButtonClick(menu.containerId, l);
                return true;
            }

            i = this.leftPos + 124;
            j = this.topPos + 9;
            if (mouseX >= i && mouseX < (i + 11) && mouseY >= j && mouseY < (j + recipeIconPosX)) {
                this.mouseClicked = true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.mouseClicked && this.shouldScroll()) {
            int i = this.topPos + recipeIconPosY;
            int j = i + recipeIconPosX;
            this.scrollAmount = ((float)mouseY - (float)i - 7.5f) / ((float)(j - i) - 15.0f);
            this.scrollAmount = Mth.clamp(this.scrollAmount, 0.0f, 1.0f);
            this.scrollOffset = (int)((double)(this.scrollAmount * (float)this.getMaxScroll()) + 0.5) * 4;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (this.shouldScroll()) {
            int i = this.getMaxScroll();
            float f = (float)amount / (float)i;
            this.scrollAmount = Mth.clamp(this.scrollAmount - f, 0.0f, 1.0f);
            this.scrollOffset = (int)((double)(this.scrollAmount * (float)i) + 0.5) * 4;
        }
        return true;
    }

    private boolean shouldScroll() {
        return this.canCraft && this.menu.getAvailableRecipeCount() > maxRecipeIcons;
    }

    protected int getMaxScroll() {
        return (this.menu.getAvailableRecipeCount() + 4 - 1) / 4 - 3;
    }

    @Override
    protected void init() {
        super.init();
        titleLabelX = (imageWidth - font.width(title)) / 2;
    }
}
