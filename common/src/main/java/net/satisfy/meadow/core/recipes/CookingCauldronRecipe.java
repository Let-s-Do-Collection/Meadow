package net.satisfy.meadow.core.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.satisfy.meadow.core.cooking.CookingLiquids;
import net.satisfy.meadow.core.registry.RecipeRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CookingCauldronRecipe implements Recipe<RecipeInput> {
    private final NonNullList<Ingredient> inputs;
    private final ItemStack output;
    private final Optional<LiquidRequirement> liquid;
    private final ItemStack container;
    private final int craftingDuration;

    public CookingCauldronRecipe(NonNullList<Ingredient> inputs, ItemStack output, Optional<LiquidRequirement> liquid, ItemStack container, int craftingDuration) {
        this.inputs = inputs;
        this.output = output;
        this.liquid = liquid;
        this.container = container;
        this.craftingDuration = craftingDuration;
    }

    @Override
    public boolean matches(RecipeInput inventory, Level level) {
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < inventory.size(); i++) {
            items.add(inventory.getItem(i));
        }
        return matches(items);
    }

    public boolean matches(List<ItemStack> stacks) {
        return allocate(stacks) != null;
    }

    public int maxBatch(List<ItemStack> stacks) {
        int[] used = allocate(stacks);
        if (used == null) {
            return 0;
        }
        int batch = Integer.MAX_VALUE;
        for (int i = 0; i < stacks.size(); i++) {
            if (used[i] > 0) {
                batch = Math.min(batch, stacks.get(i).getCount() / used[i]);
            }
        }
        return batch == Integer.MAX_VALUE ? 1 : batch;
    }

    public void consume(List<ItemStack> stacks, int batch) {
        int[] used = allocate(stacks);
        if (used == null) {
            return;
        }
        for (int i = 0; i < stacks.size(); i++) {
            stacks.get(i).shrink(used[i] * batch);
        }
    }

    private int[] allocate(List<ItemStack> stacks) {
        int[] used = new int[stacks.size()];
        for (Ingredient ingredient : inputs) {
            boolean found = false;
            for (int i = 0; i < stacks.size(); i++) {
                ItemStack stack = stacks.get(i);
                if (!stack.isEmpty() && used[i] < stack.getCount() && ingredient.test(stack)) {
                    used[i]++;
                    found = true;
                    break;
                }
            }
            if (!found) {
                return null;
            }
        }
        for (int i = 0; i < stacks.size(); i++) {
            if (!stacks.get(i).isEmpty() && used[i] == 0) {
                return null;
            }
        }
        return used;
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
        return output.copy();
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.Provider provider) {
        return output.copy();
    }

    public Optional<LiquidRequirement> getLiquid() {
        return liquid;
    }

    public ItemStack getContainer() {
        return container;
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

    public record LiquidRequirement(ResourceLocation id, int amount) {
        public static final Codec<LiquidRequirement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(LiquidRequirement::id),
                Codec.intRange(1, CookingLiquids.CAPACITY).optionalFieldOf("amount", 1).forGetter(LiquidRequirement::amount)
        ).apply(instance, LiquidRequirement::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, LiquidRequirement> STREAM_CODEC = StreamCodec.composite(
                ResourceLocation.STREAM_CODEC, LiquidRequirement::id,
                ByteBufCodecs.VAR_INT, LiquidRequirement::amount,
                LiquidRequirement::new
        );
    }

    public static class Serializer implements RecipeSerializer<CookingCauldronRecipe> {
        public static final MapCodec<CookingCauldronRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                Ingredient.CODEC.listOf().fieldOf("ingredients").forGetter(r -> List.copyOf(r.inputs)),
                ItemStack.CODEC.fieldOf("result").forGetter(r -> r.output),
                LiquidRequirement.CODEC.optionalFieldOf("liquid").forGetter(r -> r.liquid),
                Codec.INT.optionalFieldOf("fluid_amount", 0).forGetter(r -> 0),
                ItemStack.SIMPLE_ITEM_CODEC.optionalFieldOf("container", ItemStack.EMPTY).forGetter(r -> r.container),
                Codec.INT.optionalFieldOf("crafting_duration", 10).forGetter(r -> r.craftingDuration)
        ).apply(i, Serializer::create));

        public static final StreamCodec<RegistryFriendlyByteBuf, CookingCauldronRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.collection(NonNullList::createWithCapacity, Ingredient.CONTENTS_STREAM_CODEC), r -> r.inputs,
                ItemStack.STREAM_CODEC, r -> r.output,
                ByteBufCodecs.optional(LiquidRequirement.STREAM_CODEC), r -> r.liquid,
                ItemStack.OPTIONAL_STREAM_CODEC, r -> r.container,
                ByteBufCodecs.VAR_INT, r -> r.craftingDuration,
                CookingCauldronRecipe::new
        );

        private static CookingCauldronRecipe create(List<Ingredient> ingredients, ItemStack result, Optional<LiquidRequirement> liquid, int legacyFluid, ItemStack container, int duration) {
            NonNullList<Ingredient> inputs = NonNullList.create();
            inputs.addAll(ingredients);
            Optional<LiquidRequirement> resolved = liquid.isEmpty() && legacyFluid > 0
                    ? Optional.of(new LiquidRequirement(CookingLiquids.WATER, Math.min(CookingLiquids.CAPACITY, Math.max(1, (legacyFluid + 24) / 25))))
                    : liquid;
            return new CookingCauldronRecipe(inputs, result, resolved, container, duration);
        }

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
