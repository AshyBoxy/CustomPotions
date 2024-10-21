package xyz.ashyboxy.mc.custompotions.rei;

import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.common.displays.brewing.BrewingRecipe;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCustomDisplay;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import xyz.ashyboxy.mc.custompotions.*;

import java.util.List;

// on join, rei starts the plugin too early
// on reload, rei starts it too late
// so, my solution is to only actually run the methods after they've been called twice
public class REIHelper {
    private static int displayCounter = 0;
    private static int entryCounter = 0;

    public static void registerDisplays(DisplayRegistry registry) {
//        if (CustomPotionClientData.reiReloading) {
//            displayCounter++;
//            if (displayCounter < 2) return;
//            displayCounter = 0;
//            CustomPotionClientData.reiReloading = false;
//        } else displayCounter = 0;

/*        // TODO: deduplicate this code from emi(?)
        for (PotionRecipe p : PotionLike.getRecipes().values()) {
            for (Item r : new Item[]{ Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION }) {
                registry.add(new BrewingRecipe(
                        Ingredient.of(p.getBase().customPotions$make(new ItemStack(r))),
                        Ingredient.of(p.getReagent()),
                        p.getResult().customPotions$make(new ItemStack(r))));
            }
        }

        EntryIngredient arrow = EntryIngredients.of(Items.ARROW);
        RecipeHolder<?> tippedArrowRecipe = registry.getRecipeManager().byKey(ResourceLocation.withDefaultNamespace(
                "tipped_arrow")).orElse(null);

        for (PotionLike p : PotionLike.getPotions().values()) {
            registry.add(new BrewingRecipe(
                    Ingredient.of(p.customPotions$make(new ItemStack(Items.POTION))),
                    Ingredient.of(Items.GUNPOWDER),
                    p.customPotions$make(new ItemStack(Items.SPLASH_POTION))));
            registry.add(new BrewingRecipe(
                    Ingredient.of(p.customPotions$make(new ItemStack(Items.SPLASH_POTION))),
                    Ingredient.of(Items.DRAGON_BREATH),
                    p.customPotions$make(new ItemStack(Items.LINGERING_POTION))));

            List<EntryIngredient> arrowRecipe = List.of(
                    arrow, arrow, arrow,
                    arrow, EntryIngredients.of(p.customPotions$make(new ItemStack(Items.LINGERING_POTION))), arrow,
                    arrow, arrow, arrow
            );

            registry.add(new DefaultCustomDisplay(tippedArrowRecipe, arrowRecipe,
                    List.of(EntryIngredients.of(p.customPotions$make(new ItemStack(Items.TIPPED_ARROW))))));
        }

        registry.add(new BrewingRecipe(
                Ingredient.of(PotionContents.createItemStack(Items.POTION, Potions.LUCK)),
                Ingredient.of(Items.GUNPOWDER),
                PotionContents.createItemStack(Items.SPLASH_POTION, Potions.LUCK)
        ));
        registry.add(new BrewingRecipe(
                Ingredient.of(PotionContents.createItemStack(Items.SPLASH_POTION, Potions.LUCK)),
                Ingredient.of(Items.DRAGON_BREATH),
                PotionContents.createItemStack(Items.LINGERING_POTION, Potions.LUCK)
        ));*/
    }

    public static void registerEntries(EntryRegistry registry) {
//        if (CustomPotionClientData.reiReloading) {
//            entryCounter++;
//            if (entryCounter < 2) return;
//            entryCounter = 0;
//            CustomPotionClientData.reiReloading = false;
//        } else entryCounter = 0;
        for (PotionLike p : PotionLike.getPotions().values()) {
            registry.addEntry(EntryStacks.of(p.customPotions$make(new ItemStack(Items.POTION))));
            registry.addEntry(EntryStacks.of(p.customPotions$make(new ItemStack(Items.SPLASH_POTION))));
            registry.addEntry(EntryStacks.of(p.customPotions$make(new ItemStack(Items.LINGERING_POTION))));
            registry.addEntry(EntryStacks.of(p.customPotions$make(new ItemStack(Items.TIPPED_ARROW))));
        }
    }
}
