package net.satisfy.meadow.fabric.cca;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Sheep;
import net.satisfy.meadow.Meadow;

public class MyComponents implements EntityComponentInitializer {
    public static final ComponentKey<VarHolder> VAR =
            ComponentRegistryV3.INSTANCE.getOrCreate(Meadow.identifier("var"), VarHolder.class);
    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(Cow.class, VAR, VarHolder::new);
        registry.registerFor(Chicken.class, VAR, VarHolder::new);
        registry.registerFor(Sheep.class, VAR, VarHolder::new);
    }
}
