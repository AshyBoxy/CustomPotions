package xyz.ashyboxy.mc.custompotions;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;

import java.util.*;

import static xyz.ashyboxy.mc.custompotions.CustomPotionsMod.LOGGER;

public class PotionRecipeResourceReloadListener extends SimpleJsonResourceReloadListener<PotionRecipeResourceReloadListener.HalfLoadedRecipe> implements IdentifiableResourceReloadListener {
    private static int loadedRecipes = 0;

    // the normal codec uses the potionlike codec which depends on custom potions being loaded
    // minecraft seems to use this codec before running the custom potion resource reload listener
    // so that ends up with every recipe having potionlike.unknown instead of custompotion
    private static final Codec<HalfLoadedRecipe> HALF_LOADED_RECIPE_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    BuiltInRegistries.ITEM.byNameCodec().fieldOf("reagent").forGetter(HalfLoadedRecipe::reagent),
                    ExtraCodecs.JSON.fieldOf("base").forGetter(HalfLoadedRecipe::base),
                    ExtraCodecs.JSON.fieldOf("result").forGetter(HalfLoadedRecipe::result)
            ).apply(instance, HalfLoadedRecipe::new));
    protected record HalfLoadedRecipe(Item reagent, JsonElement base, JsonElement result) {}

    public PotionRecipeResourceReloadListener() {
        super(HALF_LOADED_RECIPE_CODEC, FileToIdConverter.json("potion_recipes"));
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
    protected void apply(Map<ResourceLocation, HalfLoadedRecipe> recipes, ResourceManager resourceManager,
                         ProfilerFiller profiler) {
        PotionBrewing.POTION_RECIPES = new HashMap<>();

        loadedRecipes = 0;

        recipes.forEach((id, halfLoadedRecipe) -> {
            try {
//                PotionRecipe r = PotionRecipe.getDCodec(id).decode(JsonOps.INSTANCE, jsonElement).getOrThrow().getFirst();

                PotionLike base = PotionLike.CODEC.decode(JsonOps.INSTANCE, halfLoadedRecipe.base).getOrThrow().getFirst();
                PotionLike result = PotionLike.CODEC.decode(JsonOps.INSTANCE, halfLoadedRecipe.result).getOrThrow().getFirst();

                PotionRecipe recipe = PotionRecipe.createFully(halfLoadedRecipe.reagent, id, base, result);

                PotionBrewing.POTION_RECIPES.put(id, recipe);

                loadedRecipes++;
            } catch (Exception e) {
                LOGGER.error("Error loading brewing recipe {}: {}", id.toString(), e.getMessage());
            }
        });

        LOGGER.info("Loaded {} custom potion recipes", loadedRecipes);
    }
}
