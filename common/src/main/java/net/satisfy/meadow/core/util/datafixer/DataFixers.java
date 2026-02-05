package net.satisfy.meadow.core.util.datafixer;

import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class DataFixers {
    private static final Map<String, StringPairs> FIXERS = new ConcurrentHashMap<>();

    private DataFixers() {
    }

    public static StringPairs get(String namespace) {
        return FIXERS.get(namespace);
    }

    public static void register(String oldId, String newId) {
        ResourceLocation oldResourceLocation = ResourceLocation.tryParse(oldId);
        if (oldResourceLocation == null) {
            return;
        }

        ResourceLocation newResourceLocation = ResourceLocation.tryParse(newId);
        if (newResourceLocation == null) {
            return;
        }

        register(oldResourceLocation.getNamespace(), oldResourceLocation.getPath(), newResourceLocation.getNamespace() + ":" + newResourceLocation.getPath());
    }

    public static void register(String oldNamespace, String oldPath, String newPathOrResourceLocation) {
        if (oldNamespace == null || oldNamespace.isEmpty() || oldPath == null || oldPath.isEmpty() || newPathOrResourceLocation == null || newPathOrResourceLocation.isEmpty()) {
            return;
        }

        FIXERS.computeIfAbsent(oldNamespace, key -> new StringPairs()).put(oldPath, newPathOrResourceLocation);
    }
}