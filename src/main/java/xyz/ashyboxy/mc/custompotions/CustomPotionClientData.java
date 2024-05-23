package xyz.ashyboxy.mc.custompotions;

import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.Map;

public class CustomPotionClientData {
    public static Map<ResourceLocation, PotionRecipe> POTION_RECIPES = Collections.emptyMap();
    public static Map<ResourceLocation, CustomPotion> CUSTOM_POTIONS = Collections.emptyMap();
    public static boolean recipesReady = false;
    // to prevent hanging on client reloads
    public static boolean reiReloading = false;
    public static boolean emiReloading = false;
}
