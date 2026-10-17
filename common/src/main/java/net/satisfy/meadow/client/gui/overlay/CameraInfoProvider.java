package net.satisfy.meadow.client.gui.overlay;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.foundation.overlay.BlockInfoProvider;
import net.satisfy.foundation.overlay.InfoSection;
import net.satisfy.meadow.core.block.CameraBlock;
import net.satisfy.meadow.core.block.entity.CameraBlockEntity;
import net.satisfy.meadow.platform.PlatformHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CameraInfoProvider implements BlockInfoProvider {
    @Override
    public List<InfoSection> describe(Level level, BlockPos pos, BlockState state, @Nullable BlockHitResult hit) {
        if (hit == null || !PlatformHelper.showCameraInfo() || !(state.getBlock() instanceof CameraBlock) || !(level.getBlockEntity(pos) instanceof CameraBlockEntity camera)) {
            return List.of();
        }
        List<InfoSection> sections = new ArrayList<>();
        int paper = camera.getPaper();
        if (camera.isCounting()) {
            sections.add(InfoSection.title(Component.translatable("hud.meadow.camera.timer", (camera.getTimer() + 19) / 20).withStyle(ChatFormatting.YELLOW)));
        } else if (paper == 0) {
            sections.add(InfoSection.icons(Component.translatable("hud.meadow.camera.needs_paper").withStyle(ChatFormatting.GOLD), List.of(new ItemStack(Items.PAPER)), InfoSection.ROW_COLUMNS));
        }
        if (paper > 0) {
            sections.add(InfoSection.icons(Component.translatable("hud.meadow.camera.in_camera"), List.of(new ItemStack(Items.PAPER, paper)), InfoSection.ROW_COLUMNS).withDecorations());
        }
        if (camera.isCounting()) {
            sections.add(hint("hud.meadow.camera.hint_cancel"));
            return sections;
        }
        if (paper < PlatformHelper.getCameraMaxPaper()) {
            sections.add(hint("hud.meadow.camera.hint_load"));
        }
        if (paper > 0) {
            sections.add(hint("hud.meadow.camera.hint_photo"));
        } else {
            sections.add(hint(PlatformHelper.enableCameraFlash() ? "hud.meadow.camera.hint_flash" : "hud.meadow.camera.hint_click"));
        }
        sections.add(hint("hud.meadow.camera.hint_timer"));
        return sections;
    }

    private static InfoSection hint(String key) {
        return InfoSection.title(Component.translatable(key).withStyle(ChatFormatting.GRAY));
    }
}
