package net.satisfy.meadow.core.recipes;

import com.google.gson.JsonObject;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.satisfy.meadow.platform.PlatformHelper;

public class SimpleConditionalRecipe {

    public static class Serializer<T extends Recipe<?>> implements RecipeSerializer<T> {

        @Override
        public MapCodec<T> codec() {
            return null;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
            return null;
        }
    }

    public static boolean checkCondition(JsonObject c){
        String type = GsonHelper.getAsString(c, "type");

        if(type.equals("neoforge:mod_loaded")){
            String modId = c.get("modid").getAsString();
            return PlatformHelper.isModLoaded(modId);
        }
        return false;
    }
}