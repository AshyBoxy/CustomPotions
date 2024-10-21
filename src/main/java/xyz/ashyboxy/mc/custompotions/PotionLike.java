package xyz.ashyboxy.mc.custompotions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public interface PotionLike {
    boolean customPotions$same(PotionLike potion);
    ResourceLocation customPotions$getType();
    ResourceLocation customPotions$getLocation();
    ItemStack customPotions$make(ItemStack base);

    HashMap<ResourceLocation, Handler> handlers = new HashMap<>();
    static void registerHandler(ResourceLocation id, Handler handler) {
        handlers.put(id, handler);
    }

    @Nullable
    static PotionLike create(ResourceLocation type, ResourceLocation value) {
        for (Handler handler : handlers.values()) {
            // the handler's key doesn't have to match the type it handles
            if(type.equals(handler.getType())) {
                PotionLike p = handler.get(value);
                if(p != null) return p;
            }
        }
        // this one's special, since we always want it to run last so other handlers can cancel it
        if(type.equals(CustomPotion.VANILLA_POTION_TYPE)) {
            return (PotionLike) BuiltInRegistries.POTION.getValue(value);
        }
        return new Unknown(type, value);
    }

    @Nullable
    static PotionLike fromItemStack(ItemStack stack) {
        // see create
        for (Handler handler : handlers.values()) {
            var p = handler.fromItemStack(stack);
            if(p != null) return p;
        }
        var p = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).potion().orElse(null);
        if(p == null) return null;
        return (PotionLike) p.value();
    }

    static Map<ResourceLocation, PotionRecipe> getRecipes() {
        HashMap<ResourceLocation, PotionRecipe> recipes = new HashMap<>();
        for (Handler handler : handlers.values()) {
            recipes.putAll(handler.getRecipes());
        }
        return recipes;
    }

    static Map<ResourceLocation, PotionLike> getPotions() {
        HashMap<ResourceLocation, PotionLike> recipes = new HashMap<>();
        for (Handler handler : handlers.values()) {
            recipes.putAll(handler.getPotions());
        }
        return recipes;
    }

    PotionLike EMPTY = new PotionLike() {
        @Override
        public boolean customPotions$same(PotionLike potion) {
            return false;
        }
        @Override
        public ResourceLocation customPotions$getType() {
            return null;
        }
        @Override
        public ResourceLocation customPotions$getLocation() {
            return null;
        }
        @Override
        public ItemStack customPotions$make(ItemStack base) {
            return null;
        }
    };

    Codec<PotionLike> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("type").forGetter(PotionLike::customPotions$getType),
            ResourceLocation.CODEC.fieldOf("potion").forGetter(PotionLike::customPotions$getLocation))
            .apply(instance, PotionLike::create));

    StreamCodec<ByteBuf, PotionLike> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

    interface Handler {
        ResourceLocation getType();

        @Nullable
        PotionLike get(ResourceLocation value);

        @Nullable
        PotionLike fromItemStack(ItemStack stack);

        Map<ResourceLocation, PotionRecipe> getRecipes();

        Map<ResourceLocation, PotionLike> getPotions();
    }

    class Unknown implements PotionLike {
        public ResourceLocation type;
        public ResourceLocation location;

        public Unknown(ResourceLocation type, ResourceLocation location) {
            this.type = type;
            this.location = location;
        }

        @Override
        public boolean customPotions$same(PotionLike potion) {
            return potion.customPotions$getType().equals(type) && potion.customPotions$getLocation().equals(location);
        }

        @Override
        public ResourceLocation customPotions$getType() {
            return type;
        }

        @Override
        public ResourceLocation customPotions$getLocation() {
            return location;
        }

        @Override
        public ItemStack customPotions$make(ItemStack base) {
            return null;
        }
    }
}
