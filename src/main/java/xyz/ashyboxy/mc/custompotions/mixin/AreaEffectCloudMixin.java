package xyz.ashyboxy.mc.custompotions.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import xyz.ashyboxy.mc.custompotions.minterfaces.CustomPotionAreaEffectCloud;

import java.util.List;

@Mixin(AreaEffectCloud.class)
public abstract class AreaEffectCloudMixin implements CustomPotionAreaEffectCloud {
    @Shadow
    private PotionContents potionContents;

    @Unique
    private boolean isCustomPotion = false;

    @Override
    public boolean customPotions$isCustomPotion() {
        return isCustomPotion;
    }

    @Override
    public void customPotions$setCustomPotion(boolean isCustomPotion) {
        this.isCustomPotion = isCustomPotion;
    }

    @WrapOperation(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/alchemy/Potion;getEffects()Ljava/util/List;"))
    private List<MobEffectInstance> getEffects(Potion instance, Operation<List<MobEffectInstance>> original) {
        if (customPotions$isCustomPotion()) return potionContents.customEffects();
        return original.call(instance);
    }

    @WrapOperation(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/alchemy/PotionContents;customEffects()Ljava/util/List;"))
    private List<MobEffectInstance> noCustomEffects(PotionContents potionContents, Operation<List<MobEffectInstance>> original) {
        if (customPotions$isCustomPotion()) return List.of();
        return original.call(potionContents);
    }
}
