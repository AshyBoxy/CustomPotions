package xyz.ashyboxy.mc.custompotions;

import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import net.fabricmc.loader.api.FabricLoader;
import xyz.ashyboxy.mc.custompotions.rei.REIHelper;

public class PluginHelper {
    public static void reloadPotions() {
        if(FabricLoader.getInstance().isModLoaded("roughlyenoughitems")) REIHelper.registerEntries(EntryRegistry.getInstance());
    }

    public static void reloadRecipes() {
        if(FabricLoader.getInstance().isModLoaded("roughlyenoughitems")) REIHelper.registerDisplays(DisplayRegistry.getInstance());
    }
}
