package net.satisfy.meadow.core.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.satisfy.foundation.wood.BoatWood;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.core.registry.ObjectRegistry;

public class MeadowWoodType {
    public static final ResourceLocation PINE_ID = Meadow.identifier("pine");
    public static final WoodType PINE = WoodType.register(new WoodType(PINE_ID.toString(), BlockSetType.OAK));
    public static final BoatWood PINE_BOAT = BoatWood.register(PINE_ID, () -> ObjectRegistry.PINE_BOAT.get(), () -> ObjectRegistry.PINE_CHEST_BOAT.get());
}
