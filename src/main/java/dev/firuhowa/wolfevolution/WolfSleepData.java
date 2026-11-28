
package dev.firuhowa.wolfevolution;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.animal.wolf.Wolf;

public final class WolfSleepData {

    /**
     * 狼の睡眠状態をクライアントと同期するデータ。
     */
    public static final EntityDataAccessor<Boolean> SLEEPING =
            SynchedEntityData.defineId(
                    Wolf.class,
                    EntityDataSerializers.BOOLEAN
            );

    private WolfSleepData() {
    }

    /**
     * 狼が眠っているか確認する。
     */
    public static boolean isSleeping(Wolf wolf) {
        return wolf.getEntityData().get(SLEEPING);
    }

    /**
     * 狼の睡眠状態を変更する。
     */
    public static void setSleeping(Wolf wolf, boolean sleeping) {
        wolf.getEntityData().set(SLEEPING, sleeping);
    }
}
