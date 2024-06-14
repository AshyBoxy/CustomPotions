package xyz.ashyboxy.mc.custompotions;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.HashMap;
import java.util.Map;

import static xyz.ashyboxy.mc.custompotions.CustomPotionsMod.LOGGER;

public class CustomPotionResourceReloadListener extends SimpleJsonResourceReloadListener
        implements IdentifiableResourceReloadListener {
    public CustomPotionResourceReloadListener() {
        super(new GsonBuilder().create(), "custom_potions");
    }

    public static final ResourceLocation id = CustomPotionsMod.id("custom_potions");

    @Override
    public ResourceLocation getFabricId() {
        return id;
    }

    private static int loadedPotions = 0;

    public static int getLoadedPotions() {
        return loadedPotions;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> potions, ResourceManager resourceManager,
            ProfilerFiller profiler) {
        PotionBrewing.CUSTOM_POTIONS = new HashMap<>();

        loadedPotions = 0;

        potions.forEach((id, jsonElement) -> {
            try {
                CustomPotion pot = CustomPotion.getDCodec(id).decode(JsonOps.INSTANCE, jsonElement).getOrThrow().getFirst();

                PotionBrewing.CUSTOM_POTIONS.put(id, pot);

                loadedPotions++;
            } catch (Exception e) {
                LOGGER.error("Error loading potion {}: {}", id.toString(), e.getMessage());
            }
        });

        LOGGER.info("Loaded {} custom potions", loadedPotions);
    }
}
