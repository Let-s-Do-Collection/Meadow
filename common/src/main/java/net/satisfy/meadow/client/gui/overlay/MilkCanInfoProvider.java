package net.satisfy.meadow.client.gui.overlay;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.foundation.overlay.BlockInfoProvider;
import net.satisfy.foundation.overlay.InfoSection;
import net.satisfy.meadow.core.block.MilkCanBlock;
import net.satisfy.meadow.platform.PlatformHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MilkCanInfoProvider implements BlockInfoProvider {
    private static final int MAX_FILL = 3;

    @Override
    public List<InfoSection> describe(Level level, BlockPos pos, BlockState state, @Nullable BlockHitResult hit) {
        if (!PlatformHelper.showMilkCanInfo() || !(state.getBlock() instanceof MilkCanBlock)) {
            return List.of();
        }
        int fill = state.getValue(MilkCanBlock.FILL_LEVEL);
        List<InfoSection> sections = new ArrayList<>();
        if (fill >= MAX_FILL) {
            sections.add(InfoSection.title(Component.translatable("hud.meadow.milk_can.full").withStyle(ChatFormatting.GREEN)));
        } else if (level.getEntities(EntityType.COW, new AABB(pos).inflate(3.0), cow -> true).isEmpty()) {
            sections.add(InfoSection.title(Component.translatable("hud.meadow.milk_can.needs_cow").withStyle(ChatFormatting.GOLD)));
        }
        if (fill > 0) {
            sections.add(InfoSection.icons(Component.translatable("hud.meadow.milk_can.in_milk_can"), List.of(new ItemStack(Items.MILK_BUCKET, fill)), InfoSection.ROW_COLUMNS).withDecorations());
            sections.add(InfoSection.title(Component.translatable("hud.meadow.milk_can.fill", fill, MAX_FILL).withStyle(ChatFormatting.GRAY)));
        }
        if (fill < MAX_FILL) {
            sections.add(InfoSection.title(Component.translatable("hud.meadow.milk_can.hint_fill").withStyle(ChatFormatting.GRAY)));
        }
        if (fill > 0) {
            sections.add(InfoSection.title(Component.translatable("hud.meadow.milk_can.hint_take").withStyle(ChatFormatting.GRAY)));
        }
        return sections;
    }
}
