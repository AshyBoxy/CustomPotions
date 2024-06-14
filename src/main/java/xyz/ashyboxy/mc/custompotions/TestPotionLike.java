package xyz.ashyboxy.mc.custompotions;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

// for brevity's sake, this doesn't sync if it's loaded with clients
public class TestPotionLike implements PotionLike {
    public static ResourceLocation TEST_POTION = CustomPotionsMod.id("test");

    public static void initialize() {
        PotionLike.registerHandler(TEST_POTION, new Handler() {
            @Override
            public ResourceLocation getType() {
                return TEST_POTION;
            }

            @Override
            public @Nullable PotionLike get(ResourceLocation value) {
                if(value.equals(TEST_POTION)) return new TestPotionLike();
                return null;
            }

            @Override
            public @Nullable PotionLike fromItemStack(ItemStack stack) {
                if(stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean(TEST_POTION.toString())) return new TestPotionLike();
                return null;
            }

            @Override
            public Map<ResourceLocation, PotionRecipe> getRecipes() {
                return Map.of(TEST_POTION, new PotionRecipe(Items.ACACIA_BUTTON, TEST_POTION, (PotionLike) Potions.THICK.value(), new TestPotionLike()));
            }

            @Override
            public Map<ResourceLocation, PotionLike> getPotions() {
                return Map.of(TEST_POTION, new TestPotionLike());
            }
        });
    }

    @Override
    public boolean customPotions$same(PotionLike potion) {
        return potion instanceof TestPotionLike;
    }

    @Override
    public ResourceLocation customPotions$getType() {
        return TEST_POTION;
    }

    @Override
    public ResourceLocation customPotions$getLocation() {
        return TEST_POTION;
    }

    @Override
    public ItemStack customPotions$make(ItemStack base) {
        ItemStack stack = new ItemStack(base.getItem(), 1);

        PotionContents customPotionContents =
                new PotionContents(Potions.THICK).withEffectAdded(new MobEffectInstance(MobEffects.CONFUSION, 300, 4));
        stack.set(DataComponents.ITEM_NAME, Component.literal("Test Potion"));
        stack.set(DataComponents.POTION_CONTENTS, customPotionContents);

        CompoundTag customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        customData.put(TEST_POTION.toString(), ByteTag.ONE);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(customData));

        return stack;
    }
}
