package net.satisfy.meadow.client.gui.overlay;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.foundation.overlay.BlockInfoProvider;
import net.satisfy.foundation.overlay.InfoSection;
import net.satisfy.meadow.core.block.CheeseFormBlock;
import net.satisfy.meadow.core.block.entity.CheeseFormBlockEntity;
import net.satisfy.meadow.core.recipes.CheeseFormRecipe;
import net.satisfy.meadow.core.registry.RecipeRegistry;
import net.satisfy.meadow.platform.PlatformHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class CheesePressInfoProvider implements BlockInfoProvider {
    @Override
    public List<InfoSection> describe(Level level, BlockPos pos, BlockState state, @Nullable BlockHitResult hit) {
        if (hit == null || !PlatformHelper.showCheesePressInfo() || !(state.getBlock() instanceof CheeseFormBlock) || !(level.getBlockEntity(pos) instanceof CheeseFormBlockEntity press)) {
            return List.of();
        }
        List<InfoSection> sections = new ArrayList<>();
        if (press.getResultCount() > 0) {
            sections.add(InfoSection.icons(Component.translatable("hud.meadow.cheese_press.take").withStyle(ChatFormatting.GREEN), List.of(press.getResult().copyWithCount(press.getResultCount())), InfoSection.ROW_COLUMNS).withDecorations());
            sections.add(hint("hud.meadow.cheese_press.hint_take"));
            return sections;
        }
        ItemStack result = result(level, press);
        if (press.isReady()) {
            sections.add(InfoSection.icons(Component.translatable("hud.meadow.cheese_press.ready").withStyle(ChatFormatting.GREEN), List.of(result), InfoSection.ROW_COLUMNS).withDecorations());
            sections.add(hint("hud.meadow.cheese_press.hint_crank_up"));
            return sections;
        }
        if (press.isPressed()) {
            sections.add(InfoSection.title(Component.translatable("hud.meadow.cheese_press.pressing", Math.round(press.pressProgress() * 100)).withStyle(ChatFormatting.YELLOW)));
            sections.add(contents(press));
            if (!result.isEmpty()) {
                sections.add(InfoSection.icons(Component.translatable("hud.meadow.cheese_press.result").withStyle(ChatFormatting.GREEN), List.of(result), InfoSection.ROW_COLUMNS).withDecorations());
            }
            return sections;
        }
        if (!press.isOpen()) {
            sections.add(InfoSection.title(Component.translatable("hud.meadow.cheese_press.cranked", Math.round(press.crankProgress(0) * 100)).withStyle(ChatFormatting.GRAY)));
            sections.add(contents(press));
            sections.add(hint("hud.meadow.cheese_press.hint_crank_down"));
            return sections;
        }
        if (press.getMilkCount() == 0 && press.getRennet().isEmpty()) {
            List<ItemStack> start = choices(level, recipe -> true, CheeseFormRecipe::bucket);
            choices(level, recipe -> true, CheeseFormRecipe::ingredient).forEach(start::add);
            sections.add(InfoSection.icons(Component.translatable("hud.meadow.cheese_press.start_with"), start, InfoSection.GRID_COLUMNS));
            sections.add(hint("hud.meadow.cheese_press.hint_add"));
            return sections;
        }
        sections.add(InfoSection.title(Component.translatable("hud.meadow.cheese_press.filled", press.getMilkCount(), CheeseFormBlockEntity.MAX_MILK).withStyle(ChatFormatting.GRAY)));
        if (press.getRennet().isEmpty()) {
            sections.add(InfoSection.icons(Component.translatable("hud.meadow.cheese_press.needs_rennet").withStyle(ChatFormatting.GOLD), choices(level, recipe -> recipe.bucket().test(press.getMilk()), CheeseFormRecipe::ingredient), InfoSection.ROW_COLUMNS));
        }
        sections.add(contents(press));
        if (press.getMilkCount() == 0) {
            sections.add(InfoSection.icons(Component.translatable("hud.meadow.cheese_press.next"), choices(level, recipe -> recipe.ingredient().test(press.getRennet()), CheeseFormRecipe::bucket), InfoSection.GRID_COLUMNS));
        } else if (!result.isEmpty()) {
            sections.add(InfoSection.icons(Component.translatable("hud.meadow.cheese_press.result").withStyle(ChatFormatting.GREEN), List.of(result), InfoSection.ROW_COLUMNS).withDecorations());
        }
        if (press.getMilkCount() < CheeseFormBlockEntity.MAX_MILK) {
            sections.add(hint("hud.meadow.cheese_press.hint_milk"));
        }
        if (press.getRennet().isEmpty()) {
            sections.add(hint("hud.meadow.cheese_press.hint_rennet"));
        }
        if (press.canCrank()) {
            sections.add(hint("hud.meadow.cheese_press.hint_crank_down"));
        }
        return sections;
    }

    private static InfoSection contents(CheeseFormBlockEntity press) {
        List<ItemStack> items = new ArrayList<>();
        if (press.getMilkCount() > 0) {
            items.add(press.getMilk().copyWithCount(press.getMilkCount()));
        }
        if (!press.getRennet().isEmpty()) {
            items.add(press.getRennet().copy());
        }
        return InfoSection.icons(Component.translatable("hud.meadow.cheese_press.in_cheese_press"), items, InfoSection.ROW_COLUMNS).withDecorations();
    }

    private static ItemStack result(Level level, CheeseFormBlockEntity press) {
        CheeseFormRecipe recipe = CheeseFormBlockEntity.recipeFor(level, press.getMilk());
        if (recipe == null) {
            return ItemStack.EMPTY;
        }
        ItemStack result = recipe.getResultItem(level.registryAccess());
        return result.copyWithCount(result.getCount() * press.getMilkCount());
    }

    private static List<ItemStack> choices(Level level, Predicate<CheeseFormRecipe> filter, Function<CheeseFormRecipe, Ingredient> part) {
        List<ItemStack> choices = new ArrayList<>();
        for (RecipeHolder<CheeseFormRecipe> holder : level.getRecipeManager().getAllRecipesFor(RecipeRegistry.CHEESE.get())) {
            if (!filter.test(holder.value())) {
                continue;
            }
            for (ItemStack stack : part.apply(holder.value()).getItems()) {
                if (choices.stream().noneMatch(known -> ItemStack.isSameItem(known, stack))) {
                    choices.add(stack.copyWithCount(1));
                }
            }
        }
        return choices;
    }

    private static InfoSection hint(String key) {
        return InfoSection.title(Component.translatable(key).withStyle(ChatFormatting.GRAY));
    }
}
