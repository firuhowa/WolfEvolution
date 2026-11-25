
package dev.firuhowa.wolfevolution;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WolfEvolution implements ModInitializer {

	public static final String MOD_ID = "wolfevolution";

	public static final Logger LOGGER =
			LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("WolfEvolution is initializing...");

		// サーバーのtickごとにAFK状態と狼の睡眠状態を更新
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			server.getPlayerList().getPlayers().forEach(player -> {

				// プレイヤーのAFK状態を更新
				PlayerAfkTracker.update(player);

				// 飼い主のAFK状態に応じて狼の睡眠状態を更新
				WolfSleepManager.update(player);
			});
		});

		// プレイヤーがログアウトしたときの処理
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {

			// AFKデータを削除
			PlayerAfkTracker.remove(handler.player);

			// 所有する狼を起床状態に戻す
			WolfSleepManager.removeOwner(handler.player);
		});

		LOGGER.info("WolfEvolution AFK tracker initialized.");
		LOGGER.info("WolfEvolution wolf sleep manager initialized.");
		LOGGER.info("WolfEvolution AI systems are ready.");
		LOGGER.info("WolfEvolution initialized successfully!");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
