package xyz.ashyboxy.mc.custompotions.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.recipe.EmiBrewingRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import xyz.ashyboxy.mc.custompotions.CustomPotionClientData;
import xyz.ashyboxy.mc.custompotions.CustomPotionsMod;
import xyz.ashyboxy.mc.custompotions.PotionLike;
import xyz.ashyboxy.mc.custompotions.PotionRecipe;

import java.util.List;

public class Plugin implements EmiPlugin {
    @Override
    public void register(EmiRegistry registry) {
        // i am so sorry
        if(CustomPotionClientData.emiReloading) {
            while (!CustomPotionClientData.recipesReady) continue;
            CustomPotionClientData.recipesReady = false;
        }

        for (PotionRecipe p : PotionLike.getRecipes().values()) {
            for (Item r : new Item[]{ Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION }) {
                registry.addRecipe(new EmiBrewingRecipe(
                        EmiStack.of(p.getBase().customPotions$make(new ItemStack(r))),
                        EmiIngredient.of(Ingredient.of(p.getReagent())),
                        EmiStack.of(p.getResult().customPotions$make(new ItemStack(r))),
                        new ResourceLocation(p.getId().getNamespace(),
                                "/recipe/" + p.getId().getPath() + "/" + BuiltInRegistries.ITEM.getKey(r).getNamespace() + "/" + BuiltInRegistries.ITEM.getKey(r).getPath())));
            }
        }

        EmiStack arrow = EmiStack.of(Items.ARROW);
        for (PotionLike p : PotionLike.getPotions().values()) {
            registry.addRecipe(new EmiBrewingRecipe(
                    EmiStack.of(p.customPotions$make(new ItemStack(Items.POTION))),
                    EmiIngredient.of(Ingredient.of(Items.GUNPOWDER)),
                    EmiStack.of(p.customPotions$make(new ItemStack(Items.SPLASH_POTION))),
                    new ResourceLocation(p.customPotions$getLocation().getNamespace(),
                            "/recipe/to_splash/" + p.customPotions$getLocation().getPath())));
            registry.addRecipe(new EmiBrewingRecipe(
                    EmiStack.of(p.customPotions$make(new ItemStack(Items.SPLASH_POTION))),
                    EmiIngredient.of(Ingredient.of(Items.DRAGON_BREATH)),
                    EmiStack.of(p.customPotions$make(new ItemStack(Items.LINGERING_POTION))),
                    new ResourceLocation(p.customPotions$getLocation().getNamespace(),
                            "/recipe/to_lingering/" + p.customPotions$getLocation().getPath())));
            registry.addRecipe(new EmiCraftingRecipe(List.of(
                    arrow, arrow, arrow,
                    arrow, EmiStack.of(p.customPotions$make(new ItemStack(Items.LINGERING_POTION))), arrow,
                    arrow, arrow, arrow
            ), EmiStack.of(p.customPotions$make(new ItemStack(Items.TIPPED_ARROW))),
                    new ResourceLocation(p.customPotions$getLocation().getNamespace(),
                            "/recipe/to_tipped_arrow/" + p.customPotions$getLocation().getPath()),
                    false));

            registry.addEmiStack(EmiStack.of(p.customPotions$make(new ItemStack(Items.POTION))));
            registry.addEmiStack(EmiStack.of(p.customPotions$make(new ItemStack(Items.SPLASH_POTION))));
            registry.addEmiStack(EmiStack.of(p.customPotions$make(new ItemStack(Items.LINGERING_POTION))));
            registry.addEmiStack(EmiStack.of(p.customPotions$make(new ItemStack(Items.TIPPED_ARROW))));
        }

        registry.addRecipe(new EmiBrewingRecipe(
                EmiStack.of(PotionContents.createItemStack(Items.POTION, Potions.LUCK)),
                EmiIngredient.of(Ingredient.of(Items.GUNPOWDER)),
                EmiStack.of(PotionContents.createItemStack(Items.SPLASH_POTION, Potions.LUCK)),
                new ResourceLocation(CustomPotionsMod.MOD_ID, "recipe/to_splash/luck") // no leading / is intentional
        ));
        registry.addRecipe(new EmiBrewingRecipe(
                EmiStack.of(PotionContents.createItemStack(Items.SPLASH_POTION, Potions.LUCK)),
                EmiIngredient.of(Ingredient.of(Items.DRAGON_BREATH)),
                EmiStack.of(PotionContents.createItemStack(Items.LINGERING_POTION, Potions.LUCK)),
                new ResourceLocation(CustomPotionsMod.MOD_ID, "recipe/to_lingering/luck") // no leading / is intentional
        ));
    }
}
