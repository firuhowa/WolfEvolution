
package dev.firuhowa.wolfevolution.mixin;

import dev.firuhowa.wolfevolution.WolfSleepData;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(
            method = "defineSynchedData",
            at = @At("TAIL")
    )
    private void wolfevolution$registerWolfSleepData(
            SynchedEntityData.Builder builder,
            CallbackInfo info
    ) {
        if ((Object) this instanceof Wolf) {
            builder.define(WolfSleepData.SLEEPING, false);
        }
    }
}
