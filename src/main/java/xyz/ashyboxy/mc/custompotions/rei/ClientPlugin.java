package xyz.ashyboxy.mc.custompotions.rei;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;

// TODO: like this, things will only be registered on initial join
public class ClientPlugin implements REIClientPlugin {
    @Override
    public void registerDisplays(DisplayRegistry registry) {
//        REIHelper.registerDisplays(registry);
    }

   @Override
    public void registerEntries(EntryRegistry registry) {
//        REIHelper.registerEntries(registry);
    }
}
