package net.satisfy.meadow.neoforge.core.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import net.satisfy.meadow.neoforge.core.registry.MeadowBiomeModifiers;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record AddBiomeEffectsModifier(
        HolderSet<Biome> biomes,
        Optional<Integer> grassColor,
        Optional<Integer> foliageColor,
        Optional<Integer> waterColor,
        Optional<Integer> waterFogColor
) implements BiomeModifier {

    public static final MapCodec<AddBiomeEffectsModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Biome.LIST_CODEC.fieldOf("biomes").forGetter(AddBiomeEffectsModifier::biomes),
                    Codec.INT.optionalFieldOf("grass_color").forGetter(AddBiomeEffectsModifier::grassColor),
                    Codec.INT.optionalFieldOf("foliage_color").forGetter(AddBiomeEffectsModifier::foliageColor),
                    Codec.INT.optionalFieldOf("water_color").forGetter(AddBiomeEffectsModifier::waterColor),
                    Codec.INT.optionalFieldOf("water_fog_color").forGetter(AddBiomeEffectsModifier::waterFogColor)
            ).apply(instance, AddBiomeEffectsModifier::new)
    );

    @Override
    public void modify(@NotNull Holder<Biome> biome, @NotNull Phase phase, ModifiableBiomeInfo.BiomeInfo.@NotNull Builder builder) {
        if (!biomes.contains(biome)) {
            return;
        }
        if (phase != Phase.BEFORE_EVERYTHING && phase != Phase.MODIFY) {
            return;
        }

        var effects = builder.getSpecialEffects();

        grassColor.ifPresent(effects::grassColorOverride);
        foliageColor.ifPresent(effects::foliageColorOverride);
        waterColor.ifPresent(effects::waterColor);
        waterFogColor.ifPresent(effects::waterFogColor);
    }

    @Override
    public @NotNull MapCodec<? extends BiomeModifier> codec() {
        return MeadowBiomeModifiers.BIOME_EFFECTS.get();
    }
}