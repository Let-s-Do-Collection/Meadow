package net.satisfy.meadow.fabric.core.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "meadow")
@Config.Gui.Background("meadow:textures/block/pine_planks.png")
public class MeadowFabricConfig implements ConfigData {

    @ConfigEntry.Gui.CollapsibleObject
    public BlocksSettings blocks = new BlocksSettings();

    @ConfigEntry.Gui.CollapsibleObject
    public ItemsSettings items = new ItemsSettings();

    public static class BlocksSettings {
    }

    public static class ItemsSettings {

        @ConfigEntry.Gui.CollapsibleObject
        public BannerSettings banner = new BannerSettings();
    }

    public static class BannerSettings {

        public boolean giveEffect = true;

        public boolean showTooltip = true;

        public boolean isShowTooltipEnabled() {
            return giveEffect && showTooltip;
        }
    }
}
