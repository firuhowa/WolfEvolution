
package dev.firuhowa.wolfevolution;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class PlayerAfkTracker {

    private static final long AFK_TIME_MS = 60_000L;

    private static final double MOVEMENT_THRESHOLD_SQUARED = 0.0001D;

    private static final Map<UUID, PlayerData> PLAYERS = new HashMap<>();

    private PlayerAfkTracker() {
    }

    /**
     * プレイヤーの移動状態を更新する。
     * サーバー側から定期的に呼び出すこと。
     */
    public static void update(ServerPlayer player) {
        UUID playerId = player.getUUID();
        Vec3 currentPosition = player.position();

        long currentTime = System.currentTimeMillis();

        PlayerData data = PLAYERS.get(playerId);

        if (data == null) {
            PLAYERS.put(
                    playerId,
                    new PlayerData(currentPosition, currentTime)
            );
            return;
        }

        double distanceSquared =
                currentPosition.distanceToSqr(data.lastPosition);

        // 移動を検知したらAFK時間をリセット
        if (distanceSquared > MOVEMENT_THRESHOLD_SQUARED) {
            data.lastPosition = currentPosition;
            data.lastMovementTime = currentTime;
        }
    }

    /**
     * プレイヤーが60秒以上移動していないか確認する。
     */
    public static boolean isAfk(ServerPlayer player) {
        PlayerData data = PLAYERS.get(player.getUUID());

        if (data == null) {
            return false;
        }

        return System.currentTimeMillis() - data.lastMovementTime
                >= AFK_TIME_MS;
    }

    /**
     * プレイヤーのAFK経過時間を取得する。
     */
    public static long getAfkTimeMillis(ServerPlayer player) {
        PlayerData data = PLAYERS.get(player.getUUID());

        if (data == null) {
            return 0L;
        }

        return Math.max(
                0L,
                System.currentTimeMillis() - data.lastMovementTime
        );
    }

    /**
     * プレイヤーのログアウト時に記録を削除する。
     */
    public static void remove(ServerPlayer player) {
        PLAYERS.remove(player.getUUID());
    }

    /**
     * プレイヤーごとのAFKデータ。
     */
    private static final class PlayerData {

        private Vec3 lastPosition;
        private long lastMovementTime;

        private PlayerData(
                Vec3 lastPosition,
                long lastMovementTime
        ) {
            this.lastPosition = lastPosition;
            this.lastMovementTime = lastMovementTime;
        }
    }
}
