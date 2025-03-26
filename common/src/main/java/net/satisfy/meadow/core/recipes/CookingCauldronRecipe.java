package net.satisfy.meadow.core.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.satisfy.meadow.core.registry.RecipeRegistry;
import net.satisfy.meadow.core.util.GeneralUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class CookingCauldronRecipe implements Recipe<RecipeInput> {
    private final ResourceLocation id;
    private final NonNullList<Ingredient> inputs;
    private final ItemStack output;
    private final int fluidAmount;
    private final int craftingDuration;

    public CookingCauldronRecipe(ResourceLocation id, NonNullList<Ingredient> inputs, ItemStack output, int fluidAmount, int craftingDuration) {
        this.id = id;
        this.inputs = inputs;
        this.output = output;
        this.fluidAmount = fluidAmount;
        this.craftingDuration = craftingDuration;
    }

    @Override
    public boolean matches(RecipeInput recipeInput, Level level) {
        return GeneralUtil.matchesRecipe(recipeInput, inputs, 0, 6);
    }

    public ItemStack assemble() {
        return assemble(null, null);
    }

    @Override
    public @NotNull ItemStack assemble(RecipeInput recipeInput, HolderLookup.Provider provider) {
        return this.output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.Provider provider) {
        return this.output.copy();
    }

    public int getFluidAmount() {
        return fluidAmount;
    }

    public int getCraftingDuration() {
        return craftingDuration;
    }

    public @NotNull ResourceLocation getId() {
        return ResourceLocation.fromNamespaceAndPath("meadow", "cooking");
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.COOKING_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return RecipeRegistry.COOKING.get();
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return this.inputs;
    }

    public List<Ingredient> getIngredientsList() {
        return new ArrayList<>(this.inputs);
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public static class Serializer implements RecipeSerializer<CookingCauldronRecipe> {
        public static final MapCodec<CookingCauldronRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").flatXmap(list -> {
                    Ingredient[] ingredients = list.toArray(Ingredient[]::new);
                    if (ingredients.length == 0) {
                        return DataResult.error(() -> "No ingredients for CookingCauldron recipe");
                    }
                    return DataResult.success(NonNullList.of(Ingredient.EMPTY, ingredients));
                }, DataResult::success).forGetter(CookingCauldronRecipe::getIngredients),

                ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.output),
                Codec.INT.fieldOf("fluid_amount").forGetter(CookingCauldronRecipe::getFluidAmount),
                Codec.INT.fieldOf("crafting_duration").forGetter(CookingCauldronRecipe::getCraftingDuration)
                ).apply(instance, CookingCauldronRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, CookingCauldronRecipe> STREAM_CODEC = StreamCodec.composite(
                GeneralUtil.StreamCodecUtil.nonNullList(Ingredient.CONTENTS_STREAM_CODEC, Ingredient.EMPTY), CookingCauldronRecipe::getIngredients,
                ItemStack.OPTIONAL_STREAM_CODEC, CookingCauldronRecipe::getResultItem,
                Codec.INT, CookingCauldronRecipe::getFluidAmount,
                Codec.INT, CookingCauldronRecipe::getCraftingDuration,
                CookingCauldronRecipe::new
        );

        @Override
        public @NotNull MapCodec<CookingCauldronRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, CookingCauldronRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
