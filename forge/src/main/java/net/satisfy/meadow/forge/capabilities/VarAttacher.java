package net.satisfy.meadow.forge.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.satisfy.meadow.Meadow;
import org.jetbrains.annotations.NotNull;

public final class VarAttacher {
    private static class VarCapabilityProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

        public static final ResourceLocation IDENTIFIER = Meadow.identifier("var");
        private final VarHolder backend = new VarHolder();
        private final LazyOptional<VarHolder> optionalData = LazyOptional.of(() -> backend);

        @Override
        public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
            return MeadowCapabilities.VAR_HOLDER_CAPABILITY.orEmpty(cap, this.optionalData);
        }
        

        @Override
        public CompoundTag serializeNBT() {
            return this.backend.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            this.backend.deserializeNBT(nbt);
        }
    }

    public static void attach(final AttachCapabilitiesEvent<Entity> event) {
        Class<? extends Entity> entity = event.getObject().getClass();
        if (!(entity == Sheep.class || entity == Cow.class || entity == Chicken.class)) return;

        final VarCapabilityProvider provider = new VarCapabilityProvider();
        event.addCapability(VarCapabilityProvider.IDENTIFIER, provider);
    }
}
