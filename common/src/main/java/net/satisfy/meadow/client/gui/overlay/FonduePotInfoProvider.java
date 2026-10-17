package net.satisfy.meadow.client.gui.overlay;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.satisfy.meadow.Meadow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.foundation.overlay.BlockInfoProvider;
import net.satisfy.foundation.overlay.InfoSection;
import net.satisfy.foundation.tooltip.TooltipBorder;
import net.satisfy.meadow.core.block.FonduePotBlock;
import net.satisfy.meadow.core.block.entity.FonduePotBlockEntity;
import net.satisfy.meadow.core.registry.ObjectRegistry;
import net.satisfy.meadow.core.registry.TagRegistry;
import net.satisfy.meadow.platform.PlatformHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class FonduePotInfoProvider implements BlockInfoProvider {
    private static final ResourceLocation STIR_ICON = Meadow.identifier("textures/gui/stir.png");
    private static final int STIR_ICON_SIZE = 20;
    private static final int STIR_START_COLOR = 0xFFFFD700;
    private static final int STIR_END_COLOR = 0xFFE0301E;
    private static final float STIR_FADE_TICKS = 60.0F;
    @Override
    public List<InfoSection> describe(Level level, BlockPos pos, BlockState state, @Nullable BlockHitResult hit) {
        if (hit == null || !PlatformHelper.showFonduePotInfo() || !(state.getBlock() instanceof FonduePotBlock) || !(level.getBlockEntity(pos) instanceof FonduePotBlockEntity pot)) {
            return List.of();
        }
        List<InfoSection> sections = new ArrayList<>();
        if (!FonduePotBlock.isHeated(level, pos)) {
            sections.add(InfoSection.title(Component.translatable("hud.meadow.fondue_pot.needs_heat").withStyle(ChatFormatting.GOLD)));
            return sections;
        }
        ItemStack melting = pot.getMelting();
        if (!melting.isEmpty()) {
            if (pot.isStirring()) {
                sections.add(InfoSection.icons(Component.translatable("hud.meadow.fondue_pot.melting"), List.of(melting), InfoSection.ROW_COLUMNS));
            } else {
                float progress = Mth.clamp((level.getGameTime() - pot.getLastStirTime()) / STIR_FADE_TICKS, 0.0F, 1.0F);
                int color = FastColor.ARGB32.lerp(progress, STIR_START_COLOR, STIR_END_COLOR) & 0xFFFFFF;
                sections.add(InfoSection.image(Component.translatable("hud.meadow.fondue_pot.stir").withStyle(Style.EMPTY.withBold(true).withColor(TextColor.fromRgb(color))), STIR_ICON, STIR_ICON_SIZE));
            }
            return sections;
        }
        int fill = pot.getPortions().size();
        if (fill > 0) {
            sections.add(InfoSection.icons(Component.translatable("hud.meadow.fondue_pot.ready").withStyle(ChatFormatting.GREEN), List.of(new ItemStack(ObjectRegistry.CHEESE_STICK.get(), fill)), InfoSection.ROW_COLUMNS));
            return sections;
        }
        sections.add(InfoSection.icons(Component.translatable("hud.meadow.fondue_pot.start_with"), cheeses(), InfoSection.GRID_COLUMNS));
        return sections;
    }

    @Override
    public void beforeBackground(Level level, BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof FonduePotBlock) || !FonduePotBlock.isHeated(level, pos)) {
            return;
        }
        if (level.getBlockEntity(pos) instanceof FonduePotBlockEntity pot && !pot.getMelting().isEmpty() && !pot.isStirring()) {
            TooltipBorder.mark(0xF0FFD700, 0xF0B8860B);
        } else {
            TooltipBorder.markHot();
        }
    }

    private static List<ItemStack> cheeses() {
        Set<Item> items = new LinkedHashSet<>();
        for (Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(TagRegistry.CHEESE)) {
            items.add(holder.value());
        }
        return items.stream().map(Item::getDefaultInstance).toList();
    }
}
