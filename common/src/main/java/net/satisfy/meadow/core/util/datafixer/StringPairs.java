package net.satisfy.meadow.core.util.datafixer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class StringPairs {
    private final Map<String, String> oldPathToNewPathOrResourceLocation = new ConcurrentHashMap<>();

    public void put(String oldPath, String newPathOrResourceLocation) {
        if (oldPath == null || oldPath.isEmpty() || newPathOrResourceLocation == null || newPathOrResourceLocation.isEmpty()) {
            return;
        }
        oldPathToNewPathOrResourceLocation.put(oldPath, newPathOrResourceLocation);
    }

    public boolean containsOldPath(String oldPath) {
        return oldPath != null && oldPathToNewPathOrResourceLocation.containsKey(oldPath);
    }

    public String getNewPathOrRL(String oldPath) {
        return oldPath == null ? null : oldPathToNewPathOrResourceLocation.get(oldPath);
    }
}