
package dev.firuhowa.wolfevolution.client.mixin;

import dev.firuhowa.wolfevolution.client.AdultWolfModelAccess;
import dev.firuhowa.wolfevolution.client.WolfRenderStateAccess;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.animal.wolf.AdultWolfModel;
import net.minecraft.client.model.animal.wolf.WolfModel;
import net.minecraft.client.renderer.entity.state.WolfRenderState;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WolfModel.class)
public abstract class WolfModelMixin {

    @Shadow @Final protected ModelPart head;
    @Shadow @Final protected ModelPart body;
    @Shadow @Final protected ModelPart rightHindLeg;
    @Shadow @Final protected ModelPart leftHindLeg;
    @Shadow @Final protected ModelPart rightFrontLeg;
    @Shadow @Final protected ModelPart leftFrontLeg;
    @Shadow @Final protected ModelPart tail;

    @Unique
    private float wolfevolution$sleepAnimationTime;

    @Inject(method = "setupAnim", at = @At("TAIL"))
    private void wolfevolution$applySleepingPose(
            WolfRenderState state,
            CallbackInfo info
    ) {
        if (!((WolfRenderStateAccess) state).wolfevolution$isSleeping()) {
            wolfevolution$sleepAnimationTime = 0.0F;
            return;
        }

        if (!((Object) this instanceof AdultWolfModel)) {
            return;
        }

        AdultWolfModel adultModel = (AdultWolfModel) (Object) this;

        AdultWolfModelAccess adult =
                (AdultWolfModelAccess) (Object) this;

        ModelPart root = adultModel.root();
        ModelPart upperBody = adult.wolfevolution$getUpperBody();
        ModelPart realHead = adult.wolfevolution$getRealHead();
        ModelPart realTail = adult.wolfevolution$getRealTail();

        wolfevolution$sleepAnimationTime += 0.05F;

        float rollProgress =
                (wolfevolution$sleepAnimationTime - 5.0F) / 5.0F;

        rollProgress = Math.max(0.0F, Math.min(1.0F, rollProgress));

        rollProgress = rollProgress * rollProgress
                * (3.0F - 2.0F * rollProgress);

        float breathing = (float) Math.sin(
                wolfevolution$sleepAnimationTime
        );

        float breathingAmount = breathing * 0.025F;

        // 胴体
        body.y += 1.5F + breathingAmount;
        upperBody.y += 1.5F + breathingAmount;

        body.zRot = -0.08F;
        upperBody.zRot = -0.08F;

        // 前脚
        rightFrontLeg.xRot = -0.65F * rollProgress;
        leftFrontLeg.xRot = -0.45F * rollProgress;

        rightFrontLeg.zRot = 0.28F * rollProgress;
        leftFrontLeg.zRot = -0.28F * rollProgress;

        // 後ろ脚
        rightHindLeg.xRot = -0.35F * rollProgress;
        leftHindLeg.xRot = -0.60F * rollProgress;

        rightHindLeg.zRot = 0.40F * rollProgress;
        leftHindLeg.zRot = -0.45F * rollProgress;

        // 頭
        head.y += 1.0F;
        head.z -= 0.5F;
        head.xRot = 0.30F;

        realHead.xRot = 0.12F;

        // しっぽ
        tail.xRot = 0.45F;
        tail.zRot = 0.15F * rollProgress;

        realTail.zRot = 0.18F * rollProgress;

        float tailMovement =
                (float) Math.sin(
                        wolfevolution$sleepAnimationTime * 0.2F
                ) * 0.025F;

        realTail.yRot += tailMovement * rollProgress;

        // 呼吸
        upperBody.y += breathingAmount;

        // 横向き回転
        root.zRot = (float) (Math.PI / 2.0D) * rollProgress;
        root.y += 20.0F * rollProgress;
    }
}
