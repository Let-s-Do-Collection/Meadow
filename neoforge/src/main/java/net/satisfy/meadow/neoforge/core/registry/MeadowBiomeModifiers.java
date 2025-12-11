package net.satisfy.meadow.neoforge.core.registry;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.neoforge.core.world.AddAnimalsBiomeModifier;
import net.satisfy.meadow.neoforge.core.world.AddBiomeEffectsModifier;

import java.util.function.Supplier;

public class MeadowBiomeModifiers {
    public static DeferredRegister<MapCodec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, Meadow.MOD_ID);

    public static final Supplier<MapCodec<AddBiomeEffectsModifier>> BIOME_EFFECTS = BIOME_MODIFIER_SERIALIZERS.register("biome_effects", () -> AddBiomeEffectsModifier.CODEC);
    public static DeferredHolder<MapCodec<? extends BiomeModifier>, MapCodec<AddAnimalsBiomeModifier>> ADD_ANIMALS_CODEC = BIOME_MODIFIER_SERIALIZERS.register("add_animals", () -> MapCodec.unit(AddAnimalsBiomeModifier::new));
}

