package net.satisfy.meadow.core.util.datafixer;

public final class DataFixerEntries {
    private DataFixerEntries() {
    }

    public static void init() {
        DataFixers.register("meadow:pine_scabinet", "meadow:pine_cabinet");
        DataFixers.register("meadow:cheese_rack", "meadow:pine_cheese_rack");
        DataFixers.register("meadow:chair", "meadow:pine_chair");
        DataFixers.register("meadow:table", "meadow:pine_table");
        DataFixers.register("meadow:bench", "meadow:pine_bench");
        DataFixers.register("meadow:pine_window", "meadow:pine_window_pane");
        DataFixers.register("meadow:can", "meadow:milk_can");
        DataFixers.register("meadow:linen_wool", "meadow:linen");
        DataFixers.register("sun_patterned_window", "ornate_glass_window_pane");
        DataFixers.register("heart_patterned_window", "artisan_glass_window_pane");
        DataFixers.register("eriophorum_tall", "tall_eriophorum");
    }
}