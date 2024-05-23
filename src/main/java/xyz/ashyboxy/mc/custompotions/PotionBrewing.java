package xyz.ashyboxy.mc.custompotions;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.Map;

public class PotionBrewing {
    // i looked at using a registry but it confused me
    public static Map<ResourceLocation, CustomPotion> CUSTOM_POTIONS = Collections.emptyMap();
    public static Map<ResourceLocation, PotionRecipe> POTION_RECIPES = Collections.emptyMap();

    public static void mix(ItemStack reagent, ItemStack potion, CallbackInfoReturnable<ItemStack> cir) {
        if (!potion.isEmpty()) {
            PotionLike p = PotionLike.fromItemStack(potion);

            if (p == PotionLike.EMPTY) {
                cir.setReturnValue(potion);
                return;
            }
            if (p == null) return;

            // for these two we return regardless,
            // we don't want to support using them in recipes
            if (reagent.getItem() == Items.GUNPOWDER) {
                if (potion.getItem() == Items.POTION) {
                    cir.setReturnValue(p.customPotions$make(new ItemStack(Items.SPLASH_POTION)));
                }
                return;
            } else if (reagent.getItem() == Items.DRAGON_BREATH) {
                if (potion.getItem() == Items.SPLASH_POTION) {
                    cir.setReturnValue(p.customPotions$make(new ItemStack(Items.LINGERING_POTION)));
                }
                return;
            }

            ItemStack output;
            if ((output = getResult(reagent, potion)) != null)
                cir.setReturnValue(output);
        }
    }

    public static void hasMix(ItemStack input, ItemStack reagent, CallbackInfoReturnable<Boolean> cir) {
        if (PotionLike.fromItemStack(input) == PotionLike.EMPTY) {
            cir.setReturnValue(false);
            return;
        }
        if (getResult(reagent, input) != null) cir.setReturnValue(true);
    }

    public static void isIngredient(ItemStack input, CallbackInfoReturnable<Boolean> cir) {
        for (PotionRecipe r : PotionLike.getRecipes().values()) {
            if (input.getItem() == r.getReagent()) {
                cir.setReturnValue(true);
                return;
            }
        }
    }

    @Nullable
    private static ItemStack getResult(ItemStack reagent, ItemStack potion) {
        PotionLike p = PotionLike.fromItemStack(potion);
        if (p == null || p == PotionLike.EMPTY) return null;
        for (PotionRecipe r : PotionLike.getRecipes().values()) {
            if (reagent.getItem() != r.getReagent()) continue;
            if (!p.customPotions$same(r.getBase())) continue;
            return r.getResult().customPotions$make(potion);
        }
        return null;
    }
}
