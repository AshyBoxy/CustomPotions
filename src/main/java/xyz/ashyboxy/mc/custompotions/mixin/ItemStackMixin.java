package xyz.ashyboxy.mc.custompotions.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.item.component.CustomData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.ashyboxy.mc.custompotions.CustomPotion;

public abstract class ItemStackMixin {
    @Mixin(targets = "net/minecraft/world/item/ItemStack$1")
    public static abstract class StreamCodecMixin {
        // mojang's fix for MC-276327 was to add custom_name to PotionContents, but that doesn't do what we need
        // so move item_name to custom_name when encoding custom potion item stacks for networking
        @ModifyExpressionValue(method = "encode(Lnet/minecraft/network/RegistryFriendlyByteBuf;" +
                "Lnet/minecraft/world/item/ItemStack;)V", at =@At(value = "FIELD", target = "Lnet/minecraft/world/item/ItemStack;components:Lnet/minecraft/core/component/PatchedDataComponentMap;"))
        public PatchedDataComponentMap workaroundPotionContentsName(PatchedDataComponentMap components) {
            if (components.has(DataComponents.CUSTOM_NAME)) return components;
            if (!components.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).contains(CustomPotion.CUSTOM_POTION_DATA.toString())) return components;
            if (!components.has(DataComponents.ITEM_NAME)) return components;
            PatchedDataComponentMap copy = components.copy();
            copy.set(DataComponents.CUSTOM_NAME, copy.get(DataComponents.ITEM_NAME));
            return copy;
        }
    }
}
