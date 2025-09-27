package net.satisfy.meadow.core.recipes;

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
import org.jetbrains.annotations.NotNull;

public class WoodcuttingRecipe implements Recipe<RecipeInput> {
    private final Ingredient input;
    private final ItemStack outputStack;

    public WoodcuttingRecipe(ItemStack itemStack, Ingredient ingredient) {
        this.outputStack = itemStack;
        this.input = ingredient;
    }

    public Ingredient getInput() {
        return input;
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> defaultedList = NonNullList.create();
        defaultedList.add(this.input);
        return defaultedList;
    }

    @Override
    public boolean matches(RecipeInput recipeInput, Level level) {
        return this.input.test(recipeInput.getItem(0));
    }

    @Override
    public @NotNull ItemStack assemble(RecipeInput recipeInput, HolderLookup.Provider provider) {
        return this.outputStack.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.Provider provider) {
        return this.outputStack.copy();
    }

    public ItemStack getResultItem() {
        return getResultItem(null);
    }

    public ItemStack getOutputStack() {
        return getResultItem(null);
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public ResourceLocation getId() {
        return ResourceLocation.fromNamespaceAndPath("meadow", "woodcutting");
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.WOODCUTTING_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return RecipeRegistry.WOODCUTTING.get();
    }

    public static class Serializer implements RecipeSerializer<WoodcuttingRecipe> {
        public static final MapCodec<WoodcuttingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                        ItemStack.CODEC.fieldOf("outputItem").forGetter(recipe -> recipe.outputStack),
                        Ingredient.CODEC.fieldOf("inputItem").forGetter(WoodcuttingRecipe::getInput)
                ).apply(instance, WoodcuttingRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, WoodcuttingRecipe> STREAM_CODEC = StreamCodec.composite(
                ItemStack.STREAM_CODEC, WoodcuttingRecipe::getOutputStack,
                Ingredient.CONTENTS_STREAM_CODEC, WoodcuttingRecipe::getInput,
                WoodcuttingRecipe::new
        );

        @Override
        public @NotNull MapCodec<WoodcuttingRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, WoodcuttingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}