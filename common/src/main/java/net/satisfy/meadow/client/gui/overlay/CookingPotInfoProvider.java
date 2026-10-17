package net.satisfy.meadow.client.gui.overlay;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.core.registry.RecipeRegistry;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.foundation.overlay.BlockInfoProvider;
import net.satisfy.foundation.overlay.InfoSection;
import net.satisfy.foundation.tooltip.TooltipBorder;
import net.satisfy.meadow.core.block.CookingCauldronBlock;
import net.satisfy.meadow.core.block.CookingFrameBlock;
import net.satisfy.meadow.core.block.entity.CookingCauldronBlockEntity;
import net.satisfy.meadow.core.cooking.CookingLiquids;
import net.satisfy.meadow.core.recipes.CookingCauldronRecipe;
import net.satisfy.meadow.platform.PlatformHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CookingPotInfoProvider implements BlockInfoProvider {
    private static final int SKIM_START_COLOR = 0xFFFFD700;
    private static final int SKIM_END_COLOR = 0xFFE0301E;
    private static final float SKIM_FADE_TICKS = 100.0F;
    private static final ResourceLocation SKIM_ICON = Meadow.identifier("textures/gui/skim.png");
    private static final int SKIM_ICON_SIZE = 20;

    @Override
    public List<InfoSection> describe(Level level, BlockPos pos, BlockState state, @Nullable BlockHitResult hit) {
        CookingCauldronBlockEntity pot = pot(level, pos, state);
        if (hit == null || !PlatformHelper.showCookingPotInfo() || pot == null) {
            return List.of();
        }
        List<InfoSection> sections = new ArrayList<>();
        if (!pot.isHeated()) {
            sections.add(InfoSection.title(Component.translatable("hud.meadow.cooking_pot.needs_heat").withStyle(ChatFormatting.GOLD)));
            return sections;
        }
        List<ItemStack> ingredients = pot.getIngredients();
        if (pot.isCooking()) {
            if (pot.hasFoam()) {
                float progress = Mth.clamp((level.getGameTime() - pot.getFoamSince()) / SKIM_FADE_TICKS, 0.0F, 1.0F);
                int color = FastColor.ARGB32.lerp(progress, SKIM_START_COLOR, SKIM_END_COLOR) & 0xFFFFFF;
                sections.add(InfoSection.image(Component.translatable("hud.meadow.cooking_pot.skim").withStyle(Style.EMPTY.withBold(true).withColor(TextColor.fromRgb(color))), SKIM_ICON, SKIM_ICON_SIZE));
            } else {
                sections.add(InfoSection.icons(Component.translatable("hud.meadow.cooking_pot.cooking"), ingredients, InfoSection.ROW_COLUMNS));
            }
            return sections;
        }
        ItemStack result = pot.getResult();
        if (!result.isEmpty()) {
            Component title = pot.getResultContainer().isEmpty()
                    ? Component.translatable("hud.meadow.cooking_pot.take")
                    : Component.translatable("hud.meadow.cooking_pot.ready_to_fill");
            sections.add(InfoSection.icons(title.copy().withStyle(ChatFormatting.GREEN), List.of(result), InfoSection.ROW_COLUMNS));
            return sections;
        }
        if (!ingredients.isEmpty()) {
            CookingCauldronRecipe recipe = pot.getRecipe();
            if (recipe != null && !pot.hasLiquidFor(recipe)) {
                recipe.getLiquid().ifPresent(liquid -> sections.add(InfoSection.title(Component.translatable("hud.meadow.cooking_pot.needs_liquid", CookingLiquids.name(liquid.id())).withStyle(ChatFormatting.GOLD))));
                return sections;
            }
            List<ItemStack> next = nextIngredients(level, ingredients);
            if (!next.isEmpty()) {
                sections.add(InfoSection.icons(Component.translatable("hud.meadow.cooking_pot.next"), next, InfoSection.GRID_COLUMNS));
            } else if (recipe == null) {
                sections.add(InfoSection.title(Component.translatable("hud.meadow.cooking_pot.no_recipe").withStyle(ChatFormatting.RED)));
            }
            return sections;
        }
        List<ItemStack> start = startIngredients(level);
        if (!start.isEmpty()) {
            sections.add(InfoSection.icons(Component.translatable("hud.meadow.cooking_pot.start_with"), start, InfoSection.GRID_COLUMNS));
        }
        return sections;
    }

    @Override
    public void beforeBackground(Level level, BlockPos pos, BlockState state) {
        CookingCauldronBlockEntity pot = pot(level, pos, state);
        if (pot == null) {
            return;
        }
        if (pot.isCooking() && pot.hasFoam()) {
            TooltipBorder.mark(0xF0FFD700, 0xF0B8860B);
        } else if (pot.isHeated()) {
            TooltipBorder.markHot();
        }
    }

    private static List<ItemStack> startIngredients(Level level) {
        List<ItemStack> start = new ArrayList<>();
        for (RecipeHolder<CookingCauldronRecipe> holder : level.getRecipeManager().getAllRecipesFor(RecipeRegistry.COOKING.get())) {
            if (!holder.value().getIngredients().isEmpty()) {
                addFirst(start, holder.value().getIngredients().getFirst());
            }
        }
        return start;
    }

    private static List<ItemStack> nextIngredients(Level level, List<ItemStack> current) {
        List<ItemStack> next = new ArrayList<>();
        for (RecipeHolder<CookingCauldronRecipe> holder : level.getRecipeManager().getAllRecipesFor(RecipeRegistry.COOKING.get())) {
            List<Ingredient> open = new ArrayList<>(holder.value().getIngredients());
            boolean fits = true;
            for (ItemStack stack : current) {
                boolean used = false;
                for (int count = 0; count < stack.getCount(); count++) {
                    Ingredient match = open.stream().filter(ingredient -> ingredient.test(stack)).findFirst().orElse(null);
                    if (match == null) {
                        break;
                    }
                    open.remove(match);
                    used = true;
                }
                if (!used) {
                    fits = false;
                    break;
                }
            }
            if (fits) {
                open.forEach(ingredient -> addFirst(next, ingredient));
            }
        }
        return next;
    }

    private static void addFirst(List<ItemStack> list, Ingredient ingredient) {
        ItemStack[] choices = ingredient.getItems();
        if (choices.length > 0 && list.stream().noneMatch(known -> ItemStack.isSameItem(known, choices[0]))) {
            list.add(choices[0].copyWithCount(1));
        }
    }

    @Nullable
    private static CookingCauldronBlockEntity pot(Level level, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof CookingCauldronBlock) {
            return level.getBlockEntity(pos) instanceof CookingCauldronBlockEntity pot ? pot : null;
        }
        if (state.getBlock() instanceof CookingFrameBlock) {
            BlockPos base = state.getValue(CookingFrameBlock.HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;
            return level.getBlockEntity(base) instanceof CookingCauldronBlockEntity pot ? pot : null;
        }
        return null;
    }
}
