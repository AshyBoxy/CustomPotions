package xyz.ashyboxy.mc.custompotions.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.TippedArrowRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.ashyboxy.mc.custompotions.PotionLike;

@Mixin(TippedArrowRecipe.class)
public class TippedArrowRecipeMixin {
    @Inject(method = "assemble(Lnet/minecraft/world/inventory/CraftingContainer;Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/world/item/ItemStack;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;<init>" + "(Lnet/minecraft/world/level/ItemLike;I)V"), cancellable = true)
    private void assemble(CraftingContainer craftingContainer, HolderLookup.Provider registries, CallbackInfoReturnable<ItemStack> cir, @Local ItemStack itemStack) {
        PotionLike p = PotionLike.fromItemStack(itemStack);
        if (p == null || p == PotionLike.EMPTY || p instanceof Potion) // in case other mods modify the logic for vanilla potions
            return;
        ItemStack s = p.customPotions$make(new ItemStack(Items.TIPPED_ARROW));
        s.setCount(8);
        cir.setReturnValue(s);
    }
}
