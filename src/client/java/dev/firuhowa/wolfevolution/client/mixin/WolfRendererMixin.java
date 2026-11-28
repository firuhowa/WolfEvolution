
package dev.firuhowa.wolfevolution.client.mixin;

import dev.firuhowa.wolfevolution.WolfSleepData;
import dev.firuhowa.wolfevolution.client.WolfRenderStateAccess;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.client.renderer.entity.state.WolfRenderState;
import net.minecraft.world.entity.animal.wolf.Wolf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WolfRenderer.class)
public abstract class WolfRendererMixin {

    @Inject(
            method = "extractRenderState",
            at = @At("TAIL")
    )
    private void wolfevolution$extractSleepState(
            Wolf wolf,
            WolfRenderState state,
            float partialTick,
            CallbackInfo info
    ) {
        boolean sleeping = WolfSleepData.isSleeping(wolf);

        // 睡眠状態を描画データへ反映
        ((WolfRenderStateAccess) state).wolfevolution$setSleeping(
                sleeping
        );

        if (sleeping) {
            // バニラの歩行アニメーションを停止
            state.walkAnimationSpeed = 0.0F;
            state.walkAnimationPos = 0.0F;
        }
    }
}
