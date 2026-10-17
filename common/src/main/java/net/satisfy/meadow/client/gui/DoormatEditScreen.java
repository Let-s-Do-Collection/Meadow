package net.satisfy.meadow.client.gui;

import dev.architectury.networking.NetworkManager;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.core.block.entity.DoormatBlockEntity;
import net.satisfy.meadow.core.network.packet.SetDoormatTextPacket;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;

public class DoormatEditScreen extends Screen {
    private static final ResourceLocation TEXTURE = Meadow.identifier("textures/block/doormat.png");
    private static final int SCALE = 12;
    private static final int MAT_WIDTH = 14 * SCALE;
    private static final int MAT_HEIGHT = 10 * SCALE;
    private static final int LINE_SPACING = 12;

    private final DoormatBlockEntity doormat;
    private final String[] lines = new String[DoormatBlockEntity.LINES];
    private int line;
    private TextFieldHelper field;

    public DoormatEditScreen(DoormatBlockEntity doormat) {
        super(Component.translatable("gui.meadow.doormat.edit"));
        this.doormat = doormat;
        for (int i = 0; i < lines.length; i++) {
            lines[i] = doormat.getLine(i);
        }
    }

    @Override
    protected void init() {
        field = new TextFieldHelper(() -> lines[line], text -> lines[line] = text,
                TextFieldHelper.createClipboardGetter(minecraft), TextFieldHelper.createClipboardSetter(minecraft),
                text -> text.length() <= DoormatBlockEntity.MAX_LENGTH);
        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> onClose())
                .bounds(width / 2 - 50, height / 2 + MAT_HEIGHT / 2 + 20, 100, 20)
                .build());
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(graphics, mouseX, mouseY, partialTick);
        int left = width / 2 - MAT_WIDTH / 2;
        int top = height / 2 - MAT_HEIGHT / 2;
        graphics.blit(TEXTURE, left, top, MAT_WIDTH, MAT_HEIGHT, 1, 3, 14, 10, 16, 16);
        graphics.drawCenteredString(font, title, width / 2, top - 20, 0xFFFFFF);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        int color = doormat.getColor().getTextColor() | 0xFF000000;
        int top = height / 2 - (LINE_SPACING * lines.length) / 2 + 2;
        boolean blink = (Util.getMillis() / 300L) % 2L == 0L;
        for (int i = 0; i < lines.length; i++) {
            String text = lines[i];
            int x = width / 2 - font.width(text) / 2;
            int y = top + i * LINE_SPACING;
            graphics.drawString(font, text, x, y, color, false);
            if (i != line) {
                continue;
            }
            int cursor = Math.min(field.getCursorPos(), text.length());
            int cursorX = x + font.width(text.substring(0, cursor));
            int selection = Math.min(field.getSelectionPos(), text.length());
            if (selection != cursor) {
                int selectionX = x + font.width(text.substring(0, selection));
                graphics.fill(Math.min(cursorX, selectionX), y - 1, Math.max(cursorX, selectionX), y + font.lineHeight, 0x80FFFFFF);
            } else if (blink) {
                if (cursor < text.length()) {
                    graphics.fill(cursorX, y - 1, cursorX + 1, y + font.lineHeight, color);
                } else {
                    graphics.drawString(font, "_", cursorX, y, color, false);
                }
            }
        }
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        field.charTyped(codePoint);
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_UP) {
            selectLine(line - 1);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_DOWN || keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            if (keyCode != GLFW.GLFW_KEY_DOWN && line == lines.length - 1) {
                onClose();
            } else {
                selectLine(line + 1);
            }
            return true;
        }
        return field.keyPressed(keyCode) || super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void selectLine(int target) {
        line = Math.floorMod(target, lines.length);
        field.setCursorToEnd();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        NetworkManager.sendToServer(new SetDoormatTextPacket(doormat.getBlockPos(), Arrays.asList(lines)));
        super.onClose();
    }
}
