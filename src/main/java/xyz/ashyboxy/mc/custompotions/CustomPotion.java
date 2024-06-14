package xyz.ashyboxy.mc.custompotions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CustomPotion implements PotionLike {
    public static final Codec<CustomPotion> S_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ComponentSerialization.CODEC.fieldOf("fallback").forGetter(CustomPotion::getFallbackName),
            Codec.list(MobEffectInstance.CODEC).fieldOf("effects").forGetter(CustomPotion::getEffects),
            ResourceLocation.CODEC.fieldOf("id").forGetter(CustomPotion::getId)
    ).apply(instance, (f, e, id) -> new CustomPotion(e, f, id)));

    public static Codec<CustomPotion> getDCodec(ResourceLocation id) {
        return RecordCodecBuilder.create(instance -> instance.group(
                ComponentSerialization.CODEC.fieldOf("fallback").forGetter(CustomPotion::getFallbackName),
                Codec.list(MobEffectInstance.CODEC).fieldOf("effects").forGetter(CustomPotion::getEffects)
        ).apply(instance, (f, e) -> new CustomPotion(e, f, id)));
    }

    public static final StreamCodec<ByteBuf, CustomPotion> STREAM_CODEC = ByteBufCodecs.fromCodec(S_CODEC);

    public static ResourceLocation VANILLA_POTION_TYPE = CustomPotionsMod.id("vanilla");
    public static ResourceLocation CUSTOM_POTION_TYPE = CustomPotionsMod.id("custom");

    public static ResourceLocation CUSTOM_POTION_DATA = CustomPotionsMod.id("custom_potion");
    // the potions registry is synced...
    public static Holder<Potion> CUSTOM_POTION = Potions.THICK;

    @Nullable
    public static CustomPotion asCustomPotion(ItemStack potion) {
        return (PotionBrewing.CUSTOM_POTIONS.isEmpty() ? CustomPotionClientData.CUSTOM_POTIONS :
                PotionBrewing.CUSTOM_POTIONS).get(ResourceLocation.tryParse(potion.getOrDefault(DataComponents.CUSTOM_DATA,
                CustomData.EMPTY).copyTag().getCompound(CUSTOM_POTION_DATA.toString()).getString("id")));
    }
    public static boolean isCustomPotion(ItemStack potion) {
        return potion.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().contains(CUSTOM_POTION_DATA.toString(), Tag.TAG_COMPOUND);
    }

    public static void initialize() {
        PotionLike.registerHandler(CUSTOM_POTION_TYPE, new Handler() {
            @Override
            public ResourceLocation getType() {
                return CUSTOM_POTION_TYPE;
            }

            @Override
            public @Nullable PotionLike get(ResourceLocation value) {
                // if the server side map is empty, we're probably connected to a server
                // (otherwise both maps will be empty and this'll have the same result anyway)
                if (PotionBrewing.CUSTOM_POTIONS.isEmpty()) return CustomPotionClientData.CUSTOM_POTIONS.get(value);
                return PotionBrewing.CUSTOM_POTIONS.get(value);
            }

            @Override
            public @Nullable PotionLike fromItemStack(ItemStack stack) {
                var p = CustomPotion.asCustomPotion(stack);
                if (p != null) return p;
                if (CustomPotion.isCustomPotion(stack)) return PotionLike.EMPTY;
                return null;
            }

            @Override
            public Map<ResourceLocation, PotionRecipe> getRecipes() {
                return Map.copyOf(PotionBrewing.POTION_RECIPES.isEmpty() ? CustomPotionClientData.POTION_RECIPES :
                        PotionBrewing.POTION_RECIPES);
            }

            @Override
            public Map<ResourceLocation, PotionLike> getPotions() {
                return Map.copyOf(PotionBrewing.CUSTOM_POTIONS.isEmpty() ? CustomPotionClientData.CUSTOM_POTIONS
                        : PotionBrewing.CUSTOM_POTIONS);
            }
        });
    }

    private final List<MobEffectInstance> effects;
    private final Component fallbackName;
    private final ResourceLocation id;

    public CustomPotion(List<MobEffectInstance> effects, Component fallbackName,
            ResourceLocation id) {
        this.effects = effects;
        this.fallbackName = fallbackName;
        this.id = id;
    }

    public ItemStack create(ItemStack replacing) {
        ItemStack stack = new ItemStack(replacing.getItem(), 1);

        PotionContents customPotionContents = new PotionContents(Optional.of(CUSTOM_POTION), Optional.empty(),
                effects.stream().toList());
        stack.set(DataComponents.ITEM_NAME, getDisplayName(replacing.getItem()));
        stack.set(DataComponents.POTION_CONTENTS, customPotionContents);

        CompoundTag customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        CompoundTag potionData = new CompoundTag();
        potionData.put("id", StringTag.valueOf(id.toString()));
        customData.put(CUSTOM_POTION_DATA.toString(), potionData);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(customData));

        return stack;
    }

    public Component getDisplayName(Item item) {
        // TODO: make the fallback fully specified in the datapack
        // ^ i forget if this is still needed
        // i think i meant making it so you can set all 4 names fully
        String fallback = "Potion of ";
        String path = "normal";
        if (item == Items.SPLASH_POTION) {
            path = "splash";
            fallback = "Splash " + fallback;
        } else if (item == Items.LINGERING_POTION) {
            path = "lingering";
            fallback = "Lingering " + fallback;
        } else if (item == Items.TIPPED_ARROW) {
            path = "arrow";
            fallback = "Arrow of ";
        }

        return Component
                .translatableWithFallback(
                        String.format("custompotion.%s.%s.%s", this.id.getNamespace(), this.id.getPath(), path),
                        Component.literal(fallback).append(fallbackName).getString())
                .withStyle(Style.EMPTY.withItalic(false));
    }

    public List<MobEffectInstance> getEffects() {
        return effects;
    }

    public Component getFallbackName() {
        return fallbackName;
    }

    public ResourceLocation getId() {
        return id;
    }

    @Override
    public boolean customPotions$same(PotionLike potion) {
        if(!(potion instanceof CustomPotion)) return false;
        return ((CustomPotion) potion).id.equals(id); // wow, such thoroughness
    }

    @Override
    public ResourceLocation customPotions$getType() {
        return CUSTOM_POTION_TYPE;
    }

    @Override
    public ResourceLocation customPotions$getLocation() {
        return getId();
    }

    @Override
    public ItemStack customPotions$make(ItemStack base) {
        return create(base);
    }
}
