package xyz.ashyboxy.mc.custompotions;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.HashMap;
import java.util.Map;

import static xyz.ashyboxy.mc.custompotions.CustomPotionsMod.LOGGER;

public class CustomPotionResourceReloadListener extends SimpleJsonResourceReloadListener<CustomPotion>
        implements IdentifiableResourceReloadListener {
    public CustomPotionResourceReloadListener() {
        super(CustomPotion.getDCodec(loadingId), "custom_potions");
    }

    public static final ResourceLocation loadingId = CustomPotionsMod.id("/loading_potion");
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
    protected void apply(Map<ResourceLocation, CustomPotion> potions, ResourceManager resourceManager,
                         ProfilerFiller profiler) {
        PotionBrewing.CUSTOM_POTIONS = new HashMap<>();

        loadedPotions = 0;

        potions.forEach((id, potion) -> {
            try {
//                CustomPotion pot = CustomPotion.getDCodec(id).decode(JsonOps.INSTANCE, jsonElement).getOrThrow().getFirst();

                // TODO: should this use a holder now?
                potion.setId(id);
                PotionBrewing.CUSTOM_POTIONS.put(id, potion);

                loadedPotions++;
            } catch (Exception e) {
                LOGGER.error("Error loading potion {}: {}", id.toString(), e.getMessage());
            }
        });

        LOGGER.info("Loaded {} custom potions", loadedPotions);
    }
}
