package net.satisfy.meadow.neoforge.core.mixin;

import net.minecraft.resources.ResourceLocation;
import net.satisfy.meadow.core.util.datafixer.DataFixers;
import net.satisfy.meadow.core.util.datafixer.StringPairs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ResourceLocation.class)
public class DataFixerMixin {
    @Mutable
    @Shadow
    @Final
    private String path;

    @Mutable
    @Shadow
    @Final
    private String namespace;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void meadow$updateResourceLocation(String namespace, String path, CallbackInfo ci) {
        if (namespace == null || path == null) {
            return;
        }

        StringPairs pairs = DataFixers.get(namespace);
        if (pairs == null || !pairs.containsOldPath(path)) {
            return;
        }

        String remapped = pairs.getNewPathOrRL(path);
        if (remapped == null || remapped.isEmpty()) {
            return;
        }

        int separatorIndex = remapped.indexOf(':');
        if (separatorIndex > 0 && separatorIndex < remapped.length() - 1) {
            this.namespace = remapped.substring(0, separatorIndex);
            this.path = remapped.substring(separatorIndex + 1);
            return;
        }

        this.path = remapped;
    }
}