package dev.firuhowa.wolfevolution;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.phys.Vec3;

public final class WolfSleepData {

    private static final byte AWAKE = 0;
    private static final byte SLEEPING_AI_ENABLED = 1;
    private static final byte SLEEPING_AI_DISABLED = 2;

    public static final AttachmentType<Byte> SLEEP_STATE =
            AttachmentRegistry.create(
                    WolfEvolution.id("wolf_sleep_state"),
                    builder -> builder
                            .initializer(() -> AWAKE)
                            .persistent(Codec.BYTE)
                            .syncWith(
                                    ByteBufCodecs.BYTE,
                                    AttachmentSyncPredicate.all()
                            )
            );

    private WolfSleepData() {
    }

    // Data Attachmentを登録するためにクラスを初期化する
    public static void initialize() {
    }

    public static boolean isSleeping(Wolf wolf) {
        return wolf.getAttachedOrElse(SLEEP_STATE, AWAKE) != AWAKE;
    }

    public static boolean wasNoAiBeforeSleep(Wolf wolf) {
        return wolf.getAttachedOrElse(SLEEP_STATE, AWAKE)
                == SLEEPING_AI_DISABLED;
    }

    public static void setSleeping(Wolf wolf, boolean sleeping) {
        setSleeping(wolf, sleeping, wolf.isNoAi());
    }

    public static void setSleeping(
            Wolf wolf,
            boolean sleeping,
            boolean wasNoAi
    ) {
        if (!sleeping) {
            wolf.setAttached(SLEEP_STATE, AWAKE);
            return;
        }

        wolf.setAttached(
                SLEEP_STATE,
                wasNoAi
                        ? SLEEPING_AI_DISABLED
                        : SLEEPING_AI_ENABLED
        );
    }

    // 保存後の再読み込みで睡眠状態が残っていた場合に復旧
    public static void recoverAfterReload(Wolf wolf) {

        if (!isSleeping(wolf)) {
            return;
        }

        boolean wasNoAi = wasNoAiBeforeSleep(wolf);

        wolf.setNoAi(wasNoAi);

        wolf.setOrderedToSit(false);
        wolf.setInSittingPose(false);

        wolf.getNavigation().stop();
        wolf.setDeltaMovement(Vec3.ZERO);

        setSleeping(wolf, false);
    }

    // 口笛などから呼び出す復旧処理
    public static void recover(Wolf wolf) {

        if (!isSleeping(wolf)) {
            return;
        }

        boolean wasNoAi = wasNoAiBeforeSleep(wolf);

        wolf.setNoAi(wasNoAi);
        wolf.setOrderedToSit(false);
        wolf.setInSittingPose(false);

        wolf.getNavigation().stop();
        wolf.setDeltaMovement(Vec3.ZERO);

        setSleeping(wolf, false);
    }
}