package net.satisfy.meadow.core.recipes;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.satisfy.meadow.core.registry.RecipeRegistry;
import net.satisfy.meadow.core.util.GeneralUtil;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class CookingCauldronRecipe implements Recipe<Container> {
    final ResourceLocation id;
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
    public boolean matches(Container inventory, Level world) {
        return GeneralUtil.matchesRecipe(inventory, inputs, 1, 7);
    }

    public ItemStack assemble() {
        return assemble(null, null);
    }

    @Override
    public @NotNull ItemStack assemble(Container inventory, RegistryAccess registryManager) {
        return this.output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    public ItemStack getResultItem() {
        return getResultItem(null);
    }

    @Override
    public @NotNull ItemStack getResultItem(RegistryAccess registryManager) {
        return output.copy();
    }

    public int getFluidAmount() {
        return fluidAmount;
    }

    public int getCraftingDuration() {
        return craftingDuration;
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return id;
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

    @Override
    public boolean isSpecial() {
        return true;
    }

    public static class Serializer implements RecipeSerializer<CookingCauldronRecipe> {

        @Override
        public @NotNull CookingCauldronRecipe fromJson(ResourceLocation id, JsonObject json) {
            final var ingredients = GeneralUtil.deserializeIngredients(GsonHelper.getAsJsonArray(json, "ingredients"));
            if (ingredients.isEmpty()) {
                throw new JsonParseException("No ingredients for CookingCauldron Recipe");
            } else if (ingredients.size() > 6) {
                throw new JsonParseException("Too many ingredients for CookingCauldron Recipe");
            } else {
                int fluidAmount = GsonHelper.getAsInt(json, "fluid_amount", 0);
                int craftingDuration = GsonHelper.getAsInt(json, "crafting_duration", 10);
                return new CookingCauldronRecipe(
                        id,
                        ingredients,
                        ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result")),
                        fluidAmount,
                        craftingDuration
                );
            }
        }

        @Override
        public @NotNull CookingCauldronRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            final var ingredients = NonNullList.withSize(buf.readVarInt(), Ingredient.EMPTY);
            ingredients.replaceAll(ignored -> Ingredient.fromNetwork(buf));
            ItemStack output = buf.readItem();
            int fluidAmount = buf.readInt();
            int craftingDuration = buf.readInt();
            return new CookingCauldronRecipe(id, ingredients, output, fluidAmount, craftingDuration);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, CookingCauldronRecipe recipe) {
            buf.writeVarInt(recipe.inputs.size());
            recipe.inputs.forEach(entry -> entry.toNetwork(buf));
            buf.writeItem(recipe.output);
            buf.writeInt(recipe.fluidAmount);
            buf.writeInt(recipe.craftingDuration);
        }
    }

    public static class Type implements RecipeType<CookingCauldronRecipe> {
        private Type() {
        }

        public static final Type INSTANCE = new Type();

        public static final String ID = "cooking";
    }
}
