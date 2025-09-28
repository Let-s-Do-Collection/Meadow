package net.satisfy.meadow.core.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.satisfy.meadow.core.registry.RecipeRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CookingCauldronRecipe implements Recipe<RecipeInput> {
    private final NonNullList<Ingredient> inputs;
    private final ItemStack output;
    private final int fluidAmount;
    private final int craftingDuration;

    public CookingCauldronRecipe(NonNullList<Ingredient> inputs, ItemStack output, int fluidAmount, int craftingDuration) {
        this.inputs = inputs;
        this.output = output;
        this.fluidAmount = fluidAmount;
        this.craftingDuration = craftingDuration;
    }

    @Override
    public boolean matches(RecipeInput inventory, Level level) {
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack s = inventory.getItem(i);
            if (!s.isEmpty()) items.add(s);
        }
        boolean[] used = new boolean[items.size()];
        for (Ingredient ing : inputs) {
            boolean ok = false;
            for (int i = 0; i < items.size(); i++) {
                if (!used[i] && ing.test(items.get(i))) {
                    used[i] = true;
                    ok = true;
                    break;
                }
            }
            if (!ok) return false;
        }
        return true;
    }

    public ItemStack assemble() {
        return assemble(null, null);
    }

    @Override
    public @NotNull ItemStack assemble(RecipeInput input, HolderLookup.Provider provider) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return false;
    }

    public ItemStack getResultItem() {
        return getResultItem(null);
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.Provider provider) {
        return output.copy();
    }

    public int getFluidAmount() {
        return fluidAmount;
    }

    public int getCraftingDuration() {
        return craftingDuration;
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return inputs;
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
    public boolean isSpecial() {
        return true;
    }

    public static class Serializer implements RecipeSerializer<CookingCauldronRecipe> {
        public static final MapCodec<CookingCauldronRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                Ingredient.CODEC.listOf().fieldOf("ingredients").xmap(list -> {
                    NonNullList<Ingredient> nl = NonNullList.create();
                    nl.addAll(list);
                    return nl;
                }, List::copyOf).forGetter(r -> r.inputs),
                ItemStack.CODEC.fieldOf("result").forGetter(r -> r.output),
                Codec.INT.optionalFieldOf("fluid_amount", 0).forGetter(r -> r.fluidAmount),
                Codec.INT.optionalFieldOf("crafting_duration", 10).forGetter(r -> r.craftingDuration)
        ).apply(i, CookingCauldronRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CookingCauldronRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.collection(NonNullList::createWithCapacity, Ingredient.CONTENTS_STREAM_CODEC), r -> r.inputs,
                ItemStack.STREAM_CODEC, r -> r.output,
                ByteBufCodecs.VAR_INT, r -> r.fluidAmount,
                ByteBufCodecs.VAR_INT, r -> r.craftingDuration,
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
