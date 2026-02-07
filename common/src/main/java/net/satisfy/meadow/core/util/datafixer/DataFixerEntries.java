package net.satisfy.meadow.core.util.datafixer;

public final class DataFixerEntries {
    private DataFixerEntries() {
    }

    public static void init() {
        DataFixers.register("meadow:shelf", "meadow:pine_cabinet");
        DataFixers.register("meadow:cheese_rack", "meadow:pine_cheese_rack");
        DataFixers.register("meadow:chair", "meadow:pine_chair");
        DataFixers.register("meadow:table", "meadow:pine_table");
        DataFixers.register("meadow:bench", "meadow:pine_bench");
        DataFixers.register("meadow:can", "meadow:milk_can");
        DataFixers.register("meadow:linen_wool", "meadow:linen");
        DataFixers.register("meadow:sun_patterned_window", "meadow:ornate_glass_window_pane");
        DataFixers.register("meadow:heart_patterned_window", "meadow:artisan_glass_window_pane");
        DataFixers.register("meadow:eriophorum_tall", "meadow:tall_eriophorum");
        DataFixers.register("meadow:alpine_birch_leaves", "meadow:alpine_birch_leaves");
        DataFixers.register("meadow:fire_log", "meadow:firewood");
        DataFixers.register("meadow:fondue", "meadow:fondue_pot");
        DataFixers.register("meadow:cooking_cauldron", "meadow:cookpot");
        DataFixers.register("meadow:chiseled_limestone_bricks", "meadow:chiseled_limestone");
        DataFixers.register("meadow:polished_limestone_bricks", "meadow:polished_limestone");
    }
}