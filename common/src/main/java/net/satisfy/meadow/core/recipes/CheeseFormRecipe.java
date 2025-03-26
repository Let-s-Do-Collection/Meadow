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

import java.util.ArrayList;
import java.util.List;

public class CheeseFormRecipe implements Recipe<RecipeInput> {
    private final Ingredient bucket;
    private final Ingredient ingredient;
    private final ItemStack result;

    public CheeseFormRecipe(Ingredient bucket, Ingredient ingredient, ItemStack result) {
        this.bucket = bucket;
        this.ingredient = ingredient;
        this.result = result;
    }

    public ItemStack assemble() {
        return assemble(null, null);
    }


    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> defaultedList = NonNullList.create();
        defaultedList.add(this.bucket);
        defaultedList.add(this.ingredient);
        return defaultedList;
    }

    public Ingredient bucket() {
        return this.bucket;
    }

    public Ingredient ingredient() {
        return this.ingredient;
    }

    @Override
    public boolean matches(RecipeInput inventory, Level level) {
        NonNullList<Ingredient> ingredients = getIngredients();
        List<ItemStack> items = new ArrayList<>(List.of(inventory.getItem(1), inventory.getItem(2)));
        for (Ingredient ingredient : ingredients) {
            boolean matches = false;
            for (ItemStack stack : items) {
                if (ingredient.test(stack)) {
                    matches = true;
                    break;
                }
            }
            if (!matches) {
                return false;
            }
        }
        return true;
    }

    @Override
    public @NotNull ItemStack assemble(RecipeInput recipeInput, HolderLookup.Provider provider) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.Provider provider) {
        return this.result.copy();
    }

    public ItemStack getResultItem() {
        return getResultItem(null);
    }

    public ResourceLocation getId() {
        return ResourceLocation.fromNamespaceAndPath("meadow", "cheese_form");
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.CHEESE_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return RecipeRegistry.CHEESE.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public static class Serializer implements RecipeSerializer<CheeseFormRecipe> {
        public static final MapCodec<CheeseFormRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                        Ingredient.CODEC.fieldOf("bucket").forGetter(CheeseFormRecipe::bucket),
                        Ingredient.CODEC.fieldOf("ingredient").forGetter(CheeseFormRecipe::ingredient),
                        ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.result)
                ).apply(instance, CheeseFormRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, CheeseFormRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, CheeseFormRecipe::bucket,
                Ingredient.CONTENTS_STREAM_CODEC, CheeseFormRecipe::ingredient,
                ItemStack.OPTIONAL_STREAM_CODEC, CheeseFormRecipe::getResultItem,
                CheeseFormRecipe::new
        );

        @Override
        public @NotNull MapCodec<CheeseFormRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, CheeseFormRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}