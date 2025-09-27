package net.satisfy.meadow.forge.core.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.io.File;

public class MeadowForgeConfig {
    public static final ForgeConfigSpec COMMON_CONFIG;

    public static final ForgeConfigSpec.BooleanValue GIVE_EFFECT;
    public static final ForgeConfigSpec.BooleanValue SHOW_TOOLTIP;


    static {
        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
        COMMON_BUILDER.push("Blocks");
        COMMON_BUILDER.push("Banner");

        GIVE_EFFECT = COMMON_BUILDER
                .comment("Set to false to disable the banner's effect.")
                .define("giveEffect", true);

        SHOW_TOOLTIP = COMMON_BUILDER
                .comment("Set to false to hide the banner's tooltip. If giveEffect is false, showTooltip is automatically false.")
                .define("showTooltip", true);

        COMMON_BUILDER.pop();

        COMMON_CONFIG = COMMON_BUILDER.build();
    }

    @SubscribeEvent
    public static void onLoad(ModConfigEvent.Loading e) {
        if (e.getConfig().getSpec() instanceof ForgeConfigSpec s) {
            loadConfig(s, e.getConfig().getFileName());
        }
    }

    @SubscribeEvent
    public static void onReload(ModConfigEvent.Reloading e) {
        if (e.getConfig().getSpec() instanceof ForgeConfigSpec s) {
            loadConfig(s, e.getConfig().getFileName());
        }
    }

    public static void loadConfig(ForgeConfigSpec s, String p) {
        CommentedFileConfig f = CommentedFileConfig.builder(new File(p))
                .sync()
                .preserveInsertionOrder()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();
        f.load();
        s.setConfig(f);
    }
}
