package dev.firuhowa.wolfevolution;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.phys.AABB;

import java.util.List;

@SuppressWarnings("resource")
public final class WolfWhistleHandler {

    private static final double SEARCH_RADIUS = 30.0D;

    private static final double SEARCH_RADIUS_SQUARED =
            SEARCH_RADIUS * SEARCH_RADIUS;

    private static final double TELEPORT_RADIUS = 5.0D;

    private WolfWhistleHandler() {
    }

    public static void handle(ServerPlayer player) {

        ServerLevel level = player.level();

        PlayerAfkTracker.markActive(player);

        AABB searchArea =
                player.getBoundingBox().inflate(SEARCH_RADIUS);

        List<Wolf> wolves = level.getEntitiesOfClass(
                Wolf.class,
                searchArea,
                wolf -> wolf.isAlive()
                        && wolf.isTame()
                        && wolf.isOwnedBy(player)
                        && wolf.distanceToSqr(player)
                        <= SEARCH_RADIUS_SQUARED
        );

        for (Wolf wolf : wolves) {

            // 睡眠中なら起こす
            WolfSleepManager.wakeWolf(wolf);

            // 旧バージョンで残ったNoAI状態も復旧
            wolf.setNoAi(false);

            // 座っている狼を立たせる
            wolf.setOrderedToSit(false);
            wolf.setInSittingPose(false);

            // 安全な場所を探してプレイヤーの近くへ移動
            teleportToSafePosition(level, player, wolf);
        }
    }

    private static void teleportToSafePosition(
            ServerLevel level,
            ServerPlayer player,
            Wolf wolf
    ) {

        BlockPos playerPos = player.blockPosition();

        for (int i = 0; i < 48; i++) {

            double angle = i * 2.399963229728653D;
            double radius = 2.0D + (i % 4);

            int offsetX = (int) Math.round(
                    Math.cos(angle) * radius
            );

            int offsetZ = (int) Math.round(
                    Math.sin(angle) * radius
            );

            int blockX = playerPos.getX() + offsetX;
            int blockZ = playerPos.getZ() + offsetZ;

            int[] verticalOffsets = {
                    0, -1, 1, -2, 2
            };

            for (int verticalOffset : verticalOffsets) {

                int feetY =
                        playerPos.getY() + verticalOffset;

                BlockPos feetPos =
                        new BlockPos(blockX, feetY, blockZ);

                BlockPos headPos = feetPos.above();
                BlockPos groundPos = feetPos.below();

                double dx = blockX + 0.5D - player.getX();
                double dz = blockZ + 0.5D - player.getZ();

                if (dx * dx + dz * dz
                        > TELEPORT_RADIUS * TELEPORT_RADIUS) {
                    continue;
                }

                if (!level.getWorldBorder().isWithinBounds(feetPos)) {
                    continue;
                }

                if (!level.getBlockState(groundPos).isSolidRender()) {
                    continue;
                }

                if (!level.getBlockState(feetPos)
                        .getFluidState().isEmpty()) {
                    continue;
                }

                if (!level.getBlockState(headPos)
                        .getFluidState().isEmpty()) {
                    continue;
                }

                double targetX = blockX + 0.5D;
                double targetZ = blockZ + 0.5D;

                AABB targetBox = wolf.getBoundingBox().move(
                        targetX - wolf.getX(),
                        feetY - wolf.getY(),
                        targetZ - wolf.getZ()
                );

                if (!level.noCollision(wolf, targetBox)) {
                    continue;
                }

                wolf.getNavigation().stop();

                wolf.teleportTo(
                        targetX,
                        feetY,
                        targetZ
                );

                wolf.setDeltaMovement(0.0D, 0.0D, 0.0D);

                return;
            }
        }
    }
}