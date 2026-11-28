
package dev.firuhowa.wolfevolution;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public final class WolfSleepManager {

    private static final double SLEEP_RADIUS = 16.0D;

    private static final double SLEEP_RADIUS_SQUARED =
            SLEEP_RADIUS * SLEEP_RADIUS;

    // Wolf UUID -> 睡眠中の狼と飼い主
    private static final Map<UUID, SleepingWolf> SLEEPING_WOLVES =
            new HashMap<>();

    private WolfSleepManager() {
    }

    public static void update(ServerPlayer owner) {

        if (!(owner.level() instanceof ServerLevel level)) {
            return;
        }

        UUID ownerId = owner.getUUID();

        // 飼い主が動いたら所有する狼を起こす
        if (!PlayerAfkTracker.isAfk(owner)) {
            wakeOwnerWolves(ownerId);
            return;
        }

        // すでに睡眠中の狼が範囲外に出ていないか確認
        wakeWolvesOutsideRadius(owner);

        AABB searchArea = owner.getBoundingBox().inflate(SLEEP_RADIUS);

        for (Wolf wolf : level.getEntitiesOfClass(
                Wolf.class,
                searchArea,
                candidate -> candidate.isAlive()
                        && candidate.isTame()
                        && candidate.isOwnedBy(owner)
        )) {

            UUID wolfId = wolf.getUUID();

            if (canSleep(wolf)) {

                SleepingWolf sleepingWolf =
                        SLEEPING_WOLVES.get(wolfId);

                // 初めて睡眠する場合だけ元のAI状態を保存
                if (sleepingWolf == null) {
                    sleepingWolf = new SleepingWolf(
                            ownerId,
                            wolf,
                            wolf.isNoAi()
                    );

                    SLEEPING_WOLVES.put(wolfId, sleepingWolf);
                }

                // 睡眠状態をクライアントへ同期
                WolfSleepData.setSleeping(wolf, true);

                // AIを停止
                wolf.setNoAi(true);

                // ナビゲーションを停止
                wolf.getNavigation().stop();

                // 移動速度をゼロにする
                wolf.setDeltaMovement(Vec3.ZERO);

            } else {

                // 睡眠できない状態なら起床
                wakeWolf(wolf);
            }
        }
    }

    private static boolean canSleep(Wolf wolf) {
        return wolf.isAlive()
                && wolf.isTame()
                && !wolf.isOrderedToSit()
                && !wolf.isInSittingPose()
                && wolf.getTarget() == null
                && wolf.onGround()
                && !wolf.isInWaterOrRain();
    }

    // 睡眠中の狼が飼い主から離れた場合に起こす
    private static void wakeWolvesOutsideRadius(ServerPlayer owner) {

        UUID ownerId = owner.getUUID();

        Iterator<Map.Entry<UUID, SleepingWolf>> iterator =
                SLEEPING_WOLVES.entrySet().iterator();

        while (iterator.hasNext()) {

            Map.Entry<UUID, SleepingWolf> entry = iterator.next();
            SleepingWolf sleepingWolf = entry.getValue();

            if (!sleepingWolf.ownerId().equals(ownerId)) {
                continue;
            }

            Wolf wolf = sleepingWolf.wolf();

            if (!wolf.isAlive()
                    || owner.distanceToSqr(wolf) > SLEEP_RADIUS_SQUARED) {

                restoreWolf(sleepingWolf);
                iterator.remove();
            }
        }
    }

    public static boolean isSleeping(Wolf wolf) {
        return WolfSleepData.isSleeping(wolf);
    }

    // 特定の狼を起こす
    public static void wakeWolf(Wolf wolf) {

        SleepingWolf sleepingWolf =
                SLEEPING_WOLVES.remove(wolf.getUUID());

        if (sleepingWolf != null) {
            restoreWolf(sleepingWolf);
        } else {
            WolfSleepData.setSleeping(wolf, false);
        }
    }

    // 睡眠状態とAIを元に戻す
    private static void restoreWolf(SleepingWolf sleepingWolf) {

        Wolf wolf = sleepingWolf.wolf();

        if (!wolf.isAlive()) {
            return;
        }

        // 睡眠状態を解除してクライアントへ同期
        WolfSleepData.setSleeping(wolf, false);

        // ナビゲーションを停止
        wolf.getNavigation().stop();

        // 移動速度をリセット
        wolf.setDeltaMovement(Vec3.ZERO);

        // 睡眠前のAI状態に戻す
        wolf.setNoAi(sleepingWolf.wasNoAi());
    }

    public static void wakeOwnerWolves(UUID ownerId) {

        Iterator<Map.Entry<UUID, SleepingWolf>> iterator =
                SLEEPING_WOLVES.entrySet().iterator();

        while (iterator.hasNext()) {

            Map.Entry<UUID, SleepingWolf> entry = iterator.next();
            SleepingWolf sleepingWolf = entry.getValue();

            if (sleepingWolf.ownerId().equals(ownerId)) {

                restoreWolf(sleepingWolf);
                iterator.remove();
            }
        }
    }

    public static void removeOwner(ServerPlayer owner) {
        wakeOwnerWolves(owner.getUUID());
    }

    private record SleepingWolf(
            UUID ownerId,
            Wolf wolf,
            boolean wasNoAi
    ) {
    }
}
