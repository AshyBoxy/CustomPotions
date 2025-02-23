package xyz.ashyboxy.mc.custompotions.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import xyz.ashyboxy.mc.custompotions.PotionLike;

import java.util.List;

@Mixin(Arrow.class)
public abstract class ArrowMixin {
    @Shadow
    protected abstract ItemStack getDefaultPickupItem();

    @WrapOperation(method = "doPostHurtEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/alchemy/Potion;getEffects()Ljava/util/List;"))
    private List<MobEffectInstance> getEffects(Potion instance, Operation<List<MobEffectInstance>> original, @Local PotionContents potionContents, @Share("isCustomPotion") LocalBooleanRef isCustomPotion) {
        PotionLike p = PotionLike.fromItemStack(getDefaultPickupItem());
        if (p == null || p == PotionLike.EMPTY || p instanceof Potion)
            return original.call(instance);
        isCustomPotion.set(true);
        return potionContents.customEffects();
    }

    @WrapOperation(method = "doPostHurtEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/alchemy/PotionContents;customEffects()Ljava/util/List;"))
    private List<MobEffectInstance> noCustomEffects(PotionContents instance, Operation<List<MobEffectInstance>> original, @Share("isCustomPotion") LocalBooleanRef isCustomPotion) {
        if (isCustomPotion.get()) return List.of();
        return original.call(instance);
    }
}
