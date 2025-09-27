package net.satisfy.meadow.core.util;

import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.satisfy.meadow.Meadow;

public class MeadowWoodType {
    public static final WoodType PINE = WoodType.register(new WoodType(Meadow.identifier("pine").toString(), BlockSetType.OAK));
}
