package xyz.ashyboxy.mc.custompotions;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.*;

import static xyz.ashyboxy.mc.custompotions.CustomPotionsMod.LOGGER;

public class PotionRecipeResourceReloadListener extends SimpleJsonResourceReloadListener implements IdentifiableResourceReloadListener {
    private static int loadedRecipes = 0;

    public PotionRecipeResourceReloadListener() {
        super(new GsonBuilder().create(), "potion_recipes");
    }

    public static final ResourceLocation id = CustomPotionsMod.id("potion_recipes");

    public static int getLoadedRecipes() {
        return loadedRecipes;
    }

    @Override
    public ResourceLocation getFabricId() {
        return id;
    }

    @Override
    public Collection<ResourceLocation> getFabricDependencies() {
        List<ResourceLocation> deps = new ArrayList<>();
        deps.add(CustomPotionResourceReloadListener.id);
        return deps;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> recipes, ResourceManager resourceManager, ProfilerFiller profiler) {
        PotionBrewing.POTION_RECIPES = new HashMap<>();

        loadedRecipes = 0;

        recipes.forEach((id, jsonElement) -> {
            try {
                PotionRecipe r = PotionRecipe.getDCodec(id).decode(JsonOps.INSTANCE, jsonElement).getOrThrow().getFirst();

                PotionBrewing.POTION_RECIPES.put(id, r);

                loadedRecipes++;
            } catch (Exception e) {
                LOGGER.error("Error loading brewing recipe {}: {}", id.toString(), e.getMessage());
            }
        });

        LOGGER.info("Loaded {} custom potion recipes", loadedRecipes);
    }
}
