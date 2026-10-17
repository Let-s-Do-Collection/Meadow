package net.satisfy.meadow.client.gui.overlay;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.foundation.overlay.BlockInfoProvider;
import net.satisfy.foundation.overlay.InfoSection;
import net.satisfy.foundation.storage.StorageBlockEntity;
import net.satisfy.foundation.util.ShapeUtil;
import net.satisfy.meadow.core.block.CheeseRackBlock;
import net.satisfy.meadow.core.registry.TagRegistry;
import net.satisfy.meadow.platform.PlatformHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CheeseRackInfoProvider implements BlockInfoProvider {
    @Override
    public List<InfoSection> describe(Level level, BlockPos pos, BlockState state, @Nullable BlockHitResult hit) {
        if (hit == null || !PlatformHelper.showCheeseRackInfo() || !(state.getBlock() instanceof CheeseRackBlock rack) || !(level.getBlockEntity(pos) instanceof StorageBlockEntity storage)) {
            return List.of();
        }
        int section = hoveredSection(rack, state, hit, storage);
        if (section < 0) {
            return List.of();
        }
        ItemStack stack = storage.getInventory().get(section);
        if (!stack.isEmpty()) {
            return List.of(InfoSection.icons(stack.getHoverName(), List.of(stack), InfoSection.ROW_COLUMNS));
        }
        return List.of(InfoSection.icons(Component.translatable("hud.meadow.cheese_rack.fits"), cheeseWheels(), InfoSection.ROW_COLUMNS));
    }

    private static int hoveredSection(CheeseRackBlock rack, BlockState state, BlockHitResult hit, StorageBlockEntity storage) {
        Optional<Tuple<Float, Float>> coordinates = ShapeUtil.getRelativeHitCoordinatesForBlockFace(hit, state.getValue(CheeseRackBlock.FACING), rack.unAllowedDirections());
        if (coordinates.isEmpty()) {
            return -1;
        }
        int section = rack.getSection(coordinates.get().getA(), coordinates.get().getB());
        return section >= 0 && section < storage.getInventory().size() ? section : -1;
    }

    private static List<ItemStack> cheeseWheels() {
        List<ItemStack> items = new ArrayList<>();
        for (Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(TagRegistry.CHEESE_WHEELS)) {
            items.add(holder.value().getDefaultInstance());
        }
        return items;
    }
}
