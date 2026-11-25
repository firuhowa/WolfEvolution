
package dev.firuhowa.wolfevolution;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.phys.AABB;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public final class WolfSleepManager {

    private static final double SLEEP_RADIUS = 16.0D;

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

        if (!PlayerAfkTracker.isAfk(owner)) {
            wakeOwnerWolves(ownerId);
            return;
        }

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

                // 睡眠状態をクライアントへ同期
                WolfSleepData.setSleeping(wolf, true);

                SLEEPING_WOLVES.put(
                        wolfId,
                        new SleepingWolf(ownerId, wolf)
                );

            } else {

                // 睡眠できない状態なら起床
                WolfSleepData.setSleeping(wolf, false);

                SLEEPING_WOLVES.remove(wolfId);
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

    public static boolean isSleeping(Wolf wolf) {
        return WolfSleepData.isSleeping(wolf);
    }

    public static void wakeOwnerWolves(UUID ownerId) {

        Iterator<Map.Entry<UUID, SleepingWolf>> iterator =
                SLEEPING_WOLVES.entrySet().iterator();

        while (iterator.hasNext()) {

            Map.Entry<UUID, SleepingWolf> entry = iterator.next();
            SleepingWolf sleepingWolf = entry.getValue();

            if (sleepingWolf.ownerId().equals(ownerId)) {

                // 起床状態をクライアントへ同期
                WolfSleepData.setSleeping(
                        sleepingWolf.wolf(),
                        false
                );

                iterator.remove();
            }
        }
    }

    public static void removeOwner(ServerPlayer owner) {
        wakeOwnerWolves(owner.getUUID());
    }

    private record SleepingWolf(UUID ownerId, Wolf wolf) {
    }
}
