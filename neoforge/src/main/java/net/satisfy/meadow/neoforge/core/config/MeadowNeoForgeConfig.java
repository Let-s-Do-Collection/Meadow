package net.satisfy.meadow.neoforge.core.config;

import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public class MeadowNeoForgeConfig {
    public static final ModConfigSpec COMMON_CONFIG;
    public static final ModConfigSpec.BooleanValue GIVE_EFFECT;
    public static final ModConfigSpec.BooleanValue SHOW_TOOLTIP;

    public static boolean giveEffect = true;
    public static boolean showTooltip = true;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("Blocks");
        builder.push("Banner");
        GIVE_EFFECT = builder.define("giveEffect", true);
        SHOW_TOOLTIP = builder.define("showTooltip", true);
        builder.pop();
        builder.pop();
        COMMON_CONFIG = builder.build();
    }

    public static void onLoad(ModConfigEvent.Loading e) {
        sync();
    }

    public static void onReload(ModConfigEvent.Reloading e) {
        sync();
    }

    public static void sync() {
        giveEffect = GIVE_EFFECT.get();
        showTooltip = GIVE_EFFECT.get() && SHOW_TOOLTIP.get();
    }
}
